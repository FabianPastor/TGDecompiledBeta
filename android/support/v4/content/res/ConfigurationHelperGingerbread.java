package android.support.v4.content.res;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;

@TargetApi(9)
@RequiresApi(9)
class ConfigurationHelperGingerbread
{
  static int getDensityDpi(@NonNull Resources paramResources)
  {
    return paramResources.getDisplayMetrics().densityDpi;
  }
  
  static int getScreenHeightDp(@NonNull Resources paramResources)
  {
    paramResources = paramResources.getDisplayMetrics();
    return (int)(paramResources.heightPixels / paramResources.density);
  }
  
  static int getScreenWidthDp(@NonNull Resources paramResources)
  {
    paramResources = paramResources.getDisplayMetrics();
    return (int)(paramResources.widthPixels / paramResources.density);
  }
  
  static int getSmallestScreenWidthDp(@NonNull Resources paramResources)
  {
    return Math.min(getScreenWidthDp(paramResources), getScreenHeightDp(paramResources));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/content/res/ConfigurationHelperGingerbread.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */