package com.oneshot.calculator.editorcalculator.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


/**
 * Created by Tony Choi on 2017-01-12.
 * Not use IME Keyboard EditText
 */
public class NoImeEditText extends EditText {


    public NoImeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * This method is called before keyboard appears when text is selected.
     * So just hide the keyboard
     * @return
     */
    @Override
    public boolean onCheckIsTextEditor() {
        hideKeyboard();

        return super.onCheckIsTextEditor();
    }

    /**
     * This methdod is called when text selection is changed, so hide keyboard to prevent it to appear
     * @param selStart
     * @param selEnd
     */
    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);

        hideKeyboard();
    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindowToken(), 0);
    }
}
