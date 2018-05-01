package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class RoundStatusDrawable
  extends StatusDrawable
{
  private boolean isChat = false;
  private long lastUpdateTime = 0L;
  private float progress;
  private int progressDirection = 1;
  private boolean started = false;
  
  private void update()
  {
    long l1 = System.currentTimeMillis();
    long l2 = l1 - this.lastUpdateTime;
    this.lastUpdateTime = l1;
    l1 = l2;
    if (l2 > 50L) {
      l1 = 50L;
    }
    this.progress += (float)(this.progressDirection * l1) / 400.0F;
    if ((this.progressDirection > 0) && (this.progress >= 1.0F)) {
      this.progressDirection = -1;
    }
    for (this.progress = 1.0F;; this.progress = 0.0F)
    {
      do
      {
        invalidateSelf();
        return;
      } while ((this.progressDirection >= 0) || (this.progress > 0.0F));
      this.progressDirection = 1;
    }
  }
  
  public void draw(Canvas paramCanvas)
  {
    Theme.chat_statusPaint.setAlpha((int)(200.0F * this.progress) + 55);
    float f1 = AndroidUtilities.dp(6.0F);
    if (this.isChat) {}
    for (float f2 = 8.0F;; f2 = 9.0F)
    {
      paramCanvas.drawCircle(f1, AndroidUtilities.dp(f2), AndroidUtilities.dp(4.0F), Theme.chat_statusPaint);
      if (this.started) {
        update();
      }
      return;
    }
  }
  
  public int getIntrinsicHeight()
  {
    return AndroidUtilities.dp(10.0F);
  }
  
  public int getIntrinsicWidth()
  {
    return AndroidUtilities.dp(12.0F);
  }
  
  public int getOpacity()
  {
    return 0;
  }
  
  public void setAlpha(int paramInt) {}
  
  public void setColorFilter(ColorFilter paramColorFilter) {}
  
  public void setIsChat(boolean paramBoolean)
  {
    this.isChat = paramBoolean;
  }
  
  public void start()
  {
    this.lastUpdateTime = System.currentTimeMillis();
    this.started = true;
    invalidateSelf();
  }
  
  public void stop()
  {
    this.started = false;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/RoundStatusDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */