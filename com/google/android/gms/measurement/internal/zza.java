package com.google.android.gms.measurement.internal;

import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzaa;

class zza
{
  private String Hh;
  private String aqF;
  private String aqG;
  private String aqH;
  private long aqI;
  private long aqJ;
  private long aqK;
  private long aqL;
  private String aqM;
  private long aqN;
  private long aqO;
  private boolean aqP;
  private long aqQ;
  private long aqR;
  private long aqS;
  private long aqT;
  private long aqU;
  private long aqV;
  private boolean aqW;
  private long aqX;
  private long aqY;
  private final zzx aqw;
  private String bO;
  private final String zzctj;
  
  @WorkerThread
  zza(zzx paramzzx, String paramString)
  {
    zzaa.zzy(paramzzx);
    zzaa.zzib(paramString);
    this.aqw = paramzzx;
    this.zzctj = paramString;
    this.aqw.zzzx();
  }
  
  @WorkerThread
  public void setAppVersion(String paramString)
  {
    this.aqw.zzzx();
    boolean bool2 = this.aqW;
    if (!zzal.zzbb(this.bO, paramString)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.aqW = (bool1 | bool2);
      this.bO = paramString;
      return;
    }
  }
  
  @WorkerThread
  public void setMeasurementEnabled(boolean paramBoolean)
  {
    this.aqw.zzzx();
    boolean bool2 = this.aqW;
    if (this.aqP != paramBoolean) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.aqW = (bool1 | bool2);
      this.aqP = paramBoolean;
      return;
    }
  }
  
  @WorkerThread
  public String zzaaf()
  {
    this.aqw.zzzx();
    return this.bO;
  }
  
  @WorkerThread
  public void zzav(long paramLong)
  {
    this.aqw.zzzx();
    boolean bool2 = this.aqW;
    if (this.aqJ != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.aqW = (bool1 | bool2);
      this.aqJ = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzaw(long paramLong)
  {
    this.aqw.zzzx();
    boolean bool2 = this.aqW;
    if (this.aqK != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.aqW = (bool1 | bool2);
      this.aqK = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzax(long paramLong)
  {
    this.aqw.zzzx();
    boolean bool2 = this.aqW;
    if (this.aqL != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.aqW = (bool1 | bool2);
      this.aqL = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzay(long paramLong)
  {
    this.aqw.zzzx();
    boolean bool2 = this.aqW;
    if (this.aqN != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.aqW = (bool1 | bool2);
      this.aqN = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzaz(long paramLong)
  {
    this.aqw.zzzx();
    boolean bool2 = this.aqW;
    if (this.aqO != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.aqW = (bool1 | bool2);
      this.aqO = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public String zzazn()
  {
    this.aqw.zzzx();
    return this.Hh;
  }
  
  @WorkerThread
  public void zzba(long paramLong)
  {
    boolean bool1 = true;
    boolean bool2;
    if (paramLong >= 0L)
    {
      bool2 = true;
      zzaa.zzbt(bool2);
      this.aqw.zzzx();
      bool2 = this.aqW;
      if (this.aqI == paramLong) {
        break label58;
      }
    }
    for (;;)
    {
      this.aqW = (bool2 | bool1);
      this.aqI = paramLong;
      return;
      bool2 = false;
      break;
      label58:
      bool1 = false;
    }
  }
  
  @WorkerThread
  public void zzbb(long paramLong)
  {
    this.aqw.zzzx();
    boolean bool2 = this.aqW;
    if (this.aqX != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.aqW = (bool1 | bool2);
      this.aqX = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzbc(long paramLong)
  {
    this.aqw.zzzx();
    boolean bool2 = this.aqW;
    if (this.aqY != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.aqW = (bool1 | bool2);
      this.aqY = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzbd(long paramLong)
  {
    this.aqw.zzzx();
    boolean bool2 = this.aqW;
    if (this.aqQ != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.aqW = (bool1 | bool2);
      this.aqQ = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzbe(long paramLong)
  {
    this.aqw.zzzx();
    boolean bool2 = this.aqW;
    if (this.aqR != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.aqW = (bool1 | bool2);
      this.aqR = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzbf(long paramLong)
  {
    this.aqw.zzzx();
    boolean bool2 = this.aqW;
    if (this.aqS != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.aqW = (bool1 | bool2);
      this.aqS = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzbg(long paramLong)
  {
    this.aqw.zzzx();
    boolean bool2 = this.aqW;
    if (this.aqT != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.aqW = (bool1 | bool2);
      this.aqT = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzbh(long paramLong)
  {
    this.aqw.zzzx();
    boolean bool2 = this.aqW;
    if (this.aqV != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.aqW = (bool1 | bool2);
      this.aqV = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzbi(long paramLong)
  {
    this.aqw.zzzx();
    boolean bool2 = this.aqW;
    if (this.aqU != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.aqW = (bool1 | bool2);
      this.aqU = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzbtg()
  {
    this.aqw.zzzx();
    this.aqW = false;
  }
  
  @WorkerThread
  public String zzbth()
  {
    this.aqw.zzzx();
    return this.aqF;
  }
  
  @WorkerThread
  public String zzbti()
  {
    this.aqw.zzzx();
    return this.aqG;
  }
  
  @WorkerThread
  public String zzbtj()
  {
    this.aqw.zzzx();
    return this.aqH;
  }
  
  @WorkerThread
  public long zzbtk()
  {
    this.aqw.zzzx();
    return this.aqJ;
  }
  
  @WorkerThread
  public long zzbtl()
  {
    this.aqw.zzzx();
    return this.aqK;
  }
  
  @WorkerThread
  public long zzbtm()
  {
    this.aqw.zzzx();
    return this.aqL;
  }
  
  @WorkerThread
  public String zzbtn()
  {
    this.aqw.zzzx();
    return this.aqM;
  }
  
  @WorkerThread
  public long zzbto()
  {
    this.aqw.zzzx();
    return this.aqN;
  }
  
  @WorkerThread
  public long zzbtp()
  {
    this.aqw.zzzx();
    return this.aqO;
  }
  
  @WorkerThread
  public boolean zzbtq()
  {
    this.aqw.zzzx();
    return this.aqP;
  }
  
  @WorkerThread
  public long zzbtr()
  {
    this.aqw.zzzx();
    return this.aqI;
  }
  
  @WorkerThread
  public long zzbts()
  {
    this.aqw.zzzx();
    return this.aqX;
  }
  
  @WorkerThread
  public long zzbtt()
  {
    this.aqw.zzzx();
    return this.aqY;
  }
  
  @WorkerThread
  public void zzbtu()
  {
    this.aqw.zzzx();
    long l2 = this.aqI + 1L;
    long l1 = l2;
    if (l2 > 2147483647L)
    {
      this.aqw.zzbwb().zzbxa().log("Bundle index overflow");
      l1 = 0L;
    }
    this.aqW = true;
    this.aqI = l1;
  }
  
  @WorkerThread
  public long zzbtv()
  {
    this.aqw.zzzx();
    return this.aqQ;
  }
  
  @WorkerThread
  public long zzbtw()
  {
    this.aqw.zzzx();
    return this.aqR;
  }
  
  @WorkerThread
  public long zzbtx()
  {
    this.aqw.zzzx();
    return this.aqS;
  }
  
  @WorkerThread
  public long zzbty()
  {
    this.aqw.zzzx();
    return this.aqT;
  }
  
  @WorkerThread
  public long zzbtz()
  {
    this.aqw.zzzx();
    return this.aqV;
  }
  
  @WorkerThread
  public long zzbua()
  {
    this.aqw.zzzx();
    return this.aqU;
  }
  
  @WorkerThread
  public void zzlj(String paramString)
  {
    this.aqw.zzzx();
    boolean bool2 = this.aqW;
    if (!zzal.zzbb(this.Hh, paramString)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.aqW = (bool1 | bool2);
      this.Hh = paramString;
      return;
    }
  }
  
  @WorkerThread
  public void zzlk(String paramString)
  {
    this.aqw.zzzx();
    String str = paramString;
    if (TextUtils.isEmpty(paramString)) {
      str = null;
    }
    boolean bool2 = this.aqW;
    if (!zzal.zzbb(this.aqF, str)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.aqW = (bool1 | bool2);
      this.aqF = str;
      return;
    }
  }
  
  @WorkerThread
  public void zzll(String paramString)
  {
    this.aqw.zzzx();
    boolean bool2 = this.aqW;
    if (!zzal.zzbb(this.aqG, paramString)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.aqW = (bool1 | bool2);
      this.aqG = paramString;
      return;
    }
  }
  
  @WorkerThread
  public void zzlm(String paramString)
  {
    this.aqw.zzzx();
    boolean bool2 = this.aqW;
    if (!zzal.zzbb(this.aqH, paramString)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.aqW = (bool1 | bool2);
      this.aqH = paramString;
      return;
    }
  }
  
  @WorkerThread
  public void zzln(String paramString)
  {
    this.aqw.zzzx();
    boolean bool2 = this.aqW;
    if (!zzal.zzbb(this.aqM, paramString)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.aqW = (bool1 | bool2);
      this.aqM = paramString;
      return;
    }
  }
  
  @WorkerThread
  public String zzup()
  {
    this.aqw.zzzx();
    return this.zzctj;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zza.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */