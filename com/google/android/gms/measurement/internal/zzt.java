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
import com.google.android.gms.common.util.zze;
import com.google.firebase.iid.zzc;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Locale;

class zzt
  extends zzaa
{
  static final Pair<String, Long> asX = new Pair("", Long.valueOf(0L));
  public final zzc asY = new zzc("health_monitor", zzbwd().zzafj(), null);
  public final zzb asZ = new zzb("last_upload", 0L);
  public final zzb ata = new zzb("last_upload_attempt", 0L);
  public final zzb atb = new zzb("backoff", 0L);
  public final zzb atc = new zzb("last_delete_stale", 0L);
  public final zzb atd = new zzb("midnight_offset", 0L);
  private String ate;
  private boolean atf;
  private long atg;
  private SecureRandom ath;
  public final zzb ati = new zzb("time_before_start", 10000L);
  public final zzb atj = new zzb("session_timeout", 1800000L);
  public final zza atk = new zza("start_new_session", true);
  public final zzb atl = new zzb("last_pause_time", 0L);
  public final zzb atm = new zzb("time_active", 0L);
  public boolean atn;
  private SharedPreferences fF;
  
  zzt(zzx paramzzx)
  {
    super(paramzzx);
  }
  
  @WorkerThread
  private SecureRandom zzbxg()
  {
    zzzx();
    if (this.ath == null) {
      this.ath = new SecureRandom();
    }
    return this.ath;
  }
  
  @WorkerThread
  private SharedPreferences zzbxj()
  {
    zzzx();
    zzacj();
    return this.fF;
  }
  
  @WorkerThread
  void setMeasurementEnabled(boolean paramBoolean)
  {
    zzzx();
    zzbwb().zzbxe().zzj("Setting measurementEnabled", Boolean.valueOf(paramBoolean));
    SharedPreferences.Editor localEditor = zzbxj().edit();
    localEditor.putBoolean("measurement_enabled", paramBoolean);
    localEditor.apply();
  }
  
  @WorkerThread
  String zzbtj()
  {
    zzzx();
    try
    {
      String str = zzc.C().getId();
      return str;
    }
    catch (IllegalStateException localIllegalStateException)
    {
      zzbwb().zzbxa().log("Failed to retrieve Firebase Instance Id");
    }
    return null;
  }
  
  @WorkerThread
  String zzbxh()
  {
    byte[] arrayOfByte = new byte[16];
    zzbxg().nextBytes(arrayOfByte);
    return String.format(Locale.US, "%032x", new Object[] { new BigInteger(1, arrayOfByte) });
  }
  
  @WorkerThread
  long zzbxi()
  {
    zzacj();
    zzzx();
    long l2 = this.atd.get();
    long l1 = l2;
    if (l2 == 0L)
    {
      l1 = zzbxg().nextInt(86400000) + 1;
      this.atd.set(l1);
    }
    return l1;
  }
  
  @WorkerThread
  String zzbxk()
  {
    zzzx();
    return zzbxj().getString("gmp_app_id", null);
  }
  
  @WorkerThread
  Boolean zzbxl()
  {
    zzzx();
    if (!zzbxj().contains("use_service")) {
      return null;
    }
    return Boolean.valueOf(zzbxj().getBoolean("use_service", false));
  }
  
  @WorkerThread
  void zzbxm()
  {
    boolean bool1 = true;
    zzzx();
    zzbwb().zzbxe().log("Clearing collection preferences.");
    boolean bool2 = zzbxj().contains("measurement_enabled");
    if (bool2) {
      bool1 = zzch(true);
    }
    SharedPreferences.Editor localEditor = zzbxj().edit();
    localEditor.clear();
    localEditor.apply();
    if (bool2) {
      setMeasurementEnabled(bool1);
    }
  }
  
  @WorkerThread
  protected String zzbxn()
  {
    zzzx();
    String str1 = zzbxj().getString("previous_os_version", null);
    String str2 = zzbvs().zzbws();
    if ((!TextUtils.isEmpty(str2)) && (!str2.equals(str1)))
    {
      SharedPreferences.Editor localEditor = zzbxj().edit();
      localEditor.putString("previous_os_version", str2);
      localEditor.apply();
    }
    return str1;
  }
  
  @WorkerThread
  void zzcg(boolean paramBoolean)
  {
    zzzx();
    zzbwb().zzbxe().zzj("Setting useService", Boolean.valueOf(paramBoolean));
    SharedPreferences.Editor localEditor = zzbxj().edit();
    localEditor.putBoolean("use_service", paramBoolean);
    localEditor.apply();
  }
  
  @WorkerThread
  boolean zzch(boolean paramBoolean)
  {
    zzzx();
    return zzbxj().getBoolean("measurement_enabled", paramBoolean);
  }
  
  @NonNull
  @WorkerThread
  Pair<String, Boolean> zzmk(String paramString)
  {
    zzzx();
    long l = zzabz().elapsedRealtime();
    if ((this.ate != null) && (l < this.atg)) {
      return new Pair(this.ate, Boolean.valueOf(this.atf));
    }
    this.atg = (l + zzbwd().zzlr(paramString));
    AdvertisingIdClient.setShouldSkipGmsCoreVersionCheck(true);
    try
    {
      paramString = AdvertisingIdClient.getAdvertisingIdInfo(getContext());
      this.ate = paramString.getId();
      if (this.ate == null) {
        this.ate = "";
      }
      this.atf = paramString.isLimitAdTrackingEnabled();
    }
    catch (Throwable paramString)
    {
      for (;;)
      {
        zzbwb().zzbxd().zzj("Unable to get advertising id", paramString);
        this.ate = "";
      }
    }
    AdvertisingIdClient.setShouldSkipGmsCoreVersionCheck(false);
    return new Pair(this.ate, Boolean.valueOf(this.atf));
  }
  
  @WorkerThread
  String zzml(String paramString)
  {
    zzzx();
    paramString = (String)zzmk(paramString).first;
    MessageDigest localMessageDigest = zzal.zzfl("MD5");
    if (localMessageDigest == null) {
      return null;
    }
    return String.format(Locale.US, "%032X", new Object[] { new BigInteger(1, localMessageDigest.digest(paramString.getBytes())) });
  }
  
  @WorkerThread
  void zzmm(String paramString)
  {
    zzzx();
    SharedPreferences.Editor localEditor = zzbxj().edit();
    localEditor.putString("gmp_app_id", paramString);
    localEditor.apply();
  }
  
  protected void zzzy()
  {
    this.fF = getContext().getSharedPreferences("com.google.android.gms.measurement.prefs", 0);
    this.atn = this.fF.getBoolean("has_been_opened", false);
    if (!this.atn)
    {
      SharedPreferences.Editor localEditor = this.fF.edit();
      localEditor.putBoolean("has_been_opened", true);
      localEditor.apply();
    }
  }
  
  public final class zza
  {
    private final boolean ato;
    private boolean atp;
    private boolean xv;
    private final String zzbcn;
    
    public zza(String paramString, boolean paramBoolean)
    {
      com.google.android.gms.common.internal.zzaa.zzib(paramString);
      this.zzbcn = paramString;
      this.ato = paramBoolean;
    }
    
    @WorkerThread
    private void zzbxo()
    {
      if (this.atp) {
        return;
      }
      this.atp = true;
      this.xv = zzt.zza(zzt.this).getBoolean(this.zzbcn, this.ato);
    }
    
    @WorkerThread
    public boolean get()
    {
      zzbxo();
      return this.xv;
    }
    
    @WorkerThread
    public void set(boolean paramBoolean)
    {
      SharedPreferences.Editor localEditor = zzt.zza(zzt.this).edit();
      localEditor.putBoolean(this.zzbcn, paramBoolean);
      localEditor.apply();
      this.xv = paramBoolean;
    }
  }
  
  public final class zzb
  {
    private boolean atp;
    private final long atr;
    private long cf;
    private final String zzbcn;
    
    public zzb(String paramString, long paramLong)
    {
      com.google.android.gms.common.internal.zzaa.zzib(paramString);
      this.zzbcn = paramString;
      this.atr = paramLong;
    }
    
    @WorkerThread
    private void zzbxo()
    {
      if (this.atp) {
        return;
      }
      this.atp = true;
      this.cf = zzt.zza(zzt.this).getLong(this.zzbcn, this.atr);
    }
    
    @WorkerThread
    public long get()
    {
      zzbxo();
      return this.cf;
    }
    
    @WorkerThread
    public void set(long paramLong)
    {
      SharedPreferences.Editor localEditor = zzt.zza(zzt.this).edit();
      localEditor.putLong(this.zzbcn, paramLong);
      localEditor.apply();
      this.cf = paramLong;
    }
  }
  
  public final class zzc
  {
    final String ats;
    private final String att;
    private final String atu;
    private final long fJ;
    
    private zzc(String paramString, long paramLong)
    {
      com.google.android.gms.common.internal.zzaa.zzib(paramString);
      if (paramLong > 0L) {}
      for (boolean bool = true;; bool = false)
      {
        com.google.android.gms.common.internal.zzaa.zzbt(bool);
        this.ats = String.valueOf(paramString).concat(":start");
        this.att = String.valueOf(paramString).concat(":count");
        this.atu = String.valueOf(paramString).concat(":value");
        this.fJ = paramLong;
        return;
      }
    }
    
    @WorkerThread
    private void zzagu()
    {
      zzt.this.zzzx();
      long l = zzt.this.zzabz().currentTimeMillis();
      SharedPreferences.Editor localEditor = zzt.zza(zzt.this).edit();
      localEditor.remove(this.att);
      localEditor.remove(this.atu);
      localEditor.putLong(this.ats, l);
      localEditor.apply();
    }
    
    @WorkerThread
    private long zzagv()
    {
      zzt.this.zzzx();
      long l = zzagx();
      if (l == 0L)
      {
        zzagu();
        return 0L;
      }
      return Math.abs(l - zzt.this.zzabz().currentTimeMillis());
    }
    
    @WorkerThread
    private long zzagx()
    {
      return zzt.zzc(zzt.this).getLong(this.ats, 0L);
    }
    
    @WorkerThread
    public Pair<String, Long> zzagw()
    {
      zzt.this.zzzx();
      long l = zzagv();
      if (l < this.fJ) {
        return null;
      }
      if (l > this.fJ * 2L)
      {
        zzagu();
        return null;
      }
      String str = zzt.zzc(zzt.this).getString(this.atu, null);
      l = zzt.zzc(zzt.this).getLong(this.att, 0L);
      zzagu();
      if ((str == null) || (l <= 0L)) {
        return zzt.asX;
      }
      return new Pair(str, Long.valueOf(l));
    }
    
    @WorkerThread
    public void zzfg(String paramString)
    {
      zzg(paramString, 1L);
    }
    
    @WorkerThread
    public void zzg(String paramString, long paramLong)
    {
      zzt.this.zzzx();
      if (zzagx() == 0L) {
        zzagu();
      }
      String str = paramString;
      if (paramString == null) {
        str = "";
      }
      long l = zzt.zza(zzt.this).getLong(this.att, 0L);
      if (l <= 0L)
      {
        paramString = zzt.zza(zzt.this).edit();
        paramString.putString(this.atu, str);
        paramString.putLong(this.att, paramLong);
        paramString.apply();
        return;
      }
      if ((zzt.zzb(zzt.this).nextLong() & 0x7FFFFFFFFFFFFFFF) < Long.MAX_VALUE / (l + paramLong) * paramLong) {}
      for (int i = 1;; i = 0)
      {
        paramString = zzt.zza(zzt.this).edit();
        if (i != 0) {
          paramString.putString(this.atu, str);
        }
        paramString.putLong(this.att, l + paramLong);
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