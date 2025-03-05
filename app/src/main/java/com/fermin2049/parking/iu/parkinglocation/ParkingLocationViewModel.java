package com.fermin2049.parking.iu.parkinglocation;

import androidx.lifecycle.ViewModel;
import com.google.android.gms.maps.model.LatLng;

public class ParkingLocationViewModel extends ViewModel {
    // Datos fijos de la ubicación del estacionamiento
    private final double latitude = -34.6037;
    private final double longitude = -58.3816;
    private final String label = "Estacionamiento";

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getLabel() {
        return label;
    }

    // Método para obtener la ubicación en formato LatLng
    public LatLng getParkingLatLng() {
        return new LatLng(latitude, longitude);
    }

    // Método para construir el geo URI que se usará para abrir Google Maps
    public String getGeoUri() {
        return "geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude + "(" + label + ")";
    }
}
