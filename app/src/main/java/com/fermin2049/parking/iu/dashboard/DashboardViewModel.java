package com.fermin2049.parking.iu.dashboard;

import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.fermin2049.parking.R;
import com.fermin2049.parking.data.models.EspacioEstacionamiento;
import com.fermin2049.parking.iu.payment.PaymentFragment;
import com.fermin2049.parking.network.ApiClient;
import com.fermin2049.parking.network.ApiService;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardViewModel extends AndroidViewModel {

    private final MutableLiveData<List<EspacioEstacionamiento>> espaciosDisponibles = new MutableLiveData<>();
    private final ApiService apiService;

    public DashboardViewModel(@NonNull Application application) {
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
                }
            }

            @Override
            public void onFailure(Call<List<EspacioEstacionamiento>> call, Throwable t) {
                // Manejo de error silencioso
            }
        });
    }

    private String obtenerToken() {
        return getApplication()
                .getSharedPreferences("MisPreferencias", Application.MODE_PRIVATE)
                .getString("token", "");
    }

    public void irAPago(FragmentActivity activity, EspacioEstacionamiento espacio) {
        Bundle args = new Bundle();
        args.putInt("idEspacio", espacio.getIdEspacio());
        args.putString("numeroEspacio", String.valueOf(espacio.getNumeroEspacio()));
        args.putString("tipoEspacio", espacio.getTipoEspacio());

        Fragment paymentFragment = new PaymentFragment();
        paymentFragment.setArguments(args);

        new Handler(Looper.getMainLooper()).post(() -> {
            if (activity != null) {
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, paymentFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}
