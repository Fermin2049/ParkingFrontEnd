package com.fermin2049.parking.iu.auth;

import android.content.Context;
import androidx.lifecycle.ViewModel;
import com.fermin2049.parking.data.models.RegisterRequest;
import com.fermin2049.parking.network.ApiClient;
import com.fermin2049.parking.network.ApiService;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterViewModel extends ViewModel {
    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public void performRegistration(String nombre, String telefono, String email, String vehiculoPlaca, String password) {
        if (nombre.isEmpty() || telefono.isEmpty() || email.isEmpty() || vehiculoPlaca.isEmpty() || password.isEmpty()) {
            showErrorDialog("Validation Error", "All fields must be filled.");
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        RegisterRequest request = new RegisterRequest(nombre, telefono, email, vehiculoPlaca, password);

        apiService.registerCliente(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    showSuccessDialog("Success", "Registration completed successfully.");
                } else {
                    showErrorDialog("Error", "Registration failed. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showErrorDialog("Network Error", "Failed to connect. Please try again.");
            }
        });
    }

    private void showErrorDialog(String title, String message) {
        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(title)
                .setContentText(message)
                .show();
    }

    private void showSuccessDialog(String title, String message) {
        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(title)
                .setContentText(message)
                .show();
    }
}
