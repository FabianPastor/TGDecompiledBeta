package org.telegram.ui.Components;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;

public class CheckBox
  extends View
{
  private static Paint backgroundPaint;
  private static Paint checkPaint;
  private static Paint eraser;
  private static Paint eraser2;
  private static Paint paint;
  private static final float progressBounceDiff = 0.2F;
  private boolean attachedToWindow;
  private Canvas bitmapCanvas;
  private ObjectAnimator checkAnimator;
  private Bitmap checkBitmap;
  private Canvas checkCanvas;
  private Drawable checkDrawable;
  private int checkOffset;
  private int color = -10567099;
  private boolean drawBackground;
  private Bitmap drawBitmap;
  private boolean isCheckAnimation = true;
  private boolean isChecked;
  private float progress;
  private int size = 22;
  
  public CheckBox(Context paramContext, int paramInt)
  {
    super(paramContext);
    if (paint == null)
    {
      paint = new Paint(1);
      eraser = new Paint(1);
      eraser.setColor(0);
      eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
      eraser2 = new Paint(1);
      eraser2.setColor(0);
      eraser2.setStyle(Paint.Style.STROKE);
      eraser2.setStrokeWidth(AndroidUtilities.dp(28.0F));
      eraser2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
      backgroundPaint = new Paint(1);
      backgroundPaint.setColor(-1);
      backgroundPaint.setStyle(Paint.Style.STROKE);
      backgroundPaint.setStrokeWidth(AndroidUtilities.dp(2.0F));
    }
    this.checkDrawable = paramContext.getResources().getDrawable(paramInt);
  }
  
  private void animateToCheckedState(boolean paramBoolean)
  {
    this.isCheckAnimation = paramBoolean;
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
    if (getVisibility() != 0) {}
    while ((!this.drawBackground) && (this.progress == 0.0F)) {
      return;
    }
    eraser2.setStrokeWidth(AndroidUtilities.dp(this.size + 6));
    this.drawBitmap.eraseColor(0);
    float f5 = getMeasuredWidth() / 2;
    float f2;
    float f3;
    label84:
    float f4;
    label97:
    float f1;
    if (this.progress >= 0.5F)
    {
      f2 = 1.0F;
      if (this.progress >= 0.5F) {
        break label433;
      }
      f3 = 0.0F;
      if (!this.isCheckAnimation) {
        break label448;
      }
      f4 = this.progress;
      if (f4 >= 0.2F) {
        break label459;
      }
      f1 = f5 - AndroidUtilities.dp(2.0F) * f4 / 0.2F;
    }
    for (;;)
    {
      if (this.drawBackground)
      {
        paint.setColor(1140850688);
        paramCanvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, f1 - AndroidUtilities.dp(1.0F), paint);
        paramCanvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, f1 - AndroidUtilities.dp(1.0F), backgroundPaint);
      }
      paint.setColor(this.color);
      this.bitmapCanvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, f1, paint);
      this.bitmapCanvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, (1.0F - f2) * f1, eraser);
      paramCanvas.drawBitmap(this.drawBitmap, 0.0F, 0.0F, null);
      this.checkBitmap.eraseColor(0);
      int i = this.checkDrawable.getIntrinsicWidth();
      int j = this.checkDrawable.getIntrinsicHeight();
      int k = (getMeasuredWidth() - i) / 2;
      int m = (getMeasuredHeight() - j) / 2;
      this.checkDrawable.setBounds(k, this.checkOffset + m, k + i, m + j + this.checkOffset);
      this.checkDrawable.draw(this.checkCanvas);
      this.checkCanvas.drawCircle(getMeasuredWidth() / 2 - AndroidUtilities.dp(2.5F), getMeasuredHeight() / 2 + AndroidUtilities.dp(4.0F), (getMeasuredWidth() + AndroidUtilities.dp(6.0F)) / 2 * (1.0F - f3), eraser2);
      paramCanvas.drawBitmap(this.checkBitmap, 0.0F, 0.0F, null);
      return;
      f2 = this.progress / 0.5F;
      break;
      label433:
      f3 = (this.progress - 0.5F) / 0.5F;
      break label84;
      label448:
      f4 = 1.0F - this.progress;
      break label97;
      label459:
      f1 = f5;
      if (f4 < 0.4F) {
        f1 = f5 - (AndroidUtilities.dp(2.0F) - AndroidUtilities.dp(2.0F) * (f4 - 0.2F) / 0.2F);
      }
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void setCheckOffset(int paramInt)
  {
    this.checkOffset = paramInt;
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
  
  public void setDrawBackground(boolean paramBoolean)
  {
    this.drawBackground = paramBoolean;
  }
  
  public void setProgress(float paramFloat)
  {
    if (this.progress == paramFloat) {
      return;
    }
    this.progress = paramFloat;
    invalidate();
  }
  
  public void setSize(int paramInt)
  {
    this.size = paramInt;
  }
  
  public void setVisibility(int paramInt)
  {
    super.setVisibility(paramInt);
    if ((paramInt == 0) && (this.drawBitmap == null))
    {
      this.drawBitmap = Bitmap.createBitmap(AndroidUtilities.dp(this.size), AndroidUtilities.dp(this.size), Bitmap.Config.ARGB_4444);
      this.bitmapCanvas = new Canvas(this.drawBitmap);
      this.checkBitmap = Bitmap.createBitmap(AndroidUtilities.dp(this.size), AndroidUtilities.dp(this.size), Bitmap.Config.ARGB_4444);
      this.checkCanvas = new Canvas(this.checkBitmap);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/CheckBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */