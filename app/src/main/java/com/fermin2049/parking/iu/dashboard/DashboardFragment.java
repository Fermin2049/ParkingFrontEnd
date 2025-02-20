package com.fermin2049.parking.iu.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fermin2049.parking.R;
import com.fermin2049.parking.iu.adapters.BannerAdapter;
import com.fermin2049.parking.iu.adapters.EspacioAdapter;
import com.fermin2049.parking.data.models.BannerOferta;
import com.fermin2049.parking.data.models.EspacioEstacionamiento;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private RecyclerView recyclerEspacios, recyclerBanners;
    private FloatingActionButton fabChat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        recyclerEspacios = view.findViewById(R.id.recyclerEspacios);
        recyclerBanners = view.findViewById(R.id.recyclerBanners);
        fabChat = view.findViewById(R.id.fabChat);

        // Configuración de RecyclerView para espacios de estacionamiento
        recyclerEspacios.setLayoutManager(new LinearLayoutManager(getContext()));

        dashboardViewModel.getEspaciosDisponibles().observe(getViewLifecycleOwner(), espacios -> {
            EspacioAdapter adapter = new EspacioAdapter(espacios, espacio ->
                    dashboardViewModel.irAPago(requireActivity(), espacio));
            recyclerEspacios.setAdapter(adapter);
        });

        dashboardViewModel.cargarEspaciosDisponibles();

        // Configuración de RecyclerView para banners de ofertas
        recyclerBanners.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        dashboardViewModel.getBanners().observe(getViewLifecycleOwner(), banners -> {
            if (banners != null) {
                BannerAdapter adapter = new BannerAdapter(banners);
                recyclerBanners.setAdapter(adapter);
            }
        });

        dashboardViewModel.cargarBanners(); // Asegura que los banners se carguen

        fabChat.setOnClickListener(v -> abrirChat());

        return view;
    }

    private void abrirChat() {
        // Lógica para abrir el chat con la IA
    }
}
