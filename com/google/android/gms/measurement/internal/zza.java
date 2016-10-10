package com.google.android.gms.measurement.internal;

import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzac;

class zza
{
  private String FA;
  private String G;
  private long anA;
  private long anB;
  private long anC;
  private String anD;
  private long anE;
  private long anF;
  private boolean anG;
  private long anH;
  private long anI;
  private long anJ;
  private long anK;
  private long anL;
  private long anM;
  private boolean anN;
  private long anO;
  private long anP;
  private final zzx anq;
  private String anw;
  private String anx;
  private String any;
  private long anz;
  private final String zzcpe;
  
  @WorkerThread
  zza(zzx paramzzx, String paramString)
  {
    zzac.zzy(paramzzx);
    zzac.zzhz(paramString);
    this.anq = paramzzx;
    this.zzcpe = paramString;
    this.anq.zzyl();
  }
  
  @WorkerThread
  public void setAppVersion(String paramString)
  {
    this.anq.zzyl();
    boolean bool2 = this.anN;
    if (!zzal.zzbb(this.G, paramString)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.anN = (bool1 | bool2);
      this.G = paramString;
      return;
    }
  }
  
  @WorkerThread
  public void setMeasurementEnabled(boolean paramBoolean)
  {
    this.anq.zzyl();
    boolean bool2 = this.anN;
    if (this.anG != paramBoolean) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.anN = (bool1 | bool2);
      this.anG = paramBoolean;
      return;
    }
  }
  
  @WorkerThread
  public void zzaw(long paramLong)
  {
    this.anq.zzyl();
    boolean bool2 = this.anN;
    if (this.anA != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.anN = (bool1 | bool2);
      this.anA = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzax(long paramLong)
  {
    this.anq.zzyl();
    boolean bool2 = this.anN;
    if (this.anB != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.anN = (bool1 | bool2);
      this.anB = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzay(long paramLong)
  {
    this.anq.zzyl();
    boolean bool2 = this.anN;
    if (this.anC != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.anN = (bool1 | bool2);
      this.anC = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public String zzayn()
  {
    this.anq.zzyl();
    return this.FA;
  }
  
  @WorkerThread
  public void zzaz(long paramLong)
  {
    this.anq.zzyl();
    boolean bool2 = this.anN;
    if (this.anE != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.anN = (bool1 | bool2);
      this.anE = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzba(long paramLong)
  {
    this.anq.zzyl();
    boolean bool2 = this.anN;
    if (this.anF != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.anN = (bool1 | bool2);
      this.anF = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzbb(long paramLong)
  {
    boolean bool1 = true;
    boolean bool2;
    if (paramLong >= 0L)
    {
      bool2 = true;
      zzac.zzbs(bool2);
      this.anq.zzyl();
      bool2 = this.anN;
      if (this.anz == paramLong) {
        break label58;
      }
    }
    for (;;)
    {
      this.anN = (bool2 | bool1);
      this.anz = paramLong;
      return;
      bool2 = false;
      break;
      label58:
      bool1 = false;
    }
  }
  
  @WorkerThread
  public void zzbc(long paramLong)
  {
    this.anq.zzyl();
    boolean bool2 = this.anN;
    if (this.anO != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.anN = (bool1 | bool2);
      this.anO = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzbd(long paramLong)
  {
    this.anq.zzyl();
    boolean bool2 = this.anN;
    if (this.anP != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.anN = (bool1 | bool2);
      this.anP = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzbe(long paramLong)
  {
    this.anq.zzyl();
    boolean bool2 = this.anN;
    if (this.anH != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.anN = (bool1 | bool2);
      this.anH = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzbf(long paramLong)
  {
    this.anq.zzyl();
    boolean bool2 = this.anN;
    if (this.anI != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.anN = (bool1 | bool2);
      this.anI = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzbg(long paramLong)
  {
    this.anq.zzyl();
    boolean bool2 = this.anN;
    if (this.anJ != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.anN = (bool1 | bool2);
      this.anJ = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzbh(long paramLong)
  {
    this.anq.zzyl();
    boolean bool2 = this.anN;
    if (this.anK != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.anN = (bool1 | bool2);
      this.anK = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzbi(long paramLong)
  {
    this.anq.zzyl();
    boolean bool2 = this.anN;
    if (this.anM != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.anN = (bool1 | bool2);
      this.anM = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzbj(long paramLong)
  {
    this.anq.zzyl();
    boolean bool2 = this.anN;
    if (this.anL != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.anN = (bool1 | bool2);
      this.anL = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzbsq()
  {
    this.anq.zzyl();
    this.anN = false;
  }
  
  @WorkerThread
  public String zzbsr()
  {
    this.anq.zzyl();
    return this.anw;
  }
  
  @WorkerThread
  public String zzbss()
  {
    this.anq.zzyl();
    return this.anx;
  }
  
  @WorkerThread
  public String zzbst()
  {
    this.anq.zzyl();
    return this.any;
  }
  
  @WorkerThread
  public long zzbsu()
  {
    this.anq.zzyl();
    return this.anA;
  }
  
  @WorkerThread
  public long zzbsv()
  {
    this.anq.zzyl();
    return this.anB;
  }
  
  @WorkerThread
  public long zzbsw()
  {
    this.anq.zzyl();
    return this.anC;
  }
  
  @WorkerThread
  public String zzbsx()
  {
    this.anq.zzyl();
    return this.anD;
  }
  
  @WorkerThread
  public long zzbsy()
  {
    this.anq.zzyl();
    return this.anE;
  }
  
  @WorkerThread
  public long zzbsz()
  {
    this.anq.zzyl();
    return this.anF;
  }
  
  @WorkerThread
  public boolean zzbta()
  {
    this.anq.zzyl();
    return this.anG;
  }
  
  @WorkerThread
  public long zzbtb()
  {
    this.anq.zzyl();
    return this.anz;
  }
  
  @WorkerThread
  public long zzbtc()
  {
    this.anq.zzyl();
    return this.anO;
  }
  
  @WorkerThread
  public long zzbtd()
  {
    this.anq.zzyl();
    return this.anP;
  }
  
  @WorkerThread
  public void zzbte()
  {
    this.anq.zzyl();
    long l2 = this.anz + 1L;
    long l1 = l2;
    if (l2 > 2147483647L)
    {
      this.anq.zzbvg().zzbwe().log("Bundle index overflow");
      l1 = 0L;
    }
    this.anN = true;
    this.anz = l1;
  }
  
  @WorkerThread
  public long zzbtf()
  {
    this.anq.zzyl();
    return this.anH;
  }
  
  @WorkerThread
  public long zzbtg()
  {
    this.anq.zzyl();
    return this.anI;
  }
  
  @WorkerThread
  public long zzbth()
  {
    this.anq.zzyl();
    return this.anJ;
  }
  
  @WorkerThread
  public long zzbti()
  {
    this.anq.zzyl();
    return this.anK;
  }
  
  @WorkerThread
  public long zzbtj()
  {
    this.anq.zzyl();
    return this.anM;
  }
  
  @WorkerThread
  public long zzbtk()
  {
    this.anq.zzyl();
    return this.anL;
  }
  
  @WorkerThread
  public void zzlj(String paramString)
  {
    this.anq.zzyl();
    boolean bool2 = this.anN;
    if (!zzal.zzbb(this.FA, paramString)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.anN = (bool1 | bool2);
      this.FA = paramString;
      return;
    }
  }
  
  @WorkerThread
  public void zzlk(String paramString)
  {
    this.anq.zzyl();
    String str = paramString;
    if (TextUtils.isEmpty(paramString)) {
      str = null;
    }
    boolean bool2 = this.anN;
    if (!zzal.zzbb(this.anw, str)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.anN = (bool1 | bool2);
      this.anw = str;
      return;
    }
  }
  
  @WorkerThread
  public void zzll(String paramString)
  {
    this.anq.zzyl();
    boolean bool2 = this.anN;
    if (!zzal.zzbb(this.anx, paramString)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.anN = (bool1 | bool2);
      this.anx = paramString;
      return;
    }
  }
  
  @WorkerThread
  public void zzlm(String paramString)
  {
    this.anq.zzyl();
    boolean bool2 = this.anN;
    if (!zzal.zzbb(this.any, paramString)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.anN = (bool1 | bool2);
      this.any = paramString;
      return;
    }
  }
  
  @WorkerThread
  public void zzln(String paramString)
  {
    this.anq.zzyl();
    boolean bool2 = this.anN;
    if (!zzal.zzbb(this.anD, paramString)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.anN = (bool1 | bool2);
      this.anD = paramString;
      return;
    }
  }
  
  @WorkerThread
  public String zzti()
  {
    this.anq.zzyl();
    return this.zzcpe;
  }
  
  @WorkerThread
  public String zzyt()
  {
    this.anq.zzyl();
    return this.G;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zza.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */