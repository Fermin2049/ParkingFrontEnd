package com.fermin2049.parking.iu.dashboard;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.fermin2049.parking.data.models.EspacioEstacionamiento;
import com.fermin2049.parking.network.ApiClient;
import com.fermin2049.parking.network.ApiService;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservarViewModel extends AndroidViewModel {

    private final MutableLiveData<List<EspacioEstacionamiento>> espaciosDisponibles = new MutableLiveData<>();
    private final ApiService apiService;
    private List<EspacioEstacionamiento> listaCompletaEspacios = new ArrayList<>();

    public ReservarViewModel(@NonNull Application application) {
        super(application);
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public LiveData<List<EspacioEstacionamiento>> getEspaciosDisponibles() {
        return espaciosDisponibles;
    }

    /**
     * Combina la fecha y hora ingresadas, valida que sean correctas y en el futuro, y llama al endpoint
     * para cargar los espacios disponibles filtrados por fecha/hora y tipo.
     * @param fechaStr Cadena con la fecha en formato "dd-MM-yyyy".
     * @param horaStr Cadena con la hora en formato "HH:mm".
     * @param tipoSeleccionado El tipo de espacio ("Normal", "Motocicleta" o "Discapacitados").
     */
    public void buscarReservas(String fechaStr, String horaStr, String tipoSeleccionado) {
        if (fechaStr.isEmpty()) {
            showError("Fecha requerida", "Debe seleccionar una fecha");
            return;
        }
        if (horaStr.isEmpty()) {
            showError("Hora requerida", "Debe seleccionar la hora de llegada");
            return;
        }
        try {
            // Parsear la fecha ingresada
            SimpleDateFormat sdfInput = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Date fechaDate = sdfInput.parse(fechaStr);

            // Combinar la fecha con la hora ingresada
            Calendar calLlegada = Calendar.getInstance();
            calLlegada.setTime(fechaDate);
            String[] parts = horaStr.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            calLlegada.set(Calendar.HOUR_OF_DAY, hour);
            calLlegada.set(Calendar.MINUTE, minute);
            calLlegada.set(Calendar.SECOND, 0);

            // Definir un intervalo corto (por ejemplo, 1 minuto) para la consulta
            Calendar calFin = (Calendar) calLlegada.clone();
            calFin.add(Calendar.MINUTE, 1);

            // Convertir a formato ISO 8601 ("yyyy-MM-dd'T'HH:mm:ss")
            SimpleDateFormat sdfIso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            String fechaInicioParam = sdfIso.format(calLlegada.getTime());
            String fechaFinParam = sdfIso.format(calFin.getTime());

            // Validar que la hora de llegada sea en el futuro
            Date now = new Date();
            if (calLlegada.getTime().before(now)) {
                showError("Hora inválida", "La hora de llegada debe ser en el futuro");
                return;
            }
            if (!calFin.getTime().after(calLlegada.getTime())) {
                showError("Intervalo inválido", "La hora de fin debe ser posterior a la hora de llegada");
                return;
            }

            // Llamar al método que carga los espacios disponibles por fecha/hora y tipo
            cargarEspaciosDisponiblesPorFechaHora(fechaInicioParam, fechaFinParam, tipoSeleccionado);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "Error al procesar la fecha/hora");
        }
    }

    /**
     * Llama al endpoint de la API para obtener los espacios disponibles en el intervalo [fechaInicio, fechaFin]
     * y filtrados por tipo.
     */
    public void cargarEspaciosDisponiblesPorFechaHora(String fechaInicio, String fechaFin, String tipo) {
        String token = "Bearer " + obtenerToken();
        if (token.isEmpty() || token.equals("Bearer ")) {
            showError("Error de autenticación", "No se encontró el token de autenticación.");
            return;
        }
        apiService.getEspaciosDisponiblesPorFecha(token, fechaInicio, fechaFin, tipo)
                .enqueue(new Callback<List<EspacioEstacionamiento>>() {
                    @Override
                    public void onResponse(Call<List<EspacioEstacionamiento>> call, Response<List<EspacioEstacionamiento>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            listaCompletaEspacios = response.body();
                            espaciosDisponibles.setValue(listaCompletaEspacios);
                        } else {
                            showError("Error", "No se pudieron cargar los espacios disponibles.");
                        }
                    }
                    @Override
                    public void onFailure(Call<List<EspacioEstacionamiento>> call, Throwable t) {
                        showError("Error de red", "No se pudo conectar al servidor.");
                    }
                });
    }

    // Obtiene el token de SharedPreferences.
    private String obtenerToken() {
        return getApplication()
                .getSharedPreferences("MisPreferencias", Application.MODE_PRIVATE)
                .getString("token", "");
    }

    // Muestra un mensaje de error usando SweetAlertDialog.
    private void showError(String title, String message) {
        // Usamos Handler para asegurarnos de ejecutar en el hilo principal.
        new Handler(Looper.getMainLooper()).post(() -> {
            new SweetAlertDialog(getApplication(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(title)
                    .setContentText(message)
                    .show();
        });
    }

    // Método de navegación a pago (si es necesario)
    public void irAPago(androidx.fragment.app.FragmentActivity activity, EspacioEstacionamiento espacio) {
        // Implementación de la navegación al fragmento de pago (si aplica)
    }
}
