package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.text.TextUtils;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import java.util.Locale;
import org.telegram.ui.ActionBar.Theme;

public class NumberPicker extends LinearLayout {
    private static final int DEFAULT_LAYOUT_RESOURCE_ID = 0;
    private static final long DEFAULT_LONG_PRESS_UPDATE_INTERVAL = 300;
    private static final int SELECTOR_ADJUSTMENT_DURATION_MILLIS = 800;
    private static final int SELECTOR_MAX_FLING_VELOCITY_ADJUSTMENT = 8;
    private static final int SIZE_UNSPECIFIED = -1;
    private static final int SNAP_SCROLL_DURATION = 300;
    private static final float TOP_AND_BOTTOM_FADING_EDGE_STRENGTH = 0.9f;
    private static final int UNSCALED_DEFAULT_SELECTION_DIVIDERS_DISTANCE = 48;
    private static final int UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT = 2;
    private int SELECTOR_MIDDLE_ITEM_INDEX = (this.SELECTOR_WHEEL_ITEM_COUNT / 2);
    private int SELECTOR_WHEEL_ITEM_COUNT = 3;
    private Scroller mAdjustScroller;
    private int mBottomSelectionDividerBottom;
    private ChangeCurrentByOneFromLongPressCommand mChangeCurrentByOneFromLongPressCommand;
    private boolean mComputeMaxWidth;
    private int mCurrentScrollOffset;
    private boolean mDecrementVirtualButtonPressed;
    private String[] mDisplayedValues;
    private Scroller mFlingScroller;
    private Formatter mFormatter;
    private boolean mIncrementVirtualButtonPressed;
    private boolean mIngonreMoveEvents;
    private int mInitialScrollOffset = Integer.MIN_VALUE;
    private TextView mInputText;
    private long mLastDownEventTime;
    private float mLastDownEventY;
    private float mLastDownOrMoveEventY;
    private int mLastHandledDownDpadKeyCode = -1;
    private int mLastHoveredChildVirtualViewId;
    private long mLongPressUpdateInterval = 300;
    private int mMaxHeight;
    private int mMaxValue;
    private int mMaxWidth;
    private int mMaximumFlingVelocity;
    private int mMinHeight;
    private int mMinValue;
    private int mMinWidth;
    private int mMinimumFlingVelocity;
    private OnScrollListener mOnScrollListener;
    private OnValueChangeListener mOnValueChangeListener;
    private PressedStateHelper mPressedStateHelper;
    private int mPreviousScrollerY;
    private int mScrollState = 0;
    private Paint mSelectionDivider;
    private int mSelectionDividerHeight;
    private int mSelectionDividersDistance;
    private int mSelectorElementHeight;
    private final SparseArray<String> mSelectorIndexToStringCache = new SparseArray();
    private int[] mSelectorIndices = new int[this.SELECTOR_WHEEL_ITEM_COUNT];
    private int mSelectorTextGapHeight;
    private Paint mSelectorWheelPaint;
    private int mSolidColor;
    private int mTextSize;
    private int mTopSelectionDividerTop;
    private int mTouchSlop;
    private int mValue;
    private VelocityTracker mVelocityTracker;
    private boolean mWrapSelectorWheel;
    private int textOffset;

    class ChangeCurrentByOneFromLongPressCommand implements Runnable {
        private boolean mIncrement;

        ChangeCurrentByOneFromLongPressCommand() {
        }

        private void setStep(boolean z) {
            this.mIncrement = z;
        }

        public void run() {
            NumberPicker.this.changeValueByOne(this.mIncrement);
            NumberPicker numberPicker = NumberPicker.this;
            numberPicker.postDelayed(this, numberPicker.mLongPressUpdateInterval);
        }
    }

    public interface Formatter {
        String format(int i);
    }

    public interface OnScrollListener {
        public static final int SCROLL_STATE_FLING = 2;
        public static final int SCROLL_STATE_IDLE = 0;
        public static final int SCROLL_STATE_TOUCH_SCROLL = 1;

        void onScrollStateChange(NumberPicker numberPicker, int i);
    }

    public interface OnValueChangeListener {
        void onValueChange(NumberPicker numberPicker, int i, int i2);
    }

    class PressedStateHelper implements Runnable {
        public static final int BUTTON_DECREMENT = 2;
        public static final int BUTTON_INCREMENT = 1;
        private final int MODE_PRESS = 1;
        private final int MODE_TAPPED = 2;
        private int mManagedButton;
        private int mMode;

        PressedStateHelper() {
        }

        public void cancel() {
            NumberPicker numberPicker;
            this.mMode = 0;
            this.mManagedButton = 0;
            NumberPicker.this.removeCallbacks(this);
            if (NumberPicker.this.mIncrementVirtualButtonPressed) {
                NumberPicker.this.mIncrementVirtualButtonPressed = false;
                numberPicker = NumberPicker.this;
                numberPicker.invalidate(0, numberPicker.mBottomSelectionDividerBottom, NumberPicker.this.getRight(), NumberPicker.this.getBottom());
            }
            NumberPicker.this.mDecrementVirtualButtonPressed = false;
            if (NumberPicker.this.mDecrementVirtualButtonPressed) {
                numberPicker = NumberPicker.this;
                numberPicker.invalidate(0, 0, numberPicker.getRight(), NumberPicker.this.mTopSelectionDividerTop);
            }
        }

        public void buttonPressDelayed(int i) {
            cancel();
            this.mMode = 1;
            this.mManagedButton = i;
            NumberPicker.this.postDelayed(this, (long) ViewConfiguration.getTapTimeout());
        }

        public void buttonTapped(int i) {
            cancel();
            this.mMode = 2;
            this.mManagedButton = i;
            NumberPicker.this.post(this);
        }

        public void run() {
            int i = this.mMode;
            NumberPicker numberPicker;
            if (i == 1) {
                i = this.mManagedButton;
                if (i == 1) {
                    NumberPicker.this.mIncrementVirtualButtonPressed = true;
                    numberPicker = NumberPicker.this;
                    numberPicker.invalidate(0, numberPicker.mBottomSelectionDividerBottom, NumberPicker.this.getRight(), NumberPicker.this.getBottom());
                } else if (i == 2) {
                    NumberPicker.this.mDecrementVirtualButtonPressed = true;
                    numberPicker = NumberPicker.this;
                    numberPicker.invalidate(0, 0, numberPicker.getRight(), NumberPicker.this.mTopSelectionDividerTop);
                }
            } else if (i == 2) {
                i = this.mManagedButton;
                if (i == 1) {
                    if (!NumberPicker.this.mIncrementVirtualButtonPressed) {
                        NumberPicker.this.postDelayed(this, (long) ViewConfiguration.getPressedStateDuration());
                    }
                    numberPicker = NumberPicker.this;
                    numberPicker.mIncrementVirtualButtonPressed = numberPicker.mIncrementVirtualButtonPressed ^ 1;
                    numberPicker = NumberPicker.this;
                    numberPicker.invalidate(0, numberPicker.mBottomSelectionDividerBottom, NumberPicker.this.getRight(), NumberPicker.this.getBottom());
                } else if (i == 2) {
                    if (!NumberPicker.this.mDecrementVirtualButtonPressed) {
                        NumberPicker.this.postDelayed(this, (long) ViewConfiguration.getPressedStateDuration());
                    }
                    numberPicker = NumberPicker.this;
                    numberPicker.mDecrementVirtualButtonPressed = numberPicker.mDecrementVirtualButtonPressed ^ 1;
                    numberPicker = NumberPicker.this;
                    numberPicker.invalidate(0, 0, numberPicker.getRight(), NumberPicker.this.mTopSelectionDividerTop);
                }
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public float getBottomFadingEdgeStrength() {
        return 0.9f;
    }

    /* Access modifiers changed, original: protected */
    public float getTopFadingEdgeStrength() {
        return 0.9f;
    }

    public void setItemCount(int i) {
        if (this.SELECTOR_WHEEL_ITEM_COUNT != i) {
            this.SELECTOR_WHEEL_ITEM_COUNT = i;
            i = this.SELECTOR_WHEEL_ITEM_COUNT;
            this.SELECTOR_MIDDLE_ITEM_INDEX = i / 2;
            this.mSelectorIndices = new int[i];
            initializeSelectorWheelIndices();
        }
    }

    private void init() {
        int i;
        this.mSolidColor = 0;
        this.mSelectionDivider = new Paint();
        this.mSelectionDivider.setColor(Theme.getColor("dialogButton"));
        this.mSelectionDividerHeight = (int) TypedValue.applyDimension(1, 2.0f, getResources().getDisplayMetrics());
        this.mSelectionDividersDistance = (int) TypedValue.applyDimension(1, 48.0f, getResources().getDisplayMetrics());
        this.mMinHeight = -1;
        this.mMaxHeight = (int) TypedValue.applyDimension(1, 180.0f, getResources().getDisplayMetrics());
        int i2 = this.mMinHeight;
        if (i2 != -1) {
            i = this.mMaxHeight;
            if (i != -1 && i2 > i) {
                throw new IllegalArgumentException("minHeight > maxHeight");
            }
        }
        this.mMinWidth = (int) TypedValue.applyDimension(1, 64.0f, getResources().getDisplayMetrics());
        this.mMaxWidth = -1;
        i2 = this.mMinWidth;
        if (i2 != -1) {
            i = this.mMaxWidth;
            if (i != -1 && i2 > i) {
                throw new IllegalArgumentException("minWidth > maxWidth");
            }
        }
        this.mComputeMaxWidth = this.mMaxWidth == -1;
        this.mPressedStateHelper = new PressedStateHelper();
        setWillNotDraw(false);
        this.mInputText = new TextView(getContext());
        this.mInputText.setGravity(17);
        this.mInputText.setSingleLine(true);
        this.mInputText.setTextColor(Theme.getColor("dialogTextBlack"));
        this.mInputText.setBackgroundResource(0);
        this.mInputText.setTextSize(1, 18.0f);
        this.mInputText.setVisibility(4);
        addView(this.mInputText, new LayoutParams(-1, -2));
        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        this.mTouchSlop = viewConfiguration.getScaledTouchSlop();
        this.mMinimumFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        this.mMaximumFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity() / 8;
        this.mTextSize = (int) this.mInputText.getTextSize();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextAlign(Align.CENTER);
        paint.setTextSize((float) this.mTextSize);
        paint.setTypeface(this.mInputText.getTypeface());
        paint.setColor(this.mInputText.getTextColors().getColorForState(LinearLayout.ENABLED_STATE_SET, -1));
        this.mSelectorWheelPaint = paint;
        this.mFlingScroller = new Scroller(getContext(), null, true);
        this.mAdjustScroller = new Scroller(getContext(), new DecelerateInterpolator(2.5f));
        updateInputTextView();
    }

    public void setTextColor(int i) {
        this.mInputText.setTextColor(i);
        this.mSelectorWheelPaint.setColor(i);
    }

    public void setSelectorColor(int i) {
        this.mSelectionDivider.setColor(i);
    }

    public NumberPicker(Context context) {
        super(context);
        init();
    }

    /* Access modifiers changed, original: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        i = getMeasuredWidth();
        i2 = getMeasuredHeight();
        i3 = this.mInputText.getMeasuredWidth();
        i4 = this.mInputText.getMeasuredHeight();
        i = (i - i3) / 2;
        i2 = (i2 - i4) / 2;
        this.mInputText.layout(i, i2, i3 + i, i4 + i2);
        if (z) {
            initializeSelectorWheel();
            initializeFadingEdges();
            int height = getHeight();
            i = this.mSelectionDividersDistance;
            height = (height - i) / 2;
            i2 = this.mSelectionDividerHeight;
            this.mTopSelectionDividerTop = height - i2;
            this.mBottomSelectionDividerBottom = (this.mTopSelectionDividerTop + (i2 * 2)) + i;
        }
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(makeMeasureSpec(i, this.mMaxWidth), makeMeasureSpec(i2, this.mMaxHeight));
        setMeasuredDimension(resolveSizeAndStateRespectingMinSize(this.mMinWidth, getMeasuredWidth(), i), resolveSizeAndStateRespectingMinSize(this.mMinHeight, getMeasuredHeight(), i2));
    }

    private boolean moveToFinalScrollerPosition(Scroller scroller) {
        scroller.forceFinished(true);
        int finalY = scroller.getFinalY() - scroller.getCurrY();
        int i = this.mInitialScrollOffset - ((this.mCurrentScrollOffset + finalY) % this.mSelectorElementHeight);
        if (i == 0) {
            return false;
        }
        int abs = Math.abs(i);
        int i2 = this.mSelectorElementHeight;
        if (abs > i2 / 2) {
            i = i > 0 ? i - i2 : i + i2;
        }
        scrollBy(0, finalY + i);
        return true;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (!isEnabled() || motionEvent.getActionMasked() != 0) {
            return false;
        }
        removeAllCallbacks();
        this.mInputText.setVisibility(4);
        float y = motionEvent.getY();
        this.mLastDownEventY = y;
        this.mLastDownOrMoveEventY = y;
        this.mLastDownEventTime = motionEvent.getEventTime();
        this.mIngonreMoveEvents = false;
        float f = this.mLastDownEventY;
        if (f < ((float) this.mTopSelectionDividerTop)) {
            if (this.mScrollState == 0) {
                this.mPressedStateHelper.buttonPressDelayed(2);
            }
        } else if (f > ((float) this.mBottomSelectionDividerBottom) && this.mScrollState == 0) {
            this.mPressedStateHelper.buttonPressDelayed(1);
        }
        getParent().requestDisallowInterceptTouchEvent(true);
        if (!this.mFlingScroller.isFinished()) {
            this.mFlingScroller.forceFinished(true);
            this.mAdjustScroller.forceFinished(true);
            onScrollStateChange(0);
        } else if (this.mAdjustScroller.isFinished()) {
            f = this.mLastDownEventY;
            if (f < ((float) this.mTopSelectionDividerTop)) {
                postChangeCurrentByOneFromLongPress(false, (long) ViewConfiguration.getLongPressTimeout());
            } else if (f > ((float) this.mBottomSelectionDividerBottom)) {
                postChangeCurrentByOneFromLongPress(true, (long) ViewConfiguration.getLongPressTimeout());
            }
        } else {
            this.mFlingScroller.forceFinished(true);
            this.mAdjustScroller.forceFinished(true);
        }
        return true;
    }

    public void finishScroll() {
        if (!this.mFlingScroller.isFinished() || !this.mAdjustScroller.isFinished()) {
            this.mFlingScroller.forceFinished(true);
            this.mAdjustScroller.forceFinished(true);
            this.mCurrentScrollOffset = this.mInitialScrollOffset;
            invalidate();
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!isEnabled()) {
            return false;
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(motionEvent);
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 1) {
            removeChangeCurrentByOneFromLongPress();
            this.mPressedStateHelper.cancel();
            VelocityTracker velocityTracker = this.mVelocityTracker;
            velocityTracker.computeCurrentVelocity(1000, (float) this.mMaximumFlingVelocity);
            actionMasked = (int) velocityTracker.getYVelocity();
            if (Math.abs(actionMasked) > this.mMinimumFlingVelocity) {
                fling(actionMasked);
                onScrollStateChange(2);
            } else {
                actionMasked = (int) motionEvent.getY();
                long eventTime = motionEvent.getEventTime() - this.mLastDownEventTime;
                if (((int) Math.abs(((float) actionMasked) - this.mLastDownEventY)) > this.mTouchSlop || eventTime >= ((long) ViewConfiguration.getTapTimeout())) {
                    ensureScrollWheelAdjusted();
                } else {
                    actionMasked = (actionMasked / this.mSelectorElementHeight) - this.SELECTOR_MIDDLE_ITEM_INDEX;
                    if (actionMasked > 0) {
                        changeValueByOne(true);
                        this.mPressedStateHelper.buttonTapped(1);
                    } else if (actionMasked < 0) {
                        changeValueByOne(false);
                        this.mPressedStateHelper.buttonTapped(2);
                    }
                }
                onScrollStateChange(0);
            }
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        } else if (actionMasked == 2 && !this.mIngonreMoveEvents) {
            float y = motionEvent.getY();
            if (this.mScrollState == 1) {
                scrollBy(0, (int) (y - this.mLastDownOrMoveEventY));
                invalidate();
            } else if (((int) Math.abs(y - this.mLastDownEventY)) > this.mTouchSlop) {
                removeAllCallbacks();
                onScrollStateChange(1);
            }
            this.mLastDownOrMoveEventY = y;
        }
        return true;
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 1 || actionMasked == 3) {
            removeAllCallbacks();
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    /* JADX WARNING: Missing block: B:22:0x0047, code skipped:
            requestFocus();
            r5.mLastHandledDownDpadKeyCode = r0;
            removeAllCallbacks();
     */
    /* JADX WARNING: Missing block: B:23:0x0055, code skipped:
            if (r5.mFlingScroller.isFinished() == false) goto L_0x005f;
     */
    /* JADX WARNING: Missing block: B:24:0x0057, code skipped:
            if (r0 != 20) goto L_0x005b;
     */
    /* JADX WARNING: Missing block: B:25:0x0059, code skipped:
            r6 = true;
     */
    /* JADX WARNING: Missing block: B:26:0x005b, code skipped:
            r6 = false;
     */
    /* JADX WARNING: Missing block: B:27:0x005c, code skipped:
            changeValueByOne(r6);
     */
    /* JADX WARNING: Missing block: B:28:0x005f, code skipped:
            return true;
     */
    public boolean dispatchKeyEvent(android.view.KeyEvent r6) {
        /*
        r5 = this;
        r0 = r6.getKeyCode();
        r1 = 19;
        r2 = 20;
        if (r0 == r1) goto L_0x0019;
    L_0x000a:
        if (r0 == r2) goto L_0x0019;
    L_0x000c:
        r1 = 23;
        if (r0 == r1) goto L_0x0015;
    L_0x0010:
        r1 = 66;
        if (r0 == r1) goto L_0x0015;
    L_0x0014:
        goto L_0x0060;
    L_0x0015:
        r5.removeAllCallbacks();
        goto L_0x0060;
    L_0x0019:
        r1 = r6.getAction();
        r3 = 1;
        if (r1 == 0) goto L_0x002b;
    L_0x0020:
        if (r1 == r3) goto L_0x0023;
    L_0x0022:
        goto L_0x0060;
    L_0x0023:
        r1 = r5.mLastHandledDownDpadKeyCode;
        if (r1 != r0) goto L_0x0060;
    L_0x0027:
        r6 = -1;
        r5.mLastHandledDownDpadKeyCode = r6;
        return r3;
    L_0x002b:
        r1 = r5.mWrapSelectorWheel;
        if (r1 != 0) goto L_0x003d;
    L_0x002f:
        if (r0 != r2) goto L_0x0032;
    L_0x0031:
        goto L_0x003d;
    L_0x0032:
        r1 = r5.getValue();
        r4 = r5.getMinValue();
        if (r1 <= r4) goto L_0x0060;
    L_0x003c:
        goto L_0x0047;
    L_0x003d:
        r1 = r5.getValue();
        r4 = r5.getMaxValue();
        if (r1 >= r4) goto L_0x0060;
    L_0x0047:
        r5.requestFocus();
        r5.mLastHandledDownDpadKeyCode = r0;
        r5.removeAllCallbacks();
        r6 = r5.mFlingScroller;
        r6 = r6.isFinished();
        if (r6 == 0) goto L_0x005f;
    L_0x0057:
        if (r0 != r2) goto L_0x005b;
    L_0x0059:
        r6 = 1;
        goto L_0x005c;
    L_0x005b:
        r6 = 0;
    L_0x005c:
        r5.changeValueByOne(r6);
    L_0x005f:
        return r3;
    L_0x0060:
        r6 = super.dispatchKeyEvent(r6);
        return r6;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.NumberPicker.dispatchKeyEvent(android.view.KeyEvent):boolean");
    }

    public boolean dispatchTrackballEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 1 || actionMasked == 3) {
            removeAllCallbacks();
        }
        return super.dispatchTrackballEvent(motionEvent);
    }

    public void computeScroll() {
        Scroller scroller = this.mFlingScroller;
        if (scroller.isFinished()) {
            scroller = this.mAdjustScroller;
            if (scroller.isFinished()) {
                return;
            }
        }
        scroller.computeScrollOffset();
        int currY = scroller.getCurrY();
        if (this.mPreviousScrollerY == 0) {
            this.mPreviousScrollerY = scroller.getStartY();
        }
        scrollBy(0, currY - this.mPreviousScrollerY);
        this.mPreviousScrollerY = currY;
        if (scroller.isFinished()) {
            onScrollerFinished(scroller);
        } else {
            invalidate();
        }
    }

    public void setEnabled(boolean z) {
        super.setEnabled(z);
        this.mInputText.setEnabled(z);
    }

    public void scrollBy(int i, int i2) {
        int i3;
        int i4;
        int[] iArr = this.mSelectorIndices;
        if (!this.mWrapSelectorWheel && i2 > 0 && iArr[this.SELECTOR_MIDDLE_ITEM_INDEX] <= this.mMinValue) {
            i3 = this.mCurrentScrollOffset + i2;
            i4 = this.mInitialScrollOffset;
            if (i3 > i4) {
                this.mCurrentScrollOffset = i4;
                return;
            }
        }
        if (!this.mWrapSelectorWheel && i2 < 0 && iArr[this.SELECTOR_MIDDLE_ITEM_INDEX] >= this.mMaxValue) {
            i3 = this.mCurrentScrollOffset + i2;
            i4 = this.mInitialScrollOffset;
            if (i3 < i4) {
                this.mCurrentScrollOffset = i4;
                return;
            }
        }
        this.mCurrentScrollOffset += i2;
        while (true) {
            i2 = this.mCurrentScrollOffset;
            if (i2 - this.mInitialScrollOffset <= this.mSelectorTextGapHeight) {
                break;
            }
            this.mCurrentScrollOffset = i2 - this.mSelectorElementHeight;
            decrementSelectorIndices(iArr);
            setValueInternal(iArr[this.SELECTOR_MIDDLE_ITEM_INDEX], true);
            if (!this.mWrapSelectorWheel && iArr[this.SELECTOR_MIDDLE_ITEM_INDEX] <= this.mMinValue) {
                i2 = this.mCurrentScrollOffset;
                i3 = this.mInitialScrollOffset;
                if (i2 > i3) {
                    this.mCurrentScrollOffset = i3;
                }
            }
        }
        while (true) {
            i2 = this.mCurrentScrollOffset;
            if (i2 - this.mInitialScrollOffset < (-this.mSelectorTextGapHeight)) {
                this.mCurrentScrollOffset = i2 + this.mSelectorElementHeight;
                incrementSelectorIndices(iArr);
                setValueInternal(iArr[this.SELECTOR_MIDDLE_ITEM_INDEX], true);
                if (!this.mWrapSelectorWheel && iArr[this.SELECTOR_MIDDLE_ITEM_INDEX] >= this.mMaxValue) {
                    i2 = this.mCurrentScrollOffset;
                    i3 = this.mInitialScrollOffset;
                    if (i2 < i3) {
                        this.mCurrentScrollOffset = i3;
                    }
                }
            } else {
                return;
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public int computeVerticalScrollOffset() {
        return this.mCurrentScrollOffset;
    }

    /* Access modifiers changed, original: protected */
    public int computeVerticalScrollRange() {
        return ((this.mMaxValue - this.mMinValue) + 1) * this.mSelectorElementHeight;
    }

    /* Access modifiers changed, original: protected */
    public int computeVerticalScrollExtent() {
        return getHeight();
    }

    public int getSolidColor() {
        return this.mSolidColor;
    }

    public void setOnValueChangedListener(OnValueChangeListener onValueChangeListener) {
        this.mOnValueChangeListener = onValueChangeListener;
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.mOnScrollListener = onScrollListener;
    }

    public void setFormatter(Formatter formatter) {
        if (formatter != this.mFormatter) {
            this.mFormatter = formatter;
            initializeSelectorWheelIndices();
            updateInputTextView();
        }
    }

    public void setValue(int i) {
        setValueInternal(i, false);
    }

    public void setTextOffset(int i) {
        this.textOffset = i;
        invalidate();
    }

    private void tryComputeMaxWidth() {
        if (this.mComputeMaxWidth) {
            int i;
            String[] strArr = this.mDisplayedValues;
            int i2 = 0;
            if (strArr == null) {
                float f = 0.0f;
                for (i = 0; i <= 9; i++) {
                    float measureText = this.mSelectorWheelPaint.measureText(formatNumberWithLocale(i));
                    if (measureText > f) {
                        f = measureText;
                    }
                }
                for (i = this.mMaxValue; i > 0; i /= 10) {
                    i2++;
                }
                i = (int) (((float) i2) * f);
            } else {
                int length = strArr.length;
                int i3 = 0;
                while (i2 < length) {
                    float measureText2 = this.mSelectorWheelPaint.measureText(strArr[i2]);
                    if (measureText2 > ((float) i3)) {
                        i3 = (int) measureText2;
                    }
                    i2++;
                }
                i = i3;
            }
            i += this.mInputText.getPaddingLeft() + this.mInputText.getPaddingRight();
            if (this.mMaxWidth != i) {
                i2 = this.mMinWidth;
                if (i > i2) {
                    this.mMaxWidth = i;
                } else {
                    this.mMaxWidth = i2;
                }
                invalidate();
            }
        }
    }

    public boolean getWrapSelectorWheel() {
        return this.mWrapSelectorWheel;
    }

    public void setWrapSelectorWheel(boolean z) {
        Object obj = this.mMaxValue - this.mMinValue >= this.mSelectorIndices.length ? 1 : null;
        if ((!z || obj != null) && z != this.mWrapSelectorWheel) {
            this.mWrapSelectorWheel = z;
        }
    }

    public void setOnLongPressUpdateInterval(long j) {
        this.mLongPressUpdateInterval = j;
    }

    public int getValue() {
        return this.mValue;
    }

    public int getMinValue() {
        return this.mMinValue;
    }

    public void setMinValue(int i) {
        if (this.mMinValue != i) {
            if (i >= 0) {
                this.mMinValue = i;
                i = this.mMinValue;
                if (i > this.mValue) {
                    this.mValue = i;
                }
                setWrapSelectorWheel(this.mMaxValue - this.mMinValue > this.mSelectorIndices.length);
                initializeSelectorWheelIndices();
                updateInputTextView();
                tryComputeMaxWidth();
                invalidate();
                return;
            }
            throw new IllegalArgumentException("minValue must be >= 0");
        }
    }

    public int getMaxValue() {
        return this.mMaxValue;
    }

    public void setMaxValue(int i) {
        if (this.mMaxValue != i) {
            if (i >= 0) {
                this.mMaxValue = i;
                i = this.mMaxValue;
                if (i < this.mValue) {
                    this.mValue = i;
                }
                setWrapSelectorWheel(this.mMaxValue - this.mMinValue > this.mSelectorIndices.length);
                initializeSelectorWheelIndices();
                updateInputTextView();
                tryComputeMaxWidth();
                invalidate();
                return;
            }
            throw new IllegalArgumentException("maxValue must be >= 0");
        }
    }

    public String[] getDisplayedValues() {
        return this.mDisplayedValues;
    }

    public void setDisplayedValues(String[] strArr) {
        if (this.mDisplayedValues != strArr) {
            this.mDisplayedValues = strArr;
            updateInputTextView();
            initializeSelectorWheelIndices();
            tryComputeMaxWidth();
        }
    }

    /* Access modifiers changed, original: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeAllCallbacks();
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        float right = (float) (((getRight() - getLeft()) / 2) + this.textOffset);
        float f = (float) this.mCurrentScrollOffset;
        int[] iArr = this.mSelectorIndices;
        int i = 0;
        while (i < iArr.length) {
            String str = (String) this.mSelectorIndexToStringCache.get(iArr[i]);
            if (!(str == null || (i == this.SELECTOR_MIDDLE_ITEM_INDEX && this.mInputText.getVisibility() == 0))) {
                canvas.drawText(str, right, f, this.mSelectorWheelPaint);
            }
            f += (float) this.mSelectorElementHeight;
            i++;
        }
        int i2 = this.mTopSelectionDividerTop;
        Canvas canvas2 = canvas;
        canvas2.drawRect(0.0f, (float) i2, (float) getRight(), (float) (this.mSelectionDividerHeight + i2), this.mSelectionDivider);
        i2 = this.mBottomSelectionDividerBottom;
        canvas2.drawRect(0.0f, (float) (i2 - this.mSelectionDividerHeight), (float) getRight(), (float) i2, this.mSelectionDivider);
    }

    private int makeMeasureSpec(int i, int i2) {
        if (i2 == -1) {
            return i;
        }
        int size = MeasureSpec.getSize(i);
        int mode = MeasureSpec.getMode(i);
        if (mode == Integer.MIN_VALUE) {
            return MeasureSpec.makeMeasureSpec(Math.min(size, i2), NUM);
        }
        if (mode == 0) {
            return MeasureSpec.makeMeasureSpec(i2, NUM);
        }
        if (mode == NUM) {
            return i;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unknown measure mode: ");
        stringBuilder.append(mode);
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    private int resolveSizeAndStateRespectingMinSize(int i, int i2, int i3) {
        return i != -1 ? resolveSizeAndState(Math.max(i, i2), i3, 0) : i2;
    }

    public static int resolveSizeAndState(int i, int i2, int i3) {
        int mode = MeasureSpec.getMode(i2);
        i2 = MeasureSpec.getSize(i2);
        if (mode != Integer.MIN_VALUE) {
            if (mode != 0 && mode == NUM) {
                i = i2;
            }
        } else if (i2 < i) {
            i = 16777216 | i2;
        }
        return i | (-16777216 & i3);
    }

    private void initializeSelectorWheelIndices() {
        this.mSelectorIndexToStringCache.clear();
        int[] iArr = this.mSelectorIndices;
        int value = getValue();
        for (int i = 0; i < this.mSelectorIndices.length; i++) {
            int i2 = (i - this.SELECTOR_MIDDLE_ITEM_INDEX) + value;
            if (this.mWrapSelectorWheel) {
                i2 = getWrappedSelectorIndex(i2);
            }
            iArr[i] = i2;
            ensureCachedScrollSelectorValue(iArr[i]);
        }
    }

    private void setValueInternal(int i, boolean z) {
        if (this.mValue != i) {
            if (this.mWrapSelectorWheel) {
                i = getWrappedSelectorIndex(i);
            } else {
                i = Math.min(Math.max(i, this.mMinValue), this.mMaxValue);
            }
            int i2 = this.mValue;
            this.mValue = i;
            updateInputTextView();
            if (z) {
                notifyChange(i2, i);
            }
            initializeSelectorWheelIndices();
            invalidate();
        }
    }

    private void changeValueByOne(boolean z) {
        this.mInputText.setVisibility(4);
        if (!moveToFinalScrollerPosition(this.mFlingScroller)) {
            moveToFinalScrollerPosition(this.mAdjustScroller);
        }
        this.mPreviousScrollerY = 0;
        if (z) {
            this.mFlingScroller.startScroll(0, 0, 0, -this.mSelectorElementHeight, 300);
        } else {
            this.mFlingScroller.startScroll(0, 0, 0, this.mSelectorElementHeight, 300);
        }
        invalidate();
    }

    private void initializeSelectorWheel() {
        initializeSelectorWheelIndices();
        int[] iArr = this.mSelectorIndices;
        this.mSelectorTextGapHeight = (int) ((((float) ((getBottom() - getTop()) - (iArr.length * this.mTextSize))) / ((float) iArr.length)) + 0.5f);
        this.mSelectorElementHeight = this.mTextSize + this.mSelectorTextGapHeight;
        this.mInitialScrollOffset = (this.mInputText.getBaseline() + this.mInputText.getTop()) - (this.mSelectorElementHeight * this.SELECTOR_MIDDLE_ITEM_INDEX);
        this.mCurrentScrollOffset = this.mInitialScrollOffset;
        updateInputTextView();
    }

    private void initializeFadingEdges() {
        setVerticalFadingEdgeEnabled(true);
        setFadingEdgeLength(((getBottom() - getTop()) - this.mTextSize) / 2);
    }

    private void onScrollerFinished(Scroller scroller) {
        if (scroller == this.mFlingScroller) {
            if (!ensureScrollWheelAdjusted()) {
                updateInputTextView();
            }
            onScrollStateChange(0);
        } else if (this.mScrollState != 1) {
            updateInputTextView();
        }
    }

    private void onScrollStateChange(int i) {
        if (this.mScrollState != i) {
            this.mScrollState = i;
            OnScrollListener onScrollListener = this.mOnScrollListener;
            if (onScrollListener != null) {
                onScrollListener.onScrollStateChange(this, i);
            }
            if (i == 0) {
                AccessibilityManager accessibilityManager = (AccessibilityManager) getContext().getSystemService("accessibility");
                if (accessibilityManager.isTouchExplorationEnabled()) {
                    String[] strArr = this.mDisplayedValues;
                    Object formatNumber = strArr == null ? formatNumber(this.mValue) : strArr[this.mValue - this.mMinValue];
                    AccessibilityEvent obtain = AccessibilityEvent.obtain();
                    obtain.setEventType(16384);
                    obtain.getText().add(formatNumber);
                    accessibilityManager.sendAccessibilityEvent(obtain);
                }
            }
        }
    }

    private void fling(int i) {
        this.mPreviousScrollerY = 0;
        if (i > 0) {
            this.mFlingScroller.fling(0, 0, 0, i, 0, 0, 0, Integer.MAX_VALUE);
        } else {
            this.mFlingScroller.fling(0, Integer.MAX_VALUE, 0, i, 0, 0, 0, Integer.MAX_VALUE);
        }
        invalidate();
    }

    private int getWrappedSelectorIndex(int i) {
        int i2 = this.mMaxValue;
        int i3;
        if (i > i2) {
            i3 = this.mMinValue;
            return (i3 + ((i - i2) % (i2 - i3))) - 1;
        }
        i3 = this.mMinValue;
        return i < i3 ? (i2 - ((i3 - i) % (i2 - i3))) + 1 : i;
    }

    private void incrementSelectorIndices(int[] iArr) {
        System.arraycopy(iArr, 1, iArr, 0, iArr.length - 1);
        int i = iArr[iArr.length - 2] + 1;
        if (this.mWrapSelectorWheel && i > this.mMaxValue) {
            i = this.mMinValue;
        }
        iArr[iArr.length - 1] = i;
        ensureCachedScrollSelectorValue(i);
    }

    private void decrementSelectorIndices(int[] iArr) {
        System.arraycopy(iArr, 0, iArr, 1, iArr.length - 1);
        int i = iArr[1] - 1;
        if (this.mWrapSelectorWheel && i < this.mMinValue) {
            i = this.mMaxValue;
        }
        iArr[0] = i;
        ensureCachedScrollSelectorValue(i);
    }

    private void ensureCachedScrollSelectorValue(int i) {
        SparseArray sparseArray = this.mSelectorIndexToStringCache;
        if (((String) sparseArray.get(i)) == null) {
            Object obj;
            int i2 = this.mMinValue;
            if (i < i2 || i > this.mMaxValue) {
                obj = "";
            } else {
                String[] strArr = this.mDisplayedValues;
                obj = strArr != null ? strArr[i - i2] : formatNumber(i);
            }
            sparseArray.put(i, obj);
        }
    }

    private String formatNumber(int i) {
        Formatter formatter = this.mFormatter;
        return formatter != null ? formatter.format(i) : formatNumberWithLocale(i);
    }

    private boolean updateInputTextView() {
        String[] strArr = this.mDisplayedValues;
        CharSequence formatNumber = strArr == null ? formatNumber(this.mValue) : strArr[this.mValue - this.mMinValue];
        if (TextUtils.isEmpty(formatNumber) || formatNumber.equals(this.mInputText.getText().toString())) {
            return false;
        }
        this.mInputText.setText(formatNumber);
        return true;
    }

    private void notifyChange(int i, int i2) {
        OnValueChangeListener onValueChangeListener = this.mOnValueChangeListener;
        if (onValueChangeListener != null) {
            onValueChangeListener.onValueChange(this, i, this.mValue);
        }
    }

    private void postChangeCurrentByOneFromLongPress(boolean z, long j) {
        ChangeCurrentByOneFromLongPressCommand changeCurrentByOneFromLongPressCommand = this.mChangeCurrentByOneFromLongPressCommand;
        if (changeCurrentByOneFromLongPressCommand == null) {
            this.mChangeCurrentByOneFromLongPressCommand = new ChangeCurrentByOneFromLongPressCommand();
        } else {
            removeCallbacks(changeCurrentByOneFromLongPressCommand);
        }
        this.mChangeCurrentByOneFromLongPressCommand.setStep(z);
        postDelayed(this.mChangeCurrentByOneFromLongPressCommand, j);
    }

    private void removeChangeCurrentByOneFromLongPress() {
        ChangeCurrentByOneFromLongPressCommand changeCurrentByOneFromLongPressCommand = this.mChangeCurrentByOneFromLongPressCommand;
        if (changeCurrentByOneFromLongPressCommand != null) {
            removeCallbacks(changeCurrentByOneFromLongPressCommand);
        }
    }

    private void removeAllCallbacks() {
        ChangeCurrentByOneFromLongPressCommand changeCurrentByOneFromLongPressCommand = this.mChangeCurrentByOneFromLongPressCommand;
        if (changeCurrentByOneFromLongPressCommand != null) {
            removeCallbacks(changeCurrentByOneFromLongPressCommand);
        }
        this.mPressedStateHelper.cancel();
    }

    private int getSelectedPos(String str) {
        if (this.mDisplayedValues == null) {
            try {
                return Integer.parseInt(str);
            } catch (NumberFormatException unused) {
                return this.mMinValue;
            }
        }
        for (int i = 0; i < this.mDisplayedValues.length; i++) {
            str = str.toLowerCase();
            if (this.mDisplayedValues[i].toLowerCase().startsWith(str)) {
                return this.mMinValue + i;
            }
        }
        return Integer.parseInt(str);
    }

    private boolean ensureScrollWheelAdjusted() {
        int i = this.mInitialScrollOffset - this.mCurrentScrollOffset;
        if (i == 0) {
            return false;
        }
        this.mPreviousScrollerY = 0;
        int abs = Math.abs(i);
        int i2 = this.mSelectorElementHeight;
        if (abs > i2 / 2) {
            if (i > 0) {
                i2 = -i2;
            }
            i += i2;
        }
        this.mAdjustScroller.startScroll(0, 0, 0, i, 800);
        invalidate();
        return true;
    }

    private static String formatNumberWithLocale(int i) {
        return String.format(Locale.getDefault(), "%d", new Object[]{Integer.valueOf(i)});
    }
}
