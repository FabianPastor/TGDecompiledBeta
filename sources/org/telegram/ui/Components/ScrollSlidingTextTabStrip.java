package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.SystemClock;
import android.text.Layout;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Keep;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class ScrollSlidingTextTabStrip extends HorizontalScrollView {
    private String activeTextColorKey = "actionBarTabActiveText";
    private int allTextWidth;
    private int animateFromIndicatorWidth;
    private int animateFromIndicaxtorX;
    private int animateIndicatorStartWidth;
    private int animateIndicatorStartX;
    private int animateIndicatorToWidth;
    private int animateIndicatorToX;
    /* access modifiers changed from: private */
    public boolean animatingIndicator;
    private float animationIdicatorProgress;
    /* access modifiers changed from: private */
    public Runnable animationRunnable = new Runnable() {
        public void run() {
            if (ScrollSlidingTextTabStrip.this.animatingIndicator) {
                long elapsedRealtime = SystemClock.elapsedRealtime() - ScrollSlidingTextTabStrip.this.lastAnimationTime;
                if (elapsedRealtime > 17) {
                    elapsedRealtime = 17;
                }
                ScrollSlidingTextTabStrip.access$216(ScrollSlidingTextTabStrip.this, ((float) elapsedRealtime) / 200.0f);
                ScrollSlidingTextTabStrip scrollSlidingTextTabStrip = ScrollSlidingTextTabStrip.this;
                scrollSlidingTextTabStrip.setAnimationIdicatorProgress(scrollSlidingTextTabStrip.interpolator.getInterpolation(ScrollSlidingTextTabStrip.this.animationTime));
                if (ScrollSlidingTextTabStrip.this.animationTime > 1.0f) {
                    float unused = ScrollSlidingTextTabStrip.this.animationTime = 1.0f;
                }
                if (ScrollSlidingTextTabStrip.this.animationTime < 1.0f) {
                    AndroidUtilities.runOnUIThread(ScrollSlidingTextTabStrip.this.animationRunnable);
                    return;
                }
                boolean unused2 = ScrollSlidingTextTabStrip.this.animatingIndicator = false;
                ScrollSlidingTextTabStrip.this.setEnabled(true);
                if (ScrollSlidingTextTabStrip.this.delegate != null) {
                    ScrollSlidingTextTabStrip.this.delegate.onPageScrolled(1.0f);
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public float animationTime;
    private int currentPosition;
    /* access modifiers changed from: private */
    public ScrollSlidingTabStripDelegate delegate;
    private SparseIntArray idToPosition = new SparseIntArray(5);
    private int indicatorWidth;
    private float indicatorWidthAnimationDx;
    private int indicatorX;
    private float indicatorXAnimationDx;
    /* access modifiers changed from: private */
    public CubicBezierInterpolator interpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
    /* access modifiers changed from: private */
    public long lastAnimationTime;
    private SparseIntArray positionToId = new SparseIntArray(5);
    private SparseIntArray positionToWidth = new SparseIntArray(5);
    private int prevLayoutWidth;
    private int previousPosition;
    private int scrollingToChild = -1;
    /* access modifiers changed from: private */
    public int selectedTabId = -1;
    private String selectorColorKey = "actionBarTabSelector";
    private GradientDrawable selectorDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, (int[]) null);
    private int tabCount;
    private String tabLineColorKey = "actionBarTabLine";
    private LinearLayout tabsContainer;
    private String unactiveTextColorKey = "actionBarTabUnactiveText";
    private boolean useSameWidth;

    public interface ScrollSlidingTabStripDelegate {

        /* renamed from: org.telegram.ui.Components.ScrollSlidingTextTabStrip$ScrollSlidingTabStripDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$onSamePageSelected(ScrollSlidingTabStripDelegate scrollSlidingTabStripDelegate) {
            }
        }

        void onPageScrolled(float f);

        void onPageSelected(int i, boolean z);

        void onSamePageSelected();
    }

    static /* synthetic */ float access$216(ScrollSlidingTextTabStrip scrollSlidingTextTabStrip, float f) {
        float f2 = scrollSlidingTextTabStrip.animationTime + f;
        scrollSlidingTextTabStrip.animationTime = f2;
        return f2;
    }

    public ScrollSlidingTextTabStrip(Context context) {
        super(context);
        float dpf2 = AndroidUtilities.dpf2(3.0f);
        this.selectorDrawable.setCornerRadii(new float[]{dpf2, dpf2, dpf2, dpf2, 0.0f, 0.0f, 0.0f, 0.0f});
        this.selectorDrawable.setColor(Theme.getColor(this.tabLineColorKey));
        setFillViewport(true);
        setWillNotDraw(false);
        setHorizontalScrollBarEnabled(false);
        AnonymousClass2 r1 = new LinearLayout(context) {
            public void setAlpha(float f) {
                super.setAlpha(f);
                ScrollSlidingTextTabStrip.this.invalidate();
            }
        };
        this.tabsContainer = r1;
        r1.setOrientation(0);
        this.tabsContainer.setPadding(AndroidUtilities.dp(7.0f), 0, AndroidUtilities.dp(7.0f), 0);
        this.tabsContainer.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        addView(this.tabsContainer);
    }

    public void setDelegate(ScrollSlidingTabStripDelegate scrollSlidingTabStripDelegate) {
        this.delegate = scrollSlidingTabStripDelegate;
    }

    public boolean isAnimatingIndicator() {
        return this.animatingIndicator;
    }

    private void setAnimationProgressInernal(TextView textView, TextView textView2, float f) {
        TextView textView3 = textView;
        TextView textView4 = textView2;
        if (textView3 != null && textView4 != null) {
            int color = Theme.getColor(this.activeTextColorKey);
            int color2 = Theme.getColor(this.unactiveTextColorKey);
            int red = Color.red(color);
            int green = Color.green(color);
            int blue = Color.blue(color);
            int alpha = Color.alpha(color);
            int red2 = Color.red(color2);
            int green2 = Color.green(color2);
            int blue2 = Color.blue(color2);
            int alpha2 = Color.alpha(color2);
            textView4.setTextColor(Color.argb((int) (((float) alpha) + (((float) (alpha2 - alpha)) * f)), (int) (((float) red) + (((float) (red2 - red)) * f)), (int) (((float) green) + (((float) (green2 - green)) * f)), (int) (((float) blue) + (((float) (blue2 - blue)) * f))));
            textView3.setTextColor(Color.argb((int) (((float) alpha2) + (((float) (alpha - alpha2)) * f)), (int) (((float) red2) + (((float) (red - red2)) * f)), (int) (((float) green2) + (((float) (green - green2)) * f)), (int) (((float) blue2) + (((float) (blue - blue2)) * f))));
            int i = this.animateIndicatorStartX;
            this.indicatorX = (int) (((float) i) + (((float) (this.animateIndicatorToX - i)) * f));
            int i2 = this.animateIndicatorStartWidth;
            this.indicatorWidth = (int) (((float) i2) + (((float) (this.animateIndicatorToWidth - i2)) * f));
            invalidate();
        }
    }

    @Keep
    public void setAnimationIdicatorProgress(float f) {
        this.animationIdicatorProgress = f;
        TextView textView = (TextView) this.tabsContainer.getChildAt(this.currentPosition);
        TextView textView2 = (TextView) this.tabsContainer.getChildAt(this.previousPosition);
        if (textView2 != null && textView != null) {
            setAnimationProgressInernal(textView, textView2, f);
            if (f >= 1.0f) {
                textView2.setTag(this.unactiveTextColorKey);
                textView.setTag(this.activeTextColorKey);
            }
            ScrollSlidingTabStripDelegate scrollSlidingTabStripDelegate = this.delegate;
            if (scrollSlidingTabStripDelegate != null) {
                scrollSlidingTabStripDelegate.onPageScrolled(f);
            }
        }
    }

    public void setUseSameWidth(boolean z) {
        this.useSameWidth = z;
    }

    public Drawable getSelectorDrawable() {
        return this.selectorDrawable;
    }

    public ViewGroup getTabsContainer() {
        return this.tabsContainer;
    }

    @Keep
    public float getAnimationIdicatorProgress() {
        return this.animationIdicatorProgress;
    }

    public int getNextPageId(boolean z) {
        return this.positionToId.get(this.currentPosition + (z ? 1 : -1), -1);
    }

    public SparseArray<View> removeTabs() {
        SparseArray<View> sparseArray = new SparseArray<>();
        for (int i = 0; i < getChildCount(); i++) {
            sparseArray.get(this.positionToId.get(i), getChildAt(i));
        }
        this.positionToId.clear();
        this.idToPosition.clear();
        this.positionToWidth.clear();
        this.tabsContainer.removeAllViews();
        this.allTextWidth = 0;
        this.tabCount = 0;
        return sparseArray;
    }

    public int getTabsCount() {
        return this.tabCount;
    }

    public boolean hasTab(int i) {
        return this.idToPosition.get(i, -1) != -1;
    }

    public void addTextTab(int i, CharSequence charSequence) {
        addTextTab(i, charSequence, (SparseArray<View>) null);
    }

    public void addTextTab(final int i, CharSequence charSequence, SparseArray<View> sparseArray) {
        int i2 = this.tabCount;
        this.tabCount = i2 + 1;
        if (i2 == 0 && this.selectedTabId == -1) {
            this.selectedTabId = i;
        }
        this.positionToId.put(i2, i);
        this.idToPosition.put(i, i2);
        int i3 = this.selectedTabId;
        if (i3 != -1 && i3 == i) {
            this.currentPosition = i2;
            this.prevLayoutWidth = 0;
        }
        TextView textView = null;
        if (sparseArray != null) {
            textView = (TextView) sparseArray.get(i);
            sparseArray.delete(i);
        }
        if (textView == null) {
            textView = new TextView(getContext()) {
                public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                    super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                    accessibilityNodeInfo.setSelected(ScrollSlidingTextTabStrip.this.selectedTabId == i);
                }
            };
            textView.setWillNotDraw(false);
            textView.setGravity(17);
            textView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(this.selectorColorKey), 3));
            textView.setTextSize(1, 15.0f);
            textView.setSingleLine(true);
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
            textView.setOnClickListener(new ScrollSlidingTextTabStrip$$ExternalSyntheticLambda1(this, i));
        }
        textView.setText(charSequence);
        int ceil = ((int) Math.ceil((double) textView.getPaint().measureText(charSequence, 0, charSequence.length()))) + textView.getPaddingLeft() + textView.getPaddingRight();
        this.tabsContainer.addView(textView, LayoutHelper.createLinear(0, -1));
        this.allTextWidth += ceil;
        this.positionToWidth.put(i2, ceil);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addTextTab$0(int i, View view) {
        ScrollSlidingTabStripDelegate scrollSlidingTabStripDelegate;
        int indexOfChild = this.tabsContainer.indexOfChild(view);
        if (indexOfChild >= 0) {
            int i2 = this.currentPosition;
            if (indexOfChild != i2 || (scrollSlidingTabStripDelegate = this.delegate) == null) {
                boolean z = i2 < indexOfChild;
                this.scrollingToChild = -1;
                this.previousPosition = i2;
                this.currentPosition = indexOfChild;
                this.selectedTabId = i;
                if (this.animatingIndicator) {
                    AndroidUtilities.cancelRunOnUIThread(this.animationRunnable);
                    this.animatingIndicator = false;
                }
                this.animationTime = 0.0f;
                this.animatingIndicator = true;
                this.animateIndicatorStartX = this.indicatorX;
                this.animateIndicatorStartWidth = this.indicatorWidth;
                TextView textView = (TextView) view;
                this.animateIndicatorToWidth = getChildWidth(textView);
                this.animateIndicatorToX = textView.getLeft() + ((textView.getMeasuredWidth() - this.animateIndicatorToWidth) / 2);
                setEnabled(false);
                AndroidUtilities.runOnUIThread(this.animationRunnable, 16);
                ScrollSlidingTabStripDelegate scrollSlidingTabStripDelegate2 = this.delegate;
                if (scrollSlidingTabStripDelegate2 != null) {
                    scrollSlidingTabStripDelegate2.onPageSelected(i, z);
                }
                scrollToChild(indexOfChild);
                return;
            }
            scrollSlidingTabStripDelegate.onSamePageSelected();
        }
    }

    public void finishAddingTabs() {
        int childCount = this.tabsContainer.getChildCount();
        int i = 0;
        while (i < childCount) {
            TextView textView = (TextView) this.tabsContainer.getChildAt(i);
            textView.setTag(this.currentPosition == i ? this.activeTextColorKey : this.unactiveTextColorKey);
            textView.setTextColor(Theme.getColor(this.currentPosition == i ? this.activeTextColorKey : this.unactiveTextColorKey));
            if (i == 0) {
                textView.getLayoutParams().width = childCount == 1 ? -2 : 0;
            }
            i++;
        }
    }

    public void setColors(String str, String str2, String str3, String str4) {
        this.tabLineColorKey = str;
        this.activeTextColorKey = str2;
        this.unactiveTextColorKey = str3;
        this.selectorColorKey = str4;
        this.selectorDrawable.setColor(Theme.getColor(str));
    }

    public int getCurrentTabId() {
        return this.selectedTabId;
    }

    public void setInitialTabId(int i) {
        this.selectedTabId = i;
        int i2 = this.idToPosition.get(i);
        if (((TextView) this.tabsContainer.getChildAt(i2)) != null) {
            this.currentPosition = i2;
            this.prevLayoutWidth = 0;
            finishAddingTabs();
            requestLayout();
        }
    }

    public void resetTab() {
        this.selectedTabId = -1;
    }

    public int getFirstTabId() {
        return this.positionToId.get(0, 0);
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View view, long j) {
        boolean drawChild = super.drawChild(canvas, view, j);
        if (view == this.tabsContainer) {
            int measuredHeight = getMeasuredHeight();
            this.selectorDrawable.setAlpha((int) (this.tabsContainer.getAlpha() * 255.0f));
            float f = ((float) this.indicatorX) + this.indicatorXAnimationDx;
            this.selectorDrawable.setBounds((int) f, measuredHeight - AndroidUtilities.dpr(4.0f), (int) (((float) this.indicatorWidth) + f + this.indicatorWidthAnimationDx), measuredHeight);
            this.selectorDrawable.draw(canvas);
        }
        return drawChild;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i) - AndroidUtilities.dp(22.0f);
        int childCount = this.tabsContainer.getChildCount();
        for (int i3 = 0; i3 < childCount; i3++) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.tabsContainer.getChildAt(i3).getLayoutParams();
            int i4 = this.allTextWidth;
            if (i4 > size) {
                layoutParams.weight = 0.0f;
                layoutParams.width = -2;
            } else if (this.useSameWidth) {
                layoutParams.weight = 1.0f / ((float) childCount);
                layoutParams.width = 0;
            } else if (i3 == 0 && childCount == 1) {
                layoutParams.weight = 0.0f;
                layoutParams.width = -2;
            } else {
                layoutParams.weight = (1.0f / ((float) i4)) * ((float) this.positionToWidth.get(i3));
                layoutParams.width = 0;
            }
        }
        if (childCount == 1 || this.allTextWidth > size) {
            this.tabsContainer.setWeightSum(0.0f);
        } else {
            this.tabsContainer.setWeightSum(1.0f);
        }
        super.onMeasure(i, i2);
    }

    private void scrollToChild(int i) {
        if (this.tabCount != 0 && this.scrollingToChild != i) {
            this.scrollingToChild = i;
            TextView textView = (TextView) this.tabsContainer.getChildAt(i);
            if (textView != null) {
                int scrollX = getScrollX();
                int left = textView.getLeft();
                int measuredWidth = textView.getMeasuredWidth();
                if (left - AndroidUtilities.dp(50.0f) < scrollX) {
                    smoothScrollTo(left - AndroidUtilities.dp(50.0f), 0);
                    return;
                }
                int i2 = left + measuredWidth;
                if (AndroidUtilities.dp(21.0f) + i2 > scrollX + getWidth()) {
                    smoothScrollTo(i2, 0);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        super.onLayout(z, i, i2, i3, i4);
        int i6 = i3 - i;
        if (this.prevLayoutWidth != i6) {
            this.prevLayoutWidth = i6;
            this.scrollingToChild = -1;
            if (this.animatingIndicator) {
                AndroidUtilities.cancelRunOnUIThread(this.animationRunnable);
                this.animatingIndicator = false;
                setEnabled(true);
                ScrollSlidingTabStripDelegate scrollSlidingTabStripDelegate = this.delegate;
                if (scrollSlidingTabStripDelegate != null) {
                    scrollSlidingTabStripDelegate.onPageScrolled(1.0f);
                }
            }
            TextView textView = (TextView) this.tabsContainer.getChildAt(this.currentPosition);
            if (textView != null) {
                this.indicatorWidth = getChildWidth(textView);
                int left = textView.getLeft();
                int measuredWidth = textView.getMeasuredWidth();
                int i7 = this.indicatorWidth;
                int i8 = left + ((measuredWidth - i7) / 2);
                this.indicatorX = i8;
                int i9 = this.animateFromIndicaxtorX;
                if (i9 > 0 && (i5 = this.animateFromIndicatorWidth) > 0) {
                    if (!(i9 == i8 && i5 == i7)) {
                        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
                        ofFloat.addUpdateListener(new ScrollSlidingTextTabStrip$$ExternalSyntheticLambda0(this, i9 - i8, i5 - i7));
                        ofFloat.setDuration(200);
                        ofFloat.setInterpolator(CubicBezierInterpolator.DEFAULT);
                        ofFloat.start();
                    }
                    this.animateFromIndicaxtorX = 0;
                    this.animateFromIndicatorWidth = 0;
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onLayout$1(int i, int i2, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.indicatorXAnimationDx = ((float) i) * floatValue;
        this.indicatorWidthAnimationDx = ((float) i2) * floatValue;
        this.tabsContainer.invalidate();
        invalidate();
    }

    public int getCurrentPosition() {
        return this.currentPosition;
    }

    public void setEnabled(boolean z) {
        super.setEnabled(z);
        int childCount = this.tabsContainer.getChildCount();
        for (int i = 0; i < childCount; i++) {
            this.tabsContainer.getChildAt(i).setEnabled(z);
        }
    }

    public void selectTabWithId(int i, float f) {
        int i2 = this.idToPosition.get(i, -1);
        if (i2 >= 0) {
            if (f < 0.0f) {
                f = 0.0f;
            } else if (f > 1.0f) {
                f = 1.0f;
            }
            TextView textView = (TextView) this.tabsContainer.getChildAt(this.currentPosition);
            TextView textView2 = (TextView) this.tabsContainer.getChildAt(i2);
            if (!(textView == null || textView2 == null)) {
                this.animateIndicatorStartWidth = getChildWidth(textView);
                this.animateIndicatorStartX = textView.getLeft() + ((textView.getMeasuredWidth() - this.animateIndicatorStartWidth) / 2);
                this.animateIndicatorToWidth = getChildWidth(textView2);
                this.animateIndicatorToX = textView2.getLeft() + ((textView2.getMeasuredWidth() - this.animateIndicatorToWidth) / 2);
                setAnimationProgressInernal(textView2, textView, f);
                if (f >= 1.0f) {
                    textView.setTag(this.unactiveTextColorKey);
                    textView2.setTag(this.activeTextColorKey);
                }
                scrollToChild(this.tabsContainer.indexOfChild(textView2));
            }
            if (f >= 1.0f) {
                this.currentPosition = i2;
                this.selectedTabId = i;
            }
        }
    }

    private int getChildWidth(TextView textView) {
        Layout layout = textView.getLayout();
        if (layout != null) {
            return ((int) Math.ceil((double) layout.getLineWidth(0))) + AndroidUtilities.dp(2.0f);
        }
        return textView.getMeasuredWidth();
    }

    public void recordIndicatorParams() {
        this.animateFromIndicaxtorX = this.indicatorX;
        this.animateFromIndicatorWidth = this.indicatorWidth;
    }
}
