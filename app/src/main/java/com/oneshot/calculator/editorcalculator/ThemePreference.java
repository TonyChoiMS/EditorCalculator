package com.oneshot.calculator.editorcalculator;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2017-02-02.
 */

public class ThemePreference {
    Context mContext;

    public ThemePreference(Context context) {
        this.mContext = context;
    }

    public int getThemeNumber() {
        SharedPreferences mPref = mContext.getSharedPreferences("theme", Context.MODE_PRIVATE);
        return mPref.getInt("themeNumber", 0);
    }

    public void setThemeNumber(int theme) {
        SharedPreferences mPref = mContext.getSharedPreferences("theme", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPref.edit();
        editor.putInt("themeNumber", theme);
        editor.commit();
    }

    public boolean getIsFirst() {
        SharedPreferences mPref = mContext.getSharedPreferences("theme", Context.MODE_PRIVATE);
        return mPref.getBoolean("isFirst", true);
    }

    public void setIsFirst(boolean isFirst) {
        SharedPreferences mPref = mContext.getSharedPreferences("theme", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPref.edit();
        editor.putBoolean("isFirst", isFirst);
        editor.commit();
    }
}
