package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import org.telegram.messenger.AndroidUtilities;

public class ShutterButton
  extends View
{
  private static final int LONG_PRESS_TIME = 800;
  private ShutterButtonDelegate delegate;
  private DecelerateInterpolator interpolator = new DecelerateInterpolator();
  private long lastUpdateTime;
  private Runnable longPressed = new Runnable()
  {
    public void run()
    {
      if ((ShutterButton.this.delegate != null) && (!ShutterButton.this.delegate.shutterLongPressed())) {
        ShutterButton.access$102(ShutterButton.this, false);
      }
    }
  };
  private boolean pressed;
  private boolean processRelease;
  private Paint redPaint;
  private float redProgress;
  private Drawable shadowDrawable = getResources().getDrawable(NUM);
  private State state;
  private long totalTime;
  private Paint whitePaint = new Paint(1);
  
  public ShutterButton(Context paramContext)
  {
    super(paramContext);
    this.whitePaint.setStyle(Paint.Style.FILL);
    this.whitePaint.setColor(-1);
    this.redPaint = new Paint(1);
    this.redPaint.setStyle(Paint.Style.FILL);
    this.redPaint.setColor(-3324089);
    this.state = State.DEFAULT;
  }
  
  private void setHighlighted(boolean paramBoolean)
  {
    AnimatorSet localAnimatorSet = new AnimatorSet();
    if (paramBoolean) {
      localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this, "scaleX", new float[] { 1.06F }), ObjectAnimator.ofFloat(this, "scaleY", new float[] { 1.06F }) });
    }
    for (;;)
    {
      localAnimatorSet.setDuration(120L);
      localAnimatorSet.setInterpolator(this.interpolator);
      localAnimatorSet.start();
      return;
      localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this, "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(this, "scaleY", new float[] { 1.0F }) });
      localAnimatorSet.setStartDelay(40L);
    }
  }
  
  public ShutterButtonDelegate getDelegate()
  {
    return this.delegate;
  }
  
  public State getState()
  {
    return this.state;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    int i = getMeasuredWidth() / 2;
    int j = getMeasuredHeight() / 2;
    this.shadowDrawable.setBounds(i - AndroidUtilities.dp(36.0F), j - AndroidUtilities.dp(36.0F), AndroidUtilities.dp(36.0F) + i, AndroidUtilities.dp(36.0F) + j);
    this.shadowDrawable.draw(paramCanvas);
    float f;
    if ((this.pressed) || (getScaleX() != 1.0F))
    {
      f = (getScaleX() - 1.0F) / 0.06F;
      this.whitePaint.setAlpha((int)(255.0F * f));
      paramCanvas.drawCircle(i, j, AndroidUtilities.dp(26.0F), this.whitePaint);
      if (this.state == State.RECORDING)
      {
        if (this.redProgress != 1.0F)
        {
          long l1 = Math.abs(System.currentTimeMillis() - this.lastUpdateTime);
          long l2 = l1;
          if (l1 > 17L) {
            l2 = 17L;
          }
          this.totalTime += l2;
          if (this.totalTime > 120L) {
            this.totalTime = 120L;
          }
          this.redProgress = this.interpolator.getInterpolation((float)this.totalTime / 120.0F);
          invalidate();
        }
        paramCanvas.drawCircle(i, j, AndroidUtilities.dp(26.0F) * f * this.redProgress, this.redPaint);
      }
    }
    for (;;)
    {
      return;
      if (this.redProgress != 0.0F)
      {
        paramCanvas.drawCircle(i, j, AndroidUtilities.dp(26.0F) * f, this.redPaint);
        continue;
        if (this.redProgress != 0.0F) {
          this.redProgress = 0.0F;
        }
      }
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    setMeasuredDimension(AndroidUtilities.dp(84.0F), AndroidUtilities.dp(84.0F));
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    float f1 = paramMotionEvent.getX();
    float f2 = paramMotionEvent.getX();
    switch (paramMotionEvent.getAction())
    {
    }
    for (;;)
    {
      return true;
      AndroidUtilities.runOnUIThread(this.longPressed, 800L);
      this.pressed = true;
      this.processRelease = true;
      setHighlighted(true);
      continue;
      setHighlighted(false);
      AndroidUtilities.cancelRunOnUIThread(this.longPressed);
      if ((this.processRelease) && (f1 >= 0.0F) && (f2 >= 0.0F) && (f1 <= getMeasuredWidth()) && (f2 <= getMeasuredHeight()))
      {
        this.delegate.shutterReleased();
        continue;
        if ((f1 < 0.0F) || (f2 < 0.0F) || (f1 > getMeasuredWidth()) || (f2 > getMeasuredHeight()))
        {
          AndroidUtilities.cancelRunOnUIThread(this.longPressed);
          if (this.state == State.RECORDING)
          {
            setHighlighted(false);
            this.delegate.shutterCancel();
            setState(State.DEFAULT, true);
            continue;
            setHighlighted(false);
            this.pressed = false;
          }
        }
      }
    }
  }
  
  public void setDelegate(ShutterButtonDelegate paramShutterButtonDelegate)
  {
    this.delegate = paramShutterButtonDelegate;
  }
  
  public void setScaleX(float paramFloat)
  {
    super.setScaleX(paramFloat);
    invalidate();
  }
  
  public void setState(State paramState, boolean paramBoolean)
  {
    if (this.state != paramState)
    {
      this.state = paramState;
      if (!paramBoolean) {
        break label49;
      }
      this.lastUpdateTime = System.currentTimeMillis();
      this.totalTime = 0L;
      if (this.state != State.RECORDING) {
        this.redProgress = 0.0F;
      }
    }
    for (;;)
    {
      invalidate();
      return;
      label49:
      if (this.state == State.RECORDING) {
        this.redProgress = 1.0F;
      } else {
        this.redProgress = 0.0F;
      }
    }
  }
  
  public static abstract interface ShutterButtonDelegate
  {
    public abstract void shutterCancel();
    
    public abstract boolean shutterLongPressed();
    
    public abstract void shutterReleased();
  }
  
  public static enum State
  {
    DEFAULT,  RECORDING;
    
    private State() {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/ShutterButton.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */