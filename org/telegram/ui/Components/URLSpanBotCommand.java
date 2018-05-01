package org.telegram.ui.Components;

import android.text.TextPaint;
import org.telegram.ui.ActionBar.Theme;

public class URLSpanBotCommand
  extends URLSpanNoUnderline
{
  public static boolean enabled = true;
  public int currentType;
  
  public URLSpanBotCommand(String paramString, int paramInt)
  {
    super(paramString);
    this.currentType = paramInt;
  }
  
  public void updateDrawState(TextPaint paramTextPaint)
  {
    super.updateDrawState(paramTextPaint);
    if (this.currentType == 2)
    {
      paramTextPaint.setColor(-1);
      paramTextPaint.setUnderlineText(false);
      return;
    }
    if (this.currentType == 1)
    {
      if (enabled) {}
      for (str = "chat_messageLinkOut";; str = "chat_messageTextOut")
      {
        paramTextPaint.setColor(Theme.getColor(str));
        break;
      }
    }
    if (enabled) {}
    for (String str = "chat_messageLinkIn";; str = "chat_messageTextIn")
    {
      paramTextPaint.setColor(Theme.getColor(str));
      break;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/URLSpanBotCommand.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */