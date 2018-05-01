package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.support.annotation.Keep;
import android.text.TextPaint;
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
  private String checkedText;
  private int color;
  private boolean drawBackground;
  private Bitmap drawBitmap;
  private boolean hasBorder;
  private boolean isCheckAnimation = true;
  private boolean isChecked;
  private float progress;
  private int size = 22;
  private TextPaint textPaint;
  
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
      eraser2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
      backgroundPaint = new Paint(1);
      backgroundPaint.setColor(-1);
      backgroundPaint.setStyle(Paint.Style.STROKE);
    }
    eraser2.setStrokeWidth(AndroidUtilities.dp(28.0F));
    backgroundPaint.setStrokeWidth(AndroidUtilities.dp(2.0F));
    this.textPaint = new TextPaint(1);
    this.textPaint.setTextSize(AndroidUtilities.dp(18.0F));
    this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.checkDrawable = paramContext.getResources().getDrawable(paramInt).mutate();
  }
  
  private void animateToCheckedState(boolean paramBoolean)
  {
    this.isCheckAnimation = paramBoolean;
    if (paramBoolean) {}
    for (float f = 1.0F;; f = 0.0F)
    {
      this.checkAnimator = ObjectAnimator.ofFloat(this, "progress", new float[] { f });
      this.checkAnimator.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if (paramAnonymousAnimator.equals(CheckBox.this.checkAnimator)) {
            CheckBox.access$002(CheckBox.this, null);
          }
          if (!CheckBox.this.isChecked) {
            CheckBox.access$202(CheckBox.this, null);
          }
        }
      });
      this.checkAnimator.setDuration(300L);
      this.checkAnimator.start();
      return;
    }
  }
  
  private void cancelCheckAnimator()
  {
    if (this.checkAnimator != null)
    {
      this.checkAnimator.cancel();
      this.checkAnimator = null;
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
    if ((getVisibility() != 0) || (this.drawBitmap == null) || (this.checkBitmap == null)) {
      return;
    }
    float f1;
    float f2;
    label84:
    float f3;
    label97:
    float f4;
    label110:
    float f5;
    label133:
    int i;
    if ((this.drawBackground) || (this.progress != 0.0F))
    {
      eraser2.setStrokeWidth(AndroidUtilities.dp(this.size + 6));
      this.drawBitmap.eraseColor(0);
      f1 = getMeasuredWidth() / 2;
      if (this.progress < 0.5F) {
        break label461;
      }
      f2 = 1.0F;
      if (this.progress >= 0.5F) {
        break label472;
      }
      f3 = 0.0F;
      if (!this.isCheckAnimation) {
        break label487;
      }
      f4 = this.progress;
      if (f4 >= 0.2F) {
        break label498;
      }
      f5 = f1 - AndroidUtilities.dp(2.0F) * f4 / 0.2F;
      if (this.drawBackground)
      {
        paint.setColor(NUM);
        paramCanvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, f5 - AndroidUtilities.dp(1.0F), paint);
        paramCanvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, f5 - AndroidUtilities.dp(1.0F), backgroundPaint);
      }
      paint.setColor(this.color);
      f4 = f5;
      if (this.hasBorder) {
        f4 = f5 - AndroidUtilities.dp(2.0F);
      }
      this.bitmapCanvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, f4, paint);
      this.bitmapCanvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, (1.0F - f2) * f4, eraser);
      paramCanvas.drawBitmap(this.drawBitmap, 0.0F, 0.0F, null);
      this.checkBitmap.eraseColor(0);
      if (this.checkedText == null) {
        break label545;
      }
      i = (int)Math.ceil(this.textPaint.measureText(this.checkedText));
      Canvas localCanvas = this.checkCanvas;
      String str = this.checkedText;
      f2 = (getMeasuredWidth() - i) / 2;
      if (this.size != 40) {
        break label537;
      }
      f5 = 28.0F;
      label373:
      localCanvas.drawText(str, f2, AndroidUtilities.dp(f5), this.textPaint);
    }
    for (;;)
    {
      this.checkCanvas.drawCircle(getMeasuredWidth() / 2 - AndroidUtilities.dp(2.5F), getMeasuredHeight() / 2 + AndroidUtilities.dp(4.0F), (getMeasuredWidth() + AndroidUtilities.dp(6.0F)) / 2 * (1.0F - f3), eraser2);
      paramCanvas.drawBitmap(this.checkBitmap, 0.0F, 0.0F, null);
      break;
      break;
      label461:
      f2 = this.progress / 0.5F;
      break label84;
      label472:
      f3 = (this.progress - 0.5F) / 0.5F;
      break label97;
      label487:
      f4 = 1.0F - this.progress;
      break label110;
      label498:
      f5 = f1;
      if (f4 >= 0.4F) {
        break label133;
      }
      f5 = f1 - (AndroidUtilities.dp(2.0F) - AndroidUtilities.dp(2.0F) * (f4 - 0.2F) / 0.2F);
      break label133;
      label537:
      f5 = 21.0F;
      break label373;
      label545:
      int j = this.checkDrawable.getIntrinsicWidth();
      int k = this.checkDrawable.getIntrinsicHeight();
      i = (getMeasuredWidth() - j) / 2;
      int m = (getMeasuredHeight() - k) / 2;
      this.checkDrawable.setBounds(i, this.checkOffset + m, i + j, m + k + this.checkOffset);
      this.checkDrawable.draw(this.checkCanvas);
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void setBackgroundColor(int paramInt)
  {
    this.color = paramInt;
    invalidate();
  }
  
  public void setCheckColor(int paramInt)
  {
    this.checkDrawable.setColorFilter(new PorterDuffColorFilter(paramInt, PorterDuff.Mode.MULTIPLY));
    this.textPaint.setColor(paramInt);
    invalidate();
  }
  
  public void setCheckOffset(int paramInt)
  {
    this.checkOffset = paramInt;
  }
  
  public void setChecked(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramInt >= 0)
    {
      this.checkedText = ("" + (paramInt + 1));
      invalidate();
    }
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
  
  public void setChecked(boolean paramBoolean1, boolean paramBoolean2)
  {
    setChecked(-1, paramBoolean1, paramBoolean2);
  }
  
  public void setColor(int paramInt1, int paramInt2)
  {
    this.color = paramInt1;
    this.checkDrawable.setColorFilter(new PorterDuffColorFilter(paramInt2, PorterDuff.Mode.MULTIPLY));
    this.textPaint.setColor(paramInt2);
    invalidate();
  }
  
  public void setDrawBackground(boolean paramBoolean)
  {
    this.drawBackground = paramBoolean;
  }
  
  public void setHasBorder(boolean paramBoolean)
  {
    this.hasBorder = paramBoolean;
  }
  
  public void setNum(int paramInt)
  {
    if (paramInt >= 0) {
      this.checkedText = ("" + (paramInt + 1));
    }
    for (;;)
    {
      invalidate();
      return;
      if (this.checkAnimator == null) {
        this.checkedText = null;
      }
    }
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
  
  public void setSize(int paramInt)
  {
    this.size = paramInt;
    if (paramInt == 40) {
      this.textPaint.setTextSize(AndroidUtilities.dp(24.0F));
    }
  }
  
  public void setVisibility(int paramInt)
  {
    super.setVisibility(paramInt);
    if ((paramInt == 0) && (this.drawBitmap == null)) {}
    try
    {
      this.drawBitmap = Bitmap.createBitmap(AndroidUtilities.dp(this.size), AndroidUtilities.dp(this.size), Bitmap.Config.ARGB_4444);
      Canvas localCanvas = new android/graphics/Canvas;
      localCanvas.<init>(this.drawBitmap);
      this.bitmapCanvas = localCanvas;
      this.checkBitmap = Bitmap.createBitmap(AndroidUtilities.dp(this.size), AndroidUtilities.dp(this.size), Bitmap.Config.ARGB_4444);
      localCanvas = new android/graphics/Canvas;
      localCanvas.<init>(this.checkBitmap);
      this.checkCanvas = localCanvas;
      return;
    }
    catch (Throwable localThrowable)
    {
      for (;;) {}
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/CheckBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */