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
import com.fermin2049.parking.databinding.FragmentReservarBinding;
import com.fermin2049.parking.iu.adapters.EspacioAdapter;
import com.fermin2049.parking.data.models.EspacioEstacionamiento;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ReservarFragment extends Fragment {

    private FragmentReservarBinding binding;
    private ReservarViewModel reservarViewModel;
    private EspacioAdapter espacioAdapter;

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

        // Inicializar ViewModel
        reservarViewModel = new ViewModelProvider(this).get(ReservarViewModel.class);

        // Configurar RecyclerView para los espacios
        binding.recyclerEspacios.setLayoutManager(new LinearLayoutManager(getContext()));
        espacioAdapter = new EspacioAdapter(new ArrayList<>(), espacio ->
                reservarViewModel.irAPago(requireActivity(), espacio));
        binding.recyclerEspacios.setAdapter(espacioAdapter);

        // Configurar el Spinner para el tipo de espacio
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Normal", "Motocicleta", "Discapacitados"});
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTipo.setAdapter(spinnerAdapter);

        // Configurar DatePicker para el EditText de fecha
        binding.etFechaReserva.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view12, year, month, dayOfMonth) -> {
                Calendar selected = Calendar.getInstance();
                selected.set(year, month, dayOfMonth);
                // Formatear la fecha como "dd-MM-yyyy"
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                binding.etFechaReserva.setText(sdf.format(selected.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
            datePickerDialog.show();
        });

        // Configurar TimePicker para el EditText de hora
        binding.etHoraSalida.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view1, hourOfDay, minute) -> {
                String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                binding.etHoraSalida.setText(timeFormatted);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        });

        // Al presionar el botón, se recogen los valores y se delega al ViewModel
        binding.btnFiltrar.setOnClickListener(v -> {
            String fechaStr = binding.etFechaReserva.getText().toString(); // "dd-MM-yyyy"
            String horaStr = binding.etHoraSalida.getText().toString(); // "HH:mm"
            String tipoSeleccionado = binding.spinnerTipo.getSelectedItem().toString();
            // Llamada al método del ViewModel que se encarga de procesar y llamar a la API
            reservarViewModel.buscarReservas(fechaStr, horaStr, tipoSeleccionado);
        });

        // Observar los espacios disponibles y actualizar el RecyclerView
        reservarViewModel.getEspaciosDisponibles().observe(getViewLifecycleOwner(), espacios -> {
            if (espacios != null) {
                espacioAdapter.updateData(espacios);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
