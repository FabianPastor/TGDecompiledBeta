package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.animation.DecelerateInterpolator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.Theme;

public class PlayingGameDrawable
  extends StatusDrawable
{
  private int currentAccount = UserConfig.selectedAccount;
  private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
  private boolean isChat = false;
  private long lastUpdateTime = 0L;
  private Paint paint = new Paint(1);
  private float progress;
  private RectF rect = new RectF();
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
          PlayingGameDrawable.this.checkUpdate();
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
    if (l2 > 16L) {
      l1 = 16L;
    }
    if (this.progress >= 1.0F) {
      this.progress = 0.0F;
    }
    this.progress += (float)l1 / 300.0F;
    if (this.progress > 1.0F) {
      this.progress = 1.0F;
    }
    invalidateSelf();
  }
  
  public void draw(Canvas paramCanvas)
  {
    int i = AndroidUtilities.dp(10.0F);
    int j = getBounds().top + (getIntrinsicHeight() - i) / 2;
    int k;
    label83:
    int m;
    label86:
    float f1;
    float f2;
    float f3;
    if (this.isChat)
    {
      this.paint.setColor(Theme.getColor("actionBarDefaultSubtitle"));
      this.rect.set(0.0F, j, i, j + i);
      if (this.progress >= 0.5F) {
        break label197;
      }
      k = (int)(35.0F * (1.0F - this.progress / 0.5F));
      m = 0;
      if (m >= 3) {
        break label283;
      }
      f1 = AndroidUtilities.dp(5.0F) * m + AndroidUtilities.dp(9.2F);
      f2 = AndroidUtilities.dp(5.0F);
      f3 = this.progress;
      if (m != 2) {
        break label216;
      }
      this.paint.setAlpha(Math.min(255, (int)(255.0F * this.progress / 0.5F)));
    }
    for (;;)
    {
      paramCanvas.drawCircle(f1 - f2 * f3, i / 2 + j, AndroidUtilities.dp(1.2F), this.paint);
      m++;
      break label86;
      j += AndroidUtilities.dp(1.0F);
      break;
      label197:
      k = (int)(35.0F * (this.progress - 0.5F) / 0.5F);
      break label83;
      label216:
      if (m == 0)
      {
        if (this.progress > 0.5F) {
          this.paint.setAlpha((int)(255.0F * (1.0F - (this.progress - 0.5F) / 0.5F)));
        } else {
          this.paint.setAlpha(255);
        }
      }
      else {
        this.paint.setAlpha(255);
      }
    }
    label283:
    this.paint.setAlpha(255);
    paramCanvas.drawArc(this.rect, k, 360 - k * 2, true, this.paint);
    this.paint.setColor(Theme.getColor("actionBarDefault"));
    paramCanvas.drawCircle(AndroidUtilities.dp(4.0F), i / 2 + j - AndroidUtilities.dp(2.0F), AndroidUtilities.dp(1.0F), this.paint);
    checkUpdate();
  }
  
  public int getIntrinsicHeight()
  {
    return AndroidUtilities.dp(18.0F);
  }
  
  public int getIntrinsicWidth()
  {
    return AndroidUtilities.dp(20.0F);
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
    this.progress = 0.0F;
    this.started = false;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/PlayingGameDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */