package com.fermin2049.parking.iu.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.IntentSender;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;
import com.fermin2049.parking.R;
import com.fermin2049.parking.data.models.LoginRequest;
import com.fermin2049.parking.data.models.LoginResponse;
import com.fermin2049.parking.iu.main.MainActivity;
import com.fermin2049.parking.network.ApiClient;
import com.fermin2049.parking.network.ApiService;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import java.util.concurrent.Executor;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.activity.result.IntentSenderRequest;

public class LoginViewModel extends ViewModel {
    private Context context;
    private ApiService apiService;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    public static final int RC_SIGN_IN = 1000;

    public void setContext(Context context) {
        this.context = context;
        this.apiService = ApiClient.getClient().create(ApiService.class);
        this.oneTapClient = Identity.getSignInClient(context);
        configureGoogleSignIn();
    }

    private void configureGoogleSignIn() {
        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(context.getString(R.string.default_web_client_id))
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();
    }

    // Se modifica este método para recibir un callback con el IntentSenderRequest
    public void signInWithGoogle(OnGoogleSignInIntentReadyListener listener) {
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(result -> {
                    IntentSenderRequest request = new IntentSenderRequest.Builder(
                            result.getPendingIntent().getIntentSender()
                    ).build();
                    listener.onIntentReady(request);
                })
                .addOnFailureListener(e ->
                        showErrorDialog(context.getString(R.string.google_signin_error), context.getString(R.string.generic_error))
                );
    }

    // Ahora se usa el resultCode para verificar si la respuesta es RESULT_OK
    public void handleGoogleSignInResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            try {
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                String googleIdToken = credential.getGoogleIdToken();
                if (googleIdToken != null) {
                    authenticateWithGoogle(googleIdToken);
                } else {
                    showErrorDialog(context.getString(R.string.google_signin_error), context.getString(R.string.generic_error));
                }
            } catch (Exception e) {
                showErrorDialog(context.getString(R.string.google_signin_error), context.getString(R.string.generic_error));
            }
        } else {
            showErrorDialog(context.getString(R.string.google_signin_error), context.getString(R.string.generic_error));
        }
    }

    public void authenticateWithGoogle(String googleIdToken) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        AuthCredential credential = GoogleAuthProvider.getCredential(googleIdToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && firebaseAuth.getCurrentUser() != null) {
                        // Aquí se podría llamar al API para validar el token con el backend si es necesario
                        guardarCredenciales(googleIdToken, null, null);
                        navigateToMainActivity();
                    } else {
                        showErrorDialog(context.getString(R.string.google_signin_error), context.getString(R.string.generic_error));
                    }
                });
    }

    public void performLogin(String email, String password) {
        LoginRequest loginRequest = new LoginRequest(email, password);
        apiService.loginCliente(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getToken() != null) {
                    guardarCredenciales(response.body().getToken(), email, password);
                    navigateToMainActivity();
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

    public void authenticateWithBiometrics() {
        SharedPreferences prefs = context.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        String email = prefs.getString("email", null);
        String password = prefs.getString("password", null);
        if (email == null || password == null) {
            showErrorDialog(context.getString(R.string.biometric_login_error), context.getString(R.string.no_saved_credentials));
            return;
        }
        Executor executor = ContextCompat.getMainExecutor(context);
        BiometricPrompt biometricPrompt = new BiometricPrompt((FragmentActivity) context, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                        performLogin(email, password);
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        showErrorDialog(context.getString(R.string.authentication_failed), context.getString(R.string.try_again));
                    }
                });
        biometricPrompt.authenticate(new BiometricPrompt.PromptInfo.Builder()
                .setTitle(context.getString(R.string.authentication_error))
                .setSubtitle(context.getString(R.string.use_fingerprint))
                .setNegativeButtonText(context.getString(R.string.cancel))
                .build());
    }

    private void guardarCredenciales(String token, String email, String password) {
        SharedPreferences prefs = context.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("token", token);
        if (email != null && password != null) {
            editor.putString("email", email);
            editor.putString("password", password);
        }
        editor.apply();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void showErrorDialog(String title, String message) {
        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(title)
                .setContentText(message)
                .show();
    }

    // Interfaz para el callback cuando el IntentSenderRequest esté listo
    public interface OnGoogleSignInIntentReadyListener {
        void onIntentReady(IntentSenderRequest request);
    }
}
