package org.telegram.ui.Components;

import android.text.TextPaint;
import android.view.View;
import org.telegram.ui.ActionBar.Theme;

public class URLSpanUserMention
  extends URLSpanNoUnderline
{
  private int currentType;
  
  public URLSpanUserMention(String paramString, int paramInt)
  {
    super(paramString);
    this.currentType = paramInt;
  }
  
  public void onClick(View paramView)
  {
    super.onClick(paramView);
  }
  
  public void updateDrawState(TextPaint paramTextPaint)
  {
    super.updateDrawState(paramTextPaint);
    if (this.currentType == 2) {
      paramTextPaint.setColor(-1);
    }
    for (;;)
    {
      paramTextPaint.setUnderlineText(false);
      return;
      if (this.currentType == 1) {
        paramTextPaint.setColor(Theme.getColor("chat_messageLinkOut"));
      } else {
        paramTextPaint.setColor(Theme.getColor("chat_messageLinkIn"));
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/URLSpanUserMention.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */