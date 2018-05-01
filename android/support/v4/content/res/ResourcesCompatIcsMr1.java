package android.support.v4.content.res;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.support.annotation.RequiresApi;

@TargetApi(15)
@RequiresApi(15)
class ResourcesCompatIcsMr1
{
  public static Drawable getDrawableForDensity(Resources paramResources, int paramInt1, int paramInt2)
    throws Resources.NotFoundException
  {
    return paramResources.getDrawableForDensity(paramInt1, paramInt2);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/content/res/ResourcesCompatIcsMr1.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */