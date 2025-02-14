package com.fermin2049.parking.network;

import com.fermin2049.parking.data.models.LoginResponse;
import com.fermin2049.parking.data.models.LoginRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("auth/login-cliente")
    Call<LoginResponse> loginCliente(@Body LoginRequest loginRequest);
}
