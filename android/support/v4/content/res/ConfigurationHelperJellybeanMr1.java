package android.support.v4.content.res;

import android.annotation.TargetApi;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

@TargetApi(17)
@RequiresApi(17)
class ConfigurationHelperJellybeanMr1
{
  static int getDensityDpi(@NonNull Resources paramResources)
  {
    return paramResources.getConfiguration().densityDpi;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/content/res/ConfigurationHelperJellybeanMr1.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */