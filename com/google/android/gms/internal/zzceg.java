package com.google.android.gms.internal;

import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzbo;

final class zzceg
{
  private final String mAppId;
  private String zzXB;
  private String zzaKE;
  private String zzaeI;
  private long zzboA;
  private String zzboB;
  private long zzboC;
  private long zzboD;
  private boolean zzboE;
  private long zzboF;
  private long zzboG;
  private long zzboH;
  private long zzboI;
  private long zzboJ;
  private long zzboK;
  private long zzboL;
  private String zzboM;
  private boolean zzboN;
  private long zzboO;
  private long zzboP;
  private final zzcgl zzboe;
  private String zzbov;
  private String zzbow;
  private long zzbox;
  private long zzboy;
  private long zzboz;
  
  @WorkerThread
  zzceg(zzcgl paramzzcgl, String paramString)
  {
    zzbo.zzu(paramzzcgl);
    zzbo.zzcF(paramString);
    this.zzboe = paramzzcgl;
    this.mAppId = paramString;
    this.zzboe.zzwE().zzjC();
  }
  
  @WorkerThread
  public final String getAppInstanceId()
  {
    this.zzboe.zzwE().zzjC();
    return this.zzaKE;
  }
  
  @WorkerThread
  public final String getGmpAppId()
  {
    this.zzboe.zzwE().zzjC();
    return this.zzXB;
  }
  
  @WorkerThread
  public final void setAppVersion(String paramString)
  {
    this.zzboe.zzwE().zzjC();
    boolean bool2 = this.zzboN;
    if (!zzcjl.zzR(this.zzaeI, paramString)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzboN = (bool1 | bool2);
      this.zzaeI = paramString;
      return;
    }
  }
  
  @WorkerThread
  public final void setMeasurementEnabled(boolean paramBoolean)
  {
    this.zzboe.zzwE().zzjC();
    boolean bool2 = this.zzboN;
    if (this.zzboE != paramBoolean) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzboN = (bool1 | bool2);
      this.zzboE = paramBoolean;
      return;
    }
  }
  
  @WorkerThread
  public final void zzL(long paramLong)
  {
    this.zzboe.zzwE().zzjC();
    boolean bool2 = this.zzboN;
    if (this.zzboy != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzboN = (bool1 | bool2);
      this.zzboy = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public final void zzM(long paramLong)
  {
    this.zzboe.zzwE().zzjC();
    boolean bool2 = this.zzboN;
    if (this.zzboz != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzboN = (bool1 | bool2);
      this.zzboz = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public final void zzN(long paramLong)
  {
    this.zzboe.zzwE().zzjC();
    boolean bool2 = this.zzboN;
    if (this.zzboA != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzboN = (bool1 | bool2);
      this.zzboA = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public final void zzO(long paramLong)
  {
    this.zzboe.zzwE().zzjC();
    boolean bool2 = this.zzboN;
    if (this.zzboC != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzboN = (bool1 | bool2);
      this.zzboC = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public final void zzP(long paramLong)
  {
    this.zzboe.zzwE().zzjC();
    boolean bool2 = this.zzboN;
    if (this.zzboD != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzboN = (bool1 | bool2);
      this.zzboD = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public final void zzQ(long paramLong)
  {
    boolean bool1 = true;
    boolean bool2;
    if (paramLong >= 0L)
    {
      bool2 = true;
      zzbo.zzaf(bool2);
      this.zzboe.zzwE().zzjC();
      bool2 = this.zzboN;
      if (this.zzbox == paramLong) {
        break label61;
      }
    }
    for (;;)
    {
      this.zzboN = (bool2 | bool1);
      this.zzbox = paramLong;
      return;
      bool2 = false;
      break;
      label61:
      bool1 = false;
    }
  }
  
  @WorkerThread
  public final void zzR(long paramLong)
  {
    this.zzboe.zzwE().zzjC();
    boolean bool2 = this.zzboN;
    if (this.zzboO != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzboN = (bool1 | bool2);
      this.zzboO = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public final void zzS(long paramLong)
  {
    this.zzboe.zzwE().zzjC();
    boolean bool2 = this.zzboN;
    if (this.zzboP != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzboN = (bool1 | bool2);
      this.zzboP = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public final void zzT(long paramLong)
  {
    this.zzboe.zzwE().zzjC();
    boolean bool2 = this.zzboN;
    if (this.zzboG != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzboN = (bool1 | bool2);
      this.zzboG = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public final void zzU(long paramLong)
  {
    this.zzboe.zzwE().zzjC();
    boolean bool2 = this.zzboN;
    if (this.zzboH != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzboN = (bool1 | bool2);
      this.zzboH = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public final void zzV(long paramLong)
  {
    this.zzboe.zzwE().zzjC();
    boolean bool2 = this.zzboN;
    if (this.zzboI != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzboN = (bool1 | bool2);
      this.zzboI = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public final void zzW(long paramLong)
  {
    this.zzboe.zzwE().zzjC();
    boolean bool2 = this.zzboN;
    if (this.zzboJ != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzboN = (bool1 | bool2);
      this.zzboJ = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public final void zzX(long paramLong)
  {
    this.zzboe.zzwE().zzjC();
    boolean bool2 = this.zzboN;
    if (this.zzboL != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzboN = (bool1 | bool2);
      this.zzboL = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public final void zzY(long paramLong)
  {
    this.zzboe.zzwE().zzjC();
    boolean bool2 = this.zzboN;
    if (this.zzboK != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzboN = (bool1 | bool2);
      this.zzboK = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public final void zzZ(long paramLong)
  {
    this.zzboe.zzwE().zzjC();
    boolean bool2 = this.zzboN;
    if (this.zzboF != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzboN = (bool1 | bool2);
      this.zzboF = paramLong;
      return;
    }
  }
  
  @WorkerThread
  public final void zzdG(String paramString)
  {
    this.zzboe.zzwE().zzjC();
    boolean bool2 = this.zzboN;
    if (!zzcjl.zzR(this.zzaKE, paramString)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzboN = (bool1 | bool2);
      this.zzaKE = paramString;
      return;
    }
  }
  
  @WorkerThread
  public final void zzdH(String paramString)
  {
    this.zzboe.zzwE().zzjC();
    String str = paramString;
    if (TextUtils.isEmpty(paramString)) {
      str = null;
    }
    boolean bool2 = this.zzboN;
    if (!zzcjl.zzR(this.zzXB, str)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzboN = (bool1 | bool2);
      this.zzXB = str;
      return;
    }
  }
  
  @WorkerThread
  public final void zzdI(String paramString)
  {
    this.zzboe.zzwE().zzjC();
    boolean bool2 = this.zzboN;
    if (!zzcjl.zzR(this.zzbov, paramString)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzboN = (bool1 | bool2);
      this.zzbov = paramString;
      return;
    }
  }
  
  @WorkerThread
  public final void zzdJ(String paramString)
  {
    this.zzboe.zzwE().zzjC();
    boolean bool2 = this.zzboN;
    if (!zzcjl.zzR(this.zzbow, paramString)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzboN = (bool1 | bool2);
      this.zzbow = paramString;
      return;
    }
  }
  
  @WorkerThread
  public final void zzdK(String paramString)
  {
    this.zzboe.zzwE().zzjC();
    boolean bool2 = this.zzboN;
    if (!zzcjl.zzR(this.zzboB, paramString)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzboN = (bool1 | bool2);
      this.zzboB = paramString;
      return;
    }
  }
  
  @WorkerThread
  public final void zzdL(String paramString)
  {
    this.zzboe.zzwE().zzjC();
    boolean bool2 = this.zzboN;
    if (!zzcjl.zzR(this.zzboM, paramString)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzboN = (bool1 | bool2);
      this.zzboM = paramString;
      return;
    }
  }
  
  @WorkerThread
  public final String zzhl()
  {
    this.zzboe.zzwE().zzjC();
    return this.mAppId;
  }
  
  @WorkerThread
  public final String zzjH()
  {
    this.zzboe.zzwE().zzjC();
    return this.zzaeI;
  }
  
  @WorkerThread
  public final void zzwI()
  {
    this.zzboe.zzwE().zzjC();
    this.zzboN = false;
  }
  
  @WorkerThread
  public final String zzwJ()
  {
    this.zzboe.zzwE().zzjC();
    return this.zzbov;
  }
  
  @WorkerThread
  public final String zzwK()
  {
    this.zzboe.zzwE().zzjC();
    return this.zzbow;
  }
  
  @WorkerThread
  public final long zzwL()
  {
    this.zzboe.zzwE().zzjC();
    return this.zzboy;
  }
  
  @WorkerThread
  public final long zzwM()
  {
    this.zzboe.zzwE().zzjC();
    return this.zzboz;
  }
  
  @WorkerThread
  public final long zzwN()
  {
    this.zzboe.zzwE().zzjC();
    return this.zzboA;
  }
  
  @WorkerThread
  public final String zzwO()
  {
    this.zzboe.zzwE().zzjC();
    return this.zzboB;
  }
  
  @WorkerThread
  public final long zzwP()
  {
    this.zzboe.zzwE().zzjC();
    return this.zzboC;
  }
  
  @WorkerThread
  public final long zzwQ()
  {
    this.zzboe.zzwE().zzjC();
    return this.zzboD;
  }
  
  @WorkerThread
  public final boolean zzwR()
  {
    this.zzboe.zzwE().zzjC();
    return this.zzboE;
  }
  
  @WorkerThread
  public final long zzwS()
  {
    this.zzboe.zzwE().zzjC();
    return this.zzbox;
  }
  
  @WorkerThread
  public final long zzwT()
  {
    this.zzboe.zzwE().zzjC();
    return this.zzboO;
  }
  
  @WorkerThread
  public final long zzwU()
  {
    this.zzboe.zzwE().zzjC();
    return this.zzboP;
  }
  
  @WorkerThread
  public final void zzwV()
  {
    this.zzboe.zzwE().zzjC();
    long l2 = this.zzbox + 1L;
    long l1 = l2;
    if (l2 > 2147483647L)
    {
      this.zzboe.zzwF().zzyz().zzj("Bundle index overflow. appId", zzcfl.zzdZ(this.mAppId));
      l1 = 0L;
    }
    this.zzboN = true;
    this.zzbox = l1;
  }
  
  @WorkerThread
  public final long zzwW()
  {
    this.zzboe.zzwE().zzjC();
    return this.zzboG;
  }
  
  @WorkerThread
  public final long zzwX()
  {
    this.zzboe.zzwE().zzjC();
    return this.zzboH;
  }
  
  @WorkerThread
  public final long zzwY()
  {
    this.zzboe.zzwE().zzjC();
    return this.zzboI;
  }
  
  @WorkerThread
  public final long zzwZ()
  {
    this.zzboe.zzwE().zzjC();
    return this.zzboJ;
  }
  
  @WorkerThread
  public final long zzxa()
  {
    this.zzboe.zzwE().zzjC();
    return this.zzboL;
  }
  
  @WorkerThread
  public final long zzxb()
  {
    this.zzboe.zzwE().zzjC();
    return this.zzboK;
  }
  
  @WorkerThread
  public final String zzxc()
  {
    this.zzboe.zzwE().zzjC();
    return this.zzboM;
  }
  
  @WorkerThread
  public final String zzxd()
  {
    this.zzboe.zzwE().zzjC();
    String str = this.zzboM;
    zzdL(null);
    return str;
  }
  
  @WorkerThread
  public final long zzxe()
  {
    this.zzboe.zzwE().zzjC();
    return this.zzboF;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzceg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */