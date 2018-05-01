package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.CapabilityApi.GetCapabilityResult;
import com.google.android.gms.wearable.CapabilityInfo;

public final class zzy
  implements CapabilityApi.GetCapabilityResult
{
  private final CapabilityInfo zzbv;
  private final Status zzp;
  
  public zzy(Status paramStatus, CapabilityInfo paramCapabilityInfo)
  {
    this.zzp = paramStatus;
    this.zzbv = paramCapabilityInfo;
  }
  
  public final CapabilityInfo getCapability()
  {
    return this.zzbv;
  }
  
  public final Status getStatus()
  {
    return this.zzp;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */