package com.google.android.gms.internal;

import java.io.IOException;

public abstract interface zzvl
{
  public static final class zza
    extends zzark
  {
    private static volatile zza[] ata;
    public Boolean atb;
    public Boolean atc;
    public String name;
    
    public zza()
    {
      zzbyx();
    }
    
    public static zza[] zzbyw()
    {
      if (ata == null) {}
      synchronized (zzari.bqD)
      {
        if (ata == null) {
          ata = new zza[0];
        }
        return ata;
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
          if (this.atb == null)
          {
            if (((zza)paramObject).atb != null) {
              return false;
            }
          }
          else if (!this.atb.equals(((zza)paramObject).atb)) {
            return false;
          }
          if (this.atc != null) {
            break;
          }
        } while (((zza)paramObject).atc == null);
        return false;
      } while (this.atc.equals(((zza)paramObject).atc));
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
        if (this.atb != null) {
          break label72;
        }
        j = 0;
        label32:
        if (this.atc != null) {
          break label83;
        }
      }
      for (;;)
      {
        return (j + (i + (m + 527) * 31) * 31) * 31 + k;
        i = this.name.hashCode();
        break;
        label72:
        j = this.atb.hashCode();
        break label32;
        label83:
        k = this.atc.hashCode();
      }
    }
    
    public void zza(zzard paramzzard)
      throws IOException
    {
      if (this.name != null) {
        paramzzard.zzr(1, this.name);
      }
      if (this.atb != null) {
        paramzzard.zzj(2, this.atb.booleanValue());
      }
      if (this.atc != null) {
        paramzzard.zzj(3, this.atc.booleanValue());
      }
      super.zza(paramzzard);
    }
    
    public zza zzaj(zzarc paramzzarc)
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
          this.name = paramzzarc.readString();
          break;
        case 16: 
          this.atb = Boolean.valueOf(paramzzarc.cC());
          break;
        case 24: 
          this.atc = Boolean.valueOf(paramzzarc.cC());
        }
      }
    }
    
    public zza zzbyx()
    {
      this.name = null;
      this.atb = null;
      this.atc = null;
      this.bqE = -1;
      return this;
    }
    
    protected int zzx()
    {
      int j = super.zzx();
      int i = j;
      if (this.name != null) {
        i = j + zzard.zzs(1, this.name);
      }
      j = i;
      if (this.atb != null) {
        j = i + zzard.zzk(2, this.atb.booleanValue());
      }
      i = j;
      if (this.atc != null) {
        i = j + zzard.zzk(3, this.atc.booleanValue());
      }
      return i;
    }
  }
  
  public static final class zzb
    extends zzark
  {
    public String anQ;
    public Long atd;
    public Integer ate;
    public zzvl.zzc[] atf;
    public zzvl.zza[] atg;
    public zzvk.zza[] ath;
    
    public zzb()
    {
      zzbyy();
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
        if (this.atd == null)
        {
          if (((zzb)paramObject).atd != null) {
            return false;
          }
        }
        else if (!this.atd.equals(((zzb)paramObject).atd)) {
          return false;
        }
        if (this.anQ == null)
        {
          if (((zzb)paramObject).anQ != null) {
            return false;
          }
        }
        else if (!this.anQ.equals(((zzb)paramObject).anQ)) {
          return false;
        }
        if (this.ate == null)
        {
          if (((zzb)paramObject).ate != null) {
            return false;
          }
        }
        else if (!this.ate.equals(((zzb)paramObject).ate)) {
          return false;
        }
        if (!zzari.equals(this.atf, ((zzb)paramObject).atf)) {
          return false;
        }
        if (!zzari.equals(this.atg, ((zzb)paramObject).atg)) {
          return false;
        }
      } while (zzari.equals(this.ath, ((zzb)paramObject).ath));
      return false;
    }
    
    public int hashCode()
    {
      int k = 0;
      int m = getClass().getName().hashCode();
      int i;
      int j;
      if (this.atd == null)
      {
        i = 0;
        if (this.anQ != null) {
          break label105;
        }
        j = 0;
        label32:
        if (this.ate != null) {
          break label116;
        }
      }
      for (;;)
      {
        return ((((j + (i + (m + 527) * 31) * 31) * 31 + k) * 31 + zzari.hashCode(this.atf)) * 31 + zzari.hashCode(this.atg)) * 31 + zzari.hashCode(this.ath);
        i = this.atd.hashCode();
        break;
        label105:
        j = this.anQ.hashCode();
        break label32;
        label116:
        k = this.ate.hashCode();
      }
    }
    
    public void zza(zzard paramzzard)
      throws IOException
    {
      int j = 0;
      if (this.atd != null) {
        paramzzard.zzb(1, this.atd.longValue());
      }
      if (this.anQ != null) {
        paramzzard.zzr(2, this.anQ);
      }
      if (this.ate != null) {
        paramzzard.zzae(3, this.ate.intValue());
      }
      int i;
      Object localObject;
      if ((this.atf != null) && (this.atf.length > 0))
      {
        i = 0;
        while (i < this.atf.length)
        {
          localObject = this.atf[i];
          if (localObject != null) {
            paramzzard.zza(4, (zzark)localObject);
          }
          i += 1;
        }
      }
      if ((this.atg != null) && (this.atg.length > 0))
      {
        i = 0;
        while (i < this.atg.length)
        {
          localObject = this.atg[i];
          if (localObject != null) {
            paramzzard.zza(5, (zzark)localObject);
          }
          i += 1;
        }
      }
      if ((this.ath != null) && (this.ath.length > 0))
      {
        i = j;
        while (i < this.ath.length)
        {
          localObject = this.ath[i];
          if (localObject != null) {
            paramzzard.zza(6, (zzark)localObject);
          }
          i += 1;
        }
      }
      super.zza(paramzzard);
    }
    
    public zzb zzak(zzarc paramzzarc)
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
          this.atd = Long.valueOf(paramzzarc.cz());
          break;
        case 18: 
          this.anQ = paramzzarc.readString();
          break;
        case 24: 
          this.ate = Integer.valueOf(paramzzarc.cA());
          break;
        case 34: 
          j = zzarn.zzc(paramzzarc, 34);
          if (this.atf == null) {}
          for (i = 0;; i = this.atf.length)
          {
            localObject = new zzvl.zzc[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.atf, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length - 1)
            {
              localObject[j] = new zzvl.zzc();
              paramzzarc.zza(localObject[j]);
              paramzzarc.cw();
              j += 1;
            }
          }
          localObject[j] = new zzvl.zzc();
          paramzzarc.zza(localObject[j]);
          this.atf = ((zzvl.zzc[])localObject);
          break;
        case 42: 
          j = zzarn.zzc(paramzzarc, 42);
          if (this.atg == null) {}
          for (i = 0;; i = this.atg.length)
          {
            localObject = new zzvl.zza[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.atg, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length - 1)
            {
              localObject[j] = new zzvl.zza();
              paramzzarc.zza(localObject[j]);
              paramzzarc.cw();
              j += 1;
            }
          }
          localObject[j] = new zzvl.zza();
          paramzzarc.zza(localObject[j]);
          this.atg = ((zzvl.zza[])localObject);
          break;
        case 50: 
          j = zzarn.zzc(paramzzarc, 50);
          if (this.ath == null) {}
          for (i = 0;; i = this.ath.length)
          {
            localObject = new zzvk.zza[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.ath, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length - 1)
            {
              localObject[j] = new zzvk.zza();
              paramzzarc.zza(localObject[j]);
              paramzzarc.cw();
              j += 1;
            }
          }
          localObject[j] = new zzvk.zza();
          paramzzarc.zza(localObject[j]);
          this.ath = ((zzvk.zza[])localObject);
        }
      }
    }
    
    public zzb zzbyy()
    {
      this.atd = null;
      this.anQ = null;
      this.ate = null;
      this.atf = zzvl.zzc.zzbyz();
      this.atg = zzvl.zza.zzbyw();
      this.ath = zzvk.zza.zzbym();
      this.bqE = -1;
      return this;
    }
    
    protected int zzx()
    {
      int m = 0;
      int j = super.zzx();
      int i = j;
      if (this.atd != null) {
        i = j + zzard.zzf(1, this.atd.longValue());
      }
      j = i;
      if (this.anQ != null) {
        j = i + zzard.zzs(2, this.anQ);
      }
      i = j;
      if (this.ate != null) {
        i = j + zzard.zzag(3, this.ate.intValue());
      }
      j = i;
      Object localObject;
      if (this.atf != null)
      {
        j = i;
        if (this.atf.length > 0)
        {
          j = 0;
          while (j < this.atf.length)
          {
            localObject = this.atf[j];
            k = i;
            if (localObject != null) {
              k = i + zzard.zzc(4, (zzark)localObject);
            }
            j += 1;
            i = k;
          }
          j = i;
        }
      }
      i = j;
      if (this.atg != null)
      {
        i = j;
        if (this.atg.length > 0)
        {
          i = j;
          j = 0;
          while (j < this.atg.length)
          {
            localObject = this.atg[j];
            k = i;
            if (localObject != null) {
              k = i + zzard.zzc(5, (zzark)localObject);
            }
            j += 1;
            i = k;
          }
        }
      }
      int k = i;
      if (this.ath != null)
      {
        k = i;
        if (this.ath.length > 0)
        {
          j = m;
          for (;;)
          {
            k = i;
            if (j >= this.ath.length) {
              break;
            }
            localObject = this.ath[j];
            k = i;
            if (localObject != null) {
              k = i + zzard.zzc(6, (zzark)localObject);
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
    extends zzark
  {
    private static volatile zzc[] ati;
    public String value;
    public String zzcb;
    
    public zzc()
    {
      zzbza();
    }
    
    public static zzc[] zzbyz()
    {
      if (ati == null) {}
      synchronized (zzari.bqD)
      {
        if (ati == null) {
          ati = new zzc[0];
        }
        return ati;
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
    
    public void zza(zzard paramzzard)
      throws IOException
    {
      if (this.zzcb != null) {
        paramzzard.zzr(1, this.zzcb);
      }
      if (this.value != null) {
        paramzzard.zzr(2, this.value);
      }
      super.zza(paramzzard);
    }
    
    public zzc zzal(zzarc paramzzarc)
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
          this.zzcb = paramzzarc.readString();
          break;
        case 18: 
          this.value = paramzzarc.readString();
        }
      }
    }
    
    public zzc zzbza()
    {
      this.zzcb = null;
      this.value = null;
      this.bqE = -1;
      return this;
    }
    
    protected int zzx()
    {
      int j = super.zzx();
      int i = j;
      if (this.zzcb != null) {
        i = j + zzard.zzs(1, this.zzcb);
      }
      j = i;
      if (this.value != null) {
        j = i + zzard.zzs(2, this.value);
      }
      return j;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzvl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */