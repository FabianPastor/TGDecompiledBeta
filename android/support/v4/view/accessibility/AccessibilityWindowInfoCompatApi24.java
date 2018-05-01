package android.support.v4.view.accessibility;

import android.annotation.TargetApi;
import android.support.annotation.RequiresApi;
import android.view.accessibility.AccessibilityWindowInfo;

@TargetApi(24)
@RequiresApi(24)
class AccessibilityWindowInfoCompatApi24
{
  public static Object getAnchor(Object paramObject)
  {
    return ((AccessibilityWindowInfo)paramObject).getAnchor();
  }
  
  public static CharSequence getTitle(Object paramObject)
  {
    return ((AccessibilityWindowInfo)paramObject).getTitle();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/view/accessibility/AccessibilityWindowInfoCompatApi24.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */