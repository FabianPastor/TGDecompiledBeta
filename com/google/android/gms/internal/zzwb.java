package com.google.android.gms.internal;

import java.io.IOException;

public abstract interface zzwb
{
  public static final class zza
    extends zzasa
  {
    private static volatile zza[] awz;
    public Boolean awA;
    public Boolean awB;
    public String name;
    
    public zza()
    {
      zzbzs();
    }
    
    public static zza[] zzbzr()
    {
      if (awz == null) {}
      synchronized (zzary.btO)
      {
        if (awz == null) {
          awz = new zza[0];
        }
        return awz;
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
          if (this.name == null)
          {
            if (((zza)paramObject).name != null) {
              return false;
            }
          }
          else if (!this.name.equals(((zza)paramObject).name)) {
            return false;
          }
          if (this.awA == null)
          {
            if (((zza)paramObject).awA != null) {
              return false;
            }
          }
          else if (!this.awA.equals(((zza)paramObject).awA)) {
            return false;
          }
          if (this.awB != null) {
            break;
          }
        } while (((zza)paramObject).awB == null);
        return false;
      } while (this.awB.equals(((zza)paramObject).awB));
      return false;
    }
    
    public int hashCode()
    {
      int k = 0;
      int m = getClass().getName().hashCode();
      int i;
      int j;
      if (this.name == null)
      {
        i = 0;
        if (this.awA != null) {
          break label72;
        }
        j = 0;
        label32:
        if (this.awB != null) {
          break label83;
        }
      }
      for (;;)
      {
        return (j + (i + (m + 527) * 31) * 31) * 31 + k;
        i = this.name.hashCode();
        break;
        label72:
        j = this.awA.hashCode();
        break label32;
        label83:
        k = this.awB.hashCode();
      }
    }
    
    public void zza(zzart paramzzart)
      throws IOException
    {
      if (this.name != null) {
        paramzzart.zzq(1, this.name);
      }
      if (this.awA != null) {
        paramzzart.zzg(2, this.awA.booleanValue());
      }
      if (this.awB != null) {
        paramzzart.zzg(3, this.awB.booleanValue());
      }
      super.zza(paramzzart);
    }
    
    public zza zzam(zzars paramzzars)
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
        case 16: 
          this.awA = Boolean.valueOf(paramzzars.ca());
          break;
        case 24: 
          this.awB = Boolean.valueOf(paramzzars.ca());
        }
      }
    }
    
    public zza zzbzs()
    {
      this.name = null;
      this.awA = null;
      this.awB = null;
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
      if (this.awA != null) {
        j = i + zzart.zzh(2, this.awA.booleanValue());
      }
      i = j;
      if (this.awB != null) {
        i = j + zzart.zzh(3, this.awB.booleanValue());
      }
      return i;
    }
  }
  
  public static final class zzb
    extends zzasa
  {
    public String aqZ;
    public Long awC;
    public Integer awD;
    public zzwb.zzc[] awE;
    public zzwb.zza[] awF;
    public zzwa.zza[] awG;
    
    public zzb()
    {
      zzbzt();
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {}
      do
      {
        return true;
        if (!(paramObject instanceof zzb)) {
          return false;
        }
        paramObject = (zzb)paramObject;
        if (this.awC == null)
        {
          if (((zzb)paramObject).awC != null) {
            return false;
          }
        }
        else if (!this.awC.equals(((zzb)paramObject).awC)) {
          return false;
        }
        if (this.aqZ == null)
        {
          if (((zzb)paramObject).aqZ != null) {
            return false;
          }
        }
        else if (!this.aqZ.equals(((zzb)paramObject).aqZ)) {
          return false;
        }
        if (this.awD == null)
        {
          if (((zzb)paramObject).awD != null) {
            return false;
          }
        }
        else if (!this.awD.equals(((zzb)paramObject).awD)) {
          return false;
        }
        if (!zzary.equals(this.awE, ((zzb)paramObject).awE)) {
          return false;
        }
        if (!zzary.equals(this.awF, ((zzb)paramObject).awF)) {
          return false;
        }
      } while (zzary.equals(this.awG, ((zzb)paramObject).awG));
      return false;
    }
    
    public int hashCode()
    {
      int k = 0;
      int m = getClass().getName().hashCode();
      int i;
      int j;
      if (this.awC == null)
      {
        i = 0;
        if (this.aqZ != null) {
          break label105;
        }
        j = 0;
        label32:
        if (this.awD != null) {
          break label116;
        }
      }
      for (;;)
      {
        return ((((j + (i + (m + 527) * 31) * 31) * 31 + k) * 31 + zzary.hashCode(this.awE)) * 31 + zzary.hashCode(this.awF)) * 31 + zzary.hashCode(this.awG);
        i = this.awC.hashCode();
        break;
        label105:
        j = this.aqZ.hashCode();
        break label32;
        label116:
        k = this.awD.hashCode();
      }
    }
    
    public void zza(zzart paramzzart)
      throws IOException
    {
      int j = 0;
      if (this.awC != null) {
        paramzzart.zzb(1, this.awC.longValue());
      }
      if (this.aqZ != null) {
        paramzzart.zzq(2, this.aqZ);
      }
      if (this.awD != null) {
        paramzzart.zzaf(3, this.awD.intValue());
      }
      int i;
      Object localObject;
      if ((this.awE != null) && (this.awE.length > 0))
      {
        i = 0;
        while (i < this.awE.length)
        {
          localObject = this.awE[i];
          if (localObject != null) {
            paramzzart.zza(4, (zzasa)localObject);
          }
          i += 1;
        }
      }
      if ((this.awF != null) && (this.awF.length > 0))
      {
        i = 0;
        while (i < this.awF.length)
        {
          localObject = this.awF[i];
          if (localObject != null) {
            paramzzart.zza(5, (zzasa)localObject);
          }
          i += 1;
        }
      }
      if ((this.awG != null) && (this.awG.length > 0))
      {
        i = j;
        while (i < this.awG.length)
        {
          localObject = this.awG[i];
          if (localObject != null) {
            paramzzart.zza(6, (zzasa)localObject);
          }
          i += 1;
        }
      }
      super.zza(paramzzart);
    }
    
    public zzb zzan(zzars paramzzars)
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
          this.awC = Long.valueOf(paramzzars.bX());
          break;
        case 18: 
          this.aqZ = paramzzars.readString();
          break;
        case 24: 
          this.awD = Integer.valueOf(paramzzars.bY());
          break;
        case 34: 
          j = zzasd.zzc(paramzzars, 34);
          if (this.awE == null) {}
          for (i = 0;; i = this.awE.length)
          {
            localObject = new zzwb.zzc[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.awE, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length - 1)
            {
              localObject[j] = new zzwb.zzc();
              paramzzars.zza(localObject[j]);
              paramzzars.bU();
              j += 1;
            }
          }
          localObject[j] = new zzwb.zzc();
          paramzzars.zza(localObject[j]);
          this.awE = ((zzwb.zzc[])localObject);
          break;
        case 42: 
          j = zzasd.zzc(paramzzars, 42);
          if (this.awF == null) {}
          for (i = 0;; i = this.awF.length)
          {
            localObject = new zzwb.zza[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.awF, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length - 1)
            {
              localObject[j] = new zzwb.zza();
              paramzzars.zza(localObject[j]);
              paramzzars.bU();
              j += 1;
            }
          }
          localObject[j] = new zzwb.zza();
          paramzzars.zza(localObject[j]);
          this.awF = ((zzwb.zza[])localObject);
          break;
        case 50: 
          j = zzasd.zzc(paramzzars, 50);
          if (this.awG == null) {}
          for (i = 0;; i = this.awG.length)
          {
            localObject = new zzwa.zza[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.awG, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length - 1)
            {
              localObject[j] = new zzwa.zza();
              paramzzars.zza(localObject[j]);
              paramzzars.bU();
              j += 1;
            }
          }
          localObject[j] = new zzwa.zza();
          paramzzars.zza(localObject[j]);
          this.awG = ((zzwa.zza[])localObject);
        }
      }
    }
    
    public zzb zzbzt()
    {
      this.awC = null;
      this.aqZ = null;
      this.awD = null;
      this.awE = zzwb.zzc.zzbzu();
      this.awF = zzwb.zza.zzbzr();
      this.awG = zzwa.zza.zzbzh();
      this.btP = -1;
      return this;
    }
    
    protected int zzx()
    {
      int m = 0;
      int j = super.zzx();
      int i = j;
      if (this.awC != null) {
        i = j + zzart.zzf(1, this.awC.longValue());
      }
      j = i;
      if (this.aqZ != null) {
        j = i + zzart.zzr(2, this.aqZ);
      }
      i = j;
      if (this.awD != null) {
        i = j + zzart.zzah(3, this.awD.intValue());
      }
      j = i;
      Object localObject;
      if (this.awE != null)
      {
        j = i;
        if (this.awE.length > 0)
        {
          j = 0;
          while (j < this.awE.length)
          {
            localObject = this.awE[j];
            k = i;
            if (localObject != null) {
              k = i + zzart.zzc(4, (zzasa)localObject);
            }
            j += 1;
            i = k;
          }
          j = i;
        }
      }
      i = j;
      if (this.awF != null)
      {
        i = j;
        if (this.awF.length > 0)
        {
          i = j;
          j = 0;
          while (j < this.awF.length)
          {
            localObject = this.awF[j];
            k = i;
            if (localObject != null) {
              k = i + zzart.zzc(5, (zzasa)localObject);
            }
            j += 1;
            i = k;
          }
        }
      }
      int k = i;
      if (this.awG != null)
      {
        k = i;
        if (this.awG.length > 0)
        {
          j = m;
          for (;;)
          {
            k = i;
            if (j >= this.awG.length) {
              break;
            }
            localObject = this.awG[j];
            k = i;
            if (localObject != null) {
              k = i + zzart.zzc(6, (zzasa)localObject);
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
    extends zzasa
  {
    private static volatile zzc[] awH;
    public String value;
    public String zzcb;
    
    public zzc()
    {
      zzbzv();
    }
    
    public static zzc[] zzbzu()
    {
      if (awH == null) {}
      synchronized (zzary.btO)
      {
        if (awH == null) {
          awH = new zzc[0];
        }
        return awH;
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
          if (this.zzcb == null)
          {
            if (((zzc)paramObject).zzcb != null) {
              return false;
            }
          }
          else if (!this.zzcb.equals(((zzc)paramObject).zzcb)) {
            return false;
          }
          if (this.value != null) {
            break;
          }
        } while (((zzc)paramObject).value == null);
        return false;
      } while (this.value.equals(((zzc)paramObject).value));
      return false;
    }
    
    public int hashCode()
    {
      int j = 0;
      int k = getClass().getName().hashCode();
      int i;
      if (this.zzcb == null)
      {
        i = 0;
        if (this.value != null) {
          break label56;
        }
      }
      for (;;)
      {
        return (i + (k + 527) * 31) * 31 + j;
        i = this.zzcb.hashCode();
        break;
        label56:
        j = this.value.hashCode();
      }
    }
    
    public void zza(zzart paramzzart)
      throws IOException
    {
      if (this.zzcb != null) {
        paramzzart.zzq(1, this.zzcb);
      }
      if (this.value != null) {
        paramzzart.zzq(2, this.value);
      }
      super.zza(paramzzart);
    }
    
    public zzc zzao(zzars paramzzars)
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
          this.zzcb = paramzzars.readString();
          break;
        case 18: 
          this.value = paramzzars.readString();
        }
      }
    }
    
    public zzc zzbzv()
    {
      this.zzcb = null;
      this.value = null;
      this.btP = -1;
      return this;
    }
    
    protected int zzx()
    {
      int j = super.zzx();
      int i = j;
      if (this.zzcb != null) {
        i = j + zzart.zzr(1, this.zzcb);
      }
      j = i;
      if (this.value != null) {
        j = i + zzart.zzr(2, this.value);
      }
      return j;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzwb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */