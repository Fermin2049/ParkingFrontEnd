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

public class EspacioAdapter extends RecyclerView.Adapter<EspacioAdapter.ViewHolder> {

    private List<EspacioEstacionamiento> listaEspacios;
    private OnItemClickListener listener;

    public EspacioAdapter(List<EspacioEstacionamiento> listaEspacios, OnItemClickListener listener) {
        this.listaEspacios = (listaEspacios != null) ? listaEspacios : null;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_espacio, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EspacioEstacionamiento espacio = listaEspacios.get(position);
        holder.textEspacio.setText("Espacio #" + espacio.getNumeroEspacio());
        holder.textEstado.setText("Estado: " + (espacio.getEstado() != null ? espacio.getEstado() : "N/A"));
        holder.textTipo.setText("Tipo: " + (espacio.getTipoEspacio() != null ? espacio.getTipoEspacio() : "N/A"));
        holder.textSector.setText("Sector: " + (espacio.getSector() != null ? espacio.getSector() : "N/A"));
        holder.btnReservar.setOnClickListener(v -> listener.onItemClick(espacio));
    }

    @Override
    public int getItemCount() {
        return (listaEspacios != null) ? listaEspacios.size() : 0;
    }

    public void updateData(List<EspacioEstacionamiento> nuevosEspacios) {
        this.listaEspacios = nuevosEspacios;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textEspacio, textEstado, textTipo, textSector;
        Button btnReservar;

        public ViewHolder(View itemView) {
            super(itemView);
            textEspacio = itemView.findViewById(R.id.textEspacio);
            textEstado = itemView.findViewById(R.id.textEstado);
            textTipo = itemView.findViewById(R.id.textTipoEspacio);
            textSector = itemView.findViewById(R.id.textSector);
            btnReservar = itemView.findViewById(R.id.btnReservar);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(EspacioEstacionamiento espacio);
    }
}
