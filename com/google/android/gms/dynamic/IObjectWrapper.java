package com.google.android.gms.dynamic;

import android.os.IBinder;
import android.os.IInterface;
import com.google.android.gms.internal.stable.zza;
import com.google.android.gms.internal.stable.zzb;

public abstract interface IObjectWrapper
  extends IInterface
{
  public static class Stub
    extends zzb
    implements IObjectWrapper
  {
    public Stub()
    {
      super();
    }
    
    public static IObjectWrapper asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        paramIBinder = null;
      }
      for (;;)
      {
        return paramIBinder;
        IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gms.dynamic.IObjectWrapper");
        if ((localIInterface instanceof IObjectWrapper)) {
          paramIBinder = (IObjectWrapper)localIInterface;
        } else {
          paramIBinder = new Proxy(paramIBinder);
        }
      }
    }
    
    public static class Proxy
      extends zza
      implements IObjectWrapper
    {
      Proxy(IBinder paramIBinder)
      {
        super("com.google.android.gms.dynamic.IObjectWrapper");
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/dynamic/IObjectWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */