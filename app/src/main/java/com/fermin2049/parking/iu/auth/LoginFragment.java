package com.fermin2049.parking.iu.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import com.fermin2049.parking.R;
import com.fermin2049.parking.databinding.FragmentLoginBinding;
import androidx.activity.result.IntentSenderRequest;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private LoginViewModel loginViewModel;
    private ActivityResultLauncher<IntentSenderRequest> googleSignInLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Registrar el launcher para el IntentSender de Google Sign-In
        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                result -> {
                    // Se pasa el resultado al ViewModel para procesarlo
                    loginViewModel.handleGoogleSignInResult(result.getResultCode(), result.getData());
                }
        );
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        loginViewModel.setContext(requireContext());

        // Botón de login tradicional
        binding.loginButton.setOnClickListener(v -> {
            String email = binding.usernameInput.getText().toString().trim();
            String password = binding.passwordInput.getText().toString().trim();
            loginViewModel.performLogin(email, password);
        });

        // Botón de login con huella digital
        binding.fingerprintLoginButton.setOnClickListener(v -> loginViewModel.authenticateWithBiometrics());

        // Botón de registro
        binding.registerNow.setOnClickListener(v -> openFragment(new RegisterFragment()));

        // Botón de recuperación de contraseña
        binding.forgotPassword.setOnClickListener(v -> openFragment(new ForgotPasswordFragment()));

        // Botón de inicio de sesión con Google:
        // Se solicita al ViewModel el IntentSenderRequest y se lanza mediante el launcher registrado
        binding.googleSignInButton.setOnClickListener(v ->
                loginViewModel.signInWithGoogle(request -> googleSignInLauncher.launch(request))
        );

        return binding.getRoot();
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.replace(android.R.id.content, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
