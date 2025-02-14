package com.fermin2049.parking.iu.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
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

        // Observando el resultado del login
        loginViewModel.getLoginResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                Toast.makeText(requireContext(), "Login Successful! Token: " + result.getToken(), Toast.LENGTH_LONG).show();
                // Aquí puedes navegar al MainActivity
            } else {
                loginViewModel.showErrorDialog("Login Failed", "Please check your credentials.");
            }
        });

        // Login button click listener
        binding.loginButton.setOnClickListener(v -> {
            String email = binding.usernameInput.getText().toString().trim();
            String password = binding.passwordInput.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {
                loginViewModel.performLogin(email, password);
            } else {
                loginViewModel.showErrorDialog("Validation Error", "Please fill all fields.");
            }
        });

        return binding.getRoot();
    }
}
