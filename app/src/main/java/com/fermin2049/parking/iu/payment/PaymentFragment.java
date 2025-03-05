package com.fermin2049.parking.iu.payment;

import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.fermin2049.parking.databinding.FragmentPaymentBinding;

public class PaymentFragment extends Fragment {

    private FragmentPaymentBinding binding;
    private PaymentViewModel mViewModel;

    public static PaymentFragment newInstance(int reservaId, double totalAmount, String fechaReserva) {
        PaymentFragment fragment = new PaymentFragment();
        Bundle args = new Bundle();
        args.putInt("reservaId", reservaId);
        args.putDouble("totalAmount", totalAmount);
        args.putString("fechaReserva", fechaReserva);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPaymentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(PaymentViewModel.class);

        if (getArguments() != null) {
            int reservaId = getArguments().getInt("reservaId");
            double totalAmount = getArguments().getDouble("totalAmount");
            String fechaReserva = getArguments().getString("fechaReserva");
            // Se envían los datos al ViewModel
            mViewModel.setInitialData(reservaId, totalAmount, fechaReserva);
            binding.tvTotal.setText("Monto base: $" + totalAmount);
            binding.tvReservaInfo.setText("Reserva ID: " + reservaId + "\nInicio: " + fechaReserva);
        }

        // Delegar la validación y confirmación al ViewModel
        binding.btnConfirmPayment.setOnClickListener(v -> {
            String horasInput = binding.etHoras.getText().toString();
            mViewModel.onConfirmPayment(horasInput);
        });

        // Observa el resultado de la confirmación y muestra un SweetAlertDialog de éxito
        mViewModel.getConfirmationResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Confirmación")
                        .setContentText(result)
                        .show();
            }
        });

        // Observa los mensajes de error y muestra un SweetAlertDialog de error
        mViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null) {
                new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText(errorMsg)
                        .show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
