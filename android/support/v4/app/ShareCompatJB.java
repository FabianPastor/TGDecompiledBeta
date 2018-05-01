package android.support.v4.app;

import android.annotation.TargetApi;
import android.support.annotation.RequiresApi;
import android.text.Html;

@TargetApi(16)
@RequiresApi(16)
class ShareCompatJB
{
  public static String escapeHtml(CharSequence paramCharSequence)
  {
    return Html.escapeHtml(paramCharSequence);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/app/ShareCompatJB.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */