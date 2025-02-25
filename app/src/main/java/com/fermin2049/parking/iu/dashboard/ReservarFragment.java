package com.fermin2049.parking.iu.dashboard;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fermin2049.parking.R;
import com.fermin2049.parking.iu.adapters.EspacioAdapter;
import com.fermin2049.parking.data.models.EspacioEstacionamiento;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReservarFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private EditText etFechaReserva, etHoraReserva;
    private Spinner spinnerTipo;
    private Button btnReservar;
    private RecyclerView recyclerEspacios;
    private EspacioAdapter espacioAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservar, container, false);

        dashboardViewModel = new ViewModelProvider(requireActivity()).get(DashboardViewModel.class);

        etFechaReserva = view.findViewById(R.id.etFechaReserva);
        etHoraReserva = view.findViewById(R.id.etHoraSalida);
        spinnerTipo = view.findViewById(R.id.spinnerTipo);
        btnReservar = view.findViewById(R.id.btnFiltrar);
        recyclerEspacios = view.findViewById(R.id.recyclerEspacios);

        // Configurar RecyclerView
        recyclerEspacios.setLayoutManager(new LinearLayoutManager(getContext()));
        espacioAdapter = new EspacioAdapter(new ArrayList<>(), espacio ->
                dashboardViewModel.irAPago(requireActivity(), espacio));
        recyclerEspacios.setAdapter(espacioAdapter);

        // Configurar Spinner
        ArrayAdapter<String> tipoAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item,
                new String[]{"Normal", "Motocicleta", "Discapacitados"});
        tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(tipoAdapter);

        // Configurar DatePicker para la fecha
        etFechaReserva.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view1, year, month, dayOfMonth) -> {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                etFechaReserva.setText(sdf.format(selectedDate.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
            datePickerDialog.show();
        });

        // Configurar TimePicker para la hora (ejemplo)
        etHoraReserva.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view1, hourOfDay, minute) -> {
                String hora = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                etHoraReserva.setText(hora);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        });

        // Acción del botón "Reservar" que filtra por tipo y fecha (puedes agregar validaciones adicionales)
        btnReservar.setOnClickListener(v -> {
            String fecha = etFechaReserva.getText().toString();
            String hora = etHoraReserva.getText().toString();
            String tipo = spinnerTipo.getSelectedItem().toString();

            if (fecha.isEmpty() || hora.isEmpty()) {
                // Mostrar alerta (por ejemplo, SweetAlertDialog) indicando que debe ingresar fecha y hora
                return;
            }

            // Aquí podrías combinar fecha y hora en un Date u otro formato para filtrar según la reserva
            // Por ahora, se filtra solo por tipo:
            dashboardViewModel.filtrarEspacios(tipo);
        });

        // Observar cambios en la lista filtrada
        dashboardViewModel.getEspaciosDisponibles().observe(getViewLifecycleOwner(), espacios -> {
            if (espacios != null) {
                espacioAdapter.updateData(espacios);
            }
        });

        return view;
    }
}
