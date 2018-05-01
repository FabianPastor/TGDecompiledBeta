package com.google.android.gms.internal;

import android.text.TextUtils;
import com.google.android.gms.common.internal.zzbq;

final class zzcgh
{
  private final String mAppId;
  private String zzcwz;
  private String zzdra;
  private String zzggb;
  private final zzcim zziwf;
  private String zziww;
  private String zziwx;
  private long zziwy;
  private long zziwz;
  private long zzixa;
  private long zzixb;
  private String zzixc;
  private long zzixd;
  private long zzixe;
  private boolean zzixf;
  private long zzixg;
  private boolean zzixh;
  private long zzixi;
  private long zzixj;
  private long zzixk;
  private long zzixl;
  private long zzixm;
  private long zzixn;
  private String zzixo;
  private boolean zzixp;
  private long zzixq;
  private long zzixr;
  
  zzcgh(zzcim paramzzcim, String paramString)
  {
    zzbq.checkNotNull(paramzzcim);
    zzbq.zzgm(paramString);
    this.zziwf = paramzzcim;
    this.mAppId = paramString;
    this.zziwf.zzawx().zzve();
  }
  
  public final String getAppId()
  {
    this.zziwf.zzawx().zzve();
    return this.mAppId;
  }
  
  public final String getAppInstanceId()
  {
    this.zziwf.zzawx().zzve();
    return this.zzggb;
  }
  
  public final String getGmpAppId()
  {
    this.zziwf.zzawx().zzve();
    return this.zzcwz;
  }
  
  public final void setAppVersion(String paramString)
  {
    this.zziwf.zzawx().zzve();
    boolean bool2 = this.zzixp;
    if (!zzclq.zzas(this.zzdra, paramString)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzixp = (bool1 | bool2);
      this.zzdra = paramString;
      return;
    }
  }
  
  public final void setMeasurementEnabled(boolean paramBoolean)
  {
    this.zziwf.zzawx().zzve();
    boolean bool2 = this.zzixp;
    if (this.zzixf != paramBoolean) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzixp = (bool1 | bool2);
      this.zzixf = paramBoolean;
      return;
    }
  }
  
  public final void zzal(long paramLong)
  {
    this.zziwf.zzawx().zzve();
    boolean bool2 = this.zzixp;
    if (this.zziwz != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzixp = (bool1 | bool2);
      this.zziwz = paramLong;
      return;
    }
  }
  
  public final void zzam(long paramLong)
  {
    this.zziwf.zzawx().zzve();
    boolean bool2 = this.zzixp;
    if (this.zzixa != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzixp = (bool1 | bool2);
      this.zzixa = paramLong;
      return;
    }
  }
  
  public final void zzan(long paramLong)
  {
    this.zziwf.zzawx().zzve();
    boolean bool2 = this.zzixp;
    if (this.zzixb != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzixp = (bool1 | bool2);
      this.zzixb = paramLong;
      return;
    }
  }
  
  public final void zzao(long paramLong)
  {
    this.zziwf.zzawx().zzve();
    boolean bool2 = this.zzixp;
    if (this.zzixd != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzixp = (bool1 | bool2);
      this.zzixd = paramLong;
      return;
    }
  }
  
  public final void zzap(long paramLong)
  {
    this.zziwf.zzawx().zzve();
    boolean bool2 = this.zzixp;
    if (this.zzixe != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzixp = (bool1 | bool2);
      this.zzixe = paramLong;
      return;
    }
  }
  
  public final void zzaq(long paramLong)
  {
    boolean bool1 = true;
    boolean bool2;
    if (paramLong >= 0L)
    {
      bool2 = true;
      zzbq.checkArgument(bool2);
      this.zziwf.zzawx().zzve();
      bool2 = this.zzixp;
      if (this.zziwy == paramLong) {
        break label61;
      }
    }
    for (;;)
    {
      this.zzixp = (bool2 | bool1);
      this.zziwy = paramLong;
      return;
      bool2 = false;
      break;
      label61:
      bool1 = false;
    }
  }
  
  public final void zzar(long paramLong)
  {
    this.zziwf.zzawx().zzve();
    boolean bool2 = this.zzixp;
    if (this.zzixq != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzixp = (bool1 | bool2);
      this.zzixq = paramLong;
      return;
    }
  }
  
  public final void zzas(long paramLong)
  {
    this.zziwf.zzawx().zzve();
    boolean bool2 = this.zzixp;
    if (this.zzixr != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzixp = (bool1 | bool2);
      this.zzixr = paramLong;
      return;
    }
  }
  
  public final void zzat(long paramLong)
  {
    this.zziwf.zzawx().zzve();
    boolean bool2 = this.zzixp;
    if (this.zzixi != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzixp = (bool1 | bool2);
      this.zzixi = paramLong;
      return;
    }
  }
  
  public final void zzau(long paramLong)
  {
    this.zziwf.zzawx().zzve();
    boolean bool2 = this.zzixp;
    if (this.zzixj != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzixp = (bool1 | bool2);
      this.zzixj = paramLong;
      return;
    }
  }
  
  public final void zzav(long paramLong)
  {
    this.zziwf.zzawx().zzve();
    boolean bool2 = this.zzixp;
    if (this.zzixk != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzixp = (bool1 | bool2);
      this.zzixk = paramLong;
      return;
    }
  }
  
  public final void zzaw(long paramLong)
  {
    this.zziwf.zzawx().zzve();
    boolean bool2 = this.zzixp;
    if (this.zzixl != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzixp = (bool1 | bool2);
      this.zzixl = paramLong;
      return;
    }
  }
  
  public final void zzax(long paramLong)
  {
    this.zziwf.zzawx().zzve();
    boolean bool2 = this.zzixp;
    if (this.zzixn != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzixp = (bool1 | bool2);
      this.zzixn = paramLong;
      return;
    }
  }
  
  public final void zzaxb()
  {
    this.zziwf.zzawx().zzve();
    this.zzixp = false;
  }
  
  public final String zzaxc()
  {
    this.zziwf.zzawx().zzve();
    return this.zziww;
  }
  
  public final String zzaxd()
  {
    this.zziwf.zzawx().zzve();
    return this.zziwx;
  }
  
  public final long zzaxe()
  {
    this.zziwf.zzawx().zzve();
    return this.zziwz;
  }
  
  public final long zzaxf()
  {
    this.zziwf.zzawx().zzve();
    return this.zzixa;
  }
  
  public final long zzaxg()
  {
    this.zziwf.zzawx().zzve();
    return this.zzixb;
  }
  
  public final String zzaxh()
  {
    this.zziwf.zzawx().zzve();
    return this.zzixc;
  }
  
  public final long zzaxi()
  {
    this.zziwf.zzawx().zzve();
    return this.zzixd;
  }
  
  public final long zzaxj()
  {
    this.zziwf.zzawx().zzve();
    return this.zzixe;
  }
  
  public final boolean zzaxk()
  {
    this.zziwf.zzawx().zzve();
    return this.zzixf;
  }
  
  public final long zzaxl()
  {
    this.zziwf.zzawx().zzve();
    return this.zziwy;
  }
  
  public final long zzaxm()
  {
    this.zziwf.zzawx().zzve();
    return this.zzixq;
  }
  
  public final long zzaxn()
  {
    this.zziwf.zzawx().zzve();
    return this.zzixr;
  }
  
  public final void zzaxo()
  {
    this.zziwf.zzawx().zzve();
    long l2 = this.zziwy + 1L;
    long l1 = l2;
    if (l2 > 2147483647L)
    {
      this.zziwf.zzawy().zzazf().zzj("Bundle index overflow. appId", zzchm.zzjk(this.mAppId));
      l1 = 0L;
    }
    this.zzixp = true;
    this.zziwy = l1;
  }
  
  public final long zzaxp()
  {
    this.zziwf.zzawx().zzve();
    return this.zzixi;
  }
  
  public final long zzaxq()
  {
    this.zziwf.zzawx().zzve();
    return this.zzixj;
  }
  
  public final long zzaxr()
  {
    this.zziwf.zzawx().zzve();
    return this.zzixk;
  }
  
  public final long zzaxs()
  {
    this.zziwf.zzawx().zzve();
    return this.zzixl;
  }
  
  public final long zzaxt()
  {
    this.zziwf.zzawx().zzve();
    return this.zzixn;
  }
  
  public final long zzaxu()
  {
    this.zziwf.zzawx().zzve();
    return this.zzixm;
  }
  
  public final String zzaxv()
  {
    this.zziwf.zzawx().zzve();
    return this.zzixo;
  }
  
  public final String zzaxw()
  {
    this.zziwf.zzawx().zzve();
    String str = this.zzixo;
    zziw(null);
    return str;
  }
  
  public final long zzaxx()
  {
    this.zziwf.zzawx().zzve();
    return this.zzixg;
  }
  
  public final boolean zzaxy()
  {
    this.zziwf.zzawx().zzve();
    return this.zzixh;
  }
  
  public final void zzay(long paramLong)
  {
    this.zziwf.zzawx().zzve();
    boolean bool2 = this.zzixp;
    if (this.zzixm != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzixp = (bool1 | bool2);
      this.zzixm = paramLong;
      return;
    }
  }
  
  public final void zzaz(long paramLong)
  {
    this.zziwf.zzawx().zzve();
    boolean bool2 = this.zzixp;
    if (this.zzixg != paramLong) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzixp = (bool1 | bool2);
      this.zzixg = paramLong;
      return;
    }
  }
  
  public final void zzbl(boolean paramBoolean)
  {
    this.zziwf.zzawx().zzve();
    if (this.zzixh != paramBoolean) {}
    for (boolean bool = true;; bool = false)
    {
      this.zzixp = bool;
      this.zzixh = paramBoolean;
      return;
    }
  }
  
  public final void zzir(String paramString)
  {
    this.zziwf.zzawx().zzve();
    boolean bool2 = this.zzixp;
    if (!zzclq.zzas(this.zzggb, paramString)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzixp = (bool1 | bool2);
      this.zzggb = paramString;
      return;
    }
  }
  
  public final void zzis(String paramString)
  {
    this.zziwf.zzawx().zzve();
    String str = paramString;
    if (TextUtils.isEmpty(paramString)) {
      str = null;
    }
    boolean bool2 = this.zzixp;
    if (!zzclq.zzas(this.zzcwz, str)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzixp = (bool1 | bool2);
      this.zzcwz = str;
      return;
    }
  }
  
  public final void zzit(String paramString)
  {
    this.zziwf.zzawx().zzve();
    boolean bool2 = this.zzixp;
    if (!zzclq.zzas(this.zziww, paramString)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzixp = (bool1 | bool2);
      this.zziww = paramString;
      return;
    }
  }
  
  public final void zziu(String paramString)
  {
    this.zziwf.zzawx().zzve();
    boolean bool2 = this.zzixp;
    if (!zzclq.zzas(this.zziwx, paramString)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzixp = (bool1 | bool2);
      this.zziwx = paramString;
      return;
    }
  }
  
  public final void zziv(String paramString)
  {
    this.zziwf.zzawx().zzve();
    boolean bool2 = this.zzixp;
    if (!zzclq.zzas(this.zzixc, paramString)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzixp = (bool1 | bool2);
      this.zzixc = paramString;
      return;
    }
  }
  
  public final void zziw(String paramString)
  {
    this.zziwf.zzawx().zzve();
    boolean bool2 = this.zzixp;
    if (!zzclq.zzas(this.zzixo, paramString)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.zzixp = (bool1 | bool2);
      this.zzixo = paramString;
      return;
    }
  }
  
  public final String zzvj()
  {
    this.zziwf.zzawx().zzve();
    return this.zzdra;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcgh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */