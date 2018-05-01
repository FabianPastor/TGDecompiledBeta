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
import android.support.annotation.Keep;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.CompoundButton;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;

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
    this.mThumbDrawable = paramContext.getResources().getDrawable(NUM);
    if (this.mThumbDrawable != null) {
      this.mThumbDrawable.setCallback(this);
    }
    this.mTrackDrawable = paramContext.getResources().getDrawable(NUM);
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
    if (paramFloat1 < paramFloat2) {}
    for (;;)
    {
      return paramFloat2;
      if (paramFloat1 > paramFloat3) {
        paramFloat2 = paramFloat3;
      } else {
        paramFloat2 = paramFloat1;
      }
    }
  }
  
  private boolean getTargetCheckedState()
  {
    if (this.thumbPosition > 0.5F) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
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
    Rect localRect;
    Insets localInsets;
    if (this.mTrackDrawable != null)
    {
      localRect = this.mTempRect;
      this.mTrackDrawable.getPadding(localRect);
      if (this.mThumbDrawable != null) {
        localInsets = Insets.NONE;
      }
    }
    for (int i = this.mSwitchWidth - this.mThumbWidth - localRect.left - localRect.right - localInsets.left - localInsets.right;; i = 0)
    {
      return i;
      localInsets = Insets.NONE;
      break;
    }
  }
  
  private boolean hitThumb(float paramFloat1, float paramFloat2)
  {
    int i = getThumbOffset();
    this.mThumbDrawable.getPadding(this.mTempRect);
    int j = this.mSwitchTop;
    int k = this.mTouchSlop;
    int m = this.mSwitchLeft + i - this.mTouchSlop;
    int n = this.mThumbWidth;
    i = this.mTempRect.left;
    int i1 = this.mTempRect.right;
    int i2 = this.mTouchSlop;
    int i3 = this.mSwitchBottom;
    int i4 = this.mTouchSlop;
    if ((paramFloat1 > m) && (paramFloat1 < n + m + i + i1 + i2) && (paramFloat2 > j - k) && (paramFloat2 < i3 + i4)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  @Keep
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
        break label115;
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
        break label90;
      }
    }
    for (;;)
    {
      setChecked(bool);
      cancelSuperTouch(paramMotionEvent);
      return;
      i = 0;
      break;
      label90:
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
        label115:
        bool = isChecked();
      }
    }
  }
  
  public void checkColorFilters()
  {
    Drawable localDrawable;
    if (this.mTrackDrawable != null)
    {
      localDrawable = this.mTrackDrawable;
      if (isChecked())
      {
        i = Theme.getColor("switchTrackChecked");
        localDrawable.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
      }
    }
    else if (this.mThumbDrawable != null)
    {
      localDrawable = this.mThumbDrawable;
      if (!isChecked()) {
        break label93;
      }
    }
    label93:
    for (int i = Theme.getColor("switchThumbChecked");; i = Theme.getColor("switchThumb"))
    {
      localDrawable.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
      return;
      i = Theme.getColor("switchTrack");
      break;
    }
  }
  
  public void draw(Canvas paramCanvas)
  {
    Rect localRect = this.mTempRect;
    int i = this.mSwitchLeft;
    int j = this.mSwitchTop;
    int k = this.mSwitchRight;
    int m = this.mSwitchBottom;
    int n = i + getThumbOffset();
    Object localObject;
    if (this.mThumbDrawable != null)
    {
      localObject = Insets.NONE;
      i1 = n;
      if (this.mTrackDrawable != null)
      {
        this.mTrackDrawable.getPadding(localRect);
        int i2 = n + localRect.left;
        n = j;
        int i3 = m;
        int i4 = i3;
        int i5 = i;
        int i6 = k;
        int i7 = n;
        if (localObject != Insets.NONE)
        {
          i1 = i;
          if (((Insets)localObject).left > localRect.left) {
            i1 = i + (((Insets)localObject).left - localRect.left);
          }
          i = n;
          if (((Insets)localObject).top > localRect.top) {
            i = n + (((Insets)localObject).top - localRect.top);
          }
          n = k;
          if (((Insets)localObject).right > localRect.right) {
            n = k - (((Insets)localObject).right - localRect.right);
          }
          i4 = i3;
          i5 = i1;
          i6 = n;
          i7 = i;
          if (((Insets)localObject).bottom > localRect.bottom)
          {
            i4 = i3 - (((Insets)localObject).bottom - localRect.bottom);
            i7 = i;
            i6 = n;
            i5 = i1;
          }
        }
        this.mTrackDrawable.setBounds(i5, i7, i6, i4);
        i1 = i2;
      }
      if (this.mThumbDrawable != null)
      {
        this.mThumbDrawable.getPadding(localRect);
        k = i1 - localRect.left;
        n = this.mThumbWidth + i1 + localRect.right;
        if (AndroidUtilities.density != 1.5F) {
          break label391;
        }
      }
    }
    label391:
    for (int i1 = AndroidUtilities.dp(1.0F);; i1 = 0)
    {
      this.mThumbDrawable.setBounds(k, j + i1, n, m + i1);
      localObject = getBackground();
      if ((localObject != null) && (Build.VERSION.SDK_INT >= 21)) {
        ((Drawable)localObject).setHotspotBounds(k, j, n, m);
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
    if (!LocaleController.isRTL) {}
    for (int i = super.getCompoundPaddingLeft();; i = super.getCompoundPaddingLeft() + this.mSwitchWidth) {
      return i;
    }
  }
  
  public int getCompoundPaddingRight()
  {
    if (LocaleController.isRTL) {}
    for (int i = super.getCompoundPaddingRight();; i = super.getCompoundPaddingRight() + this.mSwitchWidth) {
      return i;
    }
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
          break label155;
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
      label155:
      localDrawable1.draw(paramCanvas);
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    this.wasLayout = true;
    paramInt2 = 0;
    paramInt1 = 0;
    Rect localRect;
    if (this.mThumbDrawable != null)
    {
      localRect = this.mTempRect;
      if (this.mTrackDrawable != null)
      {
        this.mTrackDrawable.getPadding(localRect);
        Insets localInsets = Insets.NONE;
        paramInt2 = Math.max(0, localInsets.left - localRect.left);
        paramInt1 = Math.max(0, localInsets.right - localRect.right);
      }
    }
    else
    {
      if (!LocaleController.isRTL) {
        break label191;
      }
      paramInt4 = getPaddingLeft() + paramInt2;
      paramInt3 = this.mSwitchWidth + paramInt4 - paramInt2 - paramInt1;
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
      this.mSwitchLeft = paramInt4;
      this.mSwitchTop = paramInt2;
      this.mSwitchBottom = paramInt1;
      this.mSwitchRight = paramInt3;
      return;
      localRect.setEmpty();
      break;
      label191:
      paramInt3 = getWidth() - getPaddingRight() - paramInt1;
      paramInt4 = paramInt3 - this.mSwitchWidth + paramInt2 + paramInt1;
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
    int j;
    if (this.mThumbDrawable != null)
    {
      this.mThumbDrawable.getPadding((Rect)localObject);
      i = this.mThumbDrawable.getIntrinsicWidth() - ((Rect)localObject).left - ((Rect)localObject).right;
      j = this.mThumbDrawable.getIntrinsicHeight();
      this.mThumbWidth = i;
      if (this.mTrackDrawable == null) {
        break label208;
      }
      this.mTrackDrawable.getPadding((Rect)localObject);
    }
    for (int i = this.mTrackDrawable.getIntrinsicHeight();; i = 0)
    {
      int k = ((Rect)localObject).left;
      int m = ((Rect)localObject).right;
      int n = k;
      int i1 = m;
      if (this.mThumbDrawable != null)
      {
        localObject = Insets.NONE;
        n = Math.max(k, ((Insets)localObject).left);
        i1 = Math.max(m, ((Insets)localObject).right);
      }
      i1 = Math.max(this.mSwitchMinWidth, this.mThumbWidth * 2 + n + i1);
      j = Math.max(i, j);
      this.mSwitchWidth = i1;
      this.mSwitchHeight = j;
      super.onMeasure(paramInt1, paramInt2);
      if (getMeasuredHeight() < j) {
        setMeasuredDimension(i1, j);
      }
      return;
      i = 0;
      j = 0;
      break;
      label208:
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
      boolean bool = super.onTouchEvent(paramMotionEvent);
      for (;;)
      {
        return bool;
        float f1 = paramMotionEvent.getX();
        float f2 = paramMotionEvent.getY();
        if ((!isEnabled()) || (!hitThumb(f1, f2))) {
          break;
        }
        this.mTouchMode = 1;
        this.mTouchX = f1;
        this.mTouchY = f2;
        break;
        switch (this.mTouchMode)
        {
        case 0: 
        default: 
          break;
        case 1: 
          f1 = paramMotionEvent.getX();
          f2 = paramMotionEvent.getY();
          if ((Math.abs(f1 - this.mTouchX) <= this.mTouchSlop) && (Math.abs(f2 - this.mTouchY) <= this.mTouchSlop)) {
            break;
          }
          this.mTouchMode = 2;
          getParent().requestDisallowInterceptTouchEvent(true);
          this.mTouchX = f1;
          this.mTouchY = f2;
          bool = true;
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
            bool = true;
          }
          else
          {
            if (f1 > 0.0F) {}
            for (f1 = 1.0F;; f1 = -1.0F) {
              break;
            }
            if (this.mTouchMode != 2) {
              break label334;
            }
            stopDrag(paramMotionEvent);
            super.onTouchEvent(paramMotionEvent);
            bool = true;
          }
          break;
        }
      }
      label334:
      this.mTouchMode = 0;
      this.mVelocityTracker.clear();
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
          break label132;
        }
        i = Theme.getColor("switchTrackChecked");
        label52:
        localDrawable.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
      }
      if (this.mThumbDrawable != null)
      {
        localDrawable = this.mThumbDrawable;
        if (!paramBoolean) {
          break label142;
        }
      }
    }
    label132:
    label142:
    for (int i = Theme.getColor("switchThumbChecked");; i = Theme.getColor("switchThumb"))
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
      i = Theme.getColor("switchTrack");
      break label52;
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
    if ((super.verifyDrawable(paramDrawable)) || (paramDrawable == this.mThumbDrawable) || (paramDrawable == this.mTrackDrawable)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
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