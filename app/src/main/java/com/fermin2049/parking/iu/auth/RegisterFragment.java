package com.fermin2049.parking.iu.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.fermin2049.parking.databinding.FragmentRegisterBinding;

public class RegisterFragment extends Fragment {
    private FragmentRegisterBinding binding;
    private RegisterViewModel registerViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        registerViewModel.setContext(requireContext());

        binding.registerButton.setOnClickListener(v -> {
            String nombre = binding.nameInput.getText().toString().trim();
            String telefono = binding.phoneInput.getText().toString().trim();
            String email = binding.emailInput.getText().toString().trim();
            String vehiculoPlaca = binding.licensePlateInput.getText().toString().trim();
            String password = binding.passwordInput.getText().toString().trim();

            registerViewModel.performRegistration(nombre, telefono, email, vehiculoPlaca, password);
        });

        return binding.getRoot();
    }
}
