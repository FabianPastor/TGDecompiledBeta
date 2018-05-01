package com.google.android.gms.common.api;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.internal.zzbat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class zza
  extends Exception
{
  private final ArrayMap<zzbat<?>, ConnectionResult> zzaAB;
  
  public zza(ArrayMap<zzbat<?>, ConnectionResult> paramArrayMap)
  {
    this.zzaAB = paramArrayMap;
  }
  
  public final String getMessage()
  {
    ArrayList localArrayList = new ArrayList();
    Object localObject1 = this.zzaAB.keySet().iterator();
    int i = 1;
    while (((Iterator)localObject1).hasNext())
    {
      Object localObject3 = (zzbat)((Iterator)localObject1).next();
      Object localObject2 = (ConnectionResult)this.zzaAB.get(localObject3);
      if (((ConnectionResult)localObject2).isSuccess()) {
        i = 0;
      }
      localObject3 = String.valueOf(((zzbat)localObject3).zzpr());
      localObject2 = String.valueOf(localObject2);
      localArrayList.add(String.valueOf(localObject3).length() + 2 + String.valueOf(localObject2).length() + (String)localObject3 + ": " + (String)localObject2);
    }
    localObject1 = new StringBuilder();
    if (i != 0) {
      ((StringBuilder)localObject1).append("None of the queried APIs are available. ");
    }
    for (;;)
    {
      ((StringBuilder)localObject1).append(TextUtils.join("; ", localArrayList));
      return ((StringBuilder)localObject1).toString();
      ((StringBuilder)localObject1).append("Some of the queried APIs are unavailable. ");
    }
  }
  
  public final ConnectionResult zza(GoogleApi<? extends Api.ApiOptions> paramGoogleApi)
  {
    paramGoogleApi = paramGoogleApi.zzph();
    if (this.zzaAB.get(paramGoogleApi) != null) {}
    for (boolean bool = true;; bool = false)
    {
      zzbo.zzb(bool, "The given API was not part of the availability request.");
      return (ConnectionResult)this.zzaAB.get(paramGoogleApi);
    }
  }
  
  public final ArrayMap<zzbat<?>, ConnectionResult> zzpf()
  {
    return this.zzaAB;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/zza.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */