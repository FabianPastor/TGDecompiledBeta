package com.google.android.gms.common.api.internal;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api.Client;
import com.google.android.gms.common.internal.BaseGmsClient.ConnectionProgressReportCallbacks;
import com.google.android.gms.common.internal.GoogleApiAvailabilityCache;
import com.google.android.gms.signin.SignInClient;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class zzam
  extends zzat
{
  private final Map<Api.Client, zzal> zzhx;
  
  public zzam(Map<Api.Client, zzal> paramMap)
  {
    super(paramMap, null);
    Map localMap;
    this.zzhx = localMap;
  }
  
  public final void zzaq()
  {
    int i = 0;
    Object localObject1 = new GoogleApiAvailabilityCache(zzaj.zzb(this.zzhv));
    ArrayList localArrayList = new ArrayList();
    Object localObject2 = new ArrayList();
    Object localObject3 = this.zzhx.keySet().iterator();
    Object localObject4;
    while (((Iterator)localObject3).hasNext())
    {
      localObject4 = (Api.Client)((Iterator)localObject3).next();
      if ((((Api.Client)localObject4).requiresGooglePlayServices()) && (!zzal.zza((zzal)this.zzhx.get(localObject4)))) {
        localArrayList.add(localObject4);
      } else {
        ((List)localObject2).add(localObject4);
      }
    }
    int j = -1;
    int k;
    int m;
    if (localArrayList.isEmpty())
    {
      localObject3 = (ArrayList)localObject2;
      k = ((ArrayList)localObject3).size();
      while (i < k)
      {
        localObject4 = ((ArrayList)localObject3).get(i);
        i++;
        localObject4 = (Api.Client)localObject4;
        m = ((GoogleApiAvailabilityCache)localObject1).getClientAvailability(zzaj.zza(this.zzhv), (Api.Client)localObject4);
        j = m;
        if (m == 0)
        {
          j = m;
          break label260;
        }
      }
      label205:
      if (j == 0) {
        break label315;
      }
      localObject1 = new ConnectionResult(j, null);
      zzaj.zzd(this.zzhv).zza(new zzan(this, this.zzhv, (ConnectionResult)localObject1));
    }
    for (;;)
    {
      return;
      localObject3 = (ArrayList)localArrayList;
      k = ((ArrayList)localObject3).size();
      i = 0;
      label260:
      if (i >= k) {
        break label205;
      }
      localObject4 = ((ArrayList)localObject3).get(i);
      i++;
      localObject4 = (Api.Client)localObject4;
      m = ((GoogleApiAvailabilityCache)localObject1).getClientAvailability(zzaj.zza(this.zzhv), (Api.Client)localObject4);
      j = m;
      if (m == 0) {
        break;
      }
      j = m;
      break label205;
      label315:
      if (zzaj.zze(this.zzhv)) {
        zzaj.zzf(this.zzhv).connect();
      }
      localObject3 = this.zzhx.keySet().iterator();
      while (((Iterator)localObject3).hasNext())
      {
        localObject4 = (Api.Client)((Iterator)localObject3).next();
        localObject2 = (BaseGmsClient.ConnectionProgressReportCallbacks)this.zzhx.get(localObject4);
        if ((((Api.Client)localObject4).requiresGooglePlayServices()) && (((GoogleApiAvailabilityCache)localObject1).getClientAvailability(zzaj.zza(this.zzhv), (Api.Client)localObject4) != 0)) {
          zzaj.zzd(this.zzhv).zza(new zzao(this, this.zzhv, (BaseGmsClient.ConnectionProgressReportCallbacks)localObject2));
        } else {
          ((Api.Client)localObject4).connect((BaseGmsClient.ConnectionProgressReportCallbacks)localObject2);
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzam.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */