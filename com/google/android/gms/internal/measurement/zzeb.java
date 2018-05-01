package com.google.android.gms.internal.measurement;

import android.text.TextUtils;
import com.google.android.gms.common.internal.Preconditions;

final class zzeb
{
  private final zzgl zzacr;
  private String zzadg;
  private String zzadh;
  private String zzadi;
  private String zzadj;
  private long zzadk;
  private long zzadl;
  private long zzadm;
  private long zzadn;
  private String zzado;
  private long zzadp;
  private long zzadq;
  private boolean zzadr;
  private long zzads;
  private boolean zzadt;
  private boolean zzadu;
  private long zzadv;
  private long zzadw;
  private long zzadx;
  private long zzady;
  private long zzadz;
  private long zzaea;
  private String zzaeb;
  private boolean zzaec;
  private long zzaed;
  private long zzaee;
  private String zztc;
  private final String zztd;
  
  zzeb(zzgl paramzzgl, String paramString)
  {
    Preconditions.checkNotNull(paramzzgl);
    Preconditions.checkNotEmpty(paramString);
    this.zzacr = paramzzgl;
    this.zztd = paramString;
    this.zzacr.zzgf().zzab();
  }
  
  public final String getAppInstanceId()
  {
    this.zzacr.zzgf().zzab();
    return this.zzadg;
  }
  
  public final String getGmpAppId()
  {
    this.zzacr.zzgf().zzab();
    return this.zzadh;
  }
  
  public final boolean isMeasurementEnabled()
  {
    this.zzacr.zzgf().zzab();
    return this.zzadr;
  }
  
  public final void setAppVersion(String paramString)
  {
    this.zzacr.zzgf().zzab();
    boolean bool1 = this.zzaec;
    if (!zzjv.zzs(this.zztc, paramString)) {}
    for (boolean bool2 = true;; bool2 = false)
    {
      this.zzaec = (bool2 | bool1);
      this.zztc = paramString;
      return;
    }
  }
  
  public final void setMeasurementEnabled(boolean paramBoolean)
  {
    this.zzacr.zzgf().zzab();
    boolean bool1 = this.zzaec;
    if (this.zzadr != paramBoolean) {}
    for (boolean bool2 = true;; bool2 = false)
    {
      this.zzaec = (bool2 | bool1);
      this.zzadr = paramBoolean;
      return;
    }
  }
  
  public final void zzaa(long paramLong)
  {
    this.zzacr.zzgf().zzab();
    boolean bool1 = this.zzaec;
    if (this.zzads != paramLong) {}
    for (boolean bool2 = true;; bool2 = false)
    {
      this.zzaec = (bool2 | bool1);
      this.zzads = paramLong;
      return;
    }
  }
  
  public final String zzag()
  {
    this.zzacr.zzgf().zzab();
    return this.zztc;
  }
  
  public final String zzah()
  {
    this.zzacr.zzgf().zzab();
    return this.zztd;
  }
  
  public final void zzal(String paramString)
  {
    this.zzacr.zzgf().zzab();
    boolean bool1 = this.zzaec;
    if (!zzjv.zzs(this.zzadg, paramString)) {}
    for (boolean bool2 = true;; bool2 = false)
    {
      this.zzaec = (bool2 | bool1);
      this.zzadg = paramString;
      return;
    }
  }
  
  public final void zzam(String paramString)
  {
    this.zzacr.zzgf().zzab();
    String str = paramString;
    if (TextUtils.isEmpty(paramString)) {
      str = null;
    }
    boolean bool1 = this.zzaec;
    if (!zzjv.zzs(this.zzadh, str)) {}
    for (boolean bool2 = true;; bool2 = false)
    {
      this.zzaec = (bool2 | bool1);
      this.zzadh = str;
      return;
    }
  }
  
  public final void zzan(String paramString)
  {
    this.zzacr.zzgf().zzab();
    boolean bool1 = this.zzaec;
    if (!zzjv.zzs(this.zzadi, paramString)) {}
    for (boolean bool2 = true;; bool2 = false)
    {
      this.zzaec = (bool2 | bool1);
      this.zzadi = paramString;
      return;
    }
  }
  
  public final void zzao(String paramString)
  {
    this.zzacr.zzgf().zzab();
    boolean bool1 = this.zzaec;
    if (!zzjv.zzs(this.zzadj, paramString)) {}
    for (boolean bool2 = true;; bool2 = false)
    {
      this.zzaec = (bool2 | bool1);
      this.zzadj = paramString;
      return;
    }
  }
  
  public final void zzap(String paramString)
  {
    this.zzacr.zzgf().zzab();
    boolean bool1 = this.zzaec;
    if (!zzjv.zzs(this.zzado, paramString)) {}
    for (boolean bool2 = true;; bool2 = false)
    {
      this.zzaec = (bool2 | bool1);
      this.zzado = paramString;
      return;
    }
  }
  
  public final void zzaq(String paramString)
  {
    this.zzacr.zzgf().zzab();
    boolean bool1 = this.zzaec;
    if (!zzjv.zzs(this.zzaeb, paramString)) {}
    for (boolean bool2 = true;; bool2 = false)
    {
      this.zzaec = (bool2 | bool1);
      this.zzaeb = paramString;
      return;
    }
  }
  
  public final void zzd(boolean paramBoolean)
  {
    this.zzacr.zzgf().zzab();
    if (this.zzadt != paramBoolean) {}
    for (boolean bool = true;; bool = false)
    {
      this.zzaec = bool;
      this.zzadt = paramBoolean;
      return;
    }
  }
  
  public final void zze(boolean paramBoolean)
  {
    this.zzacr.zzgf().zzab();
    if (this.zzadu != paramBoolean) {}
    for (boolean bool = true;; bool = false)
    {
      this.zzaec = bool;
      this.zzadu = paramBoolean;
      return;
    }
  }
  
  public final void zzgj()
  {
    this.zzacr.zzgf().zzab();
    this.zzaec = false;
  }
  
  public final String zzgk()
  {
    this.zzacr.zzgf().zzab();
    return this.zzadi;
  }
  
  public final String zzgl()
  {
    this.zzacr.zzgf().zzab();
    return this.zzadj;
  }
  
  public final long zzgm()
  {
    this.zzacr.zzgf().zzab();
    return this.zzadl;
  }
  
  public final long zzgn()
  {
    this.zzacr.zzgf().zzab();
    return this.zzadm;
  }
  
  public final long zzgo()
  {
    this.zzacr.zzgf().zzab();
    return this.zzadn;
  }
  
  public final String zzgp()
  {
    this.zzacr.zzgf().zzab();
    return this.zzado;
  }
  
  public final long zzgq()
  {
    this.zzacr.zzgf().zzab();
    return this.zzadp;
  }
  
  public final long zzgr()
  {
    this.zzacr.zzgf().zzab();
    return this.zzadq;
  }
  
  public final long zzgs()
  {
    this.zzacr.zzgf().zzab();
    return this.zzadk;
  }
  
  public final long zzgt()
  {
    this.zzacr.zzgf().zzab();
    return this.zzaed;
  }
  
  public final long zzgu()
  {
    this.zzacr.zzgf().zzab();
    return this.zzaee;
  }
  
  public final void zzgv()
  {
    this.zzacr.zzgf().zzab();
    long l1 = this.zzadk + 1L;
    long l2 = l1;
    if (l1 > 2147483647L)
    {
      this.zzacr.zzgg().zzin().zzg("Bundle index overflow. appId", zzfg.zzbh(this.zztd));
      l2 = 0L;
    }
    this.zzaec = true;
    this.zzadk = l2;
  }
  
  public final long zzgw()
  {
    this.zzacr.zzgf().zzab();
    return this.zzadv;
  }
  
  public final long zzgx()
  {
    this.zzacr.zzgf().zzab();
    return this.zzadw;
  }
  
  public final long zzgy()
  {
    this.zzacr.zzgf().zzab();
    return this.zzadx;
  }
  
  public final long zzgz()
  {
    this.zzacr.zzgf().zzab();
    return this.zzady;
  }
  
  public final long zzha()
  {
    this.zzacr.zzgf().zzab();
    return this.zzaea;
  }
  
  public final long zzhb()
  {
    this.zzacr.zzgf().zzab();
    return this.zzadz;
  }
  
  public final String zzhc()
  {
    this.zzacr.zzgf().zzab();
    return this.zzaeb;
  }
  
  public final String zzhd()
  {
    this.zzacr.zzgf().zzab();
    String str = this.zzaeb;
    zzaq(null);
    return str;
  }
  
  public final long zzhe()
  {
    this.zzacr.zzgf().zzab();
    return this.zzads;
  }
  
  public final boolean zzhf()
  {
    this.zzacr.zzgf().zzab();
    return this.zzadt;
  }
  
  public final boolean zzhg()
  {
    this.zzacr.zzgf().zzab();
    return this.zzadu;
  }
  
  public final void zzm(long paramLong)
  {
    this.zzacr.zzgf().zzab();
    boolean bool1 = this.zzaec;
    if (this.zzadl != paramLong) {}
    for (boolean bool2 = true;; bool2 = false)
    {
      this.zzaec = (bool2 | bool1);
      this.zzadl = paramLong;
      return;
    }
  }
  
  public final void zzn(long paramLong)
  {
    this.zzacr.zzgf().zzab();
    boolean bool1 = this.zzaec;
    if (this.zzadm != paramLong) {}
    for (boolean bool2 = true;; bool2 = false)
    {
      this.zzaec = (bool2 | bool1);
      this.zzadm = paramLong;
      return;
    }
  }
  
  public final void zzo(long paramLong)
  {
    this.zzacr.zzgf().zzab();
    boolean bool1 = this.zzaec;
    if (this.zzadn != paramLong) {}
    for (boolean bool2 = true;; bool2 = false)
    {
      this.zzaec = (bool2 | bool1);
      this.zzadn = paramLong;
      return;
    }
  }
  
  public final void zzp(long paramLong)
  {
    this.zzacr.zzgf().zzab();
    boolean bool1 = this.zzaec;
    if (this.zzadp != paramLong) {}
    for (boolean bool2 = true;; bool2 = false)
    {
      this.zzaec = (bool2 | bool1);
      this.zzadp = paramLong;
      return;
    }
  }
  
  public final void zzq(long paramLong)
  {
    this.zzacr.zzgf().zzab();
    boolean bool1 = this.zzaec;
    if (this.zzadq != paramLong) {}
    for (boolean bool2 = true;; bool2 = false)
    {
      this.zzaec = (bool2 | bool1);
      this.zzadq = paramLong;
      return;
    }
  }
  
  public final void zzr(long paramLong)
  {
    boolean bool1 = true;
    boolean bool2;
    if (paramLong >= 0L)
    {
      bool2 = true;
      Preconditions.checkArgument(bool2);
      this.zzacr.zzgf().zzab();
      bool2 = this.zzaec;
      if (this.zzadk == paramLong) {
        break label61;
      }
    }
    for (;;)
    {
      this.zzaec = (bool2 | bool1);
      this.zzadk = paramLong;
      return;
      bool2 = false;
      break;
      label61:
      bool1 = false;
    }
  }
  
  public final void zzs(long paramLong)
  {
    this.zzacr.zzgf().zzab();
    boolean bool1 = this.zzaec;
    if (this.zzaed != paramLong) {}
    for (boolean bool2 = true;; bool2 = false)
    {
      this.zzaec = (bool2 | bool1);
      this.zzaed = paramLong;
      return;
    }
  }
  
  public final void zzt(long paramLong)
  {
    this.zzacr.zzgf().zzab();
    boolean bool1 = this.zzaec;
    if (this.zzaee != paramLong) {}
    for (boolean bool2 = true;; bool2 = false)
    {
      this.zzaec = (bool2 | bool1);
      this.zzaee = paramLong;
      return;
    }
  }
  
  public final void zzu(long paramLong)
  {
    this.zzacr.zzgf().zzab();
    boolean bool1 = this.zzaec;
    if (this.zzadv != paramLong) {}
    for (boolean bool2 = true;; bool2 = false)
    {
      this.zzaec = (bool2 | bool1);
      this.zzadv = paramLong;
      return;
    }
  }
  
  public final void zzv(long paramLong)
  {
    this.zzacr.zzgf().zzab();
    boolean bool1 = this.zzaec;
    if (this.zzadw != paramLong) {}
    for (boolean bool2 = true;; bool2 = false)
    {
      this.zzaec = (bool2 | bool1);
      this.zzadw = paramLong;
      return;
    }
  }
  
  public final void zzw(long paramLong)
  {
    this.zzacr.zzgf().zzab();
    boolean bool1 = this.zzaec;
    if (this.zzadx != paramLong) {}
    for (boolean bool2 = true;; bool2 = false)
    {
      this.zzaec = (bool2 | bool1);
      this.zzadx = paramLong;
      return;
    }
  }
  
  public final void zzx(long paramLong)
  {
    this.zzacr.zzgf().zzab();
    boolean bool1 = this.zzaec;
    if (this.zzady != paramLong) {}
    for (boolean bool2 = true;; bool2 = false)
    {
      this.zzaec = (bool2 | bool1);
      this.zzady = paramLong;
      return;
    }
  }
  
  public final void zzy(long paramLong)
  {
    this.zzacr.zzgf().zzab();
    boolean bool1 = this.zzaec;
    if (this.zzaea != paramLong) {}
    for (boolean bool2 = true;; bool2 = false)
    {
      this.zzaec = (bool2 | bool1);
      this.zzaea = paramLong;
      return;
    }
  }
  
  public final void zzz(long paramLong)
  {
    this.zzacr.zzgf().zzab();
    boolean bool1 = this.zzaec;
    if (this.zzadz != paramLong) {}
    for (boolean bool2 = true;; bool2 = false)
    {
      this.zzaec = (bool2 | bool1);
      this.zzadz = paramLong;
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzeb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */