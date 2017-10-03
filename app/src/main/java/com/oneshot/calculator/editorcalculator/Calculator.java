package com.oneshot.calculator.editorcalculator;

import java.util.EmptyStackException;
import java.util.Stack;

/**
 * Created by Tony Choi on 2017-01-31.
 * This class is calculate number and so on.
 */

public class Calculator {

    private static int FIRST_PRIORITY = 0;
    private static int SECOND_PRIORITY = 1;
    private static int THIRD_PRIORITY = 2;
    private static int LAST_PRIORITY = 3;

    // 연산자 우선순위를 판정 숫자화 반환
    private static int operatorPriority(char operator) {
        if (operator == '(') return FIRST_PRIORITY;
        if (operator == '+' || operator == '-') return SECOND_PRIORITY;
        if (operator == '×' || operator == '÷') return THIRD_PRIORITY;

        return LAST_PRIORITY;
    }

    // 연산자를 표현한 문자인지 검사
    public static boolean isOperator(char ch) {
        return (ch == '+' || ch == '-' || ch == '×' || ch == '÷');
    }

    // 숫자를 표현한 문자인지 검사 (char를 사용하기 때문에 각각 한 단어씩 판별)
    public static boolean isNumeric(char ch) {
        return (ch >= '0' && ch <= '9' || ch == '.' || ch == '%'
                || ch == 'π' || ch == 'e' || ch == '√' || ch == '!' || ch == 's' || ch == 'i'
                || ch == 'n' || ch == 'c' || ch == 'o' || ch == 't' || ch == 'a' || ch == 'h'
                || ch == 'x' || ch == 'l' || ch == 'g' || ch == 'z' || ch == 'b' || ch == 'y'
                || ch == 'k' || ch == 'v');
    }

    // 중위표현식을 후위표현식으로 변환
    public static String postfix(String expression, int pointCount) {

        char[] exp = expression.toCharArray();
        char ch;
        StringBuffer sb = new StringBuffer();
        Stack stack = new Stack();

        for (int i = 0; i < exp.length; i++) {
            if (exp[i] == '(') {
                stack.push(exp[i]);

            } else if (exp[i] == ')') {

                while ((ch = (Character) stack.pop()) != '(') {
                    sb.append(ch);
                    sb.append(' ');
                }

            } else if (isOperator(exp[i])) {

                while (!stack.isEmpty() &&
                        operatorPriority((Character) stack.peek()) >= operatorPriority(exp[i])) {

                    sb.append(stack.pop());
                    sb.append(' ');
                }
                stack.push(exp[i]);

            } else if (isNumeric(exp[i])) {
                do {
                    sb.append(exp[i++]);
                } while (i < exp.length && isNumeric(exp[i]));
                sb.append(' ');
                i--;
            }
        }

        while (!stack.isEmpty()) {
            sb.append(stack.pop());
            sb.append(' ');
        }

        String value = sb.toString();

        String temp1, temp2;
        char[] reExp = value.toCharArray();

        for (int i = 0; i < reExp.length; i++) {
            if (isNumeric(reExp[i])) {
                StringBuilder build = new StringBuilder();
                do {
                    build.append(reExp[i++]);

                } while (i < reExp.length && isNumeric(reExp[i]));
                String number = build.toString();

                //수식이 포함되있을 경우 수식을 삭제하고 Math 함수로 계산해서 결과값을 stack에 push
                if (number.contains("b")) {
                    number = number.replace("b", "");
                    number = String.valueOf(Math.cbrt(Double.parseDouble(number)));
                } else if (number.contains("z")) {
                    number = number.replace("z", "");
                    number = String.valueOf((Math.log(Double.parseDouble(number)) / Math.log(2)));
                } else if (number.contains("log")) {
                    number = number.replace("log", "");
                    number = String.valueOf(Math.log10(Double.parseDouble(number)));

                } else if (number.contains("ln")) {
                    number = number.replace("ln", "");
                    number = String.valueOf(Math.log(Double.parseDouble(number)));

                } else if (number.contains("sin")) {
                    number = number.replace("sin", "");
                    number = String.valueOf(Math.sin(Double.parseDouble(number)));

                } else if (number.contains("cos")) {
                    number = number.replace("cos", "");
                    number = String.valueOf(Math.cos(Double.parseDouble(number)));

                } else if (number.contains("tan")) {
                    number = number.replace("tan", "");
                    number = String.valueOf(Math.tan(Double.parseDouble(number)));

                } else if (number.contains("ain")) {
                    number = number.replace("ain", "");
                    number = String.valueOf(Math.asin(Double.parseDouble(number)));

                } else if (number.contains("aos")) {
                    number = number.replace("aos", "");
                    number = String.valueOf(Math.acos(Double.parseDouble(number)));

                } else if (number.contains("aan")) {
                    number = number.replace("aan", "");
                    number = String.valueOf(Math.atan(Double.parseDouble(number)));

                } else if (number.contains("hin")) {
                    number = number.replace("hin", "");
                    number = String.valueOf(Math.sinh(Double.parseDouble(number)));

                } else if (number.contains("hos")) {
                    number = number.replace("hos", "");
                    number = String.valueOf(Math.cosh(Double.parseDouble(number)));

                } else if (number.contains("han")) {
                    number = number.replace("han", "");
                    number = String.valueOf(Math.tanh(Double.parseDouble(number)));

                } else if (number.contains("xin")) {
                    number = number.replace("xin", "");
                    number = String.valueOf(Math.log(Double.parseDouble(number) + Math.sqrt(Double.parseDouble(number) * Double.parseDouble(number) + 1)));

                } else if (number.contains("xos")) {
                    number = number.replace("xos", "");
                    number = String.valueOf(Math.log(Double.parseDouble(number) + Math.sqrt(Double.parseDouble(number) * Double.parseDouble(number) - 1)));
                    number = String.valueOf(Math.cos(Double.parseDouble(number)));

                } else if (number.contains("xan")) {
                    number = number.replace("xan", "");
                    number = String.valueOf((Math.log(1 + Double.parseDouble(number)) - Math.log(1 - Double.parseDouble(number))) / 2);

                } else if (number.equals("π")) {
                    number = String.valueOf(Math.PI);

                } else if (number.equals("e")) {
                    number = String.valueOf(Math.E);

                } else if (number.contains("√")) {
                    double squareRoot = Math.sqrt(Double.parseDouble(number.replace("√", "")));
                    number = String.valueOf(squareRoot);

                } else if (number.contains("!")) {
                    number = number.replace("!", "");
                    double factorial = 1;
                    for (int k = 1; k <= Integer.parseInt(number); k++) {
                        factorial = factorial * k;
                    }
                    number = String.valueOf(factorial);
                } else if (number.contains("y")) {
                    number = number.replace("y", "");
                    double powTwo = Math.pow(Double.parseDouble(number), 2);
                    number = String.valueOf(powTwo);
                } else if (number.contains("k")) {
                    number = number.replace("k", "");
                    double powTwo = Math.pow(Double.parseDouble(number), 3);
                    number = String.valueOf(powTwo);
                } else if (number.contains("v")) {
                    number = number.replace("v", "");
                    double powTwo = Math.pow(Double.parseDouble(number), -1);
                    number = String.valueOf(powTwo);
                }
                //Math 결과가  NaN(알수 없음)일 경우 무한대로 표기
                if (number.equals("NaN")) {
                    return "Infinity";
                }
                L.i(number);
                stack.push(number);
                i--;

            // '+' 연산자가 나왔을 경우 연산식 수행
            } else if (reExp[i] == '+' && isOperator(reExp[i])) {

                temp2 = String.valueOf(stack.pop());
                temp1 = String.valueOf(stack.pop());
                // 숫자에 '%'기호가 포함되어있으면 0.01 곱하여 소수점으로 반환해서 계산
                if (temp1.contains("%")) {
                    temp1 = temp1.replace("%", "");
                    double result = Double.parseDouble(temp1) * 0.01 + Double.parseDouble(temp2);
                    stack.push(result);
                } else if (temp2.contains("%")) {
                    temp2 = temp2.replace("%", "");
                    double number1 = Double.parseDouble(temp1);
                    double result = number1 + (number1 * Double.parseDouble(temp2) * 0.01);
                    stack.push(result);
                } else {
                    stack.push(Double.parseDouble(temp1) + Double.parseDouble(temp2));
                }

            // '×' 연산자가 나왔을 경우 연산식 수행
            } else if (reExp[i] == '×' && isOperator(reExp[i])) {
                temp2 = String.valueOf(stack.pop());
                temp1 = String.valueOf(stack.pop());
                if (temp1.contains("%")) {
                    temp1 = temp1.replace("%", "");
                    double result = Double.parseDouble(temp2) * (Double.parseDouble(temp1) * 0.01);
                    stack.push(result);
                } else if (temp2.contains("%")) {
                    temp2 = temp2.replace("%", "");
                    double result = Double.parseDouble(temp1) * (Double.parseDouble(temp2) * 0.01);
                    stack.push(result);
                } else {
                    stack.push(Double.parseDouble(temp1) * Double.parseDouble(temp2));
                }

            // '-' 연산자가 나왔을 경우 연산식 수행
            } else if (reExp[i] == '-' && isOperator(reExp[i])) {
                temp2 = String.valueOf(stack.pop());
                temp1 = String.valueOf(stack.pop());
                if (temp1.contains("%")) {
                    temp1 = temp1.replace("%", "");
                    double result = Double.parseDouble(temp1) * 0.01 - Double.parseDouble(temp2);
                    stack.push(result);
                } else if (temp2.contains("%")) {
                    temp2 = temp2.replace("%", "");
                    double number1 = Double.parseDouble(temp1);
                    double result = number1 - (number1 * Double.parseDouble(temp2) * 0.01);

                    stack.push(result);
                } else {
                    stack.push(Double.parseDouble(temp1) - Double.parseDouble(temp2));
                }

            // '÷' 연산자가 나왔을 경우 연산식 수행
            } else if (reExp[i] == '÷' && isOperator(reExp[i])) {
                temp2 = String.valueOf(stack.pop());
                temp1 = String.valueOf(stack.pop());
                if (temp1.contains("%")) {
                    temp1 = temp1.replace("%", "");
                    double result = (Double.parseDouble(temp1) * 0.01) / Double.parseDouble(temp2);
                    stack.push(result);
                } else if (temp2.contains("%")) {
                    temp2 = temp2.replace("%", "");
                    double result = Double.parseDouble(temp1) / (Double.parseDouble(temp2) * 0.01);
                    stack.push(result);
                } else {
                    stack.push(Double.parseDouble(temp1) / Double.parseDouble(temp2));
                }
            }
        }

        try {
            String result = stack.pop().toString().replace(",", "");

            //정수부분 자리 구하기
            int number = 0;
            for (int i = 0; i < result.length(); i++) {
                if (result.charAt(i) == '.') {
                    number = i;
                    break;
                }
            }
            // int pointCount = 결과 소수점 자리 수 표현 범위
            if (pointCount > 0) {
                //정수부분
                String mok = result.substring(0, number);

                //나머지
                double divide = Double.parseDouble(result) % Double.parseDouble(mok);

                //나머지가 0이면 소수점 없앰
                if (divide == 0) {
                    result = String.format("%.0f", Double.parseDouble(result));
                }
            }
            return result;
        } catch (EmptyStackException e) {}
        return null;
    }
}
