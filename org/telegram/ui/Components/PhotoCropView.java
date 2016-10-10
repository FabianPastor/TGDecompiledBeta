package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;

public class PhotoCropView
  extends FrameLayout
{
  private RectF animationEndValues;
  private Runnable animationRunnable;
  private RectF animationStartValues;
  private float bitmapGlobalScale = 1.0F;
  private float bitmapGlobalX = 0.0F;
  private float bitmapGlobalY = 0.0F;
  private int bitmapHeight = 1;
  private Bitmap bitmapToEdit;
  private int bitmapWidth = 1;
  private int bitmapX;
  private int bitmapY;
  private Paint circlePaint;
  private PhotoCropViewDelegate delegate;
  private int draggingState = 0;
  private boolean freeformCrop = true;
  private Paint halfPaint;
  private float oldX = 0.0F;
  private float oldY = 0.0F;
  private int orientation;
  private Paint rectPaint = new Paint();
  private float rectSizeX = 600.0F;
  private float rectSizeY = 600.0F;
  private float rectX = -1.0F;
  private float rectY = -1.0F;
  private Paint shadowPaint;
  
  public PhotoCropView(Context paramContext)
  {
    super(paramContext);
    this.rectPaint.setColor(-1291845633);
    this.rectPaint.setStrokeWidth(AndroidUtilities.dp(2.0F));
    this.rectPaint.setStyle(Paint.Style.STROKE);
    this.circlePaint = new Paint();
    this.circlePaint.setColor(-1);
    this.halfPaint = new Paint();
    this.halfPaint.setColor(2130706432);
    this.shadowPaint = new Paint();
    this.shadowPaint.setColor(436207616);
    setWillNotDraw(false);
  }
  
  private Bitmap createBitmap(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Bitmap localBitmap = this.delegate.getBitmap();
    if (localBitmap != null) {
      this.bitmapToEdit = localBitmap;
    }
    localBitmap = Bitmap.createBitmap(paramInt3, paramInt4, Bitmap.Config.ARGB_8888);
    Canvas localCanvas = new Canvas(localBitmap);
    Paint localPaint = new Paint(6);
    Matrix localMatrix = new Matrix();
    localMatrix.setTranslate(-this.bitmapToEdit.getWidth() / 2, -this.bitmapToEdit.getHeight() / 2);
    localMatrix.postRotate(this.orientation);
    if ((this.orientation % 360 == 90) || (this.orientation % 360 == 270)) {
      localMatrix.postTranslate(this.bitmapToEdit.getHeight() / 2 - paramInt1, this.bitmapToEdit.getWidth() / 2 - paramInt2);
    }
    for (;;)
    {
      localCanvas.drawBitmap(this.bitmapToEdit, localMatrix, localPaint);
      try
      {
        localCanvas.setBitmap(null);
        return localBitmap;
      }
      catch (Exception localException) {}
      localMatrix.postTranslate(this.bitmapToEdit.getWidth() / 2 - paramInt1, this.bitmapToEdit.getHeight() / 2 - paramInt2);
    }
    return localBitmap;
  }
  
  public void cancelAnimationRunnable()
  {
    if (this.animationRunnable != null)
    {
      AndroidUtilities.cancelRunOnUIThread(this.animationRunnable);
      this.animationRunnable = null;
      this.animationStartValues = null;
      this.animationEndValues = null;
    }
  }
  
  public Bitmap getBitmap()
  {
    Bitmap localBitmap1 = this.delegate.getBitmap();
    if (localBitmap1 != null) {
      this.bitmapToEdit = localBitmap1;
    }
    float f2 = this.bitmapWidth * this.bitmapGlobalScale;
    float f1 = this.bitmapHeight * this.bitmapGlobalScale;
    float f6 = (getWidth() - AndroidUtilities.dp(28.0F) - f2) / 2.0F;
    float f7 = this.bitmapGlobalX;
    float f8 = AndroidUtilities.dp(14.0F);
    float f3 = (getHeight() - AndroidUtilities.dp(28.0F) - f1) / 2.0F;
    float f4 = this.bitmapGlobalY;
    float f5 = AndroidUtilities.dp(14.0F);
    f6 = (this.rectX - (f6 + f7 + f8)) / f2;
    f3 = (this.rectY - (f3 + f4 + f5)) / f1;
    f2 = this.rectSizeX / f2;
    f1 = this.rectSizeY / f1;
    int k;
    if ((this.orientation % 360 == 90) || (this.orientation % 360 == 270)) {
      k = this.bitmapToEdit.getHeight();
    }
    int m;
    int n;
    int j;
    for (int i = this.bitmapToEdit.getWidth();; i = this.bitmapToEdit.getHeight())
    {
      m = (int)(k * f6);
      n = (int)(i * f3);
      int i2 = (int)(k * f2);
      int i1 = (int)(i * f1);
      j = m;
      if (m < 0) {
        j = 0;
      }
      m = n;
      if (n < 0) {
        m = 0;
      }
      n = i2;
      if (j + i2 > k) {
        n = k - j;
      }
      k = i1;
      if (m + i1 > i) {
        k = i - m;
      }
      try
      {
        localBitmap1 = createBitmap(j, m, n, k);
        return localBitmap1;
      }
      catch (Throwable localThrowable1)
      {
        FileLog.e("tmessags", localThrowable1);
        System.gc();
        try
        {
          Bitmap localBitmap2 = createBitmap(j, m, n, k);
          return localBitmap2;
        }
        catch (Throwable localThrowable2)
        {
          FileLog.e("tmessages", localThrowable2);
        }
      }
      k = this.bitmapToEdit.getWidth();
    }
    return null;
  }
  
  public float getBitmapX()
  {
    return this.bitmapX - AndroidUtilities.dp(14.0F);
  }
  
  public float getBitmapY()
  {
    return this.bitmapY - AndroidUtilities.dp(14.0F);
  }
  
  public float getLimitHeight()
  {
    return getHeight() - AndroidUtilities.dp(14.0F) - this.rectY - (int)Math.max(0.0D, Math.ceil((getHeight() - AndroidUtilities.dp(28.0F) - this.bitmapHeight * this.bitmapGlobalScale) / 2.0F)) - this.rectSizeY;
  }
  
  public float getLimitWidth()
  {
    return getWidth() - AndroidUtilities.dp(14.0F) - this.rectX - (int)Math.max(0.0D, Math.ceil((getWidth() - AndroidUtilities.dp(28.0F) - this.bitmapWidth * this.bitmapGlobalScale) / 2.0F)) - this.rectSizeX;
  }
  
  public float getLimitX()
  {
    return this.rectX - ((int)Math.max(0.0D, Math.ceil((getWidth() - AndroidUtilities.dp(28.0F) - this.bitmapWidth * this.bitmapGlobalScale) / 2.0F)) + AndroidUtilities.dp(14.0F));
  }
  
  public float getLimitY()
  {
    return this.rectY - ((int)Math.max(0.0D, Math.ceil((getHeight() - AndroidUtilities.dp(28.0F) - this.bitmapHeight * this.bitmapGlobalScale) / 2.0F)) + AndroidUtilities.dp(14.0F));
  }
  
  public float getRectSizeX()
  {
    return this.rectSizeX;
  }
  
  public float getRectSizeY()
  {
    return this.rectSizeY;
  }
  
  public float getRectX()
  {
    return this.rectX - AndroidUtilities.dp(14.0F);
  }
  
  public float getRectY()
  {
    return this.rectY - AndroidUtilities.dp(14.0F);
  }
  
  public void moveToFill(boolean paramBoolean)
  {
    float f2 = this.bitmapWidth / this.rectSizeX;
    float f1 = this.bitmapHeight / this.rectSizeY;
    if (f2 > f1)
    {
      f2 = f1;
      if ((f2 <= 1.0F) || (this.bitmapGlobalScale * f2 <= 3.0F)) {
        break label261;
      }
      f1 = 3.0F / this.bitmapGlobalScale;
    }
    for (;;)
    {
      float f4 = this.rectSizeX * f1;
      float f5 = this.rectSizeY * f1;
      f2 = (getWidth() - AndroidUtilities.dp(28.0F) - f4) / 2.0F + AndroidUtilities.dp(14.0F);
      float f3 = (getHeight() - AndroidUtilities.dp(28.0F) - f5) / 2.0F + AndroidUtilities.dp(14.0F);
      this.animationStartValues = new RectF(this.rectX, this.rectY, this.rectSizeX, this.rectSizeY);
      this.animationEndValues = new RectF(f2, f3, f4, f5);
      f4 = getWidth() / 2;
      f5 = this.bitmapGlobalX;
      float f6 = this.rectX;
      float f7 = getHeight() / 2;
      float f8 = this.bitmapGlobalY;
      float f9 = this.rectY;
      this.delegate.needMoveImageTo(f4 * (f1 - 1.0F) + f2 + (f5 - f6) * f1, f7 * (f1 - 1.0F) + f3 + (f8 - f9) * f1, this.bitmapGlobalScale * f1, paramBoolean);
      return;
      break;
      label261:
      f1 = f2;
      if (f2 < 1.0F)
      {
        f1 = f2;
        if (this.bitmapGlobalScale * f2 < 1.0F) {
          f1 = 1.0F / this.bitmapGlobalScale;
        }
      }
    }
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    paramCanvas.drawRect(0.0F, 0.0F, getWidth(), this.rectY, this.halfPaint);
    float f1 = this.rectY;
    float f2 = this.rectX;
    float f3 = this.rectY;
    paramCanvas.drawRect(0.0F, f1, f2, this.rectSizeY + f3, this.halfPaint);
    paramCanvas.drawRect(this.rectX + this.rectSizeX, this.rectY, getWidth(), this.rectY + this.rectSizeY, this.halfPaint);
    f1 = this.rectY;
    paramCanvas.drawRect(0.0F, this.rectSizeY + f1, getWidth(), getHeight(), this.halfPaint);
    int j = AndroidUtilities.dp(1.0F);
    f1 = this.rectX;
    f2 = j * 2;
    f3 = this.rectY;
    float f4 = j * 2;
    float f5 = this.rectX;
    float f6 = j * 2;
    paramCanvas.drawRect(f1 - f2, f3 - f4, AndroidUtilities.dp(20.0F) + (f5 - f6), this.rectY, this.circlePaint);
    f1 = this.rectX;
    f2 = j * 2;
    f3 = this.rectY;
    f4 = j * 2;
    f5 = this.rectX;
    f6 = this.rectY;
    float f7 = j * 2;
    paramCanvas.drawRect(f1 - f2, f3 - f4, f5, AndroidUtilities.dp(20.0F) + (f6 - f7), this.circlePaint);
    f1 = this.rectX;
    f2 = this.rectSizeX;
    f3 = j * 2;
    f4 = AndroidUtilities.dp(20.0F);
    f5 = this.rectY;
    f6 = j * 2;
    f7 = this.rectX;
    float f8 = this.rectSizeX;
    paramCanvas.drawRect(f1 + f2 + f3 - f4, f5 - f6, j * 2 + (f7 + f8), this.rectY, this.circlePaint);
    f1 = this.rectX;
    f2 = this.rectSizeX;
    f3 = this.rectY;
    f4 = j * 2;
    f5 = this.rectX;
    f6 = this.rectSizeX;
    f7 = j * 2;
    f8 = this.rectY;
    float f9 = j * 2;
    paramCanvas.drawRect(f2 + f1, f3 - f4, f7 + (f5 + f6), AndroidUtilities.dp(20.0F) + (f8 - f9), this.circlePaint);
    f1 = this.rectX;
    f2 = j * 2;
    f3 = this.rectY;
    f4 = this.rectSizeY;
    f5 = j * 2;
    f6 = AndroidUtilities.dp(20.0F);
    f7 = this.rectX;
    f8 = this.rectY;
    f9 = this.rectSizeY;
    paramCanvas.drawRect(f1 - f2, f3 + f4 + f5 - f6, f7, j * 2 + (f8 + f9), this.circlePaint);
    f1 = this.rectX;
    f2 = j * 2;
    f3 = this.rectY;
    f4 = this.rectSizeY;
    f5 = this.rectX;
    f6 = j * 2;
    f7 = AndroidUtilities.dp(20.0F);
    f8 = this.rectY;
    f9 = this.rectSizeY;
    paramCanvas.drawRect(f1 - f2, f4 + f3, f7 + (f5 - f6), j * 2 + (f8 + f9), this.circlePaint);
    f1 = this.rectX;
    f2 = this.rectSizeX;
    f3 = j * 2;
    f4 = AndroidUtilities.dp(20.0F);
    f5 = this.rectY;
    f6 = this.rectSizeY;
    f7 = this.rectX;
    f8 = this.rectSizeX;
    f9 = j * 2;
    float f10 = this.rectY;
    float f11 = this.rectSizeY;
    paramCanvas.drawRect(f1 + f2 + f3 - f4, f6 + f5, f9 + (f7 + f8), j * 2 + (f10 + f11), this.circlePaint);
    f1 = this.rectX;
    f2 = this.rectSizeX;
    f3 = this.rectY;
    f4 = this.rectSizeY;
    f5 = j * 2;
    f6 = AndroidUtilities.dp(20.0F);
    f7 = this.rectX;
    f8 = this.rectSizeX;
    f9 = j * 2;
    f10 = this.rectY;
    f11 = this.rectSizeY;
    paramCanvas.drawRect(f2 + f1, f3 + f4 + f5 - f6, f9 + (f7 + f8), j * 2 + (f10 + f11), this.circlePaint);
    int i = 1;
    while (i < 3)
    {
      f1 = this.rectX;
      f2 = this.rectSizeX / 3.0F;
      f3 = i;
      f4 = j;
      f5 = this.rectY;
      f6 = this.rectX;
      f7 = j * 2;
      f8 = this.rectSizeX / 3.0F;
      f9 = i;
      f10 = this.rectY;
      paramCanvas.drawRect(f1 + f2 * f3 - f4, f5, f8 * f9 + (f6 + f7), this.rectSizeY + f10, this.shadowPaint);
      f1 = this.rectX;
      f2 = this.rectY;
      f3 = this.rectSizeY / 3.0F;
      f4 = i;
      f5 = j;
      f6 = this.rectX;
      f7 = this.rectSizeX;
      f8 = this.rectY;
      f9 = this.rectSizeY / 3.0F;
      f10 = i;
      paramCanvas.drawRect(f1, f2 + f3 * f4 - f5, f7 + f6, j * 2 + (f8 + f9 * f10), this.shadowPaint);
      i += 1;
    }
    i = 1;
    while (i < 3)
    {
      f1 = this.rectX;
      f2 = this.rectSizeX / 3.0F;
      f3 = i;
      f4 = this.rectY;
      f5 = this.rectX;
      f6 = j;
      f7 = this.rectSizeX / 3.0F;
      f8 = i;
      f9 = this.rectY;
      paramCanvas.drawRect(f2 * f3 + f1, f4, f7 * f8 + (f5 + f6), this.rectSizeY + f9, this.circlePaint);
      f1 = this.rectX;
      f2 = this.rectY;
      f3 = this.rectSizeY / 3.0F;
      f4 = i;
      f5 = this.rectX;
      f6 = this.rectSizeX;
      f7 = this.rectY;
      f8 = this.rectSizeY / 3.0F;
      f9 = i;
      paramCanvas.drawRect(f1, f3 * f4 + f2, f6 + f5, j + (f7 + f8 * f9), this.circlePaint);
      i += 1;
    }
    f1 = this.rectX;
    f2 = this.rectY;
    f3 = this.rectX;
    f4 = this.rectSizeX;
    f5 = this.rectY;
    paramCanvas.drawRect(f1, f2, f4 + f3, this.rectSizeY + f5, this.rectPaint);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    Bitmap localBitmap = this.delegate.getBitmap();
    if (localBitmap != null) {
      this.bitmapToEdit = localBitmap;
    }
    if (this.bitmapToEdit == null) {
      return;
    }
    paramInt1 = getWidth() - AndroidUtilities.dp(28.0F);
    paramInt2 = getHeight() - AndroidUtilities.dp(28.0F);
    float f2;
    float f1;
    float f3;
    float f4;
    if ((this.orientation % 360 == 90) || (this.orientation % 360 == 270))
    {
      f2 = this.bitmapToEdit.getHeight();
      f1 = this.bitmapToEdit.getWidth();
      f3 = paramInt1 / f2;
      f4 = paramInt2 / f1;
      if (f3 <= f4) {
        break label356;
      }
      f1 = paramInt2;
      f2 = (int)Math.ceil(f2 * f4);
    }
    float f5;
    float f6;
    for (;;)
    {
      f3 = (this.rectX - this.bitmapX) / this.bitmapWidth;
      f4 = (this.rectY - this.bitmapY) / this.bitmapHeight;
      f5 = this.rectSizeX / this.bitmapWidth;
      f6 = this.rectSizeY / this.bitmapHeight;
      this.bitmapWidth = ((int)f2);
      this.bitmapHeight = ((int)f1);
      this.bitmapX = ((int)Math.ceil((paramInt1 - this.bitmapWidth) / 2 + AndroidUtilities.dp(14.0F)));
      this.bitmapY = ((int)Math.ceil((paramInt2 - this.bitmapHeight) / 2 + AndroidUtilities.dp(14.0F)));
      if ((this.rectX != -1.0F) || (this.rectY != -1.0F)) {
        break label481;
      }
      if (!this.freeformCrop) {
        break label376;
      }
      this.rectY = this.bitmapY;
      this.rectX = this.bitmapX;
      this.rectSizeX = this.bitmapWidth;
      this.rectSizeY = this.bitmapHeight;
      return;
      f2 = this.bitmapToEdit.getWidth();
      f1 = this.bitmapToEdit.getHeight();
      break;
      label356:
      f2 = paramInt1;
      f1 = (int)Math.ceil(f1 * f3);
    }
    label376:
    if (this.bitmapWidth > this.bitmapHeight)
    {
      this.rectY = this.bitmapY;
      this.rectX = ((paramInt1 - this.bitmapHeight) / 2 + AndroidUtilities.dp(14.0F));
      this.rectSizeX = this.bitmapHeight;
      this.rectSizeY = this.bitmapHeight;
      return;
    }
    this.rectX = this.bitmapX;
    this.rectY = ((paramInt2 - this.bitmapWidth) / 2 + AndroidUtilities.dp(14.0F));
    this.rectSizeX = this.bitmapWidth;
    this.rectSizeY = this.bitmapWidth;
    return;
    label481:
    this.rectX = (this.bitmapWidth * f3 + this.bitmapX);
    this.rectY = (this.bitmapHeight * f4 + this.bitmapY);
    this.rectSizeX = (this.bitmapWidth * f5);
    this.rectSizeY = (this.bitmapHeight * f6);
  }
  
  public boolean onTouch(MotionEvent paramMotionEvent)
  {
    if (paramMotionEvent == null)
    {
      this.draggingState = 0;
      return false;
    }
    float f5 = paramMotionEvent.getX();
    float f6 = paramMotionEvent.getY();
    int i = AndroidUtilities.dp(20.0F);
    if (paramMotionEvent.getAction() == 0) {
      if ((this.rectX - i < f5) && (this.rectX + i > f5) && (this.rectY - i < f6) && (this.rectY + i > f6))
      {
        this.draggingState = 1;
        if (this.draggingState != 0)
        {
          cancelAnimationRunnable();
          requestDisallowInterceptTouchEvent(true);
        }
        this.oldX = f5;
        this.oldY = f6;
      }
    }
    for (;;)
    {
      if (this.draggingState != 0)
      {
        return true;
        if ((this.rectX - i + this.rectSizeX < f5) && (this.rectX + i + this.rectSizeX > f5) && (this.rectY - i < f6) && (this.rectY + i > f6))
        {
          this.draggingState = 2;
          break;
        }
        if ((this.rectX - i < f5) && (this.rectX + i > f5) && (this.rectY - i + this.rectSizeY < f6) && (this.rectY + i + this.rectSizeY > f6))
        {
          this.draggingState = 3;
          break;
        }
        if ((this.rectX - i + this.rectSizeX < f5) && (this.rectX + i + this.rectSizeX > f5) && (this.rectY - i + this.rectSizeY < f6) && (this.rectY + i + this.rectSizeY > f6))
        {
          this.draggingState = 4;
          break;
        }
        if (this.freeformCrop)
        {
          if ((this.rectX + i < f5) && (this.rectX - i + this.rectSizeX > f5) && (this.rectY - i < f6) && (this.rectY + i > f6))
          {
            this.draggingState = 5;
            break;
          }
          if ((this.rectY + i < f6) && (this.rectY - i + this.rectSizeY > f6) && (this.rectX - i + this.rectSizeX < f5) && (this.rectX + i + this.rectSizeX > f5))
          {
            this.draggingState = 6;
            break;
          }
          if ((this.rectY + i < f6) && (this.rectY - i + this.rectSizeY > f6) && (this.rectX - i < f5) && (this.rectX + i > f5))
          {
            this.draggingState = 7;
            break;
          }
          if ((this.rectX + i >= f5) || (this.rectX - i + this.rectSizeX <= f5) || (this.rectY - i + this.rectSizeY >= f6) || (this.rectY + i + this.rectSizeY <= f6)) {
            break;
          }
          this.draggingState = 8;
          break;
        }
        this.draggingState = 0;
        break;
        if (paramMotionEvent.getAction() == 1)
        {
          if (this.draggingState != 0)
          {
            this.draggingState = 0;
            startAnimationRunnable();
            return true;
          }
        }
        else if ((paramMotionEvent.getAction() == 2) && (this.draggingState != 0))
        {
          float f1 = f5 - this.oldX;
          float f2 = f6 - this.oldY;
          float f7 = this.bitmapWidth * this.bitmapGlobalScale;
          float f3 = this.bitmapHeight * this.bitmapGlobalScale;
          float f9 = (getWidth() - AndroidUtilities.dp(28.0F) - f7) / 2.0F + this.bitmapGlobalX + AndroidUtilities.dp(14.0F);
          float f4 = (getHeight() - AndroidUtilities.dp(28.0F) - f3) / 2.0F + this.bitmapGlobalY + AndroidUtilities.dp(14.0F);
          float f10 = f9 + f7;
          float f8 = f4 + f3;
          f7 = AndroidUtilities.getPixelsInCM(0.9F, true);
          if ((this.draggingState == 1) || (this.draggingState == 5))
          {
            f3 = f1;
            if (this.draggingState != 5)
            {
              f3 = f1;
              if (this.rectSizeX - f1 < f7) {
                f3 = this.rectSizeX - f7;
              }
              f1 = f3;
              if (this.rectX + f3 < this.bitmapX) {
                f1 = this.bitmapX - this.rectX;
              }
              f3 = f1;
              if (this.rectX + f1 < f9)
              {
                this.bitmapGlobalX -= f9 - this.rectX - f1;
                this.delegate.needMoveImageTo(this.bitmapGlobalX, this.bitmapGlobalY, this.bitmapGlobalScale, false);
                f3 = f1;
              }
            }
            if (!this.freeformCrop)
            {
              f1 = f3;
              if (this.rectY + f3 < this.bitmapY) {
                f1 = this.bitmapY - this.rectY;
              }
              if (this.rectY + f1 < f4)
              {
                this.bitmapGlobalY -= f4 - this.rectY - f1;
                this.delegate.needMoveImageTo(this.bitmapGlobalX, this.bitmapGlobalY, this.bitmapGlobalScale, false);
              }
              this.rectX += f1;
              this.rectY += f1;
              this.rectSizeX -= f1;
              this.rectSizeY -= f1;
            }
          }
          label2157:
          do
          {
            for (;;)
            {
              this.oldX = f5;
              this.oldY = f6;
              invalidate();
              break;
              f1 = f2;
              if (this.rectSizeY - f2 < f7) {
                f1 = this.rectSizeY - f7;
              }
              f2 = f1;
              if (this.rectY + f1 < this.bitmapY) {
                f2 = this.bitmapY - this.rectY;
              }
              if (this.rectY + f2 < f4)
              {
                this.bitmapGlobalY -= f4 - this.rectY - f2;
                this.delegate.needMoveImageTo(this.bitmapGlobalX, this.bitmapGlobalY, this.bitmapGlobalScale, false);
              }
              if (this.draggingState != 5)
              {
                this.rectX += f3;
                this.rectSizeX -= f3;
              }
              this.rectY += f2;
              this.rectSizeY -= f2;
              continue;
              if ((this.draggingState == 2) || (this.draggingState == 6))
              {
                f3 = f1;
                if (this.rectSizeX + f1 < f7) {
                  f3 = -(this.rectSizeX - f7);
                }
                f1 = f3;
                if (this.rectX + this.rectSizeX + f3 > this.bitmapX + this.bitmapWidth) {
                  f1 = this.bitmapX + this.bitmapWidth - this.rectX - this.rectSizeX;
                }
                if (this.rectX + this.rectSizeX + f1 > f10)
                {
                  this.bitmapGlobalX -= f10 - this.rectX - this.rectSizeX - f1;
                  this.delegate.needMoveImageTo(this.bitmapGlobalX, this.bitmapGlobalY, this.bitmapGlobalScale, false);
                }
                if (!this.freeformCrop)
                {
                  f2 = f1;
                  if (this.rectY - f1 < this.bitmapY) {
                    f2 = this.rectY - this.bitmapY;
                  }
                  if (this.rectY - f2 < f4)
                  {
                    this.bitmapGlobalY -= f4 - this.rectY + f2;
                    this.delegate.needMoveImageTo(this.bitmapGlobalX, this.bitmapGlobalY, this.bitmapGlobalScale, false);
                  }
                  this.rectY -= f2;
                  this.rectSizeX += f2;
                  this.rectSizeY += f2;
                }
                else
                {
                  if (this.draggingState != 6)
                  {
                    f3 = f2;
                    if (this.rectSizeY - f2 < f7) {
                      f3 = this.rectSizeY - f7;
                    }
                    f2 = f3;
                    if (this.rectY + f3 < this.bitmapY) {
                      f2 = this.bitmapY - this.rectY;
                    }
                    if (this.rectY + f2 < f4)
                    {
                      this.bitmapGlobalY -= f4 - this.rectY - f2;
                      this.delegate.needMoveImageTo(this.bitmapGlobalX, this.bitmapGlobalY, this.bitmapGlobalScale, false);
                    }
                    this.rectY += f2;
                    this.rectSizeY -= f2;
                  }
                  this.rectSizeX += f1;
                }
              }
              else
              {
                if ((this.draggingState != 3) && (this.draggingState != 7)) {
                  break label2157;
                }
                f3 = f1;
                if (this.rectSizeX - f1 < f7) {
                  f3 = this.rectSizeX - f7;
                }
                f1 = f3;
                if (this.rectX + f3 < this.bitmapX) {
                  f1 = this.bitmapX - this.rectX;
                }
                if (this.rectX + f1 < f9)
                {
                  this.bitmapGlobalX -= f9 - this.rectX - f1;
                  this.delegate.needMoveImageTo(this.bitmapGlobalX, this.bitmapGlobalY, this.bitmapGlobalScale, false);
                }
                if (!this.freeformCrop)
                {
                  f2 = f1;
                  if (this.rectY + this.rectSizeX - f1 > this.bitmapY + this.bitmapHeight) {
                    f2 = this.rectY + this.rectSizeX - this.bitmapY - this.bitmapHeight;
                  }
                  if (this.rectY + this.rectSizeX - f2 > f8)
                  {
                    this.bitmapGlobalY -= f8 - this.rectY - this.rectSizeX + f2;
                    this.delegate.needMoveImageTo(this.bitmapGlobalX, this.bitmapGlobalY, this.bitmapGlobalScale, false);
                  }
                  this.rectX += f2;
                  this.rectSizeX -= f2;
                  this.rectSizeY -= f2;
                }
                else
                {
                  if (this.draggingState != 7)
                  {
                    f3 = f2;
                    if (this.rectY + this.rectSizeY + f2 > this.bitmapY + this.bitmapHeight) {
                      f3 = this.bitmapY + this.bitmapHeight - this.rectY - this.rectSizeY;
                    }
                    if (this.rectY + this.rectSizeY + f3 > f8)
                    {
                      this.bitmapGlobalY -= f8 - this.rectY - this.rectSizeY - f3;
                      this.delegate.needMoveImageTo(this.bitmapGlobalX, this.bitmapGlobalY, this.bitmapGlobalScale, false);
                    }
                    this.rectSizeY += f3;
                    if (this.rectSizeY < f7) {
                      this.rectSizeY = f7;
                    }
                  }
                  this.rectX += f1;
                  this.rectSizeX -= f1;
                }
              }
            }
          } while ((this.draggingState != 4) && (this.draggingState != 8));
          f3 = f1;
          if (this.draggingState != 8)
          {
            f4 = f1;
            if (this.rectX + this.rectSizeX + f1 > this.bitmapX + this.bitmapWidth) {
              f4 = this.bitmapX + this.bitmapWidth - this.rectX - this.rectSizeX;
            }
            f3 = f4;
            if (this.rectX + this.rectSizeX + f4 > f10)
            {
              this.bitmapGlobalX -= f10 - this.rectX - this.rectSizeX - f4;
              this.delegate.needMoveImageTo(this.bitmapGlobalX, this.bitmapGlobalY, this.bitmapGlobalScale, false);
              f3 = f4;
            }
          }
          if (!this.freeformCrop)
          {
            f1 = f3;
            if (this.rectY + this.rectSizeX + f3 > this.bitmapY + this.bitmapHeight) {
              f1 = this.bitmapY + this.bitmapHeight - this.rectY - this.rectSizeX;
            }
            if (this.rectY + this.rectSizeX + f1 > f8)
            {
              this.bitmapGlobalY -= f8 - this.rectY - this.rectSizeX - f1;
              this.delegate.needMoveImageTo(this.bitmapGlobalX, this.bitmapGlobalY, this.bitmapGlobalScale, false);
            }
            this.rectSizeX += f1;
          }
          for (this.rectSizeY += f1;; this.rectSizeY += f1)
          {
            if (this.rectSizeX < f7) {
              this.rectSizeX = f7;
            }
            if (this.rectSizeY >= f7) {
              break;
            }
            this.rectSizeY = f7;
            break;
            f1 = f2;
            if (this.rectY + this.rectSizeY + f2 > this.bitmapY + this.bitmapHeight) {
              f1 = this.bitmapY + this.bitmapHeight - this.rectY - this.rectSizeY;
            }
            if (this.rectY + this.rectSizeY + f1 > f8)
            {
              this.bitmapGlobalY -= f8 - this.rectY - this.rectSizeY - f1;
              this.delegate.needMoveImageTo(this.bitmapGlobalX, this.bitmapGlobalY, this.bitmapGlobalScale, false);
            }
            if (this.draggingState != 8) {
              this.rectSizeX += f3;
            }
          }
        }
      }
    }
    return false;
  }
  
  public void setAnimationProgress(float paramFloat)
  {
    if (this.animationStartValues != null)
    {
      if (paramFloat != 1.0F) {
        break label72;
      }
      this.rectX = this.animationEndValues.left;
      this.rectY = this.animationEndValues.top;
      this.rectSizeX = this.animationEndValues.right;
      this.rectSizeY = this.animationEndValues.bottom;
      this.animationStartValues = null;
      this.animationEndValues = null;
    }
    for (;;)
    {
      invalidate();
      return;
      label72:
      this.rectX = (this.animationStartValues.left + (this.animationEndValues.left - this.animationStartValues.left) * paramFloat);
      this.rectY = (this.animationStartValues.top + (this.animationEndValues.top - this.animationStartValues.top) * paramFloat);
      this.rectSizeX = (this.animationStartValues.right + (this.animationEndValues.right - this.animationStartValues.right) * paramFloat);
      this.rectSizeY = (this.animationStartValues.bottom + (this.animationEndValues.bottom - this.animationStartValues.bottom) * paramFloat);
    }
  }
  
  public void setBitmap(Bitmap paramBitmap, int paramInt, boolean paramBoolean)
  {
    this.bitmapToEdit = paramBitmap;
    this.rectSizeX = 600.0F;
    this.rectSizeY = 600.0F;
    this.draggingState = 0;
    this.oldX = 0.0F;
    this.oldY = 0.0F;
    this.bitmapWidth = 1;
    this.bitmapHeight = 1;
    this.rectX = -1.0F;
    this.rectY = -1.0F;
    this.freeformCrop = paramBoolean;
    this.orientation = paramInt;
    requestLayout();
  }
  
  public void setBitmapParams(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    this.bitmapGlobalScale = paramFloat1;
    this.bitmapGlobalX = paramFloat2;
    this.bitmapGlobalY = paramFloat3;
  }
  
  public void setDelegate(PhotoCropViewDelegate paramPhotoCropViewDelegate)
  {
    this.delegate = paramPhotoCropViewDelegate;
  }
  
  public void setOrientation(int paramInt)
  {
    this.orientation = paramInt;
    this.rectX = -1.0F;
    this.rectY = -1.0F;
    this.rectSizeX = 600.0F;
    this.rectSizeY = 600.0F;
    this.delegate.needMoveImageTo(0.0F, 0.0F, 1.0F, false);
    requestLayout();
  }
  
  public void startAnimationRunnable()
  {
    if (this.animationRunnable != null) {
      return;
    }
    this.animationRunnable = new Runnable()
    {
      public void run()
      {
        if (PhotoCropView.this.animationRunnable == this)
        {
          PhotoCropView.access$002(PhotoCropView.this, null);
          PhotoCropView.this.moveToFill(true);
        }
      }
    };
    AndroidUtilities.runOnUIThread(this.animationRunnable, 1500L);
  }
  
  public static abstract interface PhotoCropViewDelegate
  {
    public abstract Bitmap getBitmap();
    
    public abstract void needMoveImageTo(float paramFloat1, float paramFloat2, float paramFloat3, boolean paramBoolean);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/PhotoCropView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */