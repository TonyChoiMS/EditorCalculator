
 package com.oneshot.calculator.editorcalculator;

import android.content.res.Resources;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;

 /**
 * Util Class
 */
public class Util {
	// dp를 px값으로 환산
	public static int dpToPx(int dp) {
		DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
		return (int) (dp * metrics.density + 0.5f);
	}

    //px을 dp값으로 환산
	public static int pxToDp(int px) {
		DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
		return (int) (px / metrics.density + 0.5f);
	}

	//String 값에 html 태그를 허용
	@SuppressWarnings("deprecation")
	public static Spanned fromHtml(String html) {
		Spanned result;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
		} else {
			result = Html.fromHtml(html);
		}
		return result;
	}

	//음성인식을 받아올 경우 숫자가 한글로 넘어오는 경우가 생김
	public static String reformTextToNumber(String text) {
		String result = text;
		if (result.contains("일")) {
			result = result.replaceAll("일", "1");
		}
		if (result.contains("이")) {
			result = result.replaceAll("이", "2");
		}
		if (result.contains("삼")) {
			result = result.replaceAll("삼", "3");
		}
		if (result.contains("사")) {
			result = result.replaceAll("사", "4");
		}
		if (result.contains("오")) {
			result = result.replaceAll("오", "5");
		}
		if (result.contains("육")) {
			result = result.replaceAll("육", "6");
		}
		if (result.contains("칠")) {
			result = result.replaceAll("칠", "7");
		}
		if (result.contains("팔")) {
			result = result.replaceAll("팔", "8");
		}
		if (result.contains("구")) {
			result = result.replaceAll("구", "9");
		}
		if (result.contains("십")) {
			result = result.replaceAll("십", "10");
		}

		return result;
	}

	// 음성인식을 받아올 경우 한글을 사칙연산으로 변환
	public static String reformTextToCalculus(String text) {
		String result = text;
		//한글로된 연산자를 기호로 변경
		if (result.contains("더하기")) {
			result = result.replaceAll("더하기", "+");
		}
		if (result.contains("빼기")) {
			result = result.replaceAll("빼기", "-");
		}
		if (result.contains("곱하기")) {
			result = result.replaceAll("곱하기", "×");
		}
		if (result.contains("나누기")) {
			result = result.replaceAll("나누기", "÷");
		}
		if (result.contains("는")) {
			result = result.replaceAll("는" , "");
		}
		result = result.replaceAll(" ", "");
		return result;
	}
}
