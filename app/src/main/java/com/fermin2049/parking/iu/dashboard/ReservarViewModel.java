package com.fermin2049.parking.iu.dashboard;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.fermin2049.parking.data.models.EspacioEstacionamiento;
import com.fermin2049.parking.data.models.Reserva;
import com.fermin2049.parking.network.ApiClient;
import com.fermin2049.parking.network.ApiService;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservarViewModel extends AndroidViewModel {

    private static final String TAG = "ReservarViewModel";

    private final MutableLiveData<List<EspacioEstacionamiento>> espaciosDisponibles = new MutableLiveData<>();
    private final MutableLiveData<ErrorMessage> errorMessageLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> reservationSuccessLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> reservaIdLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> fechaReservaLiveData = new MutableLiveData<>();

    private ApiService apiService;

    // Aquí guardamos la fecha/hora local seleccionada por el usuario (ej: 17:00 Argentina)
    private Date fechaReservaCalculada;

    public ReservarViewModel(@NonNull Application application) {
        super(application);
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public LiveData<List<EspacioEstacionamiento>> getEspaciosDisponibles() {
        return espaciosDisponibles;
    }

    public LiveData<Integer> getReservaIdLiveData() {
        return reservaIdLiveData;
    }

    public LiveData<String> getFechaReservaLiveData() {
        return fechaReservaLiveData;
    }

    public LiveData<ErrorMessage> getErrorMessageLiveData() {
        return errorMessageLiveData;
    }

    public LiveData<Boolean> getReservationSuccessLiveData() {
        return reservationSuccessLiveData;
    }

    /**
     * Procesa y valida la fecha y hora de la reserva elegida por el usuario.
     */
    public void procesarFechaHoraReserva(String fechaStr, String horaStr, String tipoSeleccionado) {
        if (fechaStr.isEmpty()) {
            emitirError("Fecha requerida", "Debe seleccionar una fecha");
            return;
        }
        if (horaStr.isEmpty()) {
            emitirError("Hora requerida", "Debe seleccionar la hora de llegada");
            return;
        }
        try {
            SimpleDateFormat sdfInput = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Date fechaDate = sdfInput.parse(fechaStr);

            Calendar calLlegada = Calendar.getInstance();
            calLlegada.setTime(fechaDate);
            String[] parts = horaStr.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            calLlegada.set(Calendar.HOUR_OF_DAY, hour);
            calLlegada.set(Calendar.MINUTE, minute);
            calLlegada.set(Calendar.SECOND, 0);

            // Esta es la fecha/hora en la zona horaria local del usuario
            fechaReservaCalculada = calLlegada.getTime();
            Log.d(TAG, "fechaReservaCalculada (Local) : " + fechaReservaCalculada.toString());

            // Creamos un "fin" mínimo para la validación (1 minuto después)
            Calendar calFin = (Calendar) calLlegada.clone();
            calFin.add(Calendar.MINUTE, 1);

            SimpleDateFormat sdfIso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            // No convertimos a UTC aquí, solo para filtrar espacios
            String fechaInicioParam = sdfIso.format(calLlegada.getTime());
            String fechaFinParam = sdfIso.format(calFin.getTime());

            Date now = new Date();
            if (calLlegada.getTime().before(now)) {
                emitirError("Hora inválida", "La hora de llegada debe ser en el futuro");
                return;
            }
            if (!calFin.getTime().after(calLlegada.getTime())) {
                emitirError("Intervalo inválido", "La hora de fin debe ser posterior a la hora de llegada");
                return;
            }

            // Llamada a la API para cargar espacios disponibles
            cargarEspaciosDisponiblesPorFechaHora(fechaInicioParam, fechaFinParam, tipoSeleccionado);
        } catch (Exception e) {
            e.printStackTrace();
            emitirError("Error", "Error al procesar la fecha/hora");
        }
    }

    // Llama a la API para obtener los espacios disponibles
    public void cargarEspaciosDisponiblesPorFechaHora(String fechaInicio, String fechaFin, String tipo) {
        String token = "Bearer " + obtenerToken();
        if (token.isEmpty() || token.equals("Bearer ")) {
            emitirError("Error de autenticación", "No se encontró el token de autenticación.");
            return;
        }
        apiService.getEspaciosDisponiblesPorFecha(token, fechaInicio, fechaFin, tipo)
                .enqueue(new Callback<List<EspacioEstacionamiento>>() {
                    @Override
                    public void onResponse(Call<List<EspacioEstacionamiento>> call, Response<List<EspacioEstacionamiento>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<EspacioEstacionamiento> disponibles = new ArrayList<>();
                            for (EspacioEstacionamiento espacio : response.body()) {
                                if (!"EnProceso".equals(espacio.getEstado())) {
                                    disponibles.add(espacio);
                                }
                            }
                            espaciosDisponibles.setValue(disponibles);
                        } else {
                            emitirError("Error", "No se pudieron cargar los espacios disponibles.");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<EspacioEstacionamiento>> call, Throwable t) {
                        emitirError("Error de red", "No se pudo conectar al servidor.");
                    }
                });
    }

    /**
     * Registra la reserva convirtiendo la fecha local a UTC antes de enviar al backend.
     */
    public void registrarReserva(EspacioEstacionamiento espacio) {
        String token = "Bearer " + obtenerToken();
        if (token.isEmpty() || token.equals("Bearer ")) {
            emitirError("Error de autenticación", "No se encontró el token de autenticación.");
            return;
        }
        if (espacio == null) {
            emitirError("Error", "No se seleccionó un espacio válido.");
            return;
        }

        Reserva reserva = new Reserva();
        reserva.setIdEspacio(espacio.getIdEspacio());
        reserva.setEstado("EnProceso");

        if (fechaReservaCalculada == null) {
            // Como fallback, usar la fecha actual local para la reserva
            fechaReservaCalculada = new Date();
        }

        try {
            // 1) Convertir la fecha local elegida por el usuario a UTC para la fecha de reserva
            SimpleDateFormat sdfUtc = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            sdfUtc.setTimeZone(TimeZone.getTimeZone("UTC"));

            // Convertimos fechaReservaCalculada a string UTC
            String dateUtcString = sdfUtc.format(fechaReservaCalculada);
            // Parseamos ese string para tener un objeto Date en UTC
            Date dateUtc = sdfUtc.parse(dateUtcString);

            // Asignamos la fecha de reserva con la hora elegida por el usuario (en UTC)
            reserva.setFechaReserva(dateUtc);

            // 2) Para la fecha de expiración, usar "ahora + 10 min" en UTC
            Calendar calExp = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            Date nowUtc = calExp.getTime();
            calExp.add(Calendar.MINUTE, 5);
            Date expiracionUtc = calExp.getTime();

            reserva.setFechaExpiracion(expiracionUtc);

            // Logs de depuración
            Log.d(TAG, "LocalTime (user selected): " + fechaReservaCalculada);
            Log.d(TAG, "dateUtcString (UTC)      : " + dateUtcString);
            Log.d(TAG, "dateUtc (UTC)            : " + dateUtc);
            Log.d(TAG, "Now UTC                  : " + nowUtc);
            Log.d(TAG, "fechaExpiracion (UTC)    : " + expiracionUtc);

        } catch (Exception e) {
            e.printStackTrace();
            emitirError("Error de conversión", "No se pudo convertir la fecha local a UTC.");
            return;
        }

        // Llamada al backend para registrar la reserva
        apiService.registrarReserva(token, reserva).enqueue(new Callback<Reserva>() {
            @Override
            public void onResponse(Call<Reserva> call, Response<Reserva> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Reserva respuestaReserva = response.body();

                    // Convertir la fechaReserva de vuelta a string (para mostrar en PaymentFragment)
                    SimpleDateFormat sdfIso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                    sdfIso.setTimeZone(TimeZone.getTimeZone("UTC")); // Se mantiene en UTC

                    String fechaFormateada = sdfIso.format(respuestaReserva.getFechaReserva());
                    Log.d(TAG, "Reserva guardada en DB (UTC): " + fechaFormateada);

                    reservaIdLiveData.setValue(respuestaReserva.getIdReserva());
                    fechaReservaLiveData.setValue(fechaFormateada);
                    reservationSuccessLiveData.setValue(true);

                } else {
                    emitirError("Error", "No se pudo registrar la reserva.");
                }
            }

            @Override
            public void onFailure(Call<Reserva> call, Throwable t) {
                emitirError("Error de red", "No se pudo conectar al servidor.");
            }
        });
    }


    private String obtenerToken() {
        return getApplication()
                .getSharedPreferences("MisPreferencias", Application.MODE_PRIVATE)
                .getString("token", "");
    }

    private void emitirError(String title, String message) {
        errorMessageLiveData.setValue(new ErrorMessage(title, message));
    }

    public static class ErrorMessage {
        private final String title;
        private final String message;

        public ErrorMessage(String title, String message) {
            this.title = title;
            this.message = message;
        }
        public String getTitle() {
            return title;
        }
        public String getMessage() {
            return message;
        }
    }
}
