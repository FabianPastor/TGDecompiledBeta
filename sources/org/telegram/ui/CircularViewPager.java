package org.telegram.ui;

import android.content.Context;
import android.util.AttributeSet;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

public class CircularViewPager extends ViewPager {
    private Adapter adapter;

    public static abstract class Adapter extends PagerAdapter {
        public abstract int getExtraCount();

        public int getRealPosition(int i) {
            int count = getCount();
            int extraCount = getExtraCount();
            if (i < extraCount) {
                return ((count - (extraCount * 2)) - ((extraCount - i) - 1)) - 1;
            }
            count -= extraCount;
            return i >= count ? i - count : i - extraCount;
        }
    }

    public CircularViewPager(Context context) {
        super(context);
        addOnPageChangeListener(new OnPageChangeListener() {
            private int scrollState;

            public void onPageSelected(int i) {
            }

            public void onPageScrolled(int i, float f, int i2) {
                if (i == CircularViewPager.this.getCurrentItem() && f == 0.0f && this.scrollState == 1) {
                    checkCurrentItem();
                }
            }

            public void onPageScrollStateChanged(int i) {
                if (i == 0) {
                    checkCurrentItem();
                }
                this.scrollState = i;
            }

            private void checkCurrentItem() {
                if (CircularViewPager.this.adapter != null) {
                    int currentItem = CircularViewPager.this.getCurrentItem();
                    int extraCount = CircularViewPager.this.adapter.getExtraCount() + CircularViewPager.this.adapter.getRealPosition(currentItem);
                    if (currentItem != extraCount) {
                        CircularViewPager.this.setCurrentItem(extraCount, false);
                    }
                }
            }
        });
    }

    public CircularViewPager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        addOnPageChangeListener(/* anonymous class already generated */);
    }

    @Deprecated
    public void setAdapter(PagerAdapter pagerAdapter) {
        if (pagerAdapter instanceof Adapter) {
            setAdapter((Adapter) pagerAdapter);
            return;
        }
        throw new IllegalArgumentException();
    }

    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
        super.setAdapter(adapter);
        setCurrentItem(adapter.getExtraCount(), false);
    }
}
