package com.google.android.gms.internal;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.internal.zzaa;

public final class zzzs<O extends Api.ApiOptions>
{
  private final Api<O> zzawb;
  private final O zzaxG;
  private final boolean zzayv;
  private final int zzayw;
  
  private zzzs(Api<O> paramApi)
  {
    this.zzayv = true;
    this.zzawb = paramApi;
    this.zzaxG = null;
    this.zzayw = System.identityHashCode(this);
  }
  
  private zzzs(Api<O> paramApi, O paramO)
  {
    this.zzayv = false;
    this.zzawb = paramApi;
    this.zzaxG = paramO;
    this.zzayw = zzaa.hashCode(new Object[] { this.zzawb, this.zzaxG });
  }
  
  public static <O extends Api.ApiOptions> zzzs<O> zza(Api<O> paramApi, O paramO)
  {
    return new zzzs(paramApi, paramO);
  }
  
  public static <O extends Api.ApiOptions> zzzs<O> zzb(Api<O> paramApi)
  {
    return new zzzs(paramApi);
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {}
    do
    {
      return true;
      if (!(paramObject instanceof zzzs)) {
        return false;
      }
      paramObject = (zzzs)paramObject;
    } while ((!this.zzayv) && (!((zzzs)paramObject).zzayv) && (zzaa.equal(this.zzawb, ((zzzs)paramObject).zzawb)) && (zzaa.equal(this.zzaxG, ((zzzs)paramObject).zzaxG)));
    return false;
  }
  
  public int hashCode()
  {
    return this.zzayw;
  }
  
  public String zzuV()
  {
    return this.zzawb.getName();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzzs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */