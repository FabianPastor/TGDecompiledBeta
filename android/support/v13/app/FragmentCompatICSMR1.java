package android.support.v13.app;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.support.annotation.RequiresApi;

@TargetApi(15)
@RequiresApi(15)
class FragmentCompatICSMR1
{
  public static void setUserVisibleHint(Fragment paramFragment, boolean paramBoolean)
  {
    if (paramFragment.getFragmentManager() != null) {
      paramFragment.setUserVisibleHint(paramBoolean);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v13/app/FragmentCompatICSMR1.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */