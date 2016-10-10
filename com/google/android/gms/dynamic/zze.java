package com.google.android.gms.dynamic;

import java.lang.reflect.Field;

public final class zze<T>
  extends zzd.zza
{
  private final T mWrappedObject;
  
  private zze(T paramT)
  {
    this.mWrappedObject = paramT;
  }
  
  public static <T> zzd zzac(T paramT)
  {
    return new zze(paramT);
  }
  
  public static <T> T zzae(zzd paramzzd)
  {
    if ((paramzzd instanceof zze)) {
      return (T)((zze)paramzzd).mWrappedObject;
    }
    paramzzd = paramzzd.asBinder();
    Object localObject = paramzzd.getClass().getDeclaredFields();
    if (localObject.length == 1)
    {
      localObject = localObject[0];
      if (!((Field)localObject).isAccessible())
      {
        ((Field)localObject).setAccessible(true);
        try
        {
          paramzzd = ((Field)localObject).get(paramzzd);
          return paramzzd;
        }
        catch (NullPointerException paramzzd)
        {
          throw new IllegalArgumentException("Binder object is null.", paramzzd);
        }
        catch (IllegalAccessException paramzzd)
        {
          throw new IllegalArgumentException("Could not access the field in remoteBinder.", paramzzd);
        }
      }
      throw new IllegalArgumentException("IObjectWrapper declared field not private!");
    }
    int i = localObject.length;
    throw new IllegalArgumentException(64 + "Unexpected number of IObjectWrapper declared fields: " + i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/dynamic/zze.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */