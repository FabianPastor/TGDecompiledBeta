package com.google.android.gms.internal.config;

import android.os.Bundle;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataBuffer;
import com.google.android.gms.common.data.DataBufferSafeParcelable;
import com.google.android.gms.common.data.DataHolder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

public final class zzo
  implements zzg
{
  private static final Charset UTF_8 = Charset.forName("UTF-8");
  private static final Pattern zzl = Pattern.compile("^(1|true|t|yes|y|on)$", 2);
  private static final Pattern zzm = Pattern.compile("^(0|false|f|no|n|off|)$", 2);
  
  private static HashMap<String, TreeMap<String, byte[]>> zza(zzad paramzzad)
  {
    Object localObject1 = null;
    if (paramzzad == null) {}
    for (;;)
    {
      return (HashMap<String, TreeMap<String, byte[]>>)localObject1;
      Object localObject2 = paramzzad.zzi();
      if (localObject2 != null)
      {
        localObject2 = (zzaj)new DataBufferSafeParcelable((DataHolder)localObject2, zzaj.CREATOR).get(0);
        paramzzad.zzk();
        localObject1 = new HashMap();
        Iterator localIterator1 = ((zzaj)localObject2).zzm().keySet().iterator();
        while (localIterator1.hasNext())
        {
          Object localObject3 = (String)localIterator1.next();
          paramzzad = new TreeMap();
          ((HashMap)localObject1).put(localObject3, paramzzad);
          localObject3 = ((zzaj)localObject2).zzm().getBundle((String)localObject3);
          Iterator localIterator2 = ((Bundle)localObject3).keySet().iterator();
          while (localIterator2.hasNext())
          {
            String str = (String)localIterator2.next();
            paramzzad.put(str, ((Bundle)localObject3).getByteArray(str));
          }
        }
      }
    }
  }
  
  static List<byte[]> zzb(zzad paramzzad)
  {
    Object localObject1 = null;
    if (paramzzad == null) {}
    for (;;)
    {
      return (List<byte[]>)localObject1;
      Object localObject2 = paramzzad.zzj();
      if (localObject2 != null)
      {
        localObject1 = new ArrayList();
        localObject2 = new DataBufferSafeParcelable((DataHolder)localObject2, zzx.CREATOR).iterator();
        while (((Iterator)localObject2).hasNext()) {
          ((List)localObject1).add(((zzx)((Iterator)localObject2).next()).getPayload());
        }
        paramzzad.zzl();
      }
    }
  }
  
  private static Status zzd(int paramInt)
  {
    String str;
    switch (paramInt)
    {
    default: 
      str = CommonStatusCodes.getStatusCodeString(paramInt);
    }
    for (;;)
    {
      return new Status(paramInt, str);
      str = "NOT_AUTHORIZED_TO_FETCH";
      continue;
      str = "ANOTHER_FETCH_INFLIGHT";
      continue;
      str = "FETCH_THROTTLED";
      continue;
      str = "NOT_AVAILABLE";
      continue;
      str = "FAILURE_CACHE";
      continue;
      str = "SUCCESS_FRESH";
      continue;
      str = "SUCCESS_CACHE";
      continue;
      str = "FETCH_THROTTLED_STALE";
      continue;
      str = "SUCCESS_CACHE_STALE";
    }
  }
  
  public final PendingResult<zzk> zza(GoogleApiClient paramGoogleApiClient, zzi paramzzi)
  {
    if ((paramGoogleApiClient == null) || (paramzzi == null)) {}
    for (paramGoogleApiClient = null;; paramGoogleApiClient = paramGoogleApiClient.enqueue(new zzp(this, paramGoogleApiClient, paramzzi))) {
      return paramGoogleApiClient;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/config/zzo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */