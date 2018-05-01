package org.telegram.ui.Components;

import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

public class TextPaintSpan
  extends MetricAffectingSpan
{
  private int color;
  private TextPaint textPaint;
  private int textSize;
  
  public TextPaintSpan(TextPaint paramTextPaint)
  {
    this.textPaint = paramTextPaint;
  }
  
  public void updateDrawState(TextPaint paramTextPaint)
  {
    paramTextPaint.setColor(this.textPaint.getColor());
    paramTextPaint.setTypeface(this.textPaint.getTypeface());
    paramTextPaint.setFlags(this.textPaint.getFlags());
  }
  
  public void updateMeasureState(TextPaint paramTextPaint)
  {
    paramTextPaint.setColor(this.textPaint.getColor());
    paramTextPaint.setTypeface(this.textPaint.getTypeface());
    paramTextPaint.setFlags(this.textPaint.getFlags());
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/TextPaintSpan.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */