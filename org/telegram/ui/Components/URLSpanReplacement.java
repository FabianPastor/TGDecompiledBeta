package org.telegram.ui.Components;

import android.net.Uri;
import android.text.style.URLSpan;
import android.view.View;
import org.telegram.messenger.browser.Browser;

public class URLSpanReplacement
  extends URLSpan
{
  public URLSpanReplacement(String paramString)
  {
    super(paramString);
  }
  
  public void onClick(View paramView)
  {
    Uri localUri = Uri.parse(getURL());
    Browser.openUrl(paramView.getContext(), localUri);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/URLSpanReplacement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */