package com.example.portinas;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesConfig {

    private static final String MY_PREFERENCE_PORTINAS = "com.example.portinas";
    private static final String PREFER_CODE_KEY = "pref_code_key";
    private static final String PREFER_TOTAL_KEY = "pref_saved_total";
    ;

    public static  void saveCodeinPref(Context context, String code) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCE_PORTINAS,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREFER_CODE_KEY,code);
        editor.apply();
        editor.commit();
    }

    public static  void saveTotalinPref(Context context, int total) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCE_PORTINAS,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(PREFER_TOTAL_KEY,total);
        editor.apply();
        editor.commit();
    }

    public  static String loadCodefromPref (Context context) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCE_PORTINAS,context.MODE_PRIVATE);
        return pref.getString(PREFER_CODE_KEY, context.getString(R.string.defaultValue));
    }

    public  static String loadTotalfromPref (Context context) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCE_PORTINAS,context.MODE_PRIVATE);
        return pref.getString(PREFER_TOTAL_KEY, context.getString(R.string.defaultValue));
    }


}
