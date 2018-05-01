package org.telegram.ui.Components;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import org.telegram.messenger.AndroidUtilities;

public class ShareLocationDrawable
  extends Drawable
{
  private Drawable drawable;
  private Drawable drawableLeft;
  private Drawable drawableRight;
  private boolean isSmall;
  private long lastUpdateTime = 0L;
  private float[] progress = { 0.0F, -0.5F };
  
  public ShareLocationDrawable(Context paramContext, boolean paramBoolean)
  {
    this.isSmall = paramBoolean;
    if (paramBoolean)
    {
      this.drawable = paramContext.getResources().getDrawable(NUM);
      this.drawableLeft = paramContext.getResources().getDrawable(NUM);
    }
    for (this.drawableRight = paramContext.getResources().getDrawable(NUM);; this.drawableRight = paramContext.getResources().getDrawable(NUM))
    {
      return;
      this.drawable = paramContext.getResources().getDrawable(NUM);
      this.drawableLeft = paramContext.getResources().getDrawable(NUM);
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
    for (int i = 0; i < 2; i++)
    {
      if (this.progress[i] >= 1.0F) {
        this.progress[i] = 0.0F;
      }
      float[] arrayOfFloat = this.progress;
      arrayOfFloat[i] += (float)l1 / 1300.0F;
      if (this.progress[i] > 1.0F) {
        this.progress[i] = 1.0F;
      }
    }
    invalidateSelf();
  }
  
  public void draw(Canvas paramCanvas)
  {
    if (this.isSmall) {}
    int i;
    int j;
    int k;
    for (float f1 = 30.0F;; f1 = 120.0F)
    {
      i = AndroidUtilities.dp(f1);
      j = getBounds().top + (getIntrinsicHeight() - i) / 2;
      k = getBounds().left + (getIntrinsicWidth() - i) / 2;
      this.drawable.setBounds(k, j, this.drawable.getIntrinsicWidth() + k, this.drawable.getIntrinsicHeight() + j);
      this.drawable.draw(paramCanvas);
      for (i = 0;; i++)
      {
        if (i >= 2) {
          break label465;
        }
        if (this.progress[i] >= 0.0F) {
          break;
        }
      }
    }
    float f2 = 0.5F + 0.5F * this.progress[i];
    label144:
    int m;
    label163:
    int n;
    label182:
    int i1;
    label217:
    label228:
    int i2;
    int i4;
    if (this.isSmall)
    {
      f1 = 2.5F;
      m = AndroidUtilities.dp(f1 * f2);
      if (!this.isSmall) {
        break label411;
      }
      f1 = 6.5F;
      n = AndroidUtilities.dp(f1 * f2);
      if (!this.isSmall) {
        break label417;
      }
      f1 = 6.0F;
      i1 = AndroidUtilities.dp(f1 * this.progress[i]);
      if (this.progress[i] >= 0.5F) {
        break label423;
      }
      f1 = this.progress[i] / 0.5F;
      if (!this.isSmall) {
        break label441;
      }
      f2 = 7.0F;
      i2 = AndroidUtilities.dp(f2) + k - i1;
      int i3 = this.drawable.getIntrinsicHeight() / 2;
      if (!this.isSmall) {
        break label448;
      }
      i4 = 0;
      label262:
      i4 = j + i3 - i4;
      this.drawableLeft.setAlpha((int)(255.0F * f1));
      this.drawableLeft.setBounds(i2 - m, i4 - n, i2 + m, i4 + n);
      this.drawableLeft.draw(paramCanvas);
      i2 = this.drawable.getIntrinsicWidth();
      if (!this.isSmall) {
        break label458;
      }
    }
    label411:
    label417:
    label423:
    label441:
    label448:
    label458:
    for (f2 = 7.0F;; f2 = 42.0F)
    {
      i1 = k + i2 - AndroidUtilities.dp(f2) + i1;
      this.drawableRight.setAlpha((int)(255.0F * f1));
      this.drawableRight.setBounds(i1 - m, i4 - n, i1 + m, i4 + n);
      this.drawableRight.draw(paramCanvas);
      break;
      f1 = 5.0F;
      break label144;
      f1 = 18.0F;
      break label163;
      f1 = 15.0F;
      break label182;
      f1 = 1.0F - (this.progress[i] - 0.5F) / 0.5F;
      break label217;
      f2 = 42.0F;
      break label228;
      i4 = AndroidUtilities.dp(7.0F);
      break label262;
    }
    label465:
    update();
  }
  
  public int getIntrinsicHeight()
  {
    if (this.isSmall) {}
    for (float f = 40.0F;; f = 180.0F) {
      return AndroidUtilities.dp(f);
    }
  }
  
  public int getIntrinsicWidth()
  {
    if (this.isSmall) {}
    for (float f = 40.0F;; f = 120.0F) {
      return AndroidUtilities.dp(f);
    }
  }
  
  public int getOpacity()
  {
    return 0;
  }
  
  public void setAlpha(int paramInt) {}
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    this.drawable.setColorFilter(paramColorFilter);
    this.drawableLeft.setColorFilter(paramColorFilter);
    this.drawableRight.setColorFilter(paramColorFilter);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/ShareLocationDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */