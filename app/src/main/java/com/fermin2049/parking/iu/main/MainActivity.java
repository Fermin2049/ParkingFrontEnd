package com.fermin2049.parking.iu.main;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.fermin2049.parking.R;
import com.fermin2049.parking.iu.dashboard.DashboardFragment;
import com.fermin2049.parking.iu.parkinglocation.ParkingLocationFragment;
import com.fermin2049.parking.iu.payment.PaymentFragment;
import com.fermin2049.parking.iu.notifications.NotificationsFragment;
import com.fermin2049.parking.iu.settings.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setBackgroundResource(R.drawable.bottom_nav_background); // Aplica el fondo negro con borde degradado

        // Cargar el fragmento inicial (Dashboard/Home)
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new DashboardFragment())
                    .commit();
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.nav_dashboard) {
                selectedFragment = new DashboardFragment();
            } else if (item.getItemId() == R.id.nav_parking_location) {
                selectedFragment = new ParkingLocationFragment();
            } else if (item.getItemId() == R.id.nav_payments) {
                selectedFragment = new PaymentFragment();
            } else if (item.getItemId() == R.id.nav_notifications) {
                selectedFragment = new NotificationsFragment();
            } else if (item.getItemId() == R.id.nav_settings) {
                selectedFragment = new SettingsFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });
    }
}
