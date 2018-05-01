package org.telegram.ui.Components;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.support.annotation.Keep;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;

public class Switch2
  extends View
{
  private static Bitmap drawBitmap;
  private boolean attachedToWindow;
  private ObjectAnimator checkAnimator;
  private boolean isChecked;
  private boolean isDisabled;
  private Paint paint;
  private Paint paint2;
  private float progress;
  private RectF rectF = new RectF();
  
  public Switch2(Context paramContext)
  {
    super(paramContext);
    Canvas localCanvas;
    if ((drawBitmap == null) || (drawBitmap.getWidth() != AndroidUtilities.dp(24.0F)))
    {
      drawBitmap = Bitmap.createBitmap(AndroidUtilities.dp(24.0F), AndroidUtilities.dp(24.0F), Bitmap.Config.ARGB_8888);
      localCanvas = new Canvas(drawBitmap);
      paramContext = new Paint(1);
      paramContext.setShadowLayer(AndroidUtilities.dp(2.0F), 0.0F, 0.0F, NUM);
      localCanvas.drawCircle(AndroidUtilities.dp(12.0F), AndroidUtilities.dp(12.0F), AndroidUtilities.dp(9.0F), paramContext);
    }
    try
    {
      localCanvas.setBitmap(null);
      this.paint = new Paint(1);
      this.paint2 = new Paint(1);
      this.paint2.setStyle(Paint.Style.STROKE);
      this.paint2.setStrokeCap(Paint.Cap.ROUND);
      this.paint2.setStrokeWidth(AndroidUtilities.dp(2.0F));
      return;
    }
    catch (Exception paramContext)
    {
      for (;;) {}
    }
  }
  
  private void animateToCheckedState(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (float f = 1.0F;; f = 0.0F)
    {
      this.checkAnimator = ObjectAnimator.ofFloat(this, "progress", new float[] { f });
      this.checkAnimator.setDuration(250L);
      this.checkAnimator.start();
      return;
    }
  }
  
  private void cancelCheckAnimator()
  {
    if (this.checkAnimator != null) {
      this.checkAnimator.cancel();
    }
  }
  
  public float getProgress()
  {
    return this.progress;
  }
  
  public boolean isChecked()
  {
    return this.isChecked;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    this.attachedToWindow = true;
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    this.attachedToWindow = false;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (getVisibility() != 0) {}
    for (;;)
    {
      return;
      int i = AndroidUtilities.dp(36.0F);
      AndroidUtilities.dp(20.0F);
      int j = (getMeasuredWidth() - i) / 2;
      int k = (getMeasuredHeight() - AndroidUtilities.dp(14.0F)) / 2;
      int m = (int)((i - AndroidUtilities.dp(14.0F)) * this.progress) + j + AndroidUtilities.dp(7.0F);
      int n = getMeasuredHeight() / 2;
      int i1 = (int)(255.0F + -95.0F * this.progress);
      int i2 = (int)(176.0F + 38.0F * this.progress);
      int i3 = (int)(173.0F + 77.0F * this.progress);
      this.paint.setColor(0xFF000000 | (i1 & 0xFF) << 16 | (i2 & 0xFF) << 8 | i3 & 0xFF);
      this.rectF.set(j, k, j + i, AndroidUtilities.dp(14.0F) + k);
      paramCanvas.drawRoundRect(this.rectF, AndroidUtilities.dp(7.0F), AndroidUtilities.dp(7.0F), this.paint);
      i2 = (int)(219.0F + -151.0F * this.progress);
      k = (int)(88.0F + 80.0F * this.progress);
      i1 = (int)(92.0F + 142.0F * this.progress);
      this.paint.setColor(0xFF000000 | (i2 & 0xFF) << 16 | (k & 0xFF) << 8 | i1 & 0xFF);
      paramCanvas.drawBitmap(drawBitmap, m - AndroidUtilities.dp(12.0F), n - AndroidUtilities.dp(11.0F), null);
      paramCanvas.drawCircle(m, n, AndroidUtilities.dp(10.0F), this.paint);
      this.paint2.setColor(-1);
      m = (int)(m - (AndroidUtilities.dp(10.8F) - AndroidUtilities.dp(1.3F) * this.progress));
      n = (int)(n - (AndroidUtilities.dp(8.5F) - AndroidUtilities.dp(0.5F) * this.progress));
      int i4 = (int)AndroidUtilities.dpf2(4.6F) + m;
      i2 = (int)(AndroidUtilities.dpf2(9.5F) + n);
      i3 = AndroidUtilities.dp(2.0F);
      j = AndroidUtilities.dp(2.0F);
      k = (int)AndroidUtilities.dpf2(7.5F) + m;
      int i5 = (int)AndroidUtilities.dpf2(5.4F) + n;
      i = k + AndroidUtilities.dp(7.0F);
      i1 = i5 + AndroidUtilities.dp(7.0F);
      k = (int)(k + (i4 - k) * this.progress);
      i5 = (int)(i5 + (i2 - i5) * this.progress);
      i = (int)(i + (i4 + i3 - i) * this.progress);
      i2 = (int)(i1 + (i2 + j - i1) * this.progress);
      paramCanvas.drawLine(k, i5, i, i2, this.paint2);
      m = (int)AndroidUtilities.dpf2(7.5F) + m;
      i1 = (int)AndroidUtilities.dpf2(12.5F) + n;
      n = AndroidUtilities.dp(7.0F);
      i2 = AndroidUtilities.dp(7.0F);
      paramCanvas.drawLine(m, i1, m + n, i1 - i2, this.paint2);
    }
  }
  
  public void setChecked(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1 == this.isChecked) {}
    for (;;)
    {
      return;
      this.isChecked = paramBoolean1;
      if ((!this.attachedToWindow) || (!paramBoolean2)) {
        break;
      }
      animateToCheckedState(paramBoolean1);
    }
    cancelCheckAnimator();
    if (paramBoolean1) {}
    for (float f = 1.0F;; f = 0.0F)
    {
      setProgress(f);
      break;
    }
  }
  
  public void setDisabled(boolean paramBoolean)
  {
    this.isDisabled = paramBoolean;
    invalidate();
  }
  
  @Keep
  public void setProgress(float paramFloat)
  {
    if (this.progress == paramFloat) {}
    for (;;)
    {
      return;
      this.progress = paramFloat;
      invalidate();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/Switch2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */