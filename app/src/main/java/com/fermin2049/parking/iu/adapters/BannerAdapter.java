package com.fermin2049.parking.iu.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.fermin2049.parking.R;
import com.fermin2049.parking.data.models.BannerOferta;
import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private final List<BannerOferta> listaBanners;

    public BannerAdapter(List<BannerOferta> listaBanners) {
        this.listaBanners = listaBanners;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        BannerOferta banner = listaBanners.get(position);
        holder.imgBanner.setImageResource(banner.getImagenResId());
        holder.txtTitulo.setText(banner.getTitulo());
        holder.txtDescripcion.setText(banner.getDescripcion());
    }

    @Override
    public int getItemCount() {
        return listaBanners.size();
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBanner;
        TextView txtTitulo, txtDescripcion;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBanner = itemView.findViewById(R.id.imgBanner);
            txtTitulo = itemView.findViewById(R.id.txtTituloBanner);
            txtDescripcion = itemView.findViewById(R.id.txtDescripcionBanner);
        }
    }
}
