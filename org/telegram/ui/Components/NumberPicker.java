package org.telegram.ui.Components;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
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
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import java.util.Locale;
import org.telegram.ui.ActionBar.Theme;

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
  private Paint mSelectionDivider;
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
    int i = paramArrayOfInt[1] - 1;
    int j = i;
    if (this.mWrapSelectorWheel)
    {
      j = i;
      if (i < this.mMinValue) {
        j = this.mMaxValue;
      }
    }
    paramArrayOfInt[0] = j;
    ensureCachedScrollSelectorValue(j);
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
      break;
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
    int i = this.mInitialScrollOffset - this.mCurrentScrollOffset;
    if (i != 0)
    {
      this.mPreviousScrollerY = 0;
      j = i;
      if (Math.abs(i) > this.mSelectorElementHeight / 2) {
        if (i <= 0) {
          break label72;
        }
      }
    }
    label72:
    for (int j = -this.mSelectorElementHeight;; j = this.mSelectorElementHeight)
    {
      j = i + j;
      this.mAdjustScroller.startScroll(0, 0, 0, j, 800);
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
    if (this.mFormatter != null) {}
    for (String str = this.mFormatter.format(paramInt);; str = formatNumberWithLocale(paramInt)) {
      return str;
    }
  }
  
  private static String formatNumberWithLocale(int paramInt)
  {
    return String.format(Locale.getDefault(), "%d", new Object[] { Integer.valueOf(paramInt) });
  }
  
  /* Error */
  private int getSelectedPos(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 223	org/telegram/ui/Components/NumberPicker:mDisplayedValues	[Ljava/lang/String;
    //   4: ifnonnull +10 -> 14
    //   7: aload_1
    //   8: invokestatic 276	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   11: istore_2
    //   12: iload_2
    //   13: ireturn
    //   14: iconst_0
    //   15: istore_2
    //   16: iload_2
    //   17: aload_0
    //   18: getfield 223	org/telegram/ui/Components/NumberPicker:mDisplayedValues	[Ljava/lang/String;
    //   21: arraylength
    //   22: if_icmpge +40 -> 62
    //   25: aload_1
    //   26: invokevirtual 280	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   29: astore_1
    //   30: aload_0
    //   31: getfield 223	org/telegram/ui/Components/NumberPicker:mDisplayedValues	[Ljava/lang/String;
    //   34: iload_2
    //   35: aaload
    //   36: invokevirtual 280	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   39: aload_1
    //   40: invokevirtual 284	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   43: ifeq +13 -> 56
    //   46: aload_0
    //   47: getfield 204	org/telegram/ui/Components/NumberPicker:mMinValue	I
    //   50: iload_2
    //   51: iadd
    //   52: istore_2
    //   53: goto -41 -> 12
    //   56: iinc 2 1
    //   59: goto -43 -> 16
    //   62: aload_1
    //   63: invokestatic 276	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   66: istore_2
    //   67: goto -55 -> 12
    //   70: astore_1
    //   71: aload_0
    //   72: getfield 204	org/telegram/ui/Components/NumberPicker:mMinValue	I
    //   75: istore_2
    //   76: goto -64 -> 12
    //   79: astore_1
    //   80: goto -9 -> 71
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	83	0	this	NumberPicker
    //   0	83	1	paramString	String
    //   11	65	2	i	int
    // Exception table:
    //   from	to	target	type
    //   7	12	70	java/lang/NumberFormatException
    //   62	67	79	java/lang/NumberFormatException
  }
  
  private int getWrappedSelectorIndex(int paramInt)
  {
    int i;
    if (paramInt > this.mMaxValue) {
      i = this.mMinValue + (paramInt - this.mMaxValue) % (this.mMaxValue - this.mMinValue) - 1;
    }
    for (;;)
    {
      return i;
      i = paramInt;
      if (paramInt < this.mMinValue) {
        i = this.mMaxValue - (this.mMinValue - paramInt) % (this.mMaxValue - this.mMinValue) + 1;
      }
    }
  }
  
  private void incrementSelectorIndices(int[] paramArrayOfInt)
  {
    System.arraycopy(paramArrayOfInt, 1, paramArrayOfInt, 0, paramArrayOfInt.length - 1);
    int i = paramArrayOfInt[(paramArrayOfInt.length - 2)] + 1;
    int j = i;
    if (this.mWrapSelectorWheel)
    {
      j = i;
      if (i > this.mMaxValue) {
        j = this.mMinValue;
      }
    }
    paramArrayOfInt[(paramArrayOfInt.length - 1)] = j;
    ensureCachedScrollSelectorValue(j);
  }
  
  private void init()
  {
    this.mSolidColor = 0;
    this.mSelectionDivider = new Paint();
    this.mSelectionDivider.setColor(Theme.getColor("dialogButton"));
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
      this.mPressedStateHelper = new PressedStateHelper();
      setWillNotDraw(false);
      this.mInputText = new TextView(getContext());
      this.mInputText.setGravity(17);
      this.mInputText.setSingleLine(true);
      this.mInputText.setTextColor(Theme.getColor("dialogTextBlack"));
      this.mInputText.setBackgroundResource(0);
      this.mInputText.setTextSize(1, 18.0F);
      addView(this.mInputText, new LinearLayout.LayoutParams(-1, -2));
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
    int i = getValue();
    for (int j = 0; j < this.mSelectorIndices.length; j++)
    {
      int k = i + (j - 1);
      int m = k;
      if (this.mWrapSelectorWheel) {
        m = getWrappedSelectorIndex(k);
      }
      arrayOfInt[j] = m;
      ensureCachedScrollSelectorValue(arrayOfInt[j]);
    }
  }
  
  private int makeMeasureSpec(int paramInt1, int paramInt2)
  {
    if (paramInt2 == -1) {}
    for (;;)
    {
      return paramInt1;
      int i = View.MeasureSpec.getSize(paramInt1);
      int j = View.MeasureSpec.getMode(paramInt1);
      switch (j)
      {
      case 1073741824: 
      default: 
        throw new IllegalArgumentException("Unknown measure mode: " + j);
      case -2147483648: 
        paramInt1 = View.MeasureSpec.makeMeasureSpec(Math.min(i, paramInt2), NUM);
        break;
      case 0: 
        paramInt1 = View.MeasureSpec.makeMeasureSpec(paramInt2, NUM);
      }
    }
  }
  
  private boolean moveToFinalScrollerPosition(Scroller paramScroller)
  {
    boolean bool = true;
    paramScroller.forceFinished(true);
    int i = paramScroller.getFinalY() - paramScroller.getCurrY();
    int j = this.mCurrentScrollOffset;
    int k = this.mSelectorElementHeight;
    k = this.mInitialScrollOffset - (j + i) % k;
    if (k != 0)
    {
      j = k;
      if (Math.abs(k) > this.mSelectorElementHeight / 2)
      {
        if (k > 0) {
          j = k - this.mSelectorElementHeight;
        }
      }
      else {
        scrollBy(0, i + j);
      }
    }
    for (;;)
    {
      return bool;
      j = k + this.mSelectorElementHeight;
      break;
      bool = false;
    }
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
    for (;;)
    {
      return;
      this.mScrollState = paramInt;
      if (this.mOnScrollListener != null) {
        this.mOnScrollListener.onScrollStateChange(this, paramInt);
      }
    }
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
    for (;;)
    {
      return;
      if (this.mScrollState != 1) {
        updateInputTextView();
      }
    }
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
      break;
    }
  }
  
  private void tryComputeMaxWidth()
  {
    if (!this.mComputeMaxWidth) {}
    int i;
    float f3;
    int j;
    int k;
    do
    {
      return;
      i = 0;
      if (this.mDisplayedValues != null) {
        break;
      }
      float f1 = 0.0F;
      i = 0;
      while (i <= 9)
      {
        float f2 = this.mSelectorWheelPaint.measureText(formatNumberWithLocale(i));
        f3 = f1;
        if (f2 > f1) {
          f3 = f2;
        }
        i++;
        f1 = f3;
      }
      i = 0;
      j = this.mMaxValue;
      while (j > 0)
      {
        i++;
        j /= 10;
      }
      k = (int)(i * f1);
      i = k + (this.mInputText.getPaddingLeft() + this.mInputText.getPaddingRight());
    } while (this.mMaxWidth == i);
    if (i > this.mMinWidth) {}
    for (this.mMaxWidth = i;; this.mMaxWidth = this.mMinWidth)
    {
      invalidate();
      break;
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
        f3 = this.mSelectorWheelPaint.measureText(str);
        k = i;
        if (f3 > i) {
          k = (int)f3;
        }
        j++;
        i = k;
      }
    }
  }
  
  private boolean updateInputTextView()
  {
    String str;
    if (this.mDisplayedValues == null)
    {
      str = formatNumber(this.mValue);
      if ((TextUtils.isEmpty(str)) || (str.equals(this.mInputText.getText().toString()))) {
        break label72;
      }
      this.mInputText.setText(str);
    }
    label72:
    for (boolean bool = true;; bool = false)
    {
      return bool;
      str = this.mDisplayedValues[(this.mValue - this.mMinValue)];
      break;
    }
  }
  
  public void computeScroll()
  {
    Scroller localScroller1 = this.mFlingScroller;
    Scroller localScroller2 = localScroller1;
    if (localScroller1.isFinished())
    {
      localScroller1 = this.mAdjustScroller;
      localScroller2 = localScroller1;
      if (!localScroller1.isFinished()) {}
    }
    for (;;)
    {
      return;
      localScroller2.computeScrollOffset();
      int i = localScroller2.getCurrY();
      if (this.mPreviousScrollerY == 0) {
        this.mPreviousScrollerY = localScroller2.getStartY();
      }
      scrollBy(0, i - this.mPreviousScrollerY);
      this.mPreviousScrollerY = i;
      if (localScroller2.isFinished()) {
        onScrollerFinished(localScroller2);
      } else {
        invalidate();
      }
    }
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
    boolean bool1 = true;
    int i = paramKeyEvent.getKeyCode();
    boolean bool2;
    switch (i)
    {
    default: 
      bool2 = super.dispatchKeyEvent(paramKeyEvent);
    }
    for (;;)
    {
      label59:
      return bool2;
      removeAllCallbacks();
      break;
      switch (paramKeyEvent.getAction())
      {
      default: 
        break;
      case 0: 
        if ((this.mWrapSelectorWheel) || (i == 20))
        {
          if (getValue() >= getMaxValue()) {
            break;
          }
          requestFocus();
          this.mLastHandledDownDpadKeyCode = i;
          removeAllCallbacks();
          bool2 = bool1;
          if (!this.mFlingScroller.isFinished()) {
            continue;
          }
          if (i != 20) {
            break label185;
          }
        }
        for (bool2 = true;; bool2 = false)
        {
          changeValueByOne(bool2);
          bool2 = bool1;
          break label59;
          if (getValue() <= getMinValue()) {
            break;
          }
          break label123;
        }
      case 1: 
        label123:
        label185:
        if (this.mLastHandledDownDpadKeyCode != i) {
          break;
        }
        this.mLastHandledDownDpadKeyCode = -1;
        bool2 = bool1;
      }
    }
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
    float f1 = (getRight() - getLeft()) / 2;
    float f2 = this.mCurrentScrollOffset;
    int[] arrayOfInt = this.mSelectorIndices;
    for (int i = 0; i < arrayOfInt.length; i++)
    {
      j = arrayOfInt[i];
      String str = (String)this.mSelectorIndexToStringCache.get(j);
      if ((i != 1) || (this.mInputText.getVisibility() != 0)) {
        paramCanvas.drawText(str, f1, f2, this.mSelectorWheelPaint);
      }
      f2 += this.mSelectorElementHeight;
    }
    i = this.mTopSelectionDividerTop;
    int j = this.mSelectionDividerHeight;
    paramCanvas.drawRect(0.0F, i, getRight(), i + j, this.mSelectionDivider);
    i = this.mBottomSelectionDividerBottom;
    paramCanvas.drawRect(0.0F, i - this.mSelectionDividerHeight, getRight(), i, this.mSelectionDivider);
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool = false;
    if (!isEnabled()) {}
    for (;;)
    {
      return bool;
      switch (paramMotionEvent.getActionMasked())
      {
      }
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
      label103:
      getParent().requestDisallowInterceptTouchEvent(true);
      if (this.mFlingScroller.isFinished()) {
        break label180;
      }
      this.mFlingScroller.forceFinished(true);
      this.mAdjustScroller.forceFinished(true);
      onScrollStateChange(0);
    }
    for (;;)
    {
      bool = true;
      break;
      if ((this.mLastDownEventY <= this.mBottomSelectionDividerBottom) || (this.mScrollState != 0)) {
        break label103;
      }
      this.mPressedStateHelper.buttonPressDelayed(1);
      break label103;
      label180:
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
    paramInt3 = getMeasuredWidth();
    paramInt4 = getMeasuredHeight();
    paramInt2 = this.mInputText.getMeasuredWidth();
    paramInt1 = this.mInputText.getMeasuredHeight();
    paramInt3 = (paramInt3 - paramInt2) / 2;
    paramInt4 = (paramInt4 - paramInt1) / 2;
    this.mInputText.layout(paramInt3, paramInt4, paramInt3 + paramInt2, paramInt4 + paramInt1);
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
    boolean bool;
    if (!isEnabled())
    {
      bool = false;
      return bool;
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
      bool = true;
      break;
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
          break label214;
        }
        fling(i);
        onScrollStateChange(2);
        this.mVelocityTracker.recycle();
        this.mVelocityTracker = null;
      }
    }
    label214:
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
      }
      else
      {
        for (this.mCurrentScrollOffset += paramInt2; this.mCurrentScrollOffset - this.mInitialScrollOffset > this.mSelectorTextGapHeight; this.mCurrentScrollOffset = this.mInitialScrollOffset)
        {
          label77:
          this.mCurrentScrollOffset -= this.mSelectorElementHeight;
          decrementSelectorIndices(arrayOfInt);
          setValueInternal(arrayOfInt[1], true);
          if ((this.mWrapSelectorWheel) || (arrayOfInt[1] > this.mMinValue)) {
            break label77;
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
  }
  
  public void setDisplayedValues(String[] paramArrayOfString)
  {
    if (this.mDisplayedValues == paramArrayOfString) {}
    for (;;)
    {
      return;
      this.mDisplayedValues = paramArrayOfString;
      updateInputTextView();
      initializeSelectorWheelIndices();
      tryComputeMaxWidth();
    }
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    super.setEnabled(paramBoolean);
    this.mInputText.setEnabled(paramBoolean);
  }
  
  public void setFormatter(Formatter paramFormatter)
  {
    if (paramFormatter == this.mFormatter) {}
    for (;;)
    {
      return;
      this.mFormatter = paramFormatter;
      initializeSelectorWheelIndices();
      updateInputTextView();
    }
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
      break;
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
      break;
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
  
  public void setSelectorColor(int paramInt)
  {
    this.mSelectionDivider.setColor(paramInt);
  }
  
  public void setTextColor(int paramInt)
  {
    this.mInputText.setTextColor(paramInt);
    this.mSelectorWheelPaint.setColor(paramInt);
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
      }
      for (;;)
      {
        return;
        switch (this.mManagedButton)
        {
        default: 
          break;
        case 1: 
          NumberPicker.access$102(NumberPicker.this, true);
          NumberPicker.this.invalidate(0, NumberPicker.this.mBottomSelectionDividerBottom, NumberPicker.this.getRight(), NumberPicker.this.getBottom());
          break;
        case 2: 
          NumberPicker.access$302(NumberPicker.this, true);
          NumberPicker.this.invalidate(0, 0, NumberPicker.this.getRight(), NumberPicker.this.mTopSelectionDividerTop);
          continue;
          switch (this.mManagedButton)
          {
          default: 
            break;
          case 1: 
            if (!NumberPicker.this.mIncrementVirtualButtonPressed) {
              NumberPicker.this.postDelayed(this, ViewConfiguration.getPressedStateDuration());
            }
            NumberPicker.access$102(NumberPicker.this, NumberPicker.this.mIncrementVirtualButtonPressed ^ true);
            NumberPicker.this.invalidate(0, NumberPicker.this.mBottomSelectionDividerBottom, NumberPicker.this.getRight(), NumberPicker.this.getBottom());
            break;
          case 2: 
            if (!NumberPicker.this.mDecrementVirtualButtonPressed) {
              NumberPicker.this.postDelayed(this, ViewConfiguration.getPressedStateDuration());
            }
            NumberPicker.access$302(NumberPicker.this, NumberPicker.this.mDecrementVirtualButtonPressed ^ true);
            NumberPicker.this.invalidate(0, 0, NumberPicker.this.getRight(), NumberPicker.this.mTopSelectionDividerTop);
          }
          break;
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/NumberPicker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */