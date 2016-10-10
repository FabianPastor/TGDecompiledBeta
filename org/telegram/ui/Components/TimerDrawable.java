package org.telegram.ui.Components;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;

public class TimerDrawable
  extends Drawable
{
  private static Drawable emptyTimerDrawable;
  private static TextPaint timePaint;
  private static Drawable timerDrawable;
  private int time = 0;
  private int timeHeight = 0;
  private StaticLayout timeLayout;
  private float timeWidth = 0.0F;
  
  public TimerDrawable(Context paramContext)
  {
    if (emptyTimerDrawable == null)
    {
      emptyTimerDrawable = paramContext.getResources().getDrawable(2130837695);
      timerDrawable = paramContext.getResources().getDrawable(2130837696);
      timePaint = new TextPaint(1);
      timePaint.setColor(-1);
      timePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    }
    timePaint.setTextSize(AndroidUtilities.dp(11.0F));
  }
  
  public void draw(Canvas paramCanvas)
  {
    int j = timerDrawable.getIntrinsicWidth();
    int k = timerDrawable.getIntrinsicHeight();
    if (this.time == 0) {}
    for (Drawable localDrawable = timerDrawable;; localDrawable = emptyTimerDrawable)
    {
      int i = (j - localDrawable.getIntrinsicWidth()) / 2;
      int m = (k - localDrawable.getIntrinsicHeight()) / 2;
      localDrawable.setBounds(i, m, localDrawable.getIntrinsicWidth() + i, localDrawable.getIntrinsicHeight() + m);
      localDrawable.draw(paramCanvas);
      if ((this.time != 0) && (this.timeLayout != null))
      {
        i = 0;
        if (AndroidUtilities.density == 3.0F) {
          i = -1;
        }
        paramCanvas.translate((int)(j / 2 - Math.ceil(this.timeWidth / 2.0F)) + i, (k - this.timeHeight) / 2);
        this.timeLayout.draw(paramCanvas);
      }
      return;
    }
  }
  
  public int getIntrinsicHeight()
  {
    return timerDrawable.getIntrinsicHeight();
  }
  
  public int getIntrinsicWidth()
  {
    return timerDrawable.getIntrinsicWidth();
  }
  
  public int getOpacity()
  {
    return 0;
  }
  
  public void setAlpha(int paramInt) {}
  
  public void setColorFilter(ColorFilter paramColorFilter) {}
  
  public void setTime(int paramInt)
  {
    this.time = paramInt;
    String str2;
    String str1;
    if ((this.time >= 1) && (this.time < 60))
    {
      str2 = "" + paramInt;
      str1 = str2;
      if (str2.length() < 2) {
        str1 = str2 + "s";
      }
    }
    for (;;)
    {
      this.timeWidth = timePaint.measureText(str1);
      try
      {
        this.timeLayout = new StaticLayout(str1, timePaint, (int)Math.ceil(this.timeWidth), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        this.timeHeight = this.timeLayout.getHeight();
        invalidateSelf();
        return;
        if ((this.time >= 60) && (this.time < 3600))
        {
          str2 = "" + paramInt / 60;
          str1 = str2;
          if (str2.length() >= 2) {
            continue;
          }
          str1 = str2 + "m";
          continue;
        }
        if ((this.time >= 3600) && (this.time < 86400))
        {
          str2 = "" + paramInt / 60 / 60;
          str1 = str2;
          if (str2.length() >= 2) {
            continue;
          }
          str1 = str2 + "h";
          continue;
        }
        if ((this.time >= 86400) && (this.time < 604800))
        {
          str2 = "" + paramInt / 60 / 60 / 24;
          str1 = str2;
          if (str2.length() >= 2) {
            continue;
          }
          str1 = str2 + "d";
          continue;
        }
        str2 = "" + paramInt / 60 / 60 / 24 / 7;
        if (str2.length() < 2)
        {
          str1 = str2 + "w";
          continue;
        }
        str1 = str2;
        if (str2.length() <= 2) {
          continue;
        }
        str1 = "c";
      }
      catch (Exception localException)
      {
        for (;;)
        {
          this.timeLayout = null;
          FileLog.e("tmessages", localException);
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/TimerDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */