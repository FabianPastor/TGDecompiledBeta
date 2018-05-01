package org.telegram.ui.Components.Paint.Views;

import android.view.MotionEvent;

public class RotationGestureDetector
{
  private float angle;
  private float fX;
  private float fY;
  private OnRotationGestureListener mListener;
  private float sX;
  private float sY;
  private float startAngle;
  
  public RotationGestureDetector(OnRotationGestureListener paramOnRotationGestureListener)
  {
    this.mListener = paramOnRotationGestureListener;
  }
  
  private float angleBetweenLines(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7, float paramFloat8)
  {
    paramFloat2 = (float)Math.toDegrees((float)Math.atan2(paramFloat2 - paramFloat4, paramFloat1 - paramFloat3) - (float)Math.atan2(paramFloat6 - paramFloat8, paramFloat5 - paramFloat7)) % 360.0F;
    paramFloat1 = paramFloat2;
    if (paramFloat2 < -180.0F) {
      paramFloat1 = paramFloat2 + 360.0F;
    }
    paramFloat2 = paramFloat1;
    if (paramFloat1 > 180.0F) {
      paramFloat2 = paramFloat1 - 360.0F;
    }
    return paramFloat2;
  }
  
  public float getAngle()
  {
    return this.angle;
  }
  
  public float getStartAngle()
  {
    return this.startAngle;
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool = false;
    if (paramMotionEvent.getPointerCount() != 2) {
      return bool;
    }
    switch (paramMotionEvent.getActionMasked())
    {
    }
    for (;;)
    {
      bool = true;
      break;
      this.sX = paramMotionEvent.getX(0);
      this.sY = paramMotionEvent.getY(0);
      this.fX = paramMotionEvent.getX(1);
      this.fY = paramMotionEvent.getY(1);
      continue;
      float f1 = paramMotionEvent.getX(0);
      float f2 = paramMotionEvent.getY(0);
      float f3 = paramMotionEvent.getX(1);
      float f4 = paramMotionEvent.getY(1);
      this.angle = angleBetweenLines(this.fX, this.fY, this.sX, this.sY, f3, f4, f1, f2);
      if (this.mListener != null) {
        if (Float.isNaN(this.startAngle))
        {
          this.startAngle = this.angle;
          this.mListener.onRotationBegin(this);
        }
        else
        {
          this.mListener.onRotation(this);
          continue;
          this.startAngle = NaN.0F;
          continue;
          this.startAngle = NaN.0F;
          if (this.mListener != null) {
            this.mListener.onRotationEnd(this);
          }
        }
      }
    }
  }
  
  public static abstract interface OnRotationGestureListener
  {
    public abstract void onRotation(RotationGestureDetector paramRotationGestureDetector);
    
    public abstract void onRotationBegin(RotationGestureDetector paramRotationGestureDetector);
    
    public abstract void onRotationEnd(RotationGestureDetector paramRotationGestureDetector);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/Paint/Views/RotationGestureDetector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */