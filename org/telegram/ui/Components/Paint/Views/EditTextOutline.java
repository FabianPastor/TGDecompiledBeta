package org.telegram.ui.Components.Paint.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.widget.EditText;

public class EditTextOutline
  extends EditText
{
  private Bitmap mCache;
  private final Canvas mCanvas = new Canvas();
  private final TextPaint mPaint = new TextPaint();
  private int mStrokeColor = 0;
  private float mStrokeWidth;
  private boolean mUpdateCachedBitmap = true;
  
  public EditTextOutline(Context paramContext)
  {
    super(paramContext);
    this.mPaint.setAntiAlias(true);
    this.mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    int i;
    int j;
    int k;
    int m;
    Object localObject;
    if ((this.mCache != null) && (this.mStrokeColor != 0)) {
      if (this.mUpdateCachedBitmap)
      {
        i = getMeasuredWidth();
        j = getPaddingLeft();
        k = getPaddingRight();
        m = getMeasuredHeight();
        localObject = getText().toString();
        this.mCanvas.setBitmap(this.mCache);
        this.mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        if (this.mStrokeWidth <= 0.0F) {
          break label259;
        }
      }
    }
    label259:
    for (float f = this.mStrokeWidth;; f = (float)Math.ceil(getTextSize() / 11.5F))
    {
      this.mPaint.setStrokeWidth(f);
      this.mPaint.setColor(this.mStrokeColor);
      this.mPaint.setTextSize(getTextSize());
      this.mPaint.setTypeface(getTypeface());
      this.mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
      localObject = new StaticLayout((CharSequence)localObject, this.mPaint, i - j - k, Layout.Alignment.ALIGN_CENTER, 1.0F, 0.0F, true);
      this.mCanvas.save();
      f = (m - getPaddingTop() - getPaddingBottom() - ((StaticLayout)localObject).getHeight()) / 2.0F;
      this.mCanvas.translate(getPaddingLeft(), getPaddingTop() + f);
      ((StaticLayout)localObject).draw(this.mCanvas);
      this.mCanvas.restore();
      this.mUpdateCachedBitmap = false;
      paramCanvas.drawBitmap(this.mCache, 0.0F, 0.0F, this.mPaint);
      super.onDraw(paramCanvas);
      return;
    }
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    if ((paramInt1 > 0) && (paramInt2 > 0))
    {
      this.mUpdateCachedBitmap = true;
      this.mCache = Bitmap.createBitmap(paramInt1, paramInt2, Bitmap.Config.ARGB_8888);
      return;
    }
    this.mCache = null;
  }
  
  protected void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
  {
    super.onTextChanged(paramCharSequence, paramInt1, paramInt2, paramInt3);
    this.mUpdateCachedBitmap = true;
  }
  
  public void setStrokeColor(int paramInt)
  {
    this.mStrokeColor = paramInt;
    this.mUpdateCachedBitmap = true;
    invalidate();
  }
  
  public void setStrokeWidth(float paramFloat)
  {
    this.mStrokeWidth = paramFloat;
    this.mUpdateCachedBitmap = true;
    invalidate();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/Paint/Views/EditTextOutline.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */