package android.support.v4.app;

import android.app.Service;
import android.support.v4.os.BuildCompat;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class ServiceCompat
{
  static final ServiceCompatImpl IMPL = new BaseServiceCompatImpl();
  public static final int START_STICKY = 1;
  public static final int STOP_FOREGROUND_DETACH = 2;
  public static final int STOP_FOREGROUND_REMOVE = 1;
  
  static
  {
    if (BuildCompat.isAtLeastN())
    {
      IMPL = new Api24ServiceCompatImpl();
      return;
    }
  }
  
  public static void stopForeground(Service paramService, int paramInt)
  {
    IMPL.stopForeground(paramService, paramInt);
  }
  
  static class Api24ServiceCompatImpl
    implements ServiceCompat.ServiceCompatImpl
  {
    public void stopForeground(Service paramService, int paramInt)
    {
      ServiceCompatApi24.stopForeground(paramService, paramInt);
    }
  }
  
  static class BaseServiceCompatImpl
    implements ServiceCompat.ServiceCompatImpl
  {
    public void stopForeground(Service paramService, int paramInt)
    {
      if ((paramInt & 0x1) != 0) {}
      for (boolean bool = true;; bool = false)
      {
        paramService.stopForeground(bool);
        return;
      }
    }
  }
  
  static abstract interface ServiceCompatImpl
  {
    public abstract void stopForeground(Service paramService, int paramInt);
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface StopForegroundFlags {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/app/ServiceCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */