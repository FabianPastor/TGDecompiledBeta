package org.telegram.ui.Components.Paint.Views;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Paint.Swatch;

public class ColorPicker
  extends FrameLayout
{
  private static final int[] COLORS = { -1431751, -2409774, -13610525, -11942419, -8337308, -205211, -223667, -16777216, -1 };
  private static final float[] LOCATIONS = { 0.0F, 0.14F, 0.24F, 0.39F, 0.49F, 0.62F, 0.73F, 0.85F, 1.0F };
  private Paint backgroundPaint = new Paint(1);
  private boolean changingWeight;
  private ColorPickerDelegate delegate;
  private boolean dragging;
  private float draggingFactor;
  private Paint gradientPaint = new Paint(1);
  private boolean interacting;
  private OvershootInterpolator interpolator = new OvershootInterpolator(1.02F);
  private float location = 1.0F;
  private RectF rectF = new RectF();
  private ImageView settingsButton;
  private Drawable shadowDrawable;
  private Paint swatchPaint = new Paint(1);
  private Paint swatchStrokePaint = new Paint(1);
  private ImageView undoButton;
  private boolean wasChangingWeight;
  private float weight = 0.27F;
  
  public ColorPicker(Context paramContext)
  {
    super(paramContext);
    setWillNotDraw(false);
    this.shadowDrawable = getResources().getDrawable(NUM);
    this.backgroundPaint.setColor(-1);
    this.swatchStrokePaint.setStyle(Paint.Style.STROKE);
    this.swatchStrokePaint.setStrokeWidth(AndroidUtilities.dp(1.0F));
    this.settingsButton = new ImageView(paramContext);
    this.settingsButton.setScaleType(ImageView.ScaleType.CENTER);
    this.settingsButton.setImageResource(NUM);
    addView(this.settingsButton, LayoutHelper.createFrame(60, 52.0F));
    this.settingsButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (ColorPicker.this.delegate != null) {
          ColorPicker.this.delegate.onSettingsPressed();
        }
      }
    });
    this.undoButton = new ImageView(paramContext);
    this.undoButton.setScaleType(ImageView.ScaleType.CENTER);
    this.undoButton.setImageResource(NUM);
    addView(this.undoButton, LayoutHelper.createFrame(60, 52.0F));
    this.undoButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (ColorPicker.this.delegate != null) {
          ColorPicker.this.delegate.onUndoPressed();
        }
      }
    });
    this.location = paramContext.getSharedPreferences("paint", 0).getFloat("last_color_location", 1.0F);
    setLocation(this.location);
  }
  
  private int interpolateColors(int paramInt1, int paramInt2, float paramFloat)
  {
    paramFloat = Math.min(Math.max(paramFloat, 0.0F), 1.0F);
    int i = Color.red(paramInt1);
    int j = Color.red(paramInt2);
    int k = Color.green(paramInt1);
    int m = Color.green(paramInt2);
    paramInt1 = Color.blue(paramInt1);
    paramInt2 = Color.blue(paramInt2);
    return Color.argb(255, Math.min(255, (int)(i + (j - i) * paramFloat)), Math.min(255, (int)(k + (m - k) * paramFloat)), Math.min(255, (int)(paramInt1 + (paramInt2 - paramInt1) * paramFloat)));
  }
  
  private void setDragging(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.dragging == paramBoolean1) {}
    for (;;)
    {
      return;
      this.dragging = paramBoolean1;
      if (this.dragging) {}
      for (float f = 1.0F;; f = 0.0F)
      {
        if (!paramBoolean2) {
          break label107;
        }
        ObjectAnimator localObjectAnimator = ObjectAnimator.ofFloat(this, "draggingFactor", new float[] { this.draggingFactor, f });
        localObjectAnimator.setInterpolator(this.interpolator);
        int i = 300;
        if (this.wasChangingWeight) {
          i = (int)('Ĭ' + this.weight * 75.0F);
        }
        localObjectAnimator.setDuration(i);
        localObjectAnimator.start();
        break;
      }
      label107:
      setDraggingFactor(f);
    }
  }
  
  private void setDraggingFactor(float paramFloat)
  {
    this.draggingFactor = paramFloat;
    invalidate();
  }
  
  public int colorForLocation(float paramFloat)
  {
    if (paramFloat <= 0.0F) {}
    for (int i = COLORS[0];; i = COLORS[(COLORS.length - 1)])
    {
      return i;
      if (paramFloat < 1.0F) {
        break;
      }
    }
    int j = -1;
    int k = -1;
    for (i = 1;; i++)
    {
      int m = j;
      int n = k;
      if (i < LOCATIONS.length)
      {
        if (LOCATIONS[i] > paramFloat)
        {
          m = i - 1;
          n = i;
        }
      }
      else
      {
        float f1 = LOCATIONS[m];
        i = COLORS[m];
        float f2 = LOCATIONS[n];
        i = interpolateColors(i, COLORS[n], (paramFloat - f1) / (f2 - f1));
        break;
      }
    }
  }
  
  public float getDraggingFactor()
  {
    return this.draggingFactor;
  }
  
  public View getSettingsButton()
  {
    return this.settingsButton;
  }
  
  public Swatch getSwatch()
  {
    return new Swatch(colorForLocation(this.location), this.location, this.weight);
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    paramCanvas.drawRoundRect(this.rectF, AndroidUtilities.dp(6.0F), AndroidUtilities.dp(6.0F), this.gradientPaint);
    int i = (int)(this.rectF.left + this.rectF.width() * this.location);
    float f1 = this.rectF.centerY();
    float f2 = this.draggingFactor;
    float f3 = -AndroidUtilities.dp(70.0F);
    if (this.changingWeight) {}
    for (float f4 = this.weight * AndroidUtilities.dp(190.0F);; f4 = 0.0F)
    {
      int j = (int)(f2 * f3 + f1 - f4);
      int k = (int)(AndroidUtilities.dp(24.0F) * ((this.draggingFactor + 1.0F) * 0.5F));
      this.shadowDrawable.setBounds(i - k, j - k, i + k, j + k);
      this.shadowDrawable.draw(paramCanvas);
      f4 = (int)Math.floor(AndroidUtilities.dp(4.0F) + (AndroidUtilities.dp(19.0F) - AndroidUtilities.dp(4.0F)) * this.weight) * (this.draggingFactor + 1.0F) / 2.0F;
      paramCanvas.drawCircle(i, j, AndroidUtilities.dp(22.0F) / 2 * (this.draggingFactor + 1.0F), this.backgroundPaint);
      paramCanvas.drawCircle(i, j, f4, this.swatchPaint);
      paramCanvas.drawCircle(i, j, f4 - AndroidUtilities.dp(0.5F), this.swatchStrokePaint);
      return;
    }
  }
  
  @SuppressLint({"DrawAllocation"})
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramInt1 = paramInt3 - paramInt1;
    paramInt2 = paramInt4 - paramInt2;
    this.gradientPaint.setShader(new LinearGradient(AndroidUtilities.dp(56.0F), 0.0F, paramInt1 - AndroidUtilities.dp(56.0F), 0.0F, COLORS, LOCATIONS, Shader.TileMode.REPEAT));
    paramInt3 = paramInt2 - AndroidUtilities.dp(32.0F);
    this.rectF.set(AndroidUtilities.dp(56.0F), paramInt3, paramInt1 - AndroidUtilities.dp(56.0F), AndroidUtilities.dp(12.0F) + paramInt3);
    this.settingsButton.layout(paramInt1 - this.settingsButton.getMeasuredWidth(), paramInt2 - AndroidUtilities.dp(52.0F), paramInt1, paramInt2);
    this.undoButton.layout(0, paramInt2 - AndroidUtilities.dp(52.0F), this.settingsButton.getMeasuredWidth(), paramInt2);
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool1 = false;
    boolean bool2;
    if (paramMotionEvent.getPointerCount() > 1) {
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      float f1 = paramMotionEvent.getX();
      float f2 = this.rectF.left;
      float f3 = paramMotionEvent.getY() - this.rectF.top;
      if (!this.interacting)
      {
        bool2 = bool1;
        if (f3 < -AndroidUtilities.dp(10.0F)) {}
      }
      else
      {
        int i = paramMotionEvent.getActionMasked();
        if ((i == 3) || (i == 1) || (i == 6))
        {
          if ((this.interacting) && (this.delegate != null))
          {
            this.delegate.onFinishedColorPicking();
            getContext().getSharedPreferences("paint", 0).edit().putFloat("last_color_location", this.location).commit();
          }
          this.interacting = false;
          this.wasChangingWeight = this.changingWeight;
          this.changingWeight = false;
          setDragging(false, true);
          bool2 = bool1;
        }
        else if (i != 0)
        {
          bool2 = bool1;
          if (i != 2) {}
        }
        else
        {
          if (!this.interacting)
          {
            this.interacting = true;
            if (this.delegate != null) {
              this.delegate.onBeganColorPicking();
            }
          }
          setLocation(Math.max(0.0F, Math.min(1.0F, (f1 - f2) / this.rectF.width())));
          setDragging(true, true);
          if (f3 < -AndroidUtilities.dp(10.0F))
          {
            this.changingWeight = true;
            setWeight(Math.max(0.0F, Math.min(1.0F, (-f3 - AndroidUtilities.dp(10.0F)) / AndroidUtilities.dp(190.0F))));
          }
          if (this.delegate != null) {
            this.delegate.onColorValueChanged();
          }
          bool2 = true;
        }
      }
    }
  }
  
  public void setDelegate(ColorPickerDelegate paramColorPickerDelegate)
  {
    this.delegate = paramColorPickerDelegate;
  }
  
  public void setLocation(float paramFloat)
  {
    this.location = paramFloat;
    int i = colorForLocation(paramFloat);
    this.swatchPaint.setColor(i);
    float[] arrayOfFloat = new float[3];
    Color.colorToHSV(i, arrayOfFloat);
    if ((arrayOfFloat[0] < 0.001D) && (arrayOfFloat[1] < 0.001D) && (arrayOfFloat[2] > 0.92F))
    {
      i = (int)((1.0F - (arrayOfFloat[2] - 0.92F) / 0.08F * 0.22F) * 255.0F);
      this.swatchStrokePaint.setColor(Color.rgb(i, i, i));
    }
    for (;;)
    {
      invalidate();
      return;
      this.swatchStrokePaint.setColor(i);
    }
  }
  
  public void setSettingsButtonImage(int paramInt)
  {
    this.settingsButton.setImageResource(paramInt);
  }
  
  public void setSwatch(Swatch paramSwatch)
  {
    setLocation(paramSwatch.colorLocation);
    setWeight(paramSwatch.brushWeight);
  }
  
  public void setUndoEnabled(boolean paramBoolean)
  {
    ImageView localImageView = this.undoButton;
    if (paramBoolean) {}
    for (float f = 1.0F;; f = 0.3F)
    {
      localImageView.setAlpha(f);
      this.undoButton.setEnabled(paramBoolean);
      return;
    }
  }
  
  public void setWeight(float paramFloat)
  {
    this.weight = paramFloat;
    invalidate();
  }
  
  public static abstract interface ColorPickerDelegate
  {
    public abstract void onBeganColorPicking();
    
    public abstract void onColorValueChanged();
    
    public abstract void onFinishedColorPicking();
    
    public abstract void onSettingsPressed();
    
    public abstract void onUndoPressed();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/Paint/Views/ColorPicker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */