package com.google.android.gms.common.internal;

import android.content.Context;
import android.util.SparseIntArray;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.google.android.gms.common.api.Api.Client;

public class GoogleApiAvailabilityCache
{
  private final SparseIntArray zzug = new SparseIntArray();
  private GoogleApiAvailabilityLight zzuh;
  
  public GoogleApiAvailabilityCache()
  {
    this(GoogleApiAvailability.getInstance());
  }
  
  public GoogleApiAvailabilityCache(GoogleApiAvailabilityLight paramGoogleApiAvailabilityLight)
  {
    Preconditions.checkNotNull(paramGoogleApiAvailabilityLight);
    this.zzuh = paramGoogleApiAvailabilityLight;
  }
  
  public void flush()
  {
    this.zzug.clear();
  }
  
  public int getClientAvailability(Context paramContext, Api.Client paramClient)
  {
    int i = 0;
    Preconditions.checkNotNull(paramContext);
    Preconditions.checkNotNull(paramClient);
    if (!paramClient.requiresGooglePlayServices()) {}
    int j;
    do
    {
      return i;
      j = paramClient.getMinApkVersion();
      i = this.zzug.get(j, -1);
    } while (i != -1);
    int k = 0;
    label53:
    if (k < this.zzug.size())
    {
      int m = this.zzug.keyAt(k);
      if ((m > j) && (this.zzug.get(m) == 0)) {
        i = 0;
      }
    }
    for (;;)
    {
      k = i;
      if (i == -1) {
        k = this.zzuh.isGooglePlayServicesAvailable(paramContext, j);
      }
      this.zzug.put(j, k);
      i = k;
      break;
      k++;
      break label53;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/GoogleApiAvailabilityCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */