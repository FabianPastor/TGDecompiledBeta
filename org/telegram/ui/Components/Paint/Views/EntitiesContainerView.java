package org.telegram.ui.Components.Paint.Views;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.widget.FrameLayout;

public class EntitiesContainerView
  extends FrameLayout
  implements ScaleGestureDetector.OnScaleGestureListener, RotationGestureDetector.OnRotationGestureListener
{
  private EntitiesContainerViewDelegate delegate;
  private ScaleGestureDetector gestureDetector = new ScaleGestureDetector(paramContext, this);
  private boolean hasTransformed;
  private float previousAngle;
  private float previousScale = 1.0F;
  private RotationGestureDetector rotationGestureDetector = new RotationGestureDetector(this);
  
  public EntitiesContainerView(Context paramContext, EntitiesContainerViewDelegate paramEntitiesContainerViewDelegate)
  {
    super(paramContext);
    this.delegate = paramEntitiesContainerViewDelegate;
  }
  
  public void bringViewToFront(EntityView paramEntityView)
  {
    if (indexOfChild(paramEntityView) != getChildCount() - 1)
    {
      removeView(paramEntityView);
      addView(paramEntityView, getChildCount());
    }
  }
  
  public int entitiesCount()
  {
    int i = 0;
    int j = 0;
    if (j < getChildCount())
    {
      if (!(getChildAt(j) instanceof EntityView)) {}
      for (;;)
      {
        j++;
        break;
        i++;
      }
    }
    return i;
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((paramMotionEvent.getPointerCount() == 2) && (this.delegate.shouldReceiveTouches())) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public void onRotation(RotationGestureDetector paramRotationGestureDetector)
  {
    EntityView localEntityView = this.delegate.onSelectedEntityRequest();
    float f1 = paramRotationGestureDetector.getAngle();
    float f2 = this.previousAngle;
    localEntityView.rotate(localEntityView.getRotation() + (f2 - f1));
    this.previousAngle = f1;
  }
  
  public void onRotationBegin(RotationGestureDetector paramRotationGestureDetector)
  {
    this.previousAngle = paramRotationGestureDetector.getStartAngle();
    this.hasTransformed = true;
  }
  
  public void onRotationEnd(RotationGestureDetector paramRotationGestureDetector) {}
  
  public boolean onScale(ScaleGestureDetector paramScaleGestureDetector)
  {
    float f1 = paramScaleGestureDetector.getScaleFactor();
    float f2 = f1 / this.previousScale;
    this.delegate.onSelectedEntityRequest().scale(f2);
    this.previousScale = f1;
    return false;
  }
  
  public boolean onScaleBegin(ScaleGestureDetector paramScaleGestureDetector)
  {
    this.previousScale = 1.0F;
    this.hasTransformed = true;
    return true;
  }
  
  public void onScaleEnd(ScaleGestureDetector paramScaleGestureDetector) {}
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool1 = false;
    boolean bool2;
    if (this.delegate.onSelectedEntityRequest() == null) {
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      int i;
      if (paramMotionEvent.getPointerCount() == 1)
      {
        i = paramMotionEvent.getActionMasked();
        if (i != 0) {
          break label65;
        }
        this.hasTransformed = false;
      }
      label65:
      while ((i != 1) && (i != 2))
      {
        this.gestureDetector.onTouchEvent(paramMotionEvent);
        this.rotationGestureDetector.onTouchEvent(paramMotionEvent);
        bool2 = true;
        break;
      }
      bool2 = bool1;
      if (!this.hasTransformed)
      {
        bool2 = bool1;
        if (this.delegate != null)
        {
          this.delegate.onEntityDeselect();
          bool2 = bool1;
        }
      }
    }
  }
  
  public static abstract interface EntitiesContainerViewDelegate
  {
    public abstract void onEntityDeselect();
    
    public abstract EntityView onSelectedEntityRequest();
    
    public abstract boolean shouldReceiveTouches();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/Paint/Views/EntitiesContainerView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */