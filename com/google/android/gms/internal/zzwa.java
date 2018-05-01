package com.google.android.gms.internal;

import java.io.IOException;

public abstract interface zzwa
{
  public static final class zza
    extends zzasa
  {
    private static volatile zza[] avY;
    public Integer avZ;
    public zzwa.zze[] awa;
    public zzwa.zzb[] awb;
    
    public zza()
    {
      zzbzi();
    }
    
    public static zza[] zzbzh()
    {
      if (avY == null) {}
      synchronized (zzary.btO)
      {
        if (avY == null) {
          avY = new zza[0];
        }
        return avY;
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
        if (this.avZ == null)
        {
          if (((zza)paramObject).avZ != null) {
            return false;
          }
        }
        else if (!this.avZ.equals(((zza)paramObject).avZ)) {
          return false;
        }
        if (!zzary.equals(this.awa, ((zza)paramObject).awa)) {
          return false;
        }
      } while (zzary.equals(this.awb, ((zza)paramObject).awb));
      return false;
    }
    
    public int hashCode()
    {
      int j = getClass().getName().hashCode();
      if (this.avZ == null) {}
      for (int i = 0;; i = this.avZ.hashCode()) {
        return ((i + (j + 527) * 31) * 31 + zzary.hashCode(this.awa)) * 31 + zzary.hashCode(this.awb);
      }
    }
    
    public void zza(zzart paramzzart)
      throws IOException
    {
      int j = 0;
      if (this.avZ != null) {
        paramzzart.zzaf(1, this.avZ.intValue());
      }
      int i;
      Object localObject;
      if ((this.awa != null) && (this.awa.length > 0))
      {
        i = 0;
        while (i < this.awa.length)
        {
          localObject = this.awa[i];
          if (localObject != null) {
            paramzzart.zza(2, (zzasa)localObject);
          }
          i += 1;
        }
      }
      if ((this.awb != null) && (this.awb.length > 0))
      {
        i = j;
        while (i < this.awb.length)
        {
          localObject = this.awb[i];
          if (localObject != null) {
            paramzzart.zza(3, (zzasa)localObject);
          }
          i += 1;
        }
      }
      super.zza(paramzzart);
    }
    
    public zza zzag(zzars paramzzars)
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
          this.avZ = Integer.valueOf(paramzzars.bY());
          break;
        case 18: 
          j = zzasd.zzc(paramzzars, 18);
          if (this.awa == null) {}
          for (i = 0;; i = this.awa.length)
          {
            localObject = new zzwa.zze[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.awa, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length - 1)
            {
              localObject[j] = new zzwa.zze();
              paramzzars.zza(localObject[j]);
              paramzzars.bU();
              j += 1;
            }
          }
          localObject[j] = new zzwa.zze();
          paramzzars.zza(localObject[j]);
          this.awa = ((zzwa.zze[])localObject);
          break;
        case 26: 
          j = zzasd.zzc(paramzzars, 26);
          if (this.awb == null) {}
          for (i = 0;; i = this.awb.length)
          {
            localObject = new zzwa.zzb[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.awb, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length - 1)
            {
              localObject[j] = new zzwa.zzb();
              paramzzars.zza(localObject[j]);
              paramzzars.bU();
              j += 1;
            }
          }
          localObject[j] = new zzwa.zzb();
          paramzzars.zza(localObject[j]);
          this.awb = ((zzwa.zzb[])localObject);
        }
      }
    }
    
    public zza zzbzi()
    {
      this.avZ = null;
      this.awa = zzwa.zze.zzbzo();
      this.awb = zzwa.zzb.zzbzj();
      this.btP = -1;
      return this;
    }
    
    protected int zzx()
    {
      int m = 0;
      int i = super.zzx();
      int j = i;
      if (this.avZ != null) {
        j = i + zzart.zzah(1, this.avZ.intValue());
      }
      i = j;
      Object localObject;
      if (this.awa != null)
      {
        i = j;
        if (this.awa.length > 0)
        {
          i = j;
          j = 0;
          while (j < this.awa.length)
          {
            localObject = this.awa[j];
            k = i;
            if (localObject != null) {
              k = i + zzart.zzc(2, (zzasa)localObject);
            }
            j += 1;
            i = k;
          }
        }
      }
      int k = i;
      if (this.awb != null)
      {
        k = i;
        if (this.awb.length > 0)
        {
          j = m;
          for (;;)
          {
            k = i;
            if (j >= this.awb.length) {
              break;
            }
            localObject = this.awb[j];
            k = i;
            if (localObject != null) {
              k = i + zzart.zzc(3, (zzasa)localObject);
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
    extends zzasa
  {
    private static volatile zzb[] awc;
    public Integer awd;
    public String awe;
    public zzwa.zzc[] awf;
    public Boolean awg;
    public zzwa.zzd awh;
    
    public zzb()
    {
      zzbzk();
    }
    
    public static zzb[] zzbzj()
    {
      if (awc == null) {}
      synchronized (zzary.btO)
      {
        if (awc == null) {
          awc = new zzb[0];
        }
        return awc;
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
          if (this.awd == null)
          {
            if (((zzb)paramObject).awd != null) {
              return false;
            }
          }
          else if (!this.awd.equals(((zzb)paramObject).awd)) {
            return false;
          }
          if (this.awe == null)
          {
            if (((zzb)paramObject).awe != null) {
              return false;
            }
          }
          else if (!this.awe.equals(((zzb)paramObject).awe)) {
            return false;
          }
          if (!zzary.equals(this.awf, ((zzb)paramObject).awf)) {
            return false;
          }
          if (this.awg == null)
          {
            if (((zzb)paramObject).awg != null) {
              return false;
            }
          }
          else if (!this.awg.equals(((zzb)paramObject).awg)) {
            return false;
          }
          if (this.awh != null) {
            break;
          }
        } while (((zzb)paramObject).awh == null);
        return false;
      } while (this.awh.equals(((zzb)paramObject).awh));
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
      if (this.awd == null)
      {
        i = 0;
        if (this.awe != null) {
          break label103;
        }
        j = 0;
        i1 = zzary.hashCode(this.awf);
        if (this.awg != null) {
          break label114;
        }
        k = 0;
        label51:
        if (this.awh != null) {
          break label125;
        }
      }
      for (;;)
      {
        return (k + ((j + (i + (n + 527) * 31) * 31) * 31 + i1) * 31) * 31 + m;
        i = this.awd.hashCode();
        break;
        label103:
        j = this.awe.hashCode();
        break label33;
        label114:
        k = this.awg.hashCode();
        break label51;
        label125:
        m = this.awh.hashCode();
      }
    }
    
    public void zza(zzart paramzzart)
      throws IOException
    {
      if (this.awd != null) {
        paramzzart.zzaf(1, this.awd.intValue());
      }
      if (this.awe != null) {
        paramzzart.zzq(2, this.awe);
      }
      if ((this.awf != null) && (this.awf.length > 0))
      {
        int i = 0;
        while (i < this.awf.length)
        {
          zzwa.zzc localzzc = this.awf[i];
          if (localzzc != null) {
            paramzzart.zza(3, localzzc);
          }
          i += 1;
        }
      }
      if (this.awg != null) {
        paramzzart.zzg(4, this.awg.booleanValue());
      }
      if (this.awh != null) {
        paramzzart.zza(5, this.awh);
      }
      super.zza(paramzzart);
    }
    
    public zzb zzah(zzars paramzzars)
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
          this.awd = Integer.valueOf(paramzzars.bY());
          break;
        case 18: 
          this.awe = paramzzars.readString();
          break;
        case 26: 
          int j = zzasd.zzc(paramzzars, 26);
          if (this.awf == null) {}
          zzwa.zzc[] arrayOfzzc;
          for (i = 0;; i = this.awf.length)
          {
            arrayOfzzc = new zzwa.zzc[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.awf, 0, arrayOfzzc, 0, i);
              j = i;
            }
            while (j < arrayOfzzc.length - 1)
            {
              arrayOfzzc[j] = new zzwa.zzc();
              paramzzars.zza(arrayOfzzc[j]);
              paramzzars.bU();
              j += 1;
            }
          }
          arrayOfzzc[j] = new zzwa.zzc();
          paramzzars.zza(arrayOfzzc[j]);
          this.awf = arrayOfzzc;
          break;
        case 32: 
          this.awg = Boolean.valueOf(paramzzars.ca());
          break;
        case 42: 
          if (this.awh == null) {
            this.awh = new zzwa.zzd();
          }
          paramzzars.zza(this.awh);
        }
      }
    }
    
    public zzb zzbzk()
    {
      this.awd = null;
      this.awe = null;
      this.awf = zzwa.zzc.zzbzl();
      this.awg = null;
      this.awh = null;
      this.btP = -1;
      return this;
    }
    
    protected int zzx()
    {
      int i = super.zzx();
      int j = i;
      if (this.awd != null) {
        j = i + zzart.zzah(1, this.awd.intValue());
      }
      i = j;
      if (this.awe != null) {
        i = j + zzart.zzr(2, this.awe);
      }
      j = i;
      if (this.awf != null)
      {
        j = i;
        if (this.awf.length > 0)
        {
          j = 0;
          while (j < this.awf.length)
          {
            zzwa.zzc localzzc = this.awf[j];
            int k = i;
            if (localzzc != null) {
              k = i + zzart.zzc(3, localzzc);
            }
            j += 1;
            i = k;
          }
          j = i;
        }
      }
      i = j;
      if (this.awg != null) {
        i = j + zzart.zzh(4, this.awg.booleanValue());
      }
      j = i;
      if (this.awh != null) {
        j = i + zzart.zzc(5, this.awh);
      }
      return j;
    }
  }
  
  public static final class zzc
    extends zzasa
  {
    private static volatile zzc[] awi;
    public zzwa.zzf awj;
    public zzwa.zzd awk;
    public Boolean awl;
    public String awm;
    
    public zzc()
    {
      zzbzm();
    }
    
    public static zzc[] zzbzl()
    {
      if (awi == null) {}
      synchronized (zzary.btO)
      {
        if (awi == null) {
          awi = new zzc[0];
        }
        return awi;
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
          if (this.awj == null)
          {
            if (((zzc)paramObject).awj != null) {
              return false;
            }
          }
          else if (!this.awj.equals(((zzc)paramObject).awj)) {
            return false;
          }
          if (this.awk == null)
          {
            if (((zzc)paramObject).awk != null) {
              return false;
            }
          }
          else if (!this.awk.equals(((zzc)paramObject).awk)) {
            return false;
          }
          if (this.awl == null)
          {
            if (((zzc)paramObject).awl != null) {
              return false;
            }
          }
          else if (!this.awl.equals(((zzc)paramObject).awl)) {
            return false;
          }
          if (this.awm != null) {
            break;
          }
        } while (((zzc)paramObject).awm == null);
        return false;
      } while (this.awm.equals(((zzc)paramObject).awm));
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
      if (this.awj == null)
      {
        i = 0;
        if (this.awk != null) {
          break label88;
        }
        j = 0;
        if (this.awl != null) {
          break label99;
        }
        k = 0;
        label42:
        if (this.awm != null) {
          break label110;
        }
      }
      for (;;)
      {
        return (k + (j + (i + (n + 527) * 31) * 31) * 31) * 31 + m;
        i = this.awj.hashCode();
        break;
        label88:
        j = this.awk.hashCode();
        break label33;
        label99:
        k = this.awl.hashCode();
        break label42;
        label110:
        m = this.awm.hashCode();
      }
    }
    
    public void zza(zzart paramzzart)
      throws IOException
    {
      if (this.awj != null) {
        paramzzart.zza(1, this.awj);
      }
      if (this.awk != null) {
        paramzzart.zza(2, this.awk);
      }
      if (this.awl != null) {
        paramzzart.zzg(3, this.awl.booleanValue());
      }
      if (this.awm != null) {
        paramzzart.zzq(4, this.awm);
      }
      super.zza(paramzzart);
    }
    
    public zzc zzai(zzars paramzzars)
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
          if (this.awj == null) {
            this.awj = new zzwa.zzf();
          }
          paramzzars.zza(this.awj);
          break;
        case 18: 
          if (this.awk == null) {
            this.awk = new zzwa.zzd();
          }
          paramzzars.zza(this.awk);
          break;
        case 24: 
          this.awl = Boolean.valueOf(paramzzars.ca());
          break;
        case 34: 
          this.awm = paramzzars.readString();
        }
      }
    }
    
    public zzc zzbzm()
    {
      this.awj = null;
      this.awk = null;
      this.awl = null;
      this.awm = null;
      this.btP = -1;
      return this;
    }
    
    protected int zzx()
    {
      int j = super.zzx();
      int i = j;
      if (this.awj != null) {
        i = j + zzart.zzc(1, this.awj);
      }
      j = i;
      if (this.awk != null) {
        j = i + zzart.zzc(2, this.awk);
      }
      i = j;
      if (this.awl != null) {
        i = j + zzart.zzh(3, this.awl.booleanValue());
      }
      j = i;
      if (this.awm != null) {
        j = i + zzart.zzr(4, this.awm);
      }
      return j;
    }
  }
  
  public static final class zzd
    extends zzasa
  {
    public Integer awn;
    public Boolean awo;
    public String awp;
    public String awq;
    public String awr;
    
    public zzd()
    {
      zzbzn();
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
          if (this.awn == null)
          {
            if (((zzd)paramObject).awn != null) {
              return false;
            }
          }
          else if (!this.awn.equals(((zzd)paramObject).awn)) {
            return false;
          }
          if (this.awo == null)
          {
            if (((zzd)paramObject).awo != null) {
              return false;
            }
          }
          else if (!this.awo.equals(((zzd)paramObject).awo)) {
            return false;
          }
          if (this.awp == null)
          {
            if (((zzd)paramObject).awp != null) {
              return false;
            }
          }
          else if (!this.awp.equals(((zzd)paramObject).awp)) {
            return false;
          }
          if (this.awq == null)
          {
            if (((zzd)paramObject).awq != null) {
              return false;
            }
          }
          else if (!this.awq.equals(((zzd)paramObject).awq)) {
            return false;
          }
          if (this.awr != null) {
            break;
          }
        } while (((zzd)paramObject).awr == null);
        return false;
      } while (this.awr.equals(((zzd)paramObject).awr));
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
      if (this.awn == null)
      {
        i = 0;
        if (this.awo != null) {
          break label104;
        }
        j = 0;
        if (this.awp != null) {
          break label115;
        }
        k = 0;
        if (this.awq != null) {
          break label126;
        }
        m = 0;
        label52:
        if (this.awr != null) {
          break label138;
        }
      }
      for (;;)
      {
        return (m + (k + (j + (i + (i1 + 527) * 31) * 31) * 31) * 31) * 31 + n;
        i = this.awn.intValue();
        break;
        label104:
        j = this.awo.hashCode();
        break label33;
        label115:
        k = this.awp.hashCode();
        break label42;
        label126:
        m = this.awq.hashCode();
        break label52;
        label138:
        n = this.awr.hashCode();
      }
    }
    
    public void zza(zzart paramzzart)
      throws IOException
    {
      if (this.awn != null) {
        paramzzart.zzaf(1, this.awn.intValue());
      }
      if (this.awo != null) {
        paramzzart.zzg(2, this.awo.booleanValue());
      }
      if (this.awp != null) {
        paramzzart.zzq(3, this.awp);
      }
      if (this.awq != null) {
        paramzzart.zzq(4, this.awq);
      }
      if (this.awr != null) {
        paramzzart.zzq(5, this.awr);
      }
      super.zza(paramzzart);
    }
    
    public zzd zzaj(zzars paramzzars)
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
          i = paramzzars.bY();
          switch (i)
          {
          default: 
            break;
          case 0: 
          case 1: 
          case 2: 
          case 3: 
          case 4: 
            this.awn = Integer.valueOf(i);
          }
          break;
        case 16: 
          this.awo = Boolean.valueOf(paramzzars.ca());
          break;
        case 26: 
          this.awp = paramzzars.readString();
          break;
        case 34: 
          this.awq = paramzzars.readString();
          break;
        case 42: 
          this.awr = paramzzars.readString();
        }
      }
    }
    
    public zzd zzbzn()
    {
      this.awo = null;
      this.awp = null;
      this.awq = null;
      this.awr = null;
      this.btP = -1;
      return this;
    }
    
    protected int zzx()
    {
      int j = super.zzx();
      int i = j;
      if (this.awn != null) {
        i = j + zzart.zzah(1, this.awn.intValue());
      }
      j = i;
      if (this.awo != null) {
        j = i + zzart.zzh(2, this.awo.booleanValue());
      }
      i = j;
      if (this.awp != null) {
        i = j + zzart.zzr(3, this.awp);
      }
      j = i;
      if (this.awq != null) {
        j = i + zzart.zzr(4, this.awq);
      }
      i = j;
      if (this.awr != null) {
        i = j + zzart.zzr(5, this.awr);
      }
      return i;
    }
  }
  
  public static final class zze
    extends zzasa
  {
    private static volatile zze[] aws;
    public Integer awd;
    public String awt;
    public zzwa.zzc awu;
    
    public zze()
    {
      zzbzp();
    }
    
    public static zze[] zzbzo()
    {
      if (aws == null) {}
      synchronized (zzary.btO)
      {
        if (aws == null) {
          aws = new zze[0];
        }
        return aws;
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
          if (this.awd == null)
          {
            if (((zze)paramObject).awd != null) {
              return false;
            }
          }
          else if (!this.awd.equals(((zze)paramObject).awd)) {
            return false;
          }
          if (this.awt == null)
          {
            if (((zze)paramObject).awt != null) {
              return false;
            }
          }
          else if (!this.awt.equals(((zze)paramObject).awt)) {
            return false;
          }
          if (this.awu != null) {
            break;
          }
        } while (((zze)paramObject).awu == null);
        return false;
      } while (this.awu.equals(((zze)paramObject).awu));
      return false;
    }
    
    public int hashCode()
    {
      int k = 0;
      int m = getClass().getName().hashCode();
      int i;
      int j;
      if (this.awd == null)
      {
        i = 0;
        if (this.awt != null) {
          break label72;
        }
        j = 0;
        label32:
        if (this.awu != null) {
          break label83;
        }
      }
      for (;;)
      {
        return (j + (i + (m + 527) * 31) * 31) * 31 + k;
        i = this.awd.hashCode();
        break;
        label72:
        j = this.awt.hashCode();
        break label32;
        label83:
        k = this.awu.hashCode();
      }
    }
    
    public void zza(zzart paramzzart)
      throws IOException
    {
      if (this.awd != null) {
        paramzzart.zzaf(1, this.awd.intValue());
      }
      if (this.awt != null) {
        paramzzart.zzq(2, this.awt);
      }
      if (this.awu != null) {
        paramzzart.zza(3, this.awu);
      }
      super.zza(paramzzart);
    }
    
    public zze zzak(zzars paramzzars)
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
          this.awd = Integer.valueOf(paramzzars.bY());
          break;
        case 18: 
          this.awt = paramzzars.readString();
          break;
        case 26: 
          if (this.awu == null) {
            this.awu = new zzwa.zzc();
          }
          paramzzars.zza(this.awu);
        }
      }
    }
    
    public zze zzbzp()
    {
      this.awd = null;
      this.awt = null;
      this.awu = null;
      this.btP = -1;
      return this;
    }
    
    protected int zzx()
    {
      int j = super.zzx();
      int i = j;
      if (this.awd != null) {
        i = j + zzart.zzah(1, this.awd.intValue());
      }
      j = i;
      if (this.awt != null) {
        j = i + zzart.zzr(2, this.awt);
      }
      i = j;
      if (this.awu != null) {
        i = j + zzart.zzc(3, this.awu);
      }
      return i;
    }
  }
  
  public static final class zzf
    extends zzasa
  {
    public Integer awv;
    public String aww;
    public Boolean awx;
    public String[] awy;
    
    public zzf()
    {
      zzbzq();
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
        if (this.awv == null)
        {
          if (((zzf)paramObject).awv != null) {
            return false;
          }
        }
        else if (!this.awv.equals(((zzf)paramObject).awv)) {
          return false;
        }
        if (this.aww == null)
        {
          if (((zzf)paramObject).aww != null) {
            return false;
          }
        }
        else if (!this.aww.equals(((zzf)paramObject).aww)) {
          return false;
        }
        if (this.awx == null)
        {
          if (((zzf)paramObject).awx != null) {
            return false;
          }
        }
        else if (!this.awx.equals(((zzf)paramObject).awx)) {
          return false;
        }
      } while (zzary.equals(this.awy, ((zzf)paramObject).awy));
      return false;
    }
    
    public int hashCode()
    {
      int k = 0;
      int m = getClass().getName().hashCode();
      int i;
      int j;
      if (this.awv == null)
      {
        i = 0;
        if (this.aww != null) {
          break label83;
        }
        j = 0;
        label32:
        if (this.awx != null) {
          break label94;
        }
      }
      for (;;)
      {
        return ((j + (i + (m + 527) * 31) * 31) * 31 + k) * 31 + zzary.hashCode(this.awy);
        i = this.awv.intValue();
        break;
        label83:
        j = this.aww.hashCode();
        break label32;
        label94:
        k = this.awx.hashCode();
      }
    }
    
    public void zza(zzart paramzzart)
      throws IOException
    {
      if (this.awv != null) {
        paramzzart.zzaf(1, this.awv.intValue());
      }
      if (this.aww != null) {
        paramzzart.zzq(2, this.aww);
      }
      if (this.awx != null) {
        paramzzart.zzg(3, this.awx.booleanValue());
      }
      if ((this.awy != null) && (this.awy.length > 0))
      {
        int i = 0;
        while (i < this.awy.length)
        {
          String str = this.awy[i];
          if (str != null) {
            paramzzart.zzq(4, str);
          }
          i += 1;
        }
      }
      super.zza(paramzzart);
    }
    
    public zzf zzal(zzars paramzzars)
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
          i = paramzzars.bY();
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
            this.awv = Integer.valueOf(i);
          }
          break;
        case 18: 
          this.aww = paramzzars.readString();
          break;
        case 24: 
          this.awx = Boolean.valueOf(paramzzars.ca());
          break;
        case 34: 
          int j = zzasd.zzc(paramzzars, 34);
          if (this.awy == null) {}
          String[] arrayOfString;
          for (i = 0;; i = this.awy.length)
          {
            arrayOfString = new String[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.awy, 0, arrayOfString, 0, i);
              j = i;
            }
            while (j < arrayOfString.length - 1)
            {
              arrayOfString[j] = paramzzars.readString();
              paramzzars.bU();
              j += 1;
            }
          }
          arrayOfString[j] = paramzzars.readString();
          this.awy = arrayOfString;
        }
      }
    }
    
    public zzf zzbzq()
    {
      this.aww = null;
      this.awx = null;
      this.awy = zzasd.btW;
      this.btP = -1;
      return this;
    }
    
    protected int zzx()
    {
      int n = 0;
      int j = super.zzx();
      int i = j;
      if (this.awv != null) {
        i = j + zzart.zzah(1, this.awv.intValue());
      }
      j = i;
      if (this.aww != null) {
        j = i + zzart.zzr(2, this.aww);
      }
      i = j;
      if (this.awx != null) {
        i = j + zzart.zzh(3, this.awx.booleanValue());
      }
      j = i;
      if (this.awy != null)
      {
        j = i;
        if (this.awy.length > 0)
        {
          int k = 0;
          int m = 0;
          j = n;
          while (j < this.awy.length)
          {
            String str = this.awy[j];
            int i1 = k;
            n = m;
            if (str != null)
            {
              n = m + 1;
              i1 = k + zzart.zzuy(str);
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzwa.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */