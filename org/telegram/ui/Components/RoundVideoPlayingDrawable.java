package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class RoundVideoPlayingDrawable
  extends Drawable
{
  private long lastUpdateTime = 0L;
  private Paint paint = new Paint(1);
  private View parentView;
  private float progress1 = 0.47F;
  private int progress1Direction = 1;
  private float progress2 = 0.0F;
  private int progress2Direction = 1;
  private float progress3 = 0.32F;
  private int progress3Direction = 1;
  private boolean started = false;
  
  public RoundVideoPlayingDrawable(View paramView)
  {
    this.parentView = paramView;
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
    this.progress1 += (float)l1 / 300.0F * this.progress1Direction;
    if (this.progress1 > 1.0F)
    {
      this.progress1Direction = -1;
      this.progress1 = 1.0F;
      this.progress2 += (float)l1 / 310.0F * this.progress2Direction;
      if (this.progress2 <= 1.0F) {
        break label177;
      }
      this.progress2Direction = -1;
      this.progress2 = 1.0F;
      label108:
      this.progress3 += (float)l1 / 320.0F * this.progress3Direction;
      if (this.progress3 <= 1.0F) {
        break label199;
      }
      this.progress3Direction = -1;
      this.progress3 = 1.0F;
    }
    for (;;)
    {
      this.parentView.invalidate();
      return;
      if (this.progress1 >= 0.0F) {
        break;
      }
      this.progress1Direction = 1;
      this.progress1 = 0.0F;
      break;
      label177:
      if (this.progress2 >= 0.0F) {
        break label108;
      }
      this.progress2Direction = 1;
      this.progress2 = 0.0F;
      break label108;
      label199:
      if (this.progress3 < 0.0F)
      {
        this.progress3Direction = 1;
        this.progress3 = 0.0F;
      }
    }
  }
  
  public void draw(Canvas paramCanvas)
  {
    this.paint.setColor(Theme.getColor("chat_mediaTimeText"));
    int i = getBounds().left;
    int j = getBounds().top;
    for (int k = 0; k < 3; k++)
    {
      paramCanvas.drawRect(AndroidUtilities.dp(2.0F) + i, AndroidUtilities.dp(this.progress1 * 7.0F + 2.0F) + j, AndroidUtilities.dp(4.0F) + i, AndroidUtilities.dp(10.0F) + j, this.paint);
      paramCanvas.drawRect(AndroidUtilities.dp(5.0F) + i, AndroidUtilities.dp(this.progress2 * 7.0F + 2.0F) + j, AndroidUtilities.dp(7.0F) + i, AndroidUtilities.dp(10.0F) + j, this.paint);
      paramCanvas.drawRect(AndroidUtilities.dp(8.0F) + i, AndroidUtilities.dp(this.progress3 * 7.0F + 2.0F) + j, AndroidUtilities.dp(10.0F) + i, AndroidUtilities.dp(10.0F) + j, this.paint);
    }
    if (this.started) {
      update();
    }
  }
  
  public int getIntrinsicHeight()
  {
    return AndroidUtilities.dp(12.0F);
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
  
  public void start()
  {
    if (this.started) {}
    for (;;)
    {
      return;
      this.lastUpdateTime = System.currentTimeMillis();
      this.started = true;
      this.parentView.invalidate();
    }
  }
  
  public void stop()
  {
    if (!this.started) {}
    for (;;)
    {
      return;
      this.started = false;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/RoundVideoPlayingDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */