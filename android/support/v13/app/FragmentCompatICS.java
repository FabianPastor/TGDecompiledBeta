package android.support.v13.app;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.support.annotation.RequiresApi;

@TargetApi(14)
@RequiresApi(14)
class FragmentCompatICS
{
  public static void setMenuVisibility(Fragment paramFragment, boolean paramBoolean)
  {
    paramFragment.setMenuVisibility(paramBoolean);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v13/app/FragmentCompatICS.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */