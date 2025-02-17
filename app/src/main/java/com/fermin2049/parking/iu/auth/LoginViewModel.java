package com.fermin2049.parking.iu.auth;

import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fermin2049.parking.data.models.LoginRequest;
import com.fermin2049.parking.data.models.LoginResponse;
import com.fermin2049.parking.iu.main.MainActivity;
import com.fermin2049.parking.network.ApiClient;
import com.fermin2049.parking.network.ApiService;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends ViewModel {
    private Context context;
    private MutableLiveData<LoginResponse> loginResult = new MutableLiveData<>();
    private SignInClient oneTapClient;

    public void setContext(Context context) {
        this.context = context;
        this.oneTapClient = Identity.getSignInClient(context);
    }

    public MutableLiveData<LoginResponse> getLoginResult() {
        return loginResult;
    }

    public void performLogin(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            showErrorDialog("Validation Error", "Please fill all fields.");
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        LoginRequest loginRequest = new LoginRequest(email, password);

        apiService.loginCliente(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    loginResult.setValue(response.body());
                    navigateToMainActivity();
                } else {
                    showErrorDialog("Login Error", "Invalid credentials.");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                showErrorDialog("Network Error", "Failed to connect. Please try again.");
            }
        });
    }

    public void handleGoogleSignInResult(int requestCode, Intent data) {
        if (requestCode == 1000) {  // RC_SIGN_IN
            try {
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                String googleIdToken = credential.getGoogleIdToken();

                if (googleIdToken != null) {
                    authenticateWithGoogle(googleIdToken);
                } else {
                    showErrorDialog("Google Sign-In Failed", "No ID Token found.");
                }
            } catch (Exception e) {
                showErrorDialog("Google Sign-In Error", "Could not retrieve credential.");
            }
        }
    }

    public void authenticateWithGoogle(String googleIdToken) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        AuthCredential credential = GoogleAuthProvider.getCredential(googleIdToken, null);

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            navigateToMainActivity();
                        } else {
                            showErrorDialog("Google Sign-In Failed", "User authentication failed.");
                        }
                    } else {
                        showErrorDialog("Google Sign-In Failed", "Authentication error.");
                    }
                });
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
