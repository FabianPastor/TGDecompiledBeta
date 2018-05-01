package com.google.android.gms.internal.measurement;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Pair;
import com.google.android.gms.common.util.Clock;

final class zzfr
  extends zzhk
{
  static final Pair<String, Long> zzajr = new Pair("", Long.valueOf(0L));
  private SharedPreferences zzaba;
  public zzfv zzajs;
  public final zzfu zzajt = new zzfu(this, "last_upload", 0L);
  public final zzfu zzaju = new zzfu(this, "last_upload_attempt", 0L);
  public final zzfu zzajv = new zzfu(this, "backoff", 0L);
  public final zzfu zzajw = new zzfu(this, "last_delete_stale", 0L);
  public final zzfu zzajx = new zzfu(this, "midnight_offset", 0L);
  public final zzfu zzajy = new zzfu(this, "first_open_time", 0L);
  public final zzfu zzajz = new zzfu(this, "app_install_time", 0L);
  public final zzfw zzaka = new zzfw(this, "app_instance_id", null);
  private String zzake;
  private long zzakf;
  private final Object zzakg = new Object();
  public final zzfu zzakh = new zzfu(this, "time_before_start", 10000L);
  public final zzfu zzaki = new zzfu(this, "session_timeout", 1800000L);
  public final zzft zzakj = new zzft(this, "start_new_session", true);
  public final zzfu zzakk = new zzfu(this, "last_pause_time", 0L);
  public final zzfu zzakl = new zzfu(this, "time_active", 0L);
  public boolean zzakm;
  
  zzfr(zzgl paramzzgl)
  {
    super(paramzzgl);
  }
  
  private final SharedPreferences zziu()
  {
    zzab();
    zzch();
    return this.zzaba;
  }
  
  final void setMeasurementEnabled(boolean paramBoolean)
  {
    zzab();
    zzgg().zzir().zzg("Setting measurementEnabled", Boolean.valueOf(paramBoolean));
    SharedPreferences.Editor localEditor = zziu().edit();
    localEditor.putBoolean("measurement_enabled", paramBoolean);
    localEditor.apply();
  }
  
  final void zzbl(String paramString)
  {
    zzab();
    SharedPreferences.Editor localEditor = zziu().edit();
    localEditor.putString("gmp_app_id", paramString);
    localEditor.apply();
  }
  
  final void zzbm(String paramString)
  {
    synchronized (this.zzakg)
    {
      this.zzake = paramString;
      this.zzakf = zzbt().elapsedRealtime();
      return;
    }
  }
  
  final void zzf(boolean paramBoolean)
  {
    zzab();
    zzgg().zzir().zzg("Setting useService", Boolean.valueOf(paramBoolean));
    SharedPreferences.Editor localEditor = zziu().edit();
    localEditor.putBoolean("use_service", paramBoolean);
    localEditor.apply();
  }
  
  final boolean zzg(boolean paramBoolean)
  {
    zzab();
    return zziu().getBoolean("measurement_enabled", paramBoolean);
  }
  
  final void zzh(boolean paramBoolean)
  {
    zzab();
    zzgg().zzir().zzg("Updating deferred analytics collection", Boolean.valueOf(paramBoolean));
    SharedPreferences.Editor localEditor = zziu().edit();
    localEditor.putBoolean("deferred_analytics_collection", paramBoolean);
    localEditor.apply();
  }
  
  protected final boolean zzhh()
  {
    return true;
  }
  
  protected final void zzig()
  {
    this.zzaba = getContext().getSharedPreferences("com.google.android.gms.measurement.prefs", 0);
    this.zzakm = this.zzaba.getBoolean("has_been_opened", false);
    if (!this.zzakm)
    {
      SharedPreferences.Editor localEditor = this.zzaba.edit();
      localEditor.putBoolean("has_been_opened", true);
      localEditor.apply();
    }
    this.zzajs = new zzfv(this, "health_monitor", Math.max(0L, ((Long)zzew.zzagn.get()).longValue()), null);
  }
  
  final String zziv()
  {
    zzab();
    return zziu().getString("gmp_app_id", null);
  }
  
  final String zziw()
  {
    synchronized (this.zzakg)
    {
      if (Math.abs(zzbt().elapsedRealtime() - this.zzakf) < 1000L)
      {
        str = this.zzake;
        return str;
      }
      String str = null;
    }
  }
  
  final Boolean zzix()
  {
    zzab();
    if (!zziu().contains("use_service")) {}
    for (Boolean localBoolean = null;; localBoolean = Boolean.valueOf(zziu().getBoolean("use_service", false))) {
      return localBoolean;
    }
  }
  
  final void zziy()
  {
    boolean bool1 = true;
    zzab();
    zzgg().zzir().log("Clearing collection preferences.");
    boolean bool2 = zziu().contains("measurement_enabled");
    if (bool2) {
      bool1 = zzg(true);
    }
    SharedPreferences.Editor localEditor = zziu().edit();
    localEditor.clear();
    localEditor.apply();
    if (bool2) {
      setMeasurementEnabled(bool1);
    }
  }
  
  protected final String zziz()
  {
    zzab();
    String str1 = zziu().getString("previous_os_version", null);
    zzfw().zzch();
    String str2 = Build.VERSION.RELEASE;
    if ((!TextUtils.isEmpty(str2)) && (!str2.equals(str1)))
    {
      SharedPreferences.Editor localEditor = zziu().edit();
      localEditor.putString("previous_os_version", str2);
      localEditor.apply();
    }
    return str1;
  }
  
  final boolean zzja()
  {
    zzab();
    return zziu().getBoolean("deferred_analytics_collection", false);
  }
  
  final boolean zzjb()
  {
    return this.zzaba.contains("deferred_analytics_collection");
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzfr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */