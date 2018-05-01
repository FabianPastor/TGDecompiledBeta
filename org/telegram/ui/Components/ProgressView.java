package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Paint;
import org.telegram.messenger.AndroidUtilities;

public class ProgressView
{
  public float currentProgress = 0.0F;
  public int height;
  private Paint innerPaint = new Paint();
  private Paint outerPaint = new Paint();
  public float progressHeight = AndroidUtilities.dp(2.0F);
  public int width;
  
  public void draw(Canvas paramCanvas)
  {
    float f1 = this.height / 2;
    float f2 = this.progressHeight / 2.0F;
    float f3 = this.width;
    float f4 = this.height / 2;
    paramCanvas.drawRect(0.0F, f1 - f2, f3, this.progressHeight / 2.0F + f4, this.innerPaint);
    f1 = this.height / 2;
    f3 = this.progressHeight / 2.0F;
    f4 = this.width;
    f2 = this.currentProgress;
    float f5 = this.height / 2;
    paramCanvas.drawRect(0.0F, f1 - f3, f2 * f4, this.progressHeight / 2.0F + f5, this.outerPaint);
  }
  
  public void setProgress(float paramFloat)
  {
    this.currentProgress = paramFloat;
    if (this.currentProgress < 0.0F) {
      this.currentProgress = 0.0F;
    }
    for (;;)
    {
      return;
      if (this.currentProgress > 1.0F) {
        this.currentProgress = 1.0F;
      }
    }
  }
  
  public void setProgressColors(int paramInt1, int paramInt2)
  {
    this.innerPaint.setColor(paramInt1);
    this.outerPaint.setColor(paramInt2);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/ProgressView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */