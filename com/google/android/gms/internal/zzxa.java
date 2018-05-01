package com.google.android.gms.internal;

import android.support.annotation.Nullable;
import com.google.android.gms.common.api.Api.ApiOptions.Optional;

public final class zzxa
  implements Api.ApiOptions.Optional
{
  public static final zzxa aAa = new zza().zzcdf();
  private final boolean aAb;
  private final boolean aAc;
  private final Long aAd;
  private final Long aAe;
  private final boolean hh;
  private final boolean hj;
  private final String hk;
  private final String hl;
  
  private zzxa(boolean paramBoolean1, boolean paramBoolean2, String paramString1, boolean paramBoolean3, String paramString2, boolean paramBoolean4, Long paramLong1, Long paramLong2)
  {
    this.aAb = paramBoolean1;
    this.hh = paramBoolean2;
    this.hk = paramString1;
    this.hj = paramBoolean3;
    this.aAc = paramBoolean4;
    this.hl = paramString2;
    this.aAd = paramLong1;
    this.aAe = paramLong2;
  }
  
  public boolean zzahk()
  {
    return this.hh;
  }
  
  public boolean zzahm()
  {
    return this.hj;
  }
  
  public String zzahn()
  {
    return this.hk;
  }
  
  @Nullable
  public String zzaho()
  {
    return this.hl;
  }
  
  public boolean zzcdb()
  {
    return this.aAb;
  }
  
  public boolean zzcdc()
  {
    return this.aAc;
  }
  
  @Nullable
  public Long zzcdd()
  {
    return this.aAd;
  }
  
  @Nullable
  public Long zzcde()
  {
    return this.aAe;
  }
  
  public static final class zza
  {
    public zzxa zzcdf()
    {
      return new zzxa(false, false, null, false, null, false, null, null, null);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzxa.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */