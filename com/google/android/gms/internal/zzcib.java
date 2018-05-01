package com.google.android.gms.internal;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Pair;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.common.util.zzd;
import java.security.SecureRandom;

public final class zzcib
{
  private final long zzdyr;
  private String zzjdo;
  private final String zzjdp;
  private final String zzjdq;
  
  private zzcib(zzchx paramzzchx, String paramString, long paramLong)
  {
    zzbq.zzgm(paramString);
    if (paramLong > 0L) {}
    for (boolean bool = true;; bool = false)
    {
      zzbq.checkArgument(bool);
      this.zzjdo = String.valueOf(paramString).concat(":start");
      this.zzjdp = String.valueOf(paramString).concat(":count");
      this.zzjdq = String.valueOf(paramString).concat(":value");
      this.zzdyr = paramLong;
      return;
    }
  }
  
  private final void zzaac()
  {
    this.zzjdm.zzve();
    long l = this.zzjdm.zzws().currentTimeMillis();
    SharedPreferences.Editor localEditor = zzchx.zza(this.zzjdm).edit();
    localEditor.remove(this.zzjdp);
    localEditor.remove(this.zzjdq);
    localEditor.putLong(this.zzjdo, l);
    localEditor.apply();
  }
  
  private final long zzaae()
  {
    return zzchx.zza(this.zzjdm).getLong(this.zzjdo, 0L);
  }
  
  public final Pair<String, Long> zzaad()
  {
    this.zzjdm.zzve();
    this.zzjdm.zzve();
    long l = zzaae();
    if (l == 0L) {
      zzaac();
    }
    for (l = 0L; l < this.zzdyr; l = Math.abs(l - this.zzjdm.zzws().currentTimeMillis())) {
      return null;
    }
    if (l > this.zzdyr << 1)
    {
      zzaac();
      return null;
    }
    String str = zzchx.zza(this.zzjdm).getString(this.zzjdq, null);
    l = zzchx.zza(this.zzjdm).getLong(this.zzjdp, 0L);
    zzaac();
    if ((str == null) || (l <= 0L)) {
      return zzchx.zzjcp;
    }
    return new Pair(str, Long.valueOf(l));
  }
  
  public final void zzf(String paramString, long paramLong)
  {
    this.zzjdm.zzve();
    if (zzaae() == 0L) {
      zzaac();
    }
    String str = paramString;
    if (paramString == null) {
      str = "";
    }
    paramLong = zzchx.zza(this.zzjdm).getLong(this.zzjdp, 0L);
    if (paramLong <= 0L)
    {
      paramString = zzchx.zza(this.zzjdm).edit();
      paramString.putString(this.zzjdq, str);
      paramString.putLong(this.zzjdp, 1L);
      paramString.apply();
      return;
    }
    if ((this.zzjdm.zzawu().zzbaz().nextLong() & 0x7FFFFFFFFFFFFFFF) < Long.MAX_VALUE / (paramLong + 1L)) {}
    for (int i = 1;; i = 0)
    {
      paramString = zzchx.zza(this.zzjdm).edit();
      if (i != 0) {
        paramString.putString(this.zzjdq, str);
      }
      paramString.putLong(this.zzjdp, paramLong + 1L);
      paramString.apply();
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcib.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */