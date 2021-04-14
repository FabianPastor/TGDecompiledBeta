package org.telegram.ui.Components;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class CircularViewPager extends ViewPager {
    /* access modifiers changed from: private */
    public Adapter adapter;

    public CircularViewPager(Context context) {
        super(context);
        addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

    @Deprecated
    public void setAdapter(PagerAdapter pagerAdapter) {
        if (pagerAdapter instanceof Adapter) {
            setAdapter((Adapter) pagerAdapter);
            return;
        }
        throw new IllegalArgumentException();
    }

    public void setAdapter(Adapter adapter2) {
        this.adapter = adapter2;
        super.setAdapter(adapter2);
        if (adapter2 != null) {
            setCurrentItem(adapter2.getExtraCount(), false);
        }
    }

    public static abstract class Adapter extends PagerAdapter {
        public abstract int getExtraCount();

        public int getRealPosition(int i) {
            int count = getCount();
            int extraCount = getExtraCount();
            if (i < extraCount) {
                return ((count - (extraCount * 2)) - ((extraCount - i) - 1)) - 1;
            }
            int i2 = count - extraCount;
            return i >= i2 ? i - i2 : i - extraCount;
        }
    }
}
