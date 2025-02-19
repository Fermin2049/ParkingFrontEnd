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

    private List<EspacioEstacionamiento> listaEspacios;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(EspacioEstacionamiento espacio);
    }

    public EspacioAdapter(List<EspacioEstacionamiento> listaEspacios, OnItemClickListener listener) {
        this.listaEspacios = listaEspacios;
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
        EspacioEstacionamiento espacio = listaEspacios.get(position);

        // Mostrar el número de espacio
        holder.tvNumeroEspacio.setText("Espacio #" + espacio.getNumeroEspacio());

        // Mostrar el estado del espacio
        holder.tvEstadoEspacio.setText("Estado: " + espacio.getEstado());

        // Mostrar el tipo de espacio (ej. Normal, Discapacitados)
        holder.tvTipoEspacio.setText("Tipo: " + espacio.getTipoEspacio());

        // Manejo del clic en el botón de selección
        holder.btnReservar.setOnClickListener(v -> listener.onItemClick(espacio));
    }

    @Override
    public int getItemCount() {
        return listaEspacios.size();
    }

    public static class EspacioViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumeroEspacio, tvEstadoEspacio, tvTipoEspacio;
        Button btnReservar;

        public EspacioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumeroEspacio = itemView.findViewById(R.id.textEspacio);
            tvEstadoEspacio = itemView.findViewById(R.id.textEstado);
            tvTipoEspacio = itemView.findViewById(R.id.textTipoEspacio); // Asegurar que existe
            btnReservar = itemView.findViewById(R.id.btnReservar);
        }
    }

}
