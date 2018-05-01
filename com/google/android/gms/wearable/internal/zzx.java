package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.CapabilityApi.GetAllCapabilitiesResult;
import com.google.android.gms.wearable.CapabilityInfo;
import java.util.Map;

public final class zzx
  implements CapabilityApi.GetAllCapabilitiesResult
{
  private final Status mStatus;
  private final Map<String, CapabilityInfo> zzbSb;
  
  public zzx(Status paramStatus, Map<String, CapabilityInfo> paramMap)
  {
    this.mStatus = paramStatus;
    this.zzbSb = paramMap;
  }
  
  public final Map<String, CapabilityInfo> getAllCapabilities()
  {
    return this.zzbSb;
  }
  
  public final Status getStatus()
  {
    return this.mStatus;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */