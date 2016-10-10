package org.telegram.ui.Components;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.CompoundButton;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;

public class Switch
  extends CompoundButton
{
  private static final int THUMB_ANIMATION_DURATION = 250;
  private static final int TOUCH_MODE_DOWN = 1;
  private static final int TOUCH_MODE_DRAGGING = 2;
  private static final int TOUCH_MODE_IDLE = 0;
  private boolean attachedToWindow;
  private int mMinFlingVelocity;
  private ObjectAnimator mPositionAnimator;
  private boolean mSplitTrack;
  private int mSwitchBottom;
  private int mSwitchHeight;
  private int mSwitchLeft;
  private int mSwitchMinWidth;
  private int mSwitchPadding;
  private int mSwitchRight;
  private int mSwitchTop;
  private int mSwitchWidth;
  private final Rect mTempRect = new Rect();
  private Drawable mThumbDrawable;
  private int mThumbTextPadding;
  private int mThumbWidth;
  private int mTouchMode;
  private int mTouchSlop;
  private float mTouchX;
  private float mTouchY;
  private Drawable mTrackDrawable;
  private VelocityTracker mVelocityTracker = VelocityTracker.obtain();
  private float thumbPosition;
  private boolean wasLayout;
  
  public Switch(Context paramContext)
  {
    super(paramContext);
    this.mThumbDrawable = paramContext.getResources().getDrawable(2130837996);
    if (this.mThumbDrawable != null) {
      this.mThumbDrawable.setCallback(this);
    }
    this.mTrackDrawable = paramContext.getResources().getDrawable(2130837999);
    if (this.mTrackDrawable != null) {
      this.mTrackDrawable.setCallback(this);
    }
    if (AndroidUtilities.density < 1.0F) {}
    for (this.mSwitchMinWidth = AndroidUtilities.dp(30.0F);; this.mSwitchMinWidth = 0)
    {
      this.mSwitchPadding = 0;
      this.mSplitTrack = false;
      paramContext = ViewConfiguration.get(paramContext);
      this.mTouchSlop = paramContext.getScaledTouchSlop();
      this.mMinFlingVelocity = paramContext.getScaledMinimumFlingVelocity();
      refreshDrawableState();
      setChecked(isChecked());
      return;
    }
  }
  
  private void animateThumbToCheckedState(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (float f = 1.0F;; f = 0.0F)
    {
      this.mPositionAnimator = ObjectAnimator.ofFloat(this, "thumbPosition", new float[] { f });
      this.mPositionAnimator.setDuration(250L);
      this.mPositionAnimator.start();
      return;
    }
  }
  
  private void cancelPositionAnimator()
  {
    if (this.mPositionAnimator != null) {
      this.mPositionAnimator.cancel();
    }
  }
  
  private void cancelSuperTouch(MotionEvent paramMotionEvent)
  {
    paramMotionEvent = MotionEvent.obtain(paramMotionEvent);
    paramMotionEvent.setAction(3);
    super.onTouchEvent(paramMotionEvent);
    paramMotionEvent.recycle();
  }
  
  public static float constrain(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    if (paramFloat1 < paramFloat2) {
      return paramFloat2;
    }
    if (paramFloat1 > paramFloat3) {
      return paramFloat3;
    }
    return paramFloat1;
  }
  
  private boolean getTargetCheckedState()
  {
    return this.thumbPosition > 0.5F;
  }
  
  private int getThumbOffset()
  {
    if (LocaleController.isRTL) {}
    for (float f = 1.0F - this.thumbPosition;; f = this.thumbPosition) {
      return (int)(getThumbScrollRange() * f + 0.5F);
    }
  }
  
  private int getThumbScrollRange()
  {
    if (this.mTrackDrawable != null)
    {
      Rect localRect = this.mTempRect;
      this.mTrackDrawable.getPadding(localRect);
      if (this.mThumbDrawable != null) {}
      for (Insets localInsets = Insets.NONE;; localInsets = Insets.NONE) {
        return this.mSwitchWidth - this.mThumbWidth - localRect.left - localRect.right - localInsets.left - localInsets.right;
      }
    }
    return 0;
  }
  
  private boolean hitThumb(float paramFloat1, float paramFloat2)
  {
    int k = getThumbOffset();
    this.mThumbDrawable.getPadding(this.mTempRect);
    int i = this.mSwitchTop;
    int j = this.mTouchSlop;
    k = this.mSwitchLeft + k - this.mTouchSlop;
    int m = this.mThumbWidth;
    int n = this.mTempRect.left;
    int i1 = this.mTempRect.right;
    int i2 = this.mTouchSlop;
    int i3 = this.mSwitchBottom;
    int i4 = this.mTouchSlop;
    return (paramFloat1 > k) && (paramFloat1 < m + k + n + i1 + i2) && (paramFloat2 > i - j) && (paramFloat2 < i3 + i4);
  }
  
  private void setThumbPosition(float paramFloat)
  {
    this.thumbPosition = paramFloat;
    invalidate();
  }
  
  private void stopDrag(MotionEvent paramMotionEvent)
  {
    boolean bool = true;
    this.mTouchMode = 0;
    int i;
    float f;
    if ((paramMotionEvent.getAction() == 1) && (isEnabled()))
    {
      i = 1;
      if (i == 0) {
        break label116;
      }
      this.mVelocityTracker.computeCurrentVelocity(1000);
      f = this.mVelocityTracker.getXVelocity();
      if (Math.abs(f) <= this.mMinFlingVelocity) {
        break label107;
      }
      if (!LocaleController.isRTL) {
        break label95;
      }
      if (f >= 0.0F) {
        break label89;
      }
    }
    for (;;)
    {
      setChecked(bool);
      cancelSuperTouch(paramMotionEvent);
      return;
      i = 0;
      break;
      label89:
      bool = false;
      continue;
      label95:
      if (f <= 0.0F)
      {
        bool = false;
        continue;
        label107:
        bool = getTargetCheckedState();
        continue;
        label116:
        bool = isChecked();
      }
    }
  }
  
  public void draw(Canvas paramCanvas)
  {
    Rect localRect = this.mTempRect;
    int m = this.mSwitchLeft;
    int i5 = this.mSwitchTop;
    int j = this.mSwitchRight;
    int i6 = this.mSwitchBottom;
    int k = m + getThumbOffset();
    Object localObject;
    if (this.mThumbDrawable != null)
    {
      localObject = Insets.NONE;
      i = k;
      if (this.mTrackDrawable != null)
      {
        this.mTrackDrawable.getPadding(localRect);
        int i7 = k + localRect.left;
        k = i5;
        int n = i6;
        int i4 = n;
        int i1 = m;
        int i2 = j;
        int i3 = k;
        if (localObject != Insets.NONE)
        {
          i = m;
          if (((Insets)localObject).left > localRect.left) {
            i = m + (((Insets)localObject).left - localRect.left);
          }
          m = k;
          if (((Insets)localObject).top > localRect.top) {
            m = k + (((Insets)localObject).top - localRect.top);
          }
          k = j;
          if (((Insets)localObject).right > localRect.right) {
            k = j - (((Insets)localObject).right - localRect.right);
          }
          i4 = n;
          i1 = i;
          i2 = k;
          i3 = m;
          if (((Insets)localObject).bottom > localRect.bottom)
          {
            i4 = n - (((Insets)localObject).bottom - localRect.bottom);
            i3 = m;
            i2 = k;
            i1 = i;
          }
        }
        this.mTrackDrawable.setBounds(i1, i3, i2, i4);
        i = i7;
      }
      if (this.mThumbDrawable != null)
      {
        this.mThumbDrawable.getPadding(localRect);
        j = i - localRect.left;
        k = this.mThumbWidth + i + localRect.right;
        if (AndroidUtilities.density != 1.5F) {
          break label396;
        }
      }
    }
    label396:
    for (int i = AndroidUtilities.dp(1.0F);; i = 0)
    {
      this.mThumbDrawable.setBounds(j, i5 + i, k, i6 + i);
      localObject = getBackground();
      if ((localObject != null) && (Build.VERSION.SDK_INT >= 21)) {
        ((Drawable)localObject).setHotspotBounds(j, i5, k, i6);
      }
      super.draw(paramCanvas);
      return;
      localObject = Insets.NONE;
      break;
    }
  }
  
  @SuppressLint({"NewApi"})
  public void drawableHotspotChanged(float paramFloat1, float paramFloat2)
  {
    super.drawableHotspotChanged(paramFloat1, paramFloat2);
    if (this.mThumbDrawable != null) {
      this.mThumbDrawable.setHotspot(paramFloat1, paramFloat2);
    }
    if (this.mTrackDrawable != null) {
      this.mTrackDrawable.setHotspot(paramFloat1, paramFloat2);
    }
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    int[] arrayOfInt = getDrawableState();
    if (this.mThumbDrawable != null) {
      this.mThumbDrawable.setState(arrayOfInt);
    }
    if (this.mTrackDrawable != null) {
      this.mTrackDrawable.setState(arrayOfInt);
    }
    invalidate();
  }
  
  public int getCompoundPaddingLeft()
  {
    if (!LocaleController.isRTL) {
      return super.getCompoundPaddingLeft();
    }
    return super.getCompoundPaddingLeft() + this.mSwitchWidth;
  }
  
  public int getCompoundPaddingRight()
  {
    if (LocaleController.isRTL) {
      return super.getCompoundPaddingRight();
    }
    return super.getCompoundPaddingRight() + this.mSwitchWidth;
  }
  
  public boolean getSplitTrack()
  {
    return this.mSplitTrack;
  }
  
  public int getSwitchMinWidth()
  {
    return this.mSwitchMinWidth;
  }
  
  public int getSwitchPadding()
  {
    return this.mSwitchPadding;
  }
  
  public Drawable getThumbDrawable()
  {
    return this.mThumbDrawable;
  }
  
  public float getThumbPosition()
  {
    return this.thumbPosition;
  }
  
  public int getThumbTextPadding()
  {
    return this.mThumbTextPadding;
  }
  
  public Drawable getTrackDrawable()
  {
    return this.mTrackDrawable;
  }
  
  public void jumpDrawablesToCurrentState()
  {
    super.jumpDrawablesToCurrentState();
    if (this.mThumbDrawable != null) {
      this.mThumbDrawable.jumpToCurrentState();
    }
    if (this.mTrackDrawable != null) {
      this.mTrackDrawable.jumpToCurrentState();
    }
    if ((this.mPositionAnimator != null) && (this.mPositionAnimator.isRunning()))
    {
      this.mPositionAnimator.end();
      this.mPositionAnimator = null;
    }
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    this.attachedToWindow = true;
    requestLayout();
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    this.attachedToWindow = false;
    this.wasLayout = false;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    Rect localRect = this.mTempRect;
    Drawable localDrawable1 = this.mTrackDrawable;
    int i;
    Drawable localDrawable2;
    if (localDrawable1 != null)
    {
      localDrawable1.getPadding(localRect);
      i = this.mSwitchTop;
      i = this.mSwitchBottom;
      localDrawable2 = this.mThumbDrawable;
      if (localDrawable1 != null)
      {
        if ((!this.mSplitTrack) || (localDrawable2 == null)) {
          break label154;
        }
        Insets localInsets = Insets.NONE;
        localDrawable2.copyBounds(localRect);
        localRect.left += localInsets.left;
        localRect.right -= localInsets.right;
        i = paramCanvas.save();
        paramCanvas.clipRect(localRect, Region.Op.DIFFERENCE);
        localDrawable1.draw(paramCanvas);
        paramCanvas.restoreToCount(i);
      }
    }
    for (;;)
    {
      i = paramCanvas.save();
      if (localDrawable2 != null) {
        localDrawable2.draw(paramCanvas);
      }
      paramCanvas.restoreToCount(i);
      return;
      localRect.setEmpty();
      break;
      label154:
      localDrawable1.draw(paramCanvas);
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    this.wasLayout = true;
    paramInt1 = 0;
    paramInt2 = 0;
    Rect localRect;
    if (this.mThumbDrawable != null)
    {
      localRect = this.mTempRect;
      if (this.mTrackDrawable != null)
      {
        this.mTrackDrawable.getPadding(localRect);
        Insets localInsets = Insets.NONE;
        paramInt1 = Math.max(0, localInsets.left - localRect.left);
        paramInt2 = Math.max(0, localInsets.right - localRect.right);
      }
    }
    else
    {
      if (!LocaleController.isRTL) {
        break label191;
      }
      paramInt3 = getPaddingLeft() + paramInt1;
      paramInt4 = this.mSwitchWidth + paramInt3 - paramInt1 - paramInt2;
      label114:
      switch (getGravity() & 0x70)
      {
      default: 
        paramInt2 = getPaddingTop();
        paramInt1 = paramInt2 + this.mSwitchHeight;
      }
    }
    for (;;)
    {
      this.mSwitchLeft = paramInt3;
      this.mSwitchTop = paramInt2;
      this.mSwitchBottom = paramInt1;
      this.mSwitchRight = paramInt4;
      return;
      localRect.setEmpty();
      break;
      label191:
      paramInt4 = getWidth() - getPaddingRight() - paramInt2;
      paramInt3 = paramInt4 - this.mSwitchWidth + paramInt1 + paramInt2;
      break label114;
      paramInt2 = (getPaddingTop() + getHeight() - getPaddingBottom()) / 2 - this.mSwitchHeight / 2;
      paramInt1 = paramInt2 + this.mSwitchHeight;
      continue;
      paramInt1 = getHeight() - getPaddingBottom();
      paramInt2 = paramInt1 - this.mSwitchHeight;
    }
  }
  
  public void onMeasure(int paramInt1, int paramInt2)
  {
    Object localObject = this.mTempRect;
    int i;
    if (this.mThumbDrawable != null)
    {
      this.mThumbDrawable.getPadding((Rect)localObject);
      j = this.mThumbDrawable.getIntrinsicWidth() - ((Rect)localObject).left - ((Rect)localObject).right;
      i = this.mThumbDrawable.getIntrinsicHeight();
      this.mThumbWidth = j;
      if (this.mTrackDrawable == null) {
        break label211;
      }
      this.mTrackDrawable.getPadding((Rect)localObject);
    }
    for (int j = this.mTrackDrawable.getIntrinsicHeight();; j = 0)
    {
      int i1 = ((Rect)localObject).left;
      int n = ((Rect)localObject).right;
      int m = i1;
      int k = n;
      if (this.mThumbDrawable != null)
      {
        localObject = Insets.NONE;
        m = Math.max(i1, ((Insets)localObject).left);
        k = Math.max(n, ((Insets)localObject).right);
      }
      k = Math.max(this.mSwitchMinWidth, this.mThumbWidth * 2 + m + k);
      i = Math.max(j, i);
      this.mSwitchWidth = k;
      this.mSwitchHeight = i;
      super.onMeasure(paramInt1, paramInt2);
      if (getMeasuredHeight() < i) {
        setMeasuredDimension(k, i);
      }
      return;
      j = 0;
      i = 0;
      break;
      label211:
      ((Rect)localObject).setEmpty();
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    this.mVelocityTracker.addMovement(paramMotionEvent);
    switch (paramMotionEvent.getActionMasked())
    {
    }
    for (;;)
    {
      return super.onTouchEvent(paramMotionEvent);
      float f1 = paramMotionEvent.getX();
      float f2 = paramMotionEvent.getY();
      if ((isEnabled()) && (hitThumb(f1, f2)))
      {
        this.mTouchMode = 1;
        this.mTouchX = f1;
        this.mTouchY = f2;
        continue;
        switch (this.mTouchMode)
        {
        case 0: 
        default: 
          break;
        case 1: 
          f1 = paramMotionEvent.getX();
          f2 = paramMotionEvent.getY();
          if ((Math.abs(f1 - this.mTouchX) > this.mTouchSlop) || (Math.abs(f2 - this.mTouchY) > this.mTouchSlop))
          {
            this.mTouchMode = 2;
            getParent().requestDisallowInterceptTouchEvent(true);
            this.mTouchX = f1;
            this.mTouchY = f2;
            return true;
          }
          break;
        case 2: 
          float f3 = paramMotionEvent.getX();
          int i = getThumbScrollRange();
          f1 = f3 - this.mTouchX;
          if (i != 0)
          {
            f1 /= i;
            f2 = f1;
            if (LocaleController.isRTL) {
              f2 = -f1;
            }
            f1 = constrain(this.thumbPosition + f2, 0.0F, 1.0F);
            if (f1 != this.thumbPosition)
            {
              this.mTouchX = f3;
              setThumbPosition(f1);
            }
            return true;
          }
          if (f1 > 0.0F) {}
          for (f1 = 1.0F;; f1 = -1.0F) {
            break;
          }
          if (this.mTouchMode == 2)
          {
            stopDrag(paramMotionEvent);
            super.onTouchEvent(paramMotionEvent);
            return true;
          }
          this.mTouchMode = 0;
          this.mVelocityTracker.clear();
        }
      }
    }
  }
  
  public void resetLayout()
  {
    this.wasLayout = false;
  }
  
  public void setChecked(boolean paramBoolean)
  {
    super.setChecked(paramBoolean);
    paramBoolean = isChecked();
    Drawable localDrawable;
    if ((this.attachedToWindow) && (this.wasLayout))
    {
      animateThumbToCheckedState(paramBoolean);
      if (this.mTrackDrawable != null)
      {
        localDrawable = this.mTrackDrawable;
        if (!paramBoolean) {
          break label127;
        }
        i = -6236422;
        label50:
        localDrawable.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
      }
      if (this.mThumbDrawable != null)
      {
        localDrawable = this.mThumbDrawable;
        if (!paramBoolean) {
          break label134;
        }
      }
    }
    label127:
    label134:
    for (int i = -12211217;; i = -1184275)
    {
      localDrawable.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
      return;
      cancelPositionAnimator();
      if (paramBoolean) {}
      for (float f = 1.0F;; f = 0.0F)
      {
        setThumbPosition(f);
        break;
      }
      i = -3684409;
      break label50;
    }
  }
  
  public void setSplitTrack(boolean paramBoolean)
  {
    this.mSplitTrack = paramBoolean;
    invalidate();
  }
  
  public void setSwitchMinWidth(int paramInt)
  {
    this.mSwitchMinWidth = paramInt;
    requestLayout();
  }
  
  public void setSwitchPadding(int paramInt)
  {
    this.mSwitchPadding = paramInt;
    requestLayout();
  }
  
  public void setThumbDrawable(Drawable paramDrawable)
  {
    if (this.mThumbDrawable != null) {
      this.mThumbDrawable.setCallback(null);
    }
    this.mThumbDrawable = paramDrawable;
    if (paramDrawable != null) {
      paramDrawable.setCallback(this);
    }
    requestLayout();
  }
  
  public void setThumbTextPadding(int paramInt)
  {
    this.mThumbTextPadding = paramInt;
    requestLayout();
  }
  
  public void setTrackDrawable(Drawable paramDrawable)
  {
    if (this.mTrackDrawable != null) {
      this.mTrackDrawable.setCallback(null);
    }
    this.mTrackDrawable = paramDrawable;
    if (paramDrawable != null) {
      paramDrawable.setCallback(this);
    }
    requestLayout();
  }
  
  public void toggle()
  {
    if (!isChecked()) {}
    for (boolean bool = true;; bool = false)
    {
      setChecked(bool);
      return;
    }
  }
  
  protected boolean verifyDrawable(Drawable paramDrawable)
  {
    return (super.verifyDrawable(paramDrawable)) || (paramDrawable == this.mThumbDrawable) || (paramDrawable == this.mTrackDrawable);
  }
  
  public static class Insets
  {
    public static final Insets NONE = new Insets(AndroidUtilities.dp(4.0F), 0, AndroidUtilities.dp(4.0F), 0);
    public final int bottom;
    public final int left;
    public final int right;
    public final int top;
    
    private Insets(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this.left = paramInt1;
      this.top = paramInt2;
      this.right = paramInt3;
      this.bottom = paramInt4;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/Switch.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */