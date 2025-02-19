package com.fermin2049.parking.iu.dashboard;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.fermin2049.parking.data.models.EspacioEstacionamiento;
import com.fermin2049.parking.network.ApiClient;
import com.fermin2049.parking.network.ApiService;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardViewModel extends AndroidViewModel {

    private MutableLiveData<List<EspacioEstacionamiento>> espaciosDisponibles = new MutableLiveData<>();
    private ApiService apiService;

    public DashboardViewModel(Application application) {
        super(application);
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public LiveData<List<EspacioEstacionamiento>> getEspaciosDisponibles() {
        return espaciosDisponibles;
    }

    public void cargarEspaciosDisponibles() {
        String token = "Bearer " + obtenerToken();

        if (token.isEmpty() || token.equals("Bearer ")) {
            return;
        }

        apiService.getEspaciosDisponibles(token).enqueue(new Callback<List<EspacioEstacionamiento>>() {
            @Override
            public void onResponse(Call<List<EspacioEstacionamiento>> call, Response<List<EspacioEstacionamiento>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    espaciosDisponibles.setValue(response.body());
                    Log.d("API_SUCCESS", "Datos recibidos: " + response.body().size());
                } else {
                    Log.e("API_ERROR", "Error en la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<EspacioEstacionamiento>> call, Throwable t) {
                Log.e("API_ERROR", "Error en la llamada a la API", t);
            }
        });
    }

    private String obtenerToken() {
        return getApplication()
                .getSharedPreferences("MisPreferencias", Application.MODE_PRIVATE)
                .getString("token", "");
    }
}
