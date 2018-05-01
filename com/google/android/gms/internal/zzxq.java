package com.google.android.gms.internal;

import android.support.annotation.Nullable;
import com.google.android.gms.common.api.Api.ApiOptions.Optional;

public final class zzxq
  implements Api.ApiOptions.Optional
{
  public static final zzxq aDl = new zza().zzcdh();
  private final boolean aDm;
  private final boolean aDn;
  private final Long aDo;
  private final Long aDp;
  private final boolean jr;
  private final boolean jt;
  private final String ju;
  private final String jv;
  
  private zzxq(boolean paramBoolean1, boolean paramBoolean2, String paramString1, boolean paramBoolean3, String paramString2, boolean paramBoolean4, Long paramLong1, Long paramLong2)
  {
    this.aDm = paramBoolean1;
    this.jr = paramBoolean2;
    this.ju = paramString1;
    this.jt = paramBoolean3;
    this.aDn = paramBoolean4;
    this.jv = paramString2;
    this.aDo = paramLong1;
    this.aDp = paramLong2;
  }
  
  public boolean zzaiu()
  {
    return this.jr;
  }
  
  public boolean zzaiw()
  {
    return this.jt;
  }
  
  public String zzaix()
  {
    return this.ju;
  }
  
  @Nullable
  public String zzaiy()
  {
    return this.jv;
  }
  
  public boolean zzcdd()
  {
    return this.aDm;
  }
  
  public boolean zzcde()
  {
    return this.aDn;
  }
  
  @Nullable
  public Long zzcdf()
  {
    return this.aDo;
  }
  
  @Nullable
  public Long zzcdg()
  {
    return this.aDp;
  }
  
  public static final class zza
  {
    public zzxq zzcdh()
    {
      return new zzxq(false, false, null, false, null, false, null, null, null);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzxq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */