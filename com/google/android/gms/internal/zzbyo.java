package com.google.android.gms.internal;

import java.io.IOException;
import java.util.Arrays;

public abstract interface zzbyo
{
  public static final class zza
    extends zzbyd<zza>
    implements Cloneable
  {
    public String[] zzcwZ;
    public String[] zzcxa;
    public int[] zzcxb;
    public long[] zzcxc;
    public long[] zzcxd;
    
    public zza()
    {
      zzafD();
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
                  bool1 = bool2;
                } while (!zzbyh.equals(this.zzcwZ, ((zza)paramObject).zzcwZ));
                bool1 = bool2;
              } while (!zzbyh.equals(this.zzcxa, ((zza)paramObject).zzcxa));
              bool1 = bool2;
            } while (!zzbyh.equals(this.zzcxb, ((zza)paramObject).zzcxb));
            bool1 = bool2;
          } while (!zzbyh.equals(this.zzcxc, ((zza)paramObject).zzcxc));
          bool1 = bool2;
        } while (!zzbyh.equals(this.zzcxd, ((zza)paramObject).zzcxd));
        if ((this.zzcwC != null) && (!this.zzcwC.isEmpty())) {
          break label143;
        }
        if (((zza)paramObject).zzcwC == null) {
          break;
        }
        bool1 = bool2;
      } while (!((zza)paramObject).zzcwC.isEmpty());
      return true;
      label143:
      return this.zzcwC.equals(((zza)paramObject).zzcwC);
    }
    
    public int hashCode()
    {
      int j = getClass().getName().hashCode();
      int k = zzbyh.hashCode(this.zzcwZ);
      int m = zzbyh.hashCode(this.zzcxa);
      int n = zzbyh.hashCode(this.zzcxb);
      int i1 = zzbyh.hashCode(this.zzcxc);
      int i2 = zzbyh.hashCode(this.zzcxd);
      if ((this.zzcwC == null) || (this.zzcwC.isEmpty())) {}
      for (int i = 0;; i = this.zzcwC.hashCode()) {
        return i + ((((((j + 527) * 31 + k) * 31 + m) * 31 + n) * 31 + i1) * 31 + i2) * 31;
      }
    }
    
    public void zza(zzbyc paramzzbyc)
      throws IOException
    {
      int j = 0;
      int i;
      String str;
      if ((this.zzcwZ != null) && (this.zzcwZ.length > 0))
      {
        i = 0;
        while (i < this.zzcwZ.length)
        {
          str = this.zzcwZ[i];
          if (str != null) {
            paramzzbyc.zzq(1, str);
          }
          i += 1;
        }
      }
      if ((this.zzcxa != null) && (this.zzcxa.length > 0))
      {
        i = 0;
        while (i < this.zzcxa.length)
        {
          str = this.zzcxa[i];
          if (str != null) {
            paramzzbyc.zzq(2, str);
          }
          i += 1;
        }
      }
      if ((this.zzcxb != null) && (this.zzcxb.length > 0))
      {
        i = 0;
        while (i < this.zzcxb.length)
        {
          paramzzbyc.zzJ(3, this.zzcxb[i]);
          i += 1;
        }
      }
      if ((this.zzcxc != null) && (this.zzcxc.length > 0))
      {
        i = 0;
        while (i < this.zzcxc.length)
        {
          paramzzbyc.zzb(4, this.zzcxc[i]);
          i += 1;
        }
      }
      if ((this.zzcxd != null) && (this.zzcxd.length > 0))
      {
        i = j;
        while (i < this.zzcxd.length)
        {
          paramzzbyc.zzb(5, this.zzcxd[i]);
          i += 1;
        }
      }
      super.zza(paramzzbyc);
    }
    
    public zza zzaW(zzbyb paramzzbyb)
      throws IOException
    {
      for (;;)
      {
        int i = paramzzbyb.zzaeW();
        int j;
        Object localObject;
        int k;
        switch (i)
        {
        default: 
          if (super.zza(paramzzbyb, i)) {}
          break;
        case 0: 
          return this;
        case 10: 
          j = zzbym.zzb(paramzzbyb, 10);
          if (this.zzcwZ == null) {}
          for (i = 0;; i = this.zzcwZ.length)
          {
            localObject = new String[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzcwZ, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length - 1)
            {
              localObject[j] = paramzzbyb.readString();
              paramzzbyb.zzaeW();
              j += 1;
            }
          }
          localObject[j] = paramzzbyb.readString();
          this.zzcwZ = ((String[])localObject);
          break;
        case 18: 
          j = zzbym.zzb(paramzzbyb, 18);
          if (this.zzcxa == null) {}
          for (i = 0;; i = this.zzcxa.length)
          {
            localObject = new String[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzcxa, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length - 1)
            {
              localObject[j] = paramzzbyb.readString();
              paramzzbyb.zzaeW();
              j += 1;
            }
          }
          localObject[j] = paramzzbyb.readString();
          this.zzcxa = ((String[])localObject);
          break;
        case 24: 
          j = zzbym.zzb(paramzzbyb, 24);
          if (this.zzcxb == null) {}
          for (i = 0;; i = this.zzcxb.length)
          {
            localObject = new int[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzcxb, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length - 1)
            {
              localObject[j] = paramzzbyb.zzafa();
              paramzzbyb.zzaeW();
              j += 1;
            }
          }
          localObject[j] = paramzzbyb.zzafa();
          this.zzcxb = ((int[])localObject);
          break;
        case 26: 
          k = paramzzbyb.zzrf(paramzzbyb.zzaff());
          i = paramzzbyb.getPosition();
          j = 0;
          while (paramzzbyb.zzafk() > 0)
          {
            paramzzbyb.zzafa();
            j += 1;
          }
          paramzzbyb.zzrh(i);
          if (this.zzcxb == null) {}
          for (i = 0;; i = this.zzcxb.length)
          {
            localObject = new int[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzcxb, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length)
            {
              localObject[j] = paramzzbyb.zzafa();
              j += 1;
            }
          }
          this.zzcxb = ((int[])localObject);
          paramzzbyb.zzrg(k);
          break;
        case 32: 
          j = zzbym.zzb(paramzzbyb, 32);
          if (this.zzcxc == null) {}
          for (i = 0;; i = this.zzcxc.length)
          {
            localObject = new long[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzcxc, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length - 1)
            {
              localObject[j] = paramzzbyb.zzaeZ();
              paramzzbyb.zzaeW();
              j += 1;
            }
          }
          localObject[j] = paramzzbyb.zzaeZ();
          this.zzcxc = ((long[])localObject);
          break;
        case 34: 
          k = paramzzbyb.zzrf(paramzzbyb.zzaff());
          i = paramzzbyb.getPosition();
          j = 0;
          while (paramzzbyb.zzafk() > 0)
          {
            paramzzbyb.zzaeZ();
            j += 1;
          }
          paramzzbyb.zzrh(i);
          if (this.zzcxc == null) {}
          for (i = 0;; i = this.zzcxc.length)
          {
            localObject = new long[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzcxc, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length)
            {
              localObject[j] = paramzzbyb.zzaeZ();
              j += 1;
            }
          }
          this.zzcxc = ((long[])localObject);
          paramzzbyb.zzrg(k);
          break;
        case 40: 
          j = zzbym.zzb(paramzzbyb, 40);
          if (this.zzcxd == null) {}
          for (i = 0;; i = this.zzcxd.length)
          {
            localObject = new long[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzcxd, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length - 1)
            {
              localObject[j] = paramzzbyb.zzaeZ();
              paramzzbyb.zzaeW();
              j += 1;
            }
          }
          localObject[j] = paramzzbyb.zzaeZ();
          this.zzcxd = ((long[])localObject);
          break;
        case 42: 
          k = paramzzbyb.zzrf(paramzzbyb.zzaff());
          i = paramzzbyb.getPosition();
          j = 0;
          while (paramzzbyb.zzafk() > 0)
          {
            paramzzbyb.zzaeZ();
            j += 1;
          }
          paramzzbyb.zzrh(i);
          if (this.zzcxd == null) {}
          for (i = 0;; i = this.zzcxd.length)
          {
            localObject = new long[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzcxd, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length)
            {
              localObject[j] = paramzzbyb.zzaeZ();
              j += 1;
            }
          }
          this.zzcxd = ((long[])localObject);
          paramzzbyb.zzrg(k);
        }
      }
    }
    
    public zza zzafD()
    {
      this.zzcwZ = zzbym.EMPTY_STRING_ARRAY;
      this.zzcxa = zzbym.EMPTY_STRING_ARRAY;
      this.zzcxb = zzbym.zzcwQ;
      this.zzcxc = zzbym.zzcwR;
      this.zzcxd = zzbym.zzcwR;
      this.zzcwC = null;
      this.zzcwL = -1;
      return this;
    }
    
    public zza zzafE()
    {
      try
      {
        zza localzza = (zza)super.zzafp();
        if ((this.zzcwZ != null) && (this.zzcwZ.length > 0)) {
          localzza.zzcwZ = ((String[])this.zzcwZ.clone());
        }
        if ((this.zzcxa != null) && (this.zzcxa.length > 0)) {
          localzza.zzcxa = ((String[])this.zzcxa.clone());
        }
        if ((this.zzcxb != null) && (this.zzcxb.length > 0)) {
          localzza.zzcxb = ((int[])this.zzcxb.clone());
        }
        if ((this.zzcxc != null) && (this.zzcxc.length > 0)) {
          localzza.zzcxc = ((long[])this.zzcxc.clone());
        }
        if ((this.zzcxd != null) && (this.zzcxd.length > 0)) {
          localzza.zzcxd = ((long[])this.zzcxd.clone());
        }
        return localzza;
      }
      catch (CloneNotSupportedException localCloneNotSupportedException)
      {
        throw new AssertionError(localCloneNotSupportedException);
      }
    }
    
    protected int zzu()
    {
      int i2 = 0;
      int i1 = super.zzu();
      int j;
      int k;
      String str;
      int n;
      int m;
      if ((this.zzcwZ != null) && (this.zzcwZ.length > 0))
      {
        i = 0;
        j = 0;
        for (k = 0; i < this.zzcwZ.length; k = m)
        {
          str = this.zzcwZ[i];
          n = j;
          m = k;
          if (str != null)
          {
            m = k + 1;
            n = j + zzbyc.zzku(str);
          }
          i += 1;
          j = n;
        }
      }
      for (int i = i1 + j + k * 1;; i = i1)
      {
        j = i;
        if (this.zzcxa != null)
        {
          j = i;
          if (this.zzcxa.length > 0)
          {
            j = 0;
            k = 0;
            for (m = 0; j < this.zzcxa.length; m = n)
            {
              str = this.zzcxa[j];
              i1 = k;
              n = m;
              if (str != null)
              {
                n = m + 1;
                i1 = k + zzbyc.zzku(str);
              }
              j += 1;
              k = i1;
            }
            j = i + k + m * 1;
          }
        }
        i = j;
        if (this.zzcxb != null)
        {
          i = j;
          if (this.zzcxb.length > 0)
          {
            i = 0;
            k = 0;
            while (i < this.zzcxb.length)
            {
              k += zzbyc.zzrl(this.zzcxb[i]);
              i += 1;
            }
            i = j + k + this.zzcxb.length * 1;
          }
        }
        j = i;
        if (this.zzcxc != null)
        {
          j = i;
          if (this.zzcxc.length > 0)
          {
            j = 0;
            k = 0;
            while (j < this.zzcxc.length)
            {
              k += zzbyc.zzbq(this.zzcxc[j]);
              j += 1;
            }
            j = i + k + this.zzcxc.length * 1;
          }
        }
        i = j;
        if (this.zzcxd != null)
        {
          i = j;
          if (this.zzcxd.length > 0)
          {
            k = 0;
            i = i2;
            while (i < this.zzcxd.length)
            {
              k += zzbyc.zzbq(this.zzcxd[i]);
              i += 1;
            }
            i = j + k + this.zzcxd.length * 1;
          }
        }
        return i;
      }
    }
  }
  
  public static final class zzb
    extends zzbyd<zzb>
    implements Cloneable
  {
    public String version;
    public int zzbqV;
    public String zzcxe;
    
    public zzb()
    {
      zzafF();
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      boolean bool1;
      if (paramObject == this) {
        bool1 = true;
      }
      label54:
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
          } while (this.zzbqV != ((zzb)paramObject).zzbqV);
          if (this.zzcxe != null) {
            break;
          }
          bool1 = bool2;
        } while (((zzb)paramObject).zzcxe != null);
        if (this.version != null) {
          break label124;
        }
        bool1 = bool2;
      } while (((zzb)paramObject).version != null);
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
          if (this.zzcxe.equals(((zzb)paramObject).zzcxe)) {
            break label54;
          }
          return false;
          label124:
          if (!this.version.equals(((zzb)paramObject).version)) {
            return false;
          }
        }
      }
      return this.zzcwC.equals(((zzb)paramObject).zzcwC);
    }
    
    public int hashCode()
    {
      int m = 0;
      int n = getClass().getName().hashCode();
      int i1 = this.zzbqV;
      int i;
      int j;
      if (this.zzcxe == null)
      {
        i = 0;
        if (this.version != null) {
          break label101;
        }
        j = 0;
        label39:
        k = m;
        if (this.zzcwC != null) {
          if (!this.zzcwC.isEmpty()) {
            break label112;
          }
        }
      }
      label101:
      label112:
      for (int k = m;; k = this.zzcwC.hashCode())
      {
        return (j + (i + ((n + 527) * 31 + i1) * 31) * 31) * 31 + k;
        i = this.zzcxe.hashCode();
        break;
        j = this.version.hashCode();
        break label39;
      }
    }
    
    public void zza(zzbyc paramzzbyc)
      throws IOException
    {
      if (this.zzbqV != 0) {
        paramzzbyc.zzJ(1, this.zzbqV);
      }
      if ((this.zzcxe != null) && (!this.zzcxe.equals(""))) {
        paramzzbyc.zzq(2, this.zzcxe);
      }
      if ((this.version != null) && (!this.version.equals(""))) {
        paramzzbyc.zzq(3, this.version);
      }
      super.zza(paramzzbyc);
    }
    
    public zzb zzaX(zzbyb paramzzbyb)
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
          this.zzbqV = paramzzbyb.zzafa();
          break;
        case 18: 
          this.zzcxe = paramzzbyb.readString();
          break;
        case 26: 
          this.version = paramzzbyb.readString();
        }
      }
    }
    
    public zzb zzafF()
    {
      this.zzbqV = 0;
      this.zzcxe = "";
      this.version = "";
      this.zzcwC = null;
      this.zzcwL = -1;
      return this;
    }
    
    public zzb zzafG()
    {
      try
      {
        zzb localzzb = (zzb)super.zzafp();
        return localzzb;
      }
      catch (CloneNotSupportedException localCloneNotSupportedException)
      {
        throw new AssertionError(localCloneNotSupportedException);
      }
    }
    
    protected int zzu()
    {
      int j = super.zzu();
      int i = j;
      if (this.zzbqV != 0) {
        i = j + zzbyc.zzL(1, this.zzbqV);
      }
      j = i;
      if (this.zzcxe != null)
      {
        j = i;
        if (!this.zzcxe.equals("")) {
          j = i + zzbyc.zzr(2, this.zzcxe);
        }
      }
      i = j;
      if (this.version != null)
      {
        i = j;
        if (!this.version.equals("")) {
          i = j + zzbyc.zzr(3, this.version);
        }
      }
      return i;
    }
  }
  
  public static final class zzc
    extends zzbyd<zzc>
    implements Cloneable
  {
    public byte[] zzcxf;
    public String zzcxg;
    public byte[][] zzcxh;
    public boolean zzcxi;
    
    public zzc()
    {
      zzafH();
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
          } while (!(paramObject instanceof zzc));
          paramObject = (zzc)paramObject;
          bool1 = bool2;
        } while (!Arrays.equals(this.zzcxf, ((zzc)paramObject).zzcxf));
        if (this.zzcxg != null) {
          break;
        }
        bool1 = bool2;
      } while (((zzc)paramObject).zzcxg != null);
      while (this.zzcxg.equals(((zzc)paramObject).zzcxg))
      {
        bool1 = bool2;
        if (!zzbyh.zza(this.zzcxh, ((zzc)paramObject).zzcxh)) {
          break;
        }
        bool1 = bool2;
        if (this.zzcxi != ((zzc)paramObject).zzcxi) {
          break;
        }
        if ((this.zzcwC != null) && (!this.zzcwC.isEmpty())) {
          break label140;
        }
        if (((zzc)paramObject).zzcwC != null)
        {
          bool1 = bool2;
          if (!((zzc)paramObject).zzcwC.isEmpty()) {
            break;
          }
        }
        return true;
      }
      return false;
      label140:
      return this.zzcwC.equals(((zzc)paramObject).zzcwC);
    }
    
    public int hashCode()
    {
      int m = 0;
      int n = getClass().getName().hashCode();
      int i1 = Arrays.hashCode(this.zzcxf);
      int i;
      int i2;
      int j;
      if (this.zzcxg == null)
      {
        i = 0;
        i2 = zzbyh.zzb(this.zzcxh);
        if (!this.zzcxi) {
          break label121;
        }
        j = 1231;
        label53:
        k = m;
        if (this.zzcwC != null) {
          if (!this.zzcwC.isEmpty()) {
            break label128;
          }
        }
      }
      label121:
      label128:
      for (int k = m;; k = this.zzcwC.hashCode())
      {
        return (j + ((i + ((n + 527) * 31 + i1) * 31) * 31 + i2) * 31) * 31 + k;
        i = this.zzcxg.hashCode();
        break;
        j = 1237;
        break label53;
      }
    }
    
    public void zza(zzbyc paramzzbyc)
      throws IOException
    {
      if (!Arrays.equals(this.zzcxf, zzbym.zzcwW)) {
        paramzzbyc.zzb(1, this.zzcxf);
      }
      if ((this.zzcxh != null) && (this.zzcxh.length > 0))
      {
        int i = 0;
        while (i < this.zzcxh.length)
        {
          byte[] arrayOfByte = this.zzcxh[i];
          if (arrayOfByte != null) {
            paramzzbyc.zzb(2, arrayOfByte);
          }
          i += 1;
        }
      }
      if (this.zzcxi) {
        paramzzbyc.zzg(3, this.zzcxi);
      }
      if ((this.zzcxg != null) && (!this.zzcxg.equals(""))) {
        paramzzbyc.zzq(4, this.zzcxg);
      }
      super.zza(paramzzbyc);
    }
    
    public zzc zzaY(zzbyb paramzzbyb)
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
          this.zzcxf = paramzzbyb.readBytes();
          break;
        case 18: 
          int j = zzbym.zzb(paramzzbyb, 18);
          if (this.zzcxh == null) {}
          byte[][] arrayOfByte;
          for (i = 0;; i = this.zzcxh.length)
          {
            arrayOfByte = new byte[j + i][];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzcxh, 0, arrayOfByte, 0, i);
              j = i;
            }
            while (j < arrayOfByte.length - 1)
            {
              arrayOfByte[j] = paramzzbyb.readBytes();
              paramzzbyb.zzaeW();
              j += 1;
            }
          }
          arrayOfByte[j] = paramzzbyb.readBytes();
          this.zzcxh = arrayOfByte;
          break;
        case 24: 
          this.zzcxi = paramzzbyb.zzafc();
          break;
        case 34: 
          this.zzcxg = paramzzbyb.readString();
        }
      }
    }
    
    public zzc zzafH()
    {
      this.zzcxf = zzbym.zzcwW;
      this.zzcxg = "";
      this.zzcxh = zzbym.zzcwV;
      this.zzcxi = false;
      this.zzcwC = null;
      this.zzcwL = -1;
      return this;
    }
    
    public zzc zzafI()
    {
      try
      {
        zzc localzzc = (zzc)super.zzafp();
        if ((this.zzcxh != null) && (this.zzcxh.length > 0)) {
          localzzc.zzcxh = ((byte[][])this.zzcxh.clone());
        }
        return localzzc;
      }
      catch (CloneNotSupportedException localCloneNotSupportedException)
      {
        throw new AssertionError(localCloneNotSupportedException);
      }
    }
    
    protected int zzu()
    {
      int n = 0;
      int j = super.zzu();
      int i = j;
      if (!Arrays.equals(this.zzcxf, zzbym.zzcwW)) {
        i = j + zzbyc.zzc(1, this.zzcxf);
      }
      j = i;
      if (this.zzcxh != null)
      {
        j = i;
        if (this.zzcxh.length > 0)
        {
          int k = 0;
          int m = 0;
          j = n;
          while (j < this.zzcxh.length)
          {
            byte[] arrayOfByte = this.zzcxh[j];
            int i1 = k;
            n = m;
            if (arrayOfByte != null)
            {
              n = m + 1;
              i1 = k + zzbyc.zzaj(arrayOfByte);
            }
            j += 1;
            k = i1;
            m = n;
          }
          j = i + k + m * 1;
        }
      }
      i = j;
      if (this.zzcxi) {
        i = j + zzbyc.zzh(3, this.zzcxi);
      }
      j = i;
      if (this.zzcxg != null)
      {
        j = i;
        if (!this.zzcxg.equals("")) {
          j = i + zzbyc.zzr(4, this.zzcxg);
        }
      }
      return j;
    }
  }
  
  public static final class zzd
    extends zzbyd<zzd>
    implements Cloneable
  {
    public String tag;
    public boolean zzced;
    public zzbyo.zzf zzcnt;
    public int[] zzcxA;
    public long zzcxB;
    public long zzcxj;
    public long zzcxk;
    public long zzcxl;
    public int zzcxm;
    public zzbyo.zze[] zzcxn;
    public byte[] zzcxo;
    public zzbyo.zzb zzcxp;
    public byte[] zzcxq;
    public String zzcxr;
    public String zzcxs;
    public zzbyo.zza zzcxt;
    public String zzcxu;
    public long zzcxv;
    public zzbyo.zzc zzcxw;
    public byte[] zzcxx;
    public String zzcxy;
    public int zzcxz;
    public int zzri;
    
    public zzd()
    {
      zzafJ();
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      boolean bool1;
      if (paramObject == this) {
        bool1 = true;
      }
      label83:
      label170:
      label202:
      label218:
      label234:
      label250:
      label280:
      label312:
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
                                                    } while (!(paramObject instanceof zzd));
                                                    paramObject = (zzd)paramObject;
                                                    bool1 = bool2;
                                                  } while (this.zzcxj != ((zzd)paramObject).zzcxj);
                                                  bool1 = bool2;
                                                } while (this.zzcxk != ((zzd)paramObject).zzcxk);
                                                bool1 = bool2;
                                              } while (this.zzcxl != ((zzd)paramObject).zzcxl);
                                              if (this.tag != null) {
                                                break;
                                              }
                                              bool1 = bool2;
                                            } while (((zzd)paramObject).tag != null);
                                            bool1 = bool2;
                                          } while (this.zzcxm != ((zzd)paramObject).zzcxm);
                                          bool1 = bool2;
                                        } while (this.zzri != ((zzd)paramObject).zzri);
                                        bool1 = bool2;
                                      } while (this.zzced != ((zzd)paramObject).zzced);
                                      bool1 = bool2;
                                    } while (!zzbyh.equals(this.zzcxn, ((zzd)paramObject).zzcxn));
                                    bool1 = bool2;
                                  } while (!Arrays.equals(this.zzcxo, ((zzd)paramObject).zzcxo));
                                  if (this.zzcxp != null) {
                                    break label425;
                                  }
                                  bool1 = bool2;
                                } while (((zzd)paramObject).zzcxp != null);
                                bool1 = bool2;
                              } while (!Arrays.equals(this.zzcxq, ((zzd)paramObject).zzcxq));
                              if (this.zzcxr != null) {
                                break label441;
                              }
                              bool1 = bool2;
                            } while (((zzd)paramObject).zzcxr != null);
                            if (this.zzcxs != null) {
                              break label457;
                            }
                            bool1 = bool2;
                          } while (((zzd)paramObject).zzcxs != null);
                          if (this.zzcxt != null) {
                            break label473;
                          }
                          bool1 = bool2;
                        } while (((zzd)paramObject).zzcxt != null);
                        if (this.zzcxu != null) {
                          break label489;
                        }
                        bool1 = bool2;
                      } while (((zzd)paramObject).zzcxu != null);
                      bool1 = bool2;
                    } while (this.zzcxv != ((zzd)paramObject).zzcxv);
                    if (this.zzcxw != null) {
                      break label505;
                    }
                    bool1 = bool2;
                  } while (((zzd)paramObject).zzcxw != null);
                  bool1 = bool2;
                } while (!Arrays.equals(this.zzcxx, ((zzd)paramObject).zzcxx));
                if (this.zzcxy != null) {
                  break label521;
                }
                bool1 = bool2;
              } while (((zzd)paramObject).zzcxy != null);
              bool1 = bool2;
            } while (this.zzcxz != ((zzd)paramObject).zzcxz);
            bool1 = bool2;
          } while (!zzbyh.equals(this.zzcxA, ((zzd)paramObject).zzcxA));
          bool1 = bool2;
        } while (this.zzcxB != ((zzd)paramObject).zzcxB);
        if (this.zzcnt != null) {
          break label537;
        }
        bool1 = bool2;
      } while (((zzd)paramObject).zzcnt != null);
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
          if (this.tag.equals(((zzd)paramObject).tag)) {
            break label83;
          }
          return false;
          label425:
          if (this.zzcxp.equals(((zzd)paramObject).zzcxp)) {
            break label170;
          }
          return false;
          label441:
          if (this.zzcxr.equals(((zzd)paramObject).zzcxr)) {
            break label202;
          }
          return false;
          label457:
          if (this.zzcxs.equals(((zzd)paramObject).zzcxs)) {
            break label218;
          }
          return false;
          label473:
          if (this.zzcxt.equals(((zzd)paramObject).zzcxt)) {
            break label234;
          }
          return false;
          label489:
          if (this.zzcxu.equals(((zzd)paramObject).zzcxu)) {
            break label250;
          }
          return false;
          label505:
          if (this.zzcxw.equals(((zzd)paramObject).zzcxw)) {
            break label280;
          }
          return false;
          label521:
          if (this.zzcxy.equals(((zzd)paramObject).zzcxy)) {
            break label312;
          }
          return false;
          label537:
          if (!this.zzcnt.equals(((zzd)paramObject).zzcnt)) {
            return false;
          }
        }
      }
      return this.zzcwC.equals(((zzd)paramObject).zzcwC);
    }
    
    public int hashCode()
    {
      int i7 = 0;
      int i8 = getClass().getName().hashCode();
      int i9 = (int)(this.zzcxj ^ this.zzcxj >>> 32);
      int i10 = (int)(this.zzcxk ^ this.zzcxk >>> 32);
      int i11 = (int)(this.zzcxl ^ this.zzcxl >>> 32);
      int i;
      int i12;
      int i13;
      int j;
      label92:
      int i14;
      int i15;
      int k;
      label119:
      int i16;
      int m;
      label138:
      int n;
      label148:
      int i1;
      label158:
      int i2;
      label168:
      int i17;
      int i3;
      label193:
      int i18;
      int i4;
      label212:
      int i19;
      int i20;
      int i21;
      int i5;
      if (this.tag == null)
      {
        i = 0;
        i12 = this.zzcxm;
        i13 = this.zzri;
        if (!this.zzced) {
          break label436;
        }
        j = 1231;
        i14 = zzbyh.hashCode(this.zzcxn);
        i15 = Arrays.hashCode(this.zzcxo);
        if (this.zzcxp != null) {
          break label443;
        }
        k = 0;
        i16 = Arrays.hashCode(this.zzcxq);
        if (this.zzcxr != null) {
          break label454;
        }
        m = 0;
        if (this.zzcxs != null) {
          break label466;
        }
        n = 0;
        if (this.zzcxt != null) {
          break label478;
        }
        i1 = 0;
        if (this.zzcxu != null) {
          break label490;
        }
        i2 = 0;
        i17 = (int)(this.zzcxv ^ this.zzcxv >>> 32);
        if (this.zzcxw != null) {
          break label502;
        }
        i3 = 0;
        i18 = Arrays.hashCode(this.zzcxx);
        if (this.zzcxy != null) {
          break label514;
        }
        i4 = 0;
        i19 = this.zzcxz;
        i20 = zzbyh.hashCode(this.zzcxA);
        i21 = (int)(this.zzcxB ^ this.zzcxB >>> 32);
        if (this.zzcnt != null) {
          break label526;
        }
        i5 = 0;
        label252:
        i6 = i7;
        if (this.zzcwC != null) {
          if (!this.zzcwC.isEmpty()) {
            break label538;
          }
        }
      }
      label436:
      label443:
      label454:
      label466:
      label478:
      label490:
      label502:
      label514:
      label526:
      label538:
      for (int i6 = i7;; i6 = this.zzcwC.hashCode())
      {
        return (i5 + ((((i4 + ((i3 + ((i2 + (i1 + (n + (m + ((k + (((j + (((i + ((((i8 + 527) * 31 + i9) * 31 + i10) * 31 + i11) * 31) * 31 + i12) * 31 + i13) * 31) * 31 + i14) * 31 + i15) * 31) * 31 + i16) * 31) * 31) * 31) * 31) * 31 + i17) * 31) * 31 + i18) * 31) * 31 + i19) * 31 + i20) * 31 + i21) * 31) * 31 + i6;
        i = this.tag.hashCode();
        break;
        j = 1237;
        break label92;
        k = this.zzcxp.hashCode();
        break label119;
        m = this.zzcxr.hashCode();
        break label138;
        n = this.zzcxs.hashCode();
        break label148;
        i1 = this.zzcxt.hashCode();
        break label158;
        i2 = this.zzcxu.hashCode();
        break label168;
        i3 = this.zzcxw.hashCode();
        break label193;
        i4 = this.zzcxy.hashCode();
        break label212;
        i5 = this.zzcnt.hashCode();
        break label252;
      }
    }
    
    public void zza(zzbyc paramzzbyc)
      throws IOException
    {
      int j = 0;
      if (this.zzcxj != 0L) {
        paramzzbyc.zzb(1, this.zzcxj);
      }
      if ((this.tag != null) && (!this.tag.equals(""))) {
        paramzzbyc.zzq(2, this.tag);
      }
      int i;
      if ((this.zzcxn != null) && (this.zzcxn.length > 0))
      {
        i = 0;
        while (i < this.zzcxn.length)
        {
          zzbyo.zze localzze = this.zzcxn[i];
          if (localzze != null) {
            paramzzbyc.zza(3, localzze);
          }
          i += 1;
        }
      }
      if (!Arrays.equals(this.zzcxo, zzbym.zzcwW)) {
        paramzzbyc.zzb(4, this.zzcxo);
      }
      if (!Arrays.equals(this.zzcxq, zzbym.zzcwW)) {
        paramzzbyc.zzb(6, this.zzcxq);
      }
      if (this.zzcxt != null) {
        paramzzbyc.zza(7, this.zzcxt);
      }
      if ((this.zzcxr != null) && (!this.zzcxr.equals(""))) {
        paramzzbyc.zzq(8, this.zzcxr);
      }
      if (this.zzcxp != null) {
        paramzzbyc.zza(9, this.zzcxp);
      }
      if (this.zzced) {
        paramzzbyc.zzg(10, this.zzced);
      }
      if (this.zzcxm != 0) {
        paramzzbyc.zzJ(11, this.zzcxm);
      }
      if (this.zzri != 0) {
        paramzzbyc.zzJ(12, this.zzri);
      }
      if ((this.zzcxs != null) && (!this.zzcxs.equals(""))) {
        paramzzbyc.zzq(13, this.zzcxs);
      }
      if ((this.zzcxu != null) && (!this.zzcxu.equals(""))) {
        paramzzbyc.zzq(14, this.zzcxu);
      }
      if (this.zzcxv != 180000L) {
        paramzzbyc.zzd(15, this.zzcxv);
      }
      if (this.zzcxw != null) {
        paramzzbyc.zza(16, this.zzcxw);
      }
      if (this.zzcxk != 0L) {
        paramzzbyc.zzb(17, this.zzcxk);
      }
      if (!Arrays.equals(this.zzcxx, zzbym.zzcwW)) {
        paramzzbyc.zzb(18, this.zzcxx);
      }
      if (this.zzcxz != 0) {
        paramzzbyc.zzJ(19, this.zzcxz);
      }
      if ((this.zzcxA != null) && (this.zzcxA.length > 0))
      {
        i = j;
        while (i < this.zzcxA.length)
        {
          paramzzbyc.zzJ(20, this.zzcxA[i]);
          i += 1;
        }
      }
      if (this.zzcxl != 0L) {
        paramzzbyc.zzb(21, this.zzcxl);
      }
      if (this.zzcxB != 0L) {
        paramzzbyc.zzb(22, this.zzcxB);
      }
      if (this.zzcnt != null) {
        paramzzbyc.zza(23, this.zzcnt);
      }
      if ((this.zzcxy != null) && (!this.zzcxy.equals(""))) {
        paramzzbyc.zzq(24, this.zzcxy);
      }
      super.zza(paramzzbyc);
    }
    
    public zzd zzaZ(zzbyb paramzzbyb)
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
          this.zzcxj = paramzzbyb.zzaeZ();
          break;
        case 18: 
          this.tag = paramzzbyb.readString();
          break;
        case 26: 
          j = zzbym.zzb(paramzzbyb, 26);
          if (this.zzcxn == null) {}
          for (i = 0;; i = this.zzcxn.length)
          {
            localObject = new zzbyo.zze[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzcxn, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length - 1)
            {
              localObject[j] = new zzbyo.zze();
              paramzzbyb.zza(localObject[j]);
              paramzzbyb.zzaeW();
              j += 1;
            }
          }
          localObject[j] = new zzbyo.zze();
          paramzzbyb.zza(localObject[j]);
          this.zzcxn = ((zzbyo.zze[])localObject);
          break;
        case 34: 
          this.zzcxo = paramzzbyb.readBytes();
          break;
        case 50: 
          this.zzcxq = paramzzbyb.readBytes();
          break;
        case 58: 
          if (this.zzcxt == null) {
            this.zzcxt = new zzbyo.zza();
          }
          paramzzbyb.zza(this.zzcxt);
          break;
        case 66: 
          this.zzcxr = paramzzbyb.readString();
          break;
        case 74: 
          if (this.zzcxp == null) {
            this.zzcxp = new zzbyo.zzb();
          }
          paramzzbyb.zza(this.zzcxp);
          break;
        case 80: 
          this.zzced = paramzzbyb.zzafc();
          break;
        case 88: 
          this.zzcxm = paramzzbyb.zzafa();
          break;
        case 96: 
          this.zzri = paramzzbyb.zzafa();
          break;
        case 106: 
          this.zzcxs = paramzzbyb.readString();
          break;
        case 114: 
          this.zzcxu = paramzzbyb.readString();
          break;
        case 120: 
          this.zzcxv = paramzzbyb.zzafe();
          break;
        case 130: 
          if (this.zzcxw == null) {
            this.zzcxw = new zzbyo.zzc();
          }
          paramzzbyb.zza(this.zzcxw);
          break;
        case 136: 
          this.zzcxk = paramzzbyb.zzaeZ();
          break;
        case 146: 
          this.zzcxx = paramzzbyb.readBytes();
          break;
        case 152: 
          i = paramzzbyb.zzafa();
          switch (i)
          {
          default: 
            break;
          case 0: 
          case 1: 
          case 2: 
            this.zzcxz = i;
          }
          break;
        case 160: 
          j = zzbym.zzb(paramzzbyb, 160);
          if (this.zzcxA == null) {}
          for (i = 0;; i = this.zzcxA.length)
          {
            localObject = new int[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzcxA, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length - 1)
            {
              localObject[j] = paramzzbyb.zzafa();
              paramzzbyb.zzaeW();
              j += 1;
            }
          }
          localObject[j] = paramzzbyb.zzafa();
          this.zzcxA = ((int[])localObject);
          break;
        case 162: 
          int k = paramzzbyb.zzrf(paramzzbyb.zzaff());
          i = paramzzbyb.getPosition();
          j = 0;
          while (paramzzbyb.zzafk() > 0)
          {
            paramzzbyb.zzafa();
            j += 1;
          }
          paramzzbyb.zzrh(i);
          if (this.zzcxA == null) {}
          for (i = 0;; i = this.zzcxA.length)
          {
            localObject = new int[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzcxA, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length)
            {
              localObject[j] = paramzzbyb.zzafa();
              j += 1;
            }
          }
          this.zzcxA = ((int[])localObject);
          paramzzbyb.zzrg(k);
          break;
        case 168: 
          this.zzcxl = paramzzbyb.zzaeZ();
          break;
        case 176: 
          this.zzcxB = paramzzbyb.zzaeZ();
          break;
        case 186: 
          if (this.zzcnt == null) {
            this.zzcnt = new zzbyo.zzf();
          }
          paramzzbyb.zza(this.zzcnt);
          break;
        case 194: 
          this.zzcxy = paramzzbyb.readString();
        }
      }
    }
    
    public zzd zzafJ()
    {
      this.zzcxj = 0L;
      this.zzcxk = 0L;
      this.zzcxl = 0L;
      this.tag = "";
      this.zzcxm = 0;
      this.zzri = 0;
      this.zzced = false;
      this.zzcxn = zzbyo.zze.zzafL();
      this.zzcxo = zzbym.zzcwW;
      this.zzcxp = null;
      this.zzcxq = zzbym.zzcwW;
      this.zzcxr = "";
      this.zzcxs = "";
      this.zzcxt = null;
      this.zzcxu = "";
      this.zzcxv = 180000L;
      this.zzcxw = null;
      this.zzcxx = zzbym.zzcwW;
      this.zzcxy = "";
      this.zzcxz = 0;
      this.zzcxA = zzbym.zzcwQ;
      this.zzcxB = 0L;
      this.zzcnt = null;
      this.zzcwC = null;
      this.zzcwL = -1;
      return this;
    }
    
    public zzd zzafK()
    {
      try
      {
        zzd localzzd = (zzd)super.zzafp();
        if ((this.zzcxn != null) && (this.zzcxn.length > 0))
        {
          localzzd.zzcxn = new zzbyo.zze[this.zzcxn.length];
          int i = 0;
          while (i < this.zzcxn.length)
          {
            if (this.zzcxn[i] != null) {
              localzzd.zzcxn[i] = ((zzbyo.zze)this.zzcxn[i].clone());
            }
            i += 1;
          }
        }
        if (this.zzcxp == null) {
          break label111;
        }
      }
      catch (CloneNotSupportedException localCloneNotSupportedException)
      {
        throw new AssertionError(localCloneNotSupportedException);
      }
      localCloneNotSupportedException.zzcxp = ((zzbyo.zzb)this.zzcxp.clone());
      label111:
      if (this.zzcxt != null) {
        localCloneNotSupportedException.zzcxt = ((zzbyo.zza)this.zzcxt.clone());
      }
      if (this.zzcxw != null) {
        localCloneNotSupportedException.zzcxw = ((zzbyo.zzc)this.zzcxw.clone());
      }
      if ((this.zzcxA != null) && (this.zzcxA.length > 0)) {
        localCloneNotSupportedException.zzcxA = ((int[])this.zzcxA.clone());
      }
      if (this.zzcnt != null) {
        localCloneNotSupportedException.zzcnt = ((zzbyo.zzf)this.zzcnt.clone());
      }
      return localCloneNotSupportedException;
    }
    
    protected int zzu()
    {
      int m = 0;
      int i = super.zzu();
      int j = i;
      if (this.zzcxj != 0L) {
        j = i + zzbyc.zzf(1, this.zzcxj);
      }
      i = j;
      if (this.tag != null)
      {
        i = j;
        if (!this.tag.equals("")) {
          i = j + zzbyc.zzr(2, this.tag);
        }
      }
      j = i;
      int k;
      if (this.zzcxn != null)
      {
        j = i;
        if (this.zzcxn.length > 0)
        {
          j = 0;
          while (j < this.zzcxn.length)
          {
            zzbyo.zze localzze = this.zzcxn[j];
            k = i;
            if (localzze != null) {
              k = i + zzbyc.zzc(3, localzze);
            }
            j += 1;
            i = k;
          }
          j = i;
        }
      }
      i = j;
      if (!Arrays.equals(this.zzcxo, zzbym.zzcwW)) {
        i = j + zzbyc.zzc(4, this.zzcxo);
      }
      j = i;
      if (!Arrays.equals(this.zzcxq, zzbym.zzcwW)) {
        j = i + zzbyc.zzc(6, this.zzcxq);
      }
      i = j;
      if (this.zzcxt != null) {
        i = j + zzbyc.zzc(7, this.zzcxt);
      }
      j = i;
      if (this.zzcxr != null)
      {
        j = i;
        if (!this.zzcxr.equals("")) {
          j = i + zzbyc.zzr(8, this.zzcxr);
        }
      }
      i = j;
      if (this.zzcxp != null) {
        i = j + zzbyc.zzc(9, this.zzcxp);
      }
      j = i;
      if (this.zzced) {
        j = i + zzbyc.zzh(10, this.zzced);
      }
      i = j;
      if (this.zzcxm != 0) {
        i = j + zzbyc.zzL(11, this.zzcxm);
      }
      j = i;
      if (this.zzri != 0) {
        j = i + zzbyc.zzL(12, this.zzri);
      }
      i = j;
      if (this.zzcxs != null)
      {
        i = j;
        if (!this.zzcxs.equals("")) {
          i = j + zzbyc.zzr(13, this.zzcxs);
        }
      }
      j = i;
      if (this.zzcxu != null)
      {
        j = i;
        if (!this.zzcxu.equals("")) {
          j = i + zzbyc.zzr(14, this.zzcxu);
        }
      }
      i = j;
      if (this.zzcxv != 180000L) {
        i = j + zzbyc.zzh(15, this.zzcxv);
      }
      j = i;
      if (this.zzcxw != null) {
        j = i + zzbyc.zzc(16, this.zzcxw);
      }
      i = j;
      if (this.zzcxk != 0L) {
        i = j + zzbyc.zzf(17, this.zzcxk);
      }
      j = i;
      if (!Arrays.equals(this.zzcxx, zzbym.zzcwW)) {
        j = i + zzbyc.zzc(18, this.zzcxx);
      }
      i = j;
      if (this.zzcxz != 0) {
        i = j + zzbyc.zzL(19, this.zzcxz);
      }
      j = i;
      if (this.zzcxA != null)
      {
        j = i;
        if (this.zzcxA.length > 0)
        {
          k = 0;
          j = m;
          while (j < this.zzcxA.length)
          {
            k += zzbyc.zzrl(this.zzcxA[j]);
            j += 1;
          }
          j = i + k + this.zzcxA.length * 2;
        }
      }
      i = j;
      if (this.zzcxl != 0L) {
        i = j + zzbyc.zzf(21, this.zzcxl);
      }
      j = i;
      if (this.zzcxB != 0L) {
        j = i + zzbyc.zzf(22, this.zzcxB);
      }
      i = j;
      if (this.zzcnt != null) {
        i = j + zzbyc.zzc(23, this.zzcnt);
      }
      j = i;
      if (this.zzcxy != null)
      {
        j = i;
        if (!this.zzcxy.equals("")) {
          j = i + zzbyc.zzr(24, this.zzcxy);
        }
      }
      return j;
    }
  }
  
  public static final class zze
    extends zzbyd<zze>
    implements Cloneable
  {
    private static volatile zze[] zzcxC;
    public String value;
    public String zzaB;
    
    public zze()
    {
      zzafM();
    }
    
    public static zze[] zzafL()
    {
      if (zzcxC == null) {}
      synchronized (zzbyh.zzcwK)
      {
        if (zzcxC == null) {
          zzcxC = new zze[0];
        }
        return zzcxC;
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
          } while (!(paramObject instanceof zze));
          paramObject = (zze)paramObject;
          if (this.zzaB != null) {
            break;
          }
          bool1 = bool2;
        } while (((zze)paramObject).zzaB != null);
        if (this.value != null) {
          break label111;
        }
        bool1 = bool2;
      } while (((zze)paramObject).value != null);
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
          if (this.zzaB.equals(((zze)paramObject).zzaB)) {
            break label41;
          }
          return false;
          label111:
          if (!this.value.equals(((zze)paramObject).value)) {
            return false;
          }
        }
      }
      return this.zzcwC.equals(((zze)paramObject).zzcwC);
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
    
    public void zza(zzbyc paramzzbyc)
      throws IOException
    {
      if ((this.zzaB != null) && (!this.zzaB.equals(""))) {
        paramzzbyc.zzq(1, this.zzaB);
      }
      if ((this.value != null) && (!this.value.equals(""))) {
        paramzzbyc.zzq(2, this.value);
      }
      super.zza(paramzzbyc);
    }
    
    public zze zzafM()
    {
      this.zzaB = "";
      this.value = "";
      this.zzcwC = null;
      this.zzcwL = -1;
      return this;
    }
    
    public zze zzafN()
    {
      try
      {
        zze localzze = (zze)super.zzafp();
        return localzze;
      }
      catch (CloneNotSupportedException localCloneNotSupportedException)
      {
        throw new AssertionError(localCloneNotSupportedException);
      }
    }
    
    public zze zzba(zzbyb paramzzbyb)
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
    
    protected int zzu()
    {
      int j = super.zzu();
      int i = j;
      if (this.zzaB != null)
      {
        i = j;
        if (!this.zzaB.equals("")) {
          i = j + zzbyc.zzr(1, this.zzaB);
        }
      }
      j = i;
      if (this.value != null)
      {
        j = i;
        if (!this.value.equals("")) {
          j = i + zzbyc.zzr(2, this.value);
        }
      }
      return j;
    }
  }
  
  public static final class zzf
    extends zzbyd<zzf>
    implements Cloneable
  {
    public int zzcxD;
    public int zzcxE;
    
    public zzf()
    {
      zzafO();
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
          } while (this.zzcxD != ((zzf)paramObject).zzcxD);
          bool1 = bool2;
        } while (this.zzcxE != ((zzf)paramObject).zzcxE);
        if ((this.zzcwC != null) && (!this.zzcwC.isEmpty())) {
          break label89;
        }
        if (((zzf)paramObject).zzcwC == null) {
          break;
        }
        bool1 = bool2;
      } while (!((zzf)paramObject).zzcwC.isEmpty());
      return true;
      label89:
      return this.zzcwC.equals(((zzf)paramObject).zzcwC);
    }
    
    public int hashCode()
    {
      int j = getClass().getName().hashCode();
      int k = this.zzcxD;
      int m = this.zzcxE;
      if ((this.zzcwC == null) || (this.zzcwC.isEmpty())) {}
      for (int i = 0;; i = this.zzcwC.hashCode()) {
        return i + (((j + 527) * 31 + k) * 31 + m) * 31;
      }
    }
    
    public void zza(zzbyc paramzzbyc)
      throws IOException
    {
      if (this.zzcxD != -1) {
        paramzzbyc.zzJ(1, this.zzcxD);
      }
      if (this.zzcxE != 0) {
        paramzzbyc.zzJ(2, this.zzcxE);
      }
      super.zza(paramzzbyc);
    }
    
    public zzf zzafO()
    {
      this.zzcxD = -1;
      this.zzcxE = 0;
      this.zzcwC = null;
      this.zzcwL = -1;
      return this;
    }
    
    public zzf zzafP()
    {
      try
      {
        zzf localzzf = (zzf)super.zzafp();
        return localzzf;
      }
      catch (CloneNotSupportedException localCloneNotSupportedException)
      {
        throw new AssertionError(localCloneNotSupportedException);
      }
    }
    
    public zzf zzbb(zzbyb paramzzbyb)
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
          case -1: 
          case 0: 
          case 1: 
          case 2: 
          case 3: 
          case 4: 
          case 5: 
          case 6: 
          case 7: 
          case 8: 
          case 9: 
          case 10: 
          case 11: 
          case 12: 
          case 13: 
          case 14: 
          case 15: 
          case 16: 
          case 17: 
            this.zzcxD = i;
          }
          break;
        case 16: 
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
          case 7: 
          case 8: 
          case 9: 
          case 10: 
          case 11: 
          case 12: 
          case 13: 
          case 14: 
          case 15: 
          case 16: 
          case 100: 
            this.zzcxE = i;
          }
          break;
        }
      }
    }
    
    protected int zzu()
    {
      int j = super.zzu();
      int i = j;
      if (this.zzcxD != -1) {
        i = j + zzbyc.zzL(1, this.zzcxD);
      }
      j = i;
      if (this.zzcxE != 0) {
        j = i + zzbyc.zzL(2, this.zzcxE);
      }
      return j;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbyo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */