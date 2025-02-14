package com.fermin2049.parking.iu.auth;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fermin2049.parking.data.models.LoginRequest;
import com.fermin2049.parking.data.models.LoginResponse;
import com.fermin2049.parking.iu.main.MainActivity;
import com.fermin2049.parking.network.ApiClient;
import com.fermin2049.parking.network.ApiService;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends ViewModel {
    private Context context;
    private MutableLiveData<LoginResponse> loginResult = new MutableLiveData<>();

    public void setContext(Context context) {
        this.context = context;
    }

    public LiveData<LoginResponse> getLoginResult() {
        return loginResult;
    }

    public void performLogin(String email, String password) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        LoginRequest loginRequest = new LoginRequest(email, password);

        apiService.loginCliente(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("LoginViewModel", "Login successful. Token: " + response.body().getToken());
                    navigateToMainActivity();
                } else {
                    Log.e("LoginViewModel", "Login failed: " + response.message());
                    showErrorDialog("Login Error", "Invalid credentials.");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("LoginViewModel", "Network failure: " + t.getMessage());
                showErrorDialog("Network Error", "Failed to connect. Please try again.");
            }
        });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }


    public void showErrorDialog(String title, String message) {
        Toast.makeText(context, title + ": " + message, Toast.LENGTH_LONG).show();
    }
}
