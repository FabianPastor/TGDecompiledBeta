package org.telegram.ui.Components.Crop;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import org.telegram.messenger.AndroidUtilities;

public class CropGestureDetector
{
  private static final int INVALID_POINTER_ID = -1;
  private int mActivePointerId;
  private int mActivePointerIndex;
  private ScaleGestureDetector mDetector;
  private boolean mIsDragging;
  float mLastTouchX;
  float mLastTouchY;
  private CropGestureListener mListener;
  final float mMinimumVelocity;
  final float mTouchSlop;
  private VelocityTracker mVelocityTracker;
  private boolean started;
  
  public CropGestureDetector(Context paramContext)
  {
    this.mMinimumVelocity = ViewConfiguration.get(paramContext).getScaledMinimumFlingVelocity();
    this.mTouchSlop = AndroidUtilities.dp(1.0F);
    this.mActivePointerId = -1;
    this.mActivePointerIndex = 0;
    this.mDetector = new ScaleGestureDetector(paramContext, new ScaleGestureDetector.OnScaleGestureListener()
    {
      public boolean onScale(ScaleGestureDetector paramAnonymousScaleGestureDetector)
      {
        float f = paramAnonymousScaleGestureDetector.getScaleFactor();
        if ((Float.isNaN(f)) || (Float.isInfinite(f))) {}
        for (boolean bool = false;; bool = true)
        {
          return bool;
          CropGestureDetector.this.mListener.onScale(f, paramAnonymousScaleGestureDetector.getFocusX(), paramAnonymousScaleGestureDetector.getFocusY());
        }
      }
      
      public boolean onScaleBegin(ScaleGestureDetector paramAnonymousScaleGestureDetector)
      {
        return true;
      }
      
      public void onScaleEnd(ScaleGestureDetector paramAnonymousScaleGestureDetector) {}
    });
  }
  
  float getActiveX(MotionEvent paramMotionEvent)
  {
    try
    {
      f = paramMotionEvent.getX(this.mActivePointerIndex);
      return f;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        float f = paramMotionEvent.getX();
      }
    }
  }
  
  float getActiveY(MotionEvent paramMotionEvent)
  {
    try
    {
      f = paramMotionEvent.getY(this.mActivePointerIndex);
      return f;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        float f = paramMotionEvent.getY();
      }
    }
  }
  
  public boolean isDragging()
  {
    return this.mIsDragging;
  }
  
  public boolean isScaling()
  {
    return this.mDetector.isInProgress();
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    this.mDetector.onTouchEvent(paramMotionEvent);
    int i = 0;
    int j;
    switch (paramMotionEvent.getAction() & 0xFF)
    {
    case 2: 
    case 4: 
    case 5: 
    default: 
      j = i;
      if (this.mActivePointerId != -1) {
        j = this.mActivePointerId;
      }
      this.mActivePointerIndex = paramMotionEvent.findPointerIndex(j);
      switch (paramMotionEvent.getAction())
      {
      }
      break;
    }
    for (;;)
    {
      return true;
      this.mActivePointerId = paramMotionEvent.getPointerId(0);
      break;
      this.mActivePointerId = -1;
      break;
      j = (0xFF00 & paramMotionEvent.getAction()) >> 8;
      if (paramMotionEvent.getPointerId(j) != this.mActivePointerId) {
        break;
      }
      if (j == 0) {}
      for (j = 1;; j = 0)
      {
        this.mActivePointerId = paramMotionEvent.getPointerId(j);
        this.mLastTouchX = paramMotionEvent.getX(j);
        this.mLastTouchY = paramMotionEvent.getY(j);
        break;
      }
      if (!this.started)
      {
        this.mVelocityTracker = VelocityTracker.obtain();
        if (this.mVelocityTracker != null) {
          this.mVelocityTracker.addMovement(paramMotionEvent);
        }
        this.mLastTouchX = getActiveX(paramMotionEvent);
        this.mLastTouchY = getActiveY(paramMotionEvent);
        this.mIsDragging = false;
        this.started = true;
      }
      else
      {
        float f1 = getActiveX(paramMotionEvent);
        float f2 = getActiveY(paramMotionEvent);
        float f3 = f1 - this.mLastTouchX;
        float f4 = f2 - this.mLastTouchY;
        if (!this.mIsDragging) {
          if ((float)Math.sqrt(f3 * f3 + f4 * f4) < this.mTouchSlop) {
            break label388;
          }
        }
        label388:
        for (boolean bool = true;; bool = false)
        {
          this.mIsDragging = bool;
          if (!this.mIsDragging) {
            break;
          }
          this.mListener.onDrag(f3, f4);
          this.mLastTouchX = f1;
          this.mLastTouchY = f2;
          if (this.mVelocityTracker == null) {
            break;
          }
          this.mVelocityTracker.addMovement(paramMotionEvent);
          break;
        }
        if (this.mVelocityTracker != null)
        {
          this.mVelocityTracker.recycle();
          this.mVelocityTracker = null;
        }
        this.started = false;
        this.mIsDragging = false;
        continue;
        if (this.mIsDragging)
        {
          if (this.mVelocityTracker != null)
          {
            this.mLastTouchX = getActiveX(paramMotionEvent);
            this.mLastTouchY = getActiveY(paramMotionEvent);
            this.mVelocityTracker.addMovement(paramMotionEvent);
            this.mVelocityTracker.computeCurrentVelocity(1000);
            f3 = this.mVelocityTracker.getXVelocity();
            f2 = this.mVelocityTracker.getYVelocity();
            if (Math.max(Math.abs(f3), Math.abs(f2)) >= this.mMinimumVelocity) {
              this.mListener.onFling(this.mLastTouchX, this.mLastTouchY, -f3, -f2);
            }
          }
          this.mIsDragging = false;
        }
        if (this.mVelocityTracker != null)
        {
          this.mVelocityTracker.recycle();
          this.mVelocityTracker = null;
        }
        this.started = false;
      }
    }
  }
  
  public void setOnGestureListener(CropGestureListener paramCropGestureListener)
  {
    this.mListener = paramCropGestureListener;
  }
  
  public static abstract interface CropGestureListener
  {
    public abstract void onDrag(float paramFloat1, float paramFloat2);
    
    public abstract void onFling(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4);
    
    public abstract void onScale(float paramFloat1, float paramFloat2, float paramFloat3);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/Crop/CropGestureDetector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */