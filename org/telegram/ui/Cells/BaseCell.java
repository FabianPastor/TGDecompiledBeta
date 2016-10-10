package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

public class BaseCell
  extends View
{
  private boolean checkingForLongPress = false;
  private CheckForLongPress pendingCheckForLongPress = null;
  private CheckForTap pendingCheckForTap = null;
  private int pressCount = 0;
  
  public BaseCell(Context paramContext)
  {
    super(paramContext);
  }
  
  protected void cancelCheckLongPress()
  {
    this.checkingForLongPress = false;
    if (this.pendingCheckForLongPress != null) {
      removeCallbacks(this.pendingCheckForLongPress);
    }
    if (this.pendingCheckForTap != null) {
      removeCallbacks(this.pendingCheckForTap);
    }
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  protected void onLongPress() {}
  
  protected void setDrawableBounds(Drawable paramDrawable, float paramFloat1, float paramFloat2)
  {
    setDrawableBounds(paramDrawable, (int)paramFloat1, (int)paramFloat2, paramDrawable.getIntrinsicWidth(), paramDrawable.getIntrinsicHeight());
  }
  
  protected void setDrawableBounds(Drawable paramDrawable, int paramInt1, int paramInt2)
  {
    setDrawableBounds(paramDrawable, paramInt1, paramInt2, paramDrawable.getIntrinsicWidth(), paramDrawable.getIntrinsicHeight());
  }
  
  protected void setDrawableBounds(Drawable paramDrawable, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (paramDrawable != null) {
      paramDrawable.setBounds(paramInt1, paramInt2, paramInt1 + paramInt3, paramInt2 + paramInt4);
    }
  }
  
  protected void startCheckLongPress()
  {
    if (this.checkingForLongPress) {
      return;
    }
    this.checkingForLongPress = true;
    if (this.pendingCheckForTap == null) {
      this.pendingCheckForTap = new CheckForTap(null);
    }
    postDelayed(this.pendingCheckForTap, ViewConfiguration.getTapTimeout());
  }
  
  class CheckForLongPress
    implements Runnable
  {
    public int currentPressCount;
    
    CheckForLongPress() {}
    
    public void run()
    {
      if ((BaseCell.this.checkingForLongPress) && (BaseCell.this.getParent() != null) && (this.currentPressCount == BaseCell.this.pressCount))
      {
        BaseCell.access$202(BaseCell.this, false);
        BaseCell.this.performHapticFeedback(0);
        BaseCell.this.onLongPress();
        MotionEvent localMotionEvent = MotionEvent.obtain(0L, 0L, 3, 0.0F, 0.0F, 0);
        BaseCell.this.onTouchEvent(localMotionEvent);
        localMotionEvent.recycle();
      }
    }
  }
  
  private final class CheckForTap
    implements Runnable
  {
    private CheckForTap() {}
    
    public void run()
    {
      if (BaseCell.this.pendingCheckForLongPress == null) {
        BaseCell.access$002(BaseCell.this, new BaseCell.CheckForLongPress(BaseCell.this));
      }
      BaseCell.this.pendingCheckForLongPress.currentPressCount = BaseCell.access$104(BaseCell.this);
      BaseCell.this.postDelayed(BaseCell.this.pendingCheckForLongPress, ViewConfiguration.getLongPressTimeout() - ViewConfiguration.getTapTimeout());
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/BaseCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */