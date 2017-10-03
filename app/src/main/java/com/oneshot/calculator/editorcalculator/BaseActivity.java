package com.oneshot.calculator.editorcalculator;

import android.graphics.Typeface;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Administrator on 2017-02-01.
 */

public class BaseActivity extends AppCompatActivity {

    private static Typeface mTypeface = null;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
//        if (mTypeface == null) {
//            mTypeface = Typeface.createFromAsset(this.getAssets(), "HamChoRongR.ttf");
//        }
//        setGlobalFont(getWindow().getDecorView());
    }

//    private void setGlobalFont(View view) {
//        if (view != null) {
//            if (view instanceof ViewGroup) {
//                ViewGroup vg = (ViewGroup) view;
//                int vgCnt = vg.getChildCount();
//                for (int i = 0; i < vgCnt; i++) {
//                    View v = vg.getChildAt(i);
//                    if (v instanceof NoImeEditText) {
//                        ((NoImeEditText)v).setTypeface(mTypeface);
//                    } else if (v instanceof AutoResizeTextView) {
//                        ((AutoResizeTextView)v).setTypeface(mTypeface);
//                    } else if (v instanceof TextView) {
//                        ((TextView)v).setTypeface(mTypeface);
//                    }
//                    setGlobalFont(v);
//                }
//            }
//        }
//    }
}
