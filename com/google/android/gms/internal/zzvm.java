package com.google.android.gms.internal;

import java.io.IOException;

public abstract interface zzvm
{
  public static final class zza
    extends zzark
  {
    private static volatile zza[] atj;
    public Integer asA;
    public zzvm.zzf atk;
    public zzvm.zzf atl;
    public Boolean atm;
    
    public zza()
    {
      zzbzc();
    }
    
    public static zza[] zzbzb()
    {
      if (atj == null) {}
      synchronized (zzari.bqD)
      {
        if (atj == null) {
          atj = new zza[0];
        }
        return atj;
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
          if (this.asA == null)
          {
            if (((zza)paramObject).asA != null) {
              return false;
            }
          }
          else if (!this.asA.equals(((zza)paramObject).asA)) {
            return false;
          }
          if (this.atk == null)
          {
            if (((zza)paramObject).atk != null) {
              return false;
            }
          }
          else if (!this.atk.equals(((zza)paramObject).atk)) {
            return false;
          }
          if (this.atl == null)
          {
            if (((zza)paramObject).atl != null) {
              return false;
            }
          }
          else if (!this.atl.equals(((zza)paramObject).atl)) {
            return false;
          }
          if (this.atm != null) {
            break;
          }
        } while (((zza)paramObject).atm == null);
        return false;
      } while (this.atm.equals(((zza)paramObject).atm));
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
      if (this.asA == null)
      {
        i = 0;
        if (this.atk != null) {
          break label88;
        }
        j = 0;
        if (this.atl != null) {
          break label99;
        }
        k = 0;
        label42:
        if (this.atm != null) {
          break label110;
        }
      }
      for (;;)
      {
        return (k + (j + (i + (n + 527) * 31) * 31) * 31) * 31 + m;
        i = this.asA.hashCode();
        break;
        label88:
        j = this.atk.hashCode();
        break label33;
        label99:
        k = this.atl.hashCode();
        break label42;
        label110:
        m = this.atm.hashCode();
      }
    }
    
    public void zza(zzard paramzzard)
      throws IOException
    {
      if (this.asA != null) {
        paramzzard.zzae(1, this.asA.intValue());
      }
      if (this.atk != null) {
        paramzzard.zza(2, this.atk);
      }
      if (this.atl != null) {
        paramzzard.zza(3, this.atl);
      }
      if (this.atm != null) {
        paramzzard.zzj(4, this.atm.booleanValue());
      }
      super.zza(paramzzard);
    }
    
    public zza zzam(zzarc paramzzarc)
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
          this.asA = Integer.valueOf(paramzzarc.cA());
          break;
        case 18: 
          if (this.atk == null) {
            this.atk = new zzvm.zzf();
          }
          paramzzarc.zza(this.atk);
          break;
        case 26: 
          if (this.atl == null) {
            this.atl = new zzvm.zzf();
          }
          paramzzarc.zza(this.atl);
          break;
        case 32: 
          this.atm = Boolean.valueOf(paramzzarc.cC());
        }
      }
    }
    
    public zza zzbzc()
    {
      this.asA = null;
      this.atk = null;
      this.atl = null;
      this.atm = null;
      this.bqE = -1;
      return this;
    }
    
    protected int zzx()
    {
      int j = super.zzx();
      int i = j;
      if (this.asA != null) {
        i = j + zzard.zzag(1, this.asA.intValue());
      }
      j = i;
      if (this.atk != null) {
        j = i + zzard.zzc(2, this.atk);
      }
      i = j;
      if (this.atl != null) {
        i = j + zzard.zzc(3, this.atl);
      }
      j = i;
      if (this.atm != null) {
        j = i + zzard.zzk(4, this.atm.booleanValue());
      }
      return j;
    }
  }
  
  public static final class zzb
    extends zzark
  {
    private static volatile zzb[] atn;
    public zzvm.zzc[] ato;
    public Long atp;
    public Long atq;
    public Integer count;
    public String name;
    
    public zzb()
    {
      zzbze();
    }
    
    public static zzb[] zzbzd()
    {
      if (atn == null) {}
      synchronized (zzari.bqD)
      {
        if (atn == null) {
          atn = new zzb[0];
        }
        return atn;
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
          if (!zzari.equals(this.ato, ((zzb)paramObject).ato)) {
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
          if (this.atp == null)
          {
            if (((zzb)paramObject).atp != null) {
              return false;
            }
          }
          else if (!this.atp.equals(((zzb)paramObject).atp)) {
            return false;
          }
          if (this.atq == null)
          {
            if (((zzb)paramObject).atq != null) {
              return false;
            }
          }
          else if (!this.atq.equals(((zzb)paramObject).atq)) {
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
      int i1 = zzari.hashCode(this.ato);
      int i;
      int j;
      label42:
      int k;
      if (this.name == null)
      {
        i = 0;
        if (this.atp != null) {
          break label103;
        }
        j = 0;
        if (this.atq != null) {
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
        j = this.atp.hashCode();
        break label42;
        label114:
        k = this.atq.hashCode();
        break label51;
        label125:
        m = this.count.hashCode();
      }
    }
    
    public void zza(zzard paramzzard)
      throws IOException
    {
      if ((this.ato != null) && (this.ato.length > 0))
      {
        int i = 0;
        while (i < this.ato.length)
        {
          zzvm.zzc localzzc = this.ato[i];
          if (localzzc != null) {
            paramzzard.zza(1, localzzc);
          }
          i += 1;
        }
      }
      if (this.name != null) {
        paramzzard.zzr(2, this.name);
      }
      if (this.atp != null) {
        paramzzard.zzb(3, this.atp.longValue());
      }
      if (this.atq != null) {
        paramzzard.zzb(4, this.atq.longValue());
      }
      if (this.count != null) {
        paramzzard.zzae(5, this.count.intValue());
      }
      super.zza(paramzzard);
    }
    
    public zzb zzan(zzarc paramzzarc)
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
          int j = zzarn.zzc(paramzzarc, 10);
          if (this.ato == null) {}
          zzvm.zzc[] arrayOfzzc;
          for (i = 0;; i = this.ato.length)
          {
            arrayOfzzc = new zzvm.zzc[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.ato, 0, arrayOfzzc, 0, i);
              j = i;
            }
            while (j < arrayOfzzc.length - 1)
            {
              arrayOfzzc[j] = new zzvm.zzc();
              paramzzarc.zza(arrayOfzzc[j]);
              paramzzarc.cw();
              j += 1;
            }
          }
          arrayOfzzc[j] = new zzvm.zzc();
          paramzzarc.zza(arrayOfzzc[j]);
          this.ato = arrayOfzzc;
          break;
        case 18: 
          this.name = paramzzarc.readString();
          break;
        case 24: 
          this.atp = Long.valueOf(paramzzarc.cz());
          break;
        case 32: 
          this.atq = Long.valueOf(paramzzarc.cz());
          break;
        case 40: 
          this.count = Integer.valueOf(paramzzarc.cA());
        }
      }
    }
    
    public zzb zzbze()
    {
      this.ato = zzvm.zzc.zzbzf();
      this.name = null;
      this.atp = null;
      this.atq = null;
      this.count = null;
      this.bqE = -1;
      return this;
    }
    
    protected int zzx()
    {
      int i = super.zzx();
      int j = i;
      if (this.ato != null)
      {
        j = i;
        if (this.ato.length > 0)
        {
          int k = 0;
          for (;;)
          {
            j = i;
            if (k >= this.ato.length) {
              break;
            }
            zzvm.zzc localzzc = this.ato[k];
            j = i;
            if (localzzc != null) {
              j = i + zzard.zzc(1, localzzc);
            }
            k += 1;
            i = j;
          }
        }
      }
      i = j;
      if (this.name != null) {
        i = j + zzard.zzs(2, this.name);
      }
      j = i;
      if (this.atp != null) {
        j = i + zzard.zzf(3, this.atp.longValue());
      }
      i = j;
      if (this.atq != null) {
        i = j + zzard.zzf(4, this.atq.longValue());
      }
      j = i;
      if (this.count != null) {
        j = i + zzard.zzag(5, this.count.intValue());
      }
      return j;
    }
  }
  
  public static final class zzc
    extends zzark
  {
    private static volatile zzc[] atr;
    public String Dr;
    public Float asw;
    public Double asx;
    public Long ats;
    public String name;
    
    public zzc()
    {
      zzbzg();
    }
    
    public static zzc[] zzbzf()
    {
      if (atr == null) {}
      synchronized (zzari.bqD)
      {
        if (atr == null) {
          atr = new zzc[0];
        }
        return atr;
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
          if (this.Dr == null)
          {
            if (((zzc)paramObject).Dr != null) {
              return false;
            }
          }
          else if (!this.Dr.equals(((zzc)paramObject).Dr)) {
            return false;
          }
          if (this.ats == null)
          {
            if (((zzc)paramObject).ats != null) {
              return false;
            }
          }
          else if (!this.ats.equals(((zzc)paramObject).ats)) {
            return false;
          }
          if (this.asw == null)
          {
            if (((zzc)paramObject).asw != null) {
              return false;
            }
          }
          else if (!this.asw.equals(((zzc)paramObject).asw)) {
            return false;
          }
          if (this.asx != null) {
            break;
          }
        } while (((zzc)paramObject).asx == null);
        return false;
      } while (this.asx.equals(((zzc)paramObject).asx));
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
        if (this.Dr != null) {
          break label104;
        }
        j = 0;
        if (this.ats != null) {
          break label115;
        }
        k = 0;
        if (this.asw != null) {
          break label126;
        }
        m = 0;
        label52:
        if (this.asx != null) {
          break label138;
        }
      }
      for (;;)
      {
        return (m + (k + (j + (i + (i1 + 527) * 31) * 31) * 31) * 31) * 31 + n;
        i = this.name.hashCode();
        break;
        label104:
        j = this.Dr.hashCode();
        break label33;
        label115:
        k = this.ats.hashCode();
        break label42;
        label126:
        m = this.asw.hashCode();
        break label52;
        label138:
        n = this.asx.hashCode();
      }
    }
    
    public void zza(zzard paramzzard)
      throws IOException
    {
      if (this.name != null) {
        paramzzard.zzr(1, this.name);
      }
      if (this.Dr != null) {
        paramzzard.zzr(2, this.Dr);
      }
      if (this.ats != null) {
        paramzzard.zzb(3, this.ats.longValue());
      }
      if (this.asw != null) {
        paramzzard.zzc(4, this.asw.floatValue());
      }
      if (this.asx != null) {
        paramzzard.zza(5, this.asx.doubleValue());
      }
      super.zza(paramzzard);
    }
    
    public zzc zzao(zzarc paramzzarc)
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
        case 18: 
          this.Dr = paramzzarc.readString();
          break;
        case 24: 
          this.ats = Long.valueOf(paramzzarc.cz());
          break;
        case 37: 
          this.asw = Float.valueOf(paramzzarc.readFloat());
          break;
        case 41: 
          this.asx = Double.valueOf(paramzzarc.readDouble());
        }
      }
    }
    
    public zzc zzbzg()
    {
      this.name = null;
      this.Dr = null;
      this.ats = null;
      this.asw = null;
      this.asx = null;
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
      if (this.Dr != null) {
        j = i + zzard.zzs(2, this.Dr);
      }
      i = j;
      if (this.ats != null) {
        i = j + zzard.zzf(3, this.ats.longValue());
      }
      j = i;
      if (this.asw != null) {
        j = i + zzard.zzd(4, this.asw.floatValue());
      }
      i = j;
      if (this.asx != null) {
        i = j + zzard.zzb(5, this.asx.doubleValue());
      }
      return i;
    }
  }
  
  public static final class zzd
    extends zzark
  {
    public zzvm.zze[] att;
    
    public zzd()
    {
      zzbzh();
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
      } while (zzari.equals(this.att, ((zzd)paramObject).att));
      return false;
    }
    
    public int hashCode()
    {
      return (getClass().getName().hashCode() + 527) * 31 + zzari.hashCode(this.att);
    }
    
    public void zza(zzard paramzzard)
      throws IOException
    {
      if ((this.att != null) && (this.att.length > 0))
      {
        int i = 0;
        while (i < this.att.length)
        {
          zzvm.zze localzze = this.att[i];
          if (localzze != null) {
            paramzzard.zza(1, localzze);
          }
          i += 1;
        }
      }
      super.zza(paramzzard);
    }
    
    public zzd zzap(zzarc paramzzarc)
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
          int j = zzarn.zzc(paramzzarc, 10);
          if (this.att == null) {}
          zzvm.zze[] arrayOfzze;
          for (i = 0;; i = this.att.length)
          {
            arrayOfzze = new zzvm.zze[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.att, 0, arrayOfzze, 0, i);
              j = i;
            }
            while (j < arrayOfzze.length - 1)
            {
              arrayOfzze[j] = new zzvm.zze();
              paramzzarc.zza(arrayOfzze[j]);
              paramzzarc.cw();
              j += 1;
            }
          }
          arrayOfzze[j] = new zzvm.zze();
          paramzzarc.zza(arrayOfzze[j]);
          this.att = arrayOfzze;
        }
      }
    }
    
    public zzd zzbzh()
    {
      this.att = zzvm.zze.zzbzi();
      this.bqE = -1;
      return this;
    }
    
    protected int zzx()
    {
      int i = super.zzx();
      int k = i;
      if (this.att != null)
      {
        k = i;
        if (this.att.length > 0)
        {
          int j = 0;
          for (;;)
          {
            k = i;
            if (j >= this.att.length) {
              break;
            }
            zzvm.zze localzze = this.att[j];
            k = i;
            if (localzze != null) {
              k = i + zzard.zzc(1, localzze);
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
    extends zzark
  {
    private static volatile zze[] atu;
    public String afY;
    public String anQ;
    public String anR;
    public String anU;
    public String anY;
    public Long atA;
    public Long atB;
    public Long atC;
    public String atD;
    public String atE;
    public String atF;
    public Integer atG;
    public Long atH;
    public Long atI;
    public String atJ;
    public Boolean atK;
    public String atL;
    public Long atM;
    public Integer atN;
    public Boolean atO;
    public zzvm.zza[] atP;
    public Integer atQ;
    public Integer atR;
    public Integer atS;
    public String atT;
    public Integer atv;
    public zzvm.zzb[] atw;
    public zzvm.zzg[] atx;
    public Long aty;
    public Long atz;
    public String zzck;
    public String zzct;
    
    public zze()
    {
      zzbzj();
    }
    
    public static zze[] zzbzi()
    {
      if (atu == null) {}
      synchronized (zzari.bqD)
      {
        if (atu == null) {
          atu = new zze[0];
        }
        return atu;
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
          if (this.atv == null)
          {
            if (((zze)paramObject).atv != null) {
              return false;
            }
          }
          else if (!this.atv.equals(((zze)paramObject).atv)) {
            return false;
          }
          if (!zzari.equals(this.atw, ((zze)paramObject).atw)) {
            return false;
          }
          if (!zzari.equals(this.atx, ((zze)paramObject).atx)) {
            return false;
          }
          if (this.aty == null)
          {
            if (((zze)paramObject).aty != null) {
              return false;
            }
          }
          else if (!this.aty.equals(((zze)paramObject).aty)) {
            return false;
          }
          if (this.atz == null)
          {
            if (((zze)paramObject).atz != null) {
              return false;
            }
          }
          else if (!this.atz.equals(((zze)paramObject).atz)) {
            return false;
          }
          if (this.atA == null)
          {
            if (((zze)paramObject).atA != null) {
              return false;
            }
          }
          else if (!this.atA.equals(((zze)paramObject).atA)) {
            return false;
          }
          if (this.atB == null)
          {
            if (((zze)paramObject).atB != null) {
              return false;
            }
          }
          else if (!this.atB.equals(((zze)paramObject).atB)) {
            return false;
          }
          if (this.atC == null)
          {
            if (((zze)paramObject).atC != null) {
              return false;
            }
          }
          else if (!this.atC.equals(((zze)paramObject).atC)) {
            return false;
          }
          if (this.atD == null)
          {
            if (((zze)paramObject).atD != null) {
              return false;
            }
          }
          else if (!this.atD.equals(((zze)paramObject).atD)) {
            return false;
          }
          if (this.zzct == null)
          {
            if (((zze)paramObject).zzct != null) {
              return false;
            }
          }
          else if (!this.zzct.equals(((zze)paramObject).zzct)) {
            return false;
          }
          if (this.atE == null)
          {
            if (((zze)paramObject).atE != null) {
              return false;
            }
          }
          else if (!this.atE.equals(((zze)paramObject).atE)) {
            return false;
          }
          if (this.atF == null)
          {
            if (((zze)paramObject).atF != null) {
              return false;
            }
          }
          else if (!this.atF.equals(((zze)paramObject).atF)) {
            return false;
          }
          if (this.atG == null)
          {
            if (((zze)paramObject).atG != null) {
              return false;
            }
          }
          else if (!this.atG.equals(((zze)paramObject).atG)) {
            return false;
          }
          if (this.anR == null)
          {
            if (((zze)paramObject).anR != null) {
              return false;
            }
          }
          else if (!this.anR.equals(((zze)paramObject).anR)) {
            return false;
          }
          if (this.zzck == null)
          {
            if (((zze)paramObject).zzck != null) {
              return false;
            }
          }
          else if (!this.zzck.equals(((zze)paramObject).zzck)) {
            return false;
          }
          if (this.afY == null)
          {
            if (((zze)paramObject).afY != null) {
              return false;
            }
          }
          else if (!this.afY.equals(((zze)paramObject).afY)) {
            return false;
          }
          if (this.atH == null)
          {
            if (((zze)paramObject).atH != null) {
              return false;
            }
          }
          else if (!this.atH.equals(((zze)paramObject).atH)) {
            return false;
          }
          if (this.atI == null)
          {
            if (((zze)paramObject).atI != null) {
              return false;
            }
          }
          else if (!this.atI.equals(((zze)paramObject).atI)) {
            return false;
          }
          if (this.atJ == null)
          {
            if (((zze)paramObject).atJ != null) {
              return false;
            }
          }
          else if (!this.atJ.equals(((zze)paramObject).atJ)) {
            return false;
          }
          if (this.atK == null)
          {
            if (((zze)paramObject).atK != null) {
              return false;
            }
          }
          else if (!this.atK.equals(((zze)paramObject).atK)) {
            return false;
          }
          if (this.atL == null)
          {
            if (((zze)paramObject).atL != null) {
              return false;
            }
          }
          else if (!this.atL.equals(((zze)paramObject).atL)) {
            return false;
          }
          if (this.atM == null)
          {
            if (((zze)paramObject).atM != null) {
              return false;
            }
          }
          else if (!this.atM.equals(((zze)paramObject).atM)) {
            return false;
          }
          if (this.atN == null)
          {
            if (((zze)paramObject).atN != null) {
              return false;
            }
          }
          else if (!this.atN.equals(((zze)paramObject).atN)) {
            return false;
          }
          if (this.anU == null)
          {
            if (((zze)paramObject).anU != null) {
              return false;
            }
          }
          else if (!this.anU.equals(((zze)paramObject).anU)) {
            return false;
          }
          if (this.anQ == null)
          {
            if (((zze)paramObject).anQ != null) {
              return false;
            }
          }
          else if (!this.anQ.equals(((zze)paramObject).anQ)) {
            return false;
          }
          if (this.atO == null)
          {
            if (((zze)paramObject).atO != null) {
              return false;
            }
          }
          else if (!this.atO.equals(((zze)paramObject).atO)) {
            return false;
          }
          if (!zzari.equals(this.atP, ((zze)paramObject).atP)) {
            return false;
          }
          if (this.anY == null)
          {
            if (((zze)paramObject).anY != null) {
              return false;
            }
          }
          else if (!this.anY.equals(((zze)paramObject).anY)) {
            return false;
          }
          if (this.atQ == null)
          {
            if (((zze)paramObject).atQ != null) {
              return false;
            }
          }
          else if (!this.atQ.equals(((zze)paramObject).atQ)) {
            return false;
          }
          if (this.atR == null)
          {
            if (((zze)paramObject).atR != null) {
              return false;
            }
          }
          else if (!this.atR.equals(((zze)paramObject).atR)) {
            return false;
          }
          if (this.atS == null)
          {
            if (((zze)paramObject).atS != null) {
              return false;
            }
          }
          else if (!this.atS.equals(((zze)paramObject).atS)) {
            return false;
          }
          if (this.atT != null) {
            break;
          }
        } while (((zze)paramObject).atT == null);
        return false;
      } while (this.atT.equals(((zze)paramObject).atT));
      return false;
    }
    
    public int hashCode()
    {
      int i24 = 0;
      int i25 = getClass().getName().hashCode();
      int i;
      int i26;
      int i27;
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
      int i28;
      int i20;
      label289:
      int i21;
      label299:
      int i22;
      label309:
      int i23;
      if (this.atv == null)
      {
        i = 0;
        i26 = zzari.hashCode(this.atw);
        i27 = zzari.hashCode(this.atx);
        if (this.aty != null) {
          break label533;
        }
        j = 0;
        if (this.atz != null) {
          break label544;
        }
        k = 0;
        if (this.atA != null) {
          break label555;
        }
        m = 0;
        if (this.atB != null) {
          break label567;
        }
        n = 0;
        if (this.atC != null) {
          break label579;
        }
        i1 = 0;
        if (this.atD != null) {
          break label591;
        }
        i2 = 0;
        if (this.zzct != null) {
          break label603;
        }
        i3 = 0;
        if (this.atE != null) {
          break label615;
        }
        i4 = 0;
        if (this.atF != null) {
          break label627;
        }
        i5 = 0;
        if (this.atG != null) {
          break label639;
        }
        i6 = 0;
        if (this.anR != null) {
          break label651;
        }
        i7 = 0;
        if (this.zzck != null) {
          break label663;
        }
        i8 = 0;
        if (this.afY != null) {
          break label675;
        }
        i9 = 0;
        if (this.atH != null) {
          break label687;
        }
        i10 = 0;
        if (this.atI != null) {
          break label699;
        }
        i11 = 0;
        if (this.atJ != null) {
          break label711;
        }
        i12 = 0;
        if (this.atK != null) {
          break label723;
        }
        i13 = 0;
        if (this.atL != null) {
          break label735;
        }
        i14 = 0;
        if (this.atM != null) {
          break label747;
        }
        i15 = 0;
        if (this.atN != null) {
          break label759;
        }
        i16 = 0;
        if (this.anU != null) {
          break label771;
        }
        i17 = 0;
        if (this.anQ != null) {
          break label783;
        }
        i18 = 0;
        if (this.atO != null) {
          break label795;
        }
        i19 = 0;
        i28 = zzari.hashCode(this.atP);
        if (this.anY != null) {
          break label807;
        }
        i20 = 0;
        if (this.atQ != null) {
          break label819;
        }
        i21 = 0;
        if (this.atR != null) {
          break label831;
        }
        i22 = 0;
        if (this.atS != null) {
          break label843;
        }
        i23 = 0;
        label319:
        if (this.atT != null) {
          break label855;
        }
      }
      for (;;)
      {
        return (i23 + (i22 + (i21 + (i20 + ((i19 + (i18 + (i17 + (i16 + (i15 + (i14 + (i13 + (i12 + (i11 + (i10 + (i9 + (i8 + (i7 + (i6 + (i5 + (i4 + (i3 + (i2 + (i1 + (n + (m + (k + (j + (((i + (i25 + 527) * 31) * 31 + i26) * 31 + i27) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31 + i28) * 31) * 31) * 31) * 31) * 31 + i24;
        i = this.atv.hashCode();
        break;
        label533:
        j = this.aty.hashCode();
        break label51;
        label544:
        k = this.atz.hashCode();
        break label60;
        label555:
        m = this.atA.hashCode();
        break label70;
        label567:
        n = this.atB.hashCode();
        break label80;
        label579:
        i1 = this.atC.hashCode();
        break label90;
        label591:
        i2 = this.atD.hashCode();
        break label100;
        label603:
        i3 = this.zzct.hashCode();
        break label110;
        label615:
        i4 = this.atE.hashCode();
        break label120;
        label627:
        i5 = this.atF.hashCode();
        break label130;
        label639:
        i6 = this.atG.hashCode();
        break label140;
        label651:
        i7 = this.anR.hashCode();
        break label150;
        label663:
        i8 = this.zzck.hashCode();
        break label160;
        label675:
        i9 = this.afY.hashCode();
        break label170;
        label687:
        i10 = this.atH.hashCode();
        break label180;
        label699:
        i11 = this.atI.hashCode();
        break label190;
        label711:
        i12 = this.atJ.hashCode();
        break label200;
        label723:
        i13 = this.atK.hashCode();
        break label210;
        label735:
        i14 = this.atL.hashCode();
        break label220;
        label747:
        i15 = this.atM.hashCode();
        break label230;
        label759:
        i16 = this.atN.hashCode();
        break label240;
        label771:
        i17 = this.anU.hashCode();
        break label250;
        label783:
        i18 = this.anQ.hashCode();
        break label260;
        label795:
        i19 = this.atO.hashCode();
        break label270;
        label807:
        i20 = this.anY.hashCode();
        break label289;
        label819:
        i21 = this.atQ.hashCode();
        break label299;
        label831:
        i22 = this.atR.hashCode();
        break label309;
        label843:
        i23 = this.atS.hashCode();
        break label319;
        label855:
        i24 = this.atT.hashCode();
      }
    }
    
    public void zza(zzard paramzzard)
      throws IOException
    {
      int j = 0;
      if (this.atv != null) {
        paramzzard.zzae(1, this.atv.intValue());
      }
      int i;
      Object localObject;
      if ((this.atw != null) && (this.atw.length > 0))
      {
        i = 0;
        while (i < this.atw.length)
        {
          localObject = this.atw[i];
          if (localObject != null) {
            paramzzard.zza(2, (zzark)localObject);
          }
          i += 1;
        }
      }
      if ((this.atx != null) && (this.atx.length > 0))
      {
        i = 0;
        while (i < this.atx.length)
        {
          localObject = this.atx[i];
          if (localObject != null) {
            paramzzard.zza(3, (zzark)localObject);
          }
          i += 1;
        }
      }
      if (this.aty != null) {
        paramzzard.zzb(4, this.aty.longValue());
      }
      if (this.atz != null) {
        paramzzard.zzb(5, this.atz.longValue());
      }
      if (this.atA != null) {
        paramzzard.zzb(6, this.atA.longValue());
      }
      if (this.atC != null) {
        paramzzard.zzb(7, this.atC.longValue());
      }
      if (this.atD != null) {
        paramzzard.zzr(8, this.atD);
      }
      if (this.zzct != null) {
        paramzzard.zzr(9, this.zzct);
      }
      if (this.atE != null) {
        paramzzard.zzr(10, this.atE);
      }
      if (this.atF != null) {
        paramzzard.zzr(11, this.atF);
      }
      if (this.atG != null) {
        paramzzard.zzae(12, this.atG.intValue());
      }
      if (this.anR != null) {
        paramzzard.zzr(13, this.anR);
      }
      if (this.zzck != null) {
        paramzzard.zzr(14, this.zzck);
      }
      if (this.afY != null) {
        paramzzard.zzr(16, this.afY);
      }
      if (this.atH != null) {
        paramzzard.zzb(17, this.atH.longValue());
      }
      if (this.atI != null) {
        paramzzard.zzb(18, this.atI.longValue());
      }
      if (this.atJ != null) {
        paramzzard.zzr(19, this.atJ);
      }
      if (this.atK != null) {
        paramzzard.zzj(20, this.atK.booleanValue());
      }
      if (this.atL != null) {
        paramzzard.zzr(21, this.atL);
      }
      if (this.atM != null) {
        paramzzard.zzb(22, this.atM.longValue());
      }
      if (this.atN != null) {
        paramzzard.zzae(23, this.atN.intValue());
      }
      if (this.anU != null) {
        paramzzard.zzr(24, this.anU);
      }
      if (this.anQ != null) {
        paramzzard.zzr(25, this.anQ);
      }
      if (this.atB != null) {
        paramzzard.zzb(26, this.atB.longValue());
      }
      if (this.atO != null) {
        paramzzard.zzj(28, this.atO.booleanValue());
      }
      if ((this.atP != null) && (this.atP.length > 0))
      {
        i = j;
        while (i < this.atP.length)
        {
          localObject = this.atP[i];
          if (localObject != null) {
            paramzzard.zza(29, (zzark)localObject);
          }
          i += 1;
        }
      }
      if (this.anY != null) {
        paramzzard.zzr(30, this.anY);
      }
      if (this.atQ != null) {
        paramzzard.zzae(31, this.atQ.intValue());
      }
      if (this.atR != null) {
        paramzzard.zzae(32, this.atR.intValue());
      }
      if (this.atS != null) {
        paramzzard.zzae(33, this.atS.intValue());
      }
      if (this.atT != null) {
        paramzzard.zzr(34, this.atT);
      }
      super.zza(paramzzard);
    }
    
    public zze zzaq(zzarc paramzzarc)
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
          this.atv = Integer.valueOf(paramzzarc.cA());
          break;
        case 18: 
          j = zzarn.zzc(paramzzarc, 18);
          if (this.atw == null) {}
          for (i = 0;; i = this.atw.length)
          {
            localObject = new zzvm.zzb[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.atw, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length - 1)
            {
              localObject[j] = new zzvm.zzb();
              paramzzarc.zza(localObject[j]);
              paramzzarc.cw();
              j += 1;
            }
          }
          localObject[j] = new zzvm.zzb();
          paramzzarc.zza(localObject[j]);
          this.atw = ((zzvm.zzb[])localObject);
          break;
        case 26: 
          j = zzarn.zzc(paramzzarc, 26);
          if (this.atx == null) {}
          for (i = 0;; i = this.atx.length)
          {
            localObject = new zzvm.zzg[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.atx, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length - 1)
            {
              localObject[j] = new zzvm.zzg();
              paramzzarc.zza(localObject[j]);
              paramzzarc.cw();
              j += 1;
            }
          }
          localObject[j] = new zzvm.zzg();
          paramzzarc.zza(localObject[j]);
          this.atx = ((zzvm.zzg[])localObject);
          break;
        case 32: 
          this.aty = Long.valueOf(paramzzarc.cz());
          break;
        case 40: 
          this.atz = Long.valueOf(paramzzarc.cz());
          break;
        case 48: 
          this.atA = Long.valueOf(paramzzarc.cz());
          break;
        case 56: 
          this.atC = Long.valueOf(paramzzarc.cz());
          break;
        case 66: 
          this.atD = paramzzarc.readString();
          break;
        case 74: 
          this.zzct = paramzzarc.readString();
          break;
        case 82: 
          this.atE = paramzzarc.readString();
          break;
        case 90: 
          this.atF = paramzzarc.readString();
          break;
        case 96: 
          this.atG = Integer.valueOf(paramzzarc.cA());
          break;
        case 106: 
          this.anR = paramzzarc.readString();
          break;
        case 114: 
          this.zzck = paramzzarc.readString();
          break;
        case 130: 
          this.afY = paramzzarc.readString();
          break;
        case 136: 
          this.atH = Long.valueOf(paramzzarc.cz());
          break;
        case 144: 
          this.atI = Long.valueOf(paramzzarc.cz());
          break;
        case 154: 
          this.atJ = paramzzarc.readString();
          break;
        case 160: 
          this.atK = Boolean.valueOf(paramzzarc.cC());
          break;
        case 170: 
          this.atL = paramzzarc.readString();
          break;
        case 176: 
          this.atM = Long.valueOf(paramzzarc.cz());
          break;
        case 184: 
          this.atN = Integer.valueOf(paramzzarc.cA());
          break;
        case 194: 
          this.anU = paramzzarc.readString();
          break;
        case 202: 
          this.anQ = paramzzarc.readString();
          break;
        case 208: 
          this.atB = Long.valueOf(paramzzarc.cz());
          break;
        case 224: 
          this.atO = Boolean.valueOf(paramzzarc.cC());
          break;
        case 234: 
          j = zzarn.zzc(paramzzarc, 234);
          if (this.atP == null) {}
          for (i = 0;; i = this.atP.length)
          {
            localObject = new zzvm.zza[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.atP, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length - 1)
            {
              localObject[j] = new zzvm.zza();
              paramzzarc.zza(localObject[j]);
              paramzzarc.cw();
              j += 1;
            }
          }
          localObject[j] = new zzvm.zza();
          paramzzarc.zza(localObject[j]);
          this.atP = ((zzvm.zza[])localObject);
          break;
        case 242: 
          this.anY = paramzzarc.readString();
          break;
        case 248: 
          this.atQ = Integer.valueOf(paramzzarc.cA());
          break;
        case 256: 
          this.atR = Integer.valueOf(paramzzarc.cA());
          break;
        case 264: 
          this.atS = Integer.valueOf(paramzzarc.cA());
          break;
        case 274: 
          this.atT = paramzzarc.readString();
        }
      }
    }
    
    public zze zzbzj()
    {
      this.atv = null;
      this.atw = zzvm.zzb.zzbzd();
      this.atx = zzvm.zzg.zzbzl();
      this.aty = null;
      this.atz = null;
      this.atA = null;
      this.atB = null;
      this.atC = null;
      this.atD = null;
      this.zzct = null;
      this.atE = null;
      this.atF = null;
      this.atG = null;
      this.anR = null;
      this.zzck = null;
      this.afY = null;
      this.atH = null;
      this.atI = null;
      this.atJ = null;
      this.atK = null;
      this.atL = null;
      this.atM = null;
      this.atN = null;
      this.anU = null;
      this.anQ = null;
      this.atO = null;
      this.atP = zzvm.zza.zzbzb();
      this.anY = null;
      this.atQ = null;
      this.atR = null;
      this.atS = null;
      this.atT = null;
      this.bqE = -1;
      return this;
    }
    
    protected int zzx()
    {
      int m = 0;
      int j = super.zzx();
      int i = j;
      if (this.atv != null) {
        i = j + zzard.zzag(1, this.atv.intValue());
      }
      j = i;
      Object localObject;
      if (this.atw != null)
      {
        j = i;
        if (this.atw.length > 0)
        {
          j = 0;
          while (j < this.atw.length)
          {
            localObject = this.atw[j];
            k = i;
            if (localObject != null) {
              k = i + zzard.zzc(2, (zzark)localObject);
            }
            j += 1;
            i = k;
          }
          j = i;
        }
      }
      i = j;
      if (this.atx != null)
      {
        i = j;
        if (this.atx.length > 0)
        {
          i = j;
          j = 0;
          while (j < this.atx.length)
          {
            localObject = this.atx[j];
            k = i;
            if (localObject != null) {
              k = i + zzard.zzc(3, (zzark)localObject);
            }
            j += 1;
            i = k;
          }
        }
      }
      j = i;
      if (this.aty != null) {
        j = i + zzard.zzf(4, this.aty.longValue());
      }
      i = j;
      if (this.atz != null) {
        i = j + zzard.zzf(5, this.atz.longValue());
      }
      j = i;
      if (this.atA != null) {
        j = i + zzard.zzf(6, this.atA.longValue());
      }
      i = j;
      if (this.atC != null) {
        i = j + zzard.zzf(7, this.atC.longValue());
      }
      j = i;
      if (this.atD != null) {
        j = i + zzard.zzs(8, this.atD);
      }
      i = j;
      if (this.zzct != null) {
        i = j + zzard.zzs(9, this.zzct);
      }
      j = i;
      if (this.atE != null) {
        j = i + zzard.zzs(10, this.atE);
      }
      i = j;
      if (this.atF != null) {
        i = j + zzard.zzs(11, this.atF);
      }
      j = i;
      if (this.atG != null) {
        j = i + zzard.zzag(12, this.atG.intValue());
      }
      i = j;
      if (this.anR != null) {
        i = j + zzard.zzs(13, this.anR);
      }
      j = i;
      if (this.zzck != null) {
        j = i + zzard.zzs(14, this.zzck);
      }
      i = j;
      if (this.afY != null) {
        i = j + zzard.zzs(16, this.afY);
      }
      j = i;
      if (this.atH != null) {
        j = i + zzard.zzf(17, this.atH.longValue());
      }
      i = j;
      if (this.atI != null) {
        i = j + zzard.zzf(18, this.atI.longValue());
      }
      j = i;
      if (this.atJ != null) {
        j = i + zzard.zzs(19, this.atJ);
      }
      i = j;
      if (this.atK != null) {
        i = j + zzard.zzk(20, this.atK.booleanValue());
      }
      j = i;
      if (this.atL != null) {
        j = i + zzard.zzs(21, this.atL);
      }
      i = j;
      if (this.atM != null) {
        i = j + zzard.zzf(22, this.atM.longValue());
      }
      j = i;
      if (this.atN != null) {
        j = i + zzard.zzag(23, this.atN.intValue());
      }
      i = j;
      if (this.anU != null) {
        i = j + zzard.zzs(24, this.anU);
      }
      j = i;
      if (this.anQ != null) {
        j = i + zzard.zzs(25, this.anQ);
      }
      int k = j;
      if (this.atB != null) {
        k = j + zzard.zzf(26, this.atB.longValue());
      }
      i = k;
      if (this.atO != null) {
        i = k + zzard.zzk(28, this.atO.booleanValue());
      }
      j = i;
      if (this.atP != null)
      {
        j = i;
        if (this.atP.length > 0)
        {
          k = m;
          for (;;)
          {
            j = i;
            if (k >= this.atP.length) {
              break;
            }
            localObject = this.atP[k];
            j = i;
            if (localObject != null) {
              j = i + zzard.zzc(29, (zzark)localObject);
            }
            k += 1;
            i = j;
          }
        }
      }
      i = j;
      if (this.anY != null) {
        i = j + zzard.zzs(30, this.anY);
      }
      j = i;
      if (this.atQ != null) {
        j = i + zzard.zzag(31, this.atQ.intValue());
      }
      i = j;
      if (this.atR != null) {
        i = j + zzard.zzag(32, this.atR.intValue());
      }
      j = i;
      if (this.atS != null) {
        j = i + zzard.zzag(33, this.atS.intValue());
      }
      i = j;
      if (this.atT != null) {
        i = j + zzard.zzs(34, this.atT);
      }
      return i;
    }
  }
  
  public static final class zzf
    extends zzark
  {
    public long[] atU;
    public long[] atV;
    
    public zzf()
    {
      zzbzk();
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
        if (!zzari.equals(this.atU, ((zzf)paramObject).atU)) {
          return false;
        }
      } while (zzari.equals(this.atV, ((zzf)paramObject).atV));
      return false;
    }
    
    public int hashCode()
    {
      return ((getClass().getName().hashCode() + 527) * 31 + zzari.hashCode(this.atU)) * 31 + zzari.hashCode(this.atV);
    }
    
    public void zza(zzard paramzzard)
      throws IOException
    {
      int j = 0;
      int i;
      if ((this.atU != null) && (this.atU.length > 0))
      {
        i = 0;
        while (i < this.atU.length)
        {
          paramzzard.zza(1, this.atU[i]);
          i += 1;
        }
      }
      if ((this.atV != null) && (this.atV.length > 0))
      {
        i = j;
        while (i < this.atV.length)
        {
          paramzzard.zza(2, this.atV[i]);
          i += 1;
        }
      }
      super.zza(paramzzard);
    }
    
    public zzf zzar(zzarc paramzzarc)
      throws IOException
    {
      for (;;)
      {
        int i = paramzzarc.cw();
        int j;
        long[] arrayOfLong;
        int k;
        switch (i)
        {
        default: 
          if (zzarn.zzb(paramzzarc, i)) {}
          break;
        case 0: 
          return this;
        case 8: 
          j = zzarn.zzc(paramzzarc, 8);
          if (this.atU == null) {}
          for (i = 0;; i = this.atU.length)
          {
            arrayOfLong = new long[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.atU, 0, arrayOfLong, 0, i);
              j = i;
            }
            while (j < arrayOfLong.length - 1)
            {
              arrayOfLong[j] = paramzzarc.cy();
              paramzzarc.cw();
              j += 1;
            }
          }
          arrayOfLong[j] = paramzzarc.cy();
          this.atU = arrayOfLong;
          break;
        case 10: 
          k = paramzzarc.zzahc(paramzzarc.cF());
          i = paramzzarc.getPosition();
          j = 0;
          while (paramzzarc.cK() > 0)
          {
            paramzzarc.cy();
            j += 1;
          }
          paramzzarc.zzahe(i);
          if (this.atU == null) {}
          for (i = 0;; i = this.atU.length)
          {
            arrayOfLong = new long[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.atU, 0, arrayOfLong, 0, i);
              j = i;
            }
            while (j < arrayOfLong.length)
            {
              arrayOfLong[j] = paramzzarc.cy();
              j += 1;
            }
          }
          this.atU = arrayOfLong;
          paramzzarc.zzahd(k);
          break;
        case 16: 
          j = zzarn.zzc(paramzzarc, 16);
          if (this.atV == null) {}
          for (i = 0;; i = this.atV.length)
          {
            arrayOfLong = new long[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.atV, 0, arrayOfLong, 0, i);
              j = i;
            }
            while (j < arrayOfLong.length - 1)
            {
              arrayOfLong[j] = paramzzarc.cy();
              paramzzarc.cw();
              j += 1;
            }
          }
          arrayOfLong[j] = paramzzarc.cy();
          this.atV = arrayOfLong;
          break;
        case 18: 
          k = paramzzarc.zzahc(paramzzarc.cF());
          i = paramzzarc.getPosition();
          j = 0;
          while (paramzzarc.cK() > 0)
          {
            paramzzarc.cy();
            j += 1;
          }
          paramzzarc.zzahe(i);
          if (this.atV == null) {}
          for (i = 0;; i = this.atV.length)
          {
            arrayOfLong = new long[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.atV, 0, arrayOfLong, 0, i);
              j = i;
            }
            while (j < arrayOfLong.length)
            {
              arrayOfLong[j] = paramzzarc.cy();
              j += 1;
            }
          }
          this.atV = arrayOfLong;
          paramzzarc.zzahd(k);
        }
      }
    }
    
    public zzf zzbzk()
    {
      this.atU = zzarn.bqG;
      this.atV = zzarn.bqG;
      this.bqE = -1;
      return this;
    }
    
    protected int zzx()
    {
      int m = 0;
      int k = super.zzx();
      int j;
      if ((this.atU != null) && (this.atU.length > 0))
      {
        i = 0;
        j = 0;
        while (i < this.atU.length)
        {
          j += zzard.zzda(this.atU[i]);
          i += 1;
        }
      }
      for (int i = k + j + this.atU.length * 1;; i = k)
      {
        j = i;
        if (this.atV != null)
        {
          j = i;
          if (this.atV.length > 0)
          {
            k = 0;
            j = m;
            while (j < this.atV.length)
            {
              k += zzard.zzda(this.atV[j]);
              j += 1;
            }
            j = i + k + this.atV.length * 1;
          }
        }
        return j;
      }
    }
  }
  
  public static final class zzg
    extends zzark
  {
    private static volatile zzg[] atW;
    public String Dr;
    public Float asw;
    public Double asx;
    public Long atX;
    public Long ats;
    public String name;
    
    public zzg()
    {
      zzbzm();
    }
    
    public static zzg[] zzbzl()
    {
      if (atW == null) {}
      synchronized (zzari.bqD)
      {
        if (atW == null) {
          atW = new zzg[0];
        }
        return atW;
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
          if (this.atX == null)
          {
            if (((zzg)paramObject).atX != null) {
              return false;
            }
          }
          else if (!this.atX.equals(((zzg)paramObject).atX)) {
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
          if (this.Dr == null)
          {
            if (((zzg)paramObject).Dr != null) {
              return false;
            }
          }
          else if (!this.Dr.equals(((zzg)paramObject).Dr)) {
            return false;
          }
          if (this.ats == null)
          {
            if (((zzg)paramObject).ats != null) {
              return false;
            }
          }
          else if (!this.ats.equals(((zzg)paramObject).ats)) {
            return false;
          }
          if (this.asw == null)
          {
            if (((zzg)paramObject).asw != null) {
              return false;
            }
          }
          else if (!this.asw.equals(((zzg)paramObject).asw)) {
            return false;
          }
          if (this.asx != null) {
            break;
          }
        } while (((zzg)paramObject).asx == null);
        return false;
      } while (this.asx.equals(((zzg)paramObject).asx));
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
      if (this.atX == null)
      {
        i = 0;
        if (this.name != null) {
          break label120;
        }
        j = 0;
        if (this.Dr != null) {
          break label131;
        }
        k = 0;
        if (this.ats != null) {
          break label142;
        }
        m = 0;
        if (this.asw != null) {
          break label154;
        }
        n = 0;
        label62:
        if (this.asx != null) {
          break label166;
        }
      }
      for (;;)
      {
        return (n + (m + (k + (j + (i + (i2 + 527) * 31) * 31) * 31) * 31) * 31) * 31 + i1;
        i = this.atX.hashCode();
        break;
        label120:
        j = this.name.hashCode();
        break label33;
        label131:
        k = this.Dr.hashCode();
        break label42;
        label142:
        m = this.ats.hashCode();
        break label52;
        label154:
        n = this.asw.hashCode();
        break label62;
        label166:
        i1 = this.asx.hashCode();
      }
    }
    
    public void zza(zzard paramzzard)
      throws IOException
    {
      if (this.atX != null) {
        paramzzard.zzb(1, this.atX.longValue());
      }
      if (this.name != null) {
        paramzzard.zzr(2, this.name);
      }
      if (this.Dr != null) {
        paramzzard.zzr(3, this.Dr);
      }
      if (this.ats != null) {
        paramzzard.zzb(4, this.ats.longValue());
      }
      if (this.asw != null) {
        paramzzard.zzc(5, this.asw.floatValue());
      }
      if (this.asx != null) {
        paramzzard.zza(6, this.asx.doubleValue());
      }
      super.zza(paramzzard);
    }
    
    public zzg zzas(zzarc paramzzarc)
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
          this.atX = Long.valueOf(paramzzarc.cz());
          break;
        case 18: 
          this.name = paramzzarc.readString();
          break;
        case 26: 
          this.Dr = paramzzarc.readString();
          break;
        case 32: 
          this.ats = Long.valueOf(paramzzarc.cz());
          break;
        case 45: 
          this.asw = Float.valueOf(paramzzarc.readFloat());
          break;
        case 49: 
          this.asx = Double.valueOf(paramzzarc.readDouble());
        }
      }
    }
    
    public zzg zzbzm()
    {
      this.atX = null;
      this.name = null;
      this.Dr = null;
      this.ats = null;
      this.asw = null;
      this.asx = null;
      this.bqE = -1;
      return this;
    }
    
    protected int zzx()
    {
      int j = super.zzx();
      int i = j;
      if (this.atX != null) {
        i = j + zzard.zzf(1, this.atX.longValue());
      }
      j = i;
      if (this.name != null) {
        j = i + zzard.zzs(2, this.name);
      }
      i = j;
      if (this.Dr != null) {
        i = j + zzard.zzs(3, this.Dr);
      }
      j = i;
      if (this.ats != null) {
        j = i + zzard.zzf(4, this.ats.longValue());
      }
      i = j;
      if (this.asw != null) {
        i = j + zzard.zzd(5, this.asw.floatValue());
      }
      j = i;
      if (this.asx != null) {
        j = i + zzard.zzb(6, this.asx.doubleValue());
      }
      return j;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzvm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */