package org.telegram.ui.Components;

import android.text.TextPaint;
import org.telegram.messenger.AndroidUtilities;

public class URLSpanNoUnderlineBold
  extends URLSpanNoUnderline
{
  public URLSpanNoUnderlineBold(String paramString)
  {
    super(paramString);
  }
  
  public void updateDrawState(TextPaint paramTextPaint)
  {
    super.updateDrawState(paramTextPaint);
    paramTextPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    paramTextPaint.setUnderlineText(false);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/URLSpanNoUnderlineBold.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */