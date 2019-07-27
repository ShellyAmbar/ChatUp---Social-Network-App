package com.shelly.ambar.chatup.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

public class NonSwipeableViewPager extends ViewPager {

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    private void SetMyScroller(){

        try {

            Class <?> viewPager= ViewPager.class;
            Field scroller=viewPager.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            scroller.set(this,new MyController(getContext()));

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public NonSwipeableViewPager(@NonNull Context context) {
        super(context);
        SetMyScroller();
    }

    public NonSwipeableViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        SetMyScroller();
    }

    private class MyController extends Scroller {
        public MyController(Context context) {
            super(context, new DecelerateInterpolator());

        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, 400);

        }
    }
}
