package com.example.brainhemorrhage.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class   RetrofitClient {
    // Your machine's IP address on the local network.
    // The Android phone must be on the SAME Wi-Fi network as this computer.
    public static final String BASE_URL = "http://192.168.1.6/brainscan_api/";
    private static Retrofit retrofit;
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            // Lenient Gson: handles malformed/extra-character JSON responses from PHP gracefully
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            // OkHttpClient with timeouts so the app doesn't hang forever
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    /** Call this if the BASE_URL needs to change at runtime (e.g. IP changed). */
    public static void reset() {
        retrofit = null;
    }
}
