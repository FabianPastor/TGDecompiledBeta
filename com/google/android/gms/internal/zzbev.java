package com.google.android.gms.internal;

import com.google.android.gms.common.api.Api.zzc;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public final class zzbev
{
  public static final Status zzaFj = new Status(8, "The connection to Google Play services was lost");
  private static final zzbbe<?>[] zzaFk = new zzbbe[0];
  private final Map<Api.zzc<?>, Api.zze> zzaDF;
  final Set<zzbbe<?>> zzaFl = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
  private final zzbex zzaFm = new zzbew(this);
  
  public zzbev(Map<Api.zzc<?>, Api.zze> paramMap)
  {
    this.zzaDF = paramMap;
  }
  
  public final void release()
  {
    zzbbe[] arrayOfzzbbe = (zzbbe[])this.zzaFl.toArray(zzaFk);
    int j = arrayOfzzbbe.length;
    int i = 0;
    while (i < j)
    {
      zzbbe localzzbbe = arrayOfzzbbe[i];
      localzzbbe.zza(null);
      localzzbbe.zzpo();
      if (localzzbbe.zzpB()) {
        this.zzaFl.remove(localzzbbe);
      }
      i += 1;
    }
  }
  
  final void zzb(zzbbe<? extends Result> paramzzbbe)
  {
    this.zzaFl.add(paramzzbbe);
    paramzzbbe.zza(this.zzaFm);
  }
  
  public final void zzqM()
  {
    zzbbe[] arrayOfzzbbe = (zzbbe[])this.zzaFl.toArray(zzaFk);
    int j = arrayOfzzbbe.length;
    int i = 0;
    while (i < j)
    {
      arrayOfzzbbe[i].zzs(zzaFj);
      i += 1;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbev.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */