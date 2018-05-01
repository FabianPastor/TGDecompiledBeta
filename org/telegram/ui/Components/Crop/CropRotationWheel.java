package org.telegram.ui.Components.Crop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class CropRotationWheel
  extends FrameLayout
{
  private static final int DELTA_ANGLE = 5;
  private static final int MAX_ANGLE = 45;
  private ImageView aspectRatioButton;
  private Paint bluePaint;
  private TextView degreesLabel;
  private float prevX;
  protected float rotation;
  private ImageView rotation90Button;
  private RotationWheelListener rotationListener;
  private RectF tempRect = new RectF(0.0F, 0.0F, 0.0F, 0.0F);
  private Paint whitePaint = new Paint();
  
  public CropRotationWheel(Context paramContext)
  {
    super(paramContext);
    this.whitePaint.setStyle(Paint.Style.FILL);
    this.whitePaint.setColor(-1);
    this.whitePaint.setAlpha(255);
    this.whitePaint.setAntiAlias(true);
    this.bluePaint = new Paint();
    this.bluePaint.setStyle(Paint.Style.FILL);
    this.bluePaint.setColor(-11420173);
    this.bluePaint.setAlpha(255);
    this.bluePaint.setAntiAlias(true);
    this.aspectRatioButton = new ImageView(paramContext);
    this.aspectRatioButton.setImageResource(NUM);
    this.aspectRatioButton.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
    this.aspectRatioButton.setScaleType(ImageView.ScaleType.CENTER);
    this.aspectRatioButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (CropRotationWheel.this.rotationListener != null) {
          CropRotationWheel.this.rotationListener.aspectRatioPressed();
        }
      }
    });
    addView(this.aspectRatioButton, LayoutHelper.createFrame(70, 64, 19));
    this.rotation90Button = new ImageView(paramContext);
    this.rotation90Button.setImageResource(NUM);
    this.rotation90Button.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
    this.rotation90Button.setScaleType(ImageView.ScaleType.CENTER);
    this.rotation90Button.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (CropRotationWheel.this.rotationListener != null) {
          CropRotationWheel.this.rotationListener.rotate90Pressed();
        }
      }
    });
    addView(this.rotation90Button, LayoutHelper.createFrame(70, 64, 21));
    this.degreesLabel = new TextView(paramContext);
    this.degreesLabel.setTextColor(-1);
    addView(this.degreesLabel, LayoutHelper.createFrame(-2, -2, 49));
    setWillNotDraw(false);
    setRotation(0.0F, false);
  }
  
  protected void drawLine(Canvas paramCanvas, int paramInt1, float paramFloat, int paramInt2, int paramInt3, boolean paramBoolean, Paint paramPaint)
  {
    int i = (int)(paramInt2 / 2.0F - AndroidUtilities.dp(70.0F));
    float f = paramInt1 * 5;
    paramInt1 = (int)(i * Math.cos(Math.toRadians(90.0F - (f + paramFloat))));
    int j = paramInt2 / 2 + paramInt1;
    paramFloat = Math.abs(paramInt1) / i;
    paramInt1 = Math.min(255, Math.max(0, (int)((1.0F - paramFloat * paramFloat) * 255.0F)));
    if (paramBoolean) {
      paramPaint = this.bluePaint;
    }
    paramPaint.setAlpha(paramInt1);
    if (paramBoolean)
    {
      paramInt1 = 4;
      if (!paramBoolean) {
        break label157;
      }
    }
    label157:
    for (paramInt2 = AndroidUtilities.dp(16.0F);; paramInt2 = AndroidUtilities.dp(12.0F))
    {
      paramCanvas.drawRect(j - paramInt1 / 2, (paramInt3 - paramInt2) / 2, paramInt1 / 2 + j, (paramInt3 + paramInt2) / 2, paramPaint);
      return;
      paramInt1 = 2;
      break;
    }
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    int i = getWidth();
    int j = getHeight();
    float f1 = -this.rotation * 2.0F;
    float f2 = f1 % 5.0F;
    int k = (int)Math.floor(f1 / 5.0F);
    int m = 0;
    if (m < 16)
    {
      Paint localPaint1 = this.whitePaint;
      Paint localPaint2;
      if (m >= k)
      {
        localPaint2 = localPaint1;
        if (m == 0)
        {
          localPaint2 = localPaint1;
          if (f2 >= 0.0F) {}
        }
      }
      else
      {
        localPaint2 = this.bluePaint;
      }
      label113:
      int n;
      if ((m == k) || ((m == 0) && (k == -1)))
      {
        bool = true;
        drawLine(paramCanvas, m, f2, i, j, bool, localPaint2);
        if (m != 0)
        {
          n = -m;
          if (n <= k) {
            break label190;
          }
          localPaint2 = this.bluePaint;
          label151:
          if (n != k + 1) {
            break label199;
          }
        }
      }
      label190:
      label199:
      for (boolean bool = true;; bool = false)
      {
        drawLine(paramCanvas, n, f2, i, j, bool, localPaint2);
        m++;
        break;
        bool = false;
        break label113;
        localPaint2 = this.whitePaint;
        break label151;
      }
    }
    this.bluePaint.setAlpha(255);
    this.tempRect.left = ((i - AndroidUtilities.dp(2.5F)) / 2);
    this.tempRect.top = ((j - AndroidUtilities.dp(22.0F)) / 2);
    this.tempRect.right = ((AndroidUtilities.dp(2.5F) + i) / 2);
    this.tempRect.bottom = ((AndroidUtilities.dp(22.0F) + j) / 2);
    paramCanvas.drawRoundRect(this.tempRect, AndroidUtilities.dp(2.0F), AndroidUtilities.dp(2.0F), this.bluePaint);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(Math.min(View.MeasureSpec.getSize(paramInt1), AndroidUtilities.dp(400.0F)), NUM), paramInt2);
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getActionMasked();
    float f1 = paramMotionEvent.getX();
    if (i == 0)
    {
      this.prevX = f1;
      if (this.rotationListener != null) {
        this.rotationListener.onStart();
      }
    }
    for (;;)
    {
      return true;
      if ((i == 1) || (i == 3))
      {
        if (this.rotationListener != null) {
          this.rotationListener.onEnd(this.rotation);
        }
      }
      else if (i == 2)
      {
        float f2 = this.prevX;
        float f3 = Math.max(-45.0F, Math.min(45.0F, this.rotation + (float)((f2 - f1) / AndroidUtilities.density / 3.141592653589793D / 1.649999976158142D)));
        if (Math.abs(f3 - this.rotation) > 0.001D)
        {
          f2 = f3;
          if (Math.abs(f3) < 0.05D) {
            f2 = 0.0F;
          }
          setRotation(f2, false);
          if (this.rotationListener != null) {
            this.rotationListener.onChange(this.rotation);
          }
          this.prevX = f1;
        }
      }
    }
  }
  
  public void reset()
  {
    setRotation(0.0F, false);
  }
  
  public void setAspectLock(boolean paramBoolean)
  {
    ImageView localImageView = this.aspectRatioButton;
    if (paramBoolean) {}
    for (PorterDuffColorFilter localPorterDuffColorFilter = new PorterDuffColorFilter(-11420173, PorterDuff.Mode.MULTIPLY);; localPorterDuffColorFilter = null)
    {
      localImageView.setColorFilter(localPorterDuffColorFilter);
      return;
    }
  }
  
  public void setFreeform(boolean paramBoolean)
  {
    ImageView localImageView = this.aspectRatioButton;
    if (paramBoolean) {}
    for (int i = 0;; i = 8)
    {
      localImageView.setVisibility(i);
      return;
    }
  }
  
  public void setListener(RotationWheelListener paramRotationWheelListener)
  {
    this.rotationListener = paramRotationWheelListener;
  }
  
  public void setRotation(float paramFloat, boolean paramBoolean)
  {
    this.rotation = paramFloat;
    float f = this.rotation;
    paramFloat = f;
    if (Math.abs(f) < 0.099D) {
      paramFloat = Math.abs(f);
    }
    this.degreesLabel.setText(String.format("%.1fº", new Object[] { Float.valueOf(paramFloat) }));
    invalidate();
  }
  
  public static abstract interface RotationWheelListener
  {
    public abstract void aspectRatioPressed();
    
    public abstract void onChange(float paramFloat);
    
    public abstract void onEnd(float paramFloat);
    
    public abstract void onStart();
    
    public abstract void rotate90Pressed();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/Crop/CropRotationWheel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */