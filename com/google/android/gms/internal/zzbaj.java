package com.google.android.gms.internal;

import android.support.annotation.Nullable;
import com.google.android.gms.common.api.Api.ApiOptions.Optional;

public final class zzbaj
  implements Api.ApiOptions.Optional
{
  public static final zzbaj zzbEl = new zza().zzPQ();
  private final boolean zzajv;
  private final String zzajw;
  private final boolean zzakm;
  private final String zzakn;
  private final boolean zzbEm;
  private final boolean zzbEn;
  private final Long zzbEo;
  private final Long zzbEp;
  
  private zzbaj(boolean paramBoolean1, boolean paramBoolean2, String paramString1, boolean paramBoolean3, String paramString2, boolean paramBoolean4, Long paramLong1, Long paramLong2)
  {
    this.zzbEm = paramBoolean1;
    this.zzajv = paramBoolean2;
    this.zzajw = paramString1;
    this.zzakm = paramBoolean3;
    this.zzbEn = paramBoolean4;
    this.zzakn = paramString2;
    this.zzbEo = paramLong1;
    this.zzbEp = paramLong2;
  }
  
  public String getServerClientId()
  {
    return this.zzajw;
  }
  
  public boolean isIdTokenRequested()
  {
    return this.zzajv;
  }
  
  public boolean zzPM()
  {
    return this.zzbEm;
  }
  
  public boolean zzPN()
  {
    return this.zzbEn;
  }
  
  @Nullable
  public Long zzPO()
  {
    return this.zzbEo;
  }
  
  @Nullable
  public Long zzPP()
  {
    return this.zzbEp;
  }
  
  public boolean zzrl()
  {
    return this.zzakm;
  }
  
  @Nullable
  public String zzrm()
  {
    return this.zzakn;
  }
  
  public static final class zza
  {
    public zzbaj zzPQ()
    {
      return new zzbaj(false, false, null, false, null, false, null, null, null);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbaj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */