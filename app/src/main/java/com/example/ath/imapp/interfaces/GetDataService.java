package com.example.ath.imapp.interfaces;

import com.example.ath.imapp.Messages;
import com.example.ath.imapp.model.Employee;
import com.example.ath.imapp.model.Example;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface GetDataService {

    @GET("messages?receiver_id=1")
    Call<List<Example>> getAllReceivedChatsForUser();

    @GET("employees")
    Call<List<Employee>> getAllEmployees();

    @GET("messages?")
    Call<List<Example>> getAllChats();

    @GET("messages?sender_id=1")
    Call<List<Example>> getAllSentChatsForUser();

    @POST("messages")
    Call<Messages> createMessage(@Body Messages message);
}