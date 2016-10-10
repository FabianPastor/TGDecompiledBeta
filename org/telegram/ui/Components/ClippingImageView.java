package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;

public class ClippingImageView
  extends View
{
  private float animationProgress;
  private float[][] animationValues;
  private RectF bitmapRect;
  private BitmapShader bitmapShader;
  private Bitmap bmp;
  private int clipBottom;
  private int clipLeft;
  private int clipRight;
  private int clipTop;
  private RectF drawRect;
  private Matrix matrix;
  private boolean needRadius;
  private int orientation;
  private Paint paint = new Paint();
  private int radius;
  private Paint roundPaint;
  private RectF roundRect;
  private Matrix shaderMatrix;
  
  public ClippingImageView(Context paramContext)
  {
    super(paramContext);
    this.paint.setFilterBitmap(true);
    this.matrix = new Matrix();
    this.drawRect = new RectF();
    this.bitmapRect = new RectF();
    this.roundPaint = new Paint(1);
    this.roundRect = new RectF();
    this.shaderMatrix = new Matrix();
  }
  
  public float getAnimationProgress()
  {
    return this.animationProgress;
  }
  
  public int getClipBottom()
  {
    return this.clipBottom;
  }
  
  public int getClipHorizontal()
  {
    return this.clipRight;
  }
  
  public int getClipLeft()
  {
    return this.clipLeft;
  }
  
  public int getClipRight()
  {
    return this.clipRight;
  }
  
  public int getClipTop()
  {
    return this.clipTop;
  }
  
  public int getRadius()
  {
    return this.radius;
  }
  
  public void onDraw(Canvas paramCanvas)
  {
    if (getVisibility() != 0) {}
    while (this.bmp == null) {
      return;
    }
    float f3 = getScaleY();
    paramCanvas.save();
    if (this.needRadius)
    {
      this.shaderMatrix.reset();
      this.roundRect.set(0.0F, 0.0F, getWidth(), getHeight());
      int j;
      int i;
      float f1;
      label121:
      float f2;
      if ((this.orientation % 360 == 90) || (this.orientation % 360 == 270))
      {
        j = this.bmp.getHeight();
        i = this.bmp.getWidth();
        if (getWidth() == 0) {
          break label346;
        }
        f1 = j / getWidth();
        if (getHeight() == 0) {
          break label351;
        }
        f2 = i / getHeight();
        label138:
        float f4 = Math.min(f1, f2);
        if (Math.abs(f1 - f2) <= 1.0E-5F) {
          break label356;
        }
        int k = (int)Math.floor(getWidth() * f4);
        int m = (int)Math.floor(getHeight() * f4);
        this.bitmapRect.set((j - k) / 2, (i - m) / 2, k, m);
        AndroidUtilities.setRectToRect(this.shaderMatrix, this.bitmapRect, this.roundRect, this.orientation, Matrix.ScaleToFit.START);
      }
      for (;;)
      {
        this.bitmapShader.setLocalMatrix(this.shaderMatrix);
        paramCanvas.clipRect(this.clipLeft / f3, this.clipTop / f3, getWidth() - this.clipRight / f3, getHeight() - this.clipBottom / f3);
        paramCanvas.drawRoundRect(this.roundRect, this.radius, this.radius, this.roundPaint);
        paramCanvas.restore();
        return;
        j = this.bmp.getWidth();
        i = this.bmp.getHeight();
        break;
        label346:
        f1 = 1.0F;
        break label121;
        label351:
        f2 = 1.0F;
        break label138;
        label356:
        this.bitmapRect.set(0.0F, 0.0F, this.bmp.getWidth(), this.bmp.getHeight());
        AndroidUtilities.setRectToRect(this.shaderMatrix, this.bitmapRect, this.roundRect, this.orientation, Matrix.ScaleToFit.FILL);
      }
    }
    if ((this.orientation == 90) || (this.orientation == 270))
    {
      this.drawRect.set(-getHeight() / 2, -getWidth() / 2, getHeight() / 2, getWidth() / 2);
      this.matrix.setRectToRect(this.bitmapRect, this.drawRect, Matrix.ScaleToFit.FILL);
      this.matrix.postRotate(this.orientation, 0.0F, 0.0F);
      this.matrix.postTranslate(getWidth() / 2, getHeight() / 2);
    }
    for (;;)
    {
      paramCanvas.clipRect(this.clipLeft / f3, this.clipTop / f3, getWidth() - this.clipRight / f3, getHeight() - this.clipBottom / f3);
      try
      {
        paramCanvas.drawBitmap(this.bmp, this.matrix, this.paint);
      }
      catch (Exception localException)
      {
        FileLog.e("tmessages", localException);
      }
      break;
      if (this.orientation == 180)
      {
        this.drawRect.set(-getWidth() / 2, -getHeight() / 2, getWidth() / 2, getHeight() / 2);
        this.matrix.setRectToRect(this.bitmapRect, this.drawRect, Matrix.ScaleToFit.FILL);
        this.matrix.postRotate(this.orientation, 0.0F, 0.0F);
        this.matrix.postTranslate(getWidth() / 2, getHeight() / 2);
      }
      else
      {
        this.drawRect.set(0.0F, 0.0F, getWidth(), getHeight());
        this.matrix.setRectToRect(this.bitmapRect, this.drawRect, Matrix.ScaleToFit.FILL);
      }
    }
  }
  
  public void setAnimationProgress(float paramFloat)
  {
    this.animationProgress = paramFloat;
    setScaleX(this.animationValues[0][0] + (this.animationValues[1][0] - this.animationValues[0][0]) * this.animationProgress);
    setScaleY(this.animationValues[0][1] + (this.animationValues[1][1] - this.animationValues[0][1]) * this.animationProgress);
    setTranslationX(this.animationValues[0][2] + (this.animationValues[1][2] - this.animationValues[0][2]) * this.animationProgress);
    setTranslationY(this.animationValues[0][3] + (this.animationValues[1][3] - this.animationValues[0][3]) * this.animationProgress);
    setClipHorizontal((int)(this.animationValues[0][4] + (this.animationValues[1][4] - this.animationValues[0][4]) * this.animationProgress));
    setClipTop((int)(this.animationValues[0][5] + (this.animationValues[1][5] - this.animationValues[0][5]) * this.animationProgress));
    setClipBottom((int)(this.animationValues[0][6] + (this.animationValues[1][6] - this.animationValues[0][6]) * this.animationProgress));
    setRadius((int)(this.animationValues[0][7] + (this.animationValues[1][7] - this.animationValues[0][7]) * this.animationProgress));
    invalidate();
  }
  
  public void setAnimationValues(float[][] paramArrayOfFloat)
  {
    this.animationValues = paramArrayOfFloat;
  }
  
  public void setClipBottom(int paramInt)
  {
    this.clipBottom = paramInt;
    invalidate();
  }
  
  public void setClipHorizontal(int paramInt)
  {
    this.clipRight = paramInt;
    this.clipLeft = paramInt;
    invalidate();
  }
  
  public void setClipLeft(int paramInt)
  {
    this.clipLeft = paramInt;
    invalidate();
  }
  
  public void setClipRight(int paramInt)
  {
    this.clipRight = paramInt;
    invalidate();
  }
  
  public void setClipTop(int paramInt)
  {
    this.clipTop = paramInt;
    invalidate();
  }
  
  public void setClipVertical(int paramInt)
  {
    this.clipBottom = paramInt;
    this.clipTop = paramInt;
    invalidate();
  }
  
  public void setImageBitmap(Bitmap paramBitmap)
  {
    this.bmp = paramBitmap;
    if (paramBitmap != null)
    {
      this.bitmapRect.set(0.0F, 0.0F, paramBitmap.getWidth(), paramBitmap.getHeight());
      if (this.needRadius)
      {
        this.bitmapShader = new BitmapShader(paramBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        this.roundPaint.setShader(this.bitmapShader);
      }
    }
    invalidate();
  }
  
  public void setNeedRadius(boolean paramBoolean)
  {
    this.needRadius = paramBoolean;
  }
  
  public void setOrientation(int paramInt)
  {
    this.orientation = paramInt;
  }
  
  public void setRadius(int paramInt)
  {
    this.radius = paramInt;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/ClippingImageView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */