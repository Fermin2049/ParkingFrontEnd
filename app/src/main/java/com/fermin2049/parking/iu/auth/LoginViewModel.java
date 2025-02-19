package com.fermin2049.parking.iu.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModel;
import com.fermin2049.parking.R;
import com.fermin2049.parking.data.models.LoginRequest;
import com.fermin2049.parking.data.models.LoginResponse;
import com.fermin2049.parking.iu.main.MainActivity;
import com.fermin2049.parking.network.ApiClient;
import com.fermin2049.parking.network.ApiService;
import java.util.concurrent.Executor;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends ViewModel {
    private Context context;
    private ApiService apiService;

    public void setContext(Context context) {
        this.context = context;
        this.apiService = ApiClient.getClient().create(ApiService.class);
    }

    public boolean isBiometricAvailable() {
        BiometricManager biometricManager = BiometricManager.from(context);
        return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS;
    }

    public void authenticateWithBiometrics() {
        if (context == null) {
            return;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);
        String password = sharedPreferences.getString("password", null);

        if (email == null || password == null) {
            showErrorDialog(context.getString(R.string.biometric_login_error), context.getString(R.string.no_saved_credentials));
            return;
        }

        Executor executor = ContextCompat.getMainExecutor(context);
        BiometricPrompt biometricPrompt = new BiometricPrompt((androidx.fragment.app.FragmentActivity) context, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                performLogin(email, password);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                showErrorDialog(context.getString(R.string.authentication_failed), context.getString(R.string.try_again));
            }

            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                showErrorDialog(context.getString(R.string.authentication_error), errString.toString());
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(context.getString(R.string.authentication_error))
                .setSubtitle(context.getString(R.string.use_fingerprint))
                .setNegativeButtonText(context.getString(R.string.cancel))
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    public void performLogin(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            showErrorDialog(context.getString(R.string.validation_error), context.getString(R.string.fill_all_fields));
            return;
        }

        LoginRequest loginRequest = new LoginRequest(email, password);

        if (apiService == null) {
            showErrorDialog(context.getString(R.string.authentication_error), context.getString(R.string.server_connection_failed));
            return;
        }

        apiService.loginCliente(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();

                    if (token != null && !token.isEmpty()) {
                        guardarToken(token);
                        navigateToMainActivity();
                    } else {
                        showErrorDialog(context.getString(R.string.authentication_error_title), context.getString(R.string.invalid_token));
                    }
                } else {
                    showErrorDialog(context.getString(R.string.login_error), context.getString(R.string.invalid_credentials));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                showErrorDialog(context.getString(R.string.network_error), context.getString(R.string.server_connection_failed));
            }
        });
    }

    private void guardarToken(String token) {
        SharedPreferences prefs = context.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("token", token);
        editor.apply();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public void showErrorDialog(String title, String message) {
        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(title)
                .setContentText(message)
                .show();
    }
}
