package com.fermin2049.parking.iu.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.fermin2049.parking.R;
import com.fermin2049.parking.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);

        binding.forgotPassword.setOnClickListener(v -> navigateToForgotPassword());
        binding.registerNow.setOnClickListener(v -> navigateToRegister());

        return binding.getRoot();
    }

    private void navigateToForgotPassword() {
        ((WelcomeActivity) requireActivity()).openFragment(new ForgotPasswordFragment());
    }

    private void navigateToRegister() {
        ((WelcomeActivity) requireActivity()).openFragment(new RegisterFragment());
    }
}
