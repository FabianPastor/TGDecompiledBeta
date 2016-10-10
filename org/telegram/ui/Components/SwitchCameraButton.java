package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import org.telegram.messenger.AndroidUtilities;

public class SwitchCameraButton
  extends View
{
  private boolean isFront;
  private Paint paint = new Paint(1);
  private float progress;
  private Drawable rotateDrawable = getResources().getDrawable(2130837586);
  private Drawable shadowDrawable = getResources().getDrawable(2130837828);
  
  public SwitchCameraButton(Context paramContext)
  {
    super(paramContext);
    this.paint.setStyle(Paint.Style.STROKE);
    this.paint.setColor(-1);
  }
  
  public float getProgress()
  {
    return this.progress;
  }
  
  public boolean isFront()
  {
    return this.isFront;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    int i = getMeasuredWidth() / 2;
    int j = getMeasuredHeight() / 2;
    this.shadowDrawable.setBounds(i - AndroidUtilities.dp(9.0F), j - AndroidUtilities.dp(9.0F), AndroidUtilities.dp(9.0F) + i, AndroidUtilities.dp(9.0F) + j);
    float f = AndroidUtilities.dp(5.0F) * this.progress + AndroidUtilities.dp(2.5F);
    this.paint.setStrokeWidth(f);
    paramCanvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, AndroidUtilities.dp(6.0F) - f / 2.0F, this.paint);
    if (this.progress != 0.0F)
    {
      if (!this.isFront) {
        break label189;
      }
      paramCanvas.rotate(this.progress * -180.0F, i, j);
    }
    for (;;)
    {
      this.rotateDrawable.setBounds(i - AndroidUtilities.dp(24.0F), j - AndroidUtilities.dp(20.0F), AndroidUtilities.dp(24.0F) + i, AndroidUtilities.dp(20.0F) + j);
      this.rotateDrawable.draw(paramCanvas);
      return;
      label189:
      paramCanvas.rotate(-180.0F - 180.0F * (1.0F - this.progress), i, j);
    }
  }
  
  public void setFront(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.isFront == paramBoolean1) {
      return;
    }
    this.isFront = paramBoolean1;
    if (paramBoolean2)
    {
      AnimatorSet localAnimatorSet = new AnimatorSet();
      localAnimatorSet.setDuration(320L);
      localAnimatorSet.setInterpolator(new DecelerateInterpolator(1.1F));
      if (this.isFront) {
        localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this, "progress", new float[] { 0.0F, 1.0F }) });
      }
      for (;;)
      {
        localAnimatorSet.addListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            SwitchCameraButton.this.setClickable(true);
          }
          
          public void onAnimationStart(Animator paramAnonymousAnimator)
          {
            SwitchCameraButton.this.setClickable(false);
          }
        });
        localAnimatorSet.start();
        return;
        localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this, "progress", new float[] { 1.0F, 0.0F }) });
      }
    }
    if (this.isFront) {}
    for (float f = 1.0F;; f = 0.0F)
    {
      this.progress = f;
      invalidate();
      return;
    }
  }
  
  public void setProgress(float paramFloat)
  {
    this.progress = paramFloat;
    invalidate();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/SwitchCameraButton.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */