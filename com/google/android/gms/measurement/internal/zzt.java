package com.google.android.gms.measurement.internal;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.Pair;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zze;
import com.google.firebase.iid.zzc;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Locale;

class zzt
  extends zzaa
{
  static final Pair<String, Long> apO = new Pair("", Long.valueOf(0L));
  public final zzc apP = new zzc("health_monitor", zzbvi().zzadz(), null);
  public final zzb apQ = new zzb("last_upload", 0L);
  public final zzb apR = new zzb("last_upload_attempt", 0L);
  public final zzb apS = new zzb("backoff", 0L);
  public final zzb apT = new zzb("last_delete_stale", 0L);
  public final zzb apU = new zzb("midnight_offset", 0L);
  private String apV;
  private boolean apW;
  private long apX;
  private SecureRandom apY;
  public final zzb apZ = new zzb("time_before_start", 10000L);
  public final zzb aqa = new zzb("session_timeout", 1800000L);
  public final zza aqb = new zza("start_new_session", true);
  public final zzb aqc = new zzb("last_pause_time", 0L);
  public final zzb aqd = new zzb("time_active", 0L);
  public boolean aqe;
  private SharedPreferences dy;
  
  zzt(zzx paramzzx)
  {
    super(paramzzx);
  }
  
  @WorkerThread
  private SecureRandom zzbwl()
  {
    zzyl();
    if (this.apY == null) {
      this.apY = new SecureRandom();
    }
    return this.apY;
  }
  
  @WorkerThread
  private SharedPreferences zzbwo()
  {
    zzyl();
    zzaax();
    return this.dy;
  }
  
  @WorkerThread
  void setMeasurementEnabled(boolean paramBoolean)
  {
    zzyl();
    zzbvg().zzbwj().zzj("Setting measurementEnabled", Boolean.valueOf(paramBoolean));
    SharedPreferences.Editor localEditor = zzbwo().edit();
    localEditor.putBoolean("measurement_enabled", paramBoolean);
    localEditor.apply();
  }
  
  @WorkerThread
  String zzbst()
  {
    zzyl();
    try
    {
      String str = zzc.A().getId();
      return str;
    }
    catch (IllegalStateException localIllegalStateException)
    {
      zzbvg().zzbwe().log("Failed to retrieve Firebase Instance Id");
    }
    return null;
  }
  
  @WorkerThread
  String zzbwm()
  {
    byte[] arrayOfByte = new byte[16];
    zzbwl().nextBytes(arrayOfByte);
    return String.format(Locale.US, "%032x", new Object[] { new BigInteger(1, arrayOfByte) });
  }
  
  @WorkerThread
  long zzbwn()
  {
    zzaax();
    zzyl();
    long l2 = this.apU.get();
    long l1 = l2;
    if (l2 == 0L)
    {
      l1 = zzbwl().nextInt(86400000) + 1;
      this.apU.set(l1);
    }
    return l1;
  }
  
  @WorkerThread
  String zzbwp()
  {
    zzyl();
    return zzbwo().getString("gmp_app_id", null);
  }
  
  @WorkerThread
  Boolean zzbwq()
  {
    zzyl();
    if (!zzbwo().contains("use_service")) {
      return null;
    }
    return Boolean.valueOf(zzbwo().getBoolean("use_service", false));
  }
  
  @WorkerThread
  void zzbwr()
  {
    boolean bool1 = true;
    zzyl();
    zzbvg().zzbwj().log("Clearing collection preferences.");
    boolean bool2 = zzbwo().contains("measurement_enabled");
    if (bool2) {
      bool1 = zzcg(true);
    }
    SharedPreferences.Editor localEditor = zzbwo().edit();
    localEditor.clear();
    localEditor.apply();
    if (bool2) {
      setMeasurementEnabled(bool1);
    }
  }
  
  @WorkerThread
  protected String zzbws()
  {
    zzyl();
    String str1 = zzbwo().getString("previous_os_version", null);
    String str2 = zzbuz().zzbvv();
    if ((!TextUtils.isEmpty(str2)) && (!str2.equals(str1)))
    {
      SharedPreferences.Editor localEditor = zzbwo().edit();
      localEditor.putString("previous_os_version", str2);
      localEditor.apply();
    }
    return str1;
  }
  
  @WorkerThread
  void zzcf(boolean paramBoolean)
  {
    zzyl();
    zzbvg().zzbwj().zzj("Setting useService", Boolean.valueOf(paramBoolean));
    SharedPreferences.Editor localEditor = zzbwo().edit();
    localEditor.putBoolean("use_service", paramBoolean);
    localEditor.apply();
  }
  
  @WorkerThread
  boolean zzcg(boolean paramBoolean)
  {
    zzyl();
    return zzbwo().getBoolean("measurement_enabled", paramBoolean);
  }
  
  @NonNull
  @WorkerThread
  Pair<String, Boolean> zzml(String paramString)
  {
    zzyl();
    long l = zzaan().elapsedRealtime();
    if ((this.apV != null) && (l < this.apX)) {
      return new Pair(this.apV, Boolean.valueOf(this.apW));
    }
    this.apX = (l + zzbvi().zzlr(paramString));
    AdvertisingIdClient.setShouldSkipGmsCoreVersionCheck(true);
    try
    {
      paramString = AdvertisingIdClient.getAdvertisingIdInfo(getContext());
      this.apV = paramString.getId();
      if (this.apV == null) {
        this.apV = "";
      }
      this.apW = paramString.isLimitAdTrackingEnabled();
    }
    catch (Throwable paramString)
    {
      for (;;)
      {
        zzbvg().zzbwi().zzj("Unable to get advertising id", paramString);
        this.apV = "";
      }
    }
    AdvertisingIdClient.setShouldSkipGmsCoreVersionCheck(false);
    return new Pair(this.apV, Boolean.valueOf(this.apW));
  }
  
  String zzmm(String paramString)
  {
    paramString = (String)zzml(paramString).first;
    MessageDigest localMessageDigest = zzal.zzfi("MD5");
    if (localMessageDigest == null) {
      return null;
    }
    return String.format(Locale.US, "%032X", new Object[] { new BigInteger(1, localMessageDigest.digest(paramString.getBytes())) });
  }
  
  @WorkerThread
  void zzmn(String paramString)
  {
    zzyl();
    SharedPreferences.Editor localEditor = zzbwo().edit();
    localEditor.putString("gmp_app_id", paramString);
    localEditor.apply();
  }
  
  protected void zzym()
  {
    this.dy = getContext().getSharedPreferences("com.google.android.gms.measurement.prefs", 0);
    this.aqe = this.dy.getBoolean("has_been_opened", false);
    if (!this.aqe)
    {
      SharedPreferences.Editor localEditor = this.dy.edit();
      localEditor.putBoolean("has_been_opened", true);
      localEditor.apply();
    }
  }
  
  public final class zza
  {
    private final boolean aqf;
    private boolean aqg;
    private boolean vu;
    private final String zzbaf;
    
    public zza(String paramString, boolean paramBoolean)
    {
      zzac.zzhz(paramString);
      this.zzbaf = paramString;
      this.aqf = paramBoolean;
    }
    
    @WorkerThread
    private void zzbwt()
    {
      if (this.aqg) {
        return;
      }
      this.aqg = true;
      this.vu = zzt.zza(zzt.this).getBoolean(this.zzbaf, this.aqf);
    }
    
    @WorkerThread
    public boolean get()
    {
      zzbwt();
      return this.vu;
    }
    
    @WorkerThread
    public void set(boolean paramBoolean)
    {
      SharedPreferences.Editor localEditor = zzt.zza(zzt.this).edit();
      localEditor.putBoolean(this.zzbaf, paramBoolean);
      localEditor.apply();
      this.vu = paramBoolean;
    }
  }
  
  public final class zzb
  {
    private long X;
    private boolean aqg;
    private final long aqi;
    private final String zzbaf;
    
    public zzb(String paramString, long paramLong)
    {
      zzac.zzhz(paramString);
      this.zzbaf = paramString;
      this.aqi = paramLong;
    }
    
    @WorkerThread
    private void zzbwt()
    {
      if (this.aqg) {
        return;
      }
      this.aqg = true;
      this.X = zzt.zza(zzt.this).getLong(this.zzbaf, this.aqi);
    }
    
    @WorkerThread
    public long get()
    {
      zzbwt();
      return this.X;
    }
    
    @WorkerThread
    public void set(long paramLong)
    {
      SharedPreferences.Editor localEditor = zzt.zza(zzt.this).edit();
      localEditor.putLong(this.zzbaf, paramLong);
      localEditor.apply();
      this.X = paramLong;
    }
  }
  
  public final class zzc
  {
    final String aqj;
    private final String aqk;
    private final String aql;
    private final long dC;
    
    private zzc(String paramString, long paramLong)
    {
      zzac.zzhz(paramString);
      if (paramLong > 0L) {}
      for (boolean bool = true;; bool = false)
      {
        zzac.zzbs(bool);
        this.aqj = String.valueOf(paramString).concat(":start");
        this.aqk = String.valueOf(paramString).concat(":count");
        this.aql = String.valueOf(paramString).concat(":value");
        this.dC = paramLong;
        return;
      }
    }
    
    @WorkerThread
    private void zzafk()
    {
      zzt.this.zzyl();
      long l = zzt.this.zzaan().currentTimeMillis();
      SharedPreferences.Editor localEditor = zzt.zza(zzt.this).edit();
      localEditor.remove(this.aqk);
      localEditor.remove(this.aql);
      localEditor.putLong(this.aqj, l);
      localEditor.apply();
    }
    
    @WorkerThread
    private long zzafl()
    {
      zzt.this.zzyl();
      long l = zzafn();
      if (l == 0L)
      {
        zzafk();
        return 0L;
      }
      return Math.abs(l - zzt.this.zzaan().currentTimeMillis());
    }
    
    @WorkerThread
    private long zzafn()
    {
      return zzt.zzc(zzt.this).getLong(this.aqj, 0L);
    }
    
    @WorkerThread
    public Pair<String, Long> zzafm()
    {
      zzt.this.zzyl();
      long l = zzafl();
      if (l < this.dC) {
        return null;
      }
      if (l > this.dC * 2L)
      {
        zzafk();
        return null;
      }
      String str = zzt.zzc(zzt.this).getString(this.aql, null);
      l = zzt.zzc(zzt.this).getLong(this.aqk, 0L);
      zzafk();
      if ((str == null) || (l <= 0L)) {
        return zzt.apO;
      }
      return new Pair(str, Long.valueOf(l));
    }
    
    @WorkerThread
    public void zzfd(String paramString)
    {
      zzi(paramString, 1L);
    }
    
    @WorkerThread
    public void zzi(String paramString, long paramLong)
    {
      zzt.this.zzyl();
      if (zzafn() == 0L) {
        zzafk();
      }
      String str = paramString;
      if (paramString == null) {
        str = "";
      }
      long l = zzt.zza(zzt.this).getLong(this.aqk, 0L);
      if (l <= 0L)
      {
        paramString = zzt.zza(zzt.this).edit();
        paramString.putString(this.aql, str);
        paramString.putLong(this.aqk, paramLong);
        paramString.apply();
        return;
      }
      if ((zzt.zzb(zzt.this).nextLong() & 0x7FFFFFFFFFFFFFFF) < Long.MAX_VALUE / (l + paramLong) * paramLong) {}
      for (int i = 1;; i = 0)
      {
        paramString = zzt.zza(zzt.this).edit();
        if (i != 0) {
          paramString.putString(this.aql, str);
        }
        paramString.putLong(this.aqk, l + paramLong);
        paramString.apply();
        return;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */