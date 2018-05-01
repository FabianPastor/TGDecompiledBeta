package com.google.android.gms.internal;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.internal.zzbe;
import java.util.Arrays;

public final class zzbat<O extends Api.ApiOptions>
{
  private final O zzaAJ;
  private final int zzaBA;
  private final boolean zzaBz;
  private final Api<O> zzayW;
  
  private zzbat(Api<O> paramApi)
  {
    this.zzaBz = true;
    this.zzayW = paramApi;
    this.zzaAJ = null;
    this.zzaBA = System.identityHashCode(this);
  }
  
  private zzbat(Api<O> paramApi, O paramO)
  {
    this.zzaBz = false;
    this.zzayW = paramApi;
    this.zzaAJ = paramO;
    this.zzaBA = Arrays.hashCode(new Object[] { this.zzayW, this.zzaAJ });
  }
  
  public static <O extends Api.ApiOptions> zzbat<O> zza(Api<O> paramApi, O paramO)
  {
    return new zzbat(paramApi, paramO);
  }
  
  public static <O extends Api.ApiOptions> zzbat<O> zzb(Api<O> paramApi)
  {
    return new zzbat(paramApi);
  }
  
  public final boolean equals(Object paramObject)
  {
    if (paramObject == this) {}
    do
    {
      return true;
      if (!(paramObject instanceof zzbat)) {
        return false;
      }
      paramObject = (zzbat)paramObject;
    } while ((!this.zzaBz) && (!((zzbat)paramObject).zzaBz) && (zzbe.equal(this.zzayW, ((zzbat)paramObject).zzayW)) && (zzbe.equal(this.zzaAJ, ((zzbat)paramObject).zzaAJ)));
    return false;
  }
  
  public final int hashCode()
  {
    return this.zzaBA;
  }
  
  public final String zzpr()
  {
    return this.zzayW.getName();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */