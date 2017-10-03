package com.oneshot.calculator.editorcalculator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.oneshot.calculator.editorcalculator.widget.CircleIndicator;
import com.oneshot.editorcalculator.R;

/**
 * Created by Administrator on 2017-02-20.
 */

public class GuideActivity extends BaseActivity {

    private static ViewPager pager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        init();
    }

    public void init() {
        pager = (ViewPager) findViewById(R.id.pager);
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        GuidePagerAdapter adapter = new GuidePagerAdapter(getSupportFragmentManager(), GuideActivity.this);

        pager.setAdapter(adapter);
        indicator.setViewPager(pager);
    }

    public void setCurrentItem(int position) {
        pager.setCurrentItem(position);
    }

    private long mBackPressTime = 0;

    @Override
    public void onBackPressed() {
        int position = pager.getCurrentItem();
        L.i("position : " + position);
        switch (position) {
            case 0:
                if (System.currentTimeMillis() > mBackPressTime) {
                    mBackPressTime = System.currentTimeMillis() + 2000;
                    Toast.makeText(GuideActivity.this, getString(R.string.alert_exit), Toast.LENGTH_SHORT).show();
                } else if (System.currentTimeMillis() <= mBackPressTime) {
                    super.onBackPressed();
                }
                break;
            case 1:
                setCurrentItem(0);
                break;
            case 2:
                setCurrentItem(1);
                break;
        }
    }

    public static class GuidePagerAdapter extends FragmentPagerAdapter {

        Context mContext;

        public GuidePagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.mContext = context;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return GuideFragment.newInstance(
                            position,
                            R.drawable.tutorial1,
                            mContext.getString(R.string.tutorial_one_title),
                            mContext.getString(R.string.tutorial_one_summary),
                            mContext.getString(R.string.button_next));
                case 1:
                    return GuideFragment.newInstance(
                            position,
                            R.drawable.tutorial2,
                            mContext.getString(R.string.tutorial_two_title),
                            mContext.getString(R.string.tutorial_two_summary),
                            mContext.getString(R.string.button_next));
                case 2:
                    return GuideFragment.newInstance(
                            position,
                            R.drawable.tutorial3,
                            mContext.getString(R.string.tutorial_thr_title),
                            mContext.getString(R.string.tutorial_thr_summary),
                            mContext.getString(R.string.button_start));

            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    public static class GuideFragment extends Fragment {

        public static final GuideFragment newInstance(int position, int drawable, String title, String summary, String txtButton)
        {
            GuideFragment f = new GuideFragment();
            Bundle bdl = new Bundle();
            bdl.putInt("position", position);
            bdl.putInt("img", drawable);
            bdl.putString("title", title);
            bdl.putString("summary", summary);
            bdl.putString("txtButton", txtButton);
            f.setArguments(bdl);
            return f;
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_guide, container, false);
            ImageView ivGuide = (ImageView)v.findViewById(R.id.ivGuide);
            TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
            TextView tvSummary = (TextView) v.findViewById(R.id.tvSummary);
            Button btnNext = (Button) v.findViewById(R.id.btnNext);

            ivGuide.setImageResource(getArguments().getInt("img"));
            tvTitle.setText(getArguments().getString("title"));
            tvSummary.setText(getArguments().getString("summary"));
            btnNext.setText(getArguments().getString("txtButton"));
            final int position = getArguments().getInt("position", 0);

            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (position) {
                        case 0:
                            ((GuideActivity)getContext()).setCurrentItem(position+1);
                            break;
                        case 1:
                            ((GuideActivity)getContext()).setCurrentItem(position+1);
                            break;
                        case 2:
                            ThemePreference theme = new ThemePreference(getContext());
                            theme.setIsFirst(false);
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            getContext().startActivity(intent);
                            getActivity().finish();
                            break;
                        default:
                            break;
                    }
                }
            });
            return v;
        }
    }
}
