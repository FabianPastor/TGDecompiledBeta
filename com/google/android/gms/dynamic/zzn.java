package com.google.android.gms.dynamic;

import android.os.IBinder;
import java.lang.reflect.Field;

public final class zzn<T>
  extends IObjectWrapper.zza
{
  private final T mWrappedObject;
  
  private zzn(T paramT)
  {
    this.mWrappedObject = paramT;
  }
  
  public static <T> T zzx(IObjectWrapper paramIObjectWrapper)
  {
    int j = 0;
    if ((paramIObjectWrapper instanceof zzn)) {
      return (T)((zzn)paramIObjectWrapper).mWrappedObject;
    }
    IBinder localIBinder = paramIObjectWrapper.asBinder();
    Field[] arrayOfField = localIBinder.getClass().getDeclaredFields();
    paramIObjectWrapper = null;
    int k = arrayOfField.length;
    int i = 0;
    if (i < k)
    {
      Field localField = arrayOfField[i];
      if (localField.isSynthetic()) {
        break label169;
      }
      j += 1;
      paramIObjectWrapper = localField;
    }
    label169:
    for (;;)
    {
      i += 1;
      break;
      if (j == 1)
      {
        if (!paramIObjectWrapper.isAccessible())
        {
          paramIObjectWrapper.setAccessible(true);
          try
          {
            paramIObjectWrapper = paramIObjectWrapper.get(localIBinder);
            return paramIObjectWrapper;
          }
          catch (NullPointerException paramIObjectWrapper)
          {
            throw new IllegalArgumentException("Binder object is null.", paramIObjectWrapper);
          }
          catch (IllegalAccessException paramIObjectWrapper)
          {
            throw new IllegalArgumentException("Could not access the field in remoteBinder.", paramIObjectWrapper);
          }
        }
        throw new IllegalArgumentException("IObjectWrapper declared field not private!");
      }
      i = arrayOfField.length;
      throw new IllegalArgumentException(64 + "Unexpected number of IObjectWrapper declared fields: " + i);
    }
  }
  
  public static <T> IObjectWrapper zzz(T paramT)
  {
    return new zzn(paramT);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/dynamic/zzn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */