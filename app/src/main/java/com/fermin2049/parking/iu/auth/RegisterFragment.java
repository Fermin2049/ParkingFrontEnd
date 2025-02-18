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
import com.hcaptcha.sdk.HCaptcha;
import com.hcaptcha.sdk.HCaptchaConfig;
import com.hcaptcha.sdk.HCaptchaSize;
import com.hcaptcha.sdk.HCaptchaTheme;

public class RegisterFragment extends Fragment {
    private FragmentRegisterBinding binding;
    private RegisterViewModel registerViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        registerViewModel.setContext(requireContext());

        binding.registerButton.setOnClickListener(v -> startHCaptcha());

        return binding.getRoot();
    }

    private void startHCaptcha() {
        HCaptchaConfig config = HCaptchaConfig.builder()
                .siteKey("d76010b6-5260-49b8-b8ac-749682a29162")
                .size(HCaptchaSize.NORMAL)
                .theme(HCaptchaTheme.DARK)
                .build();

        HCaptcha.getClient(requireActivity())
                .verifyWithHCaptcha(config)
                .addOnSuccessListener(tokenResponse -> registerViewModel.performRegistration(
                        binding.nameInput.getText().toString(),
                        binding.phoneInput.getText().toString(),
                        binding.emailInput.getText().toString(),
                        binding.licensePlateInput.getText().toString(),
                        binding.passwordInput.getText().toString(),
                        tokenResponse.getTokenResult()
                ))
                .addOnFailureListener(e -> registerViewModel.showErrorDialog("Captcha Failed", "Verification failed, please try again."));
    }
}
