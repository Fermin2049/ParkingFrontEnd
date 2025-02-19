package com.fermin2049.parking.iu.auth;

import android.content.Context;
import androidx.lifecycle.ViewModel;
import com.fermin2049.parking.R;
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

    public void performRegistration(String name, String phone, String email, String licensePlate, String password, String hcaptchaToken) {
        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || licensePlate.isEmpty() || password.isEmpty() || hcaptchaToken.isEmpty()) {
            showErrorDialog(context.getString(R.string.validation_error), context.getString(R.string.fill_all_fields));
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        RegisterRequest request = new RegisterRequest(name, phone, email, licensePlate, password, hcaptchaToken);

        apiService.registerCliente(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    showSuccessDialog(context.getString(R.string.registration_success), context.getString(R.string.account_created));
                } else {
                    showErrorDialog(context.getString(R.string.registration_failed), context.getString(R.string.server_validation_failed));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showErrorDialog(context.getString(R.string.network_error), context.getString(R.string.failed_to_connect));
            }
        });
    }

    public void showErrorDialog(String title, String message) {
        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(title)
                .setContentText(message)
                .show();
    }

    public void showSuccessDialog(String title, String message) {
        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(title)
                .setContentText(message)
                .show();
    }
}
