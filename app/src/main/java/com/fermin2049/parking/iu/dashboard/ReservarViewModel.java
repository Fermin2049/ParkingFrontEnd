package com.fermin2049.parking.iu.dashboard;

import android.app.Application;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservarViewModel extends AndroidViewModel {

    private final MutableLiveData<List<EspacioEstacionamiento>> espaciosDisponibles = new MutableLiveData<>();
    private final MutableLiveData<ErrorMessage> errorMessageLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> reservationSuccessLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> reservaIdLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> fechaReservaLiveData = new MutableLiveData<>();
    private ApiService apiService;

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
     * Procesa y valida la fecha y hora de la reserva. Si todo es correcto, llama a cargarEspaciosDisponiblesPorFechaHora.
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

            // Se calcula un "fin" mínimo para la validación
            Calendar calFin = (Calendar) calLlegada.clone();
            calFin.add(Calendar.MINUTE, 1);

            SimpleDateFormat sdfIso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
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

    // Método para registrar la reserva y actualizar LiveData con los datos de la reserva
    public void registrarReserva(EspacioEstacionamiento espacio) {
        String token = "Bearer " + obtenerToken();
        if (token.isEmpty() || token.equals("Bearer ")) {
            emitirError("Error de autenticación", "No se encontró el token de autenticación.");
            return;
        }
        Reserva reserva = new Reserva();
        reserva.setIdEspacio(espacio.getIdEspacio());
        reserva.setEstado("EnProceso");

        apiService.registrarReserva(token, reserva).enqueue(new Callback<Reserva>() {
            @Override
            public void onResponse(Call<Reserva> call, Response<Reserva> response) {
                if (response.isSuccessful() && response.body() != null) {
                    reservaIdLiveData.setValue(response.body().getIdReserva());
                    // Se formatea la fecha de reserva en ISO para el PaymentFragment
                    SimpleDateFormat sdfIso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                    String fechaFormateada = sdfIso.format(response.body().getFechaReserva());
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

    // Emite errores a través de LiveData para que el Fragment los observe y actúe (por ejemplo, mostrando un diálogo)
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
