package com.google.android.gms.common.internal;

public final class Asserts
{
  public static void checkNotNull(Object paramObject)
  {
    if (paramObject == null) {
      throw new IllegalArgumentException("null reference");
    }
  }
  
  public static void checkNotNull(Object paramObject1, Object paramObject2)
  {
    if (paramObject1 == null) {
      throw new IllegalArgumentException(String.valueOf(paramObject2));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/Asserts.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */