package com.google.android.gms.internal;

import java.io.IOException;

public abstract interface zzvk
{
  public static final class zza
    extends zzark
  {
    private static volatile zza[] asz;
    public Integer asA;
    public zzvk.zze[] asB;
    public zzvk.zzb[] asC;
    
    public zza()
    {
      zzbyn();
    }
    
    public static zza[] zzbym()
    {
      if (asz == null) {}
      synchronized (zzari.bqD)
      {
        if (asz == null) {
          asz = new zza[0];
        }
        return asz;
      }
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {}
      do
      {
        return true;
        if (!(paramObject instanceof zza)) {
          return false;
        }
        paramObject = (zza)paramObject;
        if (this.asA == null)
        {
          if (((zza)paramObject).asA != null) {
            return false;
          }
        }
        else if (!this.asA.equals(((zza)paramObject).asA)) {
          return false;
        }
        if (!zzari.equals(this.asB, ((zza)paramObject).asB)) {
          return false;
        }
      } while (zzari.equals(this.asC, ((zza)paramObject).asC));
      return false;
    }
    
    public int hashCode()
    {
      int j = getClass().getName().hashCode();
      if (this.asA == null) {}
      for (int i = 0;; i = this.asA.hashCode()) {
        return ((i + (j + 527) * 31) * 31 + zzari.hashCode(this.asB)) * 31 + zzari.hashCode(this.asC);
      }
    }
    
    public void zza(zzard paramzzard)
      throws IOException
    {
      int j = 0;
      if (this.asA != null) {
        paramzzard.zzae(1, this.asA.intValue());
      }
      int i;
      Object localObject;
      if ((this.asB != null) && (this.asB.length > 0))
      {
        i = 0;
        while (i < this.asB.length)
        {
          localObject = this.asB[i];
          if (localObject != null) {
            paramzzard.zza(2, (zzark)localObject);
          }
          i += 1;
        }
      }
      if ((this.asC != null) && (this.asC.length > 0))
      {
        i = j;
        while (i < this.asC.length)
        {
          localObject = this.asC[i];
          if (localObject != null) {
            paramzzard.zza(3, (zzark)localObject);
          }
          i += 1;
        }
      }
      super.zza(paramzzard);
    }
    
    public zza zzad(zzarc paramzzarc)
      throws IOException
    {
      for (;;)
      {
        int i = paramzzarc.cw();
        int j;
        Object localObject;
        switch (i)
        {
        default: 
          if (zzarn.zzb(paramzzarc, i)) {}
          break;
        case 0: 
          return this;
        case 8: 
          this.asA = Integer.valueOf(paramzzarc.cA());
          break;
        case 18: 
          j = zzarn.zzc(paramzzarc, 18);
          if (this.asB == null) {}
          for (i = 0;; i = this.asB.length)
          {
            localObject = new zzvk.zze[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.asB, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length - 1)
            {
              localObject[j] = new zzvk.zze();
              paramzzarc.zza(localObject[j]);
              paramzzarc.cw();
              j += 1;
            }
          }
          localObject[j] = new zzvk.zze();
          paramzzarc.zza(localObject[j]);
          this.asB = ((zzvk.zze[])localObject);
          break;
        case 26: 
          j = zzarn.zzc(paramzzarc, 26);
          if (this.asC == null) {}
          for (i = 0;; i = this.asC.length)
          {
            localObject = new zzvk.zzb[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.asC, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length - 1)
            {
              localObject[j] = new zzvk.zzb();
              paramzzarc.zza(localObject[j]);
              paramzzarc.cw();
              j += 1;
            }
          }
          localObject[j] = new zzvk.zzb();
          paramzzarc.zza(localObject[j]);
          this.asC = ((zzvk.zzb[])localObject);
        }
      }
    }
    
    public zza zzbyn()
    {
      this.asA = null;
      this.asB = zzvk.zze.zzbyt();
      this.asC = zzvk.zzb.zzbyo();
      this.bqE = -1;
      return this;
    }
    
    protected int zzx()
    {
      int m = 0;
      int i = super.zzx();
      int j = i;
      if (this.asA != null) {
        j = i + zzard.zzag(1, this.asA.intValue());
      }
      i = j;
      Object localObject;
      if (this.asB != null)
      {
        i = j;
        if (this.asB.length > 0)
        {
          i = j;
          j = 0;
          while (j < this.asB.length)
          {
            localObject = this.asB[j];
            k = i;
            if (localObject != null) {
              k = i + zzard.zzc(2, (zzark)localObject);
            }
            j += 1;
            i = k;
          }
        }
      }
      int k = i;
      if (this.asC != null)
      {
        k = i;
        if (this.asC.length > 0)
        {
          j = m;
          for (;;)
          {
            k = i;
            if (j >= this.asC.length) {
              break;
            }
            localObject = this.asC[j];
            k = i;
            if (localObject != null) {
              k = i + zzard.zzc(3, (zzark)localObject);
            }
            j += 1;
            i = k;
          }
        }
      }
      return k;
    }
  }
  
  public static final class zzb
    extends zzark
  {
    private static volatile zzb[] asD;
    public Integer asE;
    public String asF;
    public zzvk.zzc[] asG;
    public Boolean asH;
    public zzvk.zzd asI;
    
    public zzb()
    {
      zzbyp();
    }
    
    public static zzb[] zzbyo()
    {
      if (asD == null) {}
      synchronized (zzari.bqD)
      {
        if (asD == null) {
          asD = new zzb[0];
        }
        return asD;
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
          if (this.asE == null)
          {
            if (((zzb)paramObject).asE != null) {
              return false;
            }
          }
          else if (!this.asE.equals(((zzb)paramObject).asE)) {
            return false;
          }
          if (this.asF == null)
          {
            if (((zzb)paramObject).asF != null) {
              return false;
            }
          }
          else if (!this.asF.equals(((zzb)paramObject).asF)) {
            return false;
          }
          if (!zzari.equals(this.asG, ((zzb)paramObject).asG)) {
            return false;
          }
          if (this.asH == null)
          {
            if (((zzb)paramObject).asH != null) {
              return false;
            }
          }
          else if (!this.asH.equals(((zzb)paramObject).asH)) {
            return false;
          }
          if (this.asI != null) {
            break;
          }
        } while (((zzb)paramObject).asI == null);
        return false;
      } while (this.asI.equals(((zzb)paramObject).asI));
      return false;
    }
    
    public int hashCode()
    {
      int m = 0;
      int n = getClass().getName().hashCode();
      int i;
      int j;
      label33:
      int i1;
      int k;
      if (this.asE == null)
      {
        i = 0;
        if (this.asF != null) {
          break label103;
        }
        j = 0;
        i1 = zzari.hashCode(this.asG);
        if (this.asH != null) {
          break label114;
        }
        k = 0;
        label51:
        if (this.asI != null) {
          break label125;
        }
      }
      for (;;)
      {
        return (k + ((j + (i + (n + 527) * 31) * 31) * 31 + i1) * 31) * 31 + m;
        i = this.asE.hashCode();
        break;
        label103:
        j = this.asF.hashCode();
        break label33;
        label114:
        k = this.asH.hashCode();
        break label51;
        label125:
        m = this.asI.hashCode();
      }
    }
    
    public void zza(zzard paramzzard)
      throws IOException
    {
      if (this.asE != null) {
        paramzzard.zzae(1, this.asE.intValue());
      }
      if (this.asF != null) {
        paramzzard.zzr(2, this.asF);
      }
      if ((this.asG != null) && (this.asG.length > 0))
      {
        int i = 0;
        while (i < this.asG.length)
        {
          zzvk.zzc localzzc = this.asG[i];
          if (localzzc != null) {
            paramzzard.zza(3, localzzc);
          }
          i += 1;
        }
      }
      if (this.asH != null) {
        paramzzard.zzj(4, this.asH.booleanValue());
      }
      if (this.asI != null) {
        paramzzard.zza(5, this.asI);
      }
      super.zza(paramzzard);
    }
    
    public zzb zzae(zzarc paramzzarc)
      throws IOException
    {
      for (;;)
      {
        int i = paramzzarc.cw();
        switch (i)
        {
        default: 
          if (zzarn.zzb(paramzzarc, i)) {}
          break;
        case 0: 
          return this;
        case 8: 
          this.asE = Integer.valueOf(paramzzarc.cA());
          break;
        case 18: 
          this.asF = paramzzarc.readString();
          break;
        case 26: 
          int j = zzarn.zzc(paramzzarc, 26);
          if (this.asG == null) {}
          zzvk.zzc[] arrayOfzzc;
          for (i = 0;; i = this.asG.length)
          {
            arrayOfzzc = new zzvk.zzc[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.asG, 0, arrayOfzzc, 0, i);
              j = i;
            }
            while (j < arrayOfzzc.length - 1)
            {
              arrayOfzzc[j] = new zzvk.zzc();
              paramzzarc.zza(arrayOfzzc[j]);
              paramzzarc.cw();
              j += 1;
            }
          }
          arrayOfzzc[j] = new zzvk.zzc();
          paramzzarc.zza(arrayOfzzc[j]);
          this.asG = arrayOfzzc;
          break;
        case 32: 
          this.asH = Boolean.valueOf(paramzzarc.cC());
          break;
        case 42: 
          if (this.asI == null) {
            this.asI = new zzvk.zzd();
          }
          paramzzarc.zza(this.asI);
        }
      }
    }
    
    public zzb zzbyp()
    {
      this.asE = null;
      this.asF = null;
      this.asG = zzvk.zzc.zzbyq();
      this.asH = null;
      this.asI = null;
      this.bqE = -1;
      return this;
    }
    
    protected int zzx()
    {
      int i = super.zzx();
      int j = i;
      if (this.asE != null) {
        j = i + zzard.zzag(1, this.asE.intValue());
      }
      i = j;
      if (this.asF != null) {
        i = j + zzard.zzs(2, this.asF);
      }
      j = i;
      if (this.asG != null)
      {
        j = i;
        if (this.asG.length > 0)
        {
          j = 0;
          while (j < this.asG.length)
          {
            zzvk.zzc localzzc = this.asG[j];
            int k = i;
            if (localzzc != null) {
              k = i + zzard.zzc(3, localzzc);
            }
            j += 1;
            i = k;
          }
          j = i;
        }
      }
      i = j;
      if (this.asH != null) {
        i = j + zzard.zzk(4, this.asH.booleanValue());
      }
      j = i;
      if (this.asI != null) {
        j = i + zzard.zzc(5, this.asI);
      }
      return j;
    }
  }
  
  public static final class zzc
    extends zzark
  {
    private static volatile zzc[] asJ;
    public zzvk.zzf asK;
    public zzvk.zzd asL;
    public Boolean asM;
    public String asN;
    
    public zzc()
    {
      zzbyr();
    }
    
    public static zzc[] zzbyq()
    {
      if (asJ == null) {}
      synchronized (zzari.bqD)
      {
        if (asJ == null) {
          asJ = new zzc[0];
        }
        return asJ;
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
          if (this.asK == null)
          {
            if (((zzc)paramObject).asK != null) {
              return false;
            }
          }
          else if (!this.asK.equals(((zzc)paramObject).asK)) {
            return false;
          }
          if (this.asL == null)
          {
            if (((zzc)paramObject).asL != null) {
              return false;
            }
          }
          else if (!this.asL.equals(((zzc)paramObject).asL)) {
            return false;
          }
          if (this.asM == null)
          {
            if (((zzc)paramObject).asM != null) {
              return false;
            }
          }
          else if (!this.asM.equals(((zzc)paramObject).asM)) {
            return false;
          }
          if (this.asN != null) {
            break;
          }
        } while (((zzc)paramObject).asN == null);
        return false;
      } while (this.asN.equals(((zzc)paramObject).asN));
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
      if (this.asK == null)
      {
        i = 0;
        if (this.asL != null) {
          break label88;
        }
        j = 0;
        if (this.asM != null) {
          break label99;
        }
        k = 0;
        label42:
        if (this.asN != null) {
          break label110;
        }
      }
      for (;;)
      {
        return (k + (j + (i + (n + 527) * 31) * 31) * 31) * 31 + m;
        i = this.asK.hashCode();
        break;
        label88:
        j = this.asL.hashCode();
        break label33;
        label99:
        k = this.asM.hashCode();
        break label42;
        label110:
        m = this.asN.hashCode();
      }
    }
    
    public void zza(zzard paramzzard)
      throws IOException
    {
      if (this.asK != null) {
        paramzzard.zza(1, this.asK);
      }
      if (this.asL != null) {
        paramzzard.zza(2, this.asL);
      }
      if (this.asM != null) {
        paramzzard.zzj(3, this.asM.booleanValue());
      }
      if (this.asN != null) {
        paramzzard.zzr(4, this.asN);
      }
      super.zza(paramzzard);
    }
    
    public zzc zzaf(zzarc paramzzarc)
      throws IOException
    {
      for (;;)
      {
        int i = paramzzarc.cw();
        switch (i)
        {
        default: 
          if (zzarn.zzb(paramzzarc, i)) {}
          break;
        case 0: 
          return this;
        case 10: 
          if (this.asK == null) {
            this.asK = new zzvk.zzf();
          }
          paramzzarc.zza(this.asK);
          break;
        case 18: 
          if (this.asL == null) {
            this.asL = new zzvk.zzd();
          }
          paramzzarc.zza(this.asL);
          break;
        case 24: 
          this.asM = Boolean.valueOf(paramzzarc.cC());
          break;
        case 34: 
          this.asN = paramzzarc.readString();
        }
      }
    }
    
    public zzc zzbyr()
    {
      this.asK = null;
      this.asL = null;
      this.asM = null;
      this.asN = null;
      this.bqE = -1;
      return this;
    }
    
    protected int zzx()
    {
      int j = super.zzx();
      int i = j;
      if (this.asK != null) {
        i = j + zzard.zzc(1, this.asK);
      }
      j = i;
      if (this.asL != null) {
        j = i + zzard.zzc(2, this.asL);
      }
      i = j;
      if (this.asM != null) {
        i = j + zzard.zzk(3, this.asM.booleanValue());
      }
      j = i;
      if (this.asN != null) {
        j = i + zzard.zzs(4, this.asN);
      }
      return j;
    }
  }
  
  public static final class zzd
    extends zzark
  {
    public Integer asO;
    public Boolean asP;
    public String asQ;
    public String asR;
    public String asS;
    
    public zzd()
    {
      zzbys();
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {}
      do
      {
        do
        {
          return true;
          if (!(paramObject instanceof zzd)) {
            return false;
          }
          paramObject = (zzd)paramObject;
          if (this.asO == null)
          {
            if (((zzd)paramObject).asO != null) {
              return false;
            }
          }
          else if (!this.asO.equals(((zzd)paramObject).asO)) {
            return false;
          }
          if (this.asP == null)
          {
            if (((zzd)paramObject).asP != null) {
              return false;
            }
          }
          else if (!this.asP.equals(((zzd)paramObject).asP)) {
            return false;
          }
          if (this.asQ == null)
          {
            if (((zzd)paramObject).asQ != null) {
              return false;
            }
          }
          else if (!this.asQ.equals(((zzd)paramObject).asQ)) {
            return false;
          }
          if (this.asR == null)
          {
            if (((zzd)paramObject).asR != null) {
              return false;
            }
          }
          else if (!this.asR.equals(((zzd)paramObject).asR)) {
            return false;
          }
          if (this.asS != null) {
            break;
          }
        } while (((zzd)paramObject).asS == null);
        return false;
      } while (this.asS.equals(((zzd)paramObject).asS));
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
      if (this.asO == null)
      {
        i = 0;
        if (this.asP != null) {
          break label104;
        }
        j = 0;
        if (this.asQ != null) {
          break label115;
        }
        k = 0;
        if (this.asR != null) {
          break label126;
        }
        m = 0;
        label52:
        if (this.asS != null) {
          break label138;
        }
      }
      for (;;)
      {
        return (m + (k + (j + (i + (i1 + 527) * 31) * 31) * 31) * 31) * 31 + n;
        i = this.asO.intValue();
        break;
        label104:
        j = this.asP.hashCode();
        break label33;
        label115:
        k = this.asQ.hashCode();
        break label42;
        label126:
        m = this.asR.hashCode();
        break label52;
        label138:
        n = this.asS.hashCode();
      }
    }
    
    public void zza(zzard paramzzard)
      throws IOException
    {
      if (this.asO != null) {
        paramzzard.zzae(1, this.asO.intValue());
      }
      if (this.asP != null) {
        paramzzard.zzj(2, this.asP.booleanValue());
      }
      if (this.asQ != null) {
        paramzzard.zzr(3, this.asQ);
      }
      if (this.asR != null) {
        paramzzard.zzr(4, this.asR);
      }
      if (this.asS != null) {
        paramzzard.zzr(5, this.asS);
      }
      super.zza(paramzzard);
    }
    
    public zzd zzag(zzarc paramzzarc)
      throws IOException
    {
      for (;;)
      {
        int i = paramzzarc.cw();
        switch (i)
        {
        default: 
          if (zzarn.zzb(paramzzarc, i)) {}
          break;
        case 0: 
          return this;
        case 8: 
          i = paramzzarc.cA();
          switch (i)
          {
          default: 
            break;
          case 0: 
          case 1: 
          case 2: 
          case 3: 
          case 4: 
            this.asO = Integer.valueOf(i);
          }
          break;
        case 16: 
          this.asP = Boolean.valueOf(paramzzarc.cC());
          break;
        case 26: 
          this.asQ = paramzzarc.readString();
          break;
        case 34: 
          this.asR = paramzzarc.readString();
          break;
        case 42: 
          this.asS = paramzzarc.readString();
        }
      }
    }
    
    public zzd zzbys()
    {
      this.asP = null;
      this.asQ = null;
      this.asR = null;
      this.asS = null;
      this.bqE = -1;
      return this;
    }
    
    protected int zzx()
    {
      int j = super.zzx();
      int i = j;
      if (this.asO != null) {
        i = j + zzard.zzag(1, this.asO.intValue());
      }
      j = i;
      if (this.asP != null) {
        j = i + zzard.zzk(2, this.asP.booleanValue());
      }
      i = j;
      if (this.asQ != null) {
        i = j + zzard.zzs(3, this.asQ);
      }
      j = i;
      if (this.asR != null) {
        j = i + zzard.zzs(4, this.asR);
      }
      i = j;
      if (this.asS != null) {
        i = j + zzard.zzs(5, this.asS);
      }
      return i;
    }
  }
  
  public static final class zze
    extends zzark
  {
    private static volatile zze[] asT;
    public Integer asE;
    public String asU;
    public zzvk.zzc asV;
    
    public zze()
    {
      zzbyu();
    }
    
    public static zze[] zzbyt()
    {
      if (asT == null) {}
      synchronized (zzari.bqD)
      {
        if (asT == null) {
          asT = new zze[0];
        }
        return asT;
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
          if (this.asE == null)
          {
            if (((zze)paramObject).asE != null) {
              return false;
            }
          }
          else if (!this.asE.equals(((zze)paramObject).asE)) {
            return false;
          }
          if (this.asU == null)
          {
            if (((zze)paramObject).asU != null) {
              return false;
            }
          }
          else if (!this.asU.equals(((zze)paramObject).asU)) {
            return false;
          }
          if (this.asV != null) {
            break;
          }
        } while (((zze)paramObject).asV == null);
        return false;
      } while (this.asV.equals(((zze)paramObject).asV));
      return false;
    }
    
    public int hashCode()
    {
      int k = 0;
      int m = getClass().getName().hashCode();
      int i;
      int j;
      if (this.asE == null)
      {
        i = 0;
        if (this.asU != null) {
          break label72;
        }
        j = 0;
        label32:
        if (this.asV != null) {
          break label83;
        }
      }
      for (;;)
      {
        return (j + (i + (m + 527) * 31) * 31) * 31 + k;
        i = this.asE.hashCode();
        break;
        label72:
        j = this.asU.hashCode();
        break label32;
        label83:
        k = this.asV.hashCode();
      }
    }
    
    public void zza(zzard paramzzard)
      throws IOException
    {
      if (this.asE != null) {
        paramzzard.zzae(1, this.asE.intValue());
      }
      if (this.asU != null) {
        paramzzard.zzr(2, this.asU);
      }
      if (this.asV != null) {
        paramzzard.zza(3, this.asV);
      }
      super.zza(paramzzard);
    }
    
    public zze zzah(zzarc paramzzarc)
      throws IOException
    {
      for (;;)
      {
        int i = paramzzarc.cw();
        switch (i)
        {
        default: 
          if (zzarn.zzb(paramzzarc, i)) {}
          break;
        case 0: 
          return this;
        case 8: 
          this.asE = Integer.valueOf(paramzzarc.cA());
          break;
        case 18: 
          this.asU = paramzzarc.readString();
          break;
        case 26: 
          if (this.asV == null) {
            this.asV = new zzvk.zzc();
          }
          paramzzarc.zza(this.asV);
        }
      }
    }
    
    public zze zzbyu()
    {
      this.asE = null;
      this.asU = null;
      this.asV = null;
      this.bqE = -1;
      return this;
    }
    
    protected int zzx()
    {
      int j = super.zzx();
      int i = j;
      if (this.asE != null) {
        i = j + zzard.zzag(1, this.asE.intValue());
      }
      j = i;
      if (this.asU != null) {
        j = i + zzard.zzs(2, this.asU);
      }
      i = j;
      if (this.asV != null) {
        i = j + zzard.zzc(3, this.asV);
      }
      return i;
    }
  }
  
  public static final class zzf
    extends zzark
  {
    public Integer asW;
    public String asX;
    public Boolean asY;
    public String[] asZ;
    
    public zzf()
    {
      zzbyv();
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
        if (this.asW == null)
        {
          if (((zzf)paramObject).asW != null) {
            return false;
          }
        }
        else if (!this.asW.equals(((zzf)paramObject).asW)) {
          return false;
        }
        if (this.asX == null)
        {
          if (((zzf)paramObject).asX != null) {
            return false;
          }
        }
        else if (!this.asX.equals(((zzf)paramObject).asX)) {
          return false;
        }
        if (this.asY == null)
        {
          if (((zzf)paramObject).asY != null) {
            return false;
          }
        }
        else if (!this.asY.equals(((zzf)paramObject).asY)) {
          return false;
        }
      } while (zzari.equals(this.asZ, ((zzf)paramObject).asZ));
      return false;
    }
    
    public int hashCode()
    {
      int k = 0;
      int m = getClass().getName().hashCode();
      int i;
      int j;
      if (this.asW == null)
      {
        i = 0;
        if (this.asX != null) {
          break label83;
        }
        j = 0;
        label32:
        if (this.asY != null) {
          break label94;
        }
      }
      for (;;)
      {
        return ((j + (i + (m + 527) * 31) * 31) * 31 + k) * 31 + zzari.hashCode(this.asZ);
        i = this.asW.intValue();
        break;
        label83:
        j = this.asX.hashCode();
        break label32;
        label94:
        k = this.asY.hashCode();
      }
    }
    
    public void zza(zzard paramzzard)
      throws IOException
    {
      if (this.asW != null) {
        paramzzard.zzae(1, this.asW.intValue());
      }
      if (this.asX != null) {
        paramzzard.zzr(2, this.asX);
      }
      if (this.asY != null) {
        paramzzard.zzj(3, this.asY.booleanValue());
      }
      if ((this.asZ != null) && (this.asZ.length > 0))
      {
        int i = 0;
        while (i < this.asZ.length)
        {
          String str = this.asZ[i];
          if (str != null) {
            paramzzard.zzr(4, str);
          }
          i += 1;
        }
      }
      super.zza(paramzzard);
    }
    
    public zzf zzai(zzarc paramzzarc)
      throws IOException
    {
      for (;;)
      {
        int i = paramzzarc.cw();
        switch (i)
        {
        default: 
          if (zzarn.zzb(paramzzarc, i)) {}
          break;
        case 0: 
          return this;
        case 8: 
          i = paramzzarc.cA();
          switch (i)
          {
          default: 
            break;
          case 0: 
          case 1: 
          case 2: 
          case 3: 
          case 4: 
          case 5: 
          case 6: 
            this.asW = Integer.valueOf(i);
          }
          break;
        case 18: 
          this.asX = paramzzarc.readString();
          break;
        case 24: 
          this.asY = Boolean.valueOf(paramzzarc.cC());
          break;
        case 34: 
          int j = zzarn.zzc(paramzzarc, 34);
          if (this.asZ == null) {}
          String[] arrayOfString;
          for (i = 0;; i = this.asZ.length)
          {
            arrayOfString = new String[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.asZ, 0, arrayOfString, 0, i);
              j = i;
            }
            while (j < arrayOfString.length - 1)
            {
              arrayOfString[j] = paramzzarc.readString();
              paramzzarc.cw();
              j += 1;
            }
          }
          arrayOfString[j] = paramzzarc.readString();
          this.asZ = arrayOfString;
        }
      }
    }
    
    public zzf zzbyv()
    {
      this.asX = null;
      this.asY = null;
      this.asZ = zzarn.bqK;
      this.bqE = -1;
      return this;
    }
    
    protected int zzx()
    {
      int n = 0;
      int j = super.zzx();
      int i = j;
      if (this.asW != null) {
        i = j + zzard.zzag(1, this.asW.intValue());
      }
      j = i;
      if (this.asX != null) {
        j = i + zzard.zzs(2, this.asX);
      }
      i = j;
      if (this.asY != null) {
        i = j + zzard.zzk(3, this.asY.booleanValue());
      }
      j = i;
      if (this.asZ != null)
      {
        j = i;
        if (this.asZ.length > 0)
        {
          int k = 0;
          int m = 0;
          j = n;
          while (j < this.asZ.length)
          {
            String str = this.asZ[j];
            int i1 = k;
            n = m;
            if (str != null)
            {
              n = m + 1;
              i1 = k + zzard.zzuy(str);
            }
            j += 1;
            k = i1;
            m = n;
          }
          j = i + k + m * 1;
        }
      }
      return j;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzvk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */