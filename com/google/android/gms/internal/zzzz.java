package com.google.android.gms.internal;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.internal.zzaa;

public final class zzzz<O extends Api.ApiOptions>
{
  private final Api<O> zzaxf;
  private final O zzayT;
  private final boolean zzazL;
  private final int zzazM;
  
  private zzzz(Api<O> paramApi)
  {
    this.zzazL = true;
    this.zzaxf = paramApi;
    this.zzayT = null;
    this.zzazM = System.identityHashCode(this);
  }
  
  private zzzz(Api<O> paramApi, O paramO)
  {
    this.zzazL = false;
    this.zzaxf = paramApi;
    this.zzayT = paramO;
    this.zzazM = zzaa.hashCode(new Object[] { this.zzaxf, this.zzayT });
  }
  
  public static <O extends Api.ApiOptions> zzzz<O> zza(Api<O> paramApi, O paramO)
  {
    return new zzzz(paramApi, paramO);
  }
  
  public static <O extends Api.ApiOptions> zzzz<O> zzb(Api<O> paramApi)
  {
    return new zzzz(paramApi);
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {}
    do
    {
      return true;
      if (!(paramObject instanceof zzzz)) {
        return false;
      }
      paramObject = (zzzz)paramObject;
    } while ((!this.zzazL) && (!((zzzz)paramObject).zzazL) && (zzaa.equal(this.zzaxf, ((zzzz)paramObject).zzaxf)) && (zzaa.equal(this.zzayT, ((zzzz)paramObject).zzayT)));
    return false;
  }
  
  public int hashCode()
  {
    return this.zzazM;
  }
  
  public String zzvw()
  {
    return this.zzaxf.getName();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzzz.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */