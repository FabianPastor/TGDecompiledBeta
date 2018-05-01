package com.google.android.gms.wearable;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;

@Deprecated
public abstract interface CapabilityApi
{
  public abstract PendingResult<GetCapabilityResult> getCapability(GoogleApiClient paramGoogleApiClient, String paramString, int paramInt);
  
  @Deprecated
  public static abstract interface CapabilityListener
  {
    public abstract void onCapabilityChanged(CapabilityInfo paramCapabilityInfo);
  }
  
  @Deprecated
  public static abstract interface GetCapabilityResult
    extends Result
  {
    public abstract CapabilityInfo getCapability();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/CapabilityApi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */