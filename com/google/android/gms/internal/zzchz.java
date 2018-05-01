package com.google.android.gms.internal;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.google.android.gms.common.internal.zzbq;

public final class zzchz
{
  private final String zzbhb;
  private boolean zzfmd;
  private final boolean zzjdk;
  private boolean zzjdl;
  
  public zzchz(zzchx paramzzchx, String paramString, boolean paramBoolean)
  {
    zzbq.zzgm(paramString);
    this.zzbhb = paramString;
    this.zzjdk = true;
  }
  
  public final boolean get()
  {
    if (!this.zzjdl)
    {
      this.zzjdl = true;
      this.zzfmd = zzchx.zza(this.zzjdm).getBoolean(this.zzbhb, this.zzjdk);
    }
    return this.zzfmd;
  }
  
  public final void set(boolean paramBoolean)
  {
    SharedPreferences.Editor localEditor = zzchx.zza(this.zzjdm).edit();
    localEditor.putBoolean(this.zzbhb, paramBoolean);
    localEditor.apply();
    this.zzfmd = paramBoolean;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzchz.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */