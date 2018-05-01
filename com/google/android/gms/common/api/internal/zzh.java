package com.google.android.gms.common.api.internal;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.internal.Objects;

public final class zzh<O extends Api.ApiOptions>
{
  private final Api<O> mApi;
  private final O zzcl;
  private final boolean zzeb;
  private final int zzec;
  
  private zzh(Api<O> paramApi)
  {
    this.zzeb = true;
    this.mApi = paramApi;
    this.zzcl = null;
    this.zzec = System.identityHashCode(this);
  }
  
  private zzh(Api<O> paramApi, O paramO)
  {
    this.zzeb = false;
    this.mApi = paramApi;
    this.zzcl = paramO;
    this.zzec = Objects.hashCode(new Object[] { this.mApi, this.zzcl });
  }
  
  public static <O extends Api.ApiOptions> zzh<O> zza(Api<O> paramApi)
  {
    return new zzh(paramApi);
  }
  
  public static <O extends Api.ApiOptions> zzh<O> zza(Api<O> paramApi, O paramO)
  {
    return new zzh(paramApi, paramO);
  }
  
  public final boolean equals(Object paramObject)
  {
    boolean bool = true;
    if (paramObject == this) {}
    for (;;)
    {
      return bool;
      if (!(paramObject instanceof zzh))
      {
        bool = false;
      }
      else
      {
        paramObject = (zzh)paramObject;
        if ((this.zzeb) || (((zzh)paramObject).zzeb) || (!Objects.equal(this.mApi, ((zzh)paramObject).mApi)) || (!Objects.equal(this.zzcl, ((zzh)paramObject).zzcl))) {
          bool = false;
        }
      }
    }
  }
  
  public final int hashCode()
  {
    return this.zzec;
  }
  
  public final String zzq()
  {
    return this.mApi.getName();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */