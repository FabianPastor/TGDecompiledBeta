package com.google.protobuf;

import java.lang.reflect.Method;

final class zzc
{
  private static Class<?> zzcrO = ;
  
  private static Class<?> zzLq()
  {
    try
    {
      Class localClass = Class.forName("com.google.protobuf.ExtensionRegistry");
      return localClass;
    }
    catch (ClassNotFoundException localClassNotFoundException) {}
    return null;
  }
  
  public static zzd zzLr()
  {
    if (zzcrO != null) {
      try
      {
        zzd localzzd = (zzd)zzcrO.getMethod("getEmptyRegistry", new Class[0]).invoke(null, new Object[0]);
        return localzzd;
      }
      catch (Exception localException) {}
    }
    return zzd.zzcrR;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/protobuf/zzc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */