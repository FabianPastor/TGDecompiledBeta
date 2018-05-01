package org.telegram.ui.Components;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.SweepGradient;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;

public class ColorPickerDialog
  extends AlertDialog
{
  public ColorPickerDialog(Context paramContext)
  {
    super(paramContext);
    setView(new ColorPicker(paramContext));
    setButton(-1, LocaleController.getString("Set", NUM), new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {}
    });
    setButton(-2, LocaleController.getString("Cancel", NUM), new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {}
    });
  }
  
  private class ColorPicker
    extends View
  {
    private int arrowPointerSize;
    private float[] colorHSV = { 0.0F, 0.0F, 1.0F };
    private RectF colorPointerCoords;
    private Paint colorPointerPaint;
    private Paint colorViewPaint;
    private Bitmap colorWheelBitmap;
    private Paint colorWheelPaint;
    private int colorWheelRadius;
    private int innerPadding;
    private int innerWheelRadius;
    private int outerPadding;
    private final int paramArrowPointerSize = 4;
    private final int paramInnerPadding = 5;
    private final int paramOuterPadding = 2;
    private final int paramValueSliderWidth = 10;
    private Paint valuePointerArrowPaint;
    private Paint valuePointerPaint;
    private Paint valueSliderPaint;
    private int valueSliderWidth;
    
    public ColorPicker(Context paramContext)
    {
      super();
      init();
    }
    
    private Bitmap createColorWheelBitmap(int paramInt1, int paramInt2)
    {
      Bitmap localBitmap = Bitmap.createBitmap(paramInt1, paramInt2, Bitmap.Config.ARGB_8888);
      Object localObject = new int[13];
      float[] arrayOfFloat = new float[3];
      float[] tmp23_21 = arrayOfFloat;
      tmp23_21[0] = 0.0F;
      float[] tmp27_23 = tmp23_21;
      tmp27_23[1] = 1.0F;
      float[] tmp31_27 = tmp27_23;
      tmp31_27[2] = 1.0F;
      tmp31_27;
      int i = 0;
      while (i < localObject.length)
      {
        arrayOfFloat[0] = ((i * 30 + 180) % 360);
        localObject[i] = Color.HSVToColor(arrayOfFloat);
        i += 1;
      }
      localObject[12] = localObject[0];
      localObject = new ComposeShader(new SweepGradient(paramInt1 / 2, paramInt2 / 2, (int[])localObject, null), new RadialGradient(paramInt1 / 2, paramInt2 / 2, this.colorWheelRadius, -1, 16777215, Shader.TileMode.CLAMP), PorterDuff.Mode.SRC_OVER);
      this.colorWheelPaint.setShader((Shader)localObject);
      new Canvas(localBitmap).drawCircle(paramInt1 / 2, paramInt2 / 2, this.colorWheelRadius, this.colorWheelPaint);
      return localBitmap;
    }
    
    private void init()
    {
      this.colorPointerPaint = new Paint();
      this.colorPointerPaint.setStyle(Paint.Style.STROKE);
      this.colorPointerPaint.setStrokeWidth(2.0F);
      this.colorPointerPaint.setARGB(128, 0, 0, 0);
      this.valuePointerPaint = new Paint();
      this.valuePointerPaint.setStyle(Paint.Style.STROKE);
      this.valuePointerPaint.setStrokeWidth(2.0F);
      this.valuePointerArrowPaint = new Paint();
      this.colorWheelPaint = new Paint();
      this.colorWheelPaint.setAntiAlias(true);
      this.colorWheelPaint.setDither(true);
      this.valueSliderPaint = new Paint();
      this.valueSliderPaint.setAntiAlias(true);
      this.valueSliderPaint.setDither(true);
      this.colorViewPaint = new Paint();
      this.colorViewPaint.setAntiAlias(true);
      this.colorPointerCoords = new RectF();
    }
    
    public int getColor()
    {
      return Color.HSVToColor(this.colorHSV);
    }
    
    protected void onDraw(Canvas paramCanvas)
    {
      int k = getWidth() / 2;
      int i = getHeight() / 2;
      paramCanvas.drawBitmap(this.colorWheelBitmap, k - this.colorWheelRadius, i - this.colorWheelRadius, null);
      float f1 = (float)Math.toRadians(this.colorHSV[0]);
      int m = (int)(-Math.cos(f1) * this.colorHSV[1] * this.colorWheelRadius);
      int j = (int)(-Math.sin(f1) * this.colorHSV[1] * this.colorWheelRadius);
      f1 = 0.075F * this.colorWheelRadius;
      k = (int)(m + k - f1 / 2.0F);
      j = (int)(j + i - f1 / 2.0F);
      this.colorPointerCoords.set(k, j, k + f1, j + f1);
      paramCanvas.drawOval(this.colorPointerCoords, this.colorPointerPaint);
      f1 = this.colorHSV[0];
      float f2 = this.colorHSV[1];
      i = this.colorWheelRadius + i + AndroidUtilities.dp(10.0F);
      j = getMeasuredWidth();
      k = AndroidUtilities.dp(10.0F);
      float f3 = 0;
      float f4 = i;
      float f5 = 0 + j;
      float f6 = i + k;
      j = Color.HSVToColor(new float[] { f1, f2, 1.0F });
      Object localObject = Shader.TileMode.CLAMP;
      localObject = new LinearGradient(f3, f4, f5, f6, new int[] { -16777216, j, -1 }, null, (Shader.TileMode)localObject);
      this.valueSliderPaint.setShader((Shader)localObject);
      paramCanvas.drawRect(0, i, getMeasuredWidth() + 0, AndroidUtilities.dp(10.0F) + i, this.valueSliderPaint);
    }
    
    protected void onMeasure(int paramInt1, int paramInt2)
    {
      paramInt1 = Math.min(View.MeasureSpec.getSize(paramInt1), View.MeasureSpec.getSize(paramInt2));
      setMeasuredDimension(paramInt1, paramInt1);
    }
    
    protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      paramInt3 = paramInt1 / 2;
      paramInt2 /= 2;
      this.innerPadding = (paramInt1 * 5 / 100);
      this.outerPadding = (paramInt1 * 2 / 100);
      this.arrowPointerSize = (paramInt1 * 4 / 100);
      this.valueSliderWidth = (paramInt1 * 10 / 100);
      this.innerWheelRadius = (paramInt1 / 2 - this.outerPadding - this.arrowPointerSize - this.valueSliderWidth);
      this.colorWheelRadius = (this.innerWheelRadius - this.innerPadding);
      this.colorWheelBitmap = createColorWheelBitmap(this.colorWheelRadius * 2, this.colorWheelRadius * 2);
    }
    
    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      switch (paramMotionEvent.getAction())
      {
      case 1: 
      default: 
        return super.onTouchEvent(paramMotionEvent);
      }
      int i = (int)paramMotionEvent.getX();
      int k = (int)paramMotionEvent.getY();
      int j = i - getWidth() / 2;
      k -= getHeight() / 2;
      double d = Math.sqrt(j * j + k * k);
      if (d <= this.colorWheelRadius)
      {
        this.colorHSV[0] = ((float)(Math.toDegrees(Math.atan2(k, j)) + 180.0D));
        this.colorHSV[1] = Math.max(0.0F, Math.min(1.0F, (float)(d / this.colorWheelRadius)));
        invalidate();
      }
      for (;;)
      {
        return true;
        if ((i >= getWidth() / 2) && (d >= this.innerWheelRadius))
        {
          this.colorHSV[2] = ((float)Math.max(0.0D, Math.min(1.0D, Math.atan2(k, j) / 3.141592653589793D + 0.5D)));
          invalidate();
        }
      }
    }
    
    public void setColor(int paramInt)
    {
      Color.colorToHSV(paramInt, this.colorHSV);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/ColorPickerDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */