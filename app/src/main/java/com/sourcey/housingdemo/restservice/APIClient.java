package com.sourcey.housingdemo.restservice;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Biswajit on 25-02-2018.
 */

public class APIClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);



        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(responseInterceptor())
                .build();


        retrofit = new Retrofit.Builder()
//                .baseUrl("https://statsodisha.org") // Test server
//                .baseUrl("https://choice-pmayodisha.org") // Current Live server
                .baseUrl("https://choice-awaasodisha.org") // not sure of this
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        return retrofit;
    }

    private static Interceptor responseInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Response response = chain.proceed(request);
                return response;
            }
        }
        ;
    }


}
