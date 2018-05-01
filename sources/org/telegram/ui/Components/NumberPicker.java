package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import java.util.Locale;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.ActionBar.Theme;

public class NumberPicker extends LinearLayout {
    private static final int DEFAULT_LAYOUT_RESOURCE_ID = 0;
    private static final long DEFAULT_LONG_PRESS_UPDATE_INTERVAL = 300;
    private static final int SELECTOR_ADJUSTMENT_DURATION_MILLIS = 800;
    private static final int SELECTOR_MAX_FLING_VELOCITY_ADJUSTMENT = 8;
    private static final int SELECTOR_MIDDLE_ITEM_INDEX = 1;
    private static final int SELECTOR_WHEEL_ITEM_COUNT = 3;
    private static final int SIZE_UNSPECIFIED = -1;
    private static final int SNAP_SCROLL_DURATION = 300;
    private static final float TOP_AND_BOTTOM_FADING_EDGE_STRENGTH = 0.9f;
    private static final int UNSCALED_DEFAULT_SELECTION_DIVIDERS_DISTANCE = 48;
    private static final int UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT = 2;
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
    private int mInitialScrollOffset;
    private TextView mInputText;
    private long mLastDownEventTime;
    private float mLastDownEventY;
    private float mLastDownOrMoveEventY;
    private int mLastHandledDownDpadKeyCode;
    private int mLastHoveredChildVirtualViewId;
    private long mLongPressUpdateInterval;
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
    private int mScrollState;
    private Paint mSelectionDivider;
    private int mSelectionDividerHeight;
    private int mSelectionDividersDistance;
    private int mSelectorElementHeight;
    private final SparseArray<String> mSelectorIndexToStringCache;
    private final int[] mSelectorIndices;
    private int mSelectorTextGapHeight;
    private Paint mSelectorWheelPaint;
    private int mSolidColor;
    private int mTextSize;
    private int mTopSelectionDividerTop;
    private int mTouchSlop;
    private int mValue;
    private VelocityTracker mVelocityTracker;
    private boolean mWrapSelectorWheel;

    class ChangeCurrentByOneFromLongPressCommand implements Runnable {
        private boolean mIncrement;

        ChangeCurrentByOneFromLongPressCommand() {
        }

        private void setStep(boolean z) {
            this.mIncrement = z;
        }

        public void run() {
            NumberPicker.this.changeValueByOne(this.mIncrement);
            NumberPicker.this.postDelayed(this, NumberPicker.this.mLongPressUpdateInterval);
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
            this.mMode = 0;
            this.mManagedButton = 0;
            NumberPicker.this.removeCallbacks(this);
            if (NumberPicker.this.mIncrementVirtualButtonPressed) {
                NumberPicker.this.mIncrementVirtualButtonPressed = false;
                NumberPicker.this.invalidate(0, NumberPicker.this.mBottomSelectionDividerBottom, NumberPicker.this.getRight(), NumberPicker.this.getBottom());
            }
            NumberPicker.this.mDecrementVirtualButtonPressed = false;
            if (NumberPicker.this.mDecrementVirtualButtonPressed) {
                NumberPicker.this.invalidate(0, 0, NumberPicker.this.getRight(), NumberPicker.this.mTopSelectionDividerTop);
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
            switch (this.mMode) {
                case 1:
                    switch (this.mManagedButton) {
                        case 1:
                            NumberPicker.this.mIncrementVirtualButtonPressed = true;
                            NumberPicker.this.invalidate(0, NumberPicker.this.mBottomSelectionDividerBottom, NumberPicker.this.getRight(), NumberPicker.this.getBottom());
                            return;
                        case 2:
                            NumberPicker.this.mDecrementVirtualButtonPressed = true;
                            NumberPicker.this.invalidate(0, 0, NumberPicker.this.getRight(), NumberPicker.this.mTopSelectionDividerTop);
                            return;
                        default:
                            return;
                    }
                case 2:
                    switch (this.mManagedButton) {
                        case 1:
                            if (!NumberPicker.this.mIncrementVirtualButtonPressed) {
                                NumberPicker.this.postDelayed(this, (long) ViewConfiguration.getPressedStateDuration());
                            }
                            NumberPicker.this.mIncrementVirtualButtonPressed = true ^ NumberPicker.this.mIncrementVirtualButtonPressed;
                            NumberPicker.this.invalidate(0, NumberPicker.this.mBottomSelectionDividerBottom, NumberPicker.this.getRight(), NumberPicker.this.getBottom());
                            return;
                        case 2:
                            if (!NumberPicker.this.mDecrementVirtualButtonPressed) {
                                NumberPicker.this.postDelayed(this, (long) ViewConfiguration.getPressedStateDuration());
                            }
                            NumberPicker.this.mDecrementVirtualButtonPressed = true ^ NumberPicker.this.mDecrementVirtualButtonPressed;
                            NumberPicker.this.invalidate(0, 0, NumberPicker.this.getRight(), NumberPicker.this.mTopSelectionDividerTop);
                            return;
                        default:
                            return;
                    }
                default:
                    return;
            }
        }
    }

    protected float getBottomFadingEdgeStrength() {
        return TOP_AND_BOTTOM_FADING_EDGE_STRENGTH;
    }

    protected float getTopFadingEdgeStrength() {
        return TOP_AND_BOTTOM_FADING_EDGE_STRENGTH;
    }

    private void init() {
        this.mSolidColor = 0;
        this.mSelectionDivider = new Paint();
        this.mSelectionDivider.setColor(Theme.getColor(Theme.key_dialogButton));
        this.mSelectionDividerHeight = (int) TypedValue.applyDimension(1, 2.0f, getResources().getDisplayMetrics());
        this.mSelectionDividersDistance = (int) TypedValue.applyDimension(1, 48.0f, getResources().getDisplayMetrics());
        this.mMinHeight = -1;
        this.mMaxHeight = (int) TypedValue.applyDimension(1, 180.0f, getResources().getDisplayMetrics());
        if (this.mMinHeight == -1 || this.mMaxHeight == -1 || this.mMinHeight <= this.mMaxHeight) {
            this.mMinWidth = (int) TypedValue.applyDimension(1, 64.0f, getResources().getDisplayMetrics());
            this.mMaxWidth = -1;
            if (this.mMinWidth == -1 || this.mMaxWidth == -1 || this.mMinWidth <= this.mMaxWidth) {
                this.mComputeMaxWidth = this.mMaxWidth == -1;
                this.mPressedStateHelper = new PressedStateHelper();
                setWillNotDraw(false);
                this.mInputText = new TextView(getContext());
                this.mInputText.setGravity(17);
                this.mInputText.setSingleLine(true);
                this.mInputText.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                this.mInputText.setBackgroundResource(0);
                this.mInputText.setTextSize(1, 18.0f);
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
                paint.setColor(this.mInputText.getTextColors().getColorForState(ENABLED_STATE_SET, -1));
                this.mSelectorWheelPaint = paint;
                this.mFlingScroller = new Scroller(getContext(), null, true);
                this.mAdjustScroller = new Scroller(getContext(), new DecelerateInterpolator(2.5f));
                updateInputTextView();
                return;
            }
            throw new IllegalArgumentException("minWidth > maxWidth");
        }
        throw new IllegalArgumentException("minHeight > maxHeight");
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
        this.mLongPressUpdateInterval = DEFAULT_LONG_PRESS_UPDATE_INTERVAL;
        this.mSelectorIndexToStringCache = new SparseArray();
        this.mSelectorIndices = new int[3];
        this.mInitialScrollOffset = Integer.MIN_VALUE;
        this.mScrollState = null;
        this.mLastHandledDownDpadKeyCode = -1;
        init();
    }

    public NumberPicker(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mLongPressUpdateInterval = SNAP_SCROLL_DURATION;
        this.mSelectorIndexToStringCache = new SparseArray();
        this.mSelectorIndices = new int[3];
        this.mInitialScrollOffset = Integer.MIN_VALUE;
        this.mScrollState = null;
        this.mLastHandledDownDpadKeyCode = -1;
        init();
    }

    public NumberPicker(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mLongPressUpdateInterval = SNAP_SCROLL_DURATION;
        this.mSelectorIndexToStringCache = new SparseArray();
        this.mSelectorIndices = new int[3];
        this.mInitialScrollOffset = Integer.MIN_VALUE;
        this.mScrollState = null;
        this.mLastHandledDownDpadKeyCode = -1;
        init();
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
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
            this.mTopSelectionDividerTop = ((getHeight() - this.mSelectionDividersDistance) / true) - this.mSelectionDividerHeight;
            this.mBottomSelectionDividerBottom = (this.mTopSelectionDividerTop + (2 * this.mSelectionDividerHeight)) + this.mSelectionDividersDistance;
        }
    }

    protected void onMeasure(int i, int i2) {
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
        if (Math.abs(i) > this.mSelectorElementHeight / 2) {
            if (i > 0) {
                i -= this.mSelectorElementHeight;
            } else {
                i += this.mSelectorElementHeight;
            }
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
        if (this.mLastDownEventY < ((float) this.mTopSelectionDividerTop)) {
            if (this.mScrollState == null) {
                this.mPressedStateHelper.buttonPressDelayed(2);
            }
        } else if (this.mLastDownEventY > ((float) this.mBottomSelectionDividerBottom) && this.mScrollState == null) {
            this.mPressedStateHelper.buttonPressDelayed(1);
        }
        getParent().requestDisallowInterceptTouchEvent(true);
        if (this.mFlingScroller.isFinished() == null) {
            this.mFlingScroller.forceFinished(true);
            this.mAdjustScroller.forceFinished(true);
            onScrollStateChange(0);
        } else if (this.mAdjustScroller.isFinished() == null) {
            this.mFlingScroller.forceFinished(true);
            this.mAdjustScroller.forceFinished(true);
        } else if (this.mLastDownEventY < ((float) this.mTopSelectionDividerTop)) {
            postChangeCurrentByOneFromLongPress(false, (long) ViewConfiguration.getLongPressTimeout());
        } else if (this.mLastDownEventY > ((float) this.mBottomSelectionDividerBottom)) {
            postChangeCurrentByOneFromLongPress(true, (long) ViewConfiguration.getLongPressTimeout());
        }
        return true;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!isEnabled()) {
            return false;
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(motionEvent);
        switch (motionEvent.getActionMasked()) {
            case 1:
                removeChangeCurrentByOneFromLongPress();
                this.mPressedStateHelper.cancel();
                VelocityTracker velocityTracker = this.mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, (float) this.mMaximumFlingVelocity);
                int yVelocity = (int) velocityTracker.getYVelocity();
                if (Math.abs(yVelocity) > this.mMinimumFlingVelocity) {
                    fling(yVelocity);
                    onScrollStateChange(2);
                } else {
                    yVelocity = (int) motionEvent.getY();
                    long eventTime = motionEvent.getEventTime() - this.mLastDownEventTime;
                    if (((int) Math.abs(((float) yVelocity) - this.mLastDownEventY)) > this.mTouchSlop || eventTime >= ((long) ViewConfiguration.getTapTimeout())) {
                        ensureScrollWheelAdjusted();
                    } else {
                        yVelocity = (yVelocity / this.mSelectorElementHeight) - 1;
                        if (yVelocity > 0) {
                            changeValueByOne(true);
                            this.mPressedStateHelper.buttonTapped(1);
                        } else if (yVelocity < 0) {
                            changeValueByOne(false);
                            this.mPressedStateHelper.buttonTapped(2);
                        }
                    }
                    onScrollStateChange(0);
                }
                this.mVelocityTracker.recycle();
                this.mVelocityTracker = null;
                break;
            case 2:
                if (!this.mIngonreMoveEvents) {
                    motionEvent = motionEvent.getY();
                    if (this.mScrollState == 1) {
                        scrollBy(0, (int) (motionEvent - this.mLastDownOrMoveEventY));
                        invalidate();
                    } else if (((int) Math.abs(motionEvent - this.mLastDownEventY)) > this.mTouchSlop) {
                        removeAllCallbacks();
                        onScrollStateChange(1);
                    }
                    this.mLastDownOrMoveEventY = motionEvent;
                    break;
                }
                break;
            default:
                break;
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

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        int keyCode = keyEvent.getKeyCode();
        if (keyCode != 23 && keyCode != 66) {
            switch (keyCode) {
                case 19:
                case 20:
                    switch (keyEvent.getAction()) {
                        case 0:
                            if (!this.mWrapSelectorWheel) {
                                if (keyCode != 20) {
                                    if (getValue() > getMinValue()) {
                                        requestFocus();
                                        this.mLastHandledDownDpadKeyCode = keyCode;
                                        removeAllCallbacks();
                                        if (this.mFlingScroller.isFinished() != null) {
                                            changeValueByOne(keyCode == 20 ? 1 : null);
                                        }
                                        return true;
                                    }
                                }
                            }
                            break;
                        case 1:
                            if (this.mLastHandledDownDpadKeyCode == keyCode) {
                                this.mLastHandledDownDpadKeyCode = -1;
                                return true;
                            }
                            break;
                        default:
                            break;
                    }
                default:
                    break;
            }
        }
        removeAllCallbacks();
        return super.dispatchKeyEvent(keyEvent);
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
        i = this.mSelectorIndices;
        if (!this.mWrapSelectorWheel && i2 > 0 && i[1] <= this.mMinValue) {
            this.mCurrentScrollOffset = this.mInitialScrollOffset;
        } else if (this.mWrapSelectorWheel || i2 >= 0 || i[1] < this.mMaxValue) {
            this.mCurrentScrollOffset += i2;
            while (this.mCurrentScrollOffset - this.mInitialScrollOffset > this.mSelectorTextGapHeight) {
                this.mCurrentScrollOffset -= this.mSelectorElementHeight;
                decrementSelectorIndices(i);
                setValueInternal(i[1], true);
                if (this.mWrapSelectorWheel == 0 && i[1] <= this.mMinValue) {
                    this.mCurrentScrollOffset = this.mInitialScrollOffset;
                }
            }
            while (this.mCurrentScrollOffset - this.mInitialScrollOffset < (-this.mSelectorTextGapHeight)) {
                this.mCurrentScrollOffset += this.mSelectorElementHeight;
                incrementSelectorIndices(i);
                setValueInternal(i[1], true);
                if (this.mWrapSelectorWheel == 0 && i[1] >= this.mMaxValue) {
                    this.mCurrentScrollOffset = this.mInitialScrollOffset;
                }
            }
        } else {
            this.mCurrentScrollOffset = this.mInitialScrollOffset;
        }
    }

    protected int computeVerticalScrollOffset() {
        return this.mCurrentScrollOffset;
    }

    protected int computeVerticalScrollRange() {
        return ((this.mMaxValue - this.mMinValue) + 1) * this.mSelectorElementHeight;
    }

    protected int computeVerticalScrollExtent() {
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

    private void tryComputeMaxWidth() {
        if (this.mComputeMaxWidth) {
            int i;
            int i2 = 0;
            if (this.mDisplayedValues == null) {
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
                String[] strArr = this.mDisplayedValues;
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
                if (i > this.mMinWidth) {
                    this.mMaxWidth = i;
                } else {
                    this.mMaxWidth = this.mMinWidth;
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
            if (i < 0) {
                throw new IllegalArgumentException("minValue must be >= 0");
            }
            this.mMinValue = i;
            if (this.mMinValue > this.mValue) {
                this.mValue = this.mMinValue;
            }
            setWrapSelectorWheel(this.mMaxValue - this.mMinValue > this.mSelectorIndices.length ? 1 : 0);
            initializeSelectorWheelIndices();
            updateInputTextView();
            tryComputeMaxWidth();
            invalidate();
        }
    }

    public int getMaxValue() {
        return this.mMaxValue;
    }

    public void setMaxValue(int i) {
        if (this.mMaxValue != i) {
            if (i < 0) {
                throw new IllegalArgumentException("maxValue must be >= 0");
            }
            this.mMaxValue = i;
            if (this.mMaxValue < this.mValue) {
                this.mValue = this.mMaxValue;
            }
            setWrapSelectorWheel(this.mMaxValue - this.mMinValue > this.mSelectorIndices.length ? 1 : 0);
            initializeSelectorWheelIndices();
            updateInputTextView();
            tryComputeMaxWidth();
            invalidate();
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

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeAllCallbacks();
    }

    protected void onDraw(Canvas canvas) {
        float right = (float) ((getRight() - getLeft()) / 2);
        float f = (float) this.mCurrentScrollOffset;
        int[] iArr = this.mSelectorIndices;
        for (int i = 0; i < iArr.length; i++) {
            String str = (String) this.mSelectorIndexToStringCache.get(iArr[i]);
            if (i != 1 || this.mInputText.getVisibility() != 0) {
                canvas.drawText(str, right, f, this.mSelectorWheelPaint);
            }
            f += (float) this.mSelectorElementHeight;
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
        i2 = new StringBuilder();
        i2.append("Unknown measure mode: ");
        i2.append(mode);
        throw new IllegalArgumentException(i2.toString());
    }

    private int resolveSizeAndStateRespectingMinSize(int i, int i2, int i3) {
        return i != -1 ? resolveSizeAndState(Math.max(i, i2), i3, 0) : i2;
    }

    public static int resolveSizeAndState(int i, int i2, int i3) {
        int mode = MeasureSpec.getMode(i2);
        i2 = MeasureSpec.getSize(i2);
        if (mode != Integer.MIN_VALUE) {
            if (mode != 0) {
                if (mode == NUM) {
                    i = i2;
                }
            }
        } else if (i2 < i) {
            i = 16777216 | i2;
        }
        return i | (Theme.ACTION_BAR_VIDEO_EDIT_COLOR & i3);
    }

    private void initializeSelectorWheelIndices() {
        this.mSelectorIndexToStringCache.clear();
        int[] iArr = this.mSelectorIndices;
        int value = getValue();
        for (int i = 0; i < this.mSelectorIndices.length; i++) {
            int i2 = (i - 1) + value;
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
            this.mFlingScroller.startScroll(0, 0, 0, -this.mSelectorElementHeight, SNAP_SCROLL_DURATION);
        } else {
            this.mFlingScroller.startScroll(0, 0, 0, this.mSelectorElementHeight, SNAP_SCROLL_DURATION);
        }
        invalidate();
    }

    private void initializeSelectorWheel() {
        initializeSelectorWheelIndices();
        int[] iArr = this.mSelectorIndices;
        this.mSelectorTextGapHeight = (int) ((((float) ((getBottom() - getTop()) - (iArr.length * this.mTextSize))) / ((float) iArr.length)) + 0.5f);
        this.mSelectorElementHeight = this.mTextSize + this.mSelectorTextGapHeight;
        this.mInitialScrollOffset = (this.mInputText.getBaseline() + this.mInputText.getTop()) - (this.mSelectorElementHeight * 1);
        this.mCurrentScrollOffset = this.mInitialScrollOffset;
        updateInputTextView();
    }

    private void initializeFadingEdges() {
        setVerticalFadingEdgeEnabled(true);
        setFadingEdgeLength(((getBottom() - getTop()) - this.mTextSize) / 2);
    }

    private void onScrollerFinished(Scroller scroller) {
        if (scroller == this.mFlingScroller) {
            if (ensureScrollWheelAdjusted() == null) {
                updateInputTextView();
            }
            onScrollStateChange(null);
        } else if (this.mScrollState != 1) {
            updateInputTextView();
        }
    }

    private void onScrollStateChange(int i) {
        if (this.mScrollState != i) {
            this.mScrollState = i;
            if (this.mOnScrollListener != null) {
                this.mOnScrollListener.onScrollStateChange(this, i);
            }
        }
    }

    private void fling(int i) {
        this.mPreviousScrollerY = 0;
        if (i > 0) {
            this.mFlingScroller.fling(0, 0, 0, i, 0, 0, 0, ConnectionsManager.DEFAULT_DATACENTER_ID);
        } else {
            this.mFlingScroller.fling(0, ConnectionsManager.DEFAULT_DATACENTER_ID, 0, i, 0, 0, 0, ConnectionsManager.DEFAULT_DATACENTER_ID);
        }
        invalidate();
    }

    private int getWrappedSelectorIndex(int i) {
        if (i > this.mMaxValue) {
            return (this.mMinValue + ((i - this.mMaxValue) % (this.mMaxValue - this.mMinValue))) - 1;
        }
        return i < this.mMinValue ? (this.mMaxValue - ((this.mMinValue - i) % (this.mMaxValue - this.mMinValue))) + 1 : i;
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
            if (i >= this.mMinValue) {
                if (i <= this.mMaxValue) {
                    if (this.mDisplayedValues != null) {
                        obj = this.mDisplayedValues[i - this.mMinValue];
                    } else {
                        obj = formatNumber(i);
                    }
                    sparseArray.put(i, obj);
                }
            }
            obj = TtmlNode.ANONYMOUS_REGION_ID;
            sparseArray.put(i, obj);
        }
    }

    private String formatNumber(int i) {
        return this.mFormatter != null ? this.mFormatter.format(i) : formatNumberWithLocale(i);
    }

    private boolean updateInputTextView() {
        CharSequence formatNumber = this.mDisplayedValues == null ? formatNumber(this.mValue) : this.mDisplayedValues[this.mValue - this.mMinValue];
        if (TextUtils.isEmpty(formatNumber) || formatNumber.equals(this.mInputText.getText().toString())) {
            return false;
        }
        this.mInputText.setText(formatNumber);
        return true;
    }

    private void notifyChange(int i, int i2) {
        if (this.mOnValueChangeListener != 0) {
            this.mOnValueChangeListener.onValueChange(this, i, this.mValue);
        }
    }

    private void postChangeCurrentByOneFromLongPress(boolean z, long j) {
        if (this.mChangeCurrentByOneFromLongPressCommand == null) {
            this.mChangeCurrentByOneFromLongPressCommand = new ChangeCurrentByOneFromLongPressCommand();
        } else {
            removeCallbacks(this.mChangeCurrentByOneFromLongPressCommand);
        }
        this.mChangeCurrentByOneFromLongPressCommand.setStep(z);
        postDelayed(this.mChangeCurrentByOneFromLongPressCommand, j);
    }

    private void removeChangeCurrentByOneFromLongPress() {
        if (this.mChangeCurrentByOneFromLongPressCommand != null) {
            removeCallbacks(this.mChangeCurrentByOneFromLongPressCommand);
        }
    }

    private void removeAllCallbacks() {
        if (this.mChangeCurrentByOneFromLongPressCommand != null) {
            removeCallbacks(this.mChangeCurrentByOneFromLongPressCommand);
        }
        this.mPressedStateHelper.cancel();
    }

    private int getSelectedPos(java.lang.String r3) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r2 = this;
        r0 = r2.mDisplayedValues;
        if (r0 != 0) goto L_0x0009;
    L_0x0004:
        r3 = java.lang.Integer.parseInt(r3);	 Catch:{ NumberFormatException -> 0x002d }
        return r3;
    L_0x0009:
        r0 = 0;
    L_0x000a:
        r1 = r2.mDisplayedValues;
        r1 = r1.length;
        if (r0 >= r1) goto L_0x0028;
    L_0x000f:
        r3 = r3.toLowerCase();
        r1 = r2.mDisplayedValues;
        r1 = r1[r0];
        r1 = r1.toLowerCase();
        r1 = r1.startsWith(r3);
        if (r1 == 0) goto L_0x0025;
    L_0x0021:
        r3 = r2.mMinValue;
        r3 = r3 + r0;
        return r3;
    L_0x0025:
        r0 = r0 + 1;
        goto L_0x000a;
    L_0x0028:
        r3 = java.lang.Integer.parseInt(r3);	 Catch:{ NumberFormatException -> 0x002d }
        return r3;
    L_0x002d:
        r3 = r2.mMinValue;
        return r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.NumberPicker.getSelectedPos(java.lang.String):int");
    }

    private boolean ensureScrollWheelAdjusted() {
        int i = this.mInitialScrollOffset - this.mCurrentScrollOffset;
        if (i == 0) {
            return false;
        }
        this.mPreviousScrollerY = 0;
        if (Math.abs(i) > this.mSelectorElementHeight / 2) {
            i += i > 0 ? -this.mSelectorElementHeight : this.mSelectorElementHeight;
        }
        this.mAdjustScroller.startScroll(0, 0, 0, i, SELECTOR_ADJUSTMENT_DURATION_MILLIS);
        invalidate();
        return true;
    }

    private static String formatNumberWithLocale(int i) {
        return String.format(Locale.getDefault(), "%d", new Object[]{Integer.valueOf(i)});
    }
}
