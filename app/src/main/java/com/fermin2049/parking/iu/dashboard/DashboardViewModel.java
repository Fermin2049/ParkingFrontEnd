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
import com.fermin2049.parking.data.models.BannerOferta;
import com.fermin2049.parking.data.models.EspacioEstacionamiento;
import com.fermin2049.parking.iu.payment.PaymentFragment;
import com.fermin2049.parking.network.ApiClient;
import com.fermin2049.parking.network.ApiService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardViewModel extends AndroidViewModel {

    private final MutableLiveData<List<EspacioEstacionamiento>> espaciosDisponibles = new MutableLiveData<>();
    private final MutableLiveData<List<BannerOferta>> bannersLiveData = new MutableLiveData<>();
    private final ApiService apiService;

    // Se guarda la lista completa cargada desde la API para luego filtrar
    private List<EspacioEstacionamiento> listaCompletaEspacios = new ArrayList<>();

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public LiveData<List<EspacioEstacionamiento>> getEspaciosDisponibles() {
        return espaciosDisponibles;
    }

    public LiveData<List<BannerOferta>> getBanners() {
        return bannersLiveData;
    }

    public void cargarBanners() {
        List<BannerOferta> banners = Arrays.asList(
                new BannerOferta("10% de Descuento", "En alquiler mensual.", R.drawable.banner_descuento),
                new BannerOferta("1 Hora Gratis", "Pagando más de 5 horas.", R.drawable.banner_hora),
                new BannerOferta("Lavado de Autos", "Servicio disponible en el estacionamiento.", R.drawable.banner_lavado),
                new BannerOferta("20% de Descuento", "Pagando con billetera virtual Mercado Pago", R.drawable.banner_velletera)
        );
        bannersLiveData.setValue(banners);
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
                    listaCompletaEspacios = response.body();
                    espaciosDisponibles.setValue(listaCompletaEspacios);
                }
            }
            @Override
            public void onFailure(Call<List<EspacioEstacionamiento>> call, Throwable t) {
                // Manejo de error (puedes notificar al usuario)
            }
        });
    }

    /**
     * Filtra la lista de espacios disponibles por tipo.
     * @param tipo El tipo de espacio seleccionado (por ejemplo, "Normal", "Motocicleta" o "Discapacitados").
     */
    public void filtrarEspacios(String tipo) {
        if (listaCompletaEspacios == null || listaCompletaEspacios.isEmpty()) {
            return;
        }
        List<EspacioEstacionamiento> filtrados = new ArrayList<>();
        for (EspacioEstacionamiento espacio : listaCompletaEspacios) {
            if (espacio.getTipoEspacio() != null && espacio.getTipoEspacio().equalsIgnoreCase(tipo)) {
                filtrados.add(espacio);
            }
        }
        espaciosDisponibles.setValue(filtrados);
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
