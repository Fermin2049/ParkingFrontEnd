package com.fermin2049.parking.iu.parkinglocation;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fermin2049.parking.R;

public class ParkingLocationFragment extends Fragment {

    private ParkingLocationViewModel mViewModel;

    public static ParkingLocationFragment newInstance() {
        return new ParkingLocationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_parking_location, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ParkingLocationViewModel.class);
        // TODO: Use the ViewModel
    }

}