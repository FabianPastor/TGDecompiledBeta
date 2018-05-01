package com.google.android.gms.internal;

import java.io.IOException;

public abstract interface zzauu
{
  public static final class zza
    extends zzbyd<zza>
  {
    private static volatile zza[] zzbwm;
    public Integer zzbwn;
    public zzauu.zze[] zzbwo;
    public zzauu.zzb[] zzbwp;
    
    public zza()
    {
      zzNm();
    }
    
    public static zza[] zzNl()
    {
      if (zzbwm == null) {}
      synchronized (zzbyh.zzcwK)
      {
        if (zzbwm == null) {
          zzbwm = new zza[0];
        }
        return zzbwm;
      }
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      boolean bool1;
      if (paramObject == this) {
        bool1 = true;
      }
      do
      {
        do
        {
          return bool1;
          bool1 = bool2;
        } while (!(paramObject instanceof zza));
        paramObject = (zza)paramObject;
        if (this.zzbwn != null) {
          break;
        }
        bool1 = bool2;
      } while (((zza)paramObject).zzbwn != null);
      while (this.zzbwn.equals(((zza)paramObject).zzbwn))
      {
        bool1 = bool2;
        if (!zzbyh.equals(this.zzbwo, ((zza)paramObject).zzbwo)) {
          break;
        }
        bool1 = bool2;
        if (!zzbyh.equals(this.zzbwp, ((zza)paramObject).zzbwp)) {
          break;
        }
        if ((this.zzcwC != null) && (!this.zzcwC.isEmpty())) {
          break label127;
        }
        if (((zza)paramObject).zzcwC != null)
        {
          bool1 = bool2;
          if (!((zza)paramObject).zzcwC.isEmpty()) {
            break;
          }
        }
        return true;
      }
      return false;
      label127:
      return this.zzcwC.equals(((zza)paramObject).zzcwC);
    }
    
    public int hashCode()
    {
      int k = 0;
      int m = getClass().getName().hashCode();
      int i;
      int n;
      int i1;
      if (this.zzbwn == null)
      {
        i = 0;
        n = zzbyh.hashCode(this.zzbwo);
        i1 = zzbyh.hashCode(this.zzbwp);
        j = k;
        if (this.zzcwC != null) {
          if (!this.zzcwC.isEmpty()) {
            break label102;
          }
        }
      }
      label102:
      for (int j = k;; j = this.zzcwC.hashCode())
      {
        return (((i + (m + 527) * 31) * 31 + n) * 31 + i1) * 31 + j;
        i = this.zzbwn.hashCode();
        break;
      }
    }
    
    public zza zzG(zzbyb paramzzbyb)
      throws IOException
    {
      for (;;)
      {
        int i = paramzzbyb.zzaeW();
        int j;
        Object localObject;
        switch (i)
        {
        default: 
          if (super.zza(paramzzbyb, i)) {}
          break;
        case 0: 
          return this;
        case 8: 
          this.zzbwn = Integer.valueOf(paramzzbyb.zzafa());
          break;
        case 18: 
          j = zzbym.zzb(paramzzbyb, 18);
          if (this.zzbwo == null) {}
          for (i = 0;; i = this.zzbwo.length)
          {
            localObject = new zzauu.zze[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzbwo, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length - 1)
            {
              localObject[j] = new zzauu.zze();
              paramzzbyb.zza(localObject[j]);
              paramzzbyb.zzaeW();
              j += 1;
            }
          }
          localObject[j] = new zzauu.zze();
          paramzzbyb.zza(localObject[j]);
          this.zzbwo = ((zzauu.zze[])localObject);
          break;
        case 26: 
          j = zzbym.zzb(paramzzbyb, 26);
          if (this.zzbwp == null) {}
          for (i = 0;; i = this.zzbwp.length)
          {
            localObject = new zzauu.zzb[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzbwp, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length - 1)
            {
              localObject[j] = new zzauu.zzb();
              paramzzbyb.zza(localObject[j]);
              paramzzbyb.zzaeW();
              j += 1;
            }
          }
          localObject[j] = new zzauu.zzb();
          paramzzbyb.zza(localObject[j]);
          this.zzbwp = ((zzauu.zzb[])localObject);
        }
      }
    }
    
    public zza zzNm()
    {
      this.zzbwn = null;
      this.zzbwo = zzauu.zze.zzNs();
      this.zzbwp = zzauu.zzb.zzNn();
      this.zzcwC = null;
      this.zzcwL = -1;
      return this;
    }
    
    public void zza(zzbyc paramzzbyc)
      throws IOException
    {
      int j = 0;
      if (this.zzbwn != null) {
        paramzzbyc.zzJ(1, this.zzbwn.intValue());
      }
      int i;
      Object localObject;
      if ((this.zzbwo != null) && (this.zzbwo.length > 0))
      {
        i = 0;
        while (i < this.zzbwo.length)
        {
          localObject = this.zzbwo[i];
          if (localObject != null) {
            paramzzbyc.zza(2, (zzbyj)localObject);
          }
          i += 1;
        }
      }
      if ((this.zzbwp != null) && (this.zzbwp.length > 0))
      {
        i = j;
        while (i < this.zzbwp.length)
        {
          localObject = this.zzbwp[i];
          if (localObject != null) {
            paramzzbyc.zza(3, (zzbyj)localObject);
          }
          i += 1;
        }
      }
      super.zza(paramzzbyc);
    }
    
    protected int zzu()
    {
      int m = 0;
      int i = super.zzu();
      int j = i;
      if (this.zzbwn != null) {
        j = i + zzbyc.zzL(1, this.zzbwn.intValue());
      }
      i = j;
      Object localObject;
      if (this.zzbwo != null)
      {
        i = j;
        if (this.zzbwo.length > 0)
        {
          i = j;
          j = 0;
          while (j < this.zzbwo.length)
          {
            localObject = this.zzbwo[j];
            k = i;
            if (localObject != null) {
              k = i + zzbyc.zzc(2, (zzbyj)localObject);
            }
            j += 1;
            i = k;
          }
        }
      }
      int k = i;
      if (this.zzbwp != null)
      {
        k = i;
        if (this.zzbwp.length > 0)
        {
          j = m;
          for (;;)
          {
            k = i;
            if (j >= this.zzbwp.length) {
              break;
            }
            localObject = this.zzbwp[j];
            k = i;
            if (localObject != null) {
              k = i + zzbyc.zzc(3, (zzbyj)localObject);
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
    extends zzbyd<zzb>
  {
    private static volatile zzb[] zzbwq;
    public Integer zzbwr;
    public String zzbws;
    public zzauu.zzc[] zzbwt;
    public Boolean zzbwu;
    public zzauu.zzd zzbwv;
    
    public zzb()
    {
      zzNo();
    }
    
    public static zzb[] zzNn()
    {
      if (zzbwq == null) {}
      synchronized (zzbyh.zzcwK)
      {
        if (zzbwq == null) {
          zzbwq = new zzb[0];
        }
        return zzbwq;
      }
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      boolean bool1;
      if (paramObject == this) {
        bool1 = true;
      }
      label41:
      label57:
      label89:
      do
      {
        do
        {
          do
          {
            do
            {
              do
              {
                do
                {
                  return bool1;
                  bool1 = bool2;
                } while (!(paramObject instanceof zzb));
                paramObject = (zzb)paramObject;
                if (this.zzbwr != null) {
                  break;
                }
                bool1 = bool2;
              } while (((zzb)paramObject).zzbwr != null);
              if (this.zzbws != null) {
                break label159;
              }
              bool1 = bool2;
            } while (((zzb)paramObject).zzbws != null);
            bool1 = bool2;
          } while (!zzbyh.equals(this.zzbwt, ((zzb)paramObject).zzbwt));
          if (this.zzbwu != null) {
            break label175;
          }
          bool1 = bool2;
        } while (((zzb)paramObject).zzbwu != null);
        if (this.zzbwv != null) {
          break label191;
        }
        bool1 = bool2;
      } while (((zzb)paramObject).zzbwv != null);
      for (;;)
      {
        if ((this.zzcwC == null) || (this.zzcwC.isEmpty()))
        {
          if (((zzb)paramObject).zzcwC != null)
          {
            bool1 = bool2;
            if (!((zzb)paramObject).zzcwC.isEmpty()) {
              break;
            }
          }
          return true;
          if (this.zzbwr.equals(((zzb)paramObject).zzbwr)) {
            break label41;
          }
          return false;
          label159:
          if (this.zzbws.equals(((zzb)paramObject).zzbws)) {
            break label57;
          }
          return false;
          label175:
          if (this.zzbwu.equals(((zzb)paramObject).zzbwu)) {
            break label89;
          }
          return false;
          label191:
          if (!this.zzbwv.equals(((zzb)paramObject).zzbwv)) {
            return false;
          }
        }
      }
      return this.zzcwC.equals(((zzb)paramObject).zzcwC);
    }
    
    public int hashCode()
    {
      int i1 = 0;
      int i2 = getClass().getName().hashCode();
      int i;
      int j;
      label33:
      int i3;
      int k;
      label51:
      int m;
      if (this.zzbwr == null)
      {
        i = 0;
        if (this.zzbws != null) {
          break label137;
        }
        j = 0;
        i3 = zzbyh.hashCode(this.zzbwt);
        if (this.zzbwu != null) {
          break label148;
        }
        k = 0;
        if (this.zzbwv != null) {
          break label159;
        }
        m = 0;
        label61:
        n = i1;
        if (this.zzcwC != null) {
          if (!this.zzcwC.isEmpty()) {
            break label171;
          }
        }
      }
      label137:
      label148:
      label159:
      label171:
      for (int n = i1;; n = this.zzcwC.hashCode())
      {
        return (m + (k + ((j + (i + (i2 + 527) * 31) * 31) * 31 + i3) * 31) * 31) * 31 + n;
        i = this.zzbwr.hashCode();
        break;
        j = this.zzbws.hashCode();
        break label33;
        k = this.zzbwu.hashCode();
        break label51;
        m = this.zzbwv.hashCode();
        break label61;
      }
    }
    
    public zzb zzH(zzbyb paramzzbyb)
      throws IOException
    {
      for (;;)
      {
        int i = paramzzbyb.zzaeW();
        switch (i)
        {
        default: 
          if (super.zza(paramzzbyb, i)) {}
          break;
        case 0: 
          return this;
        case 8: 
          this.zzbwr = Integer.valueOf(paramzzbyb.zzafa());
          break;
        case 18: 
          this.zzbws = paramzzbyb.readString();
          break;
        case 26: 
          int j = zzbym.zzb(paramzzbyb, 26);
          if (this.zzbwt == null) {}
          zzauu.zzc[] arrayOfzzc;
          for (i = 0;; i = this.zzbwt.length)
          {
            arrayOfzzc = new zzauu.zzc[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzbwt, 0, arrayOfzzc, 0, i);
              j = i;
            }
            while (j < arrayOfzzc.length - 1)
            {
              arrayOfzzc[j] = new zzauu.zzc();
              paramzzbyb.zza(arrayOfzzc[j]);
              paramzzbyb.zzaeW();
              j += 1;
            }
          }
          arrayOfzzc[j] = new zzauu.zzc();
          paramzzbyb.zza(arrayOfzzc[j]);
          this.zzbwt = arrayOfzzc;
          break;
        case 32: 
          this.zzbwu = Boolean.valueOf(paramzzbyb.zzafc());
          break;
        case 42: 
          if (this.zzbwv == null) {
            this.zzbwv = new zzauu.zzd();
          }
          paramzzbyb.zza(this.zzbwv);
        }
      }
    }
    
    public zzb zzNo()
    {
      this.zzbwr = null;
      this.zzbws = null;
      this.zzbwt = zzauu.zzc.zzNp();
      this.zzbwu = null;
      this.zzbwv = null;
      this.zzcwC = null;
      this.zzcwL = -1;
      return this;
    }
    
    public void zza(zzbyc paramzzbyc)
      throws IOException
    {
      if (this.zzbwr != null) {
        paramzzbyc.zzJ(1, this.zzbwr.intValue());
      }
      if (this.zzbws != null) {
        paramzzbyc.zzq(2, this.zzbws);
      }
      if ((this.zzbwt != null) && (this.zzbwt.length > 0))
      {
        int i = 0;
        while (i < this.zzbwt.length)
        {
          zzauu.zzc localzzc = this.zzbwt[i];
          if (localzzc != null) {
            paramzzbyc.zza(3, localzzc);
          }
          i += 1;
        }
      }
      if (this.zzbwu != null) {
        paramzzbyc.zzg(4, this.zzbwu.booleanValue());
      }
      if (this.zzbwv != null) {
        paramzzbyc.zza(5, this.zzbwv);
      }
      super.zza(paramzzbyc);
    }
    
    protected int zzu()
    {
      int i = super.zzu();
      int j = i;
      if (this.zzbwr != null) {
        j = i + zzbyc.zzL(1, this.zzbwr.intValue());
      }
      i = j;
      if (this.zzbws != null) {
        i = j + zzbyc.zzr(2, this.zzbws);
      }
      j = i;
      if (this.zzbwt != null)
      {
        j = i;
        if (this.zzbwt.length > 0)
        {
          j = 0;
          while (j < this.zzbwt.length)
          {
            zzauu.zzc localzzc = this.zzbwt[j];
            int k = i;
            if (localzzc != null) {
              k = i + zzbyc.zzc(3, localzzc);
            }
            j += 1;
            i = k;
          }
          j = i;
        }
      }
      i = j;
      if (this.zzbwu != null) {
        i = j + zzbyc.zzh(4, this.zzbwu.booleanValue());
      }
      j = i;
      if (this.zzbwv != null) {
        j = i + zzbyc.zzc(5, this.zzbwv);
      }
      return j;
    }
  }
  
  public static final class zzc
    extends zzbyd<zzc>
  {
    private static volatile zzc[] zzbww;
    public String zzbwA;
    public zzauu.zzf zzbwx;
    public zzauu.zzd zzbwy;
    public Boolean zzbwz;
    
    public zzc()
    {
      zzNq();
    }
    
    public static zzc[] zzNp()
    {
      if (zzbww == null) {}
      synchronized (zzbyh.zzcwK)
      {
        if (zzbww == null) {
          zzbww = new zzc[0];
        }
        return zzbww;
      }
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      boolean bool1;
      if (paramObject == this) {
        bool1 = true;
      }
      label41:
      label57:
      label73:
      do
      {
        do
        {
          do
          {
            do
            {
              do
              {
                return bool1;
                bool1 = bool2;
              } while (!(paramObject instanceof zzc));
              paramObject = (zzc)paramObject;
              if (this.zzbwx != null) {
                break;
              }
              bool1 = bool2;
            } while (((zzc)paramObject).zzbwx != null);
            if (this.zzbwy != null) {
              break label143;
            }
            bool1 = bool2;
          } while (((zzc)paramObject).zzbwy != null);
          if (this.zzbwz != null) {
            break label159;
          }
          bool1 = bool2;
        } while (((zzc)paramObject).zzbwz != null);
        if (this.zzbwA != null) {
          break label175;
        }
        bool1 = bool2;
      } while (((zzc)paramObject).zzbwA != null);
      for (;;)
      {
        if ((this.zzcwC == null) || (this.zzcwC.isEmpty()))
        {
          if (((zzc)paramObject).zzcwC != null)
          {
            bool1 = bool2;
            if (!((zzc)paramObject).zzcwC.isEmpty()) {
              break;
            }
          }
          return true;
          if (this.zzbwx.equals(((zzc)paramObject).zzbwx)) {
            break label41;
          }
          return false;
          label143:
          if (this.zzbwy.equals(((zzc)paramObject).zzbwy)) {
            break label57;
          }
          return false;
          label159:
          if (this.zzbwz.equals(((zzc)paramObject).zzbwz)) {
            break label73;
          }
          return false;
          label175:
          if (!this.zzbwA.equals(((zzc)paramObject).zzbwA)) {
            return false;
          }
        }
      }
      return this.zzcwC.equals(((zzc)paramObject).zzcwC);
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
      if (this.zzbwx == null)
      {
        i = 0;
        if (this.zzbwy != null) {
          break label122;
        }
        j = 0;
        if (this.zzbwz != null) {
          break label133;
        }
        k = 0;
        if (this.zzbwA != null) {
          break label144;
        }
        m = 0;
        label52:
        n = i1;
        if (this.zzcwC != null) {
          if (!this.zzcwC.isEmpty()) {
            break label156;
          }
        }
      }
      label122:
      label133:
      label144:
      label156:
      for (int n = i1;; n = this.zzcwC.hashCode())
      {
        return (m + (k + (j + (i + (i2 + 527) * 31) * 31) * 31) * 31) * 31 + n;
        i = this.zzbwx.hashCode();
        break;
        j = this.zzbwy.hashCode();
        break label33;
        k = this.zzbwz.hashCode();
        break label42;
        m = this.zzbwA.hashCode();
        break label52;
      }
    }
    
    public zzc zzI(zzbyb paramzzbyb)
      throws IOException
    {
      for (;;)
      {
        int i = paramzzbyb.zzaeW();
        switch (i)
        {
        default: 
          if (super.zza(paramzzbyb, i)) {}
          break;
        case 0: 
          return this;
        case 10: 
          if (this.zzbwx == null) {
            this.zzbwx = new zzauu.zzf();
          }
          paramzzbyb.zza(this.zzbwx);
          break;
        case 18: 
          if (this.zzbwy == null) {
            this.zzbwy = new zzauu.zzd();
          }
          paramzzbyb.zza(this.zzbwy);
          break;
        case 24: 
          this.zzbwz = Boolean.valueOf(paramzzbyb.zzafc());
          break;
        case 34: 
          this.zzbwA = paramzzbyb.readString();
        }
      }
    }
    
    public zzc zzNq()
    {
      this.zzbwx = null;
      this.zzbwy = null;
      this.zzbwz = null;
      this.zzbwA = null;
      this.zzcwC = null;
      this.zzcwL = -1;
      return this;
    }
    
    public void zza(zzbyc paramzzbyc)
      throws IOException
    {
      if (this.zzbwx != null) {
        paramzzbyc.zza(1, this.zzbwx);
      }
      if (this.zzbwy != null) {
        paramzzbyc.zza(2, this.zzbwy);
      }
      if (this.zzbwz != null) {
        paramzzbyc.zzg(3, this.zzbwz.booleanValue());
      }
      if (this.zzbwA != null) {
        paramzzbyc.zzq(4, this.zzbwA);
      }
      super.zza(paramzzbyc);
    }
    
    protected int zzu()
    {
      int j = super.zzu();
      int i = j;
      if (this.zzbwx != null) {
        i = j + zzbyc.zzc(1, this.zzbwx);
      }
      j = i;
      if (this.zzbwy != null) {
        j = i + zzbyc.zzc(2, this.zzbwy);
      }
      i = j;
      if (this.zzbwz != null) {
        i = j + zzbyc.zzh(3, this.zzbwz.booleanValue());
      }
      j = i;
      if (this.zzbwA != null) {
        j = i + zzbyc.zzr(4, this.zzbwA);
      }
      return j;
    }
  }
  
  public static final class zzd
    extends zzbyd<zzd>
  {
    public Integer zzbwB;
    public Boolean zzbwC;
    public String zzbwD;
    public String zzbwE;
    public String zzbwF;
    
    public zzd()
    {
      zzNr();
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      boolean bool1;
      if (paramObject == this) {
        bool1 = true;
      }
      label41:
      label57:
      label73:
      label89:
      do
      {
        do
        {
          do
          {
            do
            {
              do
              {
                do
                {
                  return bool1;
                  bool1 = bool2;
                } while (!(paramObject instanceof zzd));
                paramObject = (zzd)paramObject;
                if (this.zzbwB != null) {
                  break;
                }
                bool1 = bool2;
              } while (((zzd)paramObject).zzbwB != null);
              if (this.zzbwC != null) {
                break label159;
              }
              bool1 = bool2;
            } while (((zzd)paramObject).zzbwC != null);
            if (this.zzbwD != null) {
              break label175;
            }
            bool1 = bool2;
          } while (((zzd)paramObject).zzbwD != null);
          if (this.zzbwE != null) {
            break label191;
          }
          bool1 = bool2;
        } while (((zzd)paramObject).zzbwE != null);
        if (this.zzbwF != null) {
          break label207;
        }
        bool1 = bool2;
      } while (((zzd)paramObject).zzbwF != null);
      for (;;)
      {
        if ((this.zzcwC == null) || (this.zzcwC.isEmpty()))
        {
          if (((zzd)paramObject).zzcwC != null)
          {
            bool1 = bool2;
            if (!((zzd)paramObject).zzcwC.isEmpty()) {
              break;
            }
          }
          return true;
          if (this.zzbwB.equals(((zzd)paramObject).zzbwB)) {
            break label41;
          }
          return false;
          label159:
          if (this.zzbwC.equals(((zzd)paramObject).zzbwC)) {
            break label57;
          }
          return false;
          label175:
          if (this.zzbwD.equals(((zzd)paramObject).zzbwD)) {
            break label73;
          }
          return false;
          label191:
          if (this.zzbwE.equals(((zzd)paramObject).zzbwE)) {
            break label89;
          }
          return false;
          label207:
          if (!this.zzbwF.equals(((zzd)paramObject).zzbwF)) {
            return false;
          }
        }
      }
      return this.zzcwC.equals(((zzd)paramObject).zzcwC);
    }
    
    public int hashCode()
    {
      int i2 = 0;
      int i3 = getClass().getName().hashCode();
      int i;
      int j;
      label33:
      int k;
      label42:
      int m;
      label52:
      int n;
      if (this.zzbwB == null)
      {
        i = 0;
        if (this.zzbwC != null) {
          break label138;
        }
        j = 0;
        if (this.zzbwD != null) {
          break label149;
        }
        k = 0;
        if (this.zzbwE != null) {
          break label160;
        }
        m = 0;
        if (this.zzbwF != null) {
          break label172;
        }
        n = 0;
        label62:
        i1 = i2;
        if (this.zzcwC != null) {
          if (!this.zzcwC.isEmpty()) {
            break label184;
          }
        }
      }
      label138:
      label149:
      label160:
      label172:
      label184:
      for (int i1 = i2;; i1 = this.zzcwC.hashCode())
      {
        return (n + (m + (k + (j + (i + (i3 + 527) * 31) * 31) * 31) * 31) * 31) * 31 + i1;
        i = this.zzbwB.intValue();
        break;
        j = this.zzbwC.hashCode();
        break label33;
        k = this.zzbwD.hashCode();
        break label42;
        m = this.zzbwE.hashCode();
        break label52;
        n = this.zzbwF.hashCode();
        break label62;
      }
    }
    
    public zzd zzJ(zzbyb paramzzbyb)
      throws IOException
    {
      for (;;)
      {
        int i = paramzzbyb.zzaeW();
        switch (i)
        {
        default: 
          if (super.zza(paramzzbyb, i)) {}
          break;
        case 0: 
          return this;
        case 8: 
          i = paramzzbyb.zzafa();
          switch (i)
          {
          default: 
            break;
          case 0: 
          case 1: 
          case 2: 
          case 3: 
          case 4: 
            this.zzbwB = Integer.valueOf(i);
          }
          break;
        case 16: 
          this.zzbwC = Boolean.valueOf(paramzzbyb.zzafc());
          break;
        case 26: 
          this.zzbwD = paramzzbyb.readString();
          break;
        case 34: 
          this.zzbwE = paramzzbyb.readString();
          break;
        case 42: 
          this.zzbwF = paramzzbyb.readString();
        }
      }
    }
    
    public zzd zzNr()
    {
      this.zzbwC = null;
      this.zzbwD = null;
      this.zzbwE = null;
      this.zzbwF = null;
      this.zzcwC = null;
      this.zzcwL = -1;
      return this;
    }
    
    public void zza(zzbyc paramzzbyc)
      throws IOException
    {
      if (this.zzbwB != null) {
        paramzzbyc.zzJ(1, this.zzbwB.intValue());
      }
      if (this.zzbwC != null) {
        paramzzbyc.zzg(2, this.zzbwC.booleanValue());
      }
      if (this.zzbwD != null) {
        paramzzbyc.zzq(3, this.zzbwD);
      }
      if (this.zzbwE != null) {
        paramzzbyc.zzq(4, this.zzbwE);
      }
      if (this.zzbwF != null) {
        paramzzbyc.zzq(5, this.zzbwF);
      }
      super.zza(paramzzbyc);
    }
    
    protected int zzu()
    {
      int j = super.zzu();
      int i = j;
      if (this.zzbwB != null) {
        i = j + zzbyc.zzL(1, this.zzbwB.intValue());
      }
      j = i;
      if (this.zzbwC != null) {
        j = i + zzbyc.zzh(2, this.zzbwC.booleanValue());
      }
      i = j;
      if (this.zzbwD != null) {
        i = j + zzbyc.zzr(3, this.zzbwD);
      }
      j = i;
      if (this.zzbwE != null) {
        j = i + zzbyc.zzr(4, this.zzbwE);
      }
      i = j;
      if (this.zzbwF != null) {
        i = j + zzbyc.zzr(5, this.zzbwF);
      }
      return i;
    }
  }
  
  public static final class zze
    extends zzbyd<zze>
  {
    private static volatile zze[] zzbwG;
    public String zzbwH;
    public zzauu.zzc zzbwI;
    public Integer zzbwr;
    
    public zze()
    {
      zzNt();
    }
    
    public static zze[] zzNs()
    {
      if (zzbwG == null) {}
      synchronized (zzbyh.zzcwK)
      {
        if (zzbwG == null) {
          zzbwG = new zze[0];
        }
        return zzbwG;
      }
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      boolean bool1;
      if (paramObject == this) {
        bool1 = true;
      }
      label41:
      label57:
      do
      {
        do
        {
          do
          {
            do
            {
              return bool1;
              bool1 = bool2;
            } while (!(paramObject instanceof zze));
            paramObject = (zze)paramObject;
            if (this.zzbwr != null) {
              break;
            }
            bool1 = bool2;
          } while (((zze)paramObject).zzbwr != null);
          if (this.zzbwH != null) {
            break label127;
          }
          bool1 = bool2;
        } while (((zze)paramObject).zzbwH != null);
        if (this.zzbwI != null) {
          break label143;
        }
        bool1 = bool2;
      } while (((zze)paramObject).zzbwI != null);
      for (;;)
      {
        if ((this.zzcwC == null) || (this.zzcwC.isEmpty()))
        {
          if (((zze)paramObject).zzcwC != null)
          {
            bool1 = bool2;
            if (!((zze)paramObject).zzcwC.isEmpty()) {
              break;
            }
          }
          return true;
          if (this.zzbwr.equals(((zze)paramObject).zzbwr)) {
            break label41;
          }
          return false;
          label127:
          if (this.zzbwH.equals(((zze)paramObject).zzbwH)) {
            break label57;
          }
          return false;
          label143:
          if (!this.zzbwI.equals(((zze)paramObject).zzbwI)) {
            return false;
          }
        }
      }
      return this.zzcwC.equals(((zze)paramObject).zzcwC);
    }
    
    public int hashCode()
    {
      int n = 0;
      int i1 = getClass().getName().hashCode();
      int i;
      int j;
      label33:
      int k;
      if (this.zzbwr == null)
      {
        i = 0;
        if (this.zzbwH != null) {
          break label106;
        }
        j = 0;
        if (this.zzbwI != null) {
          break label117;
        }
        k = 0;
        label42:
        m = n;
        if (this.zzcwC != null) {
          if (!this.zzcwC.isEmpty()) {
            break label128;
          }
        }
      }
      label106:
      label117:
      label128:
      for (int m = n;; m = this.zzcwC.hashCode())
      {
        return (k + (j + (i + (i1 + 527) * 31) * 31) * 31) * 31 + m;
        i = this.zzbwr.hashCode();
        break;
        j = this.zzbwH.hashCode();
        break label33;
        k = this.zzbwI.hashCode();
        break label42;
      }
    }
    
    public zze zzK(zzbyb paramzzbyb)
      throws IOException
    {
      for (;;)
      {
        int i = paramzzbyb.zzaeW();
        switch (i)
        {
        default: 
          if (super.zza(paramzzbyb, i)) {}
          break;
        case 0: 
          return this;
        case 8: 
          this.zzbwr = Integer.valueOf(paramzzbyb.zzafa());
          break;
        case 18: 
          this.zzbwH = paramzzbyb.readString();
          break;
        case 26: 
          if (this.zzbwI == null) {
            this.zzbwI = new zzauu.zzc();
          }
          paramzzbyb.zza(this.zzbwI);
        }
      }
    }
    
    public zze zzNt()
    {
      this.zzbwr = null;
      this.zzbwH = null;
      this.zzbwI = null;
      this.zzcwC = null;
      this.zzcwL = -1;
      return this;
    }
    
    public void zza(zzbyc paramzzbyc)
      throws IOException
    {
      if (this.zzbwr != null) {
        paramzzbyc.zzJ(1, this.zzbwr.intValue());
      }
      if (this.zzbwH != null) {
        paramzzbyc.zzq(2, this.zzbwH);
      }
      if (this.zzbwI != null) {
        paramzzbyc.zza(3, this.zzbwI);
      }
      super.zza(paramzzbyc);
    }
    
    protected int zzu()
    {
      int j = super.zzu();
      int i = j;
      if (this.zzbwr != null) {
        i = j + zzbyc.zzL(1, this.zzbwr.intValue());
      }
      j = i;
      if (this.zzbwH != null) {
        j = i + zzbyc.zzr(2, this.zzbwH);
      }
      i = j;
      if (this.zzbwI != null) {
        i = j + zzbyc.zzc(3, this.zzbwI);
      }
      return i;
    }
  }
  
  public static final class zzf
    extends zzbyd<zzf>
  {
    public Integer zzbwJ;
    public String zzbwK;
    public Boolean zzbwL;
    public String[] zzbwM;
    
    public zzf()
    {
      zzNu();
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      boolean bool1;
      if (paramObject == this) {
        bool1 = true;
      }
      label41:
      label57:
      do
      {
        do
        {
          do
          {
            do
            {
              return bool1;
              bool1 = bool2;
            } while (!(paramObject instanceof zzf));
            paramObject = (zzf)paramObject;
            if (this.zzbwJ != null) {
              break;
            }
            bool1 = bool2;
          } while (((zzf)paramObject).zzbwJ != null);
          if (this.zzbwK != null) {
            break label143;
          }
          bool1 = bool2;
        } while (((zzf)paramObject).zzbwK != null);
        if (this.zzbwL != null) {
          break label159;
        }
        bool1 = bool2;
      } while (((zzf)paramObject).zzbwL != null);
      label143:
      label159:
      while (this.zzbwL.equals(((zzf)paramObject).zzbwL))
      {
        bool1 = bool2;
        if (!zzbyh.equals(this.zzbwM, ((zzf)paramObject).zzbwM)) {
          break;
        }
        if ((this.zzcwC != null) && (!this.zzcwC.isEmpty())) {
          break label175;
        }
        if (((zzf)paramObject).zzcwC != null)
        {
          bool1 = bool2;
          if (!((zzf)paramObject).zzcwC.isEmpty()) {
            break;
          }
        }
        return true;
        if (this.zzbwJ.equals(((zzf)paramObject).zzbwJ)) {
          break label41;
        }
        return false;
        if (this.zzbwK.equals(((zzf)paramObject).zzbwK)) {
          break label57;
        }
        return false;
      }
      return false;
      label175:
      return this.zzcwC.equals(((zzf)paramObject).zzcwC);
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
      int i2;
      if (this.zzbwJ == null)
      {
        i = 0;
        if (this.zzbwK != null) {
          break label121;
        }
        j = 0;
        if (this.zzbwL != null) {
          break label132;
        }
        k = 0;
        i2 = zzbyh.hashCode(this.zzbwM);
        m = n;
        if (this.zzcwC != null) {
          if (!this.zzcwC.isEmpty()) {
            break label143;
          }
        }
      }
      label121:
      label132:
      label143:
      for (int m = n;; m = this.zzcwC.hashCode())
      {
        return ((k + (j + (i + (i1 + 527) * 31) * 31) * 31) * 31 + i2) * 31 + m;
        i = this.zzbwJ.intValue();
        break;
        j = this.zzbwK.hashCode();
        break label33;
        k = this.zzbwL.hashCode();
        break label42;
      }
    }
    
    public zzf zzL(zzbyb paramzzbyb)
      throws IOException
    {
      for (;;)
      {
        int i = paramzzbyb.zzaeW();
        switch (i)
        {
        default: 
          if (super.zza(paramzzbyb, i)) {}
          break;
        case 0: 
          return this;
        case 8: 
          i = paramzzbyb.zzafa();
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
            this.zzbwJ = Integer.valueOf(i);
          }
          break;
        case 18: 
          this.zzbwK = paramzzbyb.readString();
          break;
        case 24: 
          this.zzbwL = Boolean.valueOf(paramzzbyb.zzafc());
          break;
        case 34: 
          int j = zzbym.zzb(paramzzbyb, 34);
          if (this.zzbwM == null) {}
          String[] arrayOfString;
          for (i = 0;; i = this.zzbwM.length)
          {
            arrayOfString = new String[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzbwM, 0, arrayOfString, 0, i);
              j = i;
            }
            while (j < arrayOfString.length - 1)
            {
              arrayOfString[j] = paramzzbyb.readString();
              paramzzbyb.zzaeW();
              j += 1;
            }
          }
          arrayOfString[j] = paramzzbyb.readString();
          this.zzbwM = arrayOfString;
        }
      }
    }
    
    public zzf zzNu()
    {
      this.zzbwK = null;
      this.zzbwL = null;
      this.zzbwM = zzbym.EMPTY_STRING_ARRAY;
      this.zzcwC = null;
      this.zzcwL = -1;
      return this;
    }
    
    public void zza(zzbyc paramzzbyc)
      throws IOException
    {
      if (this.zzbwJ != null) {
        paramzzbyc.zzJ(1, this.zzbwJ.intValue());
      }
      if (this.zzbwK != null) {
        paramzzbyc.zzq(2, this.zzbwK);
      }
      if (this.zzbwL != null) {
        paramzzbyc.zzg(3, this.zzbwL.booleanValue());
      }
      if ((this.zzbwM != null) && (this.zzbwM.length > 0))
      {
        int i = 0;
        while (i < this.zzbwM.length)
        {
          String str = this.zzbwM[i];
          if (str != null) {
            paramzzbyc.zzq(4, str);
          }
          i += 1;
        }
      }
      super.zza(paramzzbyc);
    }
    
    protected int zzu()
    {
      int n = 0;
      int j = super.zzu();
      int i = j;
      if (this.zzbwJ != null) {
        i = j + zzbyc.zzL(1, this.zzbwJ.intValue());
      }
      j = i;
      if (this.zzbwK != null) {
        j = i + zzbyc.zzr(2, this.zzbwK);
      }
      i = j;
      if (this.zzbwL != null) {
        i = j + zzbyc.zzh(3, this.zzbwL.booleanValue());
      }
      j = i;
      if (this.zzbwM != null)
      {
        j = i;
        if (this.zzbwM.length > 0)
        {
          int k = 0;
          int m = 0;
          j = n;
          while (j < this.zzbwM.length)
          {
            String str = this.zzbwM[j];
            int i1 = k;
            n = m;
            if (str != null)
            {
              n = m + 1;
              i1 = k + zzbyc.zzku(str);
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzauu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */