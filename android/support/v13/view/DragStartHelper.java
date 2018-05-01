package android.support.v13.view;

import android.annotation.TargetApi;
import android.graphics.Point;
import android.support.annotation.RequiresApi;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;

@TargetApi(13)
@RequiresApi(13)
public class DragStartHelper
{
  private boolean mDragging;
  private int mLastTouchX;
  private int mLastTouchY;
  private final OnDragStartListener mListener;
  private final View.OnLongClickListener mLongClickListener = new View.OnLongClickListener()
  {
    public boolean onLongClick(View paramAnonymousView)
    {
      return DragStartHelper.this.onLongClick(paramAnonymousView);
    }
  };
  private final View.OnTouchListener mTouchListener = new View.OnTouchListener()
  {
    public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
    {
      return DragStartHelper.this.onTouch(paramAnonymousView, paramAnonymousMotionEvent);
    }
  };
  private final View mView;
  
  public DragStartHelper(View paramView, OnDragStartListener paramOnDragStartListener)
  {
    this.mView = paramView;
    this.mListener = paramOnDragStartListener;
  }
  
  public void attach()
  {
    this.mView.setOnLongClickListener(this.mLongClickListener);
    this.mView.setOnTouchListener(this.mTouchListener);
  }
  
  public void detach()
  {
    this.mView.setOnLongClickListener(null);
    this.mView.setOnTouchListener(null);
  }
  
  public void getTouchPosition(Point paramPoint)
  {
    paramPoint.set(this.mLastTouchX, this.mLastTouchY);
  }
  
  public boolean onLongClick(View paramView)
  {
    return this.mListener.onDragStart(paramView, this);
  }
  
  public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
  {
    int i = (int)paramMotionEvent.getX();
    int j = (int)paramMotionEvent.getY();
    switch (paramMotionEvent.getAction())
    {
    default: 
    case 0: 
    case 2: 
      do
      {
        return false;
        this.mLastTouchX = i;
        this.mLastTouchY = j;
        return false;
      } while ((!MotionEventCompat.isFromSource(paramMotionEvent, 8194)) || ((MotionEventCompat.getButtonState(paramMotionEvent) & 0x1) == 0) || (this.mDragging) || ((this.mLastTouchX == i) && (this.mLastTouchY == j)));
      this.mLastTouchX = i;
      this.mLastTouchY = j;
      this.mDragging = this.mListener.onDragStart(paramView, this);
      return this.mDragging;
    }
    this.mDragging = false;
    return false;
  }
  
  public static abstract interface OnDragStartListener
  {
    public abstract boolean onDragStart(View paramView, DragStartHelper paramDragStartHelper);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v13/view/DragStartHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */