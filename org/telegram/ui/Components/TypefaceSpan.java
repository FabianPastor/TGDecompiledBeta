package org.telegram.ui.Components;

import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

public class TypefaceSpan
  extends MetricAffectingSpan
{
  private int color;
  private Typeface mTypeface;
  private int textSize;
  
  public TypefaceSpan(Typeface paramTypeface)
  {
    this.mTypeface = paramTypeface;
  }
  
  public TypefaceSpan(Typeface paramTypeface, int paramInt)
  {
    this.mTypeface = paramTypeface;
    this.textSize = paramInt;
  }
  
  public TypefaceSpan(Typeface paramTypeface, int paramInt1, int paramInt2)
  {
    this.mTypeface = paramTypeface;
    this.textSize = paramInt1;
    this.color = paramInt2;
  }
  
  public void updateDrawState(TextPaint paramTextPaint)
  {
    if (this.mTypeface != null) {
      paramTextPaint.setTypeface(this.mTypeface);
    }
    if (this.textSize != 0) {
      paramTextPaint.setTextSize(this.textSize);
    }
    if (this.color != 0) {
      paramTextPaint.setColor(this.color);
    }
    paramTextPaint.setFlags(paramTextPaint.getFlags() | 0x80);
  }
  
  public void updateMeasureState(TextPaint paramTextPaint)
  {
    if (this.mTypeface != null) {
      paramTextPaint.setTypeface(this.mTypeface);
    }
    if (this.textSize != 0) {
      paramTextPaint.setTextSize(this.textSize);
    }
    paramTextPaint.setFlags(paramTextPaint.getFlags() | 0x80);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/TypefaceSpan.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */