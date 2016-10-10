package android.support.v4.view.accessibility;

import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;

class AccessibilityNodeInfoCompatApi22
{
  public static Object getTraversalAfter(Object paramObject)
  {
    return ((AccessibilityNodeInfo)paramObject).getTraversalAfter();
  }
  
  public static Object getTraversalBefore(Object paramObject)
  {
    return ((AccessibilityNodeInfo)paramObject).getTraversalBefore();
  }
  
  public static void setTraversalAfter(Object paramObject, View paramView)
  {
    ((AccessibilityNodeInfo)paramObject).setTraversalAfter(paramView);
  }
  
  public static void setTraversalAfter(Object paramObject, View paramView, int paramInt)
  {
    ((AccessibilityNodeInfo)paramObject).setTraversalAfter(paramView, paramInt);
  }
  
  public static void setTraversalBefore(Object paramObject, View paramView)
  {
    ((AccessibilityNodeInfo)paramObject).setTraversalBefore(paramView);
  }
  
  public static void setTraversalBefore(Object paramObject, View paramView, int paramInt)
  {
    ((AccessibilityNodeInfo)paramObject).setTraversalBefore(paramView, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/view/accessibility/AccessibilityNodeInfoCompatApi22.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */