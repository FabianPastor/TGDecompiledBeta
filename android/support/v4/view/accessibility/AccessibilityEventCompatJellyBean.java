package android.support.v4.view.accessibility;

import android.view.accessibility.AccessibilityEvent;

class AccessibilityEventCompatJellyBean
{
  public static int getAction(AccessibilityEvent paramAccessibilityEvent)
  {
    return paramAccessibilityEvent.getAction();
  }
  
  public static int getMovementGranularity(AccessibilityEvent paramAccessibilityEvent)
  {
    return paramAccessibilityEvent.getMovementGranularity();
  }
  
  public static void setAction(AccessibilityEvent paramAccessibilityEvent, int paramInt)
  {
    paramAccessibilityEvent.setAction(paramInt);
  }
  
  public static void setMovementGranularity(AccessibilityEvent paramAccessibilityEvent, int paramInt)
  {
    paramAccessibilityEvent.setMovementGranularity(paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/view/accessibility/AccessibilityEventCompatJellyBean.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */