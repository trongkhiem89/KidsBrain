package com.kid.brain.provider.request;

import android.content.Context;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by khiemnt on 3/11/2016.
 */
public final class RetrofitConfig {

    private Retrofit retrofit;
    private static RetrofitConfig config ;
    private SelfSignInClient signInClient;

    private RetrofitConfig(Context context){
        signInClient = new SelfSignInClient(context);
        retrofit = new Retrofit.Builder()
                .baseUrl(APIService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(signInClient.getOkHttpClient())
                .build();
    }

    public static final RetrofitConfig getInstance(Context context){
        if (null == config){
            config = new RetrofitConfig(context);
        }
        return config;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}
