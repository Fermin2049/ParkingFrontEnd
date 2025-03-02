package com.fermin2049.parking.network;

import com.fermin2049.parking.data.models.EspacioEstacionamiento;
import com.fermin2049.parking.data.models.LoginResponse;
import com.fermin2049.parking.data.models.LoginRequest;
import com.fermin2049.parking.data.models.RecuperarPasswordRequest;
import com.fermin2049.parking.data.models.RegisterRequest;
import com.fermin2049.parking.data.models.Reserva;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @POST("auth/login-cliente")
    Call<LoginResponse> loginCliente(@Body LoginRequest loginRequest);

    @POST("clientes/recuperar-password")
    Call<Void> recuperarPassword(@Body RecuperarPasswordRequest request);

    @POST("clientes")
    Call<Void> registerCliente(@Body RegisterRequest request);

    @POST("auth/login-google")
    Call<LoginResponse> loginWithGoogle(@Body String googleIdToken);

    @GET("EspaciosEstacionamiento/disponibles")
    Call<List<EspacioEstacionamiento>> getEspaciosDisponibles(@Header("Authorization") String token);

    @GET("Reservas/disponibles")
    Call<List<EspacioEstacionamiento>> getEspaciosDisponiblesPorFecha(
            @Header("Authorization") String token,
            @Query("fechaInicio") String fechaInicio,
            @Query("fechaFin") String fechaFin,
            @Query("tipo") String tipo
    );

    @POST("Reservas")
    Call<Reserva> registrarReserva(@Header("Authorization") String token, @Body Reserva reserva);
}
