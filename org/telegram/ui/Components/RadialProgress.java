package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import org.telegram.messenger.AndroidUtilities;

public class RadialProgress
{
  private static DecelerateInterpolator decelerateInterpolator;
  private static Paint progressPaint;
  private boolean alphaForPrevious = true;
  private float animatedAlphaValue = 1.0F;
  private float animatedProgressValue = 0.0F;
  private float animationProgressStart = 0.0F;
  private RectF cicleRect = new RectF();
  private Drawable currentDrawable;
  private float currentProgress = 0.0F;
  private long currentProgressTime = 0L;
  private boolean currentWithRound;
  private boolean hideCurrentDrawable;
  private long lastUpdateTime = 0L;
  private View parent;
  private Drawable previousDrawable;
  private boolean previousWithRound;
  private int progressColor = -1;
  private RectF progressRect = new RectF();
  private float radOffset = 0.0F;
  
  public RadialProgress(View paramView)
  {
    if (decelerateInterpolator == null)
    {
      decelerateInterpolator = new DecelerateInterpolator();
      progressPaint = new Paint(1);
      progressPaint.setStyle(Paint.Style.STROKE);
      progressPaint.setStrokeCap(Paint.Cap.ROUND);
      progressPaint.setStrokeWidth(AndroidUtilities.dp(3.0F));
    }
    this.parent = paramView;
  }
  
  private void invalidateParent()
  {
    int i = AndroidUtilities.dp(2.0F);
    this.parent.invalidate((int)this.progressRect.left - i, (int)this.progressRect.top - i, (int)this.progressRect.right + i * 2, (int)this.progressRect.bottom + i * 2);
  }
  
  private void updateAnimation(boolean paramBoolean)
  {
    long l1 = System.currentTimeMillis();
    long l2 = l1 - this.lastUpdateTime;
    this.lastUpdateTime = l1;
    if (paramBoolean) {
      if (this.animatedProgressValue != 1.0F)
      {
        this.radOffset += (float)(360L * l2) / 3000.0F;
        f = this.currentProgress - this.animationProgressStart;
        if (f > 0.0F)
        {
          this.currentProgressTime += l2;
          if (this.currentProgressTime >= 300L)
          {
            this.animatedProgressValue = this.currentProgress;
            this.animationProgressStart = this.currentProgress;
            this.currentProgressTime = 0L;
          }
        }
        else
        {
          invalidateParent();
        }
      }
      else if ((this.animatedProgressValue >= 1.0F) && (this.previousDrawable != null))
      {
        this.animatedAlphaValue -= (float)l2 / 200.0F;
        if (this.animatedAlphaValue <= 0.0F)
        {
          this.animatedAlphaValue = 0.0F;
          this.previousDrawable = null;
        }
        invalidateParent();
      }
    }
    while (this.previousDrawable == null) {
      for (;;)
      {
        float f;
        return;
        this.animatedProgressValue = (this.animationProgressStart + decelerateInterpolator.getInterpolation((float)this.currentProgressTime / 300.0F) * f);
      }
    }
    this.animatedAlphaValue -= (float)l2 / 200.0F;
    if (this.animatedAlphaValue <= 0.0F)
    {
      this.animatedAlphaValue = 0.0F;
      this.previousDrawable = null;
    }
    invalidateParent();
  }
  
  public void draw(Canvas paramCanvas)
  {
    label114:
    int i;
    if (this.previousDrawable != null)
    {
      if (this.alphaForPrevious)
      {
        this.previousDrawable.setAlpha((int)(this.animatedAlphaValue * 255.0F));
        this.previousDrawable.setBounds((int)this.progressRect.left, (int)this.progressRect.top, (int)this.progressRect.right, (int)this.progressRect.bottom);
        this.previousDrawable.draw(paramCanvas);
      }
    }
    else
    {
      if ((!this.hideCurrentDrawable) && (this.currentDrawable != null))
      {
        if (this.previousDrawable == null) {
          break label309;
        }
        this.currentDrawable.setAlpha((int)((1.0F - this.animatedAlphaValue) * 255.0F));
        this.currentDrawable.setBounds((int)this.progressRect.left, (int)this.progressRect.top, (int)this.progressRect.right, (int)this.progressRect.bottom);
        this.currentDrawable.draw(paramCanvas);
      }
      if ((!this.currentWithRound) && (!this.previousWithRound)) {
        break label334;
      }
      i = AndroidUtilities.dp(4.0F);
      progressPaint.setColor(this.progressColor);
      if (!this.previousWithRound) {
        break label322;
      }
      progressPaint.setAlpha((int)(this.animatedAlphaValue * 255.0F));
    }
    for (;;)
    {
      this.cicleRect.set(this.progressRect.left + i, this.progressRect.top + i, this.progressRect.right - i, this.progressRect.bottom - i);
      paramCanvas.drawArc(this.cicleRect, this.radOffset - 90.0F, Math.max(4.0F, 360.0F * this.animatedProgressValue), false, progressPaint);
      updateAnimation(true);
      return;
      this.previousDrawable.setAlpha(255);
      break;
      label309:
      this.currentDrawable.setAlpha(255);
      break label114;
      label322:
      progressPaint.setAlpha(255);
    }
    label334:
    updateAnimation(false);
  }
  
  public float getAlpha()
  {
    if ((this.previousDrawable != null) || (this.currentDrawable != null)) {
      return this.animatedAlphaValue;
    }
    return 0.0F;
  }
  
  public void setAlphaForPrevious(boolean paramBoolean)
  {
    this.alphaForPrevious = paramBoolean;
  }
  
  public void setBackground(Drawable paramDrawable, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.lastUpdateTime = System.currentTimeMillis();
    if ((paramBoolean2) && (this.currentDrawable != paramDrawable))
    {
      this.previousDrawable = this.currentDrawable;
      this.previousWithRound = this.currentWithRound;
      this.animatedAlphaValue = 1.0F;
      setProgress(1.0F, paramBoolean2);
    }
    for (;;)
    {
      this.currentWithRound = paramBoolean1;
      this.currentDrawable = paramDrawable;
      if (paramBoolean2) {
        break;
      }
      this.parent.invalidate();
      return;
      this.previousDrawable = null;
      this.previousWithRound = false;
    }
    invalidateParent();
  }
  
  public void setHideCurrentDrawable(boolean paramBoolean)
  {
    this.hideCurrentDrawable = paramBoolean;
  }
  
  public void setProgress(float paramFloat, boolean paramBoolean)
  {
    if ((paramFloat != 1.0F) && (this.animatedAlphaValue != 0.0F) && (this.previousDrawable != null))
    {
      this.animatedAlphaValue = 0.0F;
      this.previousDrawable = null;
    }
    if (!paramBoolean) {
      this.animatedProgressValue = paramFloat;
    }
    for (this.animationProgressStart = paramFloat;; this.animationProgressStart = this.animatedProgressValue)
    {
      this.currentProgress = paramFloat;
      this.currentProgressTime = 0L;
      invalidateParent();
      return;
      if (this.animatedProgressValue > paramFloat) {
        this.animatedProgressValue = paramFloat;
      }
    }
  }
  
  public void setProgressColor(int paramInt)
  {
    this.progressColor = paramInt;
  }
  
  public void setProgressRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.progressRect.set(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public boolean swapBackground(Drawable paramDrawable)
  {
    if (this.currentDrawable != paramDrawable)
    {
      this.currentDrawable = paramDrawable;
      return true;
    }
    return false;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/RadialProgress.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */