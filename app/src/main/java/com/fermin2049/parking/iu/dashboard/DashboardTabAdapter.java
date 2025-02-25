package com.fermin2049.parking.iu.dashboard;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class DashboardTabAdapter extends FragmentStateAdapter {

    public DashboardTabAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ReservarFragment();
            case 1:
                return new AccesoFragment();
            default:
                return new ReservarFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
