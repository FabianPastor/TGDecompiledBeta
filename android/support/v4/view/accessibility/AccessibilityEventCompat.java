package android.support.v4.view.accessibility;

import android.os.Build.VERSION;
import android.view.accessibility.AccessibilityEvent;

public final class AccessibilityEventCompat
{
  public static final int CONTENT_CHANGE_TYPE_CONTENT_DESCRIPTION = 4;
  public static final int CONTENT_CHANGE_TYPE_SUBTREE = 1;
  public static final int CONTENT_CHANGE_TYPE_TEXT = 2;
  public static final int CONTENT_CHANGE_TYPE_UNDEFINED = 0;
  private static final AccessibilityEventVersionImpl IMPL = new AccessibilityEventStubImpl();
  public static final int TYPES_ALL_MASK = -1;
  public static final int TYPE_ANNOUNCEMENT = 16384;
  public static final int TYPE_ASSIST_READING_CONTEXT = 16777216;
  public static final int TYPE_GESTURE_DETECTION_END = 524288;
  public static final int TYPE_GESTURE_DETECTION_START = 262144;
  public static final int TYPE_TOUCH_EXPLORATION_GESTURE_END = 1024;
  public static final int TYPE_TOUCH_EXPLORATION_GESTURE_START = 512;
  public static final int TYPE_TOUCH_INTERACTION_END = 2097152;
  public static final int TYPE_TOUCH_INTERACTION_START = 1048576;
  public static final int TYPE_VIEW_ACCESSIBILITY_FOCUSED = 32768;
  public static final int TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED = 65536;
  public static final int TYPE_VIEW_CONTEXT_CLICKED = 8388608;
  public static final int TYPE_VIEW_HOVER_ENTER = 128;
  public static final int TYPE_VIEW_HOVER_EXIT = 256;
  public static final int TYPE_VIEW_SCROLLED = 4096;
  public static final int TYPE_VIEW_TEXT_SELECTION_CHANGED = 8192;
  public static final int TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY = 131072;
  public static final int TYPE_WINDOWS_CHANGED = 4194304;
  public static final int TYPE_WINDOW_CONTENT_CHANGED = 2048;
  
  static
  {
    if (Build.VERSION.SDK_INT >= 19)
    {
      IMPL = new AccessibilityEventKitKatImpl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 16)
    {
      IMPL = new AccessibilityEventJellyBeanImpl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 14)
    {
      IMPL = new AccessibilityEventIcsImpl();
      return;
    }
  }
  
  public static void appendRecord(AccessibilityEvent paramAccessibilityEvent, AccessibilityRecordCompat paramAccessibilityRecordCompat)
  {
    IMPL.appendRecord(paramAccessibilityEvent, paramAccessibilityRecordCompat.getImpl());
  }
  
  public static AccessibilityRecordCompat asRecord(AccessibilityEvent paramAccessibilityEvent)
  {
    return new AccessibilityRecordCompat(paramAccessibilityEvent);
  }
  
  public static int getContentChangeTypes(AccessibilityEvent paramAccessibilityEvent)
  {
    return IMPL.getContentChangeTypes(paramAccessibilityEvent);
  }
  
  public static AccessibilityRecordCompat getRecord(AccessibilityEvent paramAccessibilityEvent, int paramInt)
  {
    return new AccessibilityRecordCompat(IMPL.getRecord(paramAccessibilityEvent, paramInt));
  }
  
  public static int getRecordCount(AccessibilityEvent paramAccessibilityEvent)
  {
    return IMPL.getRecordCount(paramAccessibilityEvent);
  }
  
  public static void setContentChangeTypes(AccessibilityEvent paramAccessibilityEvent, int paramInt)
  {
    IMPL.setContentChangeTypes(paramAccessibilityEvent, paramInt);
  }
  
  public int getAction(AccessibilityEvent paramAccessibilityEvent)
  {
    return IMPL.getAction(paramAccessibilityEvent);
  }
  
  public int getMovementGranularity(AccessibilityEvent paramAccessibilityEvent)
  {
    return IMPL.getMovementGranularity(paramAccessibilityEvent);
  }
  
  public void setAction(AccessibilityEvent paramAccessibilityEvent, int paramInt)
  {
    IMPL.setAction(paramAccessibilityEvent, paramInt);
  }
  
  public void setMovementGranularity(AccessibilityEvent paramAccessibilityEvent, int paramInt)
  {
    IMPL.setMovementGranularity(paramAccessibilityEvent, paramInt);
  }
  
  static class AccessibilityEventIcsImpl
    extends AccessibilityEventCompat.AccessibilityEventStubImpl
  {
    public void appendRecord(AccessibilityEvent paramAccessibilityEvent, Object paramObject)
    {
      AccessibilityEventCompatIcs.appendRecord(paramAccessibilityEvent, paramObject);
    }
    
    public Object getRecord(AccessibilityEvent paramAccessibilityEvent, int paramInt)
    {
      return AccessibilityEventCompatIcs.getRecord(paramAccessibilityEvent, paramInt);
    }
    
    public int getRecordCount(AccessibilityEvent paramAccessibilityEvent)
    {
      return AccessibilityEventCompatIcs.getRecordCount(paramAccessibilityEvent);
    }
  }
  
  static class AccessibilityEventJellyBeanImpl
    extends AccessibilityEventCompat.AccessibilityEventIcsImpl
  {
    public int getAction(AccessibilityEvent paramAccessibilityEvent)
    {
      return AccessibilityEventCompatJellyBean.getAction(paramAccessibilityEvent);
    }
    
    public int getMovementGranularity(AccessibilityEvent paramAccessibilityEvent)
    {
      return AccessibilityEventCompatJellyBean.getMovementGranularity(paramAccessibilityEvent);
    }
    
    public void setAction(AccessibilityEvent paramAccessibilityEvent, int paramInt)
    {
      AccessibilityEventCompatJellyBean.setAction(paramAccessibilityEvent, paramInt);
    }
    
    public void setMovementGranularity(AccessibilityEvent paramAccessibilityEvent, int paramInt)
    {
      AccessibilityEventCompatJellyBean.setMovementGranularity(paramAccessibilityEvent, paramInt);
    }
  }
  
  static class AccessibilityEventKitKatImpl
    extends AccessibilityEventCompat.AccessibilityEventJellyBeanImpl
  {
    public int getContentChangeTypes(AccessibilityEvent paramAccessibilityEvent)
    {
      return AccessibilityEventCompatKitKat.getContentChangeTypes(paramAccessibilityEvent);
    }
    
    public void setContentChangeTypes(AccessibilityEvent paramAccessibilityEvent, int paramInt)
    {
      AccessibilityEventCompatKitKat.setContentChangeTypes(paramAccessibilityEvent, paramInt);
    }
  }
  
  static class AccessibilityEventStubImpl
    implements AccessibilityEventCompat.AccessibilityEventVersionImpl
  {
    public void appendRecord(AccessibilityEvent paramAccessibilityEvent, Object paramObject) {}
    
    public int getAction(AccessibilityEvent paramAccessibilityEvent)
    {
      return 0;
    }
    
    public int getContentChangeTypes(AccessibilityEvent paramAccessibilityEvent)
    {
      return 0;
    }
    
    public int getMovementGranularity(AccessibilityEvent paramAccessibilityEvent)
    {
      return 0;
    }
    
    public Object getRecord(AccessibilityEvent paramAccessibilityEvent, int paramInt)
    {
      return null;
    }
    
    public int getRecordCount(AccessibilityEvent paramAccessibilityEvent)
    {
      return 0;
    }
    
    public void setAction(AccessibilityEvent paramAccessibilityEvent, int paramInt) {}
    
    public void setContentChangeTypes(AccessibilityEvent paramAccessibilityEvent, int paramInt) {}
    
    public void setMovementGranularity(AccessibilityEvent paramAccessibilityEvent, int paramInt) {}
  }
  
  static abstract interface AccessibilityEventVersionImpl
  {
    public abstract void appendRecord(AccessibilityEvent paramAccessibilityEvent, Object paramObject);
    
    public abstract int getAction(AccessibilityEvent paramAccessibilityEvent);
    
    public abstract int getContentChangeTypes(AccessibilityEvent paramAccessibilityEvent);
    
    public abstract int getMovementGranularity(AccessibilityEvent paramAccessibilityEvent);
    
    public abstract Object getRecord(AccessibilityEvent paramAccessibilityEvent, int paramInt);
    
    public abstract int getRecordCount(AccessibilityEvent paramAccessibilityEvent);
    
    public abstract void setAction(AccessibilityEvent paramAccessibilityEvent, int paramInt);
    
    public abstract void setContentChangeTypes(AccessibilityEvent paramAccessibilityEvent, int paramInt);
    
    public abstract void setMovementGranularity(AccessibilityEvent paramAccessibilityEvent, int paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/view/accessibility/AccessibilityEventCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */