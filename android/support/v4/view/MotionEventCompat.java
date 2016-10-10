package android.support.v4.view;

import android.os.Build.VERSION;
import android.view.MotionEvent;

public final class MotionEventCompat
{
  public static final int ACTION_HOVER_ENTER = 9;
  public static final int ACTION_HOVER_EXIT = 10;
  public static final int ACTION_HOVER_MOVE = 7;
  public static final int ACTION_MASK = 255;
  public static final int ACTION_POINTER_DOWN = 5;
  public static final int ACTION_POINTER_INDEX_MASK = 65280;
  public static final int ACTION_POINTER_INDEX_SHIFT = 8;
  public static final int ACTION_POINTER_UP = 6;
  public static final int ACTION_SCROLL = 8;
  public static final int AXIS_BRAKE = 23;
  public static final int AXIS_DISTANCE = 24;
  public static final int AXIS_GAS = 22;
  public static final int AXIS_GENERIC_1 = 32;
  public static final int AXIS_GENERIC_10 = 41;
  public static final int AXIS_GENERIC_11 = 42;
  public static final int AXIS_GENERIC_12 = 43;
  public static final int AXIS_GENERIC_13 = 44;
  public static final int AXIS_GENERIC_14 = 45;
  public static final int AXIS_GENERIC_15 = 46;
  public static final int AXIS_GENERIC_16 = 47;
  public static final int AXIS_GENERIC_2 = 33;
  public static final int AXIS_GENERIC_3 = 34;
  public static final int AXIS_GENERIC_4 = 35;
  public static final int AXIS_GENERIC_5 = 36;
  public static final int AXIS_GENERIC_6 = 37;
  public static final int AXIS_GENERIC_7 = 38;
  public static final int AXIS_GENERIC_8 = 39;
  public static final int AXIS_GENERIC_9 = 40;
  public static final int AXIS_HAT_X = 15;
  public static final int AXIS_HAT_Y = 16;
  public static final int AXIS_HSCROLL = 10;
  public static final int AXIS_LTRIGGER = 17;
  public static final int AXIS_ORIENTATION = 8;
  public static final int AXIS_PRESSURE = 2;
  public static final int AXIS_RELATIVE_X = 27;
  public static final int AXIS_RELATIVE_Y = 28;
  public static final int AXIS_RTRIGGER = 18;
  public static final int AXIS_RUDDER = 20;
  public static final int AXIS_RX = 12;
  public static final int AXIS_RY = 13;
  public static final int AXIS_RZ = 14;
  public static final int AXIS_SIZE = 3;
  public static final int AXIS_THROTTLE = 19;
  public static final int AXIS_TILT = 25;
  public static final int AXIS_TOOL_MAJOR = 6;
  public static final int AXIS_TOOL_MINOR = 7;
  public static final int AXIS_TOUCH_MAJOR = 4;
  public static final int AXIS_TOUCH_MINOR = 5;
  public static final int AXIS_VSCROLL = 9;
  public static final int AXIS_WHEEL = 21;
  public static final int AXIS_X = 0;
  public static final int AXIS_Y = 1;
  public static final int AXIS_Z = 11;
  public static final int BUTTON_PRIMARY = 1;
  static final MotionEventVersionImpl IMPL = new BaseMotionEventVersionImpl();
  
  static
  {
    if (Build.VERSION.SDK_INT >= 14)
    {
      IMPL = new ICSMotionEventVersionImpl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 12)
    {
      IMPL = new HoneycombMr1MotionEventVersionImpl();
      return;
    }
  }
  
  @Deprecated
  public static int findPointerIndex(MotionEvent paramMotionEvent, int paramInt)
  {
    return paramMotionEvent.findPointerIndex(paramInt);
  }
  
  public static int getActionIndex(MotionEvent paramMotionEvent)
  {
    return (paramMotionEvent.getAction() & 0xFF00) >> 8;
  }
  
  public static int getActionMasked(MotionEvent paramMotionEvent)
  {
    return paramMotionEvent.getAction() & 0xFF;
  }
  
  public static float getAxisValue(MotionEvent paramMotionEvent, int paramInt)
  {
    return IMPL.getAxisValue(paramMotionEvent, paramInt);
  }
  
  public static float getAxisValue(MotionEvent paramMotionEvent, int paramInt1, int paramInt2)
  {
    return IMPL.getAxisValue(paramMotionEvent, paramInt1, paramInt2);
  }
  
  public static int getButtonState(MotionEvent paramMotionEvent)
  {
    return IMPL.getButtonState(paramMotionEvent);
  }
  
  @Deprecated
  public static int getPointerCount(MotionEvent paramMotionEvent)
  {
    return paramMotionEvent.getPointerCount();
  }
  
  @Deprecated
  public static int getPointerId(MotionEvent paramMotionEvent, int paramInt)
  {
    return paramMotionEvent.getPointerId(paramInt);
  }
  
  @Deprecated
  public static int getSource(MotionEvent paramMotionEvent)
  {
    return paramMotionEvent.getSource();
  }
  
  @Deprecated
  public static float getX(MotionEvent paramMotionEvent, int paramInt)
  {
    return paramMotionEvent.getX(paramInt);
  }
  
  @Deprecated
  public static float getY(MotionEvent paramMotionEvent, int paramInt)
  {
    return paramMotionEvent.getY(paramInt);
  }
  
  public static boolean isFromSource(MotionEvent paramMotionEvent, int paramInt)
  {
    return (paramMotionEvent.getSource() & paramInt) == paramInt;
  }
  
  static class BaseMotionEventVersionImpl
    implements MotionEventCompat.MotionEventVersionImpl
  {
    public float getAxisValue(MotionEvent paramMotionEvent, int paramInt)
    {
      return 0.0F;
    }
    
    public float getAxisValue(MotionEvent paramMotionEvent, int paramInt1, int paramInt2)
    {
      return 0.0F;
    }
    
    public int getButtonState(MotionEvent paramMotionEvent)
    {
      return 0;
    }
  }
  
  static class HoneycombMr1MotionEventVersionImpl
    extends MotionEventCompat.BaseMotionEventVersionImpl
  {
    public float getAxisValue(MotionEvent paramMotionEvent, int paramInt)
    {
      return MotionEventCompatHoneycombMr1.getAxisValue(paramMotionEvent, paramInt);
    }
    
    public float getAxisValue(MotionEvent paramMotionEvent, int paramInt1, int paramInt2)
    {
      return MotionEventCompatHoneycombMr1.getAxisValue(paramMotionEvent, paramInt1, paramInt2);
    }
  }
  
  private static class ICSMotionEventVersionImpl
    extends MotionEventCompat.HoneycombMr1MotionEventVersionImpl
  {
    public int getButtonState(MotionEvent paramMotionEvent)
    {
      return MotionEventCompatICS.getButtonState(paramMotionEvent);
    }
  }
  
  static abstract interface MotionEventVersionImpl
  {
    public abstract float getAxisValue(MotionEvent paramMotionEvent, int paramInt);
    
    public abstract float getAxisValue(MotionEvent paramMotionEvent, int paramInt1, int paramInt2);
    
    public abstract int getButtonState(MotionEvent paramMotionEvent);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/view/MotionEventCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */