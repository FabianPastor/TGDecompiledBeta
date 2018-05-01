package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.graphics.drawable.Drawable.ConstantState;

public class CombinedDrawable
  extends Drawable
  implements Drawable.Callback
{
  private int backHeight;
  private int backWidth;
  private Drawable background;
  private boolean fullSize;
  private Drawable icon;
  private int iconHeight;
  private int iconWidth;
  private int left;
  private int offsetX;
  private int offsetY;
  private int top;
  
  public CombinedDrawable(Drawable paramDrawable1, Drawable paramDrawable2)
  {
    this.background = paramDrawable1;
    this.icon = paramDrawable2;
    if (paramDrawable2 != null) {
      paramDrawable2.setCallback(this);
    }
  }
  
  public CombinedDrawable(Drawable paramDrawable1, Drawable paramDrawable2, int paramInt1, int paramInt2)
  {
    this.background = paramDrawable1;
    this.icon = paramDrawable2;
    this.left = paramInt1;
    this.top = paramInt2;
    if (paramDrawable2 != null) {
      paramDrawable2.setCallback(this);
    }
  }
  
  public void draw(Canvas paramCanvas)
  {
    this.background.setBounds(getBounds());
    this.background.draw(paramCanvas);
    if (this.icon != null)
    {
      if (!this.fullSize) {
        break label53;
      }
      this.icon.setBounds(getBounds());
    }
    for (;;)
    {
      this.icon.draw(paramCanvas);
      return;
      label53:
      int i;
      int j;
      if (this.iconWidth != 0)
      {
        i = getBounds().centerX() - this.iconWidth / 2 + this.left + this.offsetX;
        j = getBounds().centerY() - this.iconHeight / 2 + this.top + this.offsetY;
        this.icon.setBounds(i, j, this.iconWidth + i, this.iconHeight + j);
      }
      else
      {
        j = getBounds().centerX() - this.icon.getIntrinsicWidth() / 2 + this.left;
        i = getBounds().centerY() - this.icon.getIntrinsicHeight() / 2 + this.top;
        this.icon.setBounds(j, i, this.icon.getIntrinsicWidth() + j, this.icon.getIntrinsicHeight() + i);
      }
    }
  }
  
  public Drawable getBackground()
  {
    return this.background;
  }
  
  public Drawable.ConstantState getConstantState()
  {
    return this.icon.getConstantState();
  }
  
  public Drawable getIcon()
  {
    return this.icon;
  }
  
  public int getIntrinsicHeight()
  {
    if (this.backHeight != 0) {}
    for (int i = this.backHeight;; i = this.background.getIntrinsicHeight()) {
      return i;
    }
  }
  
  public int getIntrinsicWidth()
  {
    if (this.backWidth != 0) {}
    for (int i = this.backWidth;; i = this.background.getIntrinsicWidth()) {
      return i;
    }
  }
  
  public int getMinimumHeight()
  {
    if (this.backHeight != 0) {}
    for (int i = this.backHeight;; i = this.background.getMinimumHeight()) {
      return i;
    }
  }
  
  public int getMinimumWidth()
  {
    if (this.backWidth != 0) {}
    for (int i = this.backWidth;; i = this.background.getMinimumWidth()) {
      return i;
    }
  }
  
  public int getOpacity()
  {
    return this.icon.getOpacity();
  }
  
  public int[] getState()
  {
    return this.icon.getState();
  }
  
  public void invalidateDrawable(Drawable paramDrawable)
  {
    invalidateSelf();
  }
  
  public boolean isStateful()
  {
    return this.icon.isStateful();
  }
  
  public void jumpToCurrentState()
  {
    this.icon.jumpToCurrentState();
  }
  
  protected boolean onStateChange(int[] paramArrayOfInt)
  {
    return true;
  }
  
  public void scheduleDrawable(Drawable paramDrawable, Runnable paramRunnable, long paramLong)
  {
    scheduleSelf(paramRunnable, paramLong);
  }
  
  public void setAlpha(int paramInt)
  {
    this.icon.setAlpha(paramInt);
    this.background.setAlpha(paramInt);
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    this.icon.setColorFilter(paramColorFilter);
  }
  
  public void setCustomSize(int paramInt1, int paramInt2)
  {
    this.backWidth = paramInt1;
    this.backHeight = paramInt2;
  }
  
  public void setFullsize(boolean paramBoolean)
  {
    this.fullSize = paramBoolean;
  }
  
  public void setIconOffset(int paramInt1, int paramInt2)
  {
    this.offsetX = paramInt1;
    this.offsetY = paramInt2;
  }
  
  public void setIconSize(int paramInt1, int paramInt2)
  {
    this.iconWidth = paramInt1;
    this.iconHeight = paramInt2;
  }
  
  public boolean setState(int[] paramArrayOfInt)
  {
    this.icon.setState(paramArrayOfInt);
    return true;
  }
  
  public void unscheduleDrawable(Drawable paramDrawable, Runnable paramRunnable)
  {
    unscheduleSelf(paramRunnable);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/CombinedDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */