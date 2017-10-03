package com.oneshot.calculator.editorcalculator;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.oneshot.calculator.editorcalculator.widget.AutoResizeTextView;
import com.oneshot.calculator.editorcalculator.widget.NoImeEditText;
import com.oneshot.calculator.editorcalculator.widget.slidinglayout.SlidingUpPanelLayout;
import com.oneshot.calculator.editorcalculator.widget.stickylistview.ExpandableStickyListHeadersListView;
import com.oneshot.calculator.editorcalculator.widget.stickylistview.StickyListHeadersListView;
import com.oneshot.editorcalculator.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.oneshot.calculator.editorcalculator.Constants.CLOSE_BRACKET;
import static com.oneshot.calculator.editorcalculator.Constants.OPEN_BRACKET;

public class MainActivity extends BaseActivity {

    private final int REQ_CODE_SPEECH_INPUT = 100;      // 음성인식 activity requestCode

    private AutoResizeTextView tvResult;                // AutoResizing TextView (결과값)
    private VerticalViewPager mPager;                   // 가로 뷰페이저 (계산기/공학용계산기)
    private static NoImeEditText etGetNumber;           // 계산식 입력받는 EditText(Keyboard Hide)
    private HorizontalScrollView calculusView;          // EditText를 감싸는 가로스크롤뷰
    private SlidingUpPanelLayout slidingLayout;         // 히스토리를 보여줄 상단 슬라이딩 레이아웃

    private ArrayList<ResultData> resultArray;          // 결과 히스토리를 저장할 ArrayList
    private ResultListAdapter adapter;                  // 결과 히스토리를 저장할 Adapter
    private SQLiteDatabase mDatabase;                   // 결과 히스토리를 저장할 DB
    private KeyboardAdapter keyboardAdapter;            // 키보드 페이저 어댑터

    private boolean buttonVibrate;                      // 버튼 클릭 시 진동 여부
    private boolean saveHistory;                        // 히스토리 Local DB에 저장 여부
    private boolean viewDecimal;                        // 천단위 콤마 표현 여부
    private static int pointCount;                      // 소수점 이하 자리 표현 갯수
    private boolean showStatusBar;                      // 상단 상태바 표현 여부
    private boolean clickHistory;                       // 히스토리 클릭 시 뷰 자동 클로징 여부
    private int themeNumber;                            // 계산기 테마 번호

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create Local Database Table
        if (mDatabase == null) {
            mDatabase = openOrCreateDatabase(ResultData.class.getSimpleName(), Context.MODE_PRIVATE, null);
        }
        ResultData.createTable(mDatabase);
        resultArray = ResultData.getAllItems(mDatabase);

        tvResult = (AutoResizeTextView) findViewById(R.id.digit_view);
        calculusView = (HorizontalScrollView) findViewById(R.id.scrollView);

        mPager = (VerticalViewPager) findViewById(R.id.pager);
        mPager.setOffscreenPageLimit(2);
        keyboardAdapter = new KeyboardAdapter(MainActivity.this);
        mPager.setAdapter(keyboardAdapter);
        mPager.bringToFront();

        slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);

        slidingLayout.setScrollableViewHelper(new NestedScrollableViewHelper(mPager));
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            private int mPreviousState = ViewPager.SCROLL_STATE_IDLE;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // All of this is to inhibit any scrollable container from consuming our touch events as the user is changing pages
                if (mPreviousState == ViewPager.SCROLL_STATE_IDLE) {
                    if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                        slidingLayout.setTouchEnabled(false);
                    }
                } else {
                    if (state == ViewPager.SCROLL_STATE_IDLE || state == ViewPager.SCROLL_STATE_SETTLING) {
                        slidingLayout.setTouchEnabled(true);
                    }
                }
                mPreviousState = state;
            }
        });
        // 숫자가 표현되는 뷰들의 높이를 구해서 상단 히스토리 뷰를 구한 크기값만큼 남기고 내림
        calculusView.post(new Runnable() {
            @Override
            public void run() {
                int panelHeight = tvResult.getHeight() + calculusView.getHeight();
                slidingLayout.setPanelHeight(panelHeight);
            }
        });

        // 입력한 번호가 입력될 부분(입력 및 결과 부분, 입력 값 리스트뷰)
        etGetNumber = (NoImeEditText) findViewById(R.id.et_get_number);
        etGetNumber.setRawInputType(InputType.TYPE_CLASS_TEXT);
        etGetNumber.setTextIsSelectable(true);
        etGetNumber.setCursorVisible(true);
        etGetNumber.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        final ExpandableStickyListHeadersListView listView = (ExpandableStickyListHeadersListView) findViewById(R.id.listView);

        adapter = new ResultListAdapter(this, R.layout.list_row_data, resultArray);
        listView.setAdapter(adapter);
        // Setting ListView Sticky HeaderView
        listView.setOnHeaderClickListener(new StickyListHeadersListView.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(StickyListHeadersListView l, View header, int itemPosition, long headerId, boolean currentlySticky) {
                if (listView.isHeaderCollapsed(headerId)) {
                    listView.expand(headerId);
                } else {
                    listView.collapse(headerId);
                }
            }
        });
        // set listView emptyView
        TextView emptyView = (TextView) findViewById(R.id.emptyView);
        listView.setEmptyView(emptyView);

        // Delete item when listView Swipe item
        final SwipeDismissListViewTouchListener listViewTouchListener = new SwipeDismissListViewTouchListener(
                listView,
                new SwipeDismissListViewTouchListener.DismissCallbacks() {
                    @Override
                    public boolean canDismiss(int position) {
                        return true;
                    }

                    @Override
                    public void onDismiss(ExpandableStickyListHeadersListView listView, int[] reverseSortedPositions) {

                        for (int position : reverseSortedPositions) {
                            if (saveHistory) {
                                ResultData.delete(mDatabase, adapter.getItem(position));
                            }
                            adapter.remove(adapter.getItem(position));
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
        );
        listView.setOnTouchListener(listViewTouchListener);
        listView.setOnScrollListener(listViewTouchListener.makeScrollListener());

        // listView ItemClick Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listView.getIsScrolling()) {
                    return;
                }
                String calculus = resultArray.get(position).getCalculus();
                String result = resultArray.get(position).getResult();

                etGetNumber.setText(Util.fromHtml(calculus));
                etGetNumber.setSelection(etGetNumber.length());

                tvResult.setText(result);
                if (clickHistory) {
                    slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                }
            }
        });

        //Floating Action Button
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //Hide FAB first Time
        fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog builder = new AlertDialog.Builder(MainActivity.this).setTitle("저장한 히스토리가 모두 지워집니다.")
                        .setMessage("데이터베이스에 저장된 정보를 모두 지우시겠습니까?")
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ResultData.deleteAll(mDatabase);
                                resultArray.clear();
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("아니오", null)
                        .create();
                builder.show();
            }
        });
        slidingLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    fab.setVisibility(View.VISIBLE);
                    fab.animate().translationY(0).setInterpolator(new LinearInterpolator()).start();
                } else if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
                    int bottomMargin = params.bottomMargin;
                    fab.animate().translationY(fab.getHeight() + bottomMargin).setInterpolator(new LinearInterpolator()).start();
                }
            }
        });

    }

    /**
     * 커서 위치에 숫자 또는 사칙연산 추가
     *
     * @param number 입력할 숫자 또는 기호
     */
    public void appendString(String number) {

        int start = Math.max(etGetNumber.getSelectionStart(), 0);
        int end = Math.max(etGetNumber.getSelectionEnd(), 0);
        number = initializeEditText(number);

        etGetNumber.getText().replace(
                Math.min(start, end),
                Math.max(start, end),
                Util.fromHtml(number)
//                ,
//                0,
//                number.length()
        );
    }

    /**
     * 계산하기 위해 특정 문자열로 바꿔놓은 것을 다시 원래 문자열로 돌려놓음(html 태그 포함)
     *
     * @param s toCalculator
     * @return 바꿔놓은 문자열
     */
    public String initializeEditText(String s) {

        if (s.contains("xin")) {
            s = s.replaceAll("xin", MainActivity.this.getString(R.string.sinh) + "<sup><small>-1</sup></small>");
        }

        if (s.contains("xos")) {
            s = s.replaceAll("xos", MainActivity.this.getString(R.string.cosh) + "<sup><small>-1</sup></small>");
        }

        if (s.contains("xan")) {
            s = s.replaceAll("xan", MainActivity.this.getString(R.string.tanh) + "<sup><small>-1</sup></small>");
        }

        if (s.contains("ain")) {
            s = s.replaceAll("ain", MainActivity.this.getString(R.string.sin) + "<sup><small>-1</sup></small>");
        }

        if (s.contains("aos")) {
            s = s.replaceAll("aos", MainActivity.this.getString(R.string.cos) + "<sup><small>-1</sup></small>");
        }

        if (s.contains("aan")) {
            s = s.replaceAll("aan", MainActivity.this.getString(R.string.tan) + "<sup><small>-1</sup></small>");
        }

        if (s.contains("hin")) {
            s = s.replaceAll("hin", MainActivity.this.getString(R.string.sinh));
        }

        if (s.contains("hos")) {
            s = s.replaceAll("hos", MainActivity.this.getString(R.string.cosh));
        }

        if (s.contains("han")) {
            s = s.replaceAll("han", MainActivity.this.getString(R.string.tanh));
        }

        if (s.contains("b")) {      //cube root
            s = s.replaceAll("b", "<sup><small>3</sup></small>√");
        }

        if (s.contains("z")) {      //log2
            s = s.replaceAll("z", MainActivity.this.getString(R.string.log) + "<sub><small>2</sub></small>");
        }

        if (s.contains("sinh-1")) {
            s = s.replaceAll("sinh-1", MainActivity.this.getString(R.string.sinh) + "<sup><small>-1</sup></small>");
        }

        if (s.contains("cosh-1")) {
            s = s.replaceAll("cosh-1", MainActivity.this.getString(R.string.cosh) + "<sup><small>-1</sup></small>");
        }

        if (s.contains("tanh-1")) {
            s = s.replaceAll("tanh-1", MainActivity.this.getString(R.string.tanh) + "<sup><small>-1</sup></small>");
        }

        if (s.contains("sin-1")) {
            s = s.replaceAll("sin-1", MainActivity.this.getString(R.string.sin) + "<sup><small>-1</sup></small>");
        }

        if (s.contains("cos-1")) {
            s = s.replaceAll("cos-1", MainActivity.this.getString(R.string.cos) + "<sup><small>-1</sup></small>");
        }

        if (s.contains("tan-1")) {
            s = s.replaceAll("tan-1", MainActivity.this.getString(R.string.tan) + "<sup><small>-1</sup></small>");
        }

        if (s.contains("sinh")) {
            s = s.replaceAll("sinh", MainActivity.this.getString(R.string.sinh));
        }

        if (s.contains("cosh")) {
            s = s.replaceAll("cosh", MainActivity.this.getString(R.string.cosh));
        }

        if (s.contains("tanh")) {
            s = s.replaceAll("tanh", MainActivity.this.getString(R.string.tanh));
        }

        if (s.contains("3×√")) {      //cube root
            s = s.replaceAll("3×√", "b");
        }

        if (s.contains("y")) {          // Pow 2
            s = s.replaceAll("y", "<sup><small>２</sup></small>");
        }

        if (s.contains("k")) {          // Pow 3
            s = s.replaceAll("k", "<sup><small>３</sup></small>");
        }

        if (s.contains("v")) {          //Inverse Pow
            s = s.replaceAll("v", "<sup><small>-１</sup></small>");
        }
        L.i("initialize : " + s);
        return s;
    }

    /**
     * initializedEditText에서 복구해놓았던 문자열에서 cube root와 log2만 다시 계산할 수 있는
     * 지정한 문자열로 변환
     *
     * @param s 변환하려는 문자열
     * @return 변환 후 문자열
     */
    private String recoverEditText(String s) {

        if (s.contains("3√")) {      //cube root
            s = s.replaceAll(String.valueOf(Util.fromHtml("<sup><small>3</sup></small>" + "√")), "b");
        }

        if (s.contains("log2")) {      //log2
            s = s.replaceAll(String.valueOf(Util.fromHtml(MainActivity.this.getString(R.string.log) + "<sub><small>2</sub></small>")), "z");
        }
        if (s.contains("２")) {          // Pow 2
            s = s.replaceAll(String.valueOf(Util.fromHtml("<sup><small>２</sup></small>")), "y");
        }

        if (s.contains("３")) {          // Pow 3
            s = s.replaceAll(String.valueOf(Util.fromHtml("<sup><small>３</sup></small>")), "k");
        }

        if (s.contains("１")) {          //Inverse Pow
            s = s.replaceAll(String.valueOf(Util.fromHtml("<sup><small>-１</sup></small>")), "v");
        }
        return s;
    }

    /**
     * to Calculate of input EditText String
     * @return return to String of input Edittext
     */
    private String calcResult() {
        //현재 editText에 저장되있는 수식을 불러와서 , 제거
        String toCalculate = etGetNumber.getText().toString().trim().replaceAll(",", "");
        L.i("toCalculate : " + toCalculate);
        toCalculate = recoverEditText(toCalculate);
        if (toCalculate.charAt(0) == '-') {
            toCalculate = "0" + toCalculate;
        }
        //공학 계산식이 있을 경우 공학계산식 앞에 숫자가 있을 경우 임의로 '*'를 추가
        toCalculate = transformEngineeringCalculus("sin", toCalculate, false, "sin");      //sin
        toCalculate = transformEngineeringCalculus("cos", toCalculate, false, "cos");      //cos
        toCalculate = transformEngineeringCalculus("tan", toCalculate, false, "tan");      //tan
        toCalculate = transformEngineeringCalculus("b", toCalculate, false, "b");          //cubeRoot
        toCalculate = transformEngineeringCalculus("z", toCalculate, false, "z");          //log2
        toCalculate = transformEngineeringCalculus("sinh-1", toCalculate, true, "xin");    //sinh^-1
        toCalculate = transformEngineeringCalculus("cosh-1", toCalculate, true, "xos");    //cosh^-1
        toCalculate = transformEngineeringCalculus("tanh-1", toCalculate, true, "xan");    //tanh^-1
        toCalculate = transformEngineeringCalculus("sinh", toCalculate, true, "hin");      //sinh
        toCalculate = transformEngineeringCalculus("cosh", toCalculate, true, "hos");      //cosh
        toCalculate = transformEngineeringCalculus("tanh", toCalculate, true, "han");      //tanh
        toCalculate = transformEngineeringCalculus("sin-1", toCalculate, true, "ain");     //sin^-1
        toCalculate = transformEngineeringCalculus("cos-1", toCalculate, true, "aos");     //cos^-1
        toCalculate = transformEngineeringCalculus("tan-1", toCalculate, true, "aan");     //tan^-1
        toCalculate = transformEngineeringCalculus("ln", toCalculate, false, "ln");        //ln
        toCalculate = transformEngineeringCalculus("log", toCalculate, false, "log");      //natural log
        toCalculate = transformEngineeringCalculus("(", toCalculate, false, "(");          //열림 괄호가 나왔을 때 처리
        toCalculate = transformEngineeringCalculus("√", toCalculate, false, "√");        //Root의 경우는 뒤에 곱하기를 추가해야하는 여부를 판단하지 않아도됨.

        toCalculate = multiplyBackWord(toCalculate, ")");       //닫힘 괄호가 나왔을 때 처리
        toCalculate = multiplyBackWord(toCalculate, "!");       //팩토리얼이 나왔을 때 처리
        toCalculate = multiplyBackWord(toCalculate, "y");       //POW2이 나왔을 때 처리
        toCalculate = multiplyBackWord(toCalculate, "k");       //POW3이 나왔을 때 처리
        toCalculate = multiplyBackWord(toCalculate, "v");       //INVERSER_POW이 나왔을 때 처리

        toCalculate = multiplyFrontOrBack(toCalculate, "π");        //Pi가 나왔을 경우 처리
        toCalculate = multiplyFrontOrBack(toCalculate, "e");         //e가 나왔을 경우 처리

        String result = null;
        try {
            result = Calculator.postfix(toCalculate, pointCount);
        } catch (Exception e) {
            System.out.println(getString(R.string.alert_error_postfix));
        }

        if (result != null) {
            String getResult;

            if (viewDecimal) {
                //세자리 숫자마다 콤마 를 찍는다
                String decimalNumber;
                switch (pointCount) {
                    case 0:
                        decimalNumber = "###,###";
                        break;
                    case 2:
                        decimalNumber = "###,###.##";
                        break;
                    case 4:
                        decimalNumber = "###,###.####";
                        break;
                    case 6:
                        decimalNumber = "###,###.######";
                        break;
                    case 8:
                        decimalNumber = "###,###.########";
                        break;
                    case 10:
                        decimalNumber = "###,###.##########";
                        break;
                    default:
                        decimalNumber = "###,###";
                        break;
                }
                DecimalFormat dFormat = new DecimalFormat(decimalNumber);
                double doubleResult = Double.parseDouble(result);
                getResult = dFormat.format(doubleResult);
            } else {
                getResult = result;
            }
            return getResult;
        }
        return null;
    }

    /**
     * @param EngineeringCalculator 공학용 계산식
     * @param toCalculate           계산하려는 식
     * @param isTrigonometric       삼각함수인지 판별
     * @param translate             삼각함수일 경우 계산가능한 문자열로 변환할 문자, 아닐경우 그대로
     * @return 변환된 계산식
     */
    private String transformEngineeringCalculus(String EngineeringCalculator,
                                                  String toCalculate,
                                                  boolean isTrigonometric,
                                                  String translate) {
        //cube root 앞에 숫자가 올 경우 기호 앞에 * 추가
        if (toCalculate.contains(EngineeringCalculator)) {
            // 삼각함수일경우 계산가능한 문자로 변환
            if (isTrigonometric) {
                toCalculate = toCalculate.replaceAll(EngineeringCalculator, translate);
            }
            int rootPos = toCalculate.indexOf(EngineeringCalculator);
            while (rootPos > -1 && rootPos != 0) {
                String equalization = settingFrontMultiply(toCalculate, rootPos);
                if (equalization != null) toCalculate = equalization;

                rootPos = toCalculate.indexOf(translate, rootPos + 1);
            }
        }
        return toCalculate;
    }

    /**
     * @param toCalculate
     * @param EngineeringCalculator
     * @return
     */
    private String multiplyBackWord(String toCalculate, String EngineeringCalculator) {

        String equalization;
        int position = toCalculate.indexOf(EngineeringCalculator);
        while (position > -1) {
            if (position != toCalculate.length() - 1) {
                equalization = settingBackMultiply(toCalculate, position);
                if (equalization != null) toCalculate = equalization;
            }
            position = toCalculate.indexOf(EngineeringCalculator, position + 1);
        }
        return toCalculate;
    }

    private String multiplyFrontOrBack(String toCalculate, String EngineeringCalculator) {
        String equalization;
        int position = toCalculate.indexOf(EngineeringCalculator);
        while (position > -1) {
            //맨 앞이 기호일 경우
            if (position == 0 && toCalculate.length() > 1) {
                equalization = settingBackMultiply(toCalculate, position);
                if (equalization != null) toCalculate = equalization;

                //중간에 기호가 나올 경우
            } else if (position != 0 && toCalculate.length() > 1) {
                //기호 앞에 숫자가 있을 경우
                equalization = settingFrontMultiply(toCalculate, position);
                int length = toCalculate.length() - 1;
                if (equalization != null) {
                    toCalculate = equalization;
                    //기호 뒤에 숫자가 있을 경우
                } else if (position != length) {
                    equalization = settingBackMultiply(toCalculate, position);
                    if (equalization != null) toCalculate = equalization;
                }
            }
            position = toCalculate.indexOf(EngineeringCalculator, position + 1);
        }
        return toCalculate;
    }

    // 숫자 뒤에 기호가 붙을 경우 사이에 곱하기를 임의로 넣는다.
    public String settingBackMultiply(String calculus, int position) {

        if (Calculator.isNumeric(calculus.charAt(position + 1))) {
            String front = calculus.substring(0, position + 1);                // front = 기호 (ex. π)
            String end = calculus.substring(position + 1, calculus.length());  //end = 기호 (ex. π) 뒤에 붙는 숫자들

            calculus = front + "×" + end;

            return calculus;
        }
        return null;
    }

    //숫자 앞에 기호가 있을 경우 곱하기를 임의로 넣는다.
    private String settingFrontMultiply(String calculus, int position) {

        if (Calculator.isNumeric(calculus.charAt(position - 1))) {
            String front = calculus.substring(0, position);                 // front = 숫자
            String end = calculus.substring(position, calculus.length());   // end = 숫자 뒤에 붙는 기호
            calculus = front + "×" + end;

            return calculus;
        }
        return null;
    }

    // 사칙연산을 클릭했을 경우 연산이 중복이 되지 않게 확인 후 입력
    private void setOperator(Editable edit, int length, String last, String v) {
        if (last.equals("÷")
                || last.equals("×")
                || last.equals("-")
                || last.equals("+")
                || last.equals("(")) {
            edit.delete(length - 1, length);
        }
        if (etGetNumber.length() > 0) {
            appendString(v);
        }
    }

    /**
     * 숫자 버튼 클릭 리스너
     */
    public void onClick_Num(View v) {

        VibrateButton();
        // 누른 버튼의 숫자 또는 %, 괄호를 가져옴
        String number = ((Button) v).getText().toString();

        // 누른 버튼이 괄호일 경우 여는 괄호 닫는 괄호에 맞게 카운트를 증가
        // 괄호의 갯수가 맞는지 확인
        if (number.equals("(")) {
            OPEN_BRACKET++;
        }

        if (number.equals(")") && etGetNumber.length() == 0) {
            Toast.makeText(MainActivity.this, getString(R.string.alert_match_close_brace), Toast.LENGTH_SHORT).show();
            return;
        }

        if (number.equals(")") && OPEN_BRACKET > CLOSE_BRACKET && OPEN_BRACKET != 0) {
            CLOSE_BRACKET++;

        } else if (number.equals(")") && OPEN_BRACKET <= CLOSE_BRACKET) {
            Toast.makeText(MainActivity.this, getString(R.string.alert_match_open_brace), Toast.LENGTH_SHORT).show();
            return;
        }

        // '%' 부호 뒤에는 숫자가 올 수 없다.
        int textLength = etGetNumber.getText().toString().length();
        if (textLength > 0 && etGetNumber.getText().charAt(textLength - 1) == '%') {
            Toast.makeText(MainActivity.this, getString(R.string.alert_behind_percent), Toast.LENGTH_SHORT).show();
            return;
        }
        // 입력한 값을 EditText에 표현하는 메소드
        appendString(number);
        // 계산
        String result = calcResult();

        if (result != null) {
            tvResult.setText(result);
        }
    }

    /**
     * 첫번째 키보드 사칙연산 및 옵션 버튼 클릭 리스너
     */
    public void onClick_Opt(View v) {

        VibrateButton();
        //입력되있는 값을 temp 변수에 저장
        String temp = etGetNumber.getText().toString();
        Editable edit = etGetNumber.getText();

        int length = temp.length();
        int start = etGetNumber.getSelectionStart();
        int end = etGetNumber.getSelectionEnd();

        switch (v.getId()) {
            // 설정 버튼
            case R.id.setting:
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                break;
            // 사칙연산
            case R.id.minus:
            case R.id.plus:
            case R.id.multiply:
            case R.id.division:
                String getCursorText;
                if (length > 0) {
                    getCursorText = temp.substring(start - 1, end);
                } else {
                    getCursorText = temp;
                }
                String operator = ((Button) v).getText().toString();
                setOperator(edit, start, getCursorText, operator);
                break;
            // +/- 부호
            case R.id.plus_minus:
                //-기호 있을 경우 제거하고, 없을 경우 textview에 -만 추가 한 후 뒤에 숫자 append
                String[] splitStr = temp.split("/[/*-+]/");

                if (splitStr.length < 2 && temp.length() > 0) {
                    if ((String.valueOf(temp.charAt(0))).equals("-")) {
                        String deleteMinus = temp.replaceFirst("-", "");
                        etGetNumber.setText(deleteMinus);
                    } else {
                        etGetNumber.setText("-");
                        etGetNumber.append(temp);
                    }
                } else {
                    etGetNumber.setText("-");
                }
                etGetNumber.setSelection(etGetNumber.length());
                break;
            // '=' 결과
            case R.id.enter:
                //계산 결과값
                if (length > 0) {
                    String getResult = calcResult();
                    String getLast = temp.substring(length - 1, length);
                    if (getLast.equals("÷")
                            || getLast.equals("×")
                            || getLast.equals("-")
                            || getLast.equals("+")
                            || getLast.equals("(")) {
                        Toast.makeText(this, getString(R.string.alert_end_calculus), Toast.LENGTH_SHORT).show();
                        break;
                    } else {
                        if (OPEN_BRACKET != CLOSE_BRACKET) {
                            Toast.makeText(MainActivity.this, getString(R.string.alert_match_brace), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (getResult != null) {
                            tvResult.setText(getResult);

                            String nowDate;
                            String nowTime;
                            try {

                                Date now = new Date();
                                // 연, 월, 일
                                SimpleDateFormat dFormat = new SimpleDateFormat("yyyy.MM.dd");
                                nowDate = dFormat.format(now);
                                // AM/PM, 시, 분
                                SimpleDateFormat sdf = new SimpleDateFormat("a hh:mm");
                                nowTime = sdf.format(now);

                            } catch (Exception e) {
                                nowDate = getString(R.string.error);
                                nowTime = getString(R.string.error);
                            }

                            //DB에 계산식, 결과값, 입력한 날짜를 저장
                            ResultData result = new ResultData();
                            result.calculus = initializeEditText(temp);
                            result.result = getResult;
                            result.date = nowDate;
                            result.time = nowTime;

                            if (saveHistory) {
                                ResultData.insert(mDatabase, result);
                            }
                            resultArray.add(result);
                            adapter.notifyDataSetChanged();

                            //UI에 값 표현
                            etGetNumber.setText(getResult);
                            etGetNumber.setSelection(etGetNumber.length());

                            Constants.initialized();
                        } else {
                            tvResult.setText(getString(R.string.error));
                        }
                    } // end if
                } //end if
                break;
            // 전체 지우기 (C)
            case R.id.ac:
                //텍스트뷰의 내용 삭제
                etGetNumber.setText("");
                tvResult.setText("0");
                Constants.initialized();
                break;
            // 소수점
            case R.id.btnDot:
                //.이 있을 경우 제거하고, 없을 경우 추가.
                if (length == 0) {
                    etGetNumber.setText("0.");
                    etGetNumber.setSelection(etGetNumber.length());
                } else {
                    etGetNumber.append(".");
                } //end if
                break;
            // '%' 기호
            case R.id.percent:
                if (length > 0 && !Calculator.isOperator(temp.charAt(length - 1))) {
                    try {
                        //계산 결과값
                        appendString("%");
                        String tempResult = calcResult();
                        if (tempResult != null) {
                            tvResult.setText(tempResult);
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(MainActivity.this, getString(R.string.alert_not_percent), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                break;
            // 음성인식 버튼
            case R.id.speech:
                Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                        getString(R.string.speech_prompt));
                try {
                    startActivityForResult(speechIntent, REQ_CODE_SPEECH_INPUT);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, getString(R.string.speech_not_supported), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 2번째 키보드 클릭 리스너
     */
    public void onClick_RAD(View v) {
        VibrateButton();
        // 클릭한 버튼의 기호를 받아옴
        String number = ((Button) v).getText().toString();
        L.i("click number : " + number);
        switch (v.getId()) {
            case R.id.root_two:
                number = number + "×";
                break;
            case R.id.log_two:
                number = "z";
                break;
            case R.id.factorial:
                number = "!";
                //입력하려는 ! 앞에 숫자가 아무것도 없으면 추가하지 않음
                int cursorPosition = etGetNumber.getSelectionStart();
                if (etGetNumber.length() > 1) {
                    if (!Calculator.isNumeric(etGetNumber.getText().charAt(cursorPosition - 1)) || etGetNumber.length() < 1) {
                        number = "";
                    }
                }
                break;
            case R.id.root_x:
                number = "√";
                break;
            case R.id.cube_root_x:
                number = "b";
                break;
            case R.id.pow_two:
                number = "y";
                break;
            case R.id.pow_three:
                number = "k";
                break;
            case R.id.inverse_pow:
                number = "v";
                break;
        }
        L.i("transform number : " + number);

//        if (v.getId() == R.id.pow_two) {
//            powInvolution(2);
//        } else if (v.getId() == R.id.pow_three) {
//            powInvolution(3);
//        } else if (v.getId() == R.id.inverse_pow) {
//            powInvolution(-1);
//        } else {
            appendString(number);
            String result = calcResult();
            if (result == null) {
                tvResult.setText(getString(R.string.error));
            } else {
                tvResult.setText(result);
            }
//        }
        mPager.postDelayed(new Runnable() {

            @Override
            public void run() {
                mPager.setCurrentItem(0);
            }
        }, 100);
    }

    // 받은 수의 거듭제곱(pow)을 실행하는 메소드
    private void powInvolution(int pow) {
        if (etGetNumber.length() > 0) {
            String powResult = calcResult();
            L.i("powResult : " + powResult);
            powResult = String.valueOf(Math.pow(Double.parseDouble(powResult), pow));
            L.i("powResult : " + powResult);
            tvResult.setText(powResult);
            etGetNumber.setText(powResult);
        } else {
            Toast.makeText(this, getString(R.string.alert_involution), Toast.LENGTH_SHORT).show();
        }
    }

    // 버튼 클릭 시 진동
    private void VibrateButton() {
        if (buttonVibrate) {
            Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibe.vibrate(40);
        }
    }

    // ViewPager에서 뒤로가기 버튼을 누를 때 EditText의 내용을 변경하기 위해
    // Activity의 EditText를 리턴하는 static Method 정의
    public static NoImeEditText getEditText() {
        return etGetNumber;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            String speechCalculus = result.get(0);
            speechCalculus = Util.reformTextToNumber(speechCalculus);
            speechCalculus = Util.reformTextToCalculus(speechCalculus);
            try {
                etGetNumber.setText(speechCalculus);
                String speechResult = calcResult();
                tvResult.setText(speechResult);
                etGetNumber.setSelection(etGetNumber.length());
            } catch (NullPointerException e) {
                tvResult.setText(getString(R.string.error));
                Toast.makeText(this, getString(R.string.alert_accurate_voice), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        //앱이 종료될때 db도 같이 종료.
        if (mDatabase != null) {
            mDatabase.close();
            mDatabase = null;
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        //계산기가 하단에 숨어 있을 경우는 앱을 종료하지않고 화면으로 올라오게 설정
        if (slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Shared Preference를 액티비티가 재실행 될 때 불러와서 값을 재설정
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
        buttonVibrate = mPref.getBoolean(SettingActivity.KEY_BUTTON_VIBRATE, false);
        saveHistory = mPref.getBoolean(SettingActivity.KEY_SAVE_HISTORY, true);
        viewDecimal = mPref.getBoolean(SettingActivity.KEY_VIEW_DECIMAL, true);
        pointCount = Integer.parseInt(mPref.getString(SettingActivity.KEY_POINT_COUNT, "10"));
        showStatusBar = mPref.getBoolean(SettingActivity.KEY_STATUS_VISIBLE, false);
        clickHistory = mPref.getBoolean(SettingActivity.KEY_HISTORY_CLICK, true);

        ThemePreference themePref = new ThemePreference(MainActivity.this);
        if (themePref.getIsFirst()) {
            startActivity(new Intent(MainActivity.this, GuideActivity.class));
            finish();
        }

        themeNumber = themePref.getThemeNumber();
        // 텍스트 색상 테마색으로 변경
        int[] themeTextColor = getResources().getIntArray(R.array.main_text_color);
        tvResult.setTextColor(themeTextColor[themeNumber]);
        etGetNumber.setTextColor(themeTextColor[themeNumber]);
        //배경색 테마색으로 변경
        LinearLayout calcView = (LinearLayout) findViewById(R.id.calc_view);
        int[] mainBackgroundColor = getResources().getIntArray(R.array.main_background);
        calcView.setBackgroundColor(mainBackgroundColor[themeNumber]);

        RelativeLayout listViewParent = (RelativeLayout) findViewById(R.id.listViewParent);
        int[] listViewBackgroundColor = getResources().getIntArray(R.array.listview_background_color);
        listViewParent.setBackgroundColor(listViewBackgroundColor[themeNumber]);

        //리스트뷰 테마 갱신을 위해 어댑터 갱신
        adapter.notifyDataSetChanged();

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
        // 음성인식 버튼의 표현 여부를 확인하기 위해 keyboard Adapter notify and invalidate
        keyboardAdapter.notifyDataSetChanged();
        mPager.invalidate();
    }
}
