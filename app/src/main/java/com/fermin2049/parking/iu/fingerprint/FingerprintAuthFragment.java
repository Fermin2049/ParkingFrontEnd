package com.fermin2049.parking.iu.fingerprint;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fermin2049.parking.R;

public class FingerprintAuthFragment extends Fragment {

    private FingerprintAuthViewModel mViewModel;

    public static FingerprintAuthFragment newInstance() {
        return new FingerprintAuthFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fingerprint_auth, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FingerprintAuthViewModel.class);
        // TODO: Use the ViewModel
    }

}