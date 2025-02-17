package com.fermin2049.parking.network;

import com.fermin2049.parking.data.models.LoginResponse;
import com.fermin2049.parking.data.models.LoginRequest;
import com.fermin2049.parking.data.models.RecuperarPasswordRequest;
import com.fermin2049.parking.data.models.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("auth/login-cliente")
    Call<LoginResponse> loginCliente(@Body LoginRequest loginRequest);


    @POST("/api/clientes/recuperar-password")  // ✅ URL correcta
    Call<Void> recuperarPassword(@Body RecuperarPasswordRequest request);

    @POST("clientes")  // URL según la API
    Call<Void> registerCliente(@Body RegisterRequest request);



}
