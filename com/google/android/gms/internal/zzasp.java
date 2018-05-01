package com.google.android.gms.internal;

import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzac;

class zzasp
{
  private final String zzVQ;
  private String zzaHB;
  private String zzabL;
  private String zzbpK;
  private String zzbpL;
  private String zzbpM;
  private long zzbpN;
  private long zzbpO;
  private long zzbpP;
  private long zzbpQ;
  private String zzbpR;
  private long zzbpS;
  private long zzbpT;
  private boolean zzbpU;
  private long zzbpV;
  private long zzbpW;
  private long zzbpX;
  private long zzbpY;
  private long zzbpZ;
  private final zzatp zzbpw;
  private long zzbqa;
  private String zzbqb;
  private boolean zzbqc;
  private long zzbqd;
  private long zzbqe;
  
  @WorkerThread
  zzasp(zzatp paramzzatp, String paramString)
  {
    zzac.zzw(paramzzatp);
    zzac.zzdv(paramString);
    this.zzbpw = paramzzatp;
    this.zzVQ = paramString;
    this.zzbpw.zzmq();
  }
  
  @WorkerThread
  public String getAppInstanceId()
  {
    this.zzbpw.zzmq();
    return this.zzaHB;
  }
  
  @WorkerThread
  public String getGmpAppId()
  {
    this.zzbpw.zzmq();
    return this.zzbpK;
  }
  
  @WorkerThread
  public void setAppVersion(String paramString)
  {
    this.zzbpw.zzmq();
    boolean bool2 = this.zzbqc;
    if (!zzaue.zzab(this.zzabL, paramString)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqc = (bool1 | bool2);
      this.zzabL = paramString;
      return;
    }
  }
  
  @WorkerThread
  public void setMeasurementEnabled(boolean paramBoolean)
  {
    this.zzbpw.zzmq();
    boolean bool2 = this.zzbqc;
    if (this.zzbpU != paramBoolean) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqc = (bool1 | bool2);
      this.zzbpU = paramBoolean;
      return;
    }
  }
  
  @WorkerThread
  public long zzJA()
  {
    this.zzbpw.zzmq();
    return this.zzbpP;
  }
  
  @WorkerThread
  public long zzJB()
  {
    this.zzbpw.zzmq();
    return this.zzbpQ;
  }
  
  @WorkerThread
  public String zzJC()
  {
    this.zzbpw.zzmq();
    return this.zzbpR;
  }
  
  @WorkerThread
  public long zzJD()
  {
    this.zzbpw.zzmq();
    return this.zzbpS;
  }
  
  @WorkerThread
  public long zzJE()
  {
    this.zzbpw.zzmq();
    return this.zzbpT;
  }
  
  @WorkerThread
  public boolean zzJF()
  {
    this.zzbpw.zzmq();
    return this.zzbpU;
  }
  
  @WorkerThread
  public long zzJG()
  {
    this.zzbpw.zzmq();
    return this.zzbpN;
  }
  
  @WorkerThread
  public long zzJH()
  {
    this.zzbpw.zzmq();
    return this.zzbqd;
  }
  
  @WorkerThread
  public long zzJI()
  {
    this.zzbpw.zzmq();
    return this.zzbqe;
  }
  
  @WorkerThread
  public void zzJJ()
  {
    this.zzbpw.zzmq();
    long l2 = this.zzbpN + 1L;
    long l1 = l2;
    if (l2 > 2147483647L)
    {
      this.zzbpw.zzJt().zzLc().zzj("Bundle index overflow. appId", zzati.zzfI(this.zzVQ));
      l1 = 0L;
    }
    this.zzbqc = true;
    this.zzbpN = l1;
  }
  
  @WorkerThread
  public long zzJK()
  {
    this.zzbpw.zzmq();
    return this.zzbpV;
  }
  
  @WorkerThread
  public long zzJL()
  {
    this.zzbpw.zzmq();
    return this.zzbpW;
  }
  
  @WorkerThread
  public long zzJM()
  {
    this.zzbpw.zzmq();
    return this.zzbpX;
  }
  
  @WorkerThread
  public long zzJN()
  {
    this.zzbpw.zzmq();
    return this.zzbpY;
  }
  
  @WorkerThread
  public long zzJO()
  {
    this.zzbpw.zzmq();
    return this.zzbqa;
  }
  
  @WorkerThread
  public long zzJP()
  {
    this.zzbpw.zzmq();
    return this.zzbpZ;
  }
  
  @WorkerThread
  public String zzJQ()
  {
    this.zzbpw.zzmq();
    return this.zzbqb;
  }
  
  @WorkerThread
  public String zzJR()
  {
    this.zzbpw.zzmq();
    String str = this.zzbqb;
    zzfm(null);
    return str;
  }
  
  @WorkerThread
  public void zzJw()
  {
    this.zzbpw.zzmq();
    this.zzbqc = false;
  }
  
  @WorkerThread
  public String zzJx()
  {
    this.zzbpw.zzmq();
    return this.zzbpL;
  }
  
  @WorkerThread
  public String zzJy()
  {
    this.zzbpw.zzmq();
    return this.zzbpM;
  }
  
  @WorkerThread
  public long zzJz()
  {
    this.zzbpw.zzmq();
    return this.zzbpO;
  }
  
  @WorkerThread
  public void zzX(long paramLong)
  {
    this.zzbpw.zzmq();
    boolean bool2 = this.zzbqc;
    if (this.zzbpO != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqc = (bool1 | bool2);
      this.zzbpO = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzY(long paramLong)
  {
    this.zzbpw.zzmq();
    boolean bool2 = this.zzbqc;
    if (this.zzbpP != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqc = (bool1 | bool2);
      this.zzbpP = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzZ(long paramLong)
  {
    this.zzbpw.zzmq();
    boolean bool2 = this.zzbqc;
    if (this.zzbpQ != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqc = (bool1 | bool2);
      this.zzbpQ = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzaa(long paramLong)
  {
    this.zzbpw.zzmq();
    boolean bool2 = this.zzbqc;
    if (this.zzbpS != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqc = (bool1 | bool2);
      this.zzbpS = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzab(long paramLong)
  {
    this.zzbpw.zzmq();
    boolean bool2 = this.zzbqc;
    if (this.zzbpT != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqc = (bool1 | bool2);
      this.zzbpT = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzac(long paramLong)
  {
    boolean bool1 = true;
    boolean bool2;
    if (paramLong >= 0L)
    {
      bool2 = true;
      zzac.zzas(bool2);
      this.zzbpw.zzmq();
      bool2 = this.zzbqc;
      if (this.zzbpN == paramLong) {
        break label58;
      }
    }
    for (;;)
    {
      this.zzbqc = (bool2 | bool1);
      this.zzbpN = paramLong;
      return;
      bool2 = false;
      break;
      label58:
      bool1 = false;
    }
  }
  
  @WorkerThread
  public void zzad(long paramLong)
  {
    this.zzbpw.zzmq();
    boolean bool2 = this.zzbqc;
    if (this.zzbqd != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqc = (bool1 | bool2);
      this.zzbqd = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzae(long paramLong)
  {
    this.zzbpw.zzmq();
    boolean bool2 = this.zzbqc;
    if (this.zzbqe != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqc = (bool1 | bool2);
      this.zzbqe = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzaf(long paramLong)
  {
    this.zzbpw.zzmq();
    boolean bool2 = this.zzbqc;
    if (this.zzbpV != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqc = (bool1 | bool2);
      this.zzbpV = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzag(long paramLong)
  {
    this.zzbpw.zzmq();
    boolean bool2 = this.zzbqc;
    if (this.zzbpW != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqc = (bool1 | bool2);
      this.zzbpW = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzah(long paramLong)
  {
    this.zzbpw.zzmq();
    boolean bool2 = this.zzbqc;
    if (this.zzbpX != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqc = (bool1 | bool2);
      this.zzbpX = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzai(long paramLong)
  {
    this.zzbpw.zzmq();
    boolean bool2 = this.zzbqc;
    if (this.zzbpY != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqc = (bool1 | bool2);
      this.zzbpY = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzaj(long paramLong)
  {
    this.zzbpw.zzmq();
    boolean bool2 = this.zzbqc;
    if (this.zzbqa != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqc = (bool1 | bool2);
      this.zzbqa = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzak(long paramLong)
  {
    this.zzbpw.zzmq();
    boolean bool2 = this.zzbqc;
    if (this.zzbpZ != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqc = (bool1 | bool2);
      this.zzbpZ = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzfh(String paramString)
  {
    this.zzbpw.zzmq();
    boolean bool2 = this.zzbqc;
    if (!zzaue.zzab(this.zzaHB, paramString)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqc = (bool1 | bool2);
      this.zzaHB = paramString;
      return;
    }
  }
  
  @WorkerThread
  public void zzfi(String paramString)
  {
    this.zzbpw.zzmq();
    String str = paramString;
    if (TextUtils.isEmpty(paramString)) {
      str = null;
    }
    boolean bool2 = this.zzbqc;
    if (!zzaue.zzab(this.zzbpK, str)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqc = (bool1 | bool2);
      this.zzbpK = str;
      return;
    }
  }
  
  @WorkerThread
  public void zzfj(String paramString)
  {
    this.zzbpw.zzmq();
    boolean bool2 = this.zzbqc;
    if (!zzaue.zzab(this.zzbpL, paramString)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqc = (bool1 | bool2);
      this.zzbpL = paramString;
      return;
    }
  }
  
  @WorkerThread
  public void zzfk(String paramString)
  {
    this.zzbpw.zzmq();
    boolean bool2 = this.zzbqc;
    if (!zzaue.zzab(this.zzbpM, paramString)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqc = (bool1 | bool2);
      this.zzbpM = paramString;
      return;
    }
  }
  
  @WorkerThread
  public void zzfl(String paramString)
  {
    this.zzbpw.zzmq();
    boolean bool2 = this.zzbqc;
    if (!zzaue.zzab(this.zzbpR, paramString)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqc = (bool1 | bool2);
      this.zzbpR = paramString;
      return;
    }
  }
  
  @WorkerThread
  public void zzfm(String paramString)
  {
    this.zzbpw.zzmq();
    boolean bool2 = this.zzbqc;
    if (!zzaue.zzab(this.zzbqb, paramString)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqc = (bool1 | bool2);
      this.zzbqb = paramString;
      return;
    }
  }
  
  @WorkerThread
  public String zzjI()
  {
    this.zzbpw.zzmq();
    return this.zzVQ;
  }
  
  @WorkerThread
  public String zzmy()
  {
    this.zzbpw.zzmq();
    return this.zzabL;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzasp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */