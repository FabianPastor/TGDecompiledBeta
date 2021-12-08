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
                long dt = SystemClock.elapsedRealtime() - ScrollSlidingTextTabStrip.this.lastAnimationTime;
                if (dt > 17) {
                    dt = 17;
                }
                ScrollSlidingTextTabStrip.access$216(ScrollSlidingTextTabStrip.this, ((float) dt) / 200.0f);
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
    private boolean animationRunning;
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

    static /* synthetic */ float access$216(ScrollSlidingTextTabStrip x0, float x1) {
        float f = x0.animationTime + x1;
        x0.animationTime = f;
        return f;
    }

    public interface ScrollSlidingTabStripDelegate {
        void onPageScrolled(float f);

        void onPageSelected(int i, boolean z);

        void onSamePageSelected();

        /* renamed from: org.telegram.ui.Components.ScrollSlidingTextTabStrip$ScrollSlidingTabStripDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$onSamePageSelected(ScrollSlidingTabStripDelegate _this) {
            }
        }
    }

    public ScrollSlidingTextTabStrip(Context context) {
        super(context);
        float rad = AndroidUtilities.dpf2(3.0f);
        this.selectorDrawable.setCornerRadii(new float[]{rad, rad, rad, rad, 0.0f, 0.0f, 0.0f, 0.0f});
        this.selectorDrawable.setColor(Theme.getColor(this.tabLineColorKey));
        setFillViewport(true);
        setWillNotDraw(false);
        setHorizontalScrollBarEnabled(false);
        AnonymousClass2 r2 = new LinearLayout(context) {
            public void setAlpha(float alpha) {
                super.setAlpha(alpha);
                ScrollSlidingTextTabStrip.this.invalidate();
            }
        };
        this.tabsContainer = r2;
        r2.setOrientation(0);
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

    private void setAnimationProgressInernal(TextView newTab, TextView prevTab, float value) {
        TextView textView = newTab;
        TextView textView2 = prevTab;
        if (textView != null && textView2 != null) {
            int newColor = Theme.getColor(this.activeTextColorKey);
            int prevColor = Theme.getColor(this.unactiveTextColorKey);
            int r1 = Color.red(newColor);
            int g1 = Color.green(newColor);
            int b1 = Color.blue(newColor);
            int a1 = Color.alpha(newColor);
            int r2 = Color.red(prevColor);
            int g2 = Color.green(prevColor);
            int b2 = Color.blue(prevColor);
            int a2 = Color.alpha(prevColor);
            int i = newColor;
            int i2 = prevColor;
            textView2.setTextColor(Color.argb((int) (((float) a1) + (((float) (a2 - a1)) * value)), (int) (((float) r1) + (((float) (r2 - r1)) * value)), (int) (((float) g1) + (((float) (g2 - g1)) * value)), (int) (((float) b1) + (((float) (b2 - b1)) * value))));
            textView.setTextColor(Color.argb((int) (((float) a2) + (((float) (a1 - a2)) * value)), (int) (((float) r2) + (((float) (r1 - r2)) * value)), (int) (((float) g2) + (((float) (g1 - g2)) * value)), (int) (((float) b2) + (((float) (b1 - b2)) * value))));
            int i3 = this.animateIndicatorStartX;
            this.indicatorX = (int) (((float) i3) + (((float) (this.animateIndicatorToX - i3)) * value));
            int i4 = this.animateIndicatorStartWidth;
            this.indicatorWidth = (int) (((float) i4) + (((float) (this.animateIndicatorToWidth - i4)) * value));
            invalidate();
        }
    }

    public void setAnimationIdicatorProgress(float value) {
        this.animationIdicatorProgress = value;
        TextView newTab = (TextView) this.tabsContainer.getChildAt(this.currentPosition);
        TextView prevTab = (TextView) this.tabsContainer.getChildAt(this.previousPosition);
        if (prevTab != null && newTab != null) {
            setAnimationProgressInernal(newTab, prevTab, value);
            if (value >= 1.0f) {
                prevTab.setTag(this.unactiveTextColorKey);
                newTab.setTag(this.activeTextColorKey);
            }
            ScrollSlidingTabStripDelegate scrollSlidingTabStripDelegate = this.delegate;
            if (scrollSlidingTabStripDelegate != null) {
                scrollSlidingTabStripDelegate.onPageScrolled(value);
            }
        }
    }

    public void setUseSameWidth(boolean value) {
        this.useSameWidth = value;
    }

    public Drawable getSelectorDrawable() {
        return this.selectorDrawable;
    }

    public ViewGroup getTabsContainer() {
        return this.tabsContainer;
    }

    public float getAnimationIdicatorProgress() {
        return this.animationIdicatorProgress;
    }

    public int getNextPageId(boolean forward) {
        return this.positionToId.get(this.currentPosition + (forward ? 1 : -1), -1);
    }

    public SparseArray<View> removeTabs() {
        SparseArray<View> views = new SparseArray<>();
        for (int i = 0; i < getChildCount(); i++) {
            views.get(this.positionToId.get(i), getChildAt(i));
        }
        this.positionToId.clear();
        this.idToPosition.clear();
        this.positionToWidth.clear();
        this.tabsContainer.removeAllViews();
        this.allTextWidth = 0;
        this.tabCount = 0;
        return views;
    }

    public int getTabsCount() {
        return this.tabCount;
    }

    public boolean hasTab(int id) {
        return this.idToPosition.get(id, -1) != -1;
    }

    public void addTextTab(int id, CharSequence text) {
        addTextTab(id, text, (SparseArray<View>) null);
    }

    public void addTextTab(final int id, CharSequence text, SparseArray<View> viewsCache) {
        int position = this.tabCount;
        this.tabCount = position + 1;
        if (position == 0 && this.selectedTabId == -1) {
            this.selectedTabId = id;
        }
        this.positionToId.put(position, id);
        this.idToPosition.put(id, position);
        int i = this.selectedTabId;
        if (i != -1 && i == id) {
            this.currentPosition = position;
            this.prevLayoutWidth = 0;
        }
        TextView tab = null;
        if (viewsCache != null) {
            tab = (TextView) viewsCache.get(id);
            viewsCache.delete(id);
        }
        if (tab == null) {
            tab = new TextView(getContext()) {
                public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                    super.onInitializeAccessibilityNodeInfo(info);
                    info.setSelected(ScrollSlidingTextTabStrip.this.selectedTabId == id);
                }
            };
            tab.setWillNotDraw(false);
            tab.setGravity(17);
            tab.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(this.selectorColorKey), 3));
            tab.setTextSize(1, 15.0f);
            tab.setSingleLine(true);
            tab.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            tab.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
            tab.setOnClickListener(new ScrollSlidingTextTabStrip$$ExternalSyntheticLambda1(this, id));
        }
        tab.setText(text);
        int tabWidth = ((int) Math.ceil((double) tab.getPaint().measureText(text, 0, text.length()))) + tab.getPaddingLeft() + tab.getPaddingRight();
        this.tabsContainer.addView(tab, LayoutHelper.createLinear(0, -1));
        this.allTextWidth += tabWidth;
        this.positionToWidth.put(position, tabWidth);
    }

    /* renamed from: lambda$addTextTab$0$org-telegram-ui-Components-ScrollSlidingTextTabStrip  reason: not valid java name */
    public /* synthetic */ void m2549x28370d04(int id, View v) {
        ScrollSlidingTabStripDelegate scrollSlidingTabStripDelegate;
        int position1 = this.tabsContainer.indexOfChild(v);
        if (position1 >= 0) {
            int i = this.currentPosition;
            if (position1 != i || (scrollSlidingTabStripDelegate = this.delegate) == null) {
                boolean scrollingForward = i < position1;
                this.scrollingToChild = -1;
                this.previousPosition = i;
                this.currentPosition = position1;
                this.selectedTabId = id;
                if (this.animatingIndicator) {
                    AndroidUtilities.cancelRunOnUIThread(this.animationRunnable);
                    this.animatingIndicator = false;
                }
                this.animationTime = 0.0f;
                this.animatingIndicator = true;
                this.animateIndicatorStartX = this.indicatorX;
                this.animateIndicatorStartWidth = this.indicatorWidth;
                TextView nextChild = (TextView) v;
                this.animateIndicatorToWidth = getChildWidth(nextChild);
                this.animateIndicatorToX = nextChild.getLeft() + ((nextChild.getMeasuredWidth() - this.animateIndicatorToWidth) / 2);
                setEnabled(false);
                AndroidUtilities.runOnUIThread(this.animationRunnable, 16);
                ScrollSlidingTabStripDelegate scrollSlidingTabStripDelegate2 = this.delegate;
                if (scrollSlidingTabStripDelegate2 != null) {
                    scrollSlidingTabStripDelegate2.onPageSelected(id, scrollingForward);
                }
                scrollToChild(position1);
                return;
            }
            scrollSlidingTabStripDelegate.onSamePageSelected();
        }
    }

    public void finishAddingTabs() {
        int count = this.tabsContainer.getChildCount();
        int a = 0;
        while (a < count) {
            TextView tab = (TextView) this.tabsContainer.getChildAt(a);
            tab.setTag(this.currentPosition == a ? this.activeTextColorKey : this.unactiveTextColorKey);
            tab.setTextColor(Theme.getColor(this.currentPosition == a ? this.activeTextColorKey : this.unactiveTextColorKey));
            if (a == 0) {
                tab.getLayoutParams().width = count == 1 ? -2 : 0;
            }
            a++;
        }
    }

    public void setColors(String line, String active, String unactive, String selector) {
        this.tabLineColorKey = line;
        this.activeTextColorKey = active;
        this.unactiveTextColorKey = unactive;
        this.selectorColorKey = selector;
        this.selectorDrawable.setColor(Theme.getColor(line));
    }

    public int getCurrentTabId() {
        return this.selectedTabId;
    }

    public void setInitialTabId(int id) {
        this.selectedTabId = id;
        int pos = this.idToPosition.get(id);
        if (((TextView) this.tabsContainer.getChildAt(pos)) != null) {
            this.currentPosition = pos;
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
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean result = super.drawChild(canvas, child, drawingTime);
        if (child == this.tabsContainer) {
            int height = getMeasuredHeight();
            this.selectorDrawable.setAlpha((int) (this.tabsContainer.getAlpha() * 255.0f));
            float x = ((float) this.indicatorX) + this.indicatorXAnimationDx;
            this.selectorDrawable.setBounds((int) x, height - AndroidUtilities.dpr(4.0f), (int) (((float) this.indicatorWidth) + x + this.indicatorWidthAnimationDx), height);
            this.selectorDrawable.draw(canvas);
        }
        return result;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(22.0f);
        int count = this.tabsContainer.getChildCount();
        for (int a = 0; a < count; a++) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.tabsContainer.getChildAt(a).getLayoutParams();
            int i = this.allTextWidth;
            if (i > width) {
                layoutParams.weight = 0.0f;
                layoutParams.width = -2;
            } else if (this.useSameWidth) {
                layoutParams.weight = 1.0f / ((float) count);
                layoutParams.width = 0;
            } else if (a == 0 && count == 1) {
                layoutParams.weight = 0.0f;
                layoutParams.width = -2;
            } else {
                layoutParams.weight = (1.0f / ((float) i)) * ((float) this.positionToWidth.get(a));
                layoutParams.width = 0;
            }
        }
        if (count == 1 || this.allTextWidth > width) {
            this.tabsContainer.setWeightSum(0.0f);
        } else {
            this.tabsContainer.setWeightSum(1.0f);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void scrollToChild(int position) {
        if (this.tabCount != 0 && this.scrollingToChild != position) {
            this.scrollingToChild = position;
            TextView child = (TextView) this.tabsContainer.getChildAt(position);
            if (child != null) {
                int currentScrollX = getScrollX();
                int left = child.getLeft();
                int width = child.getMeasuredWidth();
                if (left - AndroidUtilities.dp(50.0f) < currentScrollX) {
                    smoothScrollTo(left - AndroidUtilities.dp(50.0f), 0);
                } else if (left + width + AndroidUtilities.dp(21.0f) > getWidth() + currentScrollX) {
                    smoothScrollTo(left + width, 0);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        int i;
        super.onLayout(changed, l, t, r, b);
        if (this.prevLayoutWidth != r - l) {
            this.prevLayoutWidth = r - l;
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
            TextView child = (TextView) this.tabsContainer.getChildAt(this.currentPosition);
            if (child != null) {
                this.indicatorWidth = getChildWidth(child);
                int left = child.getLeft();
                int measuredWidth = child.getMeasuredWidth();
                int i2 = this.indicatorWidth;
                int i3 = left + ((measuredWidth - i2) / 2);
                this.indicatorX = i3;
                int i4 = this.animateFromIndicaxtorX;
                if (i4 > 0 && (i = this.animateFromIndicatorWidth) > 0) {
                    if (!(i4 == i3 && i == i2)) {
                        ValueAnimator valueAnimator = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
                        valueAnimator.addUpdateListener(new ScrollSlidingTextTabStrip$$ExternalSyntheticLambda0(this, i4 - i3, i - i2));
                        valueAnimator.setDuration(200);
                        valueAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                        valueAnimator.start();
                    }
                    this.animateFromIndicaxtorX = 0;
                    this.animateFromIndicatorWidth = 0;
                }
            }
        }
    }

    /* renamed from: lambda$onLayout$1$org-telegram-ui-Components-ScrollSlidingTextTabStrip  reason: not valid java name */
    public /* synthetic */ void m2550x57ecd781(int dX, int dW, ValueAnimator valueAnimator1) {
        float v = ((Float) valueAnimator1.getAnimatedValue()).floatValue();
        this.indicatorXAnimationDx = ((float) dX) * v;
        this.indicatorWidthAnimationDx = ((float) dW) * v;
        this.tabsContainer.invalidate();
        invalidate();
    }

    public int getCurrentPosition() {
        return this.currentPosition;
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        int count = this.tabsContainer.getChildCount();
        for (int a = 0; a < count; a++) {
            this.tabsContainer.getChildAt(a).setEnabled(enabled);
        }
    }

    public void selectTabWithId(int id, float progress) {
        int position = this.idToPosition.get(id, -1);
        if (position >= 0) {
            if (progress < 0.0f) {
                progress = 0.0f;
            } else if (progress > 1.0f) {
                progress = 1.0f;
            }
            TextView child = (TextView) this.tabsContainer.getChildAt(this.currentPosition);
            TextView nextChild = (TextView) this.tabsContainer.getChildAt(position);
            if (!(child == null || nextChild == null)) {
                this.animateIndicatorStartWidth = getChildWidth(child);
                this.animateIndicatorStartX = child.getLeft() + ((child.getMeasuredWidth() - this.animateIndicatorStartWidth) / 2);
                this.animateIndicatorToWidth = getChildWidth(nextChild);
                this.animateIndicatorToX = nextChild.getLeft() + ((nextChild.getMeasuredWidth() - this.animateIndicatorToWidth) / 2);
                setAnimationProgressInernal(nextChild, child, progress);
                if (progress >= 1.0f) {
                    child.setTag(this.unactiveTextColorKey);
                    nextChild.setTag(this.activeTextColorKey);
                }
                scrollToChild(this.tabsContainer.indexOfChild(nextChild));
            }
            if (progress >= 1.0f) {
                this.currentPosition = position;
                this.selectedTabId = id;
            }
        }
    }

    private int getChildWidth(TextView child) {
        Layout layout = child.getLayout();
        if (layout != null) {
            return ((int) Math.ceil((double) layout.getLineWidth(0))) + AndroidUtilities.dp(2.0f);
        }
        return child.getMeasuredWidth();
    }

    public void onPageScrolled(int position, int first) {
        if (this.currentPosition != position) {
            this.currentPosition = position;
            if (position < this.tabsContainer.getChildCount()) {
                int a = 0;
                while (true) {
                    boolean z = true;
                    if (a >= this.tabsContainer.getChildCount()) {
                        break;
                    }
                    View childAt = this.tabsContainer.getChildAt(a);
                    if (a != position) {
                        z = false;
                    }
                    childAt.setSelected(z);
                    a++;
                }
                if (first != position || position <= 1) {
                    scrollToChild(position);
                } else {
                    scrollToChild(position - 1);
                }
                invalidate();
            }
        }
    }

    public void recordIndicatorParams() {
        this.animateFromIndicaxtorX = this.indicatorX;
        this.animateFromIndicatorWidth = this.indicatorWidth;
    }
}
