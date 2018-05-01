package com.google.android.gms.internal;

import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzac;

class zzatc
{
  private final String mAppId;
  private String zzVX;
  private String zzaIU;
  private String zzacM;
  private long zzbqA;
  private long zzbqB;
  private long zzbqC;
  private long zzbqD;
  private long zzbqE;
  private long zzbqF;
  private String zzbqG;
  private boolean zzbqH;
  private long zzbqI;
  private long zzbqJ;
  private final zzaue zzbqb;
  private String zzbqp;
  private String zzbqq;
  private long zzbqr;
  private long zzbqs;
  private long zzbqt;
  private long zzbqu;
  private String zzbqv;
  private long zzbqw;
  private long zzbqx;
  private boolean zzbqy;
  private long zzbqz;
  
  @WorkerThread
  zzatc(zzaue paramzzaue, String paramString)
  {
    zzac.zzw(paramzzaue);
    zzac.zzdr(paramString);
    this.zzbqb = paramzzaue;
    this.mAppId = paramString;
    this.zzbqb.zzmR();
  }
  
  @WorkerThread
  public String getAppInstanceId()
  {
    this.zzbqb.zzmR();
    return this.zzaIU;
  }
  
  @WorkerThread
  public String getGmpAppId()
  {
    this.zzbqb.zzmR();
    return this.zzVX;
  }
  
  @WorkerThread
  public void setAppVersion(String paramString)
  {
    this.zzbqb.zzmR();
    boolean bool2 = this.zzbqH;
    if (!zzaut.zzae(this.zzacM, paramString)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqH = (bool1 | bool2);
      this.zzacM = paramString;
      return;
    }
  }
  
  @WorkerThread
  public void setMeasurementEnabled(boolean paramBoolean)
  {
    this.zzbqb.zzmR();
    boolean bool2 = this.zzbqH;
    if (this.zzbqy != paramBoolean) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqH = (bool1 | bool2);
      this.zzbqy = paramBoolean;
      return;
    }
  }
  
  @WorkerThread
  public long zzKA()
  {
    this.zzbqb.zzmR();
    return this.zzbqJ;
  }
  
  @WorkerThread
  public void zzKB()
  {
    this.zzbqb.zzmR();
    long l2 = this.zzbqr + 1L;
    long l1 = l2;
    if (l2 > 2147483647L)
    {
      this.zzbqb.zzKl().zzMb().zzj("Bundle index overflow. appId", zzatx.zzfE(this.mAppId));
      l1 = 0L;
    }
    this.zzbqH = true;
    this.zzbqr = l1;
  }
  
  @WorkerThread
  public long zzKC()
  {
    this.zzbqb.zzmR();
    return this.zzbqA;
  }
  
  @WorkerThread
  public long zzKD()
  {
    this.zzbqb.zzmR();
    return this.zzbqB;
  }
  
  @WorkerThread
  public long zzKE()
  {
    this.zzbqb.zzmR();
    return this.zzbqC;
  }
  
  @WorkerThread
  public long zzKF()
  {
    this.zzbqb.zzmR();
    return this.zzbqD;
  }
  
  @WorkerThread
  public long zzKG()
  {
    this.zzbqb.zzmR();
    return this.zzbqF;
  }
  
  @WorkerThread
  public long zzKH()
  {
    this.zzbqb.zzmR();
    return this.zzbqE;
  }
  
  @WorkerThread
  public String zzKI()
  {
    this.zzbqb.zzmR();
    return this.zzbqG;
  }
  
  @WorkerThread
  public String zzKJ()
  {
    this.zzbqb.zzmR();
    String str = this.zzbqG;
    zzfi(null);
    return str;
  }
  
  @WorkerThread
  public void zzKo()
  {
    this.zzbqb.zzmR();
    this.zzbqH = false;
  }
  
  @WorkerThread
  public String zzKp()
  {
    this.zzbqb.zzmR();
    return this.zzbqp;
  }
  
  @WorkerThread
  public String zzKq()
  {
    this.zzbqb.zzmR();
    return this.zzbqq;
  }
  
  @WorkerThread
  public long zzKr()
  {
    this.zzbqb.zzmR();
    return this.zzbqs;
  }
  
  @WorkerThread
  public long zzKs()
  {
    this.zzbqb.zzmR();
    return this.zzbqt;
  }
  
  @WorkerThread
  public long zzKt()
  {
    this.zzbqb.zzmR();
    return this.zzbqu;
  }
  
  @WorkerThread
  public String zzKu()
  {
    this.zzbqb.zzmR();
    return this.zzbqv;
  }
  
  @WorkerThread
  public long zzKv()
  {
    this.zzbqb.zzmR();
    return this.zzbqw;
  }
  
  @WorkerThread
  public long zzKw()
  {
    this.zzbqb.zzmR();
    return this.zzbqx;
  }
  
  @WorkerThread
  public boolean zzKx()
  {
    this.zzbqb.zzmR();
    return this.zzbqy;
  }
  
  @WorkerThread
  public long zzKy()
  {
    this.zzbqb.zzmR();
    return this.zzbqr;
  }
  
  @WorkerThread
  public long zzKz()
  {
    this.zzbqb.zzmR();
    return this.zzbqI;
  }
  
  @WorkerThread
  public void zzY(long paramLong)
  {
    this.zzbqb.zzmR();
    boolean bool2 = this.zzbqH;
    if (this.zzbqs != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqH = (bool1 | bool2);
      this.zzbqs = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzZ(long paramLong)
  {
    this.zzbqb.zzmR();
    boolean bool2 = this.zzbqH;
    if (this.zzbqt != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqH = (bool1 | bool2);
      this.zzbqt = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzaa(long paramLong)
  {
    this.zzbqb.zzmR();
    boolean bool2 = this.zzbqH;
    if (this.zzbqu != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqH = (bool1 | bool2);
      this.zzbqu = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzab(long paramLong)
  {
    this.zzbqb.zzmR();
    boolean bool2 = this.zzbqH;
    if (this.zzbqw != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqH = (bool1 | bool2);
      this.zzbqw = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzac(long paramLong)
  {
    this.zzbqb.zzmR();
    boolean bool2 = this.zzbqH;
    if (this.zzbqx != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqH = (bool1 | bool2);
      this.zzbqx = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzad(long paramLong)
  {
    boolean bool1 = true;
    boolean bool2;
    if (paramLong >= 0L)
    {
      bool2 = true;
      zzac.zzaw(bool2);
      this.zzbqb.zzmR();
      bool2 = this.zzbqH;
      if (this.zzbqr == paramLong) {
        break label58;
      }
    }
    for (;;)
    {
      this.zzbqH = (bool2 | bool1);
      this.zzbqr = paramLong;
      return;
      bool2 = false;
      break;
      label58:
      bool1 = false;
    }
  }
  
  @WorkerThread
  public void zzae(long paramLong)
  {
    this.zzbqb.zzmR();
    boolean bool2 = this.zzbqH;
    if (this.zzbqI != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqH = (bool1 | bool2);
      this.zzbqI = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzaf(long paramLong)
  {
    this.zzbqb.zzmR();
    boolean bool2 = this.zzbqH;
    if (this.zzbqJ != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqH = (bool1 | bool2);
      this.zzbqJ = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzag(long paramLong)
  {
    this.zzbqb.zzmR();
    boolean bool2 = this.zzbqH;
    if (this.zzbqA != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqH = (bool1 | bool2);
      this.zzbqA = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzah(long paramLong)
  {
    this.zzbqb.zzmR();
    boolean bool2 = this.zzbqH;
    if (this.zzbqB != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqH = (bool1 | bool2);
      this.zzbqB = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzai(long paramLong)
  {
    this.zzbqb.zzmR();
    boolean bool2 = this.zzbqH;
    if (this.zzbqC != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqH = (bool1 | bool2);
      this.zzbqC = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzaj(long paramLong)
  {
    this.zzbqb.zzmR();
    boolean bool2 = this.zzbqH;
    if (this.zzbqD != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqH = (bool1 | bool2);
      this.zzbqD = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzak(long paramLong)
  {
    this.zzbqb.zzmR();
    boolean bool2 = this.zzbqH;
    if (this.zzbqF != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqH = (bool1 | bool2);
      this.zzbqF = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzal(long paramLong)
  {
    this.zzbqb.zzmR();
    boolean bool2 = this.zzbqH;
    if (this.zzbqE != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqH = (bool1 | bool2);
      this.zzbqE = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzam(long paramLong)
  {
    this.zzbqb.zzmR();
    boolean bool2 = this.zzbqH;
    if (this.zzbqz != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqH = (bool1 | bool2);
      this.zzbqz = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public void zzfd(String paramString)
  {
    this.zzbqb.zzmR();
    boolean bool2 = this.zzbqH;
    if (!zzaut.zzae(this.zzaIU, paramString)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqH = (bool1 | bool2);
      this.zzaIU = paramString;
      return;
    }
  }
  
  @WorkerThread
  public void zzfe(String paramString)
  {
    this.zzbqb.zzmR();
    String str = paramString;
    if (TextUtils.isEmpty(paramString)) {
      str = null;
    }
    boolean bool2 = this.zzbqH;
    if (!zzaut.zzae(this.zzVX, str)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqH = (bool1 | bool2);
      this.zzVX = str;
      return;
    }
  }
  
  @WorkerThread
  public void zzff(String paramString)
  {
    this.zzbqb.zzmR();
    boolean bool2 = this.zzbqH;
    if (!zzaut.zzae(this.zzbqp, paramString)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqH = (bool1 | bool2);
      this.zzbqp = paramString;
      return;
    }
  }
  
  @WorkerThread
  public void zzfg(String paramString)
  {
    this.zzbqb.zzmR();
    boolean bool2 = this.zzbqH;
    if (!zzaut.zzae(this.zzbqq, paramString)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqH = (bool1 | bool2);
      this.zzbqq = paramString;
      return;
    }
  }
  
  @WorkerThread
  public void zzfh(String paramString)
  {
    this.zzbqb.zzmR();
    boolean bool2 = this.zzbqH;
    if (!zzaut.zzae(this.zzbqv, paramString)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqH = (bool1 | bool2);
      this.zzbqv = paramString;
      return;
    }
  }
  
  @WorkerThread
  public void zzfi(String paramString)
  {
    this.zzbqb.zzmR();
    boolean bool2 = this.zzbqH;
    if (!zzaut.zzae(this.zzbqG, paramString)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzbqH = (bool1 | bool2);
      this.zzbqG = paramString;
      return;
    }
  }
  
  @WorkerThread
  public String zzke()
  {
    this.zzbqb.zzmR();
    return this.mAppId;
  }
  
  @WorkerThread
  public String zzmZ()
  {
    this.zzbqb.zzmR();
    return this.zzacM;
  }
  
  @WorkerThread
  public long zzuW()
  {
    this.zzbqb.zzmR();
    return this.zzbqz;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzatc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */