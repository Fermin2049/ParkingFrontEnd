package com.fermin2049.parking.iu.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.fermin2049.parking.R;
import com.fermin2049.parking.databinding.FragmentLoginBinding;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.Identity;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private LoginViewModel loginViewModel;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private static final int RC_SIGN_IN = 1000;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        loginViewModel.setContext(requireContext());

        // Login button
        binding.loginButton.setOnClickListener(v -> {
            String email = binding.usernameInput.getText().toString().trim();
            String password = binding.passwordInput.getText().toString().trim();
            loginViewModel.performLogin(email, password);
        });

        // Register button
        binding.registerNow.setOnClickListener(v -> openFragment(new RegisterFragment()));

        // Forgot Password button
        binding.forgotPassword.setOnClickListener(v -> openFragment(new ForgotPasswordFragment()));

        // Configurar Google Sign-In
        configureGoogleSignIn();

        binding.googleSignInButton.setOnClickListener(v -> signInWithGoogle());

        return binding.getRoot();
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.replace(android.R.id.content, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void configureGoogleSignIn() {
        oneTapClient = Identity.getSignInClient(requireActivity());
        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(
                        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                .setSupported(true)
                                .setServerClientId(getString(R.string.default_web_client_id))
                                .setFilterByAuthorizedAccounts(false)
                                .build())
                .build();
    }

    private void signInWithGoogle() {
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(requireActivity(), result -> {
                    try {
                        startIntentSenderForResult(result.getPendingIntent().getIntentSender(), RC_SIGN_IN, null, 0, 0, 0, null);
                    } catch (Exception e) {
                        loginViewModel.showErrorDialog("Google Sign-In Error", "Could not start Google sign-in.");
                    }
                })
                .addOnFailureListener(e -> loginViewModel.showErrorDialog("Google Sign-In Error", "Failed to start Google sign-in."));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginViewModel.handleGoogleSignInResult(requestCode, data);
    }
}
