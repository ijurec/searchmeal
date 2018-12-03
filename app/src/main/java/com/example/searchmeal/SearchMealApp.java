package com.example.searchmeal;

import android.app.Application;

import com.example.searchmeal.sync.SearchMealApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchMealApp extends Application {

    static {
        System.loadLibrary("native-lib");
    }

    public native static String invokeNativeFunction();

    private static SearchMealApi sSearchMealApi;

    @Override
    public void onCreate() {
        super.onCreate();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getApplicationContext().getString(R.string.api_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        sSearchMealApi = retrofit.create(SearchMealApi.class);
    }

    public static SearchMealApi getSearchMealApi() {
        return sSearchMealApi;
    }
}
