package com.google.android.gms.common.api;

import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.internal.zzpz;

public class zzc
  implements Result
{
  private final Status fp;
  private final ArrayMap<zzpz<?>, ConnectionResult> vn;
  
  public zzc(Status paramStatus, ArrayMap<zzpz<?>, ConnectionResult> paramArrayMap)
  {
    this.fp = paramStatus;
    this.vn = paramArrayMap;
  }
  
  public Status getStatus()
  {
    return this.fp;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/zzc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */