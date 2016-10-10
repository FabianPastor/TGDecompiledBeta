package org.telegram.ui.Components;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import java.util.Locale;

public class NumberPicker
  extends LinearLayout
{
  private static final int DEFAULT_LAYOUT_RESOURCE_ID = 0;
  private static final long DEFAULT_LONG_PRESS_UPDATE_INTERVAL = 300L;
  private static final int SELECTOR_ADJUSTMENT_DURATION_MILLIS = 800;
  private static final int SELECTOR_MAX_FLING_VELOCITY_ADJUSTMENT = 8;
  private static final int SELECTOR_MIDDLE_ITEM_INDEX = 1;
  private static final int SELECTOR_WHEEL_ITEM_COUNT = 3;
  private static final int SIZE_UNSPECIFIED = -1;
  private static final int SNAP_SCROLL_DURATION = 300;
  private static final float TOP_AND_BOTTOM_FADING_EDGE_STRENGTH = 0.9F;
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
  private int mInitialScrollOffset = Integer.MIN_VALUE;
  private TextView mInputText;
  private long mLastDownEventTime;
  private float mLastDownEventY;
  private float mLastDownOrMoveEventY;
  private int mLastHandledDownDpadKeyCode = -1;
  private int mLastHoveredChildVirtualViewId;
  private long mLongPressUpdateInterval = 300L;
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
  private Drawable mSelectionDivider;
  private int mSelectionDividerHeight;
  private int mSelectionDividersDistance;
  private int mSelectorElementHeight;
  private final SparseArray<String> mSelectorIndexToStringCache = new SparseArray();
  private final int[] mSelectorIndices = new int[3];
  private int mSelectorTextGapHeight;
  private Paint mSelectorWheelPaint;
  private int mSolidColor;
  private int mTextSize;
  private int mTopSelectionDividerTop;
  private int mTouchSlop;
  private int mValue;
  private VelocityTracker mVelocityTracker;
  private Drawable mVirtualButtonPressedDrawable;
  private boolean mWrapSelectorWheel;
  
  public NumberPicker(Context paramContext)
  {
    super(paramContext);
    init();
  }
  
  public NumberPicker(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init();
  }
  
  public NumberPicker(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    init();
  }
  
  private void changeValueByOne(boolean paramBoolean)
  {
    this.mInputText.setVisibility(4);
    if (!moveToFinalScrollerPosition(this.mFlingScroller)) {
      moveToFinalScrollerPosition(this.mAdjustScroller);
    }
    this.mPreviousScrollerY = 0;
    if (paramBoolean) {
      this.mFlingScroller.startScroll(0, 0, 0, -this.mSelectorElementHeight, 300);
    }
    for (;;)
    {
      invalidate();
      return;
      this.mFlingScroller.startScroll(0, 0, 0, this.mSelectorElementHeight, 300);
    }
  }
  
  private void decrementSelectorIndices(int[] paramArrayOfInt)
  {
    System.arraycopy(paramArrayOfInt, 0, paramArrayOfInt, 1, paramArrayOfInt.length - 1);
    int j = paramArrayOfInt[1] - 1;
    int i = j;
    if (this.mWrapSelectorWheel)
    {
      i = j;
      if (j < this.mMinValue) {
        i = this.mMaxValue;
      }
    }
    paramArrayOfInt[0] = i;
    ensureCachedScrollSelectorValue(i);
  }
  
  private void ensureCachedScrollSelectorValue(int paramInt)
  {
    SparseArray localSparseArray = this.mSelectorIndexToStringCache;
    if ((String)localSparseArray.get(paramInt) != null) {
      return;
    }
    String str;
    if ((paramInt < this.mMinValue) || (paramInt > this.mMaxValue)) {
      str = "";
    }
    for (;;)
    {
      localSparseArray.put(paramInt, str);
      return;
      if (this.mDisplayedValues != null)
      {
        int i = this.mMinValue;
        str = this.mDisplayedValues[(paramInt - i)];
      }
      else
      {
        str = formatNumber(paramInt);
      }
    }
  }
  
  private boolean ensureScrollWheelAdjusted()
  {
    boolean bool = false;
    int j = this.mInitialScrollOffset - this.mCurrentScrollOffset;
    if (j != 0)
    {
      this.mPreviousScrollerY = 0;
      i = j;
      if (Math.abs(j) > this.mSelectorElementHeight / 2) {
        if (j <= 0) {
          break label72;
        }
      }
    }
    label72:
    for (int i = -this.mSelectorElementHeight;; i = this.mSelectorElementHeight)
    {
      i = j + i;
      this.mAdjustScroller.startScroll(0, 0, 0, i, 800);
      invalidate();
      bool = true;
      return bool;
    }
  }
  
  private void fling(int paramInt)
  {
    this.mPreviousScrollerY = 0;
    if (paramInt > 0) {
      this.mFlingScroller.fling(0, 0, 0, paramInt, 0, 0, 0, Integer.MAX_VALUE);
    }
    for (;;)
    {
      invalidate();
      return;
      this.mFlingScroller.fling(0, Integer.MAX_VALUE, 0, paramInt, 0, 0, 0, Integer.MAX_VALUE);
    }
  }
  
  private String formatNumber(int paramInt)
  {
    if (this.mFormatter != null) {
      return this.mFormatter.format(paramInt);
    }
    return formatNumberWithLocale(paramInt);
  }
  
  private static String formatNumberWithLocale(int paramInt)
  {
    return String.format(Locale.getDefault(), "%d", new Object[] { Integer.valueOf(paramInt) });
  }
  
  private int getSelectedPos(String paramString)
  {
    if (this.mDisplayedValues == null) {}
    try
    {
      i = Integer.parseInt(paramString);
      return i;
    }
    catch (NumberFormatException paramString)
    {
      try
      {
        int i = Integer.parseInt(paramString);
        return i;
      }
      catch (NumberFormatException paramString)
      {
        for (;;) {}
      }
      paramString = paramString;
    }
    i = 0;
    while (i < this.mDisplayedValues.length)
    {
      paramString = paramString.toLowerCase();
      if (this.mDisplayedValues[i].toLowerCase().startsWith(paramString)) {
        return this.mMinValue + i;
      }
      i += 1;
    }
    return this.mMinValue;
  }
  
  private int getWrappedSelectorIndex(int paramInt)
  {
    int i;
    if (paramInt > this.mMaxValue) {
      i = this.mMinValue + (paramInt - this.mMaxValue) % (this.mMaxValue - this.mMinValue) - 1;
    }
    do
    {
      return i;
      i = paramInt;
    } while (paramInt >= this.mMinValue);
    return this.mMaxValue - (this.mMinValue - paramInt) % (this.mMaxValue - this.mMinValue) + 1;
  }
  
  private void incrementSelectorIndices(int[] paramArrayOfInt)
  {
    System.arraycopy(paramArrayOfInt, 1, paramArrayOfInt, 0, paramArrayOfInt.length - 1);
    int j = paramArrayOfInt[(paramArrayOfInt.length - 2)] + 1;
    int i = j;
    if (this.mWrapSelectorWheel)
    {
      i = j;
      if (j > this.mMaxValue) {
        i = this.mMinValue;
      }
    }
    paramArrayOfInt[(paramArrayOfInt.length - 1)] = i;
    ensureCachedScrollSelectorValue(i);
  }
  
  private void init()
  {
    this.mSolidColor = 0;
    this.mSelectionDivider = getResources().getDrawable(2130837862);
    this.mSelectionDividerHeight = ((int)TypedValue.applyDimension(1, 2.0F, getResources().getDisplayMetrics()));
    this.mSelectionDividersDistance = ((int)TypedValue.applyDimension(1, 48.0F, getResources().getDisplayMetrics()));
    this.mMinHeight = -1;
    this.mMaxHeight = ((int)TypedValue.applyDimension(1, 180.0F, getResources().getDisplayMetrics()));
    if ((this.mMinHeight != -1) && (this.mMaxHeight != -1) && (this.mMinHeight > this.mMaxHeight)) {
      throw new IllegalArgumentException("minHeight > maxHeight");
    }
    this.mMinWidth = ((int)TypedValue.applyDimension(1, 64.0F, getResources().getDisplayMetrics()));
    this.mMaxWidth = -1;
    if ((this.mMinWidth != -1) && (this.mMaxWidth != -1) && (this.mMinWidth > this.mMaxWidth)) {
      throw new IllegalArgumentException("minWidth > maxWidth");
    }
    if (this.mMaxWidth == -1) {}
    for (boolean bool = true;; bool = false)
    {
      this.mComputeMaxWidth = bool;
      this.mVirtualButtonPressedDrawable = getResources().getDrawable(2130837785);
      this.mPressedStateHelper = new PressedStateHelper();
      setWillNotDraw(false);
      this.mInputText = new TextView(getContext());
      addView(this.mInputText);
      this.mInputText.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
      this.mInputText.setGravity(17);
      this.mInputText.setSingleLine(true);
      this.mInputText.setBackgroundResource(0);
      this.mInputText.setTextSize(2, 18.0F);
      Object localObject = ViewConfiguration.get(getContext());
      this.mTouchSlop = ((ViewConfiguration)localObject).getScaledTouchSlop();
      this.mMinimumFlingVelocity = ((ViewConfiguration)localObject).getScaledMinimumFlingVelocity();
      this.mMaximumFlingVelocity = (((ViewConfiguration)localObject).getScaledMaximumFlingVelocity() / 8);
      this.mTextSize = ((int)this.mInputText.getTextSize());
      localObject = new Paint();
      ((Paint)localObject).setAntiAlias(true);
      ((Paint)localObject).setTextAlign(Paint.Align.CENTER);
      ((Paint)localObject).setTextSize(this.mTextSize);
      ((Paint)localObject).setTypeface(this.mInputText.getTypeface());
      ((Paint)localObject).setColor(this.mInputText.getTextColors().getColorForState(ENABLED_STATE_SET, -1));
      this.mSelectorWheelPaint = ((Paint)localObject);
      this.mFlingScroller = new Scroller(getContext(), null, true);
      this.mAdjustScroller = new Scroller(getContext(), new DecelerateInterpolator(2.5F));
      updateInputTextView();
      return;
    }
  }
  
  private void initializeFadingEdges()
  {
    setVerticalFadingEdgeEnabled(true);
    setFadingEdgeLength((getBottom() - getTop() - this.mTextSize) / 2);
  }
  
  private void initializeSelectorWheel()
  {
    initializeSelectorWheelIndices();
    int[] arrayOfInt = this.mSelectorIndices;
    int i = arrayOfInt.length;
    int j = this.mTextSize;
    this.mSelectorTextGapHeight = ((int)((getBottom() - getTop() - i * j) / arrayOfInt.length + 0.5F));
    this.mSelectorElementHeight = (this.mTextSize + this.mSelectorTextGapHeight);
    this.mInitialScrollOffset = (this.mInputText.getBaseline() + this.mInputText.getTop() - this.mSelectorElementHeight * 1);
    this.mCurrentScrollOffset = this.mInitialScrollOffset;
    updateInputTextView();
  }
  
  private void initializeSelectorWheelIndices()
  {
    this.mSelectorIndexToStringCache.clear();
    int[] arrayOfInt = this.mSelectorIndices;
    int m = getValue();
    int i = 0;
    while (i < this.mSelectorIndices.length)
    {
      int k = m + (i - 1);
      int j = k;
      if (this.mWrapSelectorWheel) {
        j = getWrappedSelectorIndex(k);
      }
      arrayOfInt[i] = j;
      ensureCachedScrollSelectorValue(arrayOfInt[i]);
      i += 1;
    }
  }
  
  private int makeMeasureSpec(int paramInt1, int paramInt2)
  {
    if (paramInt2 == -1) {
      return paramInt1;
    }
    int i = View.MeasureSpec.getSize(paramInt1);
    int j = View.MeasureSpec.getMode(paramInt1);
    switch (j)
    {
    case 1073741824: 
    default: 
      throw new IllegalArgumentException("Unknown measure mode: " + j);
    case -2147483648: 
      return View.MeasureSpec.makeMeasureSpec(Math.min(i, paramInt2), 1073741824);
    }
    return View.MeasureSpec.makeMeasureSpec(paramInt2, 1073741824);
  }
  
  private boolean moveToFinalScrollerPosition(Scroller paramScroller)
  {
    paramScroller.forceFinished(true);
    int k = paramScroller.getFinalY() - paramScroller.getCurrY();
    int i = this.mCurrentScrollOffset;
    int j = this.mSelectorElementHeight;
    j = this.mInitialScrollOffset - (i + k) % j;
    if (j != 0)
    {
      i = j;
      if (Math.abs(j) > this.mSelectorElementHeight / 2) {
        if (j <= 0) {
          break label79;
        }
      }
      label79:
      for (i = j - this.mSelectorElementHeight;; i = j + this.mSelectorElementHeight)
      {
        scrollBy(0, k + i);
        return true;
      }
    }
    return false;
  }
  
  private void notifyChange(int paramInt1, int paramInt2)
  {
    if (this.mOnValueChangeListener != null) {
      this.mOnValueChangeListener.onValueChange(this, paramInt1, this.mValue);
    }
  }
  
  private void onScrollStateChange(int paramInt)
  {
    if (this.mScrollState == paramInt) {}
    do
    {
      return;
      this.mScrollState = paramInt;
    } while (this.mOnScrollListener == null);
    this.mOnScrollListener.onScrollStateChange(this, paramInt);
  }
  
  private void onScrollerFinished(Scroller paramScroller)
  {
    if (paramScroller == this.mFlingScroller)
    {
      if (!ensureScrollWheelAdjusted()) {
        updateInputTextView();
      }
      onScrollStateChange(0);
    }
    while (this.mScrollState == 1) {
      return;
    }
    updateInputTextView();
  }
  
  private void postChangeCurrentByOneFromLongPress(boolean paramBoolean, long paramLong)
  {
    if (this.mChangeCurrentByOneFromLongPressCommand == null) {
      this.mChangeCurrentByOneFromLongPressCommand = new ChangeCurrentByOneFromLongPressCommand();
    }
    for (;;)
    {
      this.mChangeCurrentByOneFromLongPressCommand.setStep(paramBoolean);
      postDelayed(this.mChangeCurrentByOneFromLongPressCommand, paramLong);
      return;
      removeCallbacks(this.mChangeCurrentByOneFromLongPressCommand);
    }
  }
  
  private void removeAllCallbacks()
  {
    if (this.mChangeCurrentByOneFromLongPressCommand != null) {
      removeCallbacks(this.mChangeCurrentByOneFromLongPressCommand);
    }
    this.mPressedStateHelper.cancel();
  }
  
  private void removeChangeCurrentByOneFromLongPress()
  {
    if (this.mChangeCurrentByOneFromLongPressCommand != null) {
      removeCallbacks(this.mChangeCurrentByOneFromLongPressCommand);
    }
  }
  
  public static int resolveSizeAndState(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = paramInt1;
    int j = View.MeasureSpec.getMode(paramInt2);
    paramInt2 = View.MeasureSpec.getSize(paramInt2);
    switch (j)
    {
    default: 
      paramInt1 = i;
    }
    for (;;)
    {
      return 0xFF000000 & paramInt3 | paramInt1;
      continue;
      if (paramInt2 < paramInt1)
      {
        paramInt1 = paramInt2 | 0x1000000;
      }
      else
      {
        continue;
        paramInt1 = paramInt2;
      }
    }
  }
  
  private int resolveSizeAndStateRespectingMinSize(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = paramInt2;
    if (paramInt1 != -1) {
      i = resolveSizeAndState(Math.max(paramInt1, paramInt2), paramInt3, 0);
    }
    return i;
  }
  
  private void setValueInternal(int paramInt, boolean paramBoolean)
  {
    if (this.mValue == paramInt) {
      return;
    }
    if (this.mWrapSelectorWheel) {}
    for (paramInt = getWrappedSelectorIndex(paramInt);; paramInt = Math.min(Math.max(paramInt, this.mMinValue), this.mMaxValue))
    {
      int i = this.mValue;
      this.mValue = paramInt;
      updateInputTextView();
      if (paramBoolean) {
        notifyChange(i, paramInt);
      }
      initializeSelectorWheelIndices();
      invalidate();
      return;
    }
  }
  
  private void tryComputeMaxWidth()
  {
    if (!this.mComputeMaxWidth) {}
    int i;
    float f1;
    int j;
    int k;
    do
    {
      return;
      i = 0;
      if (this.mDisplayedValues != null) {
        break;
      }
      f1 = 0.0F;
      i = 0;
      while (i <= 9)
      {
        float f3 = this.mSelectorWheelPaint.measureText(formatNumberWithLocale(i));
        float f2 = f1;
        if (f3 > f1) {
          f2 = f3;
        }
        i += 1;
        f1 = f2;
      }
      j = 0;
      i = this.mMaxValue;
      while (i > 0)
      {
        j += 1;
        i /= 10;
      }
      k = (int)(j * f1);
      i = k + (this.mInputText.getPaddingLeft() + this.mInputText.getPaddingRight());
    } while (this.mMaxWidth == i);
    if (i > this.mMinWidth) {}
    for (this.mMaxWidth = i;; this.mMaxWidth = this.mMinWidth)
    {
      invalidate();
      return;
      String[] arrayOfString = this.mDisplayedValues;
      int m = arrayOfString.length;
      j = 0;
      for (;;)
      {
        k = i;
        if (j >= m) {
          break;
        }
        String str = arrayOfString[j];
        f1 = this.mSelectorWheelPaint.measureText(str);
        k = i;
        if (f1 > i) {
          k = (int)f1;
        }
        j += 1;
        i = k;
      }
    }
  }
  
  private boolean updateInputTextView()
  {
    if (this.mDisplayedValues == null) {}
    for (String str = formatNumber(this.mValue); (!TextUtils.isEmpty(str)) && (!str.equals(this.mInputText.getText().toString())); str = this.mDisplayedValues[(this.mValue - this.mMinValue)])
    {
      this.mInputText.setText(str);
      return true;
    }
    return false;
  }
  
  public void computeScroll()
  {
    Scroller localScroller2 = this.mFlingScroller;
    Scroller localScroller1 = localScroller2;
    if (localScroller2.isFinished())
    {
      localScroller2 = this.mAdjustScroller;
      localScroller1 = localScroller2;
      if (localScroller2.isFinished()) {
        return;
      }
    }
    localScroller1.computeScrollOffset();
    int i = localScroller1.getCurrY();
    if (this.mPreviousScrollerY == 0) {
      this.mPreviousScrollerY = localScroller1.getStartY();
    }
    scrollBy(0, i - this.mPreviousScrollerY);
    this.mPreviousScrollerY = i;
    if (localScroller1.isFinished())
    {
      onScrollerFinished(localScroller1);
      return;
    }
    invalidate();
  }
  
  protected int computeVerticalScrollExtent()
  {
    return getHeight();
  }
  
  protected int computeVerticalScrollOffset()
  {
    return this.mCurrentScrollOffset;
  }
  
  protected int computeVerticalScrollRange()
  {
    return (this.mMaxValue - this.mMinValue + 1) * this.mSelectorElementHeight;
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    boolean bool = true;
    int i = paramKeyEvent.getKeyCode();
    switch (i)
    {
    }
    label119:
    label158:
    do
    {
      bool = super.dispatchKeyEvent(paramKeyEvent);
      do
      {
        return bool;
        removeAllCallbacks();
        break;
        switch (paramKeyEvent.getAction())
        {
        default: 
          break;
        case 0: 
          if ((!this.mWrapSelectorWheel) && (i != 20)) {
            break label158;
          }
          if (getValue() >= getMaxValue()) {
            break;
          }
          requestFocus();
          this.mLastHandledDownDpadKeyCode = i;
          removeAllCallbacks();
        }
      } while (!this.mFlingScroller.isFinished());
      if (i == 20) {}
      for (bool = true;; bool = false)
      {
        changeValueByOne(bool);
        return true;
        if (getValue() <= getMinValue()) {
          break;
        }
        break label119;
      }
    } while (this.mLastHandledDownDpadKeyCode != i);
    this.mLastHandledDownDpadKeyCode = -1;
    return true;
  }
  
  public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
  {
    switch (paramMotionEvent.getActionMasked())
    {
    }
    for (;;)
    {
      return super.dispatchTouchEvent(paramMotionEvent);
      removeAllCallbacks();
    }
  }
  
  public boolean dispatchTrackballEvent(MotionEvent paramMotionEvent)
  {
    switch (paramMotionEvent.getActionMasked())
    {
    }
    for (;;)
    {
      return super.dispatchTrackballEvent(paramMotionEvent);
      removeAllCallbacks();
    }
  }
  
  protected float getBottomFadingEdgeStrength()
  {
    return 0.9F;
  }
  
  public String[] getDisplayedValues()
  {
    return this.mDisplayedValues;
  }
  
  public int getMaxValue()
  {
    return this.mMaxValue;
  }
  
  public int getMinValue()
  {
    return this.mMinValue;
  }
  
  public int getSolidColor()
  {
    return this.mSolidColor;
  }
  
  protected float getTopFadingEdgeStrength()
  {
    return 0.9F;
  }
  
  public int getValue()
  {
    return this.mValue;
  }
  
  public boolean getWrapSelectorWheel()
  {
    return this.mWrapSelectorWheel;
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    removeAllCallbacks();
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    float f2 = (getRight() - getLeft()) / 2;
    float f1 = this.mCurrentScrollOffset;
    if ((this.mVirtualButtonPressedDrawable != null) && (this.mScrollState == 0))
    {
      if (this.mDecrementVirtualButtonPressed)
      {
        this.mVirtualButtonPressedDrawable.setState(PRESSED_STATE_SET);
        this.mVirtualButtonPressedDrawable.setBounds(0, 0, getRight(), this.mTopSelectionDividerTop);
        this.mVirtualButtonPressedDrawable.draw(paramCanvas);
      }
      if (this.mIncrementVirtualButtonPressed)
      {
        this.mVirtualButtonPressedDrawable.setState(PRESSED_STATE_SET);
        this.mVirtualButtonPressedDrawable.setBounds(0, this.mBottomSelectionDividerBottom, getRight(), getBottom());
        this.mVirtualButtonPressedDrawable.draw(paramCanvas);
      }
    }
    int[] arrayOfInt = this.mSelectorIndices;
    int i = 0;
    int j;
    while (i < arrayOfInt.length)
    {
      j = arrayOfInt[i];
      String str = (String)this.mSelectorIndexToStringCache.get(j);
      if ((i != 1) || (this.mInputText.getVisibility() != 0)) {
        paramCanvas.drawText(str, f2, f1, this.mSelectorWheelPaint);
      }
      f1 += this.mSelectorElementHeight;
      i += 1;
    }
    if (this.mSelectionDivider != null)
    {
      i = this.mTopSelectionDividerTop;
      j = this.mSelectionDividerHeight;
      this.mSelectionDivider.setBounds(0, i, getRight(), i + j);
      this.mSelectionDivider.draw(paramCanvas);
      i = this.mBottomSelectionDividerBottom;
      j = this.mSelectionDividerHeight;
      this.mSelectionDivider.setBounds(0, i - j, getRight(), i);
      this.mSelectionDivider.draw(paramCanvas);
    }
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    if (!isEnabled()) {
      return false;
    }
    switch (paramMotionEvent.getActionMasked())
    {
    default: 
      return false;
    }
    removeAllCallbacks();
    this.mInputText.setVisibility(4);
    float f = paramMotionEvent.getY();
    this.mLastDownEventY = f;
    this.mLastDownOrMoveEventY = f;
    this.mLastDownEventTime = paramMotionEvent.getEventTime();
    this.mIngonreMoveEvents = false;
    if (this.mLastDownEventY < this.mTopSelectionDividerTop)
    {
      if (this.mScrollState == 0) {
        this.mPressedStateHelper.buttonPressDelayed(2);
      }
      getParent().requestDisallowInterceptTouchEvent(true);
      if (this.mFlingScroller.isFinished()) {
        break label176;
      }
      this.mFlingScroller.forceFinished(true);
      this.mAdjustScroller.forceFinished(true);
      onScrollStateChange(0);
    }
    for (;;)
    {
      return true;
      if ((this.mLastDownEventY <= this.mBottomSelectionDividerBottom) || (this.mScrollState != 0)) {
        break;
      }
      this.mPressedStateHelper.buttonPressDelayed(1);
      break;
      label176:
      if (!this.mAdjustScroller.isFinished())
      {
        this.mFlingScroller.forceFinished(true);
        this.mAdjustScroller.forceFinished(true);
      }
      else if (this.mLastDownEventY < this.mTopSelectionDividerTop)
      {
        postChangeCurrentByOneFromLongPress(false, ViewConfiguration.getLongPressTimeout());
      }
      else if (this.mLastDownEventY > this.mBottomSelectionDividerBottom)
      {
        postChangeCurrentByOneFromLongPress(true, ViewConfiguration.getLongPressTimeout());
      }
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramInt4 = getMeasuredWidth();
    paramInt3 = getMeasuredHeight();
    paramInt1 = this.mInputText.getMeasuredWidth();
    paramInt2 = this.mInputText.getMeasuredHeight();
    paramInt4 = (paramInt4 - paramInt1) / 2;
    paramInt3 = (paramInt3 - paramInt2) / 2;
    this.mInputText.layout(paramInt4, paramInt3, paramInt4 + paramInt1, paramInt3 + paramInt2);
    if (paramBoolean)
    {
      initializeSelectorWheel();
      initializeFadingEdges();
      this.mTopSelectionDividerTop = ((getHeight() - this.mSelectionDividersDistance) / 2 - this.mSelectionDividerHeight);
      this.mBottomSelectionDividerBottom = (this.mTopSelectionDividerTop + this.mSelectionDividerHeight * 2 + this.mSelectionDividersDistance);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(makeMeasureSpec(paramInt1, this.mMaxWidth), makeMeasureSpec(paramInt2, this.mMaxHeight));
    setMeasuredDimension(resolveSizeAndStateRespectingMinSize(this.mMinWidth, getMeasuredWidth(), paramInt1), resolveSizeAndStateRespectingMinSize(this.mMinHeight, getMeasuredHeight(), paramInt2));
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (!isEnabled()) {
      return false;
    }
    if (this.mVelocityTracker == null) {
      this.mVelocityTracker = VelocityTracker.obtain();
    }
    this.mVelocityTracker.addMovement(paramMotionEvent);
    switch (paramMotionEvent.getActionMasked())
    {
    }
    for (;;)
    {
      return true;
      if (!this.mIngonreMoveEvents)
      {
        float f = paramMotionEvent.getY();
        if (this.mScrollState != 1) {
          if ((int)Math.abs(f - this.mLastDownEventY) > this.mTouchSlop)
          {
            removeAllCallbacks();
            onScrollStateChange(1);
          }
        }
        for (;;)
        {
          this.mLastDownOrMoveEventY = f;
          break;
          scrollBy(0, (int)(f - this.mLastDownOrMoveEventY));
          invalidate();
        }
        removeChangeCurrentByOneFromLongPress();
        this.mPressedStateHelper.cancel();
        VelocityTracker localVelocityTracker = this.mVelocityTracker;
        localVelocityTracker.computeCurrentVelocity(1000, this.mMaximumFlingVelocity);
        i = (int)localVelocityTracker.getYVelocity();
        if (Math.abs(i) <= this.mMinimumFlingVelocity) {
          break;
        }
        fling(i);
        onScrollStateChange(2);
        this.mVelocityTracker.recycle();
        this.mVelocityTracker = null;
      }
    }
    int i = (int)paramMotionEvent.getY();
    int j = (int)Math.abs(i - this.mLastDownEventY);
    long l1 = paramMotionEvent.getEventTime();
    long l2 = this.mLastDownEventTime;
    if ((j <= this.mTouchSlop) && (l1 - l2 < ViewConfiguration.getTapTimeout()))
    {
      i = i / this.mSelectorElementHeight - 1;
      if (i > 0)
      {
        changeValueByOne(true);
        this.mPressedStateHelper.buttonTapped(1);
      }
    }
    for (;;)
    {
      onScrollStateChange(0);
      break;
      if (i < 0)
      {
        changeValueByOne(false);
        this.mPressedStateHelper.buttonTapped(2);
        continue;
        ensureScrollWheelAdjusted();
      }
    }
  }
  
  public void scrollBy(int paramInt1, int paramInt2)
  {
    int[] arrayOfInt = this.mSelectorIndices;
    if ((!this.mWrapSelectorWheel) && (paramInt2 > 0) && (arrayOfInt[1] <= this.mMinValue)) {
      this.mCurrentScrollOffset = this.mInitialScrollOffset;
    }
    for (;;)
    {
      return;
      if ((!this.mWrapSelectorWheel) && (paramInt2 < 0) && (arrayOfInt[1] >= this.mMaxValue))
      {
        this.mCurrentScrollOffset = this.mInitialScrollOffset;
        return;
      }
      for (this.mCurrentScrollOffset += paramInt2; this.mCurrentScrollOffset - this.mInitialScrollOffset > this.mSelectorTextGapHeight; this.mCurrentScrollOffset = this.mInitialScrollOffset)
      {
        label75:
        this.mCurrentScrollOffset -= this.mSelectorElementHeight;
        decrementSelectorIndices(arrayOfInt);
        setValueInternal(arrayOfInt[1], true);
        if ((this.mWrapSelectorWheel) || (arrayOfInt[1] > this.mMinValue)) {
          break label75;
        }
      }
      while (this.mCurrentScrollOffset - this.mInitialScrollOffset < -this.mSelectorTextGapHeight)
      {
        this.mCurrentScrollOffset += this.mSelectorElementHeight;
        incrementSelectorIndices(arrayOfInt);
        setValueInternal(arrayOfInt[1], true);
        if ((!this.mWrapSelectorWheel) && (arrayOfInt[1] >= this.mMaxValue)) {
          this.mCurrentScrollOffset = this.mInitialScrollOffset;
        }
      }
    }
  }
  
  public void setDisplayedValues(String[] paramArrayOfString)
  {
    if (this.mDisplayedValues == paramArrayOfString) {
      return;
    }
    this.mDisplayedValues = paramArrayOfString;
    updateInputTextView();
    initializeSelectorWheelIndices();
    tryComputeMaxWidth();
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    super.setEnabled(paramBoolean);
    this.mInputText.setEnabled(paramBoolean);
  }
  
  public void setFormatter(Formatter paramFormatter)
  {
    if (paramFormatter == this.mFormatter) {
      return;
    }
    this.mFormatter = paramFormatter;
    initializeSelectorWheelIndices();
    updateInputTextView();
  }
  
  public void setMaxValue(int paramInt)
  {
    if (this.mMaxValue == paramInt) {
      return;
    }
    if (paramInt < 0) {
      throw new IllegalArgumentException("maxValue must be >= 0");
    }
    this.mMaxValue = paramInt;
    if (this.mMaxValue < this.mValue) {
      this.mValue = this.mMaxValue;
    }
    if (this.mMaxValue - this.mMinValue > this.mSelectorIndices.length) {}
    for (boolean bool = true;; bool = false)
    {
      setWrapSelectorWheel(bool);
      initializeSelectorWheelIndices();
      updateInputTextView();
      tryComputeMaxWidth();
      invalidate();
      return;
    }
  }
  
  public void setMinValue(int paramInt)
  {
    if (this.mMinValue == paramInt) {
      return;
    }
    if (paramInt < 0) {
      throw new IllegalArgumentException("minValue must be >= 0");
    }
    this.mMinValue = paramInt;
    if (this.mMinValue > this.mValue) {
      this.mValue = this.mMinValue;
    }
    if (this.mMaxValue - this.mMinValue > this.mSelectorIndices.length) {}
    for (boolean bool = true;; bool = false)
    {
      setWrapSelectorWheel(bool);
      initializeSelectorWheelIndices();
      updateInputTextView();
      tryComputeMaxWidth();
      invalidate();
      return;
    }
  }
  
  public void setOnLongPressUpdateInterval(long paramLong)
  {
    this.mLongPressUpdateInterval = paramLong;
  }
  
  public void setOnScrollListener(OnScrollListener paramOnScrollListener)
  {
    this.mOnScrollListener = paramOnScrollListener;
  }
  
  public void setOnValueChangedListener(OnValueChangeListener paramOnValueChangeListener)
  {
    this.mOnValueChangeListener = paramOnValueChangeListener;
  }
  
  public void setValue(int paramInt)
  {
    setValueInternal(paramInt, false);
  }
  
  public void setWrapSelectorWheel(boolean paramBoolean)
  {
    if (this.mMaxValue - this.mMinValue >= this.mSelectorIndices.length) {}
    for (int i = 1;; i = 0)
    {
      if (((!paramBoolean) || (i != 0)) && (paramBoolean != this.mWrapSelectorWheel)) {
        this.mWrapSelectorWheel = paramBoolean;
      }
      return;
    }
  }
  
  class ChangeCurrentByOneFromLongPressCommand
    implements Runnable
  {
    private boolean mIncrement;
    
    ChangeCurrentByOneFromLongPressCommand() {}
    
    private void setStep(boolean paramBoolean)
    {
      this.mIncrement = paramBoolean;
    }
    
    public void run()
    {
      NumberPicker.this.changeValueByOne(this.mIncrement);
      NumberPicker.this.postDelayed(this, NumberPicker.this.mLongPressUpdateInterval);
    }
  }
  
  public static abstract interface Formatter
  {
    public abstract String format(int paramInt);
  }
  
  public static abstract interface OnScrollListener
  {
    public static final int SCROLL_STATE_FLING = 2;
    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_TOUCH_SCROLL = 1;
    
    public abstract void onScrollStateChange(NumberPicker paramNumberPicker, int paramInt);
  }
  
  public static abstract interface OnValueChangeListener
  {
    public abstract void onValueChange(NumberPicker paramNumberPicker, int paramInt1, int paramInt2);
  }
  
  class PressedStateHelper
    implements Runnable
  {
    public static final int BUTTON_DECREMENT = 2;
    public static final int BUTTON_INCREMENT = 1;
    private final int MODE_PRESS = 1;
    private final int MODE_TAPPED = 2;
    private int mManagedButton;
    private int mMode;
    
    PressedStateHelper() {}
    
    public void buttonPressDelayed(int paramInt)
    {
      cancel();
      this.mMode = 1;
      this.mManagedButton = paramInt;
      NumberPicker.this.postDelayed(this, ViewConfiguration.getTapTimeout());
    }
    
    public void buttonTapped(int paramInt)
    {
      cancel();
      this.mMode = 2;
      this.mManagedButton = paramInt;
      NumberPicker.this.post(this);
    }
    
    public void cancel()
    {
      this.mMode = 0;
      this.mManagedButton = 0;
      NumberPicker.this.removeCallbacks(this);
      if (NumberPicker.this.mIncrementVirtualButtonPressed)
      {
        NumberPicker.access$102(NumberPicker.this, false);
        NumberPicker.this.invalidate(0, NumberPicker.this.mBottomSelectionDividerBottom, NumberPicker.this.getRight(), NumberPicker.this.getBottom());
      }
      NumberPicker.access$302(NumberPicker.this, false);
      if (NumberPicker.this.mDecrementVirtualButtonPressed) {
        NumberPicker.this.invalidate(0, 0, NumberPicker.this.getRight(), NumberPicker.this.mTopSelectionDividerTop);
      }
    }
    
    public void run()
    {
      switch (this.mMode)
      {
      default: 
        return;
      case 1: 
        switch (this.mManagedButton)
        {
        default: 
          return;
        case 1: 
          NumberPicker.access$102(NumberPicker.this, true);
          NumberPicker.this.invalidate(0, NumberPicker.this.mBottomSelectionDividerBottom, NumberPicker.this.getRight(), NumberPicker.this.getBottom());
          return;
        }
        NumberPicker.access$302(NumberPicker.this, true);
        NumberPicker.this.invalidate(0, 0, NumberPicker.this.getRight(), NumberPicker.this.mTopSelectionDividerTop);
        return;
      }
      switch (this.mManagedButton)
      {
      default: 
        return;
      case 1: 
        if (!NumberPicker.this.mIncrementVirtualButtonPressed) {
          NumberPicker.this.postDelayed(this, ViewConfiguration.getPressedStateDuration());
        }
        NumberPicker.access$102(NumberPicker.this, NumberPicker.this.mIncrementVirtualButtonPressed ^ true);
        NumberPicker.this.invalidate(0, NumberPicker.this.mBottomSelectionDividerBottom, NumberPicker.this.getRight(), NumberPicker.this.getBottom());
        return;
      }
      if (!NumberPicker.this.mDecrementVirtualButtonPressed) {
        NumberPicker.this.postDelayed(this, ViewConfiguration.getPressedStateDuration());
      }
      NumberPicker.access$302(NumberPicker.this, NumberPicker.this.mDecrementVirtualButtonPressed ^ true);
      NumberPicker.this.invalidate(0, 0, NumberPicker.this.getRight(), NumberPicker.this.mTopSelectionDividerTop);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/NumberPicker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */