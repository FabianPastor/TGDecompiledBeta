package com.google.android.gms.dynamic;

import android.os.IBinder;
import java.lang.reflect.Field;

public final class ObjectWrapper<T>
  extends IObjectWrapper.Stub
{
  private final T zzabn;
  
  private ObjectWrapper(T paramT)
  {
    this.zzabn = paramT;
  }
  
  public static <T> T unwrap(IObjectWrapper paramIObjectWrapper)
  {
    if ((paramIObjectWrapper instanceof ObjectWrapper))
    {
      paramIObjectWrapper = ((ObjectWrapper)paramIObjectWrapper).zzabn;
      return paramIObjectWrapper;
    }
    IBinder localIBinder = paramIObjectWrapper.asBinder();
    Field[] arrayOfField = localIBinder.getClass().getDeclaredFields();
    paramIObjectWrapper = null;
    int i = arrayOfField.length;
    int j = 0;
    int k = 0;
    label43:
    if (j < i)
    {
      Field localField = arrayOfField[j];
      if (localField.isSynthetic()) {
        break label170;
      }
      k++;
      paramIObjectWrapper = localField;
    }
    label170:
    for (;;)
    {
      j++;
      break label43;
      if (k == 1)
      {
        if (!paramIObjectWrapper.isAccessible())
        {
          paramIObjectWrapper.setAccessible(true);
          try
          {
            paramIObjectWrapper = paramIObjectWrapper.get(localIBinder);
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
      j = arrayOfField.length;
      throw new IllegalArgumentException(64 + "Unexpected number of IObjectWrapper declared fields: " + j);
    }
  }
  
  public static <T> IObjectWrapper wrap(T paramT)
  {
    return new ObjectWrapper(paramT);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/dynamic/ObjectWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */