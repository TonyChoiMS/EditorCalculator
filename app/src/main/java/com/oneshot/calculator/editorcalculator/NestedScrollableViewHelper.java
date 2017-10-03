package com.oneshot.calculator.editorcalculator;

import android.view.View;

import com.oneshot.calculator.editorcalculator.widget.slidinglayout.ScrollableViewHelper;

/**
 * Created by Tony Choi on 2017-01-31.
 * Viewpager와 SlidingPanelUpLayout의 제스쳐가 겹치지 않게 설정하는 클래스
 */

public class NestedScrollableViewHelper extends ScrollableViewHelper {
    View mScrollableView;

    public NestedScrollableViewHelper(View ScrollableView) {
        this.mScrollableView = ScrollableView;
    }

    public int getScrollableViewScrollPosition(View scrollableView, boolean isSlidingUp) {
        if (mScrollableView instanceof VerticalViewPager) {
            if (isSlidingUp) {
                return mScrollableView.getScrollY();
            } else {
                VerticalViewPager nsv = (VerticalViewPager) mScrollableView;
                View child = nsv.getChildAt(0);
                return (child.getBottom() - (nsv.getHeight() + nsv.getScrollY()));
            }
        } else {
            return 0;
        }
    }
}