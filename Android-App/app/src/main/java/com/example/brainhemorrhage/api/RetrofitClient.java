package com.example.brainhemorrhage.api;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    // Default fallback URL — used when no custom URL is saved in SharedPreferences.
    // Change this to your server's IP for initial development builds.
    public static final String DEFAULT_BASE_URL = "http://10.18.189.84/brainscan_api/";
    public static final String BASE_URL = DEFAULT_BASE_URL;
    private static final String PREFS_NAME = "HemoScanPrefs";
    private static final String KEY_SERVER_URL = "server_url";

    private static Retrofit retrofit;
    private static String currentBaseUrl = null;

    /**
     * Returns the currently active Retrofit instance.
     * If the saved server URL has changed since last call, the instance is recreated.
     */
    public static Retrofit getRetrofitInstance() {
        return getRetrofitInstance(null);
    }

    /**
     * Returns a Retrofit instance configured with the URL stored in SharedPreferences.
     * Pass a Context so the URL can be read; pass null to reuse the cached instance.
     */
    public static Retrofit getRetrofitInstance(Context context) {
        String savedUrl = null;
        if (context != null) {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            savedUrl = prefs.getString(KEY_SERVER_URL, DEFAULT_BASE_URL);
            if (savedUrl == null || savedUrl.isEmpty()) savedUrl = DEFAULT_BASE_URL;
            if (!savedUrl.endsWith("/")) savedUrl += "/";
        }

        // Rebuild if URL changed or no instance exists
        if (retrofit == null || (savedUrl != null && !savedUrl.equals(currentBaseUrl))) {
            currentBaseUrl = (savedUrl != null) ? savedUrl : DEFAULT_BASE_URL;
            buildRetrofit(currentBaseUrl);
        }
        return retrofit;
    }

    /** Returns the URL currently being used by the Retrofit instance. */
    public static String getBaseUrl() {
        return currentBaseUrl != null ? currentBaseUrl : DEFAULT_BASE_URL;
    }

    /** Saves a new server URL to SharedPreferences and resets the Retrofit instance. */
    public static void saveServerUrl(Context context, String url) {
        if (url == null || url.isEmpty()) url = DEFAULT_BASE_URL;
        if (!url.endsWith("/")) url += "/";
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_SERVER_URL, url).apply();
        currentBaseUrl = url;
        buildRetrofit(url);
    }

    /** Force-rebuild the Retrofit instance (e.g. after URL change from Settings). */
    public static void reset() {
        retrofit = null;
        currentBaseUrl = null;
    }

    private static void buildRetrofit(String baseUrl) {
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
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
}
