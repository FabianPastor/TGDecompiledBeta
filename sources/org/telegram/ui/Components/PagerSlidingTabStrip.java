package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.viewpager.widget.ViewPager;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class PagerSlidingTabStrip extends HorizontalScrollView {
    /* access modifiers changed from: private */
    public int currentPosition = 0;
    /* access modifiers changed from: private */
    public float currentPositionOffset = 0.0f;
    private LinearLayout.LayoutParams defaultTabLayoutParams;
    public ViewPager.OnPageChangeListener delegatePageListener;
    private int dividerPadding = AndroidUtilities.dp(12.0f);
    private int indicatorColor = -10066330;
    private int indicatorHeight = AndroidUtilities.dp(8.0f);
    private int lastScrollX = 0;
    private final PageListener pageListener = new PageListener();
    /* access modifiers changed from: private */
    public ViewPager pager;
    private Paint rectPaint;
    private Theme.ResourcesProvider resourcesProvider;
    private int scrollOffset = AndroidUtilities.dp(52.0f);
    private boolean shouldExpand = false;
    private int tabCount;
    private int tabPadding = AndroidUtilities.dp(24.0f);
    /* access modifiers changed from: private */
    public LinearLayout tabsContainer;
    private int underlineColor = NUM;
    private int underlineHeight = AndroidUtilities.dp(2.0f);

    public interface IconTabProvider {
        boolean canScrollToTab(int i);

        void customOnDraw(Canvas canvas, int i);

        Drawable getPageIconDrawable(int i);
    }

    public PagerSlidingTabStrip(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.resourcesProvider = resourcesProvider2;
        setFillViewport(true);
        setWillNotDraw(false);
        LinearLayout linearLayout = new LinearLayout(context);
        this.tabsContainer = linearLayout;
        linearLayout.setOrientation(0);
        this.tabsContainer.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        addView(this.tabsContainer);
        Paint paint = new Paint();
        this.rectPaint = paint;
        paint.setAntiAlias(true);
        this.rectPaint.setStyle(Paint.Style.FILL);
        this.defaultTabLayoutParams = new LinearLayout.LayoutParams(-2, -1);
    }

    public void setViewPager(ViewPager viewPager) {
        this.pager = viewPager;
        if (viewPager.getAdapter() != null) {
            viewPager.setOnPageChangeListener(this.pageListener);
            notifyDataSetChanged();
            return;
        }
        throw new IllegalStateException("ViewPager does not have adapter instance.");
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        this.delegatePageListener = onPageChangeListener;
    }

    public void notifyDataSetChanged() {
        this.tabsContainer.removeAllViews();
        this.tabCount = this.pager.getAdapter().getCount();
        for (int i = 0; i < this.tabCount; i++) {
            if (this.pager.getAdapter() instanceof IconTabProvider) {
                addIconTab(i, ((IconTabProvider) this.pager.getAdapter()).getPageIconDrawable(i), this.pager.getAdapter().getPageTitle(i));
            }
        }
        updateTabStyles();
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                PagerSlidingTabStrip.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                PagerSlidingTabStrip pagerSlidingTabStrip = PagerSlidingTabStrip.this;
                int unused = pagerSlidingTabStrip.currentPosition = pagerSlidingTabStrip.pager.getCurrentItem();
                PagerSlidingTabStrip pagerSlidingTabStrip2 = PagerSlidingTabStrip.this;
                pagerSlidingTabStrip2.scrollToChild(pagerSlidingTabStrip2.currentPosition, 0);
            }
        });
    }

    public View getTab(int i) {
        if (i < 0 || i >= this.tabsContainer.getChildCount()) {
            return null;
        }
        return this.tabsContainer.getChildAt(i);
    }

    private void addIconTab(final int i, Drawable drawable, CharSequence charSequence) {
        AnonymousClass2 r0 = new ImageView(getContext()) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (PagerSlidingTabStrip.this.pager.getAdapter() instanceof IconTabProvider) {
                    ((IconTabProvider) PagerSlidingTabStrip.this.pager.getAdapter()).customOnDraw(canvas, i);
                }
            }

            public void setSelected(boolean z) {
                super.setSelected(z);
                Drawable background = getBackground();
                if (Build.VERSION.SDK_INT >= 21 && background != null) {
                    int access$400 = PagerSlidingTabStrip.this.getThemedColor(z ? "chat_emojiPanelIconSelected" : "chat_emojiBottomPanelIcon");
                    Theme.setSelectorDrawableColor(background, Color.argb(30, Color.red(access$400), Color.green(access$400), Color.blue(access$400)), true);
                }
            }
        };
        boolean z = true;
        r0.setFocusable(true);
        if (Build.VERSION.SDK_INT >= 21) {
            RippleDrawable rippleDrawable = (RippleDrawable) Theme.createSelectorDrawable(getThemedColor("chat_emojiBottomPanelIcon"), 1, AndroidUtilities.dp(18.0f));
            Theme.setRippleDrawableForceSoftware(rippleDrawable);
            r0.setBackground(rippleDrawable);
        }
        r0.setImageDrawable(drawable);
        r0.setScaleType(ImageView.ScaleType.CENTER);
        r0.setOnClickListener(new PagerSlidingTabStrip$$ExternalSyntheticLambda0(this, i));
        this.tabsContainer.addView(r0);
        if (i != this.currentPosition) {
            z = false;
        }
        r0.setSelected(z);
        r0.setContentDescription(charSequence);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addIconTab$0(int i, View view) {
        if (!(this.pager.getAdapter() instanceof IconTabProvider) || ((IconTabProvider) this.pager.getAdapter()).canScrollToTab(i)) {
            this.pager.setCurrentItem(i, false);
        }
    }

    private void updateTabStyles() {
        for (int i = 0; i < this.tabCount; i++) {
            View childAt = this.tabsContainer.getChildAt(i);
            childAt.setLayoutParams(this.defaultTabLayoutParams);
            if (this.shouldExpand) {
                childAt.setPadding(0, 0, 0, 0);
                childAt.setLayoutParams(new LinearLayout.LayoutParams(-1, -1, 1.0f));
            } else {
                int i2 = this.tabPadding;
                childAt.setPadding(i2, 0, i2, 0);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.shouldExpand && View.MeasureSpec.getMode(i) != 0) {
            this.tabsContainer.measure(getMeasuredWidth() | NUM, i2);
        }
    }

    /* access modifiers changed from: private */
    public void scrollToChild(int i, int i2) {
        View childAt;
        if (this.tabCount != 0 && (childAt = this.tabsContainer.getChildAt(i)) != null) {
            int left = childAt.getLeft() + i2;
            if (i > 0 || i2 > 0) {
                left -= this.scrollOffset;
            }
            if (left != this.lastScrollX) {
                this.lastScrollX = left;
                scrollTo(left, 0);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int i;
        super.onDraw(canvas);
        if (!isInEditMode() && this.tabCount != 0) {
            int height = getHeight();
            if (this.underlineHeight != 0) {
                this.rectPaint.setColor(this.underlineColor);
                canvas.drawRect(0.0f, (float) (height - this.underlineHeight), (float) this.tabsContainer.getWidth(), (float) height, this.rectPaint);
            }
            View childAt = this.tabsContainer.getChildAt(this.currentPosition);
            float left = (float) childAt.getLeft();
            float right = (float) childAt.getRight();
            if (this.currentPositionOffset > 0.0f && (i = this.currentPosition) < this.tabCount - 1) {
                View childAt2 = this.tabsContainer.getChildAt(i + 1);
                float f = this.currentPositionOffset;
                left = (((float) childAt2.getLeft()) * f) + ((1.0f - f) * left);
                right = (((float) childAt2.getRight()) * f) + ((1.0f - f) * right);
            }
            float f2 = right;
            float f3 = left;
            if (this.indicatorHeight != 0) {
                this.rectPaint.setColor(this.indicatorColor);
                canvas.drawRect(f3, (float) (height - this.indicatorHeight), f2, (float) height, this.rectPaint);
            }
        }
    }

    private class PageListener implements ViewPager.OnPageChangeListener {
        private PageListener() {
        }

        public void onPageScrolled(int i, float f, int i2) {
            int unused = PagerSlidingTabStrip.this.currentPosition = i;
            float unused2 = PagerSlidingTabStrip.this.currentPositionOffset = f;
            if (PagerSlidingTabStrip.this.tabsContainer.getChildAt(i) != null) {
                PagerSlidingTabStrip pagerSlidingTabStrip = PagerSlidingTabStrip.this;
                pagerSlidingTabStrip.scrollToChild(i, (int) (((float) pagerSlidingTabStrip.tabsContainer.getChildAt(i).getWidth()) * f));
                PagerSlidingTabStrip.this.invalidate();
                ViewPager.OnPageChangeListener onPageChangeListener = PagerSlidingTabStrip.this.delegatePageListener;
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrolled(i, f, i2);
                }
            }
        }

        public void onPageScrollStateChanged(int i) {
            if (i == 0) {
                PagerSlidingTabStrip pagerSlidingTabStrip = PagerSlidingTabStrip.this;
                pagerSlidingTabStrip.scrollToChild(pagerSlidingTabStrip.pager.getCurrentItem(), 0);
            }
            ViewPager.OnPageChangeListener onPageChangeListener = PagerSlidingTabStrip.this.delegatePageListener;
            if (onPageChangeListener != null) {
                onPageChangeListener.onPageScrollStateChanged(i);
            }
        }

        public void onPageSelected(int i) {
            ViewPager.OnPageChangeListener onPageChangeListener = PagerSlidingTabStrip.this.delegatePageListener;
            if (onPageChangeListener != null) {
                onPageChangeListener.onPageSelected(i);
            }
            int i2 = 0;
            while (i2 < PagerSlidingTabStrip.this.tabsContainer.getChildCount()) {
                PagerSlidingTabStrip.this.tabsContainer.getChildAt(i2).setSelected(i2 == i);
                i2++;
            }
        }
    }

    /* access modifiers changed from: private */
    public int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }

    public void onSizeChanged(int i, int i2, int i3, int i4) {
        if (!this.shouldExpand) {
            post(new PagerSlidingTabStrip$$ExternalSyntheticLambda1(this));
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
