package com.fermin2049.parking.iu.auth;

import android.content.Context;
import androidx.lifecycle.ViewModel;
import com.fermin2049.parking.R;
import com.fermin2049.parking.data.models.RecuperarPasswordRequest;
import com.fermin2049.parking.network.ApiClient;
import com.fermin2049.parking.network.ApiService;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordViewModel extends ViewModel {
    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public void performPasswordRecovery(String email) {
        if (email.isEmpty()) {
            showErrorDialog(context.getString(R.string.validation_error), context.getString(R.string.enter_email));
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        RecuperarPasswordRequest request = new RecuperarPasswordRequest(email);

        apiService.recuperarPassword(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    showSuccessDialog(context.getString(R.string.email_sent), context.getString(R.string.reset_password_instructions));
                } else {
                    showErrorDialog(context.getString(R.string.error), context.getString(R.string.no_account_found));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showErrorDialog(context.getString(R.string.network_error), context.getString(R.string.failed_to_connect));
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
