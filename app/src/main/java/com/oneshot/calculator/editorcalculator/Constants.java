package com.oneshot.calculator.editorcalculator;

/**
 * Created by Tony Choi on 2017-01-31.
 * Constants Class
 */

public class Constants {
    public static int OPEN_BRACKET = 0;                 // 여는 괄호 카운트
    public static int CLOSE_BRACKET = 0;                // 닫는 괄호 카운트

    //괄호 카운트 초기화 메소드
    public static void initialized() {
        OPEN_BRACKET = 0;
        CLOSE_BRACKET = 0;
    }
}
