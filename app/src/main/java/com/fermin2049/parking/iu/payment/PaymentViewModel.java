package com.fermin2049.parking.iu.payment;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.fermin2049.parking.data.models.PaymentConfirmationDto;
import com.fermin2049.parking.data.models.Reserva;
import com.fermin2049.parking.network.ApiClient;
import com.fermin2049.parking.network.ApiService;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentViewModel extends AndroidViewModel {

    private static final String TAG = "PaymentViewModel";
    private int reservaId;
    private double totalAmount;
    // Fecha de reserva en formato ISO (UTC)
    private String fechaReserva;

    private final MutableLiveData<String> confirmationResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> paymentStatus = new MutableLiveData<>();
    private final MutableLiveData<Long> scheduledStartTime = new MutableLiveData<>();
    private final MutableLiveData<Long> expirationTime = new MutableLiveData<>();
    private static final String PREF_START = "reservation_start";
    private static final String PREF_EXPIRATION = "reservation_expiration";

    private final ApiService backendApi;

    public PaymentViewModel(@NonNull Application application) {
        super(application);
        backendApi = ApiClient.getClient().create(ApiService.class);
        SharedPreferences prefs = getApplication().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        long savedStart = prefs.getLong(PREF_START, 0);
        long savedExpiration = prefs.getLong(PREF_EXPIRATION, 0);
        long now = System.currentTimeMillis();
        if (savedStart > now) {
            scheduledStartTime.setValue(savedStart);
        }
        if (savedExpiration > now) {
            expirationTime.setValue(savedExpiration);
        }
    }

    public LiveData<String> getConfirmationResult() { return confirmationResult; }
    public LiveData<String> getErrorMessage()       { return errorMessage; }
    public LiveData<String> getPaymentStatus()      { return paymentStatus; }
    public LiveData<Long>   getScheduledStartTime() { return scheduledStartTime; }
    public LiveData<Long>   getExpirationTime()     { return expirationTime; }

    public void setInitialData(int reservaId, double totalAmount, String fechaReserva) {
        this.reservaId = reservaId;
        this.totalAmount = totalAmount;
        this.fechaReserva = fechaReserva;
        Log.d(TAG, "setInitialData: reservaId=" + reservaId + ", totalAmount=" + totalAmount +
                ", fechaReserva(ISO UTC)=" + fechaReserva);
    }

    public void onConfirmPayment(String minutosInput) {
        int minutos;
        if (minutosInput == null || minutosInput.trim().isEmpty()) {
            minutos = 5;
            errorMessage.setValue("No ingresaste la cantidad de minutos. Se reserva 5 minutos.");
        } else {
            try {
                minutos = Integer.parseInt(minutosInput.trim());
                if (minutos <= 0) {
                    errorMessage.setValue("Ingresa un número de minutos válido.");
                    return;
                }
            } catch (NumberFormatException e) {
                errorMessage.setValue("Formato de minutos incorrecto.");
                return;
            }
        }
        Log.d(TAG, "onConfirmPayment: minutos=" + minutos);
        // Llamamos a confirmPayment pasando los minutos en lugar de horas
        confirmPayment(reservaId, minutos, fechaReserva);
    }

    public void confirmPayment(int reservaId, int minutos, String fechaReservaUtc) {
        // Configuramos el SimpleDateFormat para trabajar en UTC
        SimpleDateFormat sdfUtc = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        sdfUtc.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date scheduledStart;
        try {
            // Parseamos la fecha de inicio (que ya fue convertida a UTC en ReservarViewModel)
            scheduledStart = sdfUtc.parse(fechaReservaUtc);
        } catch (ParseException e) {
            Log.e(TAG, "Error al parsear fechaReserva (UTC): " + fechaReservaUtc, e);
            // En caso de error, usamos la hora actual + 60 segundos como fallback
            scheduledStart = new Date(System.currentTimeMillis() + 60_000);
        }
        long startMillis = scheduledStart.getTime();
        long currentTime = System.currentTimeMillis();
        long waitTime = Math.max(0, startMillis - currentTime);

        // Actualizamos el LiveData del tiempo de inicio
        scheduledStartTime.setValue(startMillis);

        // Calculamos la fecha de expiración para el contador sin ajuste
        long expirationMillisForCountdown = startMillis + (minutos * 60000L);
        // Calculamos la fecha de expiración ajustada (restando 3 horas) para guardar en la base de datos
        long expirationMillisForDB = expirationMillisForCountdown - (3 * 3600_000L);

        // Usamos la variable sin ajuste para el contador activo
        expirationTime.setValue(expirationMillisForCountdown);
        Log.d(TAG, "expirationMillisForCountdown : " + expirationMillisForCountdown);
        Log.d(TAG, "expirationMillisForDB        : " + expirationMillisForDB);

        // Guardamos en SharedPreferences la fecha de inicio y la fecha ajustada para la DB
        SharedPreferences prefs = getApplication().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        prefs.edit().putLong(PREF_START, startMillis)
                .putLong(PREF_EXPIRATION, expirationMillisForDB)
                .apply();

        // Formateamos la fecha de expiración ajustada para enviarla al backend
        String fechaExpiracion = sdfUtc.format(new Date(expirationMillisForDB));
        // Para efectos de prueba, el monto se calcula en base a minutos (por ejemplo, 100 por minuto)
        double monto = minutos * 100.0;

        // Construir el DTO de confirmación de pago y asignarle la fecha de expiración transformada
        PaymentConfirmationDto dto = new PaymentConfirmationDto();
        dto.setReservaId(reservaId);
        dto.setMonto(monto);
        dto.setMetodoPago("Simulado");
        // Aunque el nombre del campo es 'horas', aquí se usa para testear con minutos
        dto.setHoras(minutos);
        dto.setFechaExpiracion(fechaExpiracion);

        String token = "Bearer " + obtenerToken();
        Log.d(TAG, "confirmPayment: Token: " + token);

        backendApi.confirmPayment(token, dto).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful() && response.body() != null) {
                    confirmationResult.setValue("Pago confirmado y reserva activada");
                    paymentStatus.setValue("Activa");
                    Log.d(TAG, "Pago confirmado exitosamente.");
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                        Log.e(TAG, "Error al confirmar pago: " + errorBody);
                    } catch (Exception ex) {
                        Log.e(TAG, "Error al leer errorBody", ex);
                    }
                    confirmationResult.setValue("Error al confirmar pago");
                }
            }
            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e(TAG, "Fallo al confirmar pago", t);
                confirmationResult.setValue("Fallo al confirmar pago");
            }
        });
    }





    /**
     * Actualiza el estado de la reserva a "Finalizada" cuando termine el contador activo.
     */
    public void completeReservation() {
        updateReservationState("Finalizada");
        paymentStatus.setValue("Finalizada");
        SharedPreferences prefs = getApplication().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        prefs.edit().remove(PREF_START).remove(PREF_EXPIRATION).apply();
    }

    /**
     * Método para actualizar el estado de la reserva en el backend.
     */
    public void updateReservationState(String newState) {
        Reserva reserva = new Reserva();
        reserva.setIdReserva(reservaId);
        reserva.setEstado(newState);

        String token = "Bearer " + obtenerToken();
        backendApi.updateReservaEstado(token, reservaId, reserva).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Reserva " + reservaId + " actualizada a estado: " + newState);
                } else {
                    Log.e(TAG, "Error al actualizar reserva " + reservaId + ", code: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Fallo al actualizar reserva " + reservaId, t);
            }
        });
    }

    private String obtenerToken() {
        SharedPreferences prefs = getApplication().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        return prefs.getString("token", "");
    }
}
