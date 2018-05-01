package android.support.v4.view.accessibility;

import android.annotation.TargetApi;
import android.support.annotation.RequiresApi;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;

@TargetApi(23)
@RequiresApi(23)
class AccessibilityNodeInfoCompatApi23
{
  public static Object getActionContextClick()
  {
    return AccessibilityNodeInfo.AccessibilityAction.ACTION_CONTEXT_CLICK;
  }
  
  public static Object getActionScrollDown()
  {
    return AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_DOWN;
  }
  
  public static Object getActionScrollLeft()
  {
    return AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_LEFT;
  }
  
  public static Object getActionScrollRight()
  {
    return AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_RIGHT;
  }
  
  public static Object getActionScrollToPosition()
  {
    return AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_TO_POSITION;
  }
  
  public static Object getActionScrollUp()
  {
    return AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_UP;
  }
  
  public static Object getActionShowOnScreen()
  {
    return AccessibilityNodeInfo.AccessibilityAction.ACTION_SHOW_ON_SCREEN;
  }
  
  public static boolean isContextClickable(Object paramObject)
  {
    return ((AccessibilityNodeInfo)paramObject).isContextClickable();
  }
  
  public static void setContextClickable(Object paramObject, boolean paramBoolean)
  {
    ((AccessibilityNodeInfo)paramObject).setContextClickable(paramBoolean);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/view/accessibility/AccessibilityNodeInfoCompatApi23.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */