package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.SystemClock;
import android.support.annotation.Keep;
import android.util.SparseIntArray;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class ScrollSlidingTextTabStrip extends HorizontalScrollView {
    private int allTextWidth;
    private int animateIndicatorStartWidth;
    private int animateIndicatorStartX;
    private int animateIndicatorToWidth;
    private int animateIndicatorToX;
    private boolean animatingIndicator;
    private float animationIdicatorProgress;
    private Runnable animationRunnable = new Runnable() {
        public void run() {
            if (ScrollSlidingTextTabStrip.this.animatingIndicator) {
                long dt = SystemClock.elapsedRealtime() - ScrollSlidingTextTabStrip.this.lastAnimationTime;
                if (dt > 17) {
                    dt = 17;
                }
                ScrollSlidingTextTabStrip.this.animationTime = ScrollSlidingTextTabStrip.this.animationTime + (((float) dt) / 200.0f);
                ScrollSlidingTextTabStrip.this.setAnimationIdicatorProgress(ScrollSlidingTextTabStrip.this.interpolator.getInterpolation(ScrollSlidingTextTabStrip.this.animationTime));
                if (ScrollSlidingTextTabStrip.this.animationTime > 1.0f) {
                    ScrollSlidingTextTabStrip.this.animationTime = 1.0f;
                }
                if (ScrollSlidingTextTabStrip.this.animationTime < 1.0f) {
                    AndroidUtilities.runOnUIThread(ScrollSlidingTextTabStrip.this.animationRunnable);
                    return;
                }
                ScrollSlidingTextTabStrip.this.animatingIndicator = false;
                ScrollSlidingTextTabStrip.this.setEnabled(true);
                if (ScrollSlidingTextTabStrip.this.delegate != null) {
                    ScrollSlidingTextTabStrip.this.delegate.onPageScrolled(1.0f);
                }
            }
        }
    };
    private boolean animationRunning;
    private float animationTime;
    private int currentPosition;
    private ScrollSlidingTabStripDelegate delegate;
    private SparseIntArray idToPosition = new SparseIntArray(5);
    private int indicatorWidth;
    private int indicatorX;
    private CubicBezierInterpolator interpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
    private long lastAnimationTime;
    private SparseIntArray positionToId = new SparseIntArray(5);
    private SparseIntArray positionToWidth = new SparseIntArray(5);
    private int prevLayoutWidth;
    private int previousPosition;
    private Paint rectPaint;
    private int selectedTabId = -1;
    private int tabCount;
    private LinearLayout tabsContainer;
    private boolean useSameWidth;

    public interface ScrollSlidingTabStripDelegate {
        void onPageScrolled(float f);

        void onPageSelected(int i, boolean z);
    }

    public ScrollSlidingTextTabStrip(Context context) {
        super(context);
        setFillViewport(true);
        setWillNotDraw(false);
        setHorizontalScrollBarEnabled(false);
        this.tabsContainer = new LinearLayout(context);
        this.tabsContainer.setOrientation(0);
        this.tabsContainer.setLayoutParams(new LayoutParams(-1, -1));
        addView(this.tabsContainer);
        this.rectPaint = new Paint();
        this.rectPaint.setAntiAlias(true);
        this.rectPaint.setStyle(Style.FILL);
        this.rectPaint.setColor(Theme.getColor("actionBarDefaultTitle"));
    }

    public void setDelegate(ScrollSlidingTabStripDelegate scrollSlidingTabStripDelegate) {
        this.delegate = scrollSlidingTabStripDelegate;
    }

    public boolean isAnimatingIndicator() {
        return this.animatingIndicator;
    }

    private void setAnimationProgressInernal(TextView newTab, TextView prevTab, float value) {
        int newColor = Theme.getColor("actionBarDefaultTitle");
        int prevColor = Theme.getColor("actionBarDefaultSubtitle");
        int r1 = Color.red(newColor);
        int g1 = Color.green(newColor);
        int b1 = Color.blue(newColor);
        int a1 = Color.alpha(newColor);
        int r2 = Color.red(prevColor);
        int g2 = Color.green(prevColor);
        int b2 = Color.blue(prevColor);
        int a2 = Color.alpha(prevColor);
        prevTab.setTextColor(Color.argb((int) (((float) a1) + (((float) (a2 - a1)) * value)), (int) (((float) r1) + (((float) (r2 - r1)) * value)), (int) (((float) g1) + (((float) (g2 - g1)) * value)), (int) (((float) b1) + (((float) (b2 - b1)) * value))));
        newTab.setTextColor(Color.argb((int) (((float) a2) + (((float) (a1 - a2)) * value)), (int) (((float) r2) + (((float) (r1 - r2)) * value)), (int) (((float) g2) + (((float) (g1 - g2)) * value)), (int) (((float) b2) + (((float) (b1 - b2)) * value))));
        this.indicatorX = (int) (((float) this.animateIndicatorStartX) + (((float) (this.animateIndicatorToX - this.animateIndicatorStartX)) * value));
        this.indicatorWidth = (int) (((float) this.animateIndicatorStartWidth) + (((float) (this.animateIndicatorToWidth - this.animateIndicatorStartWidth)) * value));
        invalidate();
    }

    @Keep
    public void setAnimationIdicatorProgress(float value) {
        this.animationIdicatorProgress = value;
        setAnimationProgressInernal((TextView) this.tabsContainer.getChildAt(this.currentPosition), (TextView) this.tabsContainer.getChildAt(this.previousPosition), value);
        if (this.delegate != null) {
            this.delegate.onPageScrolled(value);
        }
    }

    public void setUseSameWidth(boolean value) {
        this.useSameWidth = value;
    }

    public Paint getRectPaint() {
        return this.rectPaint;
    }

    public View getTabsContainer() {
        return this.tabsContainer;
    }

    public float getAnimationIdicatorProgress() {
        return this.animationIdicatorProgress;
    }

    public int getNextPageId(boolean forward) {
        return this.positionToId.get((forward ? 1 : -1) + this.currentPosition, -1);
    }

    public void removeTabs() {
        this.positionToId.clear();
        this.idToPosition.clear();
        this.positionToWidth.clear();
        this.tabsContainer.removeAllViews();
        this.allTextWidth = 0;
        this.tabCount = 0;
    }

    public int getTabsCount() {
        return this.tabCount;
    }

    public boolean hasTab(int id) {
        return this.idToPosition.get(id, -1) != -1;
    }

    public void addTextTab(int id, CharSequence text) {
        int position = this.tabCount;
        this.tabCount = position + 1;
        if (position == 0 && this.selectedTabId == -1) {
            this.selectedTabId = id;
        }
        this.positionToId.put(position, id);
        this.idToPosition.put(id, position);
        if (this.selectedTabId != -1 && this.selectedTabId == id) {
            this.currentPosition = position;
            this.prevLayoutWidth = 0;
        }
        TextView tab = new TextView(getContext());
        tab.setGravity(17);
        tab.setText(text);
        tab.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("actionBarDefaultSelector"), 2));
        tab.setTextSize(1, 14.0f);
        tab.setSingleLine(true);
        tab.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        tab.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
        tab.setOnClickListener(new ScrollSlidingTextTabStrip$$Lambda$0(this, id));
        int tabWidth = ((int) Math.ceil((double) tab.getPaint().measureText(text, 0, text.length()))) + AndroidUtilities.dp(16.0f);
        this.allTextWidth += tabWidth;
        this.positionToWidth.put(position, tabWidth);
        this.tabsContainer.addView(tab, LayoutHelper.createLinear(0, -1));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$addTextTab$0$ScrollSlidingTextTabStrip(int id, View v) {
        int position1 = this.tabsContainer.indexOfChild(v);
        if (position1 >= 0 && position1 != this.currentPosition) {
            boolean scrollingForward;
            if (this.currentPosition < position1) {
                scrollingForward = true;
            } else {
                scrollingForward = false;
            }
            this.previousPosition = this.currentPosition;
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
            this.animateIndicatorToX = v.getLeft();
            this.animateIndicatorToWidth = v.getMeasuredWidth();
            setEnabled(false);
            AndroidUtilities.runOnUIThread(this.animationRunnable, 16);
            if (this.delegate != null) {
                this.delegate.onPageSelected(id, scrollingForward);
            }
            scrollToChild(position1);
        }
    }

    public void finishAddingTabs() {
        int count = this.tabsContainer.getChildCount();
        int a = 0;
        while (a < count) {
            TextView tab = (TextView) this.tabsContainer.getChildAt(a);
            tab.setTag(this.currentPosition == a ? "actionBarDefaultTitle" : "actionBarDefaultSubtitle");
            tab.setTextColor(Theme.getColor(this.currentPosition == a ? "actionBarDefaultTitle" : "actionBarDefaultSubtitle"));
            a++;
        }
    }

    public int getCurrentTabId() {
        return this.selectedTabId;
    }

    public void setInitialTabId(int id) {
        this.selectedTabId = id;
    }

    public int getFirstTabId() {
        return this.positionToId.get(0, 0);
    }

    /* Access modifiers changed, original: protected */
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean result = super.drawChild(canvas, child, drawingTime);
        if (child == this.tabsContainer) {
            int height = getMeasuredHeight();
            canvas.drawRect((float) this.indicatorX, (float) (height - AndroidUtilities.dp(2.0f)), (float) (this.indicatorX + this.indicatorWidth), (float) height, this.rectPaint);
        }
        return result;
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int count = this.tabsContainer.getChildCount();
        for (int a = 0; a < count; a++) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.tabsContainer.getChildAt(a).getLayoutParams();
            if (this.allTextWidth > width) {
                layoutParams.weight = 0.0f;
                layoutParams.width = -2;
            } else if (this.useSameWidth) {
                layoutParams.weight = 1.0f / ((float) count);
                layoutParams.width = 0;
            } else {
                layoutParams.weight = (1.0f / ((float) this.allTextWidth)) * ((float) this.positionToWidth.get(a));
                layoutParams.width = 0;
            }
        }
        if (this.allTextWidth > width) {
            this.tabsContainer.setWeightSum(0.0f);
        } else {
            this.tabsContainer.setWeightSum(1.0f);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void scrollToChild(int position) {
        if (this.tabCount != 0) {
            View child = this.tabsContainer.getChildAt(position);
            if (child != null) {
                int currentScrollX = getScrollX();
                int left = child.getLeft();
                int width = child.getMeasuredWidth();
                if (left < currentScrollX) {
                    smoothScrollTo(left, 0);
                } else if (left + width > getWidth() + currentScrollX) {
                    smoothScrollTo(left + width, 0);
                }
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (this.prevLayoutWidth != r - l) {
            this.prevLayoutWidth = r - l;
            if (this.animatingIndicator) {
                AndroidUtilities.cancelRunOnUIThread(this.animationRunnable);
                this.animatingIndicator = false;
                setEnabled(true);
                if (this.delegate != null) {
                    this.delegate.onPageScrolled(1.0f);
                }
            }
            View child = this.tabsContainer.getChildAt(this.currentPosition);
            if (child != null) {
                this.indicatorX = child.getLeft();
                this.indicatorWidth = child.getMeasuredWidth();
            }
        }
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
                this.animateIndicatorStartX = child.getLeft();
                this.animateIndicatorStartWidth = child.getMeasuredWidth();
                this.animateIndicatorToX = nextChild.getLeft();
                this.animateIndicatorToWidth = nextChild.getMeasuredWidth();
                setAnimationProgressInernal(nextChild, child, progress);
            }
            if (progress >= 1.0f) {
                this.currentPosition = position;
                this.selectedTabId = id;
            }
        }
    }

    public void onPageScrolled(int position, int first) {
        if (this.currentPosition != position) {
            this.currentPosition = position;
            if (position < this.tabsContainer.getChildCount()) {
                int a = 0;
                while (a < this.tabsContainer.getChildCount()) {
                    this.tabsContainer.getChildAt(a).setSelected(a == position);
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
}
