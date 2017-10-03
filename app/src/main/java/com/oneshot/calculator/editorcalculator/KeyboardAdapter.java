package com.oneshot.calculator.editorcalculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.view.PagerAdapter;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.oneshot.calculator.editorcalculator.widget.NoImeEditText;
import com.oneshot.editorcalculator.R;

import static com.oneshot.calculator.editorcalculator.Constants.CLOSE_BRACKET;
import static com.oneshot.calculator.editorcalculator.Constants.OPEN_BRACKET;

/**
 * Created by Tony Choi on 2017-01-31.
 * For MainActivity Keyboard ViewPager Adapter
 * 1st Page
 */

public class KeyboardAdapter extends PagerAdapter {

    private Context mContext;
    private CountDownTimer mTimer;      // 키보드 삭제버튼 롱클릭할 경우 실행될 타이머
    //Calculator 1
    private Button[] btnNumber = new Button[12];
    private ImageButton[] btnTop = new ImageButton[3];
    private Button[] btnCalc = new Button[4];
    private Button[] btnSymbol = new Button[3];
    private Button btnAC;
    private Button btnEnter;

    //Calculator 2
    private Button[] btnEngineering = new Button[24];

    public KeyboardAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View v = null;
        Context context = container.getContext();

        if (position == 0) {
            //일반 계산기 뷰
            v = LayoutInflater.from(context).inflate(R.layout.calculator, null);

            btnNumber[0] = (Button) v.findViewById(R.id.btn0);
            btnNumber[1] = (Button) v.findViewById(R.id.btn1);
            btnNumber[2] = (Button) v.findViewById(R.id.btn2);
            btnNumber[3] = (Button) v.findViewById(R.id.btn3);
            btnNumber[4] = (Button) v.findViewById(R.id.btn4);
            btnNumber[5] = (Button) v.findViewById(R.id.btn5);
            btnNumber[6] = (Button) v.findViewById(R.id.btn6);
            btnNumber[7] = (Button) v.findViewById(R.id.btn7);
            btnNumber[8] = (Button) v.findViewById(R.id.btn8);
            btnNumber[9] = (Button) v.findViewById(R.id.btn9);
            btnNumber[10] = (Button) v.findViewById(R.id.plus_minus);
            btnNumber[11] = (Button) v.findViewById(R.id.btnDot);

            btnSymbol[0] = (Button) v.findViewById(R.id.percent);
            btnSymbol[1] = (Button) v.findViewById(R.id.open);
            btnSymbol[2] = (Button) v.findViewById(R.id.close);

            btnTop[0] = (ImageButton) v.findViewById(R.id.setting);
            btnTop[1] = (ImageButton) v.findViewById(R.id.btnDelete);
            btnTop[2] = (ImageButton) v.findViewById(R.id.speech);

            btnAC = (Button) v.findViewById(R.id.ac);

            btnCalc[0] = (Button) v.findViewById(R.id.division);
            btnCalc[1] = (Button) v.findViewById(R.id.multiply);
            btnCalc[2] = (Button) v.findViewById(R.id.minus);
            btnCalc[3] = (Button) v.findViewById(R.id.plus);

            btnEnter = (Button) v.findViewById(R.id.enter);

            changeButtonBackground(position);
            btnTop[1].setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            deleteChar();
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            mTimer.cancel();
                            break;
                    }
                    return false;
                }
            });

            //음성인식 버튼 표현 여부
            SharedPreferences voicePref = PreferenceManager.getDefaultSharedPreferences(context);
            boolean isBtnSpeechVisible = voicePref.getBoolean(SettingActivity.KEY_SPEECH_BUTTON, true);
            if (isBtnSpeechVisible) {
                btnEnter.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f));
                btnTop[2].setVisibility(View.VISIBLE);
            } else {
                btnEnter.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.8f));
                btnTop[2].setVisibility(View.GONE);
            }

        } else if (position == 1) {
            //공학용 계산기 뷰
            v = LayoutInflater.from(context).inflate(R.layout.calculator_two, null);

            btnEngineering[0] = (Button) v.findViewById(R.id.sin);
            btnEngineering[1] = (Button) v.findViewById(R.id.cos);
            btnEngineering[2] = (Button) v.findViewById(R.id.tan);
            btnEngineering[3] = (Button) v.findViewById(R.id.arc_sin);
            btnEngineering[4] = (Button) v.findViewById(R.id.arc_cos);
            btnEngineering[5] = (Button) v.findViewById(R.id.arc_tan);
            btnEngineering[6] = (Button) v.findViewById(R.id.sinh);
            btnEngineering[7] = (Button) v.findViewById(R.id.cosh);
            btnEngineering[8] = (Button) v.findViewById(R.id.tanh);
            btnEngineering[9] = (Button) v.findViewById(R.id.arc_sinh);
            btnEngineering[10] = (Button) v.findViewById(R.id.arc_cosh);
            btnEngineering[11] = (Button) v.findViewById(R.id.arc_tanh);
            btnEngineering[12] = (Button) v.findViewById(R.id.pi);
            btnEngineering[13] = (Button) v.findViewById(R.id.exp);
            btnEngineering[14] = (Button) v.findViewById(R.id.root_two);
            btnEngineering[15] = (Button) v.findViewById(R.id.pow_two);
            btnEngineering[16] = (Button) v.findViewById(R.id.pow_three);
            btnEngineering[17] = (Button) v.findViewById(R.id.inverse_pow);
            btnEngineering[18] = (Button) v.findViewById(R.id.log);
            btnEngineering[19] = (Button) v.findViewById(R.id.ln);
            btnEngineering[20] = (Button) v.findViewById(R.id.log_two);
            btnEngineering[21] = (Button) v.findViewById(R.id.factorial);
            btnEngineering[22] = (Button) v.findViewById(R.id.root_x);
            btnEngineering[23] = (Button) v.findViewById(R.id.cube_root_x);

            btnEngineering[3].setText(Util.fromHtml(context.getString(R.string.sin) + "<sup><small>-1</sup></small>"));
            btnEngineering[4].setText(Util.fromHtml(context.getString(R.string.cos) + "<sup><small>-1</sup></small>"));
            btnEngineering[5].setText(Util.fromHtml(context.getString(R.string.tan) + "<sup><small>-1</sup></small>"));
            btnEngineering[9].setText(Util.fromHtml(context.getString(R.string.sinh) + "<sup><small>-1</sup></small>"));
            btnEngineering[10].setText(Util.fromHtml(context.getString(R.string.cosh) + "<sup><small>-1</sup></small>"));
            btnEngineering[11].setText(Util.fromHtml(context.getString(R.string.tanh) + "<sup><small>-1</sup></small>"));
            btnEngineering[15].setText(Util.fromHtml(context.getString(R.string.pow) + "<sup><small>2</sup></small>"));
            btnEngineering[16].setText(Util.fromHtml(context.getString(R.string.pow) + "<sup><small>3</sup></small>"));
            btnEngineering[17].setText(Util.fromHtml(context.getString(R.string.pow) + "<sup><small>-1</sup></small>"));
            btnEngineering[20].setText(Util.fromHtml(context.getString(R.string.log) + "<sub><small>2</sub></small>"));
            btnEngineering[23].setText(Util.fromHtml("<sup><small>3</sup></small>" + context.getString(R.string.root_x)));

            changeButtonBackground(position);
        }
        container.addView(v);

        return v;
    }

    /**
     * Setting Keyboard Theme
     *
     * @param position Viewpager Page position
     */
    private void changeButtonBackground(int position) {
        ThemePreference themePref = new ThemePreference(mContext);
        int themeNumber = themePref.getThemeNumber();
        int numberBackground = 0;
        int topBackground = 0;
        int calculateBackground = 0;
        int equalBackground = 0;

        switch (themeNumber) {
            //theme01(default)
            case 0:
                numberBackground = R.drawable.theme_default_box_number;
                topBackground = R.drawable.theme_default_box_top_button;
                calculateBackground = R.drawable.theme_default_box_enter;
                equalBackground = R.drawable.theme_default_box_equal;

                btnTop[0].setImageResource(R.drawable.settings);
                btnTop[1].setImageResource(R.drawable.delete);
                btnTop[2].setImageResource(R.drawable.btn_speech);
                break;
            //theme02
            case 1:
                numberBackground = R.drawable.theme_blue_box_number;
                topBackground = R.drawable.theme_blue_box_top_button;
                calculateBackground = R.drawable.theme_blue_box_enter;
                equalBackground = R.drawable.theme_blue_box_equal;

                btnTop[0].setImageResource(R.drawable.settings);
                btnTop[1].setImageResource(R.drawable.delete);
                btnTop[2].setImageResource(R.drawable.btn_speech);
                break;
            //theme03
            case 2:
                numberBackground = R.drawable.theme_red_box_number;
                topBackground = R.drawable.theme_red_box_top_button;
                calculateBackground = R.drawable.theme_red_box_enter;
                equalBackground = R.drawable.theme_red_box_equal;

                btnTop[0].setImageResource(R.drawable.settings);
                btnTop[1].setImageResource(R.drawable.delete);
                btnTop[2].setImageResource(R.drawable.btn_speech);
                break;
            //theme04
            case 3:
                numberBackground = R.drawable.theme_developer_box_number;
                topBackground = R.drawable.theme_developer_box_top_butto;
                calculateBackground = R.drawable.theme_developer_box_enter;
                equalBackground = R.drawable.theme_developer_box_equal;

                btnTop[0].setImageResource(R.drawable.btn_settings);
                btnTop[1].setImageResource(R.drawable.btn_del);
                btnTop[2].setImageResource(R.drawable.btn_speech);
                break;
        }
        int[] textColorArray = mContext.getResources().getIntArray(R.array.calculator_button_color);
        int[] topColorArray = mContext.getResources().getIntArray(R.array.calculator_top_color);
        int[] enterColorArray = mContext.getResources().getIntArray(R.array.calculator_enter_color);
        int[] equalColorArray = mContext.getResources().getIntArray(R.array.calculator_equal_color);
        if (position == 0) {
            for (int i = 0; i < 12; i++) {
                btnNumber[i].setBackgroundResource(numberBackground);
                btnNumber[i].setTextColor(textColorArray[themeNumber]);
            }
            for (int i = 0; i < 4; i++) {
                btnCalc[i].setBackgroundResource(calculateBackground);
                btnCalc[i].setTextColor(enterColorArray[themeNumber]);
            }
            int paddingValve = Util.dpToPx(20);
            for (int i = 0; i < 3; i++) {
                btnTop[i].setBackgroundResource(topBackground);
                btnTop[i].setPadding(paddingValve, paddingValve, paddingValve, paddingValve);
            }
            for (int i = 0; i < 3; i++) {
                btnSymbol[i].setBackgroundResource(numberBackground);
                btnSymbol[i].setTextColor(topColorArray[themeNumber]);
            }
            btnAC.setBackgroundResource(topBackground);
            btnAC.setTextColor(topColorArray[themeNumber]);

            btnEnter.setBackgroundResource(equalBackground);
            btnEnter.setTextColor(equalColorArray[themeNumber]);

        } else if (position == 1) {
            for (int i = 0; i < 24; i++) {
                btnEngineering[i].setBackgroundResource(numberBackground);
                btnEngineering[i].setTextColor(textColorArray[themeNumber]);
            }
        }
    }

    private void deleteChar() {
        // 타이머를 동작시켜서 일정 시간 간격으로
        // 타이머가 돌고 있는 동안 숫자 삭제 프로세스를 실행
        mTimer = new CountDownTimer(9999999, 200) {
            @Override
            public void onTick(long l) {
                NoImeEditText etGetNumber = MainActivity.getEditText();
                Editable edit = etGetNumber.getText();
                int start = etGetNumber.getSelectionStart();
                int length = etGetNumber.getText().toString().length();
                if (length > 0 && start != 0) {
                    //삭제 중 '(' 또는 ')'를 삭제할 경우 전역변수 카운트도 같이 감소
                    if (etGetNumber.getText().charAt(start - 1) == ')') {
                        CLOSE_BRACKET--;
                    } else if (etGetNumber.getText().charAt(start - 1) == '(') {
                        OPEN_BRACKET--;
                    }
                    if (start == 0) {
                        edit.clear();
                        Constants.initialized();
                    } else {
                        edit.delete(start - 1, start);
                    } //end if
                } //end if
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
    }
}
