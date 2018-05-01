package android.support.v4.view;

import android.annotation.TargetApi;
import android.support.annotation.RequiresApi;
import android.view.ViewGroup;

@TargetApi(18)
@RequiresApi(18)
class ViewGroupCompatJellybeanMR2
{
  public static int getLayoutMode(ViewGroup paramViewGroup)
  {
    return paramViewGroup.getLayoutMode();
  }
  
  public static void setLayoutMode(ViewGroup paramViewGroup, int paramInt)
  {
    paramViewGroup.setLayoutMode(paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/view/ViewGroupCompatJellybeanMR2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */