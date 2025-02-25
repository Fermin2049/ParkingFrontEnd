package com.fermin2049.parking.iu.dashboard;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import com.fermin2049.parking.R;
import com.fermin2049.parking.databinding.FragmentDashboardBinding;
import com.fermin2049.parking.iu.adapters.BannerAdapter;
import com.google.android.material.tabs.TabLayoutMediator;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private DashboardViewModel dashboardViewModel;
    private DashboardTabAdapter tabAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull android.view.LayoutInflater inflater,
                             @Nullable android.view.ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflar el layout usando binding
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull android.view.View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar el ViewModel
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        // --- Configurar el RecyclerView para los banners ---
        binding.recyclerBanners.setLayoutManager(
                new androidx.recyclerview.widget.LinearLayoutManager(getContext(),
                        androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));
        // Inicialmente usamos una lista vacía para evitar problemas
        BannerAdapter bannerAdapter = new BannerAdapter(new java.util.ArrayList<>());
        binding.recyclerBanners.setAdapter(bannerAdapter);

        // Observar el LiveData de banners para actualizar el adaptador cuando se carguen
        dashboardViewModel.getBanners().observe(getViewLifecycleOwner(), banners -> {
            if (banners != null) {
                BannerAdapter newAdapter = new BannerAdapter(banners);
                binding.recyclerBanners.setAdapter(newAdapter);
            }
        });
        // Cargar los banners
        dashboardViewModel.cargarBanners();

        // --- Configurar el TabLayout y ViewPager2 ---
        tabAdapter = new DashboardTabAdapter(this);
        binding.viewPager.setAdapter(tabAdapter);
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Reservar");
            } else if (position == 1) {
                tab.setText("Acceso sin Reserva");
            }
        }).attach();

        // --- Configurar el botón flotante para el chat ---
        binding.fabChat.setOnClickListener(v -> abrirChat());
    }

    private void abrirChat() {
        // Aquí implementa la lógica para abrir el chat con la IA.
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
