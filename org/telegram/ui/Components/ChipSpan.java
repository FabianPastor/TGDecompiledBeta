package org.telegram.ui.Components;

import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;
import org.telegram.messenger.AndroidUtilities;

public class ChipSpan
  extends ImageSpan
{
  public int uid;
  
  public ChipSpan(Drawable paramDrawable, int paramInt)
  {
    super(paramDrawable, paramInt);
  }
  
  public int getSize(Paint paramPaint, CharSequence paramCharSequence, int paramInt1, int paramInt2, Paint.FontMetricsInt paramFontMetricsInt)
  {
    Paint.FontMetricsInt localFontMetricsInt = paramFontMetricsInt;
    if (paramFontMetricsInt == null) {
      localFontMetricsInt = new Paint.FontMetricsInt();
    }
    paramInt1 = super.getSize(paramPaint, paramCharSequence, paramInt1, paramInt2, localFontMetricsInt);
    paramInt2 = AndroidUtilities.dp(6.0F);
    int i = (localFontMetricsInt.bottom - localFontMetricsInt.top) / 2;
    localFontMetricsInt.top = (-i - paramInt2);
    localFontMetricsInt.bottom = (i - paramInt2);
    localFontMetricsInt.ascent = (-i - paramInt2);
    localFontMetricsInt.leading = 0;
    localFontMetricsInt.descent = (i - paramInt2);
    return paramInt1;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/ChipSpan.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */