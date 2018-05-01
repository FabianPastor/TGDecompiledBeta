package com.google.android.gms.internal;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Pair;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;
import com.google.android.gms.common.util.zzd;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Locale;

final class zzchx
  extends zzcjl
{
  static final Pair<String, Long> zzjcp = new Pair("", Long.valueOf(0L));
  private SharedPreferences zzdyn;
  public final zzcib zzjcq = new zzcib(this, "health_monitor", Math.max(0L, ((Long)zzchc.zzjaf.get()).longValue()), null);
  public final zzcia zzjcr = new zzcia(this, "last_upload", 0L);
  public final zzcia zzjcs = new zzcia(this, "last_upload_attempt", 0L);
  public final zzcia zzjct = new zzcia(this, "backoff", 0L);
  public final zzcia zzjcu = new zzcia(this, "last_delete_stale", 0L);
  public final zzcia zzjcv = new zzcia(this, "midnight_offset", 0L);
  public final zzcia zzjcw = new zzcia(this, "first_open_time", 0L);
  public final zzcic zzjcx = new zzcic(this, "app_instance_id", null);
  private String zzjcy;
  private boolean zzjcz;
  private long zzjda;
  private String zzjdb;
  private long zzjdc;
  private final Object zzjdd = new Object();
  public final zzcia zzjde = new zzcia(this, "time_before_start", 10000L);
  public final zzcia zzjdf = new zzcia(this, "session_timeout", 1800000L);
  public final zzchz zzjdg = new zzchz(this, "start_new_session", true);
  public final zzcia zzjdh = new zzcia(this, "last_pause_time", 0L);
  public final zzcia zzjdi = new zzcia(this, "time_active", 0L);
  public boolean zzjdj;
  
  zzchx(zzcim paramzzcim)
  {
    super(paramzzcim);
  }
  
  private final SharedPreferences zzazl()
  {
    zzve();
    zzxf();
    return this.zzdyn;
  }
  
  final void setMeasurementEnabled(boolean paramBoolean)
  {
    zzve();
    zzawy().zzazj().zzj("Setting measurementEnabled", Boolean.valueOf(paramBoolean));
    SharedPreferences.Editor localEditor = zzazl().edit();
    localEditor.putBoolean("measurement_enabled", paramBoolean);
    localEditor.apply();
  }
  
  protected final boolean zzaxz()
  {
    return true;
  }
  
  protected final void zzayy()
  {
    this.zzdyn = getContext().getSharedPreferences("com.google.android.gms.measurement.prefs", 0);
    this.zzjdj = this.zzdyn.getBoolean("has_been_opened", false);
    if (!this.zzjdj)
    {
      SharedPreferences.Editor localEditor = this.zzdyn.edit();
      localEditor.putBoolean("has_been_opened", true);
      localEditor.apply();
    }
  }
  
  final String zzazm()
  {
    zzve();
    return zzazl().getString("gmp_app_id", null);
  }
  
  final String zzazn()
  {
    synchronized (this.zzjdd)
    {
      if (Math.abs(zzws().elapsedRealtime() - this.zzjdc) < 1000L)
      {
        String str = this.zzjdb;
        return str;
      }
      return null;
    }
  }
  
  final Boolean zzazo()
  {
    zzve();
    if (!zzazl().contains("use_service")) {
      return null;
    }
    return Boolean.valueOf(zzazl().getBoolean("use_service", false));
  }
  
  final void zzazp()
  {
    boolean bool1 = true;
    zzve();
    zzawy().zzazj().log("Clearing collection preferences.");
    boolean bool2 = zzazl().contains("measurement_enabled");
    if (bool2) {
      bool1 = zzbn(true);
    }
    SharedPreferences.Editor localEditor = zzazl().edit();
    localEditor.clear();
    localEditor.apply();
    if (bool2) {
      setMeasurementEnabled(bool1);
    }
  }
  
  protected final String zzazq()
  {
    zzve();
    String str1 = zzazl().getString("previous_os_version", null);
    zzawo().zzxf();
    String str2 = Build.VERSION.RELEASE;
    if ((!TextUtils.isEmpty(str2)) && (!str2.equals(str1)))
    {
      SharedPreferences.Editor localEditor = zzazl().edit();
      localEditor.putString("previous_os_version", str2);
      localEditor.apply();
    }
    return str1;
  }
  
  final void zzbm(boolean paramBoolean)
  {
    zzve();
    zzawy().zzazj().zzj("Setting useService", Boolean.valueOf(paramBoolean));
    SharedPreferences.Editor localEditor = zzazl().edit();
    localEditor.putBoolean("use_service", paramBoolean);
    localEditor.apply();
  }
  
  final boolean zzbn(boolean paramBoolean)
  {
    zzve();
    return zzazl().getBoolean("measurement_enabled", paramBoolean);
  }
  
  final Pair<String, Boolean> zzjm(String paramString)
  {
    zzve();
    long l = zzws().elapsedRealtime();
    if ((this.zzjcy != null) && (l < this.zzjda)) {
      return new Pair(this.zzjcy, Boolean.valueOf(this.zzjcz));
    }
    this.zzjda = (l + zzaxa().zza(paramString, zzchc.zzjae));
    AdvertisingIdClient.setShouldSkipGmsCoreVersionCheck(true);
    try
    {
      paramString = AdvertisingIdClient.getAdvertisingIdInfo(getContext());
      if (paramString != null)
      {
        this.zzjcy = paramString.getId();
        this.zzjcz = paramString.isLimitAdTrackingEnabled();
      }
      if (this.zzjcy == null) {
        this.zzjcy = "";
      }
    }
    catch (Throwable paramString)
    {
      for (;;)
      {
        zzawy().zzazi().zzj("Unable to get advertising id", paramString);
        this.zzjcy = "";
      }
    }
    AdvertisingIdClient.setShouldSkipGmsCoreVersionCheck(false);
    return new Pair(this.zzjcy, Boolean.valueOf(this.zzjcz));
  }
  
  final String zzjn(String paramString)
  {
    zzve();
    paramString = (String)zzjm(paramString).first;
    MessageDigest localMessageDigest = zzclq.zzek("MD5");
    if (localMessageDigest == null) {
      return null;
    }
    return String.format(Locale.US, "%032X", new Object[] { new BigInteger(1, localMessageDigest.digest(paramString.getBytes())) });
  }
  
  final void zzjo(String paramString)
  {
    zzve();
    SharedPreferences.Editor localEditor = zzazl().edit();
    localEditor.putString("gmp_app_id", paramString);
    localEditor.apply();
  }
  
  final void zzjp(String paramString)
  {
    synchronized (this.zzjdd)
    {
      this.zzjdb = paramString;
      this.zzjdc = zzws().elapsedRealtime();
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzchx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */