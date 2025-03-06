package com.fermin2049.parking.iu.payment;

import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.fermin2049.parking.databinding.FragmentPaymentBinding;

public class PaymentFragment extends Fragment {

    private static final String TAG = "PaymentFragment";
    private FragmentPaymentBinding binding;
    private PaymentViewModel mViewModel;
    private CountDownTimer waitingTimer, activeTimer;

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
            mViewModel.setInitialData(reservaId, totalAmount, fechaReserva);
            binding.tvTotal.setText("Monto base: $" + totalAmount);
            binding.tvReservaInfo.setText("Reserva ID: " + reservaId + "\nInicio: " + fechaReserva);
        }

        binding.btnConfirmPayment.setOnClickListener(v -> {
            String horasInput = binding.etHoras.getText().toString();
            mViewModel.onConfirmPayment(horasInput);
        });

        mViewModel.getConfirmationResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                Log.d(TAG, "Payment confirmed: " + result);
                new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Confirmación")
                        .setContentText(result)
                        .show();
                binding.btnConfirmPayment.setEnabled(false);

                Long scheduledStart = mViewModel.getScheduledStartTime().getValue();
                Long expiration = mViewModel.getExpirationTime().getValue();
                if (scheduledStart != null && expiration != null) {
                    long currentTime = System.currentTimeMillis();
                    final long waitTime = Math.max(0, scheduledStart - currentTime);
                    long activeDuration = expiration - scheduledStart;

                    Log.d(TAG, "PaymentFragment - scheduledStart: " + scheduledStart +
                            ", currentTime: " + currentTime +
                            ", waitTime: " + waitTime +
                            ", activeDuration: " + activeDuration);

                    String timeUntilStart = formatTime(waitTime);
                    new SweetAlertDialog(getContext(), SweetAlertDialog.NORMAL_TYPE)
                            .setTitleText("Reserva Programada")
                            .setContentText("La reserva iniciará en: " + timeUntilStart)
                            .setConfirmText("Aceptar")
                            .setConfirmClickListener(dialog -> {
                                dialog.dismissWithAnimation();
                                // Iniciar contador de espera y luego el activo
                                if (waitTime > 0) {
                                    startWaitingCountdown(waitTime, () -> {
                                        // Al finalizar la espera, se oculta el contador de espera y se muestra el activo
                                        binding.tvWaitCounter.setText("Reserva iniciada");
                                        startActiveCountdown(activeDuration, null);
                                    });
                                } else {
                                    startActiveCountdown(activeDuration, null);
                                }
                            })
                            .show();
                }
            }
        });

        mViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null) {
                Log.e(TAG, "Error: " + errorMsg);
                new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText(errorMsg)
                        .show();
            }
        });
    }

    private void startWaitingCountdown(long duration, Runnable onFinish) {
        if (waitingTimer != null) waitingTimer.cancel();
        Log.d(TAG, "Starting waiting countdown: " + duration + " ms");
        waitingTimer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.tvWaitCounter.setText("Reserva inicia en: " + formatTime(millisUntilFinished));
            }
            @Override
            public void onFinish() {
                Log.d(TAG, "Waiting countdown finished");
                if (onFinish != null) onFinish.run();
            }
        }.start();
    }

    private void startActiveCountdown(long duration, Runnable onFinish) {
        if (activeTimer != null) activeTimer.cancel();
        Log.d(TAG, "Starting active countdown: " + duration + " ms");
        activeTimer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.tvActiveCounter.setText("Tiempo restante de la reserva: " + formatTime(millisUntilFinished));
            }
            @Override
            public void onFinish() {
                binding.tvActiveCounter.setText("Reserva finalizada");
                mViewModel.completeReservation();
                Log.d(TAG, "Active countdown finished");
                if (onFinish != null) onFinish.run();
            }
        }.start();
    }

    private String formatTime(long millis) {
        long totalSeconds = millis / 1000;
        long days = totalSeconds / (24 * 3600);
        long hours = (totalSeconds % (24 * 3600)) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append(" días ");
        }
        sb.append(String.format("%02d:%02d:%02d", hours, minutes, seconds));
        return sb.toString();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (waitingTimer != null) waitingTimer.cancel();
        if (activeTimer != null) activeTimer.cancel();
        binding = null;
    }
}
