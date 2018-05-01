package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import org.telegram.messenger.AndroidUtilities;

public class PagerSlidingTabStrip extends HorizontalScrollView {
    private int currentPosition = 0;
    private float currentPositionOffset = 0.0f;
    private LayoutParams defaultTabLayoutParams;
    public OnPageChangeListener delegatePageListener;
    private int dividerPadding = AndroidUtilities.dp(12.0f);
    private int indicatorColor = -10066330;
    private int indicatorHeight = AndroidUtilities.dp(8.0f);
    private int lastScrollX = 0;
    private final PageListener pageListener = new PageListener();
    private ViewPager pager;
    private Paint rectPaint;
    private int scrollOffset = AndroidUtilities.dp(52.0f);
    private boolean shouldExpand = false;
    private int tabCount;
    private int tabPadding = AndroidUtilities.dp(24.0f);
    private LinearLayout tabsContainer;
    private int underlineColor = 436207616;
    private int underlineHeight = AndroidUtilities.dp(2.0f);

    /* renamed from: org.telegram.ui.Components.PagerSlidingTabStrip$1 */
    class C11961 implements OnGlobalLayoutListener {
        C11961() {
        }

        public void onGlobalLayout() {
            PagerSlidingTabStrip.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            PagerSlidingTabStrip.this.currentPosition = PagerSlidingTabStrip.this.pager.getCurrentItem();
            PagerSlidingTabStrip.this.scrollToChild(PagerSlidingTabStrip.this.currentPosition, 0);
        }
    }

    /* renamed from: org.telegram.ui.Components.PagerSlidingTabStrip$4 */
    class C11994 implements Runnable {
        C11994() {
        }

        public void run() {
            PagerSlidingTabStrip.this.notifyDataSetChanged();
        }
    }

    public interface IconTabProvider {
        boolean canScrollToTab(int i);

        void customOnDraw(Canvas canvas, int i);

        Drawable getPageIconDrawable(int i);
    }

    private class PageListener implements OnPageChangeListener {
        private PageListener() {
        }

        public void onPageScrolled(int i, float f, int i2) {
            PagerSlidingTabStrip.this.currentPosition = i;
            PagerSlidingTabStrip.this.currentPositionOffset = f;
            PagerSlidingTabStrip.this.scrollToChild(i, (int) (((float) PagerSlidingTabStrip.this.tabsContainer.getChildAt(i).getWidth()) * f));
            PagerSlidingTabStrip.this.invalidate();
            if (PagerSlidingTabStrip.this.delegatePageListener != null) {
                PagerSlidingTabStrip.this.delegatePageListener.onPageScrolled(i, f, i2);
            }
        }

        public void onPageScrollStateChanged(int i) {
            if (i == 0) {
                PagerSlidingTabStrip.this.scrollToChild(PagerSlidingTabStrip.this.pager.getCurrentItem(), 0);
            }
            if (PagerSlidingTabStrip.this.delegatePageListener != null) {
                PagerSlidingTabStrip.this.delegatePageListener.onPageScrollStateChanged(i);
            }
        }

        public void onPageSelected(int i) {
            if (PagerSlidingTabStrip.this.delegatePageListener != null) {
                PagerSlidingTabStrip.this.delegatePageListener.onPageSelected(i);
            }
            int i2 = 0;
            while (i2 < PagerSlidingTabStrip.this.tabsContainer.getChildCount()) {
                PagerSlidingTabStrip.this.tabsContainer.getChildAt(i2).setSelected(i2 == i);
                i2++;
            }
        }
    }

    public PagerSlidingTabStrip(Context context) {
        super(context);
        setFillViewport(true);
        setWillNotDraw(false);
        this.tabsContainer = new LinearLayout(context);
        this.tabsContainer.setOrientation(0);
        this.tabsContainer.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        addView(this.tabsContainer);
        this.rectPaint = new Paint();
        this.rectPaint.setAntiAlias(true);
        this.rectPaint.setStyle(Style.FILL);
        this.defaultTabLayoutParams = new LayoutParams(-2, -1);
    }

    public void setViewPager(ViewPager viewPager) {
        this.pager = viewPager;
        if (viewPager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        viewPager.setOnPageChangeListener(this.pageListener);
        notifyDataSetChanged();
    }

    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        this.delegatePageListener = onPageChangeListener;
    }

    public void notifyDataSetChanged() {
        this.tabsContainer.removeAllViews();
        this.tabCount = this.pager.getAdapter().getCount();
        for (int i = 0; i < this.tabCount; i++) {
            if (this.pager.getAdapter() instanceof IconTabProvider) {
                addIconTab(i, ((IconTabProvider) this.pager.getAdapter()).getPageIconDrawable(i));
            }
        }
        updateTabStyles();
        getViewTreeObserver().addOnGlobalLayoutListener(new C11961());
    }

    public View getTab(int i) {
        if (i >= 0) {
            if (i < this.tabsContainer.getChildCount()) {
                return this.tabsContainer.getChildAt(i);
            }
        }
        return 0;
    }

    private void addIconTab(final int i, Drawable drawable) {
        View c11972 = new ImageView(getContext()) {
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (PagerSlidingTabStrip.this.pager.getAdapter() instanceof IconTabProvider) {
                    ((IconTabProvider) PagerSlidingTabStrip.this.pager.getAdapter()).customOnDraw(canvas, i);
                }
            }
        };
        boolean z = true;
        c11972.setFocusable(true);
        c11972.setImageDrawable(drawable);
        c11972.setScaleType(ScaleType.CENTER);
        c11972.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if ((PagerSlidingTabStrip.this.pager.getAdapter() instanceof IconTabProvider) == null || ((IconTabProvider) PagerSlidingTabStrip.this.pager.getAdapter()).canScrollToTab(i) != null) {
                    PagerSlidingTabStrip.this.pager.setCurrentItem(i);
                }
            }
        });
        this.tabsContainer.addView(c11972);
        if (i != this.currentPosition) {
            z = false;
        }
        c11972.setSelected(z);
    }

    private void updateTabStyles() {
        for (int i = 0; i < this.tabCount; i++) {
            View childAt = this.tabsContainer.getChildAt(i);
            childAt.setLayoutParams(this.defaultTabLayoutParams);
            if (this.shouldExpand) {
                childAt.setPadding(0, 0, 0, 0);
                childAt.setLayoutParams(new LayoutParams(-1, -1, 1.0f));
            } else {
                childAt.setPadding(this.tabPadding, 0, this.tabPadding, 0);
            }
        }
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.shouldExpand) {
            if (MeasureSpec.getMode(i) != 0) {
                this.tabsContainer.measure(getMeasuredWidth() | NUM, i2);
            }
        }
    }

    private void scrollToChild(int i, int i2) {
        if (this.tabCount != 0) {
            int left = this.tabsContainer.getChildAt(i).getLeft() + i2;
            if (i > 0 || i2 > 0) {
                left -= this.scrollOffset;
            }
            if (left != this.lastScrollX) {
                this.lastScrollX = left;
                scrollTo(left, 0);
            }
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isInEditMode()) {
            if (this.tabCount != 0) {
                float f;
                float f2;
                int height = getHeight();
                this.rectPaint.setColor(this.underlineColor);
                float f3 = (float) height;
                canvas.drawRect(0.0f, (float) (height - this.underlineHeight), (float) this.tabsContainer.getWidth(), f3, this.rectPaint);
                View childAt = this.tabsContainer.getChildAt(this.currentPosition);
                float left = (float) childAt.getLeft();
                float right = (float) childAt.getRight();
                if (this.currentPositionOffset <= 0.0f || this.currentPosition >= this.tabCount - 1) {
                    f = right;
                    f2 = left;
                } else {
                    View childAt2 = this.tabsContainer.getChildAt(this.currentPosition + 1);
                    float left2 = (float) childAt2.getLeft();
                    f = (this.currentPositionOffset * ((float) childAt2.getRight())) + ((1.0f - this.currentPositionOffset) * right);
                    f2 = (this.currentPositionOffset * left2) + ((1.0f - this.currentPositionOffset) * left);
                }
                this.rectPaint.setColor(this.indicatorColor);
                canvas.drawRect(f2, (float) (height - this.indicatorHeight), f, f3, this.rectPaint);
            }
        }
    }

    public void onSizeChanged(int i, int i2, int i3, int i4) {
        if (this.shouldExpand == 0) {
            post(new C11994());
        }
    }

    public void setIndicatorColor(int i) {
        this.indicatorColor = i;
        invalidate();
    }

    public void setIndicatorColorResource(int i) {
        this.indicatorColor = getResources().getColor(i);
        invalidate();
    }

    public int getIndicatorColor() {
        return this.indicatorColor;
    }

    public void setIndicatorHeight(int i) {
        this.indicatorHeight = i;
        invalidate();
    }

    public int getIndicatorHeight() {
        return this.indicatorHeight;
    }

    public void setUnderlineColor(int i) {
        this.underlineColor = i;
        invalidate();
    }

    public void setUnderlineColorResource(int i) {
        this.underlineColor = getResources().getColor(i);
        invalidate();
    }

    public int getUnderlineColor() {
        return this.underlineColor;
    }

    public void setUnderlineHeight(int i) {
        this.underlineHeight = i;
        invalidate();
    }

    public int getUnderlineHeight() {
        return this.underlineHeight;
    }

    public void setDividerPadding(int i) {
        this.dividerPadding = i;
        invalidate();
    }

    public int getDividerPadding() {
        return this.dividerPadding;
    }

    public void setScrollOffset(int i) {
        this.scrollOffset = i;
        invalidate();
    }

    public int getScrollOffset() {
        return this.scrollOffset;
    }

    public void setShouldExpand(boolean z) {
        this.shouldExpand = z;
        this.tabsContainer.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        updateTabStyles();
        requestLayout();
    }

    public boolean getShouldExpand() {
        return this.shouldExpand;
    }

    public void setTabPaddingLeftRight(int i) {
        this.tabPadding = i;
        updateTabStyles();
    }

    public int getTabPaddingLeftRight() {
        return this.tabPadding;
    }
}
