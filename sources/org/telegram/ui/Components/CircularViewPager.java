package org.telegram.ui.Components;

import android.content.Context;
import android.util.AttributeSet;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class CircularViewPager extends ViewPager {
    /* access modifiers changed from: private */
    public Adapter adapter;

    public CircularViewPager(Context context) {
        super(context);
        addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int scrollState;

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == CircularViewPager.this.getCurrentItem() && positionOffset == 0.0f && this.scrollState == 1) {
                    checkCurrentItem();
                }
            }

            public void onPageSelected(int position) {
            }

            public void onPageScrollStateChanged(int state) {
                if (state == 0) {
                    checkCurrentItem();
                }
                this.scrollState = state;
            }

            private void checkCurrentItem() {
                if (CircularViewPager.this.adapter != null) {
                    int position = CircularViewPager.this.getCurrentItem();
                    int newPosition = CircularViewPager.this.adapter.getExtraCount() + CircularViewPager.this.adapter.getRealPosition(position);
                    if (position != newPosition) {
                        CircularViewPager.this.setCurrentItem(newPosition, false);
                    }
                }
            }
        });
    }

    public CircularViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int scrollState;

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == CircularViewPager.this.getCurrentItem() && positionOffset == 0.0f && this.scrollState == 1) {
                    checkCurrentItem();
                }
            }

            public void onPageSelected(int position) {
            }

            public void onPageScrollStateChanged(int state) {
                if (state == 0) {
                    checkCurrentItem();
                }
                this.scrollState = state;
            }

            private void checkCurrentItem() {
                if (CircularViewPager.this.adapter != null) {
                    int position = CircularViewPager.this.getCurrentItem();
                    int newPosition = CircularViewPager.this.adapter.getExtraCount() + CircularViewPager.this.adapter.getRealPosition(position);
                    if (position != newPosition) {
                        CircularViewPager.this.setCurrentItem(newPosition, false);
                    }
                }
            }
        });
    }

    @Deprecated
    public void setAdapter(PagerAdapter adapter2) {
        if (adapter2 instanceof Adapter) {
            setAdapter((Adapter) adapter2);
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

        public int getRealPosition(int adapterPosition) {
            int count = getCount();
            int extraCount = getExtraCount();
            if (adapterPosition < extraCount) {
                return ((count - (extraCount * 2)) - ((extraCount - adapterPosition) - 1)) - 1;
            }
            if (adapterPosition >= count - extraCount) {
                return adapterPosition - (count - extraCount);
            }
            return adapterPosition - extraCount;
        }
    }
}
