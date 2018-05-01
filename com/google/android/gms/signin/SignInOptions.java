package com.google.android.gms.signin;

import com.google.android.gms.common.api.Api.ApiOptions.Optional;

public final class SignInOptions
  implements Api.ApiOptions.Optional
{
  public static final SignInOptions DEFAULT = new Builder().build();
  private final boolean zzadb;
  private final boolean zzadc;
  private final Long zzadd;
  private final Long zzade;
  private final boolean zzt;
  private final boolean zzv;
  private final String zzw;
  private final String zzx;
  
  private SignInOptions(boolean paramBoolean1, boolean paramBoolean2, String paramString1, boolean paramBoolean3, String paramString2, boolean paramBoolean4, Long paramLong1, Long paramLong2)
  {
    this.zzadb = paramBoolean1;
    this.zzt = paramBoolean2;
    this.zzw = paramString1;
    this.zzv = paramBoolean3;
    this.zzadc = paramBoolean4;
    this.zzx = paramString2;
    this.zzadd = paramLong1;
    this.zzade = paramLong2;
  }
  
  public final Long getAuthApiSignInModuleVersion()
  {
    return this.zzadd;
  }
  
  public final String getHostedDomain()
  {
    return this.zzx;
  }
  
  public final Long getRealClientLibraryVersion()
  {
    return this.zzade;
  }
  
  public final String getServerClientId()
  {
    return this.zzw;
  }
  
  public final boolean isForceCodeForRefreshToken()
  {
    return this.zzv;
  }
  
  public final boolean isIdTokenRequested()
  {
    return this.zzt;
  }
  
  public final boolean isOfflineAccessRequested()
  {
    return this.zzadb;
  }
  
  public final boolean waitForAccessTokenRefresh()
  {
    return this.zzadc;
  }
  
  public static final class Builder
  {
    private boolean zzadf;
    private boolean zzadg;
    private String zzadh;
    private boolean zzadi;
    private String zzadj;
    private boolean zzadk;
    private Long zzadl;
    private Long zzadm;
    
    public final SignInOptions build()
    {
      return new SignInOptions(this.zzadf, this.zzadg, this.zzadh, this.zzadi, this.zzadj, this.zzadk, this.zzadl, this.zzadm, null);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/signin/SignInOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */