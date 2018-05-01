package android.support.v4.text;

import android.os.Build.VERSION;
import android.text.TextUtils;
import java.util.Locale;

public final class TextUtilsCompat
{
  private static final Locale ROOT = new Locale("", "");
  
  private static int getLayoutDirectionFromFirstChar(Locale paramLocale)
  {
    int i = 0;
    switch (Character.getDirectionality(paramLocale.getDisplayName(paramLocale).charAt(0)))
    {
    }
    for (;;)
    {
      return i;
      i = 1;
    }
  }
  
  public static int getLayoutDirectionFromLocale(Locale paramLocale)
  {
    int i;
    if (Build.VERSION.SDK_INT >= 17) {
      i = TextUtils.getLayoutDirectionFromLocale(paramLocale);
    }
    for (;;)
    {
      return i;
      if ((paramLocale != null) && (!paramLocale.equals(ROOT)))
      {
        String str = ICUCompat.maximizeAndGetScript(paramLocale);
        if (str == null)
        {
          i = getLayoutDirectionFromFirstChar(paramLocale);
          continue;
        }
        if ((str.equalsIgnoreCase("Arab")) || (str.equalsIgnoreCase("Hebr")))
        {
          i = 1;
          continue;
        }
      }
      i = 0;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/text/TextUtilsCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */