package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewParent;
import org.telegram.messenger.AndroidUtilities;

public class ColorPickerView
  extends View
{
  private static final int[] COLORS = { -65536, -65281, -16776961, -16711681, -16711936, -1, 65280, -65536 };
  private static final String STATE_ANGLE = "angle";
  private static final String STATE_OLD_COLOR = "color";
  private static final String STATE_PARENT = "parent";
  private static final String STATE_SHOW_OLD_COLOR = "showColor";
  private float mAngle;
  private Paint mCenterHaloPaint;
  private int mCenterNewColor;
  private Paint mCenterNewPaint;
  private int mCenterOldColor;
  private Paint mCenterOldPaint;
  private RectF mCenterRectangle = new RectF();
  private int mColorCenterHaloRadius;
  private int mColorCenterRadius;
  private int mColorPointerHaloRadius;
  private int mColorPointerRadius;
  private Paint mColorWheelPaint;
  private int mColorWheelRadius;
  private RectF mColorWheelRectangle = new RectF();
  private int mColorWheelThickness;
  private float[] mHSV = new float[3];
  private Paint mPointerColor;
  private Paint mPointerHaloPaint;
  private int mPreferredColorCenterHaloRadius;
  private int mPreferredColorCenterRadius;
  private int mPreferredColorWheelRadius;
  private boolean mShowCenterOldColor;
  private float mSlopX;
  private float mSlopY;
  private float mTranslationOffset;
  private boolean mUserIsMovingPointer = false;
  private int oldChangedListenerColor;
  private int oldSelectedListenerColor;
  private OnColorChangedListener onColorChangedListener;
  private OnColorSelectedListener onColorSelectedListener;
  
  public ColorPickerView(Context paramContext)
  {
    super(paramContext);
    init(null, 0);
  }
  
  public ColorPickerView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init(paramAttributeSet, 0);
  }
  
  public ColorPickerView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    init(paramAttributeSet, paramInt);
  }
  
  private int ave(int paramInt1, int paramInt2, float paramFloat)
  {
    return Math.round((paramInt2 - paramInt1) * paramFloat) + paramInt1;
  }
  
  private int calculateColor(float paramFloat)
  {
    float f = (float)(paramFloat / 6.283185307179586D);
    paramFloat = f;
    if (f < 0.0F) {
      paramFloat = f + 1.0F;
    }
    if (paramFloat <= 0.0F) {
      return COLORS[0];
    }
    if (paramFloat >= 1.0F) {
      return COLORS[(COLORS.length - 1)];
    }
    paramFloat *= (COLORS.length - 1);
    int j = (int)paramFloat;
    paramFloat -= j;
    int i = COLORS[j];
    j = COLORS[(j + 1)];
    return Color.argb(ave(Color.alpha(i), Color.alpha(j), paramFloat), ave(Color.red(i), Color.red(j), paramFloat), ave(Color.green(i), Color.green(j), paramFloat), ave(Color.blue(i), Color.blue(j), paramFloat));
  }
  
  private float[] calculatePointerPosition(float paramFloat)
  {
    return new float[] { (float)(this.mColorWheelRadius * Math.cos(paramFloat)), (float)(this.mColorWheelRadius * Math.sin(paramFloat)) };
  }
  
  private float colorToAngle(int paramInt)
  {
    float[] arrayOfFloat = new float[3];
    Color.colorToHSV(paramInt, arrayOfFloat);
    return (float)Math.toRadians(-arrayOfFloat[0]);
  }
  
  private void init(AttributeSet paramAttributeSet, int paramInt)
  {
    this.mColorWheelThickness = AndroidUtilities.dp(8.0F);
    this.mColorWheelRadius = AndroidUtilities.dp(124.0F);
    this.mPreferredColorWheelRadius = this.mColorWheelRadius;
    this.mColorCenterRadius = AndroidUtilities.dp(54.0F);
    this.mPreferredColorCenterRadius = this.mColorCenterRadius;
    this.mColorCenterHaloRadius = AndroidUtilities.dp(60.0F);
    this.mPreferredColorCenterHaloRadius = this.mColorCenterHaloRadius;
    this.mColorPointerRadius = AndroidUtilities.dp(14.0F);
    this.mColorPointerHaloRadius = AndroidUtilities.dp(18.0F);
    this.mAngle = -1.5707964F;
    paramAttributeSet = new SweepGradient(0.0F, 0.0F, COLORS, null);
    this.mColorWheelPaint = new Paint(1);
    this.mColorWheelPaint.setShader(paramAttributeSet);
    this.mColorWheelPaint.setStyle(Paint.Style.STROKE);
    this.mColorWheelPaint.setStrokeWidth(this.mColorWheelThickness);
    this.mPointerHaloPaint = new Paint(1);
    this.mPointerHaloPaint.setColor(-16777216);
    this.mPointerHaloPaint.setAlpha(80);
    this.mPointerColor = new Paint(1);
    this.mPointerColor.setColor(calculateColor(this.mAngle));
    this.mCenterNewPaint = new Paint(1);
    this.mCenterNewPaint.setColor(calculateColor(this.mAngle));
    this.mCenterNewPaint.setStyle(Paint.Style.FILL);
    this.mCenterOldPaint = new Paint(1);
    this.mCenterOldPaint.setColor(calculateColor(this.mAngle));
    this.mCenterOldPaint.setStyle(Paint.Style.FILL);
    this.mCenterHaloPaint = new Paint(1);
    this.mCenterHaloPaint.setColor(-16777216);
    this.mCenterHaloPaint.setAlpha(0);
    this.mCenterNewColor = calculateColor(this.mAngle);
    this.mCenterOldColor = calculateColor(this.mAngle);
    this.mShowCenterOldColor = true;
  }
  
  public int getColor()
  {
    return this.mCenterNewColor;
  }
  
  public int getOldCenterColor()
  {
    return this.mCenterOldColor;
  }
  
  public boolean getShowOldCenterColor()
  {
    return this.mShowCenterOldColor;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    paramCanvas.translate(this.mTranslationOffset, this.mTranslationOffset);
    paramCanvas.drawOval(this.mColorWheelRectangle, this.mColorWheelPaint);
    float[] arrayOfFloat = calculatePointerPosition(this.mAngle);
    paramCanvas.drawCircle(arrayOfFloat[0], arrayOfFloat[1], this.mColorPointerHaloRadius, this.mPointerHaloPaint);
    paramCanvas.drawCircle(arrayOfFloat[0], arrayOfFloat[1], this.mColorPointerRadius, this.mPointerColor);
    paramCanvas.drawCircle(0.0F, 0.0F, this.mColorCenterHaloRadius, this.mCenterHaloPaint);
    if (this.mShowCenterOldColor)
    {
      paramCanvas.drawArc(this.mCenterRectangle, 90.0F, 180.0F, true, this.mCenterOldPaint);
      paramCanvas.drawArc(this.mCenterRectangle, 270.0F, 180.0F, true, this.mCenterNewPaint);
      return;
    }
    paramCanvas.drawArc(this.mCenterRectangle, 0.0F, 360.0F, true, this.mCenterNewPaint);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = (this.mPreferredColorWheelRadius + this.mColorPointerHaloRadius) * 2;
    int k = View.MeasureSpec.getMode(paramInt1);
    paramInt1 = View.MeasureSpec.getSize(paramInt1);
    int j = View.MeasureSpec.getMode(paramInt2);
    paramInt2 = View.MeasureSpec.getSize(paramInt2);
    if (k == 1073741824) {
      if (j != 1073741824) {
        break label214;
      }
    }
    for (;;)
    {
      paramInt1 = Math.min(paramInt1, paramInt2);
      setMeasuredDimension(paramInt1, paramInt1);
      this.mTranslationOffset = (paramInt1 * 0.5F);
      this.mColorWheelRadius = (paramInt1 / 2 - this.mColorWheelThickness - this.mColorPointerHaloRadius);
      this.mColorWheelRectangle.set(-this.mColorWheelRadius, -this.mColorWheelRadius, this.mColorWheelRadius, this.mColorWheelRadius);
      this.mColorCenterRadius = ((int)(this.mPreferredColorCenterRadius * (this.mColorWheelRadius / this.mPreferredColorWheelRadius)));
      this.mColorCenterHaloRadius = ((int)(this.mPreferredColorCenterHaloRadius * (this.mColorWheelRadius / this.mPreferredColorWheelRadius)));
      this.mCenterRectangle.set(-this.mColorCenterRadius, -this.mColorCenterRadius, this.mColorCenterRadius, this.mColorCenterRadius);
      return;
      if (k == Integer.MIN_VALUE)
      {
        paramInt1 = Math.min(i, paramInt1);
        break;
      }
      paramInt1 = i;
      break;
      label214:
      if (j == Integer.MIN_VALUE) {
        paramInt2 = Math.min(i, paramInt2);
      } else {
        paramInt2 = i;
      }
    }
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    paramParcelable = (Bundle)paramParcelable;
    super.onRestoreInstanceState(paramParcelable.getParcelable("parent"));
    this.mAngle = paramParcelable.getFloat("angle");
    setOldCenterColor(paramParcelable.getInt("color"));
    this.mShowCenterOldColor = paramParcelable.getBoolean("showColor");
    int i = calculateColor(this.mAngle);
    this.mPointerColor.setColor(i);
    setNewCenterColor(i);
  }
  
  protected Parcelable onSaveInstanceState()
  {
    Parcelable localParcelable = super.onSaveInstanceState();
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("parent", localParcelable);
    localBundle.putFloat("angle", this.mAngle);
    localBundle.putInt("color", this.mCenterOldColor);
    localBundle.putBoolean("showColor", this.mShowCenterOldColor);
    return localBundle;
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    getParent().requestDisallowInterceptTouchEvent(true);
    float f1 = paramMotionEvent.getX() - this.mTranslationOffset;
    float f2 = paramMotionEvent.getY() - this.mTranslationOffset;
    switch (paramMotionEvent.getAction())
    {
    }
    for (;;)
    {
      return true;
      paramMotionEvent = calculatePointerPosition(this.mAngle);
      if ((f1 >= paramMotionEvent[0] - this.mColorPointerHaloRadius) && (f1 <= paramMotionEvent[0] + this.mColorPointerHaloRadius) && (f2 >= paramMotionEvent[1] - this.mColorPointerHaloRadius) && (f2 <= paramMotionEvent[1] + this.mColorPointerHaloRadius))
      {
        this.mSlopX = (f1 - paramMotionEvent[0]);
        this.mSlopY = (f2 - paramMotionEvent[1]);
        this.mUserIsMovingPointer = true;
        invalidate();
      }
      else if ((f1 >= -this.mColorCenterRadius) && (f1 <= this.mColorCenterRadius) && (f2 >= -this.mColorCenterRadius) && (f2 <= this.mColorCenterRadius) && (this.mShowCenterOldColor))
      {
        this.mCenterHaloPaint.setAlpha(80);
        setColor(getOldCenterColor());
        invalidate();
      }
      else
      {
        getParent().requestDisallowInterceptTouchEvent(false);
        return false;
        if (this.mUserIsMovingPointer)
        {
          this.mAngle = ((float)Math.atan2(f2 - this.mSlopY, f1 - this.mSlopX));
          this.mPointerColor.setColor(calculateColor(this.mAngle));
          int i = calculateColor(this.mAngle);
          this.mCenterNewColor = i;
          setNewCenterColor(i);
          invalidate();
        }
        else
        {
          getParent().requestDisallowInterceptTouchEvent(false);
          return false;
          this.mUserIsMovingPointer = false;
          this.mCenterHaloPaint.setAlpha(0);
          if ((this.onColorSelectedListener != null) && (this.mCenterNewColor != this.oldSelectedListenerColor))
          {
            this.onColorSelectedListener.onColorSelected(this.mCenterNewColor);
            this.oldSelectedListenerColor = this.mCenterNewColor;
          }
          invalidate();
          continue;
          if ((this.onColorSelectedListener != null) && (this.mCenterNewColor != this.oldSelectedListenerColor))
          {
            this.onColorSelectedListener.onColorSelected(this.mCenterNewColor);
            this.oldSelectedListenerColor = this.mCenterNewColor;
          }
        }
      }
    }
  }
  
  public void setColor(int paramInt)
  {
    this.mAngle = colorToAngle(paramInt);
    this.mPointerColor.setColor(calculateColor(this.mAngle));
    this.mCenterNewPaint.setColor(calculateColor(this.mAngle));
    invalidate();
  }
  
  public void setNewCenterColor(int paramInt)
  {
    this.mCenterNewColor = paramInt;
    this.mCenterNewPaint.setColor(paramInt);
    if (this.mCenterOldColor == 0)
    {
      this.mCenterOldColor = paramInt;
      this.mCenterOldPaint.setColor(paramInt);
    }
    if ((this.onColorChangedListener != null) && (paramInt != this.oldChangedListenerColor))
    {
      this.onColorChangedListener.onColorChanged(paramInt);
      this.oldChangedListenerColor = paramInt;
    }
    invalidate();
  }
  
  public void setOldCenterColor(int paramInt)
  {
    this.mCenterOldColor = paramInt;
    this.mCenterOldPaint.setColor(paramInt);
    invalidate();
  }
  
  public void setOnColorChangedListener(OnColorChangedListener paramOnColorChangedListener)
  {
    this.onColorChangedListener = paramOnColorChangedListener;
  }
  
  public void setOnColorSelectedListener(OnColorSelectedListener paramOnColorSelectedListener)
  {
    this.onColorSelectedListener = paramOnColorSelectedListener;
  }
  
  public void setShowOldCenterColor(boolean paramBoolean)
  {
    this.mShowCenterOldColor = paramBoolean;
    invalidate();
  }
  
  public static abstract interface OnColorChangedListener
  {
    public abstract void onColorChanged(int paramInt);
  }
  
  public static abstract interface OnColorSelectedListener
  {
    public abstract void onColorSelected(int paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/ColorPickerView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */