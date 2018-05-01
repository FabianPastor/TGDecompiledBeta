package com.google.android.gms.internal;

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

class zzaua
  extends zzauh
{
  static final Pair<String, Long> zzbsZ = new Pair("", Long.valueOf(0L));
  private SharedPreferences zzagD;
  public final zzc zzbta = new zzc("health_monitor", zzKn().zzpz(), null);
  public final zzb zzbtb = new zzb("last_upload", 0L);
  public final zzb zzbtc = new zzb("last_upload_attempt", 0L);
  public final zzb zzbtd = new zzb("backoff", 0L);
  public final zzb zzbte = new zzb("last_delete_stale", 0L);
  public final zzb zzbtf = new zzb("midnight_offset", 0L);
  private String zzbtg;
  private boolean zzbth;
  private long zzbti;
  private String zzbtj;
  private long zzbtk;
  private final Object zzbtl = new Object();
  private SecureRandom zzbtm;
  public final zzb zzbtn = new zzb("time_before_start", 10000L);
  public final zzb zzbto = new zzb("session_timeout", 1800000L);
  public final zza zzbtp = new zza("start_new_session", true);
  public final zzb zzbtq = new zzb("last_pause_time", 0L);
  public final zzb zzbtr = new zzb("time_active", 0L);
  public boolean zzbts;
  
  zzaua(zzaue paramzzaue)
  {
    super(paramzzaue);
  }
  
  @WorkerThread
  private SharedPreferences zzMk()
  {
    zzmR();
    zzob();
    return this.zzagD;
  }
  
  @WorkerThread
  void setMeasurementEnabled(boolean paramBoolean)
  {
    zzmR();
    zzKl().zzMf().zzj("Setting measurementEnabled", Boolean.valueOf(paramBoolean));
    SharedPreferences.Editor localEditor = zzMk().edit();
    localEditor.putBoolean("measurement_enabled", paramBoolean);
    localEditor.apply();
  }
  
  @WorkerThread
  String zzKq()
  {
    zzmR();
    try
    {
      String str = zzc.zzabN().getId();
      return str;
    }
    catch (IllegalStateException localIllegalStateException)
    {
      zzKl().zzMb().log("Failed to retrieve Firebase Instance Id");
    }
    return null;
  }
  
  @WorkerThread
  protected SecureRandom zzMh()
  {
    zzmR();
    if (this.zzbtm == null) {
      this.zzbtm = new SecureRandom();
    }
    return this.zzbtm;
  }
  
  @WorkerThread
  String zzMi()
  {
    byte[] arrayOfByte = new byte[16];
    zzMh().nextBytes(arrayOfByte);
    return String.format(Locale.US, "%032x", new Object[] { new BigInteger(1, arrayOfByte) });
  }
  
  @WorkerThread
  long zzMj()
  {
    zzob();
    zzmR();
    long l2 = this.zzbtf.get();
    long l1 = l2;
    if (l2 == 0L)
    {
      l1 = zzMh().nextInt(86400000) + 1;
      this.zzbtf.set(l1);
    }
    return l1;
  }
  
  @WorkerThread
  String zzMl()
  {
    zzmR();
    return zzMk().getString("gmp_app_id", null);
  }
  
  String zzMm()
  {
    synchronized (this.zzbtl)
    {
      if (Math.abs(zznR().elapsedRealtime() - this.zzbtk) < 1000L)
      {
        String str = this.zzbtj;
        return str;
      }
      return null;
    }
  }
  
  @WorkerThread
  Boolean zzMn()
  {
    zzmR();
    if (!zzMk().contains("use_service")) {
      return null;
    }
    return Boolean.valueOf(zzMk().getBoolean("use_service", false));
  }
  
  @WorkerThread
  void zzMo()
  {
    boolean bool1 = true;
    zzmR();
    zzKl().zzMf().log("Clearing collection preferences.");
    boolean bool2 = zzMk().contains("measurement_enabled");
    if (bool2) {
      bool1 = zzaK(true);
    }
    SharedPreferences.Editor localEditor = zzMk().edit();
    localEditor.clear();
    localEditor.apply();
    if (bool2) {
      setMeasurementEnabled(bool1);
    }
  }
  
  @WorkerThread
  protected String zzMp()
  {
    zzmR();
    String str1 = zzMk().getString("previous_os_version", null);
    String str2 = zzKc().zzLS();
    if ((!TextUtils.isEmpty(str2)) && (!str2.equals(str1)))
    {
      SharedPreferences.Editor localEditor = zzMk().edit();
      localEditor.putString("previous_os_version", str2);
      localEditor.apply();
    }
    return str1;
  }
  
  @WorkerThread
  void zzaJ(boolean paramBoolean)
  {
    zzmR();
    zzKl().zzMf().zzj("Setting useService", Boolean.valueOf(paramBoolean));
    SharedPreferences.Editor localEditor = zzMk().edit();
    localEditor.putBoolean("use_service", paramBoolean);
    localEditor.apply();
  }
  
  @WorkerThread
  boolean zzaK(boolean paramBoolean)
  {
    zzmR();
    return zzMk().getBoolean("measurement_enabled", paramBoolean);
  }
  
  @NonNull
  @WorkerThread
  Pair<String, Boolean> zzfG(String paramString)
  {
    zzmR();
    long l = zznR().elapsedRealtime();
    if ((this.zzbtg != null) && (l < this.zzbti)) {
      return new Pair(this.zzbtg, Boolean.valueOf(this.zzbth));
    }
    this.zzbti = (l + zzKn().zzfm(paramString));
    AdvertisingIdClient.setShouldSkipGmsCoreVersionCheck(true);
    try
    {
      paramString = AdvertisingIdClient.getAdvertisingIdInfo(getContext());
      this.zzbtg = paramString.getId();
      if (this.zzbtg == null) {
        this.zzbtg = "";
      }
      this.zzbth = paramString.isLimitAdTrackingEnabled();
    }
    catch (Throwable paramString)
    {
      for (;;)
      {
        zzKl().zzMe().zzj("Unable to get advertising id", paramString);
        this.zzbtg = "";
      }
    }
    AdvertisingIdClient.setShouldSkipGmsCoreVersionCheck(false);
    return new Pair(this.zzbtg, Boolean.valueOf(this.zzbth));
  }
  
  @WorkerThread
  String zzfH(String paramString)
  {
    zzmR();
    paramString = (String)zzfG(paramString).first;
    MessageDigest localMessageDigest = zzaut.zzch("MD5");
    if (localMessageDigest == null) {
      return null;
    }
    return String.format(Locale.US, "%032X", new Object[] { new BigInteger(1, localMessageDigest.digest(paramString.getBytes())) });
  }
  
  @WorkerThread
  void zzfI(String paramString)
  {
    zzmR();
    SharedPreferences.Editor localEditor = zzMk().edit();
    localEditor.putString("gmp_app_id", paramString);
    localEditor.apply();
  }
  
  void zzfJ(String paramString)
  {
    synchronized (this.zzbtl)
    {
      this.zzbtj = paramString;
      this.zzbtk = zznR().elapsedRealtime();
      return;
    }
  }
  
  protected void zzmS()
  {
    this.zzagD = getContext().getSharedPreferences("com.google.android.gms.measurement.prefs", 0);
    this.zzbts = this.zzagD.getBoolean("has_been_opened", false);
    if (!this.zzbts)
    {
      SharedPreferences.Editor localEditor = this.zzagD.edit();
      localEditor.putBoolean("has_been_opened", true);
      localEditor.apply();
    }
  }
  
  public final class zza
  {
    private final String zzAX;
    private boolean zzayS;
    private final boolean zzbtt;
    private boolean zzbtu;
    
    public zza(String paramString, boolean paramBoolean)
    {
      zzac.zzdr(paramString);
      this.zzAX = paramString;
      this.zzbtt = paramBoolean;
    }
    
    @WorkerThread
    private void zzMq()
    {
      if (this.zzbtu) {
        return;
      }
      this.zzbtu = true;
      this.zzayS = zzaua.zza(zzaua.this).getBoolean(this.zzAX, this.zzbtt);
    }
    
    @WorkerThread
    public boolean get()
    {
      zzMq();
      return this.zzayS;
    }
    
    @WorkerThread
    public void set(boolean paramBoolean)
    {
      SharedPreferences.Editor localEditor = zzaua.zza(zzaua.this).edit();
      localEditor.putBoolean(this.zzAX, paramBoolean);
      localEditor.apply();
      this.zzayS = paramBoolean;
    }
  }
  
  public final class zzb
  {
    private final String zzAX;
    private long zzadd;
    private boolean zzbtu;
    private final long zzbtw;
    
    public zzb(String paramString, long paramLong)
    {
      zzac.zzdr(paramString);
      this.zzAX = paramString;
      this.zzbtw = paramLong;
    }
    
    @WorkerThread
    private void zzMq()
    {
      if (this.zzbtu) {
        return;
      }
      this.zzbtu = true;
      this.zzadd = zzaua.zza(zzaua.this).getLong(this.zzAX, this.zzbtw);
    }
    
    @WorkerThread
    public long get()
    {
      zzMq();
      return this.zzadd;
    }
    
    @WorkerThread
    public void set(long paramLong)
    {
      SharedPreferences.Editor localEditor = zzaua.zza(zzaua.this).edit();
      localEditor.putLong(this.zzAX, paramLong);
      localEditor.apply();
      this.zzadd = paramLong;
    }
  }
  
  public final class zzc
  {
    private final long zzagH;
    final String zzbtx;
    private final String zzbty;
    private final String zzbtz;
    
    private zzc(String paramString, long paramLong)
    {
      zzac.zzdr(paramString);
      if (paramLong > 0L) {}
      for (boolean bool = true;; bool = false)
      {
        zzac.zzaw(bool);
        this.zzbtx = String.valueOf(paramString).concat(":start");
        this.zzbty = String.valueOf(paramString).concat(":count");
        this.zzbtz = String.valueOf(paramString).concat(":value");
        this.zzagH = paramLong;
        return;
      }
    }
    
    @WorkerThread
    private void zzqk()
    {
      zzaua.this.zzmR();
      long l = zzaua.this.zznR().currentTimeMillis();
      SharedPreferences.Editor localEditor = zzaua.zza(zzaua.this).edit();
      localEditor.remove(this.zzbty);
      localEditor.remove(this.zzbtz);
      localEditor.putLong(this.zzbtx, l);
      localEditor.apply();
    }
    
    @WorkerThread
    private long zzql()
    {
      zzaua.this.zzmR();
      long l = zzqn();
      if (l == 0L)
      {
        zzqk();
        return 0L;
      }
      return Math.abs(l - zzaua.this.zznR().currentTimeMillis());
    }
    
    @WorkerThread
    private long zzqn()
    {
      return zzaua.zzb(zzaua.this).getLong(this.zzbtx, 0L);
    }
    
    @WorkerThread
    public void zzcc(String paramString)
    {
      zzk(paramString, 1L);
    }
    
    @WorkerThread
    public void zzk(String paramString, long paramLong)
    {
      zzaua.this.zzmR();
      if (zzqn() == 0L) {
        zzqk();
      }
      String str = paramString;
      if (paramString == null) {
        str = "";
      }
      long l = zzaua.zza(zzaua.this).getLong(this.zzbty, 0L);
      if (l <= 0L)
      {
        paramString = zzaua.zza(zzaua.this).edit();
        paramString.putString(this.zzbtz, str);
        paramString.putLong(this.zzbty, paramLong);
        paramString.apply();
        return;
      }
      if ((zzaua.this.zzMh().nextLong() & 0x7FFFFFFFFFFFFFFF) < Long.MAX_VALUE / (l + paramLong) * paramLong) {}
      for (int i = 1;; i = 0)
      {
        paramString = zzaua.zza(zzaua.this).edit();
        if (i != 0) {
          paramString.putString(this.zzbtz, str);
        }
        paramString.putLong(this.zzbty, l + paramLong);
        paramString.apply();
        return;
      }
    }
    
    @WorkerThread
    public Pair<String, Long> zzqm()
    {
      zzaua.this.zzmR();
      long l = zzql();
      if (l < this.zzagH) {
        return null;
      }
      if (l > this.zzagH * 2L)
      {
        zzqk();
        return null;
      }
      String str = zzaua.zzb(zzaua.this).getString(this.zzbtz, null);
      l = zzaua.zzb(zzaua.this).getLong(this.zzbty, 0L);
      zzqk();
      if ((str == null) || (l <= 0L)) {
        return zzaua.zzbsZ;
      }
      return new Pair(str, Long.valueOf(l));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaua.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */