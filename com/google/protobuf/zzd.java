package com.google.protobuf;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class zzd
{
  private static volatile boolean zzcrP = false;
  private static final Class<?> zzcrQ = zzLs();
  static final zzd zzcrR = new zzd(true);
  private final Map<Object, Object> zzcrS;
  
  zzd()
  {
    this.zzcrS = new HashMap();
  }
  
  zzd(boolean paramBoolean)
  {
    this.zzcrS = Collections.emptyMap();
  }
  
  private static Class<?> zzLs()
  {
    try
    {
      Class localClass = Class.forName("com.google.protobuf.Extension");
      return localClass;
    }
    catch (ClassNotFoundException localClassNotFoundException) {}
    return null;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/protobuf/zzd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */