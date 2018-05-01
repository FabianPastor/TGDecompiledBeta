package com.google.android.gms.internal.measurement;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Pair;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.util.Clock;
import java.security.SecureRandom;

public final class zzfv
{
  private final long zzabe;
  private final String zzakr;
  private final String zzaks;
  private final String zzakt;
  
  private zzfv(zzfr paramzzfr, String paramString, long paramLong)
  {
    Preconditions.checkNotEmpty(paramString);
    if (paramLong > 0L) {}
    for (boolean bool = true;; bool = false)
    {
      Preconditions.checkArgument(bool);
      this.zzakr = String.valueOf(paramString).concat(":start");
      this.zzaks = String.valueOf(paramString).concat(":count");
      this.zzakt = String.valueOf(paramString).concat(":value");
      this.zzabe = paramLong;
      return;
    }
  }
  
  private final void zzfg()
  {
    this.zzakp.zzab();
    long l = this.zzakp.zzbt().currentTimeMillis();
    SharedPreferences.Editor localEditor = zzfr.zza(this.zzakp).edit();
    localEditor.remove(this.zzaks);
    localEditor.remove(this.zzakt);
    localEditor.putLong(this.zzakr, l);
    localEditor.apply();
  }
  
  private final long zzfi()
  {
    return zzfr.zza(this.zzakp).getLong(this.zzakr, 0L);
  }
  
  public final void zzc(String paramString, long paramLong)
  {
    this.zzakp.zzab();
    if (zzfi() == 0L) {
      zzfg();
    }
    String str = paramString;
    if (paramString == null) {
      str = "";
    }
    paramLong = zzfr.zza(this.zzakp).getLong(this.zzaks, 0L);
    if (paramLong <= 0L)
    {
      paramString = zzfr.zza(this.zzakp).edit();
      paramString.putString(this.zzakt, str);
      paramString.putLong(this.zzaks, 1L);
      paramString.apply();
      return;
    }
    if ((this.zzakp.zzgc().zzku().nextLong() & 0x7FFFFFFFFFFFFFFF) < Long.MAX_VALUE / (paramLong + 1L)) {}
    for (int i = 1;; i = 0)
    {
      paramString = zzfr.zza(this.zzakp).edit();
      if (i != 0) {
        paramString.putString(this.zzakt, str);
      }
      paramString.putLong(this.zzaks, paramLong + 1L);
      paramString.apply();
      break;
    }
  }
  
  public final Pair<String, Long> zzfh()
  {
    this.zzakp.zzab();
    this.zzakp.zzab();
    long l = zzfi();
    Object localObject;
    if (l == 0L)
    {
      zzfg();
      l = 0L;
      if (l >= this.zzabe) {
        break label65;
      }
      localObject = null;
    }
    for (;;)
    {
      return (Pair<String, Long>)localObject;
      l = Math.abs(l - this.zzakp.zzbt().currentTimeMillis());
      break;
      label65:
      if (l > this.zzabe << 1)
      {
        zzfg();
        localObject = null;
      }
      else
      {
        localObject = zzfr.zza(this.zzakp).getString(this.zzakt, null);
        l = zzfr.zza(this.zzakp).getLong(this.zzaks, 0L);
        zzfg();
        if ((localObject == null) || (l <= 0L)) {
          localObject = zzfr.zzajr;
        } else {
          localObject = new Pair(localObject, Long.valueOf(l));
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzfv.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */