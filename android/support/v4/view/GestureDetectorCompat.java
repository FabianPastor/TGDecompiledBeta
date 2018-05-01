package android.support.v4.view;

import android.content.Context;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

public final class GestureDetectorCompat
{
  private final GestureDetectorCompatImpl mImpl;
  
  public GestureDetectorCompat(Context paramContext, GestureDetector.OnGestureListener paramOnGestureListener)
  {
    this(paramContext, paramOnGestureListener, null);
  }
  
  public GestureDetectorCompat(Context paramContext, GestureDetector.OnGestureListener paramOnGestureListener, Handler paramHandler)
  {
    if (Build.VERSION.SDK_INT > 17) {}
    for (this.mImpl = new GestureDetectorCompatImplJellybeanMr2(paramContext, paramOnGestureListener, paramHandler);; this.mImpl = new GestureDetectorCompatImplBase(paramContext, paramOnGestureListener, paramHandler)) {
      return;
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    return this.mImpl.onTouchEvent(paramMotionEvent);
  }
  
  static abstract interface GestureDetectorCompatImpl
  {
    public abstract boolean onTouchEvent(MotionEvent paramMotionEvent);
  }
  
  static class GestureDetectorCompatImplBase
    implements GestureDetectorCompat.GestureDetectorCompatImpl
  {
    private static final int DOUBLE_TAP_TIMEOUT = ViewConfiguration.getDoubleTapTimeout();
    private static final int LONGPRESS_TIMEOUT = ;
    private static final int TAP_TIMEOUT = ViewConfiguration.getTapTimeout();
    private boolean mAlwaysInBiggerTapRegion;
    private boolean mAlwaysInTapRegion;
    MotionEvent mCurrentDownEvent;
    boolean mDeferConfirmSingleTap;
    GestureDetector.OnDoubleTapListener mDoubleTapListener;
    private int mDoubleTapSlopSquare;
    private float mDownFocusX;
    private float mDownFocusY;
    private final Handler mHandler;
    private boolean mInLongPress;
    private boolean mIsDoubleTapping;
    private boolean mIsLongpressEnabled;
    private float mLastFocusX;
    private float mLastFocusY;
    final GestureDetector.OnGestureListener mListener;
    private int mMaximumFlingVelocity;
    private int mMinimumFlingVelocity;
    private MotionEvent mPreviousUpEvent;
    boolean mStillDown;
    private int mTouchSlopSquare;
    private VelocityTracker mVelocityTracker;
    
    GestureDetectorCompatImplBase(Context paramContext, GestureDetector.OnGestureListener paramOnGestureListener, Handler paramHandler)
    {
      if (paramHandler != null) {}
      for (this.mHandler = new GestureHandler(paramHandler);; this.mHandler = new GestureHandler())
      {
        this.mListener = paramOnGestureListener;
        if ((paramOnGestureListener instanceof GestureDetector.OnDoubleTapListener)) {
          setOnDoubleTapListener((GestureDetector.OnDoubleTapListener)paramOnGestureListener);
        }
        init(paramContext);
        return;
      }
    }
    
    private void cancel()
    {
      this.mHandler.removeMessages(1);
      this.mHandler.removeMessages(2);
      this.mHandler.removeMessages(3);
      this.mVelocityTracker.recycle();
      this.mVelocityTracker = null;
      this.mIsDoubleTapping = false;
      this.mStillDown = false;
      this.mAlwaysInTapRegion = false;
      this.mAlwaysInBiggerTapRegion = false;
      this.mDeferConfirmSingleTap = false;
      if (this.mInLongPress) {
        this.mInLongPress = false;
      }
    }
    
    private void cancelTaps()
    {
      this.mHandler.removeMessages(1);
      this.mHandler.removeMessages(2);
      this.mHandler.removeMessages(3);
      this.mIsDoubleTapping = false;
      this.mAlwaysInTapRegion = false;
      this.mAlwaysInBiggerTapRegion = false;
      this.mDeferConfirmSingleTap = false;
      if (this.mInLongPress) {
        this.mInLongPress = false;
      }
    }
    
    private void init(Context paramContext)
    {
      if (paramContext == null) {
        throw new IllegalArgumentException("Context must not be null");
      }
      if (this.mListener == null) {
        throw new IllegalArgumentException("OnGestureListener must not be null");
      }
      this.mIsLongpressEnabled = true;
      paramContext = ViewConfiguration.get(paramContext);
      int i = paramContext.getScaledTouchSlop();
      int j = paramContext.getScaledDoubleTapSlop();
      this.mMinimumFlingVelocity = paramContext.getScaledMinimumFlingVelocity();
      this.mMaximumFlingVelocity = paramContext.getScaledMaximumFlingVelocity();
      this.mTouchSlopSquare = (i * i);
      this.mDoubleTapSlopSquare = (j * j);
    }
    
    private boolean isConsideredDoubleTap(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, MotionEvent paramMotionEvent3)
    {
      boolean bool1 = false;
      boolean bool2;
      if (!this.mAlwaysInBiggerTapRegion) {
        bool2 = bool1;
      }
      for (;;)
      {
        return bool2;
        bool2 = bool1;
        if (paramMotionEvent3.getEventTime() - paramMotionEvent2.getEventTime() <= DOUBLE_TAP_TIMEOUT)
        {
          int i = (int)paramMotionEvent1.getX() - (int)paramMotionEvent3.getX();
          int j = (int)paramMotionEvent1.getY() - (int)paramMotionEvent3.getY();
          bool2 = bool1;
          if (i * i + j * j < this.mDoubleTapSlopSquare) {
            bool2 = true;
          }
        }
      }
    }
    
    void dispatchLongPress()
    {
      this.mHandler.removeMessages(3);
      this.mDeferConfirmSingleTap = false;
      this.mInLongPress = true;
      this.mListener.onLongPress(this.mCurrentDownEvent);
    }
    
    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      int i = paramMotionEvent.getAction();
      if (this.mVelocityTracker == null) {
        this.mVelocityTracker = VelocityTracker.obtain();
      }
      this.mVelocityTracker.addMovement(paramMotionEvent);
      int j;
      int m;
      label49:
      float f1;
      float f2;
      int n;
      int i1;
      if ((i & 0xFF) == 6)
      {
        j = 1;
        if (j == 0) {
          break label89;
        }
        m = paramMotionEvent.getActionIndex();
        f1 = 0.0F;
        f2 = 0.0F;
        n = paramMotionEvent.getPointerCount();
        i1 = 0;
        label64:
        if (i1 >= n) {
          break label120;
        }
        if (m != i1) {
          break label95;
        }
      }
      for (;;)
      {
        i1++;
        break label64;
        j = 0;
        break;
        label89:
        m = -1;
        break label49;
        label95:
        f1 += paramMotionEvent.getX(i1);
        f2 += paramMotionEvent.getY(i1);
      }
      label120:
      boolean bool2;
      boolean bool3;
      boolean bool4;
      boolean bool5;
      if (j != 0)
      {
        j = n - 1;
        f1 /= j;
        f2 /= j;
        m = 0;
        bool2 = false;
        bool3 = false;
        bool4 = false;
        bool5 = bool4;
        switch (i & 0xFF)
        {
        default: 
          bool5 = bool4;
        }
      }
      for (;;)
      {
        return bool5;
        j = n;
        break;
        this.mLastFocusX = f1;
        this.mDownFocusX = f1;
        this.mLastFocusY = f2;
        this.mDownFocusY = f2;
        cancelTaps();
        bool5 = bool4;
        continue;
        this.mLastFocusX = f1;
        this.mDownFocusX = f1;
        this.mLastFocusY = f2;
        this.mDownFocusY = f2;
        this.mVelocityTracker.computeCurrentVelocity(1000, this.mMaximumFlingVelocity);
        m = paramMotionEvent.getActionIndex();
        j = paramMotionEvent.getPointerId(m);
        f2 = this.mVelocityTracker.getXVelocity(j);
        f1 = this.mVelocityTracker.getYVelocity(j);
        j = 0;
        bool5 = bool4;
        if (j < n)
        {
          if (j == m) {}
          do
          {
            j++;
            break;
            i1 = paramMotionEvent.getPointerId(j);
          } while (f2 * this.mVelocityTracker.getXVelocity(i1) + f1 * this.mVelocityTracker.getYVelocity(i1) >= 0.0F);
          this.mVelocityTracker.clear();
          bool5 = bool4;
          continue;
          j = m;
          if (this.mDoubleTapListener != null)
          {
            bool5 = this.mHandler.hasMessages(3);
            if (bool5) {
              this.mHandler.removeMessages(3);
            }
            if ((this.mCurrentDownEvent == null) || (this.mPreviousUpEvent == null) || (!bool5) || (!isConsideredDoubleTap(this.mCurrentDownEvent, this.mPreviousUpEvent, paramMotionEvent))) {
              break label653;
            }
            this.mIsDoubleTapping = true;
          }
          for (boolean bool1 = false | this.mDoubleTapListener.onDoubleTap(this.mCurrentDownEvent) | this.mDoubleTapListener.onDoubleTapEvent(paramMotionEvent);; bool1 = m)
          {
            this.mLastFocusX = f1;
            this.mDownFocusX = f1;
            this.mLastFocusY = f2;
            this.mDownFocusY = f2;
            if (this.mCurrentDownEvent != null) {
              this.mCurrentDownEvent.recycle();
            }
            this.mCurrentDownEvent = MotionEvent.obtain(paramMotionEvent);
            this.mAlwaysInTapRegion = true;
            this.mAlwaysInBiggerTapRegion = true;
            this.mStillDown = true;
            this.mInLongPress = false;
            this.mDeferConfirmSingleTap = false;
            if (this.mIsLongpressEnabled)
            {
              this.mHandler.removeMessages(2);
              this.mHandler.sendEmptyMessageAtTime(2, this.mCurrentDownEvent.getDownTime() + TAP_TIMEOUT + LONGPRESS_TIMEOUT);
            }
            this.mHandler.sendEmptyMessageAtTime(1, this.mCurrentDownEvent.getDownTime() + TAP_TIMEOUT);
            bool5 = bool1 | this.mListener.onDown(paramMotionEvent);
            break;
            label653:
            this.mHandler.sendEmptyMessageDelayed(3, DOUBLE_TAP_TIMEOUT);
          }
          bool5 = bool4;
          if (!this.mInLongPress)
          {
            float f3 = this.mLastFocusX - f1;
            float f4 = this.mLastFocusY - f2;
            if (this.mIsDoubleTapping)
            {
              bool5 = false | this.mDoubleTapListener.onDoubleTapEvent(paramMotionEvent);
            }
            else
            {
              int k;
              if (this.mAlwaysInTapRegion)
              {
                m = (int)(f1 - this.mDownFocusX);
                k = (int)(f2 - this.mDownFocusY);
                k = m * m + k * k;
                bool4 = bool2;
                if (k > this.mTouchSlopSquare)
                {
                  bool4 = this.mListener.onScroll(this.mCurrentDownEvent, paramMotionEvent, f3, f4);
                  this.mLastFocusX = f1;
                  this.mLastFocusY = f2;
                  this.mAlwaysInTapRegion = false;
                  this.mHandler.removeMessages(3);
                  this.mHandler.removeMessages(1);
                  this.mHandler.removeMessages(2);
                }
                bool5 = bool4;
                if (k > this.mTouchSlopSquare)
                {
                  this.mAlwaysInBiggerTapRegion = false;
                  bool5 = bool4;
                }
              }
              else if (Math.abs(f3) < 1.0F)
              {
                bool5 = bool4;
                if (Math.abs(f4) < 1.0F) {}
              }
              else
              {
                bool5 = this.mListener.onScroll(this.mCurrentDownEvent, paramMotionEvent, f3, f4);
                this.mLastFocusX = f1;
                this.mLastFocusY = f2;
                continue;
                this.mStillDown = false;
                MotionEvent localMotionEvent = MotionEvent.obtain(paramMotionEvent);
                if (this.mIsDoubleTapping) {
                  bool5 = false | this.mDoubleTapListener.onDoubleTapEvent(paramMotionEvent);
                }
                for (;;)
                {
                  if (this.mPreviousUpEvent != null) {
                    this.mPreviousUpEvent.recycle();
                  }
                  this.mPreviousUpEvent = localMotionEvent;
                  if (this.mVelocityTracker != null)
                  {
                    this.mVelocityTracker.recycle();
                    this.mVelocityTracker = null;
                  }
                  this.mIsDoubleTapping = false;
                  this.mDeferConfirmSingleTap = false;
                  this.mHandler.removeMessages(1);
                  this.mHandler.removeMessages(2);
                  break;
                  if (this.mInLongPress)
                  {
                    this.mHandler.removeMessages(3);
                    this.mInLongPress = false;
                    bool5 = bool3;
                  }
                  else if (this.mAlwaysInTapRegion)
                  {
                    bool4 = this.mListener.onSingleTapUp(paramMotionEvent);
                    bool5 = bool4;
                    if (this.mDeferConfirmSingleTap)
                    {
                      bool5 = bool4;
                      if (this.mDoubleTapListener != null)
                      {
                        this.mDoubleTapListener.onSingleTapConfirmed(paramMotionEvent);
                        bool5 = bool4;
                      }
                    }
                  }
                  else
                  {
                    VelocityTracker localVelocityTracker = this.mVelocityTracker;
                    k = paramMotionEvent.getPointerId(0);
                    localVelocityTracker.computeCurrentVelocity(1000, this.mMaximumFlingVelocity);
                    f1 = localVelocityTracker.getYVelocity(k);
                    f2 = localVelocityTracker.getXVelocity(k);
                    if (Math.abs(f1) <= this.mMinimumFlingVelocity)
                    {
                      bool5 = bool3;
                      if (Math.abs(f2) <= this.mMinimumFlingVelocity) {}
                    }
                    else
                    {
                      bool5 = this.mListener.onFling(this.mCurrentDownEvent, paramMotionEvent, f2, f1);
                    }
                  }
                }
                cancel();
                bool5 = bool4;
              }
            }
          }
        }
      }
    }
    
    public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener paramOnDoubleTapListener)
    {
      this.mDoubleTapListener = paramOnDoubleTapListener;
    }
    
    private class GestureHandler
      extends Handler
    {
      GestureHandler() {}
      
      GestureHandler(Handler paramHandler)
      {
        super();
      }
      
      public void handleMessage(Message paramMessage)
      {
        switch (paramMessage.what)
        {
        default: 
          throw new RuntimeException("Unknown message " + paramMessage);
        case 1: 
          GestureDetectorCompat.GestureDetectorCompatImplBase.this.mListener.onShowPress(GestureDetectorCompat.GestureDetectorCompatImplBase.this.mCurrentDownEvent);
        }
        for (;;)
        {
          return;
          GestureDetectorCompat.GestureDetectorCompatImplBase.this.dispatchLongPress();
          continue;
          if (GestureDetectorCompat.GestureDetectorCompatImplBase.this.mDoubleTapListener != null) {
            if (!GestureDetectorCompat.GestureDetectorCompatImplBase.this.mStillDown) {
              GestureDetectorCompat.GestureDetectorCompatImplBase.this.mDoubleTapListener.onSingleTapConfirmed(GestureDetectorCompat.GestureDetectorCompatImplBase.this.mCurrentDownEvent);
            } else {
              GestureDetectorCompat.GestureDetectorCompatImplBase.this.mDeferConfirmSingleTap = true;
            }
          }
        }
      }
    }
  }
  
  static class GestureDetectorCompatImplJellybeanMr2
    implements GestureDetectorCompat.GestureDetectorCompatImpl
  {
    private final GestureDetector mDetector;
    
    GestureDetectorCompatImplJellybeanMr2(Context paramContext, GestureDetector.OnGestureListener paramOnGestureListener, Handler paramHandler)
    {
      this.mDetector = new GestureDetector(paramContext, paramOnGestureListener, paramHandler);
    }
    
    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      return this.mDetector.onTouchEvent(paramMotionEvent);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/view/GestureDetectorCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */