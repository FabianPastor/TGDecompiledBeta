package com.google.android.gms.common.api;

import java.util.Map;
import java.util.WeakHashMap;
import javax.annotation.concurrent.GuardedBy;

public abstract class zzc
{
  private static final Object sLock = new Object();
  @GuardedBy("sLock")
  private static final Map<Object, zzc> zzdo = new WeakHashMap();
  
  public abstract void remove(int paramInt);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/zzc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */