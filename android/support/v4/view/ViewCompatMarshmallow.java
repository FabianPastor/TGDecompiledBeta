package android.support.v4.view;

import android.view.View;

class ViewCompatMarshmallow
{
  public static int getScrollIndicators(View paramView)
  {
    return paramView.getScrollIndicators();
  }
  
  static void offsetLeftAndRight(View paramView, int paramInt)
  {
    paramView.offsetLeftAndRight(paramInt);
  }
  
  static void offsetTopAndBottom(View paramView, int paramInt)
  {
    paramView.offsetTopAndBottom(paramInt);
  }
  
  public static void setScrollIndicators(View paramView, int paramInt)
  {
    paramView.setScrollIndicators(paramInt);
  }
  
  public static void setScrollIndicators(View paramView, int paramInt1, int paramInt2)
  {
    paramView.setScrollIndicators(paramInt1, paramInt2);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/view/ViewCompatMarshmallow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */