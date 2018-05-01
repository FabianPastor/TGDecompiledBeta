package org.telegram.ui.Components;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Keep;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class GroupCreateCheckBox
  extends View
{
  private static Paint eraser;
  private static Paint eraser2;
  private static final float progressBounceDiff = 0.2F;
  private boolean attachedToWindow;
  private Paint backgroundInnerPaint;
  private String backgroundKey = "groupcreate_checkboxCheck";
  private Paint backgroundPaint;
  private Canvas bitmapCanvas;
  private ObjectAnimator checkAnimator;
  private String checkKey = "groupcreate_checkboxCheck";
  private Paint checkPaint;
  private float checkScale = 1.0F;
  private Bitmap drawBitmap;
  private String innerKey = "groupcreate_checkbox";
  private int innerRadDiff;
  private boolean isCheckAnimation = true;
  private boolean isChecked;
  private float progress;
  
  public GroupCreateCheckBox(Context paramContext)
  {
    super(paramContext);
    if (eraser == null)
    {
      eraser = new Paint(1);
      eraser.setColor(0);
      eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
      eraser2 = new Paint(1);
      eraser2.setColor(0);
      eraser2.setStyle(Paint.Style.STROKE);
      eraser2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }
    this.backgroundPaint = new Paint(1);
    this.backgroundInnerPaint = new Paint(1);
    this.checkPaint = new Paint(1);
    this.checkPaint.setStyle(Paint.Style.STROKE);
    this.innerRadDiff = AndroidUtilities.dp(2.0F);
    this.checkPaint.setStrokeWidth(AndroidUtilities.dp(1.5F));
    eraser2.setStrokeWidth(AndroidUtilities.dp(28.0F));
    this.drawBitmap = Bitmap.createBitmap(AndroidUtilities.dp(24.0F), AndroidUtilities.dp(24.0F), Bitmap.Config.ARGB_4444);
    this.bitmapCanvas = new Canvas(this.drawBitmap);
    updateColors();
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
    updateColors();
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
    int i;
    int j;
    float f1;
    label64:
    float f2;
    label77:
    float f3;
    if (this.progress != 0.0F)
    {
      i = getMeasuredWidth() / 2;
      j = getMeasuredHeight() / 2;
      eraser2.setStrokeWidth(AndroidUtilities.dp(30.0F));
      this.drawBitmap.eraseColor(0);
      if (this.progress < 0.5F) {
        break label340;
      }
      f1 = 1.0F;
      if (this.progress >= 0.5F) {
        break label352;
      }
      f2 = 0.0F;
      if (!this.isCheckAnimation) {
        break label367;
      }
      f3 = this.progress;
      label90:
      if (f3 >= 0.2F) {
        break label378;
      }
      f3 = AndroidUtilities.dp(2.0F) * f3 / 0.2F;
    }
    for (;;)
    {
      if (f2 != 0.0F) {
        paramCanvas.drawCircle(i, j, i - AndroidUtilities.dp(2.0F) + AndroidUtilities.dp(2.0F) * f2 - f3, this.backgroundPaint);
      }
      f3 = i - this.innerRadDiff - f3;
      this.bitmapCanvas.drawCircle(i, j, f3, this.backgroundInnerPaint);
      this.bitmapCanvas.drawCircle(i, j, (1.0F - f1) * f3, eraser);
      paramCanvas.drawBitmap(this.drawBitmap, 0.0F, 0.0F, null);
      f3 = AndroidUtilities.dp(10.0F) * f2 * this.checkScale;
      f1 = AndroidUtilities.dp(5.0F) * f2 * this.checkScale;
      i -= AndroidUtilities.dp(1.0F);
      j += AndroidUtilities.dp(4.0F);
      f1 = (float)Math.sqrt(f1 * f1 / 2.0F);
      paramCanvas.drawLine(i, j, i - f1, j - f1, this.checkPaint);
      f3 = (float)Math.sqrt(f3 * f3 / 2.0F);
      i -= AndroidUtilities.dp(1.2F);
      paramCanvas.drawLine(i, j, i + f3, j - f3, this.checkPaint);
      break;
      break;
      label340:
      f1 = this.progress / 0.5F;
      break label64;
      label352:
      f2 = (this.progress - 0.5F) / 0.5F;
      break label77;
      label367:
      f3 = 1.0F - this.progress;
      break label90;
      label378:
      if (f3 < 0.4F) {
        f3 = AndroidUtilities.dp(2.0F) - AndroidUtilities.dp(2.0F) * (f3 - 0.2F) / 0.2F;
      } else {
        f3 = 0.0F;
      }
    }
  }
  
  public void setCheckScale(float paramFloat)
  {
    this.checkScale = paramFloat;
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
  
  public void setColorKeysOverrides(String paramString1, String paramString2, String paramString3)
  {
    this.checkKey = paramString1;
    this.innerKey = paramString2;
    this.backgroundKey = paramString3;
    updateColors();
  }
  
  public void setInnerRadDiff(int paramInt)
  {
    this.innerRadDiff = paramInt;
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
  
  public void updateColors()
  {
    this.backgroundInnerPaint.setColor(Theme.getColor(this.innerKey));
    this.backgroundPaint.setColor(Theme.getColor(this.backgroundKey));
    this.checkPaint.setColor(Theme.getColor(this.checkKey));
    invalidate();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/GroupCreateCheckBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */