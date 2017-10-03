package com.oneshot.calculator.editorcalculator;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.oneshot.calculator.editorcalculator.widget.SlidingTabLayout;
import com.oneshot.editorcalculator.R;

import java.util.Locale;

/**
 * Created by Administrator on 2017-01-23.
 * 설정 페이지 Preference
 */

public class SettingActivity extends BaseActivity {

    public static final String KEY_SAVE_HISTORY = "saveHistory";
    public static final String KEY_BUTTON_VIBRATE = "buttonVibrate";
    public static final String KEY_VIEW_DECIMAL = "viewDecimal";
    public static final String KEY_POINT_COUNT = "pointCount";
    public static final String KEY_STATUS_VISIBLE = "showStatusBar";
    public static final String KEY_HISTORY_CLICK = "clickHistory";
    public static final String KEY_SPEECH_BUTTON = "isBtnSpeechVisible";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        SettingViewPagerAdapter adapter = new SettingViewPagerAdapter(getFragmentManager(), SettingActivity.this);
        pager.setAdapter(adapter);
        //뷰페이터 탭
        SlidingTabLayout tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);         //제목이 적힌 탭이 움직이지 않게 설정
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollerColor);
            }
        });
        tabs.setViewPager(pager);
    }

    public static class ThemeFragment extends Fragment implements View.OnClickListener {

        public ThemeFragment() { }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_theme, container, false);

            ImageButton btnTheme0 = (ImageButton) v.findViewById(R.id.btnTheme0);
            ImageButton btnTheme1 = (ImageButton) v.findViewById(R.id.btnTheme1);
            ImageButton btnTheme2 = (ImageButton) v.findViewById(R.id.btnTheme2);
            ImageButton btnTheme3 = (ImageButton) v.findViewById(R.id.btnTheme3);

            btnTheme0.setOnClickListener(this);
            btnTheme1.setOnClickListener(this);
            btnTheme2.setOnClickListener(this);
            btnTheme3.setOnClickListener(this);

            return v;
        }

        @Override
        public void onClick(View v) {
            ThemePreference theme = new ThemePreference(v.getContext());
            switch (v.getId()) {
                case R.id.btnTheme0 :
                    theme.setThemeNumber(0);
                    break;
                case R.id.btnTheme1 :
                    theme.setThemeNumber(1);
                    break;
                case R.id.btnTheme2 :
                    theme.setThemeNumber(2);
                    break;
                case R.id.btnTheme3 :
                    theme.setThemeNumber(3);
                    break;
            }
            getActivity().finish();
        }
    }

    @SuppressLint("ValidFragment")
    public static class PreferFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        ListPreference pointCountPrefs;
        Context mContext;

        public PreferFragment(Context context) {
            this.mContext = context;
        }



        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            pointCountPrefs = (ListPreference) findPreference(KEY_POINT_COUNT);

            String value = pointCountPrefs.getValue();
            pointCountPrefs.setSummary(String.format(Locale.getDefault(), getString(R.string.point_count_summary), value));
            pointCountPrefs.setOnPreferenceChangeListener(this);
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            if (preference.getKey().equals("evaluation")) {
                //평가하기 페이지로 넘어가는 코드 작성
                final String appPackageName = preference.getContext().getPackageName();

                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            } else if (preference.getKey().equals("version")) {
                Toast.makeText(
                        preference.getContext(),
                        getString(R.string.version_title) + " : " + getString(R.string.version_summary),
                        Toast.LENGTH_SHORT)
                        .show();
            } else if (preference.getKey().equals("tutorial")) {
                startActivity(new Intent(mContext, GuideActivity.class));
                getActivity().finish();
            }
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            // TODO Auto-generated method stub
            if (pointCountPrefs == preference) {
                String value = (String) newValue;
                pointCountPrefs.setSummary(String.format(Locale.getDefault(), getString(R.string.point_count_summary), value));

                return true;
            }
            return false;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean showStatusBar = mPref.getBoolean(KEY_STATUS_VISIBLE, false);
        //상태바 표현 여부
        if (showStatusBar) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            if (Build.VERSION.SDK_INT < 16) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else {
                View decoreView = getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
                decoreView.setSystemUiVisibility(uiOptions);
            }
        }
    }

    public class SettingViewPagerAdapter extends FragmentPagerAdapter {

        Context mContext;

        // Build a Constructor and assign the passed Values to appropriate values in the class
        public SettingViewPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.mContext = context;
        }

        //This method return the fragment for the every position in the View Pager
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new ThemeFragment();
                case 1:
                    return new PreferFragment(mContext);
            }
            return null;
        }

        // This method return the titles for the Tabs in the Tab Strip
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "테마";
                case 1:
                    return "설정";
            }
            return null;
        }

        // This method return the Number of tabs for the tabs Strip
        @Override
        public int getCount() {
            return 2;
        }
    }
}