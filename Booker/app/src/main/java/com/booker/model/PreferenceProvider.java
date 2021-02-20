package com.booker.model;


import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceProvider {
    private static final String JWT_TOKEN_SAVED_AT = "dorm.booker.jwt.token";

    private final Context mAppContext;

    public PreferenceProvider(Context context) {
        mAppContext = context.getApplicationContext();
    }

    private SharedPreferences preferences() {
        return mAppContext.getSharedPreferences("dorm.booker.pref.file", Context.MODE_PRIVATE);
    }

    public void saveToken(String token) {
        preferences().edit()
                .putString(JWT_TOKEN_SAVED_AT, token)
                .apply();
    }

    public String getToken() {
        return preferences().getString(JWT_TOKEN_SAVED_AT, null);
    }
}
