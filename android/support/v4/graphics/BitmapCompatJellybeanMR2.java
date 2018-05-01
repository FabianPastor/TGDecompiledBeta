package android.support.v4.graphics;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.support.annotation.RequiresApi;

@TargetApi(18)
@RequiresApi(18)
class BitmapCompatJellybeanMR2
{
  public static boolean hasMipMap(Bitmap paramBitmap)
  {
    return paramBitmap.hasMipMap();
  }
  
  public static void setHasMipMap(Bitmap paramBitmap, boolean paramBoolean)
  {
    paramBitmap.setHasMipMap(paramBoolean);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/graphics/BitmapCompatJellybeanMR2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */