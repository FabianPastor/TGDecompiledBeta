package org.telegram.ui.Components;

import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ActionBar.Theme;

public class URLSpanMono
  extends MetricAffectingSpan
{
  private int currentEnd;
  private CharSequence currentMessage;
  private int currentStart;
  private byte currentType;
  
  public URLSpanMono(CharSequence paramCharSequence, int paramInt1, int paramInt2, byte paramByte)
  {
    this.currentMessage = paramCharSequence;
    this.currentStart = paramInt1;
    this.currentEnd = paramInt2;
    this.currentType = ((byte)paramByte);
  }
  
  public void copyToClipboard()
  {
    AndroidUtilities.addToClipboard(this.currentMessage.subSequence(this.currentStart, this.currentEnd).toString());
  }
  
  public void updateDrawState(TextPaint paramTextPaint)
  {
    paramTextPaint.setTextSize(AndroidUtilities.dp(SharedConfig.fontSize - 1));
    paramTextPaint.setTypeface(Typeface.MONOSPACE);
    paramTextPaint.setUnderlineText(false);
    if (this.currentType == 2) {
      paramTextPaint.setColor(-1);
    }
    for (;;)
    {
      return;
      if (this.currentType == 1) {
        paramTextPaint.setColor(Theme.getColor("chat_messageTextOut"));
      } else {
        paramTextPaint.setColor(Theme.getColor("chat_messageTextIn"));
      }
    }
  }
  
  public void updateMeasureState(TextPaint paramTextPaint)
  {
    paramTextPaint.setTypeface(Typeface.MONOSPACE);
    paramTextPaint.setTextSize(AndroidUtilities.dp(SharedConfig.fontSize - 1));
    paramTextPaint.setFlags(paramTextPaint.getFlags() | 0x80);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/URLSpanMono.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */