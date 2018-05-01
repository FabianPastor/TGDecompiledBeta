package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class ContextProgressView
  extends View
{
  private RectF cicleRect = new RectF();
  private int currentColorType;
  private Paint innerPaint = new Paint(1);
  private long lastUpdateTime;
  private Paint outerPaint = new Paint(1);
  private int radOffset = 0;
  
  public ContextProgressView(Context paramContext, int paramInt)
  {
    super(paramContext);
    this.innerPaint.setStyle(Paint.Style.STROKE);
    this.innerPaint.setStrokeWidth(AndroidUtilities.dp(2.0F));
    this.outerPaint.setStyle(Paint.Style.STROKE);
    this.outerPaint.setStrokeWidth(AndroidUtilities.dp(2.0F));
    this.outerPaint.setStrokeCap(Paint.Cap.ROUND);
    this.currentColorType = paramInt;
    updateColors();
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    this.lastUpdateTime = System.currentTimeMillis();
    invalidate();
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (getVisibility() != 0) {}
    for (;;)
    {
      return;
      long l1 = System.currentTimeMillis();
      long l2 = this.lastUpdateTime;
      this.lastUpdateTime = l1;
      this.radOffset = ((int)(this.radOffset + (float)(360L * (l1 - l2)) / 1000.0F));
      int i = getMeasuredWidth() / 2 - AndroidUtilities.dp(9.0F);
      int j = getMeasuredHeight() / 2 - AndroidUtilities.dp(9.0F);
      this.cicleRect.set(i, j, AndroidUtilities.dp(18.0F) + i, AndroidUtilities.dp(18.0F) + j);
      paramCanvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, AndroidUtilities.dp(9.0F), this.innerPaint);
      paramCanvas.drawArc(this.cicleRect, this.radOffset - 90, 90.0F, false, this.outerPaint);
      invalidate();
    }
  }
  
  public void setVisibility(int paramInt)
  {
    super.setVisibility(paramInt);
    this.lastUpdateTime = System.currentTimeMillis();
    invalidate();
  }
  
  public void updateColors()
  {
    if (this.currentColorType == 0)
    {
      this.innerPaint.setColor(Theme.getColor("contextProgressInner1"));
      this.outerPaint.setColor(Theme.getColor("contextProgressOuter1"));
    }
    for (;;)
    {
      invalidate();
      return;
      if (this.currentColorType == 1)
      {
        this.innerPaint.setColor(Theme.getColor("contextProgressInner2"));
        this.outerPaint.setColor(Theme.getColor("contextProgressOuter2"));
      }
      else if (this.currentColorType == 2)
      {
        this.innerPaint.setColor(Theme.getColor("contextProgressInner3"));
        this.outerPaint.setColor(Theme.getColor("contextProgressOuter3"));
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/ContextProgressView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */