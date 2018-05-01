package com.google.android.gms.common.api;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.internal.zzh;
import com.google.android.gms.common.internal.Preconditions;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class AvailabilityException
  extends Exception
{
  private final ArrayMap<zzh<?>, ConnectionResult> zzcc;
  
  public AvailabilityException(ArrayMap<zzh<?>, ConnectionResult> paramArrayMap)
  {
    this.zzcc = paramArrayMap;
  }
  
  public ConnectionResult getConnectionResult(GoogleApi<? extends Api.ApiOptions> paramGoogleApi)
  {
    paramGoogleApi = paramGoogleApi.zzm();
    if (this.zzcc.get(paramGoogleApi) != null) {}
    for (boolean bool = true;; bool = false)
    {
      Preconditions.checkArgument(bool, "The given API was not part of the availability request.");
      return (ConnectionResult)this.zzcc.get(paramGoogleApi);
    }
  }
  
  public String getMessage()
  {
    ArrayList localArrayList = new ArrayList();
    Object localObject1 = this.zzcc.keySet().iterator();
    int i = 1;
    while (((Iterator)localObject1).hasNext())
    {
      Object localObject2 = (zzh)((Iterator)localObject1).next();
      Object localObject3 = (ConnectionResult)this.zzcc.get(localObject2);
      if (((ConnectionResult)localObject3).isSuccess()) {
        i = 0;
      }
      localObject2 = ((zzh)localObject2).zzq();
      localObject3 = String.valueOf(localObject3);
      localArrayList.add(String.valueOf(localObject2).length() + 2 + String.valueOf(localObject3).length() + (String)localObject2 + ": " + (String)localObject3);
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
  
  public final ArrayMap<zzh<?>, ConnectionResult> zzl()
  {
    return this.zzcc;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/AvailabilityException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */