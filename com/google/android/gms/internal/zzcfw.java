package com.google.android.gms.internal;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.Pair;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;
import com.google.android.gms.common.util.zze;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Locale;

final class zzcfw
  extends zzchj
{
  static final Pair<String, Long> zzbri = new Pair("", Long.valueOf(0L));
  private SharedPreferences zzaix;
  public final zzcfz zzbrA = new zzcfz(this, "last_pause_time", 0L);
  public final zzcfz zzbrB = new zzcfz(this, "time_active", 0L);
  public boolean zzbrC;
  public final zzcga zzbrj = new zzcga(this, "health_monitor", zzcem.zzxK(), null);
  public final zzcfz zzbrk = new zzcfz(this, "last_upload", 0L);
  public final zzcfz zzbrl = new zzcfz(this, "last_upload_attempt", 0L);
  public final zzcfz zzbrm = new zzcfz(this, "backoff", 0L);
  public final zzcfz zzbrn = new zzcfz(this, "last_delete_stale", 0L);
  public final zzcfz zzbro = new zzcfz(this, "midnight_offset", 0L);
  public final zzcfz zzbrp = new zzcfz(this, "first_open_time", 0L);
  public final zzcgb zzbrq = new zzcgb(this, "app_instance_id", null);
  private String zzbrr;
  private boolean zzbrs;
  private long zzbrt;
  private String zzbru;
  private long zzbrv;
  private final Object zzbrw = new Object();
  public final zzcfz zzbrx = new zzcfz(this, "time_before_start", 10000L);
  public final zzcfz zzbry = new zzcfz(this, "session_timeout", 1800000L);
  public final zzcfy zzbrz = new zzcfy(this, "start_new_session", true);
  
  zzcfw(zzcgl paramzzcgl)
  {
    super(paramzzcgl);
  }
  
  @WorkerThread
  private final SharedPreferences zzyF()
  {
    zzjC();
    zzkD();
    return this.zzaix;
  }
  
  @WorkerThread
  final void setMeasurementEnabled(boolean paramBoolean)
  {
    zzjC();
    zzwF().zzyD().zzj("Setting measurementEnabled", Boolean.valueOf(paramBoolean));
    SharedPreferences.Editor localEditor = zzyF().edit();
    localEditor.putBoolean("measurement_enabled", paramBoolean);
    localEditor.apply();
  }
  
  @WorkerThread
  final void zzak(boolean paramBoolean)
  {
    zzjC();
    zzwF().zzyD().zzj("Setting useService", Boolean.valueOf(paramBoolean));
    SharedPreferences.Editor localEditor = zzyF().edit();
    localEditor.putBoolean("use_service", paramBoolean);
    localEditor.apply();
  }
  
  @WorkerThread
  final boolean zzal(boolean paramBoolean)
  {
    zzjC();
    return zzyF().getBoolean("measurement_enabled", paramBoolean);
  }
  
  @NonNull
  @WorkerThread
  final Pair<String, Boolean> zzeb(String paramString)
  {
    zzjC();
    long l = zzkq().elapsedRealtime();
    if ((this.zzbrr != null) && (l < this.zzbrt)) {
      return new Pair(this.zzbrr, Boolean.valueOf(this.zzbrs));
    }
    this.zzbrt = (l + zzwH().zza(paramString, zzcfb.zzbpW));
    AdvertisingIdClient.setShouldSkipGmsCoreVersionCheck(true);
    try
    {
      paramString = AdvertisingIdClient.getAdvertisingIdInfo(getContext());
      if (paramString != null)
      {
        this.zzbrr = paramString.getId();
        this.zzbrs = paramString.isLimitAdTrackingEnabled();
      }
      if (this.zzbrr == null) {
        this.zzbrr = "";
      }
    }
    catch (Throwable paramString)
    {
      for (;;)
      {
        zzwF().zzyC().zzj("Unable to get advertising id", paramString);
        this.zzbrr = "";
      }
    }
    AdvertisingIdClient.setShouldSkipGmsCoreVersionCheck(false);
    return new Pair(this.zzbrr, Boolean.valueOf(this.zzbrs));
  }
  
  @WorkerThread
  final String zzec(String paramString)
  {
    zzjC();
    paramString = (String)zzeb(paramString).first;
    MessageDigest localMessageDigest = zzcjl.zzbE("MD5");
    if (localMessageDigest == null) {
      return null;
    }
    return String.format(Locale.US, "%032X", new Object[] { new BigInteger(1, localMessageDigest.digest(paramString.getBytes())) });
  }
  
  @WorkerThread
  final void zzed(String paramString)
  {
    zzjC();
    SharedPreferences.Editor localEditor = zzyF().edit();
    localEditor.putString("gmp_app_id", paramString);
    localEditor.apply();
  }
  
  final void zzee(String paramString)
  {
    synchronized (this.zzbrw)
    {
      this.zzbru = paramString;
      this.zzbrv = zzkq().elapsedRealtime();
      return;
    }
  }
  
  protected final void zzjD()
  {
    this.zzaix = getContext().getSharedPreferences("com.google.android.gms.measurement.prefs", 0);
    this.zzbrC = this.zzaix.getBoolean("has_been_opened", false);
    if (!this.zzbrC)
    {
      SharedPreferences.Editor localEditor = this.zzaix.edit();
      localEditor.putBoolean("has_been_opened", true);
      localEditor.apply();
    }
  }
  
  @WorkerThread
  final String zzyG()
  {
    zzjC();
    return zzyF().getString("gmp_app_id", null);
  }
  
  final String zzyH()
  {
    synchronized (this.zzbrw)
    {
      if (Math.abs(zzkq().elapsedRealtime() - this.zzbrv) < 1000L)
      {
        String str = this.zzbru;
        return str;
      }
      return null;
    }
  }
  
  @WorkerThread
  final Boolean zzyI()
  {
    zzjC();
    if (!zzyF().contains("use_service")) {
      return null;
    }
    return Boolean.valueOf(zzyF().getBoolean("use_service", false));
  }
  
  @WorkerThread
  final void zzyJ()
  {
    boolean bool1 = true;
    zzjC();
    zzwF().zzyD().log("Clearing collection preferences.");
    boolean bool2 = zzyF().contains("measurement_enabled");
    if (bool2) {
      bool1 = zzal(true);
    }
    SharedPreferences.Editor localEditor = zzyF().edit();
    localEditor.clear();
    localEditor.apply();
    if (bool2) {
      setMeasurementEnabled(bool1);
    }
  }
  
  @WorkerThread
  protected final String zzyK()
  {
    zzjC();
    String str1 = zzyF().getString("previous_os_version", null);
    zzwv().zzkD();
    String str2 = Build.VERSION.RELEASE;
    if ((!TextUtils.isEmpty(str2)) && (!str2.equals(str1)))
    {
      SharedPreferences.Editor localEditor = zzyF().edit();
      localEditor.putString("previous_os_version", str2);
      localEditor.apply();
    }
    return str1;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcfw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */