package com.fermin2049.parking.iu.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.fermin2049.parking.R;
import com.fermin2049.parking.data.models.EspacioEstacionamiento;
import java.util.List;

public class EspacioAdapter extends RecyclerView.Adapter<EspacioAdapter.EspacioViewHolder> {

    private List<EspacioEstacionamiento> espacios;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onReservarClick(EspacioEstacionamiento espacio);
    }

    public EspacioAdapter(List<EspacioEstacionamiento> espacios, OnItemClickListener listener) {
        this.espacios = espacios;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EspacioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_espacio, parent, false);
        return new EspacioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EspacioViewHolder holder, int position) {
        EspacioEstacionamiento espacio = espacios.get(position);
        holder.bind(espacio);
    }

    @Override
    public int getItemCount() {
        return espacios.size();
    }

    public class EspacioViewHolder extends RecyclerView.ViewHolder {
        private TextView textEspacio;
        private TextView textEstado;
        private Button btnReservar;

        public EspacioViewHolder(@NonNull View itemView) {
            super(itemView);
            textEspacio = itemView.findViewById(R.id.textEspacio);
            textEstado = itemView.findViewById(R.id.textEstado);
            btnReservar = itemView.findViewById(R.id.btnReservar);
        }

        public void bind(final EspacioEstacionamiento espacio) {
            textEspacio.setText("Espacio: " + espacio.getNumeroEspacio());
            textEstado.setText("Estado: " + (espacio.getEstado().equalsIgnoreCase("Disponible") ? "Disponible" : "Ocupado"));

            btnReservar.setEnabled(espacio.isDisponible());
            btnReservar.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onReservarClick(espacio);
                }
            });
        }
    }
}
