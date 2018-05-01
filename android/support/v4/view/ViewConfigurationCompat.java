package android.support.v4.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.Build.VERSION;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewConfiguration;
import java.lang.reflect.Method;

public final class ViewConfigurationCompat
{
  private static Method sGetScaledScrollFactorMethod;
  
  static
  {
    if (Build.VERSION.SDK_INT == 25) {}
    try
    {
      sGetScaledScrollFactorMethod = ViewConfiguration.class.getDeclaredMethod("getScaledScrollFactor", new Class[0]);
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        Log.i("ViewConfigCompat", "Could not find method getScaledScrollFactor() on ViewConfiguration");
      }
    }
  }
  
  private static float getLegacyScrollFactor(ViewConfiguration paramViewConfiguration, Context paramContext)
  {
    if ((Build.VERSION.SDK_INT >= 25) && (sGetScaledScrollFactorMethod != null)) {}
    for (;;)
    {
      float f;
      try
      {
        int i = ((Integer)sGetScaledScrollFactorMethod.invoke(paramViewConfiguration, new Object[0])).intValue();
        f = i;
        return f;
      }
      catch (Exception paramViewConfiguration)
      {
        Log.i("ViewConfigCompat", "Could not find method getScaledScrollFactor() on ViewConfiguration");
      }
      paramViewConfiguration = new TypedValue();
      if (paramContext.getTheme().resolveAttribute(16842829, paramViewConfiguration, true)) {
        f = paramViewConfiguration.getDimension(paramContext.getResources().getDisplayMetrics());
      } else {
        f = 0.0F;
      }
    }
  }
  
  public static float getScaledHorizontalScrollFactor(ViewConfiguration paramViewConfiguration, Context paramContext)
  {
    if (Build.VERSION.SDK_INT >= 26) {}
    for (float f = paramViewConfiguration.getScaledHorizontalScrollFactor();; f = getLegacyScrollFactor(paramViewConfiguration, paramContext)) {
      return f;
    }
  }
  
  public static float getScaledVerticalScrollFactor(ViewConfiguration paramViewConfiguration, Context paramContext)
  {
    if (Build.VERSION.SDK_INT >= 26) {}
    for (float f = paramViewConfiguration.getScaledVerticalScrollFactor();; f = getLegacyScrollFactor(paramViewConfiguration, paramContext)) {
      return f;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/view/ViewConfigurationCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */