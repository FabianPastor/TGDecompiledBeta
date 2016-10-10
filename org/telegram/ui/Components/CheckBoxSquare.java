package org.telegram.ui.Components;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;

public class CheckBoxSquare
  extends View
{
  private static Paint backgroundPaint;
  private static Paint checkPaint;
  private static Paint eraser;
  private static final float progressBounceDiff = 0.2F;
  private static RectF rectF;
  private boolean attachedToWindow;
  private ObjectAnimator checkAnimator;
  private int color = -12345121;
  private Bitmap drawBitmap;
  private Canvas drawCanvas;
  private boolean isChecked;
  private boolean isDisabled;
  private float progress;
  
  public CheckBoxSquare(Context paramContext)
  {
    super(paramContext);
    if (checkPaint == null)
    {
      checkPaint = new Paint(1);
      checkPaint.setColor(-1);
      checkPaint.setStyle(Paint.Style.STROKE);
      checkPaint.setStrokeWidth(AndroidUtilities.dp(2.0F));
      eraser = new Paint(1);
      eraser.setColor(0);
      eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
      backgroundPaint = new Paint(1);
      rectF = new RectF();
    }
    this.drawBitmap = Bitmap.createBitmap(AndroidUtilities.dp(18.0F), AndroidUtilities.dp(18.0F), Bitmap.Config.ARGB_4444);
    this.drawCanvas = new Canvas(this.drawBitmap);
  }
  
  private void animateToCheckedState(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (float f = 1.0F;; f = 0.0F)
    {
      this.checkAnimator = ObjectAnimator.ofFloat(this, "progress", new float[] { f });
      this.checkAnimator.setDuration(300L);
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
    if (getVisibility() != 0) {
      return;
    }
    float f2;
    float f1;
    int i;
    if (this.progress <= 0.5F)
    {
      f2 = this.progress / 0.5F;
      f1 = f2;
      i = Color.rgb((int)((Color.red(this.color) - 115) * f2) + 115, (int)((Color.green(this.color) - 115) * f2) + 115, (int)((Color.blue(this.color) - 115) * f2) + 115);
      backgroundPaint.setColor(i);
    }
    for (;;)
    {
      if (this.isDisabled) {
        backgroundPaint.setColor(-5197648);
      }
      float f3 = AndroidUtilities.dp(1.0F) * f1;
      rectF.set(f3, f3, AndroidUtilities.dp(18.0F) - f3, AndroidUtilities.dp(18.0F) - f3);
      this.drawBitmap.eraseColor(0);
      this.drawCanvas.drawRoundRect(rectF, AndroidUtilities.dp(2.0F), AndroidUtilities.dp(2.0F), backgroundPaint);
      if (f2 != 1.0F)
      {
        f2 = Math.min(AndroidUtilities.dp(7.0F), AndroidUtilities.dp(7.0F) * f2 + f3);
        rectF.set(AndroidUtilities.dp(2.0F) + f2, AndroidUtilities.dp(2.0F) + f2, AndroidUtilities.dp(16.0F) - f2, AndroidUtilities.dp(16.0F) - f2);
        this.drawCanvas.drawRect(rectF, eraser);
      }
      if (this.progress > 0.5F)
      {
        i = (int)(AndroidUtilities.dp(7.5F) - AndroidUtilities.dp(5.0F) * (1.0F - f1));
        int j = (int)(AndroidUtilities.dpf2(13.5F) - AndroidUtilities.dp(5.0F) * (1.0F - f1));
        this.drawCanvas.drawLine(AndroidUtilities.dp(7.5F), (int)AndroidUtilities.dpf2(13.5F), i, j, checkPaint);
        i = (int)(AndroidUtilities.dpf2(6.5F) + AndroidUtilities.dp(9.0F) * (1.0F - f1));
        j = (int)(AndroidUtilities.dpf2(13.5F) - AndroidUtilities.dp(9.0F) * (1.0F - f1));
        this.drawCanvas.drawLine((int)AndroidUtilities.dpf2(6.5F), (int)AndroidUtilities.dpf2(13.5F), i, j, checkPaint);
      }
      paramCanvas.drawBitmap(this.drawBitmap, 0.0F, 0.0F, null);
      return;
      f1 = 2.0F - this.progress / 0.5F;
      f2 = 1.0F;
      backgroundPaint.setColor(this.color);
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void setChecked(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1 == this.isChecked) {
      return;
    }
    this.isChecked = paramBoolean1;
    if ((this.attachedToWindow) && (paramBoolean2))
    {
      animateToCheckedState(paramBoolean1);
      return;
    }
    cancelCheckAnimator();
    if (paramBoolean1) {}
    for (float f = 1.0F;; f = 0.0F)
    {
      setProgress(f);
      return;
    }
  }
  
  public void setColor(int paramInt)
  {
    this.color = paramInt;
  }
  
  public void setDisabled(boolean paramBoolean)
  {
    this.isDisabled = paramBoolean;
    invalidate();
  }
  
  public void setProgress(float paramFloat)
  {
    if (this.progress == paramFloat) {
      return;
    }
    this.progress = paramFloat;
    invalidate();
  }
  
  public void setVisibility(int paramInt)
  {
    super.setVisibility(paramInt);
    if ((paramInt == 0) && (this.drawBitmap == null)) {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/CheckBoxSquare.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */