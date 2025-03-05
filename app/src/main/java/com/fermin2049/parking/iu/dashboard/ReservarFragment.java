package com.fermin2049.parking.iu.dashboard;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.fermin2049.parking.R;
import com.fermin2049.parking.databinding.FragmentReservarBinding;
import com.fermin2049.parking.iu.adapters.EspacioAdapter;
import com.fermin2049.parking.iu.payment.PaymentFragment;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class ReservarFragment extends Fragment {

    private FragmentReservarBinding binding;
    private ReservarViewModel reservarViewModel;
    private EspacioAdapter espacioAdapter;
    double totalAmount = 100.0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentReservarBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        reservarViewModel = new ViewModelProvider(this).get(ReservarViewModel.class);

        // Configurar RecyclerView para mostrar los espacios
        binding.recyclerEspacios.setLayoutManager(new LinearLayoutManager(getContext()));
        espacioAdapter = new EspacioAdapter(new ArrayList<>(), espacio -> reservarViewModel.registrarReserva(espacio));
        binding.recyclerEspacios.setAdapter(espacioAdapter);

        // Configurar el Spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Normal", "Motocicleta", "Discapacitados"});
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTipo.setAdapter(spinnerAdapter);

        // Configurar DatePicker para seleccionar la fecha
        binding.etFechaReserva.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view12, year, month, dayOfMonth) -> {
                Calendar selected = Calendar.getInstance();
                selected.set(year, month, dayOfMonth);
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                binding.etFechaReserva.setText(sdf.format(selected.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
            datePickerDialog.show();
        });

        // Configurar TimePicker para seleccionar la hora
        binding.etHoraSalida.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view1, hourOfDay, minute) -> {
                String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                binding.etHoraSalida.setText(timeFormatted);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        });

        // Al pulsar el botón, se recogen los valores y se delega al ViewModel para procesar la fecha/hora
        binding.btnFiltrar.setOnClickListener(v -> {
            String fechaStr = binding.etFechaReserva.getText().toString();
            String horaStr = binding.etHoraSalida.getText().toString();
            String tipoSeleccionado = binding.spinnerTipo.getSelectedItem().toString();
            reservarViewModel.procesarFechaHoraReserva(fechaStr, horaStr, tipoSeleccionado);
        });

        // Observar espacios disponibles para actualizar la lista
        reservarViewModel.getEspaciosDisponibles().observe(getViewLifecycleOwner(), espacios -> {
            if (espacios != null) {
                espacioAdapter.updateData(espacios);
            }
        });

        // Observar errores y mostrarlos (ejemplo usando SweetAlertDialog)
        reservarViewModel.getErrorMessageLiveData().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(errorMessage.getTitle())
                        .setContentText(errorMessage.getMessage())
                        .show();
            }
        });

        // Observar el éxito en la reserva y proceder a navegar al fragment de pago
        reservarViewModel.getReservationSuccessLiveData().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Reserva exitosa")
                        .setContentText("Se ha reservado el espacio. ¿Desea proceder al pago?")
                        .setConfirmText("Aceptar")
                        .setConfirmClickListener(dialog -> {
                            dialog.dismissWithAnimation();
                            Integer reservaId = reservarViewModel.getReservaIdLiveData().getValue();
                            String fechaReserva = reservarViewModel.getFechaReservaLiveData().getValue();
                            if (reservaId != null && fechaReserva != null) {
                                PaymentFragment paymentFragment = PaymentFragment.newInstance(reservaId, totalAmount, fechaReserva);
                                requireActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container, paymentFragment)
                                        .addToBackStack(null)
                                        .commit();
                            }
                        })
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
