package com.google.android.gms.internal;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.google.android.gms.common.internal.zzbq;

public final class zzcic
{
  private String mValue;
  private final String zzbhb;
  private boolean zzjdl;
  private final String zzjdr;
  
  public zzcic(zzchx paramzzchx, String paramString1, String paramString2)
  {
    zzbq.zzgm(paramString1);
    this.zzbhb = paramString1;
    this.zzjdr = null;
  }
  
  public final String zzazr()
  {
    if (!this.zzjdl)
    {
      this.zzjdl = true;
      this.mValue = zzchx.zza(this.zzjdm).getString(this.zzbhb, null);
    }
    return this.mValue;
  }
  
  public final void zzjq(String paramString)
  {
    if (zzclq.zzas(paramString, this.mValue)) {
      return;
    }
    SharedPreferences.Editor localEditor = zzchx.zza(this.zzjdm).edit();
    localEditor.putString(this.zzbhb, paramString);
    localEditor.apply();
    this.mValue = paramString;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcic.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */