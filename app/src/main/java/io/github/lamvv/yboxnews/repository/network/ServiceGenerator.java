package io.github.lamvv.yboxnews.repository.network;

import java.util.concurrent.TimeUnit;

import io.github.lamvv.yboxnews.common.ApiConstants;
import io.github.lamvv.yboxnews.util.App;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sab99r
 */
public class ServiceGenerator {

    public static <S> S createService(Class<S> serviceClass) {

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(App.defaultOkHttpClient())
                .build();

        return retrofit.create(serviceClass);
    }
}
