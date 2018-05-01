package com.google.android.gms.internal;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.WorkerThread;
import android.util.Pair;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.util.zze;
import java.security.SecureRandom;

public final class zzcga
{
  private final long zzaiB;
  private String zzbrH;
  private final String zzbrI;
  private final String zzbrJ;
  
  private zzcga(zzcfw paramzzcfw, String paramString, long paramLong)
  {
    zzbo.zzcF(paramString);
    if (paramLong > 0L) {}
    for (boolean bool = true;; bool = false)
    {
      zzbo.zzaf(bool);
      this.zzbrH = String.valueOf(paramString).concat(":start");
      this.zzbrI = String.valueOf(paramString).concat(":count");
      this.zzbrJ = String.valueOf(paramString).concat(":value");
      this.zzaiB = paramLong;
      return;
    }
  }
  
  @WorkerThread
  private final void zzma()
  {
    this.zzbrF.zzjC();
    long l = this.zzbrF.zzkq().currentTimeMillis();
    SharedPreferences.Editor localEditor = zzcfw.zza(this.zzbrF).edit();
    localEditor.remove(this.zzbrI);
    localEditor.remove(this.zzbrJ);
    localEditor.putLong(this.zzbrH, l);
    localEditor.apply();
  }
  
  @WorkerThread
  private final long zzmc()
  {
    return zzcfw.zzb(this.zzbrF).getLong(this.zzbrH, 0L);
  }
  
  @WorkerThread
  public final void zzf(String paramString, long paramLong)
  {
    this.zzbrF.zzjC();
    if (zzmc() == 0L) {
      zzma();
    }
    String str = paramString;
    if (paramString == null) {
      str = "";
    }
    paramLong = zzcfw.zza(this.zzbrF).getLong(this.zzbrI, 0L);
    if (paramLong <= 0L)
    {
      paramString = zzcfw.zza(this.zzbrF).edit();
      paramString.putString(this.zzbrJ, str);
      paramString.putLong(this.zzbrI, 1L);
      paramString.apply();
      return;
    }
    if ((this.zzbrF.zzwB().zzzt().nextLong() & 0x7FFFFFFFFFFFFFFF) < Long.MAX_VALUE / (paramLong + 1L)) {}
    for (int i = 1;; i = 0)
    {
      paramString = zzcfw.zza(this.zzbrF).edit();
      if (i != 0) {
        paramString.putString(this.zzbrJ, str);
      }
      paramString.putLong(this.zzbrI, paramLong + 1L);
      paramString.apply();
      return;
    }
  }
  
  @WorkerThread
  public final Pair<String, Long> zzmb()
  {
    this.zzbrF.zzjC();
    this.zzbrF.zzjC();
    long l = zzmc();
    if (l == 0L) {
      zzma();
    }
    for (l = 0L; l < this.zzaiB; l = Math.abs(l - this.zzbrF.zzkq().currentTimeMillis())) {
      return null;
    }
    if (l > this.zzaiB << 1)
    {
      zzma();
      return null;
    }
    String str = zzcfw.zzb(this.zzbrF).getString(this.zzbrJ, null);
    l = zzcfw.zzb(this.zzbrF).getLong(this.zzbrI, 0L);
    zzma();
    if ((str == null) || (l <= 0L)) {
      return zzcfw.zzbri;
    }
    return new Pair(str, Long.valueOf(l));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcga.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */