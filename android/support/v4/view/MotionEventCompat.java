package android.support.v4.view;

import android.view.MotionEvent;

public final class MotionEventCompat
{
  public static boolean isFromSource(MotionEvent paramMotionEvent, int paramInt)
  {
    if ((paramMotionEvent.getSource() & paramInt) == paramInt) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/view/MotionEventCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */