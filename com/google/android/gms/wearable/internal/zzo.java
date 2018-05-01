package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityApi.GetCapabilityResult;

public final class zzo
  implements CapabilityApi
{
  public final PendingResult<CapabilityApi.GetCapabilityResult> getCapability(GoogleApiClient paramGoogleApiClient, String paramString, int paramInt)
  {
    boolean bool1 = true;
    boolean bool2 = bool1;
    if (paramInt != 0) {
      if (paramInt != 1) {
        break label41;
      }
    }
    label41:
    for (bool2 = bool1;; bool2 = false)
    {
      Preconditions.checkArgument(bool2);
      return paramGoogleApiClient.enqueue(new zzp(this, paramGoogleApiClient, paramString, paramInt));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */