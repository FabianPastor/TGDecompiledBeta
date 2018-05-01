package org.telegram.ui.Components;

import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;

public class ColorSpanUnderline
  extends ForegroundColorSpan
{
  public ColorSpanUnderline(int paramInt)
  {
    super(paramInt);
  }
  
  public void updateDrawState(TextPaint paramTextPaint)
  {
    super.updateDrawState(paramTextPaint);
    paramTextPaint.setUnderlineText(true);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/ColorSpanUnderline.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */