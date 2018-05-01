package com.google.android.gms.wearable.internal;

import android.content.Context;
import com.google.android.gms.common.api.GoogleApi.Settings;
import com.google.android.gms.common.internal.Asserts;
import com.google.android.gms.common.internal.PendingResultUtil;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;

public final class zzaa
  extends CapabilityClient
{
  private final CapabilityApi zzbw = new zzo();
  
  public zzaa(Context paramContext, GoogleApi.Settings paramSettings)
  {
    super(paramContext, paramSettings);
  }
  
  public final Task<CapabilityInfo> getCapability(String paramString, int paramInt)
  {
    Asserts.checkNotNull(paramString, "capability must not be null");
    return PendingResultUtil.toTask(this.zzbw.getCapability(asGoogleApiClient(), paramString, paramInt), zzab.zzbx);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzaa.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */