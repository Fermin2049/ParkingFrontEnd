package com.fermin2049.parking.iu.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.fermin2049.parking.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private LoginViewModel loginViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        loginViewModel.setContext(requireContext());

        binding.loginButton.setOnClickListener(v -> {
            String email = binding.usernameInput.getText().toString().trim();
            String password = binding.passwordInput.getText().toString().trim();
            loginViewModel.performLogin(email, password);
        });

        binding.fingerprintLoginButton.setOnClickListener(v -> {

                loginViewModel.authenticateWithBiometrics();

        });

        return binding.getRoot();
    }
}
