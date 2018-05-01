package android.support.v13.app;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.support.annotation.RequiresApi;

@TargetApi(23)
@RequiresApi(23)
class FragmentCompat23
{
  public static void requestPermissions(Fragment paramFragment, String[] paramArrayOfString, int paramInt)
  {
    paramFragment.requestPermissions(paramArrayOfString, paramInt);
  }
  
  public static boolean shouldShowRequestPermissionRationale(Fragment paramFragment, String paramString)
  {
    return paramFragment.shouldShowRequestPermissionRationale(paramString);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v13/app/FragmentCompat23.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */