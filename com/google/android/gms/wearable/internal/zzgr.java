package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.BaseImplementation.ResultHolder;
import com.google.android.gms.wearable.CapabilityApi.GetCapabilityResult;

final class zzgr
  extends zzgm<CapabilityApi.GetCapabilityResult>
{
  public zzgr(BaseImplementation.ResultHolder<CapabilityApi.GetCapabilityResult> paramResultHolder)
  {
    super(paramResultHolder);
  }
  
  public final void zza(zzdk paramzzdk)
  {
    Status localStatus = zzgd.zzb(paramzzdk.statusCode);
    if (paramzzdk.zzdq == null) {}
    for (paramzzdk = null;; paramzzdk = new zzw(paramzzdk.zzdq))
    {
      zza(new zzy(localStatus, paramzzdk));
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzgr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */