package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.animation.DecelerateInterpolator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.Theme;

public class TypingDotsDrawable
  extends StatusDrawable
{
  private int currentAccount = UserConfig.selectedAccount;
  private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
  private float[] elapsedTimes = { 0.0F, 0.0F, 0.0F };
  private boolean isChat = false;
  private long lastUpdateTime = 0L;
  private float[] scales = new float[3];
  private float[] startTimes = { 0.0F, 150.0F, 300.0F };
  private boolean started = false;
  
  private void checkUpdate()
  {
    if (this.started)
    {
      if (NotificationCenter.getInstance(this.currentAccount).isAnimationInProgress()) {
        break label25;
      }
      update();
    }
    for (;;)
    {
      return;
      label25:
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          TypingDotsDrawable.this.checkUpdate();
        }
      }, 100L);
    }
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
    int i = 0;
    if (i < 3)
    {
      float[] arrayOfFloat = this.elapsedTimes;
      arrayOfFloat[i] += (float)l1;
      float f = this.elapsedTimes[i] - this.startTimes[i];
      if (f > 0.0F) {
        if (f <= 320.0F)
        {
          f = this.decelerateInterpolator.getInterpolation(f / 320.0F);
          this.scales[i] = (1.33F + f);
        }
      }
      for (;;)
      {
        i++;
        break;
        if (f <= 640.0F)
        {
          f = this.decelerateInterpolator.getInterpolation((f - 320.0F) / 320.0F);
          this.scales[i] = (1.0F - f + 1.33F);
        }
        else if (f >= 800.0F)
        {
          this.elapsedTimes[i] = 0.0F;
          this.startTimes[i] = 0.0F;
          this.scales[i] = 1.33F;
        }
        else
        {
          this.scales[i] = 1.33F;
          continue;
          this.scales[i] = 1.33F;
        }
      }
    }
    invalidateSelf();
  }
  
  public void draw(Canvas paramCanvas)
  {
    if (this.isChat) {}
    for (int i = AndroidUtilities.dp(8.5F) + getBounds().top;; i = AndroidUtilities.dp(9.3F) + getBounds().top)
    {
      Theme.chat_statusPaint.setAlpha(255);
      paramCanvas.drawCircle(AndroidUtilities.dp(3.0F), i, this.scales[0] * AndroidUtilities.density, Theme.chat_statusPaint);
      paramCanvas.drawCircle(AndroidUtilities.dp(9.0F), i, this.scales[1] * AndroidUtilities.density, Theme.chat_statusPaint);
      paramCanvas.drawCircle(AndroidUtilities.dp(15.0F), i, this.scales[2] * AndroidUtilities.density, Theme.chat_statusPaint);
      checkUpdate();
      return;
    }
  }
  
  public int getIntrinsicHeight()
  {
    return AndroidUtilities.dp(18.0F);
  }
  
  public int getIntrinsicWidth()
  {
    return AndroidUtilities.dp(18.0F);
  }
  
  public int getOpacity()
  {
    return -2;
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
    for (int i = 0; i < 3; i++)
    {
      this.elapsedTimes[i] = 0.0F;
      this.scales[i] = 1.33F;
    }
    this.startTimes[0] = 0.0F;
    this.startTimes[1] = 150.0F;
    this.startTimes[2] = 300.0F;
    this.started = false;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/TypingDotsDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */