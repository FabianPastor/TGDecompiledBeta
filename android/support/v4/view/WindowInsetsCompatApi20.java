package android.support.v4.view;

import android.annotation.TargetApi;
import android.support.annotation.RequiresApi;
import android.view.WindowInsets;

@TargetApi(20)
@RequiresApi(20)
class WindowInsetsCompatApi20
{
  public static Object consumeSystemWindowInsets(Object paramObject)
  {
    return ((WindowInsets)paramObject).consumeSystemWindowInsets();
  }
  
  public static Object getSourceWindowInsets(Object paramObject)
  {
    return new WindowInsets((WindowInsets)paramObject);
  }
  
  public static int getSystemWindowInsetBottom(Object paramObject)
  {
    return ((WindowInsets)paramObject).getSystemWindowInsetBottom();
  }
  
  public static int getSystemWindowInsetLeft(Object paramObject)
  {
    return ((WindowInsets)paramObject).getSystemWindowInsetLeft();
  }
  
  public static int getSystemWindowInsetRight(Object paramObject)
  {
    return ((WindowInsets)paramObject).getSystemWindowInsetRight();
  }
  
  public static int getSystemWindowInsetTop(Object paramObject)
  {
    return ((WindowInsets)paramObject).getSystemWindowInsetTop();
  }
  
  public static boolean hasInsets(Object paramObject)
  {
    return ((WindowInsets)paramObject).hasInsets();
  }
  
  public static boolean hasSystemWindowInsets(Object paramObject)
  {
    return ((WindowInsets)paramObject).hasSystemWindowInsets();
  }
  
  public static boolean isRound(Object paramObject)
  {
    return ((WindowInsets)paramObject).isRound();
  }
  
  public static Object replaceSystemWindowInsets(Object paramObject, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return ((WindowInsets)paramObject).replaceSystemWindowInsets(paramInt1, paramInt2, paramInt3, paramInt4);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/view/WindowInsetsCompatApi20.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */