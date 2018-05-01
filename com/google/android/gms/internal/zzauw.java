package com.google.android.gms.internal;

import java.io.IOException;

public abstract interface zzauw
{
  public static final class zza
    extends zzbyd<zza>
  {
    private static volatile zza[] zzbwW;
    public zzauw.zzf zzbwX;
    public zzauw.zzf zzbwY;
    public Boolean zzbwZ;
    public Integer zzbwn;
    
    public zza()
    {
      zzNB();
    }
    
    public static zza[] zzNA()
    {
      if (zzbwW == null) {}
      synchronized (zzbyh.zzcwK)
      {
        if (zzbwW == null) {
          zzbwW = new zza[0];
        }
        return zzbwW;
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
              } while (!(paramObject instanceof zza));
              paramObject = (zza)paramObject;
              if (this.zzbwn != null) {
                break;
              }
              bool1 = bool2;
            } while (((zza)paramObject).zzbwn != null);
            if (this.zzbwX != null) {
              break label143;
            }
            bool1 = bool2;
          } while (((zza)paramObject).zzbwX != null);
          if (this.zzbwY != null) {
            break label159;
          }
          bool1 = bool2;
        } while (((zza)paramObject).zzbwY != null);
        if (this.zzbwZ != null) {
          break label175;
        }
        bool1 = bool2;
      } while (((zza)paramObject).zzbwZ != null);
      for (;;)
      {
        if ((this.zzcwC == null) || (this.zzcwC.isEmpty()))
        {
          if (((zza)paramObject).zzcwC != null)
          {
            bool1 = bool2;
            if (!((zza)paramObject).zzcwC.isEmpty()) {
              break;
            }
          }
          return true;
          if (this.zzbwn.equals(((zza)paramObject).zzbwn)) {
            break label41;
          }
          return false;
          label143:
          if (this.zzbwX.equals(((zza)paramObject).zzbwX)) {
            break label57;
          }
          return false;
          label159:
          if (this.zzbwY.equals(((zza)paramObject).zzbwY)) {
            break label73;
          }
          return false;
          label175:
          if (!this.zzbwZ.equals(((zza)paramObject).zzbwZ)) {
            return false;
          }
        }
      }
      return this.zzcwC.equals(((zza)paramObject).zzcwC);
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
      if (this.zzbwn == null)
      {
        i = 0;
        if (this.zzbwX != null) {
          break label122;
        }
        j = 0;
        if (this.zzbwY != null) {
          break label133;
        }
        k = 0;
        if (this.zzbwZ != null) {
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
        i = this.zzbwn.hashCode();
        break;
        j = this.zzbwX.hashCode();
        break label33;
        k = this.zzbwY.hashCode();
        break label42;
        m = this.zzbwZ.hashCode();
        break label52;
      }
    }
    
    public zza zzNB()
    {
      this.zzbwn = null;
      this.zzbwX = null;
      this.zzbwY = null;
      this.zzbwZ = null;
      this.zzcwC = null;
      this.zzcwL = -1;
      return this;
    }
    
    public zza zzP(zzbyb paramzzbyb)
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
          this.zzbwn = Integer.valueOf(paramzzbyb.zzafa());
          break;
        case 18: 
          if (this.zzbwX == null) {
            this.zzbwX = new zzauw.zzf();
          }
          paramzzbyb.zza(this.zzbwX);
          break;
        case 26: 
          if (this.zzbwY == null) {
            this.zzbwY = new zzauw.zzf();
          }
          paramzzbyb.zza(this.zzbwY);
          break;
        case 32: 
          this.zzbwZ = Boolean.valueOf(paramzzbyb.zzafc());
        }
      }
    }
    
    public void zza(zzbyc paramzzbyc)
      throws IOException
    {
      if (this.zzbwn != null) {
        paramzzbyc.zzJ(1, this.zzbwn.intValue());
      }
      if (this.zzbwX != null) {
        paramzzbyc.zza(2, this.zzbwX);
      }
      if (this.zzbwY != null) {
        paramzzbyc.zza(3, this.zzbwY);
      }
      if (this.zzbwZ != null) {
        paramzzbyc.zzg(4, this.zzbwZ.booleanValue());
      }
      super.zza(paramzzbyc);
    }
    
    protected int zzu()
    {
      int j = super.zzu();
      int i = j;
      if (this.zzbwn != null) {
        i = j + zzbyc.zzL(1, this.zzbwn.intValue());
      }
      j = i;
      if (this.zzbwX != null) {
        j = i + zzbyc.zzc(2, this.zzbwX);
      }
      i = j;
      if (this.zzbwY != null) {
        i = j + zzbyc.zzc(3, this.zzbwY);
      }
      j = i;
      if (this.zzbwZ != null) {
        j = i + zzbyc.zzh(4, this.zzbwZ.booleanValue());
      }
      return j;
    }
  }
  
  public static final class zzb
    extends zzbyd<zzb>
  {
    private static volatile zzb[] zzbxa;
    public Integer count;
    public String name;
    public zzauw.zzc[] zzbxb;
    public Long zzbxc;
    public Long zzbxd;
    
    public zzb()
    {
      zzND();
    }
    
    public static zzb[] zzNC()
    {
      if (zzbxa == null) {}
      synchronized (zzbyh.zzcwK)
      {
        if (zzbxa == null) {
          zzbxa = new zzb[0];
        }
        return zzbxa;
      }
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      boolean bool1;
      if (paramObject == this) {
        bool1 = true;
      }
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
                } while (!(paramObject instanceof zzb));
                paramObject = (zzb)paramObject;
                bool1 = bool2;
              } while (!zzbyh.equals(this.zzbxb, ((zzb)paramObject).zzbxb));
              if (this.name != null) {
                break;
              }
              bool1 = bool2;
            } while (((zzb)paramObject).name != null);
            if (this.zzbxc != null) {
              break label159;
            }
            bool1 = bool2;
          } while (((zzb)paramObject).zzbxc != null);
          if (this.zzbxd != null) {
            break label175;
          }
          bool1 = bool2;
        } while (((zzb)paramObject).zzbxd != null);
        if (this.count != null) {
          break label191;
        }
        bool1 = bool2;
      } while (((zzb)paramObject).count != null);
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
          if (this.name.equals(((zzb)paramObject).name)) {
            break label57;
          }
          return false;
          label159:
          if (this.zzbxc.equals(((zzb)paramObject).zzbxc)) {
            break label73;
          }
          return false;
          label175:
          if (this.zzbxd.equals(((zzb)paramObject).zzbxd)) {
            break label89;
          }
          return false;
          label191:
          if (!this.count.equals(((zzb)paramObject).count)) {
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
      int i3 = zzbyh.hashCode(this.zzbxb);
      int i;
      int j;
      label42:
      int k;
      label51:
      int m;
      if (this.name == null)
      {
        i = 0;
        if (this.zzbxc != null) {
          break label137;
        }
        j = 0;
        if (this.zzbxd != null) {
          break label148;
        }
        k = 0;
        if (this.count != null) {
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
        return (m + (k + (j + (i + ((i2 + 527) * 31 + i3) * 31) * 31) * 31) * 31) * 31 + n;
        i = this.name.hashCode();
        break;
        j = this.zzbxc.hashCode();
        break label42;
        k = this.zzbxd.hashCode();
        break label51;
        m = this.count.hashCode();
        break label61;
      }
    }
    
    public zzb zzND()
    {
      this.zzbxb = zzauw.zzc.zzNE();
      this.name = null;
      this.zzbxc = null;
      this.zzbxd = null;
      this.count = null;
      this.zzcwC = null;
      this.zzcwL = -1;
      return this;
    }
    
    public zzb zzQ(zzbyb paramzzbyb)
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
          int j = zzbym.zzb(paramzzbyb, 10);
          if (this.zzbxb == null) {}
          zzauw.zzc[] arrayOfzzc;
          for (i = 0;; i = this.zzbxb.length)
          {
            arrayOfzzc = new zzauw.zzc[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzbxb, 0, arrayOfzzc, 0, i);
              j = i;
            }
            while (j < arrayOfzzc.length - 1)
            {
              arrayOfzzc[j] = new zzauw.zzc();
              paramzzbyb.zza(arrayOfzzc[j]);
              paramzzbyb.zzaeW();
              j += 1;
            }
          }
          arrayOfzzc[j] = new zzauw.zzc();
          paramzzbyb.zza(arrayOfzzc[j]);
          this.zzbxb = arrayOfzzc;
          break;
        case 18: 
          this.name = paramzzbyb.readString();
          break;
        case 24: 
          this.zzbxc = Long.valueOf(paramzzbyb.zzaeZ());
          break;
        case 32: 
          this.zzbxd = Long.valueOf(paramzzbyb.zzaeZ());
          break;
        case 40: 
          this.count = Integer.valueOf(paramzzbyb.zzafa());
        }
      }
    }
    
    public void zza(zzbyc paramzzbyc)
      throws IOException
    {
      if ((this.zzbxb != null) && (this.zzbxb.length > 0))
      {
        int i = 0;
        while (i < this.zzbxb.length)
        {
          zzauw.zzc localzzc = this.zzbxb[i];
          if (localzzc != null) {
            paramzzbyc.zza(1, localzzc);
          }
          i += 1;
        }
      }
      if (this.name != null) {
        paramzzbyc.zzq(2, this.name);
      }
      if (this.zzbxc != null) {
        paramzzbyc.zzb(3, this.zzbxc.longValue());
      }
      if (this.zzbxd != null) {
        paramzzbyc.zzb(4, this.zzbxd.longValue());
      }
      if (this.count != null) {
        paramzzbyc.zzJ(5, this.count.intValue());
      }
      super.zza(paramzzbyc);
    }
    
    protected int zzu()
    {
      int i = super.zzu();
      int j = i;
      if (this.zzbxb != null)
      {
        j = i;
        if (this.zzbxb.length > 0)
        {
          int k = 0;
          for (;;)
          {
            j = i;
            if (k >= this.zzbxb.length) {
              break;
            }
            zzauw.zzc localzzc = this.zzbxb[k];
            j = i;
            if (localzzc != null) {
              j = i + zzbyc.zzc(1, localzzc);
            }
            k += 1;
            i = j;
          }
        }
      }
      i = j;
      if (this.name != null) {
        i = j + zzbyc.zzr(2, this.name);
      }
      j = i;
      if (this.zzbxc != null) {
        j = i + zzbyc.zzf(3, this.zzbxc.longValue());
      }
      i = j;
      if (this.zzbxd != null) {
        i = j + zzbyc.zzf(4, this.zzbxd.longValue());
      }
      j = i;
      if (this.count != null) {
        j = i + zzbyc.zzL(5, this.count.intValue());
      }
      return j;
    }
  }
  
  public static final class zzc
    extends zzbyd<zzc>
  {
    private static volatile zzc[] zzbxe;
    public String name;
    public String zzaGV;
    public Float zzbwh;
    public Double zzbwi;
    public Long zzbxf;
    
    public zzc()
    {
      zzNF();
    }
    
    public static zzc[] zzNE()
    {
      if (zzbxe == null) {}
      synchronized (zzbyh.zzcwK)
      {
        if (zzbxe == null) {
          zzbxe = new zzc[0];
        }
        return zzbxe;
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
                } while (!(paramObject instanceof zzc));
                paramObject = (zzc)paramObject;
                if (this.name != null) {
                  break;
                }
                bool1 = bool2;
              } while (((zzc)paramObject).name != null);
              if (this.zzaGV != null) {
                break label159;
              }
              bool1 = bool2;
            } while (((zzc)paramObject).zzaGV != null);
            if (this.zzbxf != null) {
              break label175;
            }
            bool1 = bool2;
          } while (((zzc)paramObject).zzbxf != null);
          if (this.zzbwh != null) {
            break label191;
          }
          bool1 = bool2;
        } while (((zzc)paramObject).zzbwh != null);
        if (this.zzbwi != null) {
          break label207;
        }
        bool1 = bool2;
      } while (((zzc)paramObject).zzbwi != null);
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
          if (this.name.equals(((zzc)paramObject).name)) {
            break label41;
          }
          return false;
          label159:
          if (this.zzaGV.equals(((zzc)paramObject).zzaGV)) {
            break label57;
          }
          return false;
          label175:
          if (this.zzbxf.equals(((zzc)paramObject).zzbxf)) {
            break label73;
          }
          return false;
          label191:
          if (this.zzbwh.equals(((zzc)paramObject).zzbwh)) {
            break label89;
          }
          return false;
          label207:
          if (!this.zzbwi.equals(((zzc)paramObject).zzbwi)) {
            return false;
          }
        }
      }
      return this.zzcwC.equals(((zzc)paramObject).zzcwC);
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
      if (this.name == null)
      {
        i = 0;
        if (this.zzaGV != null) {
          break label138;
        }
        j = 0;
        if (this.zzbxf != null) {
          break label149;
        }
        k = 0;
        if (this.zzbwh != null) {
          break label160;
        }
        m = 0;
        if (this.zzbwi != null) {
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
        i = this.name.hashCode();
        break;
        j = this.zzaGV.hashCode();
        break label33;
        k = this.zzbxf.hashCode();
        break label42;
        m = this.zzbwh.hashCode();
        break label52;
        n = this.zzbwi.hashCode();
        break label62;
      }
    }
    
    public zzc zzNF()
    {
      this.name = null;
      this.zzaGV = null;
      this.zzbxf = null;
      this.zzbwh = null;
      this.zzbwi = null;
      this.zzcwC = null;
      this.zzcwL = -1;
      return this;
    }
    
    public zzc zzR(zzbyb paramzzbyb)
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
          this.name = paramzzbyb.readString();
          break;
        case 18: 
          this.zzaGV = paramzzbyb.readString();
          break;
        case 24: 
          this.zzbxf = Long.valueOf(paramzzbyb.zzaeZ());
          break;
        case 37: 
          this.zzbwh = Float.valueOf(paramzzbyb.readFloat());
          break;
        case 41: 
          this.zzbwi = Double.valueOf(paramzzbyb.readDouble());
        }
      }
    }
    
    public void zza(zzbyc paramzzbyc)
      throws IOException
    {
      if (this.name != null) {
        paramzzbyc.zzq(1, this.name);
      }
      if (this.zzaGV != null) {
        paramzzbyc.zzq(2, this.zzaGV);
      }
      if (this.zzbxf != null) {
        paramzzbyc.zzb(3, this.zzbxf.longValue());
      }
      if (this.zzbwh != null) {
        paramzzbyc.zzc(4, this.zzbwh.floatValue());
      }
      if (this.zzbwi != null) {
        paramzzbyc.zza(5, this.zzbwi.doubleValue());
      }
      super.zza(paramzzbyc);
    }
    
    protected int zzu()
    {
      int j = super.zzu();
      int i = j;
      if (this.name != null) {
        i = j + zzbyc.zzr(1, this.name);
      }
      j = i;
      if (this.zzaGV != null) {
        j = i + zzbyc.zzr(2, this.zzaGV);
      }
      i = j;
      if (this.zzbxf != null) {
        i = j + zzbyc.zzf(3, this.zzbxf.longValue());
      }
      j = i;
      if (this.zzbwh != null) {
        j = i + zzbyc.zzd(4, this.zzbwh.floatValue());
      }
      i = j;
      if (this.zzbwi != null) {
        i = j + zzbyc.zzb(5, this.zzbwi.doubleValue());
      }
      return i;
    }
  }
  
  public static final class zzd
    extends zzbyd<zzd>
  {
    public zzauw.zze[] zzbxg;
    
    public zzd()
    {
      zzNG();
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
          do
          {
            return bool1;
            bool1 = bool2;
          } while (!(paramObject instanceof zzd));
          paramObject = (zzd)paramObject;
          bool1 = bool2;
        } while (!zzbyh.equals(this.zzbxg, ((zzd)paramObject).zzbxg));
        if ((this.zzcwC != null) && (!this.zzcwC.isEmpty())) {
          break label79;
        }
        if (((zzd)paramObject).zzcwC == null) {
          break;
        }
        bool1 = bool2;
      } while (!((zzd)paramObject).zzcwC.isEmpty());
      return true;
      label79:
      return this.zzcwC.equals(((zzd)paramObject).zzcwC);
    }
    
    public int hashCode()
    {
      int j = getClass().getName().hashCode();
      int k = zzbyh.hashCode(this.zzbxg);
      if ((this.zzcwC == null) || (this.zzcwC.isEmpty())) {}
      for (int i = 0;; i = this.zzcwC.hashCode()) {
        return i + ((j + 527) * 31 + k) * 31;
      }
    }
    
    public zzd zzNG()
    {
      this.zzbxg = zzauw.zze.zzNH();
      this.zzcwC = null;
      this.zzcwL = -1;
      return this;
    }
    
    public zzd zzS(zzbyb paramzzbyb)
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
          int j = zzbym.zzb(paramzzbyb, 10);
          if (this.zzbxg == null) {}
          zzauw.zze[] arrayOfzze;
          for (i = 0;; i = this.zzbxg.length)
          {
            arrayOfzze = new zzauw.zze[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzbxg, 0, arrayOfzze, 0, i);
              j = i;
            }
            while (j < arrayOfzze.length - 1)
            {
              arrayOfzze[j] = new zzauw.zze();
              paramzzbyb.zza(arrayOfzze[j]);
              paramzzbyb.zzaeW();
              j += 1;
            }
          }
          arrayOfzze[j] = new zzauw.zze();
          paramzzbyb.zza(arrayOfzze[j]);
          this.zzbxg = arrayOfzze;
        }
      }
    }
    
    public void zza(zzbyc paramzzbyc)
      throws IOException
    {
      if ((this.zzbxg != null) && (this.zzbxg.length > 0))
      {
        int i = 0;
        while (i < this.zzbxg.length)
        {
          zzauw.zze localzze = this.zzbxg[i];
          if (localzze != null) {
            paramzzbyc.zza(1, localzze);
          }
          i += 1;
        }
      }
      super.zza(paramzzbyc);
    }
    
    protected int zzu()
    {
      int i = super.zzu();
      int k = i;
      if (this.zzbxg != null)
      {
        k = i;
        if (this.zzbxg.length > 0)
        {
          int j = 0;
          for (;;)
          {
            k = i;
            if (j >= this.zzbxg.length) {
              break;
            }
            zzauw.zze localzze = this.zzbxg[j];
            k = i;
            if (localzze != null) {
              k = i + zzbyc.zzc(1, localzze);
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
    extends zzbyd<zze>
  {
    private static volatile zze[] zzbxh;
    public String zzaS;
    public String zzbb;
    public String zzbhN;
    public String zzbqK;
    public String zzbqL;
    public String zzbqO;
    public String zzbqS;
    public Integer zzbxA;
    public Boolean zzbxB;
    public zzauw.zza[] zzbxC;
    public Integer zzbxD;
    public Integer zzbxE;
    public Integer zzbxF;
    public String zzbxG;
    public Long zzbxH;
    public Long zzbxI;
    public Integer zzbxi;
    public zzauw.zzb[] zzbxj;
    public zzauw.zzg[] zzbxk;
    public Long zzbxl;
    public Long zzbxm;
    public Long zzbxn;
    public Long zzbxo;
    public Long zzbxp;
    public String zzbxq;
    public String zzbxr;
    public String zzbxs;
    public Integer zzbxt;
    public Long zzbxu;
    public Long zzbxv;
    public String zzbxw;
    public Boolean zzbxx;
    public String zzbxy;
    public Long zzbxz;
    
    public zze()
    {
      zzNI();
    }
    
    public static zze[] zzNH()
    {
      if (zzbxh == null) {}
      synchronized (zzbyh.zzcwK)
      {
        if (zzbxh == null) {
          zzbxh = new zze[0];
        }
        return zzbxh;
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
      label89:
      label105:
      label121:
      label137:
      label153:
      label169:
      label185:
      label201:
      label217:
      label233:
      label249:
      label265:
      label281:
      label297:
      label313:
      label329:
      label345:
      label361:
      label377:
      label393:
      label409:
      label425:
      label441:
      label473:
      label489:
      label505:
      label521:
      label537:
      label553:
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
                                                                          } while (!(paramObject instanceof zze));
                                                                          paramObject = (zze)paramObject;
                                                                          if (this.zzbxi != null) {
                                                                            break;
                                                                          }
                                                                          bool1 = bool2;
                                                                        } while (((zze)paramObject).zzbxi != null);
                                                                        bool1 = bool2;
                                                                      } while (!zzbyh.equals(this.zzbxj, ((zze)paramObject).zzbxj));
                                                                      bool1 = bool2;
                                                                    } while (!zzbyh.equals(this.zzbxk, ((zze)paramObject).zzbxk));
                                                                    if (this.zzbxl != null) {
                                                                      break label623;
                                                                    }
                                                                    bool1 = bool2;
                                                                  } while (((zze)paramObject).zzbxl != null);
                                                                  if (this.zzbxm != null) {
                                                                    break label639;
                                                                  }
                                                                  bool1 = bool2;
                                                                } while (((zze)paramObject).zzbxm != null);
                                                                if (this.zzbxn != null) {
                                                                  break label655;
                                                                }
                                                                bool1 = bool2;
                                                              } while (((zze)paramObject).zzbxn != null);
                                                              if (this.zzbxo != null) {
                                                                break label671;
                                                              }
                                                              bool1 = bool2;
                                                            } while (((zze)paramObject).zzbxo != null);
                                                            if (this.zzbxp != null) {
                                                              break label687;
                                                            }
                                                            bool1 = bool2;
                                                          } while (((zze)paramObject).zzbxp != null);
                                                          if (this.zzbxq != null) {
                                                            break label703;
                                                          }
                                                          bool1 = bool2;
                                                        } while (((zze)paramObject).zzbxq != null);
                                                        if (this.zzbb != null) {
                                                          break label719;
                                                        }
                                                        bool1 = bool2;
                                                      } while (((zze)paramObject).zzbb != null);
                                                      if (this.zzbxr != null) {
                                                        break label735;
                                                      }
                                                      bool1 = bool2;
                                                    } while (((zze)paramObject).zzbxr != null);
                                                    if (this.zzbxs != null) {
                                                      break label751;
                                                    }
                                                    bool1 = bool2;
                                                  } while (((zze)paramObject).zzbxs != null);
                                                  if (this.zzbxt != null) {
                                                    break label767;
                                                  }
                                                  bool1 = bool2;
                                                } while (((zze)paramObject).zzbxt != null);
                                                if (this.zzbqL != null) {
                                                  break label783;
                                                }
                                                bool1 = bool2;
                                              } while (((zze)paramObject).zzbqL != null);
                                              if (this.zzaS != null) {
                                                break label799;
                                              }
                                              bool1 = bool2;
                                            } while (((zze)paramObject).zzaS != null);
                                            if (this.zzbhN != null) {
                                              break label815;
                                            }
                                            bool1 = bool2;
                                          } while (((zze)paramObject).zzbhN != null);
                                          if (this.zzbxu != null) {
                                            break label831;
                                          }
                                          bool1 = bool2;
                                        } while (((zze)paramObject).zzbxu != null);
                                        if (this.zzbxv != null) {
                                          break label847;
                                        }
                                        bool1 = bool2;
                                      } while (((zze)paramObject).zzbxv != null);
                                      if (this.zzbxw != null) {
                                        break label863;
                                      }
                                      bool1 = bool2;
                                    } while (((zze)paramObject).zzbxw != null);
                                    if (this.zzbxx != null) {
                                      break label879;
                                    }
                                    bool1 = bool2;
                                  } while (((zze)paramObject).zzbxx != null);
                                  if (this.zzbxy != null) {
                                    break label895;
                                  }
                                  bool1 = bool2;
                                } while (((zze)paramObject).zzbxy != null);
                                if (this.zzbxz != null) {
                                  break label911;
                                }
                                bool1 = bool2;
                              } while (((zze)paramObject).zzbxz != null);
                              if (this.zzbxA != null) {
                                break label927;
                              }
                              bool1 = bool2;
                            } while (((zze)paramObject).zzbxA != null);
                            if (this.zzbqO != null) {
                              break label943;
                            }
                            bool1 = bool2;
                          } while (((zze)paramObject).zzbqO != null);
                          if (this.zzbqK != null) {
                            break label959;
                          }
                          bool1 = bool2;
                        } while (((zze)paramObject).zzbqK != null);
                        if (this.zzbxB != null) {
                          break label975;
                        }
                        bool1 = bool2;
                      } while (((zze)paramObject).zzbxB != null);
                      bool1 = bool2;
                    } while (!zzbyh.equals(this.zzbxC, ((zze)paramObject).zzbxC));
                    if (this.zzbqS != null) {
                      break label991;
                    }
                    bool1 = bool2;
                  } while (((zze)paramObject).zzbqS != null);
                  if (this.zzbxD != null) {
                    break label1007;
                  }
                  bool1 = bool2;
                } while (((zze)paramObject).zzbxD != null);
                if (this.zzbxE != null) {
                  break label1023;
                }
                bool1 = bool2;
              } while (((zze)paramObject).zzbxE != null);
              if (this.zzbxF != null) {
                break label1039;
              }
              bool1 = bool2;
            } while (((zze)paramObject).zzbxF != null);
            if (this.zzbxG != null) {
              break label1055;
            }
            bool1 = bool2;
          } while (((zze)paramObject).zzbxG != null);
          if (this.zzbxH != null) {
            break label1071;
          }
          bool1 = bool2;
        } while (((zze)paramObject).zzbxH != null);
        if (this.zzbxI != null) {
          break label1087;
        }
        bool1 = bool2;
      } while (((zze)paramObject).zzbxI != null);
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
          if (this.zzbxi.equals(((zze)paramObject).zzbxi)) {
            break label41;
          }
          return false;
          label623:
          if (this.zzbxl.equals(((zze)paramObject).zzbxl)) {
            break label89;
          }
          return false;
          label639:
          if (this.zzbxm.equals(((zze)paramObject).zzbxm)) {
            break label105;
          }
          return false;
          label655:
          if (this.zzbxn.equals(((zze)paramObject).zzbxn)) {
            break label121;
          }
          return false;
          label671:
          if (this.zzbxo.equals(((zze)paramObject).zzbxo)) {
            break label137;
          }
          return false;
          label687:
          if (this.zzbxp.equals(((zze)paramObject).zzbxp)) {
            break label153;
          }
          return false;
          label703:
          if (this.zzbxq.equals(((zze)paramObject).zzbxq)) {
            break label169;
          }
          return false;
          label719:
          if (this.zzbb.equals(((zze)paramObject).zzbb)) {
            break label185;
          }
          return false;
          label735:
          if (this.zzbxr.equals(((zze)paramObject).zzbxr)) {
            break label201;
          }
          return false;
          label751:
          if (this.zzbxs.equals(((zze)paramObject).zzbxs)) {
            break label217;
          }
          return false;
          label767:
          if (this.zzbxt.equals(((zze)paramObject).zzbxt)) {
            break label233;
          }
          return false;
          label783:
          if (this.zzbqL.equals(((zze)paramObject).zzbqL)) {
            break label249;
          }
          return false;
          label799:
          if (this.zzaS.equals(((zze)paramObject).zzaS)) {
            break label265;
          }
          return false;
          label815:
          if (this.zzbhN.equals(((zze)paramObject).zzbhN)) {
            break label281;
          }
          return false;
          label831:
          if (this.zzbxu.equals(((zze)paramObject).zzbxu)) {
            break label297;
          }
          return false;
          label847:
          if (this.zzbxv.equals(((zze)paramObject).zzbxv)) {
            break label313;
          }
          return false;
          label863:
          if (this.zzbxw.equals(((zze)paramObject).zzbxw)) {
            break label329;
          }
          return false;
          label879:
          if (this.zzbxx.equals(((zze)paramObject).zzbxx)) {
            break label345;
          }
          return false;
          label895:
          if (this.zzbxy.equals(((zze)paramObject).zzbxy)) {
            break label361;
          }
          return false;
          label911:
          if (this.zzbxz.equals(((zze)paramObject).zzbxz)) {
            break label377;
          }
          return false;
          label927:
          if (this.zzbxA.equals(((zze)paramObject).zzbxA)) {
            break label393;
          }
          return false;
          label943:
          if (this.zzbqO.equals(((zze)paramObject).zzbqO)) {
            break label409;
          }
          return false;
          label959:
          if (this.zzbqK.equals(((zze)paramObject).zzbqK)) {
            break label425;
          }
          return false;
          label975:
          if (this.zzbxB.equals(((zze)paramObject).zzbxB)) {
            break label441;
          }
          return false;
          label991:
          if (this.zzbqS.equals(((zze)paramObject).zzbqS)) {
            break label473;
          }
          return false;
          label1007:
          if (this.zzbxD.equals(((zze)paramObject).zzbxD)) {
            break label489;
          }
          return false;
          label1023:
          if (this.zzbxE.equals(((zze)paramObject).zzbxE)) {
            break label505;
          }
          return false;
          label1039:
          if (this.zzbxF.equals(((zze)paramObject).zzbxF)) {
            break label521;
          }
          return false;
          label1055:
          if (this.zzbxG.equals(((zze)paramObject).zzbxG)) {
            break label537;
          }
          return false;
          label1071:
          if (this.zzbxH.equals(((zze)paramObject).zzbxH)) {
            break label553;
          }
          return false;
          label1087:
          if (!this.zzbxI.equals(((zze)paramObject).zzbxI)) {
            return false;
          }
        }
      }
      return this.zzcwC.equals(((zze)paramObject).zzcwC);
    }
    
    public int hashCode()
    {
      int i28 = 0;
      int i29 = getClass().getName().hashCode();
      int i;
      int i30;
      int i31;
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
      int i32;
      int i20;
      label289:
      int i21;
      label299:
      int i22;
      label309:
      int i23;
      label319:
      int i24;
      label329:
      int i25;
      label339:
      int i26;
      if (this.zzbxi == null)
      {
        i = 0;
        i30 = zzbyh.hashCode(this.zzbxj);
        i31 = zzbyh.hashCode(this.zzbxk);
        if (this.zzbxl != null) {
          break label599;
        }
        j = 0;
        if (this.zzbxm != null) {
          break label610;
        }
        k = 0;
        if (this.zzbxn != null) {
          break label621;
        }
        m = 0;
        if (this.zzbxo != null) {
          break label633;
        }
        n = 0;
        if (this.zzbxp != null) {
          break label645;
        }
        i1 = 0;
        if (this.zzbxq != null) {
          break label657;
        }
        i2 = 0;
        if (this.zzbb != null) {
          break label669;
        }
        i3 = 0;
        if (this.zzbxr != null) {
          break label681;
        }
        i4 = 0;
        if (this.zzbxs != null) {
          break label693;
        }
        i5 = 0;
        if (this.zzbxt != null) {
          break label705;
        }
        i6 = 0;
        if (this.zzbqL != null) {
          break label717;
        }
        i7 = 0;
        if (this.zzaS != null) {
          break label729;
        }
        i8 = 0;
        if (this.zzbhN != null) {
          break label741;
        }
        i9 = 0;
        if (this.zzbxu != null) {
          break label753;
        }
        i10 = 0;
        if (this.zzbxv != null) {
          break label765;
        }
        i11 = 0;
        if (this.zzbxw != null) {
          break label777;
        }
        i12 = 0;
        if (this.zzbxx != null) {
          break label789;
        }
        i13 = 0;
        if (this.zzbxy != null) {
          break label801;
        }
        i14 = 0;
        if (this.zzbxz != null) {
          break label813;
        }
        i15 = 0;
        if (this.zzbxA != null) {
          break label825;
        }
        i16 = 0;
        if (this.zzbqO != null) {
          break label837;
        }
        i17 = 0;
        if (this.zzbqK != null) {
          break label849;
        }
        i18 = 0;
        if (this.zzbxB != null) {
          break label861;
        }
        i19 = 0;
        i32 = zzbyh.hashCode(this.zzbxC);
        if (this.zzbqS != null) {
          break label873;
        }
        i20 = 0;
        if (this.zzbxD != null) {
          break label885;
        }
        i21 = 0;
        if (this.zzbxE != null) {
          break label897;
        }
        i22 = 0;
        if (this.zzbxF != null) {
          break label909;
        }
        i23 = 0;
        if (this.zzbxG != null) {
          break label921;
        }
        i24 = 0;
        if (this.zzbxH != null) {
          break label933;
        }
        i25 = 0;
        if (this.zzbxI != null) {
          break label945;
        }
        i26 = 0;
        label349:
        i27 = i28;
        if (this.zzcwC != null) {
          if (!this.zzcwC.isEmpty()) {
            break label957;
          }
        }
      }
      label599:
      label610:
      label621:
      label633:
      label645:
      label657:
      label669:
      label681:
      label693:
      label705:
      label717:
      label729:
      label741:
      label753:
      label765:
      label777:
      label789:
      label801:
      label813:
      label825:
      label837:
      label849:
      label861:
      label873:
      label885:
      label897:
      label909:
      label921:
      label933:
      label945:
      label957:
      for (int i27 = i28;; i27 = this.zzcwC.hashCode())
      {
        return (i26 + (i25 + (i24 + (i23 + (i22 + (i21 + (i20 + ((i19 + (i18 + (i17 + (i16 + (i15 + (i14 + (i13 + (i12 + (i11 + (i10 + (i9 + (i8 + (i7 + (i6 + (i5 + (i4 + (i3 + (i2 + (i1 + (n + (m + (k + (j + (((i + (i29 + 527) * 31) * 31 + i30) * 31 + i31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31 + i32) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31 + i27;
        i = this.zzbxi.hashCode();
        break;
        j = this.zzbxl.hashCode();
        break label51;
        k = this.zzbxm.hashCode();
        break label60;
        m = this.zzbxn.hashCode();
        break label70;
        n = this.zzbxo.hashCode();
        break label80;
        i1 = this.zzbxp.hashCode();
        break label90;
        i2 = this.zzbxq.hashCode();
        break label100;
        i3 = this.zzbb.hashCode();
        break label110;
        i4 = this.zzbxr.hashCode();
        break label120;
        i5 = this.zzbxs.hashCode();
        break label130;
        i6 = this.zzbxt.hashCode();
        break label140;
        i7 = this.zzbqL.hashCode();
        break label150;
        i8 = this.zzaS.hashCode();
        break label160;
        i9 = this.zzbhN.hashCode();
        break label170;
        i10 = this.zzbxu.hashCode();
        break label180;
        i11 = this.zzbxv.hashCode();
        break label190;
        i12 = this.zzbxw.hashCode();
        break label200;
        i13 = this.zzbxx.hashCode();
        break label210;
        i14 = this.zzbxy.hashCode();
        break label220;
        i15 = this.zzbxz.hashCode();
        break label230;
        i16 = this.zzbxA.hashCode();
        break label240;
        i17 = this.zzbqO.hashCode();
        break label250;
        i18 = this.zzbqK.hashCode();
        break label260;
        i19 = this.zzbxB.hashCode();
        break label270;
        i20 = this.zzbqS.hashCode();
        break label289;
        i21 = this.zzbxD.hashCode();
        break label299;
        i22 = this.zzbxE.hashCode();
        break label309;
        i23 = this.zzbxF.hashCode();
        break label319;
        i24 = this.zzbxG.hashCode();
        break label329;
        i25 = this.zzbxH.hashCode();
        break label339;
        i26 = this.zzbxI.hashCode();
        break label349;
      }
    }
    
    public zze zzNI()
    {
      this.zzbxi = null;
      this.zzbxj = zzauw.zzb.zzNC();
      this.zzbxk = zzauw.zzg.zzNK();
      this.zzbxl = null;
      this.zzbxm = null;
      this.zzbxn = null;
      this.zzbxo = null;
      this.zzbxp = null;
      this.zzbxq = null;
      this.zzbb = null;
      this.zzbxr = null;
      this.zzbxs = null;
      this.zzbxt = null;
      this.zzbqL = null;
      this.zzaS = null;
      this.zzbhN = null;
      this.zzbxu = null;
      this.zzbxv = null;
      this.zzbxw = null;
      this.zzbxx = null;
      this.zzbxy = null;
      this.zzbxz = null;
      this.zzbxA = null;
      this.zzbqO = null;
      this.zzbqK = null;
      this.zzbxB = null;
      this.zzbxC = zzauw.zza.zzNA();
      this.zzbqS = null;
      this.zzbxD = null;
      this.zzbxE = null;
      this.zzbxF = null;
      this.zzbxG = null;
      this.zzbxH = null;
      this.zzbxI = null;
      this.zzcwC = null;
      this.zzcwL = -1;
      return this;
    }
    
    public zze zzT(zzbyb paramzzbyb)
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
          this.zzbxi = Integer.valueOf(paramzzbyb.zzafa());
          break;
        case 18: 
          j = zzbym.zzb(paramzzbyb, 18);
          if (this.zzbxj == null) {}
          for (i = 0;; i = this.zzbxj.length)
          {
            localObject = new zzauw.zzb[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzbxj, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length - 1)
            {
              localObject[j] = new zzauw.zzb();
              paramzzbyb.zza(localObject[j]);
              paramzzbyb.zzaeW();
              j += 1;
            }
          }
          localObject[j] = new zzauw.zzb();
          paramzzbyb.zza(localObject[j]);
          this.zzbxj = ((zzauw.zzb[])localObject);
          break;
        case 26: 
          j = zzbym.zzb(paramzzbyb, 26);
          if (this.zzbxk == null) {}
          for (i = 0;; i = this.zzbxk.length)
          {
            localObject = new zzauw.zzg[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzbxk, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length - 1)
            {
              localObject[j] = new zzauw.zzg();
              paramzzbyb.zza(localObject[j]);
              paramzzbyb.zzaeW();
              j += 1;
            }
          }
          localObject[j] = new zzauw.zzg();
          paramzzbyb.zza(localObject[j]);
          this.zzbxk = ((zzauw.zzg[])localObject);
          break;
        case 32: 
          this.zzbxl = Long.valueOf(paramzzbyb.zzaeZ());
          break;
        case 40: 
          this.zzbxm = Long.valueOf(paramzzbyb.zzaeZ());
          break;
        case 48: 
          this.zzbxn = Long.valueOf(paramzzbyb.zzaeZ());
          break;
        case 56: 
          this.zzbxp = Long.valueOf(paramzzbyb.zzaeZ());
          break;
        case 66: 
          this.zzbxq = paramzzbyb.readString();
          break;
        case 74: 
          this.zzbb = paramzzbyb.readString();
          break;
        case 82: 
          this.zzbxr = paramzzbyb.readString();
          break;
        case 90: 
          this.zzbxs = paramzzbyb.readString();
          break;
        case 96: 
          this.zzbxt = Integer.valueOf(paramzzbyb.zzafa());
          break;
        case 106: 
          this.zzbqL = paramzzbyb.readString();
          break;
        case 114: 
          this.zzaS = paramzzbyb.readString();
          break;
        case 130: 
          this.zzbhN = paramzzbyb.readString();
          break;
        case 136: 
          this.zzbxu = Long.valueOf(paramzzbyb.zzaeZ());
          break;
        case 144: 
          this.zzbxv = Long.valueOf(paramzzbyb.zzaeZ());
          break;
        case 154: 
          this.zzbxw = paramzzbyb.readString();
          break;
        case 160: 
          this.zzbxx = Boolean.valueOf(paramzzbyb.zzafc());
          break;
        case 170: 
          this.zzbxy = paramzzbyb.readString();
          break;
        case 176: 
          this.zzbxz = Long.valueOf(paramzzbyb.zzaeZ());
          break;
        case 184: 
          this.zzbxA = Integer.valueOf(paramzzbyb.zzafa());
          break;
        case 194: 
          this.zzbqO = paramzzbyb.readString();
          break;
        case 202: 
          this.zzbqK = paramzzbyb.readString();
          break;
        case 208: 
          this.zzbxo = Long.valueOf(paramzzbyb.zzaeZ());
          break;
        case 224: 
          this.zzbxB = Boolean.valueOf(paramzzbyb.zzafc());
          break;
        case 234: 
          j = zzbym.zzb(paramzzbyb, 234);
          if (this.zzbxC == null) {}
          for (i = 0;; i = this.zzbxC.length)
          {
            localObject = new zzauw.zza[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzbxC, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length - 1)
            {
              localObject[j] = new zzauw.zza();
              paramzzbyb.zza(localObject[j]);
              paramzzbyb.zzaeW();
              j += 1;
            }
          }
          localObject[j] = new zzauw.zza();
          paramzzbyb.zza(localObject[j]);
          this.zzbxC = ((zzauw.zza[])localObject);
          break;
        case 242: 
          this.zzbqS = paramzzbyb.readString();
          break;
        case 248: 
          this.zzbxD = Integer.valueOf(paramzzbyb.zzafa());
          break;
        case 256: 
          this.zzbxE = Integer.valueOf(paramzzbyb.zzafa());
          break;
        case 264: 
          this.zzbxF = Integer.valueOf(paramzzbyb.zzafa());
          break;
        case 274: 
          this.zzbxG = paramzzbyb.readString();
          break;
        case 280: 
          this.zzbxH = Long.valueOf(paramzzbyb.zzaeZ());
          break;
        case 288: 
          this.zzbxI = Long.valueOf(paramzzbyb.zzaeZ());
        }
      }
    }
    
    public void zza(zzbyc paramzzbyc)
      throws IOException
    {
      int j = 0;
      if (this.zzbxi != null) {
        paramzzbyc.zzJ(1, this.zzbxi.intValue());
      }
      int i;
      Object localObject;
      if ((this.zzbxj != null) && (this.zzbxj.length > 0))
      {
        i = 0;
        while (i < this.zzbxj.length)
        {
          localObject = this.zzbxj[i];
          if (localObject != null) {
            paramzzbyc.zza(2, (zzbyj)localObject);
          }
          i += 1;
        }
      }
      if ((this.zzbxk != null) && (this.zzbxk.length > 0))
      {
        i = 0;
        while (i < this.zzbxk.length)
        {
          localObject = this.zzbxk[i];
          if (localObject != null) {
            paramzzbyc.zza(3, (zzbyj)localObject);
          }
          i += 1;
        }
      }
      if (this.zzbxl != null) {
        paramzzbyc.zzb(4, this.zzbxl.longValue());
      }
      if (this.zzbxm != null) {
        paramzzbyc.zzb(5, this.zzbxm.longValue());
      }
      if (this.zzbxn != null) {
        paramzzbyc.zzb(6, this.zzbxn.longValue());
      }
      if (this.zzbxp != null) {
        paramzzbyc.zzb(7, this.zzbxp.longValue());
      }
      if (this.zzbxq != null) {
        paramzzbyc.zzq(8, this.zzbxq);
      }
      if (this.zzbb != null) {
        paramzzbyc.zzq(9, this.zzbb);
      }
      if (this.zzbxr != null) {
        paramzzbyc.zzq(10, this.zzbxr);
      }
      if (this.zzbxs != null) {
        paramzzbyc.zzq(11, this.zzbxs);
      }
      if (this.zzbxt != null) {
        paramzzbyc.zzJ(12, this.zzbxt.intValue());
      }
      if (this.zzbqL != null) {
        paramzzbyc.zzq(13, this.zzbqL);
      }
      if (this.zzaS != null) {
        paramzzbyc.zzq(14, this.zzaS);
      }
      if (this.zzbhN != null) {
        paramzzbyc.zzq(16, this.zzbhN);
      }
      if (this.zzbxu != null) {
        paramzzbyc.zzb(17, this.zzbxu.longValue());
      }
      if (this.zzbxv != null) {
        paramzzbyc.zzb(18, this.zzbxv.longValue());
      }
      if (this.zzbxw != null) {
        paramzzbyc.zzq(19, this.zzbxw);
      }
      if (this.zzbxx != null) {
        paramzzbyc.zzg(20, this.zzbxx.booleanValue());
      }
      if (this.zzbxy != null) {
        paramzzbyc.zzq(21, this.zzbxy);
      }
      if (this.zzbxz != null) {
        paramzzbyc.zzb(22, this.zzbxz.longValue());
      }
      if (this.zzbxA != null) {
        paramzzbyc.zzJ(23, this.zzbxA.intValue());
      }
      if (this.zzbqO != null) {
        paramzzbyc.zzq(24, this.zzbqO);
      }
      if (this.zzbqK != null) {
        paramzzbyc.zzq(25, this.zzbqK);
      }
      if (this.zzbxo != null) {
        paramzzbyc.zzb(26, this.zzbxo.longValue());
      }
      if (this.zzbxB != null) {
        paramzzbyc.zzg(28, this.zzbxB.booleanValue());
      }
      if ((this.zzbxC != null) && (this.zzbxC.length > 0))
      {
        i = j;
        while (i < this.zzbxC.length)
        {
          localObject = this.zzbxC[i];
          if (localObject != null) {
            paramzzbyc.zza(29, (zzbyj)localObject);
          }
          i += 1;
        }
      }
      if (this.zzbqS != null) {
        paramzzbyc.zzq(30, this.zzbqS);
      }
      if (this.zzbxD != null) {
        paramzzbyc.zzJ(31, this.zzbxD.intValue());
      }
      if (this.zzbxE != null) {
        paramzzbyc.zzJ(32, this.zzbxE.intValue());
      }
      if (this.zzbxF != null) {
        paramzzbyc.zzJ(33, this.zzbxF.intValue());
      }
      if (this.zzbxG != null) {
        paramzzbyc.zzq(34, this.zzbxG);
      }
      if (this.zzbxH != null) {
        paramzzbyc.zzb(35, this.zzbxH.longValue());
      }
      if (this.zzbxI != null) {
        paramzzbyc.zzb(36, this.zzbxI.longValue());
      }
      super.zza(paramzzbyc);
    }
    
    protected int zzu()
    {
      int m = 0;
      int j = super.zzu();
      int i = j;
      if (this.zzbxi != null) {
        i = j + zzbyc.zzL(1, this.zzbxi.intValue());
      }
      j = i;
      Object localObject;
      if (this.zzbxj != null)
      {
        j = i;
        if (this.zzbxj.length > 0)
        {
          j = 0;
          while (j < this.zzbxj.length)
          {
            localObject = this.zzbxj[j];
            k = i;
            if (localObject != null) {
              k = i + zzbyc.zzc(2, (zzbyj)localObject);
            }
            j += 1;
            i = k;
          }
          j = i;
        }
      }
      i = j;
      if (this.zzbxk != null)
      {
        i = j;
        if (this.zzbxk.length > 0)
        {
          i = j;
          j = 0;
          while (j < this.zzbxk.length)
          {
            localObject = this.zzbxk[j];
            k = i;
            if (localObject != null) {
              k = i + zzbyc.zzc(3, (zzbyj)localObject);
            }
            j += 1;
            i = k;
          }
        }
      }
      j = i;
      if (this.zzbxl != null) {
        j = i + zzbyc.zzf(4, this.zzbxl.longValue());
      }
      i = j;
      if (this.zzbxm != null) {
        i = j + zzbyc.zzf(5, this.zzbxm.longValue());
      }
      j = i;
      if (this.zzbxn != null) {
        j = i + zzbyc.zzf(6, this.zzbxn.longValue());
      }
      i = j;
      if (this.zzbxp != null) {
        i = j + zzbyc.zzf(7, this.zzbxp.longValue());
      }
      j = i;
      if (this.zzbxq != null) {
        j = i + zzbyc.zzr(8, this.zzbxq);
      }
      i = j;
      if (this.zzbb != null) {
        i = j + zzbyc.zzr(9, this.zzbb);
      }
      j = i;
      if (this.zzbxr != null) {
        j = i + zzbyc.zzr(10, this.zzbxr);
      }
      i = j;
      if (this.zzbxs != null) {
        i = j + zzbyc.zzr(11, this.zzbxs);
      }
      j = i;
      if (this.zzbxt != null) {
        j = i + zzbyc.zzL(12, this.zzbxt.intValue());
      }
      i = j;
      if (this.zzbqL != null) {
        i = j + zzbyc.zzr(13, this.zzbqL);
      }
      j = i;
      if (this.zzaS != null) {
        j = i + zzbyc.zzr(14, this.zzaS);
      }
      i = j;
      if (this.zzbhN != null) {
        i = j + zzbyc.zzr(16, this.zzbhN);
      }
      j = i;
      if (this.zzbxu != null) {
        j = i + zzbyc.zzf(17, this.zzbxu.longValue());
      }
      i = j;
      if (this.zzbxv != null) {
        i = j + zzbyc.zzf(18, this.zzbxv.longValue());
      }
      j = i;
      if (this.zzbxw != null) {
        j = i + zzbyc.zzr(19, this.zzbxw);
      }
      i = j;
      if (this.zzbxx != null) {
        i = j + zzbyc.zzh(20, this.zzbxx.booleanValue());
      }
      j = i;
      if (this.zzbxy != null) {
        j = i + zzbyc.zzr(21, this.zzbxy);
      }
      i = j;
      if (this.zzbxz != null) {
        i = j + zzbyc.zzf(22, this.zzbxz.longValue());
      }
      j = i;
      if (this.zzbxA != null) {
        j = i + zzbyc.zzL(23, this.zzbxA.intValue());
      }
      i = j;
      if (this.zzbqO != null) {
        i = j + zzbyc.zzr(24, this.zzbqO);
      }
      j = i;
      if (this.zzbqK != null) {
        j = i + zzbyc.zzr(25, this.zzbqK);
      }
      int k = j;
      if (this.zzbxo != null) {
        k = j + zzbyc.zzf(26, this.zzbxo.longValue());
      }
      i = k;
      if (this.zzbxB != null) {
        i = k + zzbyc.zzh(28, this.zzbxB.booleanValue());
      }
      j = i;
      if (this.zzbxC != null)
      {
        j = i;
        if (this.zzbxC.length > 0)
        {
          k = m;
          for (;;)
          {
            j = i;
            if (k >= this.zzbxC.length) {
              break;
            }
            localObject = this.zzbxC[k];
            j = i;
            if (localObject != null) {
              j = i + zzbyc.zzc(29, (zzbyj)localObject);
            }
            k += 1;
            i = j;
          }
        }
      }
      i = j;
      if (this.zzbqS != null) {
        i = j + zzbyc.zzr(30, this.zzbqS);
      }
      j = i;
      if (this.zzbxD != null) {
        j = i + zzbyc.zzL(31, this.zzbxD.intValue());
      }
      i = j;
      if (this.zzbxE != null) {
        i = j + zzbyc.zzL(32, this.zzbxE.intValue());
      }
      j = i;
      if (this.zzbxF != null) {
        j = i + zzbyc.zzL(33, this.zzbxF.intValue());
      }
      i = j;
      if (this.zzbxG != null) {
        i = j + zzbyc.zzr(34, this.zzbxG);
      }
      j = i;
      if (this.zzbxH != null) {
        j = i + zzbyc.zzf(35, this.zzbxH.longValue());
      }
      i = j;
      if (this.zzbxI != null) {
        i = j + zzbyc.zzf(36, this.zzbxI.longValue());
      }
      return i;
    }
  }
  
  public static final class zzf
    extends zzbyd<zzf>
  {
    public long[] zzbxJ;
    public long[] zzbxK;
    
    public zzf()
    {
      zzNJ();
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
          do
          {
            do
            {
              return bool1;
              bool1 = bool2;
            } while (!(paramObject instanceof zzf));
            paramObject = (zzf)paramObject;
            bool1 = bool2;
          } while (!zzbyh.equals(this.zzbxJ, ((zzf)paramObject).zzbxJ));
          bool1 = bool2;
        } while (!zzbyh.equals(this.zzbxK, ((zzf)paramObject).zzbxK));
        if ((this.zzcwC != null) && (!this.zzcwC.isEmpty())) {
          break label95;
        }
        if (((zzf)paramObject).zzcwC == null) {
          break;
        }
        bool1 = bool2;
      } while (!((zzf)paramObject).zzcwC.isEmpty());
      return true;
      label95:
      return this.zzcwC.equals(((zzf)paramObject).zzcwC);
    }
    
    public int hashCode()
    {
      int j = getClass().getName().hashCode();
      int k = zzbyh.hashCode(this.zzbxJ);
      int m = zzbyh.hashCode(this.zzbxK);
      if ((this.zzcwC == null) || (this.zzcwC.isEmpty())) {}
      for (int i = 0;; i = this.zzcwC.hashCode()) {
        return i + (((j + 527) * 31 + k) * 31 + m) * 31;
      }
    }
    
    public zzf zzNJ()
    {
      this.zzbxJ = zzbym.zzcwR;
      this.zzbxK = zzbym.zzcwR;
      this.zzcwC = null;
      this.zzcwL = -1;
      return this;
    }
    
    public zzf zzU(zzbyb paramzzbyb)
      throws IOException
    {
      for (;;)
      {
        int i = paramzzbyb.zzaeW();
        int j;
        long[] arrayOfLong;
        int k;
        switch (i)
        {
        default: 
          if (super.zza(paramzzbyb, i)) {}
          break;
        case 0: 
          return this;
        case 8: 
          j = zzbym.zzb(paramzzbyb, 8);
          if (this.zzbxJ == null) {}
          for (i = 0;; i = this.zzbxJ.length)
          {
            arrayOfLong = new long[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzbxJ, 0, arrayOfLong, 0, i);
              j = i;
            }
            while (j < arrayOfLong.length - 1)
            {
              arrayOfLong[j] = paramzzbyb.zzaeY();
              paramzzbyb.zzaeW();
              j += 1;
            }
          }
          arrayOfLong[j] = paramzzbyb.zzaeY();
          this.zzbxJ = arrayOfLong;
          break;
        case 10: 
          k = paramzzbyb.zzrf(paramzzbyb.zzaff());
          i = paramzzbyb.getPosition();
          j = 0;
          while (paramzzbyb.zzafk() > 0)
          {
            paramzzbyb.zzaeY();
            j += 1;
          }
          paramzzbyb.zzrh(i);
          if (this.zzbxJ == null) {}
          for (i = 0;; i = this.zzbxJ.length)
          {
            arrayOfLong = new long[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzbxJ, 0, arrayOfLong, 0, i);
              j = i;
            }
            while (j < arrayOfLong.length)
            {
              arrayOfLong[j] = paramzzbyb.zzaeY();
              j += 1;
            }
          }
          this.zzbxJ = arrayOfLong;
          paramzzbyb.zzrg(k);
          break;
        case 16: 
          j = zzbym.zzb(paramzzbyb, 16);
          if (this.zzbxK == null) {}
          for (i = 0;; i = this.zzbxK.length)
          {
            arrayOfLong = new long[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzbxK, 0, arrayOfLong, 0, i);
              j = i;
            }
            while (j < arrayOfLong.length - 1)
            {
              arrayOfLong[j] = paramzzbyb.zzaeY();
              paramzzbyb.zzaeW();
              j += 1;
            }
          }
          arrayOfLong[j] = paramzzbyb.zzaeY();
          this.zzbxK = arrayOfLong;
          break;
        case 18: 
          k = paramzzbyb.zzrf(paramzzbyb.zzaff());
          i = paramzzbyb.getPosition();
          j = 0;
          while (paramzzbyb.zzafk() > 0)
          {
            paramzzbyb.zzaeY();
            j += 1;
          }
          paramzzbyb.zzrh(i);
          if (this.zzbxK == null) {}
          for (i = 0;; i = this.zzbxK.length)
          {
            arrayOfLong = new long[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzbxK, 0, arrayOfLong, 0, i);
              j = i;
            }
            while (j < arrayOfLong.length)
            {
              arrayOfLong[j] = paramzzbyb.zzaeY();
              j += 1;
            }
          }
          this.zzbxK = arrayOfLong;
          paramzzbyb.zzrg(k);
        }
      }
    }
    
    public void zza(zzbyc paramzzbyc)
      throws IOException
    {
      int j = 0;
      int i;
      if ((this.zzbxJ != null) && (this.zzbxJ.length > 0))
      {
        i = 0;
        while (i < this.zzbxJ.length)
        {
          paramzzbyc.zza(1, this.zzbxJ[i]);
          i += 1;
        }
      }
      if ((this.zzbxK != null) && (this.zzbxK.length > 0))
      {
        i = j;
        while (i < this.zzbxK.length)
        {
          paramzzbyc.zza(2, this.zzbxK[i]);
          i += 1;
        }
      }
      super.zza(paramzzbyc);
    }
    
    protected int zzu()
    {
      int m = 0;
      int k = super.zzu();
      int j;
      if ((this.zzbxJ != null) && (this.zzbxJ.length > 0))
      {
        i = 0;
        j = 0;
        while (i < this.zzbxJ.length)
        {
          j += zzbyc.zzbp(this.zzbxJ[i]);
          i += 1;
        }
      }
      for (int i = k + j + this.zzbxJ.length * 1;; i = k)
      {
        j = i;
        if (this.zzbxK != null)
        {
          j = i;
          if (this.zzbxK.length > 0)
          {
            k = 0;
            j = m;
            while (j < this.zzbxK.length)
            {
              k += zzbyc.zzbp(this.zzbxK[j]);
              j += 1;
            }
            j = i + k + this.zzbxK.length * 1;
          }
        }
        return j;
      }
    }
  }
  
  public static final class zzg
    extends zzbyd<zzg>
  {
    private static volatile zzg[] zzbxL;
    public String name;
    public String zzaGV;
    public Float zzbwh;
    public Double zzbwi;
    public Long zzbxM;
    public Long zzbxf;
    
    public zzg()
    {
      zzNL();
    }
    
    public static zzg[] zzNK()
    {
      if (zzbxL == null) {}
      synchronized (zzbyh.zzcwK)
      {
        if (zzbxL == null) {
          zzbxL = new zzg[0];
        }
        return zzbxL;
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
      label89:
      label105:
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
                  do
                  {
                    return bool1;
                    bool1 = bool2;
                  } while (!(paramObject instanceof zzg));
                  paramObject = (zzg)paramObject;
                  if (this.zzbxM != null) {
                    break;
                  }
                  bool1 = bool2;
                } while (((zzg)paramObject).zzbxM != null);
                if (this.name != null) {
                  break label175;
                }
                bool1 = bool2;
              } while (((zzg)paramObject).name != null);
              if (this.zzaGV != null) {
                break label191;
              }
              bool1 = bool2;
            } while (((zzg)paramObject).zzaGV != null);
            if (this.zzbxf != null) {
              break label207;
            }
            bool1 = bool2;
          } while (((zzg)paramObject).zzbxf != null);
          if (this.zzbwh != null) {
            break label223;
          }
          bool1 = bool2;
        } while (((zzg)paramObject).zzbwh != null);
        if (this.zzbwi != null) {
          break label239;
        }
        bool1 = bool2;
      } while (((zzg)paramObject).zzbwi != null);
      for (;;)
      {
        if ((this.zzcwC == null) || (this.zzcwC.isEmpty()))
        {
          if (((zzg)paramObject).zzcwC != null)
          {
            bool1 = bool2;
            if (!((zzg)paramObject).zzcwC.isEmpty()) {
              break;
            }
          }
          return true;
          if (this.zzbxM.equals(((zzg)paramObject).zzbxM)) {
            break label41;
          }
          return false;
          label175:
          if (this.name.equals(((zzg)paramObject).name)) {
            break label57;
          }
          return false;
          label191:
          if (this.zzaGV.equals(((zzg)paramObject).zzaGV)) {
            break label73;
          }
          return false;
          label207:
          if (this.zzbxf.equals(((zzg)paramObject).zzbxf)) {
            break label89;
          }
          return false;
          label223:
          if (this.zzbwh.equals(((zzg)paramObject).zzbwh)) {
            break label105;
          }
          return false;
          label239:
          if (!this.zzbwi.equals(((zzg)paramObject).zzbwi)) {
            return false;
          }
        }
      }
      return this.zzcwC.equals(((zzg)paramObject).zzcwC);
    }
    
    public int hashCode()
    {
      int i3 = 0;
      int i4 = getClass().getName().hashCode();
      int i;
      int j;
      label33:
      int k;
      label42:
      int m;
      label52:
      int n;
      label62:
      int i1;
      if (this.zzbxM == null)
      {
        i = 0;
        if (this.name != null) {
          break label154;
        }
        j = 0;
        if (this.zzaGV != null) {
          break label165;
        }
        k = 0;
        if (this.zzbxf != null) {
          break label176;
        }
        m = 0;
        if (this.zzbwh != null) {
          break label188;
        }
        n = 0;
        if (this.zzbwi != null) {
          break label200;
        }
        i1 = 0;
        label72:
        i2 = i3;
        if (this.zzcwC != null) {
          if (!this.zzcwC.isEmpty()) {
            break label212;
          }
        }
      }
      label154:
      label165:
      label176:
      label188:
      label200:
      label212:
      for (int i2 = i3;; i2 = this.zzcwC.hashCode())
      {
        return (i1 + (n + (m + (k + (j + (i + (i4 + 527) * 31) * 31) * 31) * 31) * 31) * 31) * 31 + i2;
        i = this.zzbxM.hashCode();
        break;
        j = this.name.hashCode();
        break label33;
        k = this.zzaGV.hashCode();
        break label42;
        m = this.zzbxf.hashCode();
        break label52;
        n = this.zzbwh.hashCode();
        break label62;
        i1 = this.zzbwi.hashCode();
        break label72;
      }
    }
    
    public zzg zzNL()
    {
      this.zzbxM = null;
      this.name = null;
      this.zzaGV = null;
      this.zzbxf = null;
      this.zzbwh = null;
      this.zzbwi = null;
      this.zzcwC = null;
      this.zzcwL = -1;
      return this;
    }
    
    public zzg zzV(zzbyb paramzzbyb)
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
          this.zzbxM = Long.valueOf(paramzzbyb.zzaeZ());
          break;
        case 18: 
          this.name = paramzzbyb.readString();
          break;
        case 26: 
          this.zzaGV = paramzzbyb.readString();
          break;
        case 32: 
          this.zzbxf = Long.valueOf(paramzzbyb.zzaeZ());
          break;
        case 45: 
          this.zzbwh = Float.valueOf(paramzzbyb.readFloat());
          break;
        case 49: 
          this.zzbwi = Double.valueOf(paramzzbyb.readDouble());
        }
      }
    }
    
    public void zza(zzbyc paramzzbyc)
      throws IOException
    {
      if (this.zzbxM != null) {
        paramzzbyc.zzb(1, this.zzbxM.longValue());
      }
      if (this.name != null) {
        paramzzbyc.zzq(2, this.name);
      }
      if (this.zzaGV != null) {
        paramzzbyc.zzq(3, this.zzaGV);
      }
      if (this.zzbxf != null) {
        paramzzbyc.zzb(4, this.zzbxf.longValue());
      }
      if (this.zzbwh != null) {
        paramzzbyc.zzc(5, this.zzbwh.floatValue());
      }
      if (this.zzbwi != null) {
        paramzzbyc.zza(6, this.zzbwi.doubleValue());
      }
      super.zza(paramzzbyc);
    }
    
    protected int zzu()
    {
      int j = super.zzu();
      int i = j;
      if (this.zzbxM != null) {
        i = j + zzbyc.zzf(1, this.zzbxM.longValue());
      }
      j = i;
      if (this.name != null) {
        j = i + zzbyc.zzr(2, this.name);
      }
      i = j;
      if (this.zzaGV != null) {
        i = j + zzbyc.zzr(3, this.zzaGV);
      }
      j = i;
      if (this.zzbxf != null) {
        j = i + zzbyc.zzf(4, this.zzbxf.longValue());
      }
      i = j;
      if (this.zzbwh != null) {
        i = j + zzbyc.zzd(5, this.zzbwh.floatValue());
      }
      j = i;
      if (this.zzbwi != null) {
        j = i + zzbyc.zzb(6, this.zzbwi.doubleValue());
      }
      return j;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzauw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */