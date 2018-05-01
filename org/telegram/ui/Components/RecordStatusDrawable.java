package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class RecordStatusDrawable
  extends StatusDrawable
{
  private boolean isChat = false;
  private long lastUpdateTime = 0L;
  private float progress;
  private RectF rect = new RectF();
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
    for (this.progress += (float)l1 / 300.0F; this.progress > 1.0F; this.progress -= 1.0F) {}
    invalidateSelf();
  }
  
  public void draw(Canvas paramCanvas)
  {
    paramCanvas.save();
    int i = getIntrinsicHeight() / 2;
    float f;
    if (this.isChat)
    {
      f = 1.0F;
      paramCanvas.translate(0.0F, AndroidUtilities.dp(f) + i);
      i = 0;
      label35:
      if (i >= 4) {
        break label155;
      }
      if (i != 0) {
        break label119;
      }
      Theme.chat_statusRecordPaint.setAlpha((int)(this.progress * 255.0F));
    }
    for (;;)
    {
      f = AndroidUtilities.dp(4.0F) * i + AndroidUtilities.dp(4.0F) * this.progress;
      this.rect.set(-f, -f, f, f);
      paramCanvas.drawArc(this.rect, -15.0F, 30.0F, false, Theme.chat_statusRecordPaint);
      i++;
      break label35;
      f = 2.0F;
      break;
      label119:
      if (i == 3) {
        Theme.chat_statusRecordPaint.setAlpha((int)((1.0F - this.progress) * 255.0F));
      } else {
        Theme.chat_statusRecordPaint.setAlpha(255);
      }
    }
    label155:
    paramCanvas.restore();
    if (this.started) {
      update();
    }
  }
  
  public int getIntrinsicHeight()
  {
    return AndroidUtilities.dp(14.0F);
  }
  
  public int getIntrinsicWidth()
  {
    return AndroidUtilities.dp(18.0F);
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/RecordStatusDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */