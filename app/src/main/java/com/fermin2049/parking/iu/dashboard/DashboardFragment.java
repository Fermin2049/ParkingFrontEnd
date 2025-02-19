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
import com.fermin2049.parking.iu.adapters.EspacioAdapter;
import com.fermin2049.parking.data.models.EspacioEstacionamiento;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private RecyclerView recyclerEspacios;
    private FloatingActionButton fabChat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        recyclerEspacios = view.findViewById(R.id.recyclerEspacios);
        fabChat = view.findViewById(R.id.fabChat);

        recyclerEspacios.setLayoutManager(new LinearLayoutManager(getContext()));

        dashboardViewModel.getEspaciosDisponibles().observe(getViewLifecycleOwner(), espacios -> {
            EspacioAdapter adapter = new EspacioAdapter(espacios, espacio ->
                    dashboardViewModel.irAPago(requireActivity(), espacio) // Pasando la actividad al ViewModel
            );
            recyclerEspacios.setAdapter(adapter);
        });

        dashboardViewModel.cargarEspaciosDisponibles();

        fabChat.setOnClickListener(v -> abrirChat());

        return view;
    }

    private void abrirChat() {
        // Lógica para abrir el chat con la IA
    }
}
