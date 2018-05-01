package com.google.android.gms.internal;

import java.io.IOException;

public abstract interface zzauv
{
  public static final class zza
    extends zzbyd<zza>
  {
    private static volatile zza[] zzbwN;
    public String name;
    public Boolean zzbwO;
    public Boolean zzbwP;
    
    public zza()
    {
      zzNw();
    }
    
    public static zza[] zzNv()
    {
      if (zzbwN == null) {}
      synchronized (zzbyh.zzcwK)
      {
        if (zzbwN == null) {
          zzbwN = new zza[0];
        }
        return zzbwN;
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
            } while (!(paramObject instanceof zza));
            paramObject = (zza)paramObject;
            if (this.name != null) {
              break;
            }
            bool1 = bool2;
          } while (((zza)paramObject).name != null);
          if (this.zzbwO != null) {
            break label127;
          }
          bool1 = bool2;
        } while (((zza)paramObject).zzbwO != null);
        if (this.zzbwP != null) {
          break label143;
        }
        bool1 = bool2;
      } while (((zza)paramObject).zzbwP != null);
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
          if (this.name.equals(((zza)paramObject).name)) {
            break label41;
          }
          return false;
          label127:
          if (this.zzbwO.equals(((zza)paramObject).zzbwO)) {
            break label57;
          }
          return false;
          label143:
          if (!this.zzbwP.equals(((zza)paramObject).zzbwP)) {
            return false;
          }
        }
      }
      return this.zzcwC.equals(((zza)paramObject).zzcwC);
    }
    
    public int hashCode()
    {
      int n = 0;
      int i1 = getClass().getName().hashCode();
      int i;
      int j;
      label33:
      int k;
      if (this.name == null)
      {
        i = 0;
        if (this.zzbwO != null) {
          break label106;
        }
        j = 0;
        if (this.zzbwP != null) {
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
        i = this.name.hashCode();
        break;
        j = this.zzbwO.hashCode();
        break label33;
        k = this.zzbwP.hashCode();
        break label42;
      }
    }
    
    public zza zzM(zzbyb paramzzbyb)
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
        case 16: 
          this.zzbwO = Boolean.valueOf(paramzzbyb.zzafc());
          break;
        case 24: 
          this.zzbwP = Boolean.valueOf(paramzzbyb.zzafc());
        }
      }
    }
    
    public zza zzNw()
    {
      this.name = null;
      this.zzbwO = null;
      this.zzbwP = null;
      this.zzcwC = null;
      this.zzcwL = -1;
      return this;
    }
    
    public void zza(zzbyc paramzzbyc)
      throws IOException
    {
      if (this.name != null) {
        paramzzbyc.zzq(1, this.name);
      }
      if (this.zzbwO != null) {
        paramzzbyc.zzg(2, this.zzbwO.booleanValue());
      }
      if (this.zzbwP != null) {
        paramzzbyc.zzg(3, this.zzbwP.booleanValue());
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
      if (this.zzbwO != null) {
        j = i + zzbyc.zzh(2, this.zzbwO.booleanValue());
      }
      i = j;
      if (this.zzbwP != null) {
        i = j + zzbyc.zzh(3, this.zzbwP.booleanValue());
      }
      return i;
    }
  }
  
  public static final class zzb
    extends zzbyd<zzb>
  {
    public String zzbqK;
    public Long zzbwQ;
    public Integer zzbwR;
    public zzauv.zzc[] zzbwS;
    public zzauv.zza[] zzbwT;
    public zzauu.zza[] zzbwU;
    
    public zzb()
    {
      zzNx();
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
            } while (!(paramObject instanceof zzb));
            paramObject = (zzb)paramObject;
            if (this.zzbwQ != null) {
              break;
            }
            bool1 = bool2;
          } while (((zzb)paramObject).zzbwQ != null);
          if (this.zzbqK != null) {
            break label175;
          }
          bool1 = bool2;
        } while (((zzb)paramObject).zzbqK != null);
        if (this.zzbwR != null) {
          break label191;
        }
        bool1 = bool2;
      } while (((zzb)paramObject).zzbwR != null);
      label175:
      label191:
      while (this.zzbwR.equals(((zzb)paramObject).zzbwR))
      {
        bool1 = bool2;
        if (!zzbyh.equals(this.zzbwS, ((zzb)paramObject).zzbwS)) {
          break;
        }
        bool1 = bool2;
        if (!zzbyh.equals(this.zzbwT, ((zzb)paramObject).zzbwT)) {
          break;
        }
        bool1 = bool2;
        if (!zzbyh.equals(this.zzbwU, ((zzb)paramObject).zzbwU)) {
          break;
        }
        if ((this.zzcwC != null) && (!this.zzcwC.isEmpty())) {
          break label207;
        }
        if (((zzb)paramObject).zzcwC != null)
        {
          bool1 = bool2;
          if (!((zzb)paramObject).zzcwC.isEmpty()) {
            break;
          }
        }
        return true;
        if (this.zzbwQ.equals(((zzb)paramObject).zzbwQ)) {
          break label41;
        }
        return false;
        if (this.zzbqK.equals(((zzb)paramObject).zzbqK)) {
          break label57;
        }
        return false;
      }
      return false;
      label207:
      return this.zzcwC.equals(((zzb)paramObject).zzcwC);
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
      int i3;
      int i4;
      if (this.zzbwQ == null)
      {
        i = 0;
        if (this.zzbqK != null) {
          break label151;
        }
        j = 0;
        if (this.zzbwR != null) {
          break label162;
        }
        k = 0;
        i2 = zzbyh.hashCode(this.zzbwS);
        i3 = zzbyh.hashCode(this.zzbwT);
        i4 = zzbyh.hashCode(this.zzbwU);
        m = n;
        if (this.zzcwC != null) {
          if (!this.zzcwC.isEmpty()) {
            break label173;
          }
        }
      }
      label151:
      label162:
      label173:
      for (int m = n;; m = this.zzcwC.hashCode())
      {
        return ((((k + (j + (i + (i1 + 527) * 31) * 31) * 31) * 31 + i2) * 31 + i3) * 31 + i4) * 31 + m;
        i = this.zzbwQ.hashCode();
        break;
        j = this.zzbqK.hashCode();
        break label33;
        k = this.zzbwR.hashCode();
        break label42;
      }
    }
    
    public zzb zzN(zzbyb paramzzbyb)
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
          this.zzbwQ = Long.valueOf(paramzzbyb.zzaeZ());
          break;
        case 18: 
          this.zzbqK = paramzzbyb.readString();
          break;
        case 24: 
          this.zzbwR = Integer.valueOf(paramzzbyb.zzafa());
          break;
        case 34: 
          j = zzbym.zzb(paramzzbyb, 34);
          if (this.zzbwS == null) {}
          for (i = 0;; i = this.zzbwS.length)
          {
            localObject = new zzauv.zzc[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzbwS, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length - 1)
            {
              localObject[j] = new zzauv.zzc();
              paramzzbyb.zza(localObject[j]);
              paramzzbyb.zzaeW();
              j += 1;
            }
          }
          localObject[j] = new zzauv.zzc();
          paramzzbyb.zza(localObject[j]);
          this.zzbwS = ((zzauv.zzc[])localObject);
          break;
        case 42: 
          j = zzbym.zzb(paramzzbyb, 42);
          if (this.zzbwT == null) {}
          for (i = 0;; i = this.zzbwT.length)
          {
            localObject = new zzauv.zza[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzbwT, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length - 1)
            {
              localObject[j] = new zzauv.zza();
              paramzzbyb.zza(localObject[j]);
              paramzzbyb.zzaeW();
              j += 1;
            }
          }
          localObject[j] = new zzauv.zza();
          paramzzbyb.zza(localObject[j]);
          this.zzbwT = ((zzauv.zza[])localObject);
          break;
        case 50: 
          j = zzbym.zzb(paramzzbyb, 50);
          if (this.zzbwU == null) {}
          for (i = 0;; i = this.zzbwU.length)
          {
            localObject = new zzauu.zza[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzbwU, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length - 1)
            {
              localObject[j] = new zzauu.zza();
              paramzzbyb.zza(localObject[j]);
              paramzzbyb.zzaeW();
              j += 1;
            }
          }
          localObject[j] = new zzauu.zza();
          paramzzbyb.zza(localObject[j]);
          this.zzbwU = ((zzauu.zza[])localObject);
        }
      }
    }
    
    public zzb zzNx()
    {
      this.zzbwQ = null;
      this.zzbqK = null;
      this.zzbwR = null;
      this.zzbwS = zzauv.zzc.zzNy();
      this.zzbwT = zzauv.zza.zzNv();
      this.zzbwU = zzauu.zza.zzNl();
      this.zzcwC = null;
      this.zzcwL = -1;
      return this;
    }
    
    public void zza(zzbyc paramzzbyc)
      throws IOException
    {
      int j = 0;
      if (this.zzbwQ != null) {
        paramzzbyc.zzb(1, this.zzbwQ.longValue());
      }
      if (this.zzbqK != null) {
        paramzzbyc.zzq(2, this.zzbqK);
      }
      if (this.zzbwR != null) {
        paramzzbyc.zzJ(3, this.zzbwR.intValue());
      }
      int i;
      Object localObject;
      if ((this.zzbwS != null) && (this.zzbwS.length > 0))
      {
        i = 0;
        while (i < this.zzbwS.length)
        {
          localObject = this.zzbwS[i];
          if (localObject != null) {
            paramzzbyc.zza(4, (zzbyj)localObject);
          }
          i += 1;
        }
      }
      if ((this.zzbwT != null) && (this.zzbwT.length > 0))
      {
        i = 0;
        while (i < this.zzbwT.length)
        {
          localObject = this.zzbwT[i];
          if (localObject != null) {
            paramzzbyc.zza(5, (zzbyj)localObject);
          }
          i += 1;
        }
      }
      if ((this.zzbwU != null) && (this.zzbwU.length > 0))
      {
        i = j;
        while (i < this.zzbwU.length)
        {
          localObject = this.zzbwU[i];
          if (localObject != null) {
            paramzzbyc.zza(6, (zzbyj)localObject);
          }
          i += 1;
        }
      }
      super.zza(paramzzbyc);
    }
    
    protected int zzu()
    {
      int m = 0;
      int j = super.zzu();
      int i = j;
      if (this.zzbwQ != null) {
        i = j + zzbyc.zzf(1, this.zzbwQ.longValue());
      }
      j = i;
      if (this.zzbqK != null) {
        j = i + zzbyc.zzr(2, this.zzbqK);
      }
      i = j;
      if (this.zzbwR != null) {
        i = j + zzbyc.zzL(3, this.zzbwR.intValue());
      }
      j = i;
      Object localObject;
      if (this.zzbwS != null)
      {
        j = i;
        if (this.zzbwS.length > 0)
        {
          j = 0;
          while (j < this.zzbwS.length)
          {
            localObject = this.zzbwS[j];
            k = i;
            if (localObject != null) {
              k = i + zzbyc.zzc(4, (zzbyj)localObject);
            }
            j += 1;
            i = k;
          }
          j = i;
        }
      }
      i = j;
      if (this.zzbwT != null)
      {
        i = j;
        if (this.zzbwT.length > 0)
        {
          i = j;
          j = 0;
          while (j < this.zzbwT.length)
          {
            localObject = this.zzbwT[j];
            k = i;
            if (localObject != null) {
              k = i + zzbyc.zzc(5, (zzbyj)localObject);
            }
            j += 1;
            i = k;
          }
        }
      }
      int k = i;
      if (this.zzbwU != null)
      {
        k = i;
        if (this.zzbwU.length > 0)
        {
          j = m;
          for (;;)
          {
            k = i;
            if (j >= this.zzbwU.length) {
              break;
            }
            localObject = this.zzbwU[j];
            k = i;
            if (localObject != null) {
              k = i + zzbyc.zzc(6, (zzbyj)localObject);
            }
            j += 1;
            i = k;
          }
        }
      }
      return k;
    }
  }
  
  public static final class zzc
    extends zzbyd<zzc>
  {
    private static volatile zzc[] zzbwV;
    public String value;
    public String zzaB;
    
    public zzc()
    {
      zzNz();
    }
    
    public static zzc[] zzNy()
    {
      if (zzbwV == null) {}
      synchronized (zzbyh.zzcwK)
      {
        if (zzbwV == null) {
          zzbwV = new zzc[0];
        }
        return zzbwV;
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
          if (this.zzaB != null) {
            break;
          }
          bool1 = bool2;
        } while (((zzc)paramObject).zzaB != null);
        if (this.value != null) {
          break label111;
        }
        bool1 = bool2;
      } while (((zzc)paramObject).value != null);
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
          if (this.zzaB.equals(((zzc)paramObject).zzaB)) {
            break label41;
          }
          return false;
          label111:
          if (!this.value.equals(((zzc)paramObject).value)) {
            return false;
          }
        }
      }
      return this.zzcwC.equals(((zzc)paramObject).zzcwC);
    }
    
    public int hashCode()
    {
      int m = 0;
      int n = getClass().getName().hashCode();
      int i;
      int j;
      if (this.zzaB == null)
      {
        i = 0;
        if (this.value != null) {
          break label89;
        }
        j = 0;
        label33:
        k = m;
        if (this.zzcwC != null) {
          if (!this.zzcwC.isEmpty()) {
            break label100;
          }
        }
      }
      label89:
      label100:
      for (int k = m;; k = this.zzcwC.hashCode())
      {
        return (j + (i + (n + 527) * 31) * 31) * 31 + k;
        i = this.zzaB.hashCode();
        break;
        j = this.value.hashCode();
        break label33;
      }
    }
    
    public zzc zzNz()
    {
      this.zzaB = null;
      this.value = null;
      this.zzcwC = null;
      this.zzcwL = -1;
      return this;
    }
    
    public zzc zzO(zzbyb paramzzbyb)
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
          this.zzaB = paramzzbyb.readString();
          break;
        case 18: 
          this.value = paramzzbyb.readString();
        }
      }
    }
    
    public void zza(zzbyc paramzzbyc)
      throws IOException
    {
      if (this.zzaB != null) {
        paramzzbyc.zzq(1, this.zzaB);
      }
      if (this.value != null) {
        paramzzbyc.zzq(2, this.value);
      }
      super.zza(paramzzbyc);
    }
    
    protected int zzu()
    {
      int j = super.zzu();
      int i = j;
      if (this.zzaB != null) {
        i = j + zzbyc.zzr(1, this.zzaB);
      }
      j = i;
      if (this.value != null) {
        j = i + zzbyc.zzr(2, this.value);
      }
      return j;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzauv.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */