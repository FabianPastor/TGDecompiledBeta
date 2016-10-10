package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import org.telegram.messenger.AndroidUtilities;

public class RecordStatusDrawable
  extends Drawable
{
  private boolean isChat = false;
  private long lastUpdateTime = 0L;
  private Paint paint = new Paint(1);
  private float progress;
  private RectF rect = new RectF();
  private boolean started = false;
  
  public RecordStatusDrawable()
  {
    this.paint.setColor(-2758409);
    this.paint.setStyle(Paint.Style.STROKE);
    this.paint.setStrokeWidth(AndroidUtilities.dp(2.0F));
    this.paint.setStrokeCap(Paint.Cap.ROUND);
  }
  
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
        break label160;
      }
      if (i != 0) {
        break label122;
      }
      this.paint.setAlpha((int)(this.progress * 255.0F));
    }
    for (;;)
    {
      f = AndroidUtilities.dp(4.0F) * i + AndroidUtilities.dp(4.0F) * this.progress;
      this.rect.set(-f, -f, f, f);
      paramCanvas.drawArc(this.rect, -15.0F, 30.0F, false, this.paint);
      i += 1;
      break label35;
      f = 2.0F;
      break;
      label122:
      if (i == 3) {
        this.paint.setAlpha((int)((1.0F - this.progress) * 255.0F));
      } else {
        this.paint.setAlpha(255);
      }
    }
    label160:
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