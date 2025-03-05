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
import com.fermin2049.parking.network.ApiService;
import com.fermin2049.parking.network.ApiClient;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentViewModel extends AndroidViewModel {

    private int reservaId;
    private double totalAmount;
    private String fechaReserva;

    private final MutableLiveData<String> confirmationResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> paymentStatus = new MutableLiveData<>();

    private final ApiService backendApi;

    public PaymentViewModel(@NonNull Application application) {
        super(application);
        backendApi = ApiClient.getClient().create(ApiService.class);
    }

    // Método para recibir los datos iniciales desde el Fragment
    public void setInitialData(int reservaId, double totalAmount, String fechaReserva) {
        this.reservaId = reservaId;
        this.totalAmount = totalAmount;
        this.fechaReserva = fechaReserva;
    }

    public LiveData<String> getConfirmationResult() {
        return confirmationResult;
    }

    public LiveData<String> getPaymentStatus() {
        return paymentStatus;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    /**
     * Método encargado de validar el input de horas y llamar a confirmPayment si es correcto.
     * @param horasInput Valor ingresado por el usuario.
     */
    public void onConfirmPayment(String horasInput) {
        int horas;
        if (horasInput == null || horasInput.trim().isEmpty()) {
            horas = 1;
            errorMessage.setValue("No ingresaste la cantidad de horas. Se reserva 1 hora.\nEl cierre del estacionamiento es a las 21:00 y se cobrará 1 hora adicional.");
        } else {
            try {
                horas = Integer.parseInt(horasInput.trim());
                if (horas <= 0) {
                    errorMessage.setValue("Ingresa un número de horas válido.");
                    return;
                }
            } catch (NumberFormatException e) {
                errorMessage.setValue("Formato de horas incorrecto.");
                return;
            }
        }
        confirmPayment(reservaId, horas, fechaReserva);
    }

    /**
     * Confirma el pago simulado.
     * Calcula la fecha de expiración sumándole las horas a la fecha de reserva.
     *
     * @param reservaId    ID de la reserva.
     * @param horas        Cantidad de horas reservadas.
     * @param fechaReserva Fecha de inicio de la reserva en formato ISO ("yyyy-MM-dd'T'HH:mm:ss")
     */
    public void confirmPayment(int reservaId, int horas, String fechaReserva) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        Date fechaInicio;
        try {
            fechaInicio = sdf.parse(fechaReserva);
        } catch (ParseException e) {
            Log.e("PaymentViewModel", "Error al parsear fechaReserva: " + fechaReserva, e);
            fechaInicio = new Date();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaInicio);
        cal.add(Calendar.HOUR_OF_DAY, horas);
        String fechaExpiracion = sdf.format(cal.getTime());
        double monto = horas * 100.0; // 100 por hora

        PaymentConfirmationDto dto = new PaymentConfirmationDto();
        dto.setReservaId(reservaId);
        dto.setMonto(monto);
        dto.setMetodoPago("Simulado");
        dto.setHoras(horas);
        dto.setFechaExpiracion(fechaExpiracion);

        String token = "Bearer " + obtenerToken();
        Log.d("PaymentViewModel", "Token para confirmación: " + token);

        backendApi.confirmPayment(token, dto).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("PaymentViewModel", "Pago confirmado: " + response.body().toString());
                    confirmationResult.setValue("Pago confirmado y reserva activada");
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                        Log.e("PaymentViewModel", "Error al confirmar pago: " + errorBody);
                    } catch (Exception ex) {
                        Log.e("PaymentViewModel", "Error al leer errorBody", ex);
                    }
                    confirmationResult.setValue("Error al confirmar pago");
                }
            }
            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e("PaymentViewModel", "Fallo al confirmar pago", t);
                confirmationResult.setValue("Fallo al confirmar pago");
            }
        });
    }

    // Método para obtener el token JWT de SharedPreferences
    private String obtenerToken() {
        SharedPreferences prefs = getApplication().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        return prefs.getString("token", "");
    }
}

