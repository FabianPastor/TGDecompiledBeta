package com.google.android.gms.internal;

import java.io.IOException;

public abstract interface zzwc
{
  public static final class zza
    extends zzasa
  {
    private static volatile zza[] awI;
    public Integer avZ;
    public zzwc.zzf awJ;
    public zzwc.zzf awK;
    public Boolean awL;
    
    public zza()
    {
      zzbzx();
    }
    
    public static zza[] zzbzw()
    {
      if (awI == null) {}
      synchronized (zzary.btO)
      {
        if (awI == null) {
          awI = new zza[0];
        }
        return awI;
      }
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {}
      do
      {
        do
        {
          return true;
          if (!(paramObject instanceof zza)) {
            return false;
          }
          paramObject = (zza)paramObject;
          if (this.avZ == null)
          {
            if (((zza)paramObject).avZ != null) {
              return false;
            }
          }
          else if (!this.avZ.equals(((zza)paramObject).avZ)) {
            return false;
          }
          if (this.awJ == null)
          {
            if (((zza)paramObject).awJ != null) {
              return false;
            }
          }
          else if (!this.awJ.equals(((zza)paramObject).awJ)) {
            return false;
          }
          if (this.awK == null)
          {
            if (((zza)paramObject).awK != null) {
              return false;
            }
          }
          else if (!this.awK.equals(((zza)paramObject).awK)) {
            return false;
          }
          if (this.awL != null) {
            break;
          }
        } while (((zza)paramObject).awL == null);
        return false;
      } while (this.awL.equals(((zza)paramObject).awL));
      return false;
    }
    
    public int hashCode()
    {
      int m = 0;
      int n = getClass().getName().hashCode();
      int i;
      int j;
      label33:
      int k;
      if (this.avZ == null)
      {
        i = 0;
        if (this.awJ != null) {
          break label88;
        }
        j = 0;
        if (this.awK != null) {
          break label99;
        }
        k = 0;
        label42:
        if (this.awL != null) {
          break label110;
        }
      }
      for (;;)
      {
        return (k + (j + (i + (n + 527) * 31) * 31) * 31) * 31 + m;
        i = this.avZ.hashCode();
        break;
        label88:
        j = this.awJ.hashCode();
        break label33;
        label99:
        k = this.awK.hashCode();
        break label42;
        label110:
        m = this.awL.hashCode();
      }
    }
    
    public void zza(zzart paramzzart)
      throws IOException
    {
      if (this.avZ != null) {
        paramzzart.zzaf(1, this.avZ.intValue());
      }
      if (this.awJ != null) {
        paramzzart.zza(2, this.awJ);
      }
      if (this.awK != null) {
        paramzzart.zza(3, this.awK);
      }
      if (this.awL != null) {
        paramzzart.zzg(4, this.awL.booleanValue());
      }
      super.zza(paramzzart);
    }
    
    public zza zzap(zzars paramzzars)
      throws IOException
    {
      for (;;)
      {
        int i = paramzzars.bU();
        switch (i)
        {
        default: 
          if (zzasd.zzb(paramzzars, i)) {}
          break;
        case 0: 
          return this;
        case 8: 
          this.avZ = Integer.valueOf(paramzzars.bY());
          break;
        case 18: 
          if (this.awJ == null) {
            this.awJ = new zzwc.zzf();
          }
          paramzzars.zza(this.awJ);
          break;
        case 26: 
          if (this.awK == null) {
            this.awK = new zzwc.zzf();
          }
          paramzzars.zza(this.awK);
          break;
        case 32: 
          this.awL = Boolean.valueOf(paramzzars.ca());
        }
      }
    }
    
    public zza zzbzx()
    {
      this.avZ = null;
      this.awJ = null;
      this.awK = null;
      this.awL = null;
      this.btP = -1;
      return this;
    }
    
    protected int zzx()
    {
      int j = super.zzx();
      int i = j;
      if (this.avZ != null) {
        i = j + zzart.zzah(1, this.avZ.intValue());
      }
      j = i;
      if (this.awJ != null) {
        j = i + zzart.zzc(2, this.awJ);
      }
      i = j;
      if (this.awK != null) {
        i = j + zzart.zzc(3, this.awK);
      }
      j = i;
      if (this.awL != null) {
        j = i + zzart.zzh(4, this.awL.booleanValue());
      }
      return j;
    }
  }
  
  public static final class zzb
    extends zzasa
  {
    private static volatile zzb[] awM;
    public zzwc.zzc[] awN;
    public Long awO;
    public Long awP;
    public Integer count;
    public String name;
    
    public zzb()
    {
      zzbzz();
    }
    
    public static zzb[] zzbzy()
    {
      if (awM == null) {}
      synchronized (zzary.btO)
      {
        if (awM == null) {
          awM = new zzb[0];
        }
        return awM;
      }
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {}
      do
      {
        do
        {
          return true;
          if (!(paramObject instanceof zzb)) {
            return false;
          }
          paramObject = (zzb)paramObject;
          if (!zzary.equals(this.awN, ((zzb)paramObject).awN)) {
            return false;
          }
          if (this.name == null)
          {
            if (((zzb)paramObject).name != null) {
              return false;
            }
          }
          else if (!this.name.equals(((zzb)paramObject).name)) {
            return false;
          }
          if (this.awO == null)
          {
            if (((zzb)paramObject).awO != null) {
              return false;
            }
          }
          else if (!this.awO.equals(((zzb)paramObject).awO)) {
            return false;
          }
          if (this.awP == null)
          {
            if (((zzb)paramObject).awP != null) {
              return false;
            }
          }
          else if (!this.awP.equals(((zzb)paramObject).awP)) {
            return false;
          }
          if (this.count != null) {
            break;
          }
        } while (((zzb)paramObject).count == null);
        return false;
      } while (this.count.equals(((zzb)paramObject).count));
      return false;
    }
    
    public int hashCode()
    {
      int m = 0;
      int n = getClass().getName().hashCode();
      int i1 = zzary.hashCode(this.awN);
      int i;
      int j;
      label42:
      int k;
      if (this.name == null)
      {
        i = 0;
        if (this.awO != null) {
          break label103;
        }
        j = 0;
        if (this.awP != null) {
          break label114;
        }
        k = 0;
        label51:
        if (this.count != null) {
          break label125;
        }
      }
      for (;;)
      {
        return (k + (j + (i + ((n + 527) * 31 + i1) * 31) * 31) * 31) * 31 + m;
        i = this.name.hashCode();
        break;
        label103:
        j = this.awO.hashCode();
        break label42;
        label114:
        k = this.awP.hashCode();
        break label51;
        label125:
        m = this.count.hashCode();
      }
    }
    
    public void zza(zzart paramzzart)
      throws IOException
    {
      if ((this.awN != null) && (this.awN.length > 0))
      {
        int i = 0;
        while (i < this.awN.length)
        {
          zzwc.zzc localzzc = this.awN[i];
          if (localzzc != null) {
            paramzzart.zza(1, localzzc);
          }
          i += 1;
        }
      }
      if (this.name != null) {
        paramzzart.zzq(2, this.name);
      }
      if (this.awO != null) {
        paramzzart.zzb(3, this.awO.longValue());
      }
      if (this.awP != null) {
        paramzzart.zzb(4, this.awP.longValue());
      }
      if (this.count != null) {
        paramzzart.zzaf(5, this.count.intValue());
      }
      super.zza(paramzzart);
    }
    
    public zzb zzaq(zzars paramzzars)
      throws IOException
    {
      for (;;)
      {
        int i = paramzzars.bU();
        switch (i)
        {
        default: 
          if (zzasd.zzb(paramzzars, i)) {}
          break;
        case 0: 
          return this;
        case 10: 
          int j = zzasd.zzc(paramzzars, 10);
          if (this.awN == null) {}
          zzwc.zzc[] arrayOfzzc;
          for (i = 0;; i = this.awN.length)
          {
            arrayOfzzc = new zzwc.zzc[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.awN, 0, arrayOfzzc, 0, i);
              j = i;
            }
            while (j < arrayOfzzc.length - 1)
            {
              arrayOfzzc[j] = new zzwc.zzc();
              paramzzars.zza(arrayOfzzc[j]);
              paramzzars.bU();
              j += 1;
            }
          }
          arrayOfzzc[j] = new zzwc.zzc();
          paramzzars.zza(arrayOfzzc[j]);
          this.awN = arrayOfzzc;
          break;
        case 18: 
          this.name = paramzzars.readString();
          break;
        case 24: 
          this.awO = Long.valueOf(paramzzars.bX());
          break;
        case 32: 
          this.awP = Long.valueOf(paramzzars.bX());
          break;
        case 40: 
          this.count = Integer.valueOf(paramzzars.bY());
        }
      }
    }
    
    public zzb zzbzz()
    {
      this.awN = zzwc.zzc.zzcaa();
      this.name = null;
      this.awO = null;
      this.awP = null;
      this.count = null;
      this.btP = -1;
      return this;
    }
    
    protected int zzx()
    {
      int i = super.zzx();
      int j = i;
      if (this.awN != null)
      {
        j = i;
        if (this.awN.length > 0)
        {
          int k = 0;
          for (;;)
          {
            j = i;
            if (k >= this.awN.length) {
              break;
            }
            zzwc.zzc localzzc = this.awN[k];
            j = i;
            if (localzzc != null) {
              j = i + zzart.zzc(1, localzzc);
            }
            k += 1;
            i = j;
          }
        }
      }
      i = j;
      if (this.name != null) {
        i = j + zzart.zzr(2, this.name);
      }
      j = i;
      if (this.awO != null) {
        j = i + zzart.zzf(3, this.awO.longValue());
      }
      i = j;
      if (this.awP != null) {
        i = j + zzart.zzf(4, this.awP.longValue());
      }
      j = i;
      if (this.count != null) {
        j = i + zzart.zzah(5, this.count.intValue());
      }
      return j;
    }
  }
  
  public static final class zzc
    extends zzasa
  {
    private static volatile zzc[] awQ;
    public String Fe;
    public Float avV;
    public Double avW;
    public Long awR;
    public String name;
    
    public zzc()
    {
      zzcab();
    }
    
    public static zzc[] zzcaa()
    {
      if (awQ == null) {}
      synchronized (zzary.btO)
      {
        if (awQ == null) {
          awQ = new zzc[0];
        }
        return awQ;
      }
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {}
      do
      {
        do
        {
          return true;
          if (!(paramObject instanceof zzc)) {
            return false;
          }
          paramObject = (zzc)paramObject;
          if (this.name == null)
          {
            if (((zzc)paramObject).name != null) {
              return false;
            }
          }
          else if (!this.name.equals(((zzc)paramObject).name)) {
            return false;
          }
          if (this.Fe == null)
          {
            if (((zzc)paramObject).Fe != null) {
              return false;
            }
          }
          else if (!this.Fe.equals(((zzc)paramObject).Fe)) {
            return false;
          }
          if (this.awR == null)
          {
            if (((zzc)paramObject).awR != null) {
              return false;
            }
          }
          else if (!this.awR.equals(((zzc)paramObject).awR)) {
            return false;
          }
          if (this.avV == null)
          {
            if (((zzc)paramObject).avV != null) {
              return false;
            }
          }
          else if (!this.avV.equals(((zzc)paramObject).avV)) {
            return false;
          }
          if (this.avW != null) {
            break;
          }
        } while (((zzc)paramObject).avW == null);
        return false;
      } while (this.avW.equals(((zzc)paramObject).avW));
      return false;
    }
    
    public int hashCode()
    {
      int n = 0;
      int i1 = getClass().getName().hashCode();
      int i;
      int j;
      label33:
      int k;
      label42:
      int m;
      if (this.name == null)
      {
        i = 0;
        if (this.Fe != null) {
          break label104;
        }
        j = 0;
        if (this.awR != null) {
          break label115;
        }
        k = 0;
        if (this.avV != null) {
          break label126;
        }
        m = 0;
        label52:
        if (this.avW != null) {
          break label138;
        }
      }
      for (;;)
      {
        return (m + (k + (j + (i + (i1 + 527) * 31) * 31) * 31) * 31) * 31 + n;
        i = this.name.hashCode();
        break;
        label104:
        j = this.Fe.hashCode();
        break label33;
        label115:
        k = this.awR.hashCode();
        break label42;
        label126:
        m = this.avV.hashCode();
        break label52;
        label138:
        n = this.avW.hashCode();
      }
    }
    
    public void zza(zzart paramzzart)
      throws IOException
    {
      if (this.name != null) {
        paramzzart.zzq(1, this.name);
      }
      if (this.Fe != null) {
        paramzzart.zzq(2, this.Fe);
      }
      if (this.awR != null) {
        paramzzart.zzb(3, this.awR.longValue());
      }
      if (this.avV != null) {
        paramzzart.zzc(4, this.avV.floatValue());
      }
      if (this.avW != null) {
        paramzzart.zza(5, this.avW.doubleValue());
      }
      super.zza(paramzzart);
    }
    
    public zzc zzar(zzars paramzzars)
      throws IOException
    {
      for (;;)
      {
        int i = paramzzars.bU();
        switch (i)
        {
        default: 
          if (zzasd.zzb(paramzzars, i)) {}
          break;
        case 0: 
          return this;
        case 10: 
          this.name = paramzzars.readString();
          break;
        case 18: 
          this.Fe = paramzzars.readString();
          break;
        case 24: 
          this.awR = Long.valueOf(paramzzars.bX());
          break;
        case 37: 
          this.avV = Float.valueOf(paramzzars.readFloat());
          break;
        case 41: 
          this.avW = Double.valueOf(paramzzars.readDouble());
        }
      }
    }
    
    public zzc zzcab()
    {
      this.name = null;
      this.Fe = null;
      this.awR = null;
      this.avV = null;
      this.avW = null;
      this.btP = -1;
      return this;
    }
    
    protected int zzx()
    {
      int j = super.zzx();
      int i = j;
      if (this.name != null) {
        i = j + zzart.zzr(1, this.name);
      }
      j = i;
      if (this.Fe != null) {
        j = i + zzart.zzr(2, this.Fe);
      }
      i = j;
      if (this.awR != null) {
        i = j + zzart.zzf(3, this.awR.longValue());
      }
      j = i;
      if (this.avV != null) {
        j = i + zzart.zzd(4, this.avV.floatValue());
      }
      i = j;
      if (this.avW != null) {
        i = j + zzart.zzb(5, this.avW.doubleValue());
      }
      return i;
    }
  }
  
  public static final class zzd
    extends zzasa
  {
    public zzwc.zze[] awS;
    
    public zzd()
    {
      zzcac();
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {}
      do
      {
        return true;
        if (!(paramObject instanceof zzd)) {
          return false;
        }
        paramObject = (zzd)paramObject;
      } while (zzary.equals(this.awS, ((zzd)paramObject).awS));
      return false;
    }
    
    public int hashCode()
    {
      return (getClass().getName().hashCode() + 527) * 31 + zzary.hashCode(this.awS);
    }
    
    public void zza(zzart paramzzart)
      throws IOException
    {
      if ((this.awS != null) && (this.awS.length > 0))
      {
        int i = 0;
        while (i < this.awS.length)
        {
          zzwc.zze localzze = this.awS[i];
          if (localzze != null) {
            paramzzart.zza(1, localzze);
          }
          i += 1;
        }
      }
      super.zza(paramzzart);
    }
    
    public zzd zzas(zzars paramzzars)
      throws IOException
    {
      for (;;)
      {
        int i = paramzzars.bU();
        switch (i)
        {
        default: 
          if (zzasd.zzb(paramzzars, i)) {}
          break;
        case 0: 
          return this;
        case 10: 
          int j = zzasd.zzc(paramzzars, 10);
          if (this.awS == null) {}
          zzwc.zze[] arrayOfzze;
          for (i = 0;; i = this.awS.length)
          {
            arrayOfzze = new zzwc.zze[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.awS, 0, arrayOfzze, 0, i);
              j = i;
            }
            while (j < arrayOfzze.length - 1)
            {
              arrayOfzze[j] = new zzwc.zze();
              paramzzars.zza(arrayOfzze[j]);
              paramzzars.bU();
              j += 1;
            }
          }
          arrayOfzze[j] = new zzwc.zze();
          paramzzars.zza(arrayOfzze[j]);
          this.awS = arrayOfzze;
        }
      }
    }
    
    public zzd zzcac()
    {
      this.awS = zzwc.zze.zzcad();
      this.btP = -1;
      return this;
    }
    
    protected int zzx()
    {
      int i = super.zzx();
      int k = i;
      if (this.awS != null)
      {
        k = i;
        if (this.awS.length > 0)
        {
          int j = 0;
          for (;;)
          {
            k = i;
            if (j >= this.awS.length) {
              break;
            }
            zzwc.zze localzze = this.awS[j];
            k = i;
            if (localzze != null) {
              k = i + zzart.zzc(1, localzze);
            }
            j += 1;
            i = k;
          }
        }
      }
      return k;
    }
  }
  
  public static final class zze
    extends zzasa
  {
    private static volatile zze[] awT;
    public String aii;
    public String aqZ;
    public String ara;
    public String ard;
    public String arh;
    public Integer awU;
    public zzwc.zzb[] awV;
    public zzwc.zzg[] awW;
    public Long awX;
    public Long awY;
    public Long awZ;
    public Long axa;
    public Long axb;
    public String axc;
    public String axd;
    public String axe;
    public Integer axf;
    public Long axg;
    public Long axh;
    public String axi;
    public Boolean axj;
    public String axk;
    public Long axl;
    public Integer axm;
    public Boolean axn;
    public zzwc.zza[] axo;
    public Integer axp;
    public Integer axq;
    public Integer axr;
    public String axs;
    public Long axt;
    public String zzcs;
    public String zzdb;
    
    public zze()
    {
      zzcae();
    }
    
    public static zze[] zzcad()
    {
      if (awT == null) {}
      synchronized (zzary.btO)
      {
        if (awT == null) {
          awT = new zze[0];
        }
        return awT;
      }
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {}
      do
      {
        do
        {
          return true;
          if (!(paramObject instanceof zze)) {
            return false;
          }
          paramObject = (zze)paramObject;
          if (this.awU == null)
          {
            if (((zze)paramObject).awU != null) {
              return false;
            }
          }
          else if (!this.awU.equals(((zze)paramObject).awU)) {
            return false;
          }
          if (!zzary.equals(this.awV, ((zze)paramObject).awV)) {
            return false;
          }
          if (!zzary.equals(this.awW, ((zze)paramObject).awW)) {
            return false;
          }
          if (this.awX == null)
          {
            if (((zze)paramObject).awX != null) {
              return false;
            }
          }
          else if (!this.awX.equals(((zze)paramObject).awX)) {
            return false;
          }
          if (this.awY == null)
          {
            if (((zze)paramObject).awY != null) {
              return false;
            }
          }
          else if (!this.awY.equals(((zze)paramObject).awY)) {
            return false;
          }
          if (this.awZ == null)
          {
            if (((zze)paramObject).awZ != null) {
              return false;
            }
          }
          else if (!this.awZ.equals(((zze)paramObject).awZ)) {
            return false;
          }
          if (this.axa == null)
          {
            if (((zze)paramObject).axa != null) {
              return false;
            }
          }
          else if (!this.axa.equals(((zze)paramObject).axa)) {
            return false;
          }
          if (this.axb == null)
          {
            if (((zze)paramObject).axb != null) {
              return false;
            }
          }
          else if (!this.axb.equals(((zze)paramObject).axb)) {
            return false;
          }
          if (this.axc == null)
          {
            if (((zze)paramObject).axc != null) {
              return false;
            }
          }
          else if (!this.axc.equals(((zze)paramObject).axc)) {
            return false;
          }
          if (this.zzdb == null)
          {
            if (((zze)paramObject).zzdb != null) {
              return false;
            }
          }
          else if (!this.zzdb.equals(((zze)paramObject).zzdb)) {
            return false;
          }
          if (this.axd == null)
          {
            if (((zze)paramObject).axd != null) {
              return false;
            }
          }
          else if (!this.axd.equals(((zze)paramObject).axd)) {
            return false;
          }
          if (this.axe == null)
          {
            if (((zze)paramObject).axe != null) {
              return false;
            }
          }
          else if (!this.axe.equals(((zze)paramObject).axe)) {
            return false;
          }
          if (this.axf == null)
          {
            if (((zze)paramObject).axf != null) {
              return false;
            }
          }
          else if (!this.axf.equals(((zze)paramObject).axf)) {
            return false;
          }
          if (this.ara == null)
          {
            if (((zze)paramObject).ara != null) {
              return false;
            }
          }
          else if (!this.ara.equals(((zze)paramObject).ara)) {
            return false;
          }
          if (this.zzcs == null)
          {
            if (((zze)paramObject).zzcs != null) {
              return false;
            }
          }
          else if (!this.zzcs.equals(((zze)paramObject).zzcs)) {
            return false;
          }
          if (this.aii == null)
          {
            if (((zze)paramObject).aii != null) {
              return false;
            }
          }
          else if (!this.aii.equals(((zze)paramObject).aii)) {
            return false;
          }
          if (this.axg == null)
          {
            if (((zze)paramObject).axg != null) {
              return false;
            }
          }
          else if (!this.axg.equals(((zze)paramObject).axg)) {
            return false;
          }
          if (this.axh == null)
          {
            if (((zze)paramObject).axh != null) {
              return false;
            }
          }
          else if (!this.axh.equals(((zze)paramObject).axh)) {
            return false;
          }
          if (this.axi == null)
          {
            if (((zze)paramObject).axi != null) {
              return false;
            }
          }
          else if (!this.axi.equals(((zze)paramObject).axi)) {
            return false;
          }
          if (this.axj == null)
          {
            if (((zze)paramObject).axj != null) {
              return false;
            }
          }
          else if (!this.axj.equals(((zze)paramObject).axj)) {
            return false;
          }
          if (this.axk == null)
          {
            if (((zze)paramObject).axk != null) {
              return false;
            }
          }
          else if (!this.axk.equals(((zze)paramObject).axk)) {
            return false;
          }
          if (this.axl == null)
          {
            if (((zze)paramObject).axl != null) {
              return false;
            }
          }
          else if (!this.axl.equals(((zze)paramObject).axl)) {
            return false;
          }
          if (this.axm == null)
          {
            if (((zze)paramObject).axm != null) {
              return false;
            }
          }
          else if (!this.axm.equals(((zze)paramObject).axm)) {
            return false;
          }
          if (this.ard == null)
          {
            if (((zze)paramObject).ard != null) {
              return false;
            }
          }
          else if (!this.ard.equals(((zze)paramObject).ard)) {
            return false;
          }
          if (this.aqZ == null)
          {
            if (((zze)paramObject).aqZ != null) {
              return false;
            }
          }
          else if (!this.aqZ.equals(((zze)paramObject).aqZ)) {
            return false;
          }
          if (this.axn == null)
          {
            if (((zze)paramObject).axn != null) {
              return false;
            }
          }
          else if (!this.axn.equals(((zze)paramObject).axn)) {
            return false;
          }
          if (!zzary.equals(this.axo, ((zze)paramObject).axo)) {
            return false;
          }
          if (this.arh == null)
          {
            if (((zze)paramObject).arh != null) {
              return false;
            }
          }
          else if (!this.arh.equals(((zze)paramObject).arh)) {
            return false;
          }
          if (this.axp == null)
          {
            if (((zze)paramObject).axp != null) {
              return false;
            }
          }
          else if (!this.axp.equals(((zze)paramObject).axp)) {
            return false;
          }
          if (this.axq == null)
          {
            if (((zze)paramObject).axq != null) {
              return false;
            }
          }
          else if (!this.axq.equals(((zze)paramObject).axq)) {
            return false;
          }
          if (this.axr == null)
          {
            if (((zze)paramObject).axr != null) {
              return false;
            }
          }
          else if (!this.axr.equals(((zze)paramObject).axr)) {
            return false;
          }
          if (this.axs == null)
          {
            if (((zze)paramObject).axs != null) {
              return false;
            }
          }
          else if (!this.axs.equals(((zze)paramObject).axs)) {
            return false;
          }
          if (this.axt != null) {
            break;
          }
        } while (((zze)paramObject).axt == null);
        return false;
      } while (this.axt.equals(((zze)paramObject).axt));
      return false;
    }
    
    public int hashCode()
    {
      int i25 = 0;
      int i26 = getClass().getName().hashCode();
      int i;
      int i27;
      int i28;
      int j;
      label51:
      int k;
      label60:
      int m;
      label70:
      int n;
      label80:
      int i1;
      label90:
      int i2;
      label100:
      int i3;
      label110:
      int i4;
      label120:
      int i5;
      label130:
      int i6;
      label140:
      int i7;
      label150:
      int i8;
      label160:
      int i9;
      label170:
      int i10;
      label180:
      int i11;
      label190:
      int i12;
      label200:
      int i13;
      label210:
      int i14;
      label220:
      int i15;
      label230:
      int i16;
      label240:
      int i17;
      label250:
      int i18;
      label260:
      int i19;
      label270:
      int i29;
      int i20;
      label289:
      int i21;
      label299:
      int i22;
      label309:
      int i23;
      label319:
      int i24;
      if (this.awU == null)
      {
        i = 0;
        i27 = zzary.hashCode(this.awV);
        i28 = zzary.hashCode(this.awW);
        if (this.awX != null) {
          break label549;
        }
        j = 0;
        if (this.awY != null) {
          break label560;
        }
        k = 0;
        if (this.awZ != null) {
          break label571;
        }
        m = 0;
        if (this.axa != null) {
          break label583;
        }
        n = 0;
        if (this.axb != null) {
          break label595;
        }
        i1 = 0;
        if (this.axc != null) {
          break label607;
        }
        i2 = 0;
        if (this.zzdb != null) {
          break label619;
        }
        i3 = 0;
        if (this.axd != null) {
          break label631;
        }
        i4 = 0;
        if (this.axe != null) {
          break label643;
        }
        i5 = 0;
        if (this.axf != null) {
          break label655;
        }
        i6 = 0;
        if (this.ara != null) {
          break label667;
        }
        i7 = 0;
        if (this.zzcs != null) {
          break label679;
        }
        i8 = 0;
        if (this.aii != null) {
          break label691;
        }
        i9 = 0;
        if (this.axg != null) {
          break label703;
        }
        i10 = 0;
        if (this.axh != null) {
          break label715;
        }
        i11 = 0;
        if (this.axi != null) {
          break label727;
        }
        i12 = 0;
        if (this.axj != null) {
          break label739;
        }
        i13 = 0;
        if (this.axk != null) {
          break label751;
        }
        i14 = 0;
        if (this.axl != null) {
          break label763;
        }
        i15 = 0;
        if (this.axm != null) {
          break label775;
        }
        i16 = 0;
        if (this.ard != null) {
          break label787;
        }
        i17 = 0;
        if (this.aqZ != null) {
          break label799;
        }
        i18 = 0;
        if (this.axn != null) {
          break label811;
        }
        i19 = 0;
        i29 = zzary.hashCode(this.axo);
        if (this.arh != null) {
          break label823;
        }
        i20 = 0;
        if (this.axp != null) {
          break label835;
        }
        i21 = 0;
        if (this.axq != null) {
          break label847;
        }
        i22 = 0;
        if (this.axr != null) {
          break label859;
        }
        i23 = 0;
        if (this.axs != null) {
          break label871;
        }
        i24 = 0;
        label329:
        if (this.axt != null) {
          break label883;
        }
      }
      for (;;)
      {
        return (i24 + (i23 + (i22 + (i21 + (i20 + ((i19 + (i18 + (i17 + (i16 + (i15 + (i14 + (i13 + (i12 + (i11 + (i10 + (i9 + (i8 + (i7 + (i6 + (i5 + (i4 + (i3 + (i2 + (i1 + (n + (m + (k + (j + (((i + (i26 + 527) * 31) * 31 + i27) * 31 + i28) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31 + i29) * 31) * 31) * 31) * 31) * 31) * 31 + i25;
        i = this.awU.hashCode();
        break;
        label549:
        j = this.awX.hashCode();
        break label51;
        label560:
        k = this.awY.hashCode();
        break label60;
        label571:
        m = this.awZ.hashCode();
        break label70;
        label583:
        n = this.axa.hashCode();
        break label80;
        label595:
        i1 = this.axb.hashCode();
        break label90;
        label607:
        i2 = this.axc.hashCode();
        break label100;
        label619:
        i3 = this.zzdb.hashCode();
        break label110;
        label631:
        i4 = this.axd.hashCode();
        break label120;
        label643:
        i5 = this.axe.hashCode();
        break label130;
        label655:
        i6 = this.axf.hashCode();
        break label140;
        label667:
        i7 = this.ara.hashCode();
        break label150;
        label679:
        i8 = this.zzcs.hashCode();
        break label160;
        label691:
        i9 = this.aii.hashCode();
        break label170;
        label703:
        i10 = this.axg.hashCode();
        break label180;
        label715:
        i11 = this.axh.hashCode();
        break label190;
        label727:
        i12 = this.axi.hashCode();
        break label200;
        label739:
        i13 = this.axj.hashCode();
        break label210;
        label751:
        i14 = this.axk.hashCode();
        break label220;
        label763:
        i15 = this.axl.hashCode();
        break label230;
        label775:
        i16 = this.axm.hashCode();
        break label240;
        label787:
        i17 = this.ard.hashCode();
        break label250;
        label799:
        i18 = this.aqZ.hashCode();
        break label260;
        label811:
        i19 = this.axn.hashCode();
        break label270;
        label823:
        i20 = this.arh.hashCode();
        break label289;
        label835:
        i21 = this.axp.hashCode();
        break label299;
        label847:
        i22 = this.axq.hashCode();
        break label309;
        label859:
        i23 = this.axr.hashCode();
        break label319;
        label871:
        i24 = this.axs.hashCode();
        break label329;
        label883:
        i25 = this.axt.hashCode();
      }
    }
    
    public void zza(zzart paramzzart)
      throws IOException
    {
      int j = 0;
      if (this.awU != null) {
        paramzzart.zzaf(1, this.awU.intValue());
      }
      int i;
      Object localObject;
      if ((this.awV != null) && (this.awV.length > 0))
      {
        i = 0;
        while (i < this.awV.length)
        {
          localObject = this.awV[i];
          if (localObject != null) {
            paramzzart.zza(2, (zzasa)localObject);
          }
          i += 1;
        }
      }
      if ((this.awW != null) && (this.awW.length > 0))
      {
        i = 0;
        while (i < this.awW.length)
        {
          localObject = this.awW[i];
          if (localObject != null) {
            paramzzart.zza(3, (zzasa)localObject);
          }
          i += 1;
        }
      }
      if (this.awX != null) {
        paramzzart.zzb(4, this.awX.longValue());
      }
      if (this.awY != null) {
        paramzzart.zzb(5, this.awY.longValue());
      }
      if (this.awZ != null) {
        paramzzart.zzb(6, this.awZ.longValue());
      }
      if (this.axb != null) {
        paramzzart.zzb(7, this.axb.longValue());
      }
      if (this.axc != null) {
        paramzzart.zzq(8, this.axc);
      }
      if (this.zzdb != null) {
        paramzzart.zzq(9, this.zzdb);
      }
      if (this.axd != null) {
        paramzzart.zzq(10, this.axd);
      }
      if (this.axe != null) {
        paramzzart.zzq(11, this.axe);
      }
      if (this.axf != null) {
        paramzzart.zzaf(12, this.axf.intValue());
      }
      if (this.ara != null) {
        paramzzart.zzq(13, this.ara);
      }
      if (this.zzcs != null) {
        paramzzart.zzq(14, this.zzcs);
      }
      if (this.aii != null) {
        paramzzart.zzq(16, this.aii);
      }
      if (this.axg != null) {
        paramzzart.zzb(17, this.axg.longValue());
      }
      if (this.axh != null) {
        paramzzart.zzb(18, this.axh.longValue());
      }
      if (this.axi != null) {
        paramzzart.zzq(19, this.axi);
      }
      if (this.axj != null) {
        paramzzart.zzg(20, this.axj.booleanValue());
      }
      if (this.axk != null) {
        paramzzart.zzq(21, this.axk);
      }
      if (this.axl != null) {
        paramzzart.zzb(22, this.axl.longValue());
      }
      if (this.axm != null) {
        paramzzart.zzaf(23, this.axm.intValue());
      }
      if (this.ard != null) {
        paramzzart.zzq(24, this.ard);
      }
      if (this.aqZ != null) {
        paramzzart.zzq(25, this.aqZ);
      }
      if (this.axa != null) {
        paramzzart.zzb(26, this.axa.longValue());
      }
      if (this.axn != null) {
        paramzzart.zzg(28, this.axn.booleanValue());
      }
      if ((this.axo != null) && (this.axo.length > 0))
      {
        i = j;
        while (i < this.axo.length)
        {
          localObject = this.axo[i];
          if (localObject != null) {
            paramzzart.zza(29, (zzasa)localObject);
          }
          i += 1;
        }
      }
      if (this.arh != null) {
        paramzzart.zzq(30, this.arh);
      }
      if (this.axp != null) {
        paramzzart.zzaf(31, this.axp.intValue());
      }
      if (this.axq != null) {
        paramzzart.zzaf(32, this.axq.intValue());
      }
      if (this.axr != null) {
        paramzzart.zzaf(33, this.axr.intValue());
      }
      if (this.axs != null) {
        paramzzart.zzq(34, this.axs);
      }
      if (this.axt != null) {
        paramzzart.zzb(35, this.axt.longValue());
      }
      super.zza(paramzzart);
    }
    
    public zze zzat(zzars paramzzars)
      throws IOException
    {
      for (;;)
      {
        int i = paramzzars.bU();
        int j;
        Object localObject;
        switch (i)
        {
        default: 
          if (zzasd.zzb(paramzzars, i)) {}
          break;
        case 0: 
          return this;
        case 8: 
          this.awU = Integer.valueOf(paramzzars.bY());
          break;
        case 18: 
          j = zzasd.zzc(paramzzars, 18);
          if (this.awV == null) {}
          for (i = 0;; i = this.awV.length)
          {
            localObject = new zzwc.zzb[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.awV, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length - 1)
            {
              localObject[j] = new zzwc.zzb();
              paramzzars.zza(localObject[j]);
              paramzzars.bU();
              j += 1;
            }
          }
          localObject[j] = new zzwc.zzb();
          paramzzars.zza(localObject[j]);
          this.awV = ((zzwc.zzb[])localObject);
          break;
        case 26: 
          j = zzasd.zzc(paramzzars, 26);
          if (this.awW == null) {}
          for (i = 0;; i = this.awW.length)
          {
            localObject = new zzwc.zzg[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.awW, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length - 1)
            {
              localObject[j] = new zzwc.zzg();
              paramzzars.zza(localObject[j]);
              paramzzars.bU();
              j += 1;
            }
          }
          localObject[j] = new zzwc.zzg();
          paramzzars.zza(localObject[j]);
          this.awW = ((zzwc.zzg[])localObject);
          break;
        case 32: 
          this.awX = Long.valueOf(paramzzars.bX());
          break;
        case 40: 
          this.awY = Long.valueOf(paramzzars.bX());
          break;
        case 48: 
          this.awZ = Long.valueOf(paramzzars.bX());
          break;
        case 56: 
          this.axb = Long.valueOf(paramzzars.bX());
          break;
        case 66: 
          this.axc = paramzzars.readString();
          break;
        case 74: 
          this.zzdb = paramzzars.readString();
          break;
        case 82: 
          this.axd = paramzzars.readString();
          break;
        case 90: 
          this.axe = paramzzars.readString();
          break;
        case 96: 
          this.axf = Integer.valueOf(paramzzars.bY());
          break;
        case 106: 
          this.ara = paramzzars.readString();
          break;
        case 114: 
          this.zzcs = paramzzars.readString();
          break;
        case 130: 
          this.aii = paramzzars.readString();
          break;
        case 136: 
          this.axg = Long.valueOf(paramzzars.bX());
          break;
        case 144: 
          this.axh = Long.valueOf(paramzzars.bX());
          break;
        case 154: 
          this.axi = paramzzars.readString();
          break;
        case 160: 
          this.axj = Boolean.valueOf(paramzzars.ca());
          break;
        case 170: 
          this.axk = paramzzars.readString();
          break;
        case 176: 
          this.axl = Long.valueOf(paramzzars.bX());
          break;
        case 184: 
          this.axm = Integer.valueOf(paramzzars.bY());
          break;
        case 194: 
          this.ard = paramzzars.readString();
          break;
        case 202: 
          this.aqZ = paramzzars.readString();
          break;
        case 208: 
          this.axa = Long.valueOf(paramzzars.bX());
          break;
        case 224: 
          this.axn = Boolean.valueOf(paramzzars.ca());
          break;
        case 234: 
          j = zzasd.zzc(paramzzars, 234);
          if (this.axo == null) {}
          for (i = 0;; i = this.axo.length)
          {
            localObject = new zzwc.zza[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.axo, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length - 1)
            {
              localObject[j] = new zzwc.zza();
              paramzzars.zza(localObject[j]);
              paramzzars.bU();
              j += 1;
            }
          }
          localObject[j] = new zzwc.zza();
          paramzzars.zza(localObject[j]);
          this.axo = ((zzwc.zza[])localObject);
          break;
        case 242: 
          this.arh = paramzzars.readString();
          break;
        case 248: 
          this.axp = Integer.valueOf(paramzzars.bY());
          break;
        case 256: 
          this.axq = Integer.valueOf(paramzzars.bY());
          break;
        case 264: 
          this.axr = Integer.valueOf(paramzzars.bY());
          break;
        case 274: 
          this.axs = paramzzars.readString();
          break;
        case 280: 
          this.axt = Long.valueOf(paramzzars.bX());
        }
      }
    }
    
    public zze zzcae()
    {
      this.awU = null;
      this.awV = zzwc.zzb.zzbzy();
      this.awW = zzwc.zzg.zzcag();
      this.awX = null;
      this.awY = null;
      this.awZ = null;
      this.axa = null;
      this.axb = null;
      this.axc = null;
      this.zzdb = null;
      this.axd = null;
      this.axe = null;
      this.axf = null;
      this.ara = null;
      this.zzcs = null;
      this.aii = null;
      this.axg = null;
      this.axh = null;
      this.axi = null;
      this.axj = null;
      this.axk = null;
      this.axl = null;
      this.axm = null;
      this.ard = null;
      this.aqZ = null;
      this.axn = null;
      this.axo = zzwc.zza.zzbzw();
      this.arh = null;
      this.axp = null;
      this.axq = null;
      this.axr = null;
      this.axs = null;
      this.axt = null;
      this.btP = -1;
      return this;
    }
    
    protected int zzx()
    {
      int m = 0;
      int j = super.zzx();
      int i = j;
      if (this.awU != null) {
        i = j + zzart.zzah(1, this.awU.intValue());
      }
      j = i;
      Object localObject;
      if (this.awV != null)
      {
        j = i;
        if (this.awV.length > 0)
        {
          j = 0;
          while (j < this.awV.length)
          {
            localObject = this.awV[j];
            k = i;
            if (localObject != null) {
              k = i + zzart.zzc(2, (zzasa)localObject);
            }
            j += 1;
            i = k;
          }
          j = i;
        }
      }
      i = j;
      if (this.awW != null)
      {
        i = j;
        if (this.awW.length > 0)
        {
          i = j;
          j = 0;
          while (j < this.awW.length)
          {
            localObject = this.awW[j];
            k = i;
            if (localObject != null) {
              k = i + zzart.zzc(3, (zzasa)localObject);
            }
            j += 1;
            i = k;
          }
        }
      }
      j = i;
      if (this.awX != null) {
        j = i + zzart.zzf(4, this.awX.longValue());
      }
      i = j;
      if (this.awY != null) {
        i = j + zzart.zzf(5, this.awY.longValue());
      }
      j = i;
      if (this.awZ != null) {
        j = i + zzart.zzf(6, this.awZ.longValue());
      }
      i = j;
      if (this.axb != null) {
        i = j + zzart.zzf(7, this.axb.longValue());
      }
      j = i;
      if (this.axc != null) {
        j = i + zzart.zzr(8, this.axc);
      }
      i = j;
      if (this.zzdb != null) {
        i = j + zzart.zzr(9, this.zzdb);
      }
      j = i;
      if (this.axd != null) {
        j = i + zzart.zzr(10, this.axd);
      }
      i = j;
      if (this.axe != null) {
        i = j + zzart.zzr(11, this.axe);
      }
      j = i;
      if (this.axf != null) {
        j = i + zzart.zzah(12, this.axf.intValue());
      }
      i = j;
      if (this.ara != null) {
        i = j + zzart.zzr(13, this.ara);
      }
      j = i;
      if (this.zzcs != null) {
        j = i + zzart.zzr(14, this.zzcs);
      }
      i = j;
      if (this.aii != null) {
        i = j + zzart.zzr(16, this.aii);
      }
      j = i;
      if (this.axg != null) {
        j = i + zzart.zzf(17, this.axg.longValue());
      }
      i = j;
      if (this.axh != null) {
        i = j + zzart.zzf(18, this.axh.longValue());
      }
      j = i;
      if (this.axi != null) {
        j = i + zzart.zzr(19, this.axi);
      }
      i = j;
      if (this.axj != null) {
        i = j + zzart.zzh(20, this.axj.booleanValue());
      }
      j = i;
      if (this.axk != null) {
        j = i + zzart.zzr(21, this.axk);
      }
      i = j;
      if (this.axl != null) {
        i = j + zzart.zzf(22, this.axl.longValue());
      }
      j = i;
      if (this.axm != null) {
        j = i + zzart.zzah(23, this.axm.intValue());
      }
      i = j;
      if (this.ard != null) {
        i = j + zzart.zzr(24, this.ard);
      }
      j = i;
      if (this.aqZ != null) {
        j = i + zzart.zzr(25, this.aqZ);
      }
      int k = j;
      if (this.axa != null) {
        k = j + zzart.zzf(26, this.axa.longValue());
      }
      i = k;
      if (this.axn != null) {
        i = k + zzart.zzh(28, this.axn.booleanValue());
      }
      j = i;
      if (this.axo != null)
      {
        j = i;
        if (this.axo.length > 0)
        {
          k = m;
          for (;;)
          {
            j = i;
            if (k >= this.axo.length) {
              break;
            }
            localObject = this.axo[k];
            j = i;
            if (localObject != null) {
              j = i + zzart.zzc(29, (zzasa)localObject);
            }
            k += 1;
            i = j;
          }
        }
      }
      i = j;
      if (this.arh != null) {
        i = j + zzart.zzr(30, this.arh);
      }
      j = i;
      if (this.axp != null) {
        j = i + zzart.zzah(31, this.axp.intValue());
      }
      i = j;
      if (this.axq != null) {
        i = j + zzart.zzah(32, this.axq.intValue());
      }
      j = i;
      if (this.axr != null) {
        j = i + zzart.zzah(33, this.axr.intValue());
      }
      i = j;
      if (this.axs != null) {
        i = j + zzart.zzr(34, this.axs);
      }
      j = i;
      if (this.axt != null) {
        j = i + zzart.zzf(35, this.axt.longValue());
      }
      return j;
    }
  }
  
  public static final class zzf
    extends zzasa
  {
    public long[] axu;
    public long[] axv;
    
    public zzf()
    {
      zzcaf();
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {}
      do
      {
        return true;
        if (!(paramObject instanceof zzf)) {
          return false;
        }
        paramObject = (zzf)paramObject;
        if (!zzary.equals(this.axu, ((zzf)paramObject).axu)) {
          return false;
        }
      } while (zzary.equals(this.axv, ((zzf)paramObject).axv));
      return false;
    }
    
    public int hashCode()
    {
      return ((getClass().getName().hashCode() + 527) * 31 + zzary.hashCode(this.axu)) * 31 + zzary.hashCode(this.axv);
    }
    
    public void zza(zzart paramzzart)
      throws IOException
    {
      int j = 0;
      int i;
      if ((this.axu != null) && (this.axu.length > 0))
      {
        i = 0;
        while (i < this.axu.length)
        {
          paramzzart.zza(1, this.axu[i]);
          i += 1;
        }
      }
      if ((this.axv != null) && (this.axv.length > 0))
      {
        i = j;
        while (i < this.axv.length)
        {
          paramzzart.zza(2, this.axv[i]);
          i += 1;
        }
      }
      super.zza(paramzzart);
    }
    
    public zzf zzau(zzars paramzzars)
      throws IOException
    {
      for (;;)
      {
        int i = paramzzars.bU();
        int j;
        long[] arrayOfLong;
        int k;
        switch (i)
        {
        default: 
          if (zzasd.zzb(paramzzars, i)) {}
          break;
        case 0: 
          return this;
        case 8: 
          j = zzasd.zzc(paramzzars, 8);
          if (this.axu == null) {}
          for (i = 0;; i = this.axu.length)
          {
            arrayOfLong = new long[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.axu, 0, arrayOfLong, 0, i);
              j = i;
            }
            while (j < arrayOfLong.length - 1)
            {
              arrayOfLong[j] = paramzzars.bW();
              paramzzars.bU();
              j += 1;
            }
          }
          arrayOfLong[j] = paramzzars.bW();
          this.axu = arrayOfLong;
          break;
        case 10: 
          k = paramzzars.zzagt(paramzzars.cd());
          i = paramzzars.getPosition();
          j = 0;
          while (paramzzars.ci() > 0)
          {
            paramzzars.bW();
            j += 1;
          }
          paramzzars.zzagv(i);
          if (this.axu == null) {}
          for (i = 0;; i = this.axu.length)
          {
            arrayOfLong = new long[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.axu, 0, arrayOfLong, 0, i);
              j = i;
            }
            while (j < arrayOfLong.length)
            {
              arrayOfLong[j] = paramzzars.bW();
              j += 1;
            }
          }
          this.axu = arrayOfLong;
          paramzzars.zzagu(k);
          break;
        case 16: 
          j = zzasd.zzc(paramzzars, 16);
          if (this.axv == null) {}
          for (i = 0;; i = this.axv.length)
          {
            arrayOfLong = new long[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.axv, 0, arrayOfLong, 0, i);
              j = i;
            }
            while (j < arrayOfLong.length - 1)
            {
              arrayOfLong[j] = paramzzars.bW();
              paramzzars.bU();
              j += 1;
            }
          }
          arrayOfLong[j] = paramzzars.bW();
          this.axv = arrayOfLong;
          break;
        case 18: 
          k = paramzzars.zzagt(paramzzars.cd());
          i = paramzzars.getPosition();
          j = 0;
          while (paramzzars.ci() > 0)
          {
            paramzzars.bW();
            j += 1;
          }
          paramzzars.zzagv(i);
          if (this.axv == null) {}
          for (i = 0;; i = this.axv.length)
          {
            arrayOfLong = new long[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.axv, 0, arrayOfLong, 0, i);
              j = i;
            }
            while (j < arrayOfLong.length)
            {
              arrayOfLong[j] = paramzzars.bW();
              j += 1;
            }
          }
          this.axv = arrayOfLong;
          paramzzars.zzagu(k);
        }
      }
    }
    
    public zzf zzcaf()
    {
      this.axu = zzasd.btS;
      this.axv = zzasd.btS;
      this.btP = -1;
      return this;
    }
    
    protected int zzx()
    {
      int m = 0;
      int k = super.zzx();
      int j;
      if ((this.axu != null) && (this.axu.length > 0))
      {
        i = 0;
        j = 0;
        while (i < this.axu.length)
        {
          j += zzart.zzcy(this.axu[i]);
          i += 1;
        }
      }
      for (int i = k + j + this.axu.length * 1;; i = k)
      {
        j = i;
        if (this.axv != null)
        {
          j = i;
          if (this.axv.length > 0)
          {
            k = 0;
            j = m;
            while (j < this.axv.length)
            {
              k += zzart.zzcy(this.axv[j]);
              j += 1;
            }
            j = i + k + this.axv.length * 1;
          }
        }
        return j;
      }
    }
  }
  
  public static final class zzg
    extends zzasa
  {
    private static volatile zzg[] axw;
    public String Fe;
    public Float avV;
    public Double avW;
    public Long awR;
    public Long axx;
    public String name;
    
    public zzg()
    {
      zzcah();
    }
    
    public static zzg[] zzcag()
    {
      if (axw == null) {}
      synchronized (zzary.btO)
      {
        if (axw == null) {
          axw = new zzg[0];
        }
        return axw;
      }
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {}
      do
      {
        do
        {
          return true;
          if (!(paramObject instanceof zzg)) {
            return false;
          }
          paramObject = (zzg)paramObject;
          if (this.axx == null)
          {
            if (((zzg)paramObject).axx != null) {
              return false;
            }
          }
          else if (!this.axx.equals(((zzg)paramObject).axx)) {
            return false;
          }
          if (this.name == null)
          {
            if (((zzg)paramObject).name != null) {
              return false;
            }
          }
          else if (!this.name.equals(((zzg)paramObject).name)) {
            return false;
          }
          if (this.Fe == null)
          {
            if (((zzg)paramObject).Fe != null) {
              return false;
            }
          }
          else if (!this.Fe.equals(((zzg)paramObject).Fe)) {
            return false;
          }
          if (this.awR == null)
          {
            if (((zzg)paramObject).awR != null) {
              return false;
            }
          }
          else if (!this.awR.equals(((zzg)paramObject).awR)) {
            return false;
          }
          if (this.avV == null)
          {
            if (((zzg)paramObject).avV != null) {
              return false;
            }
          }
          else if (!this.avV.equals(((zzg)paramObject).avV)) {
            return false;
          }
          if (this.avW != null) {
            break;
          }
        } while (((zzg)paramObject).avW == null);
        return false;
      } while (this.avW.equals(((zzg)paramObject).avW));
      return false;
    }
    
    public int hashCode()
    {
      int i1 = 0;
      int i2 = getClass().getName().hashCode();
      int i;
      int j;
      label33:
      int k;
      label42:
      int m;
      label52:
      int n;
      if (this.axx == null)
      {
        i = 0;
        if (this.name != null) {
          break label120;
        }
        j = 0;
        if (this.Fe != null) {
          break label131;
        }
        k = 0;
        if (this.awR != null) {
          break label142;
        }
        m = 0;
        if (this.avV != null) {
          break label154;
        }
        n = 0;
        label62:
        if (this.avW != null) {
          break label166;
        }
      }
      for (;;)
      {
        return (n + (m + (k + (j + (i + (i2 + 527) * 31) * 31) * 31) * 31) * 31) * 31 + i1;
        i = this.axx.hashCode();
        break;
        label120:
        j = this.name.hashCode();
        break label33;
        label131:
        k = this.Fe.hashCode();
        break label42;
        label142:
        m = this.awR.hashCode();
        break label52;
        label154:
        n = this.avV.hashCode();
        break label62;
        label166:
        i1 = this.avW.hashCode();
      }
    }
    
    public void zza(zzart paramzzart)
      throws IOException
    {
      if (this.axx != null) {
        paramzzart.zzb(1, this.axx.longValue());
      }
      if (this.name != null) {
        paramzzart.zzq(2, this.name);
      }
      if (this.Fe != null) {
        paramzzart.zzq(3, this.Fe);
      }
      if (this.awR != null) {
        paramzzart.zzb(4, this.awR.longValue());
      }
      if (this.avV != null) {
        paramzzart.zzc(5, this.avV.floatValue());
      }
      if (this.avW != null) {
        paramzzart.zza(6, this.avW.doubleValue());
      }
      super.zza(paramzzart);
    }
    
    public zzg zzav(zzars paramzzars)
      throws IOException
    {
      for (;;)
      {
        int i = paramzzars.bU();
        switch (i)
        {
        default: 
          if (zzasd.zzb(paramzzars, i)) {}
          break;
        case 0: 
          return this;
        case 8: 
          this.axx = Long.valueOf(paramzzars.bX());
          break;
        case 18: 
          this.name = paramzzars.readString();
          break;
        case 26: 
          this.Fe = paramzzars.readString();
          break;
        case 32: 
          this.awR = Long.valueOf(paramzzars.bX());
          break;
        case 45: 
          this.avV = Float.valueOf(paramzzars.readFloat());
          break;
        case 49: 
          this.avW = Double.valueOf(paramzzars.readDouble());
        }
      }
    }
    
    public zzg zzcah()
    {
      this.axx = null;
      this.name = null;
      this.Fe = null;
      this.awR = null;
      this.avV = null;
      this.avW = null;
      this.btP = -1;
      return this;
    }
    
    protected int zzx()
    {
      int j = super.zzx();
      int i = j;
      if (this.axx != null) {
        i = j + zzart.zzf(1, this.axx.longValue());
      }
      j = i;
      if (this.name != null) {
        j = i + zzart.zzr(2, this.name);
      }
      i = j;
      if (this.Fe != null) {
        i = j + zzart.zzr(3, this.Fe);
      }
      j = i;
      if (this.awR != null) {
        j = i + zzart.zzf(4, this.awR.longValue());
      }
      i = j;
      if (this.avV != null) {
        i = j + zzart.zzd(5, this.avV.floatValue());
      }
      j = i;
      if (this.avW != null) {
        j = i + zzart.zzb(6, this.avW.doubleValue());
      }
      return j;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzwc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */