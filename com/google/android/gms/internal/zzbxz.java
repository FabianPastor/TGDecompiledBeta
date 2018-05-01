package com.google.android.gms.internal;

import java.io.IOException;

public abstract interface zzbxz
{
  public static final class zza
    extends zzbxn<zza>
  {
    private static volatile zza[] zzcvO;
    public String zzcvP;
    
    public zza()
    {
      zzafj();
    }
    
    public static zza[] zzafi()
    {
      if (zzcvO == null) {}
      synchronized (zzbxr.zzcuQ)
      {
        if (zzcvO == null) {
          zzcvO = new zza[0];
        }
        return zzcvO;
      }
    }
    
    public void zza(zzbxm paramzzbxm)
      throws IOException
    {
      if ((this.zzcvP != null) && (!this.zzcvP.equals(""))) {
        paramzzbxm.zzq(1, this.zzcvP);
      }
      super.zza(paramzzbxm);
    }
    
    public zza zzaV(zzbxl paramzzbxl)
      throws IOException
    {
      for (;;)
      {
        int i = paramzzbxl.zzaeo();
        switch (i)
        {
        default: 
          if (super.zza(paramzzbxl, i)) {}
          break;
        case 0: 
          return this;
        case 10: 
          this.zzcvP = paramzzbxl.readString();
        }
      }
    }
    
    public zza zzafj()
    {
      this.zzcvP = "";
      this.zzcuI = null;
      this.zzcuR = -1;
      return this;
    }
    
    protected int zzu()
    {
      int j = super.zzu();
      int i = j;
      if (this.zzcvP != null)
      {
        i = j;
        if (!this.zzcvP.equals("")) {
          i = j + zzbxm.zzr(1, this.zzcvP);
        }
      }
      return i;
    }
  }
  
  public static final class zzb
    extends zzbxn<zzb>
  {
    public String zzcvP;
    public String zzcvQ;
    public long zzcvR;
    public String zzcvS;
    public int zzcvT;
    public int zzcvU;
    public String zzcvV;
    public String zzcvW;
    public String zzcvX;
    public String zzcvY;
    public String zzcvZ;
    public int zzcwa;
    public zzbxz.zza[] zzcwb;
    
    public zzb()
    {
      zzafk();
    }
    
    public static zzb zzak(byte[] paramArrayOfByte)
      throws zzbxs
    {
      return (zzb)zzbxt.zza(new zzb(), paramArrayOfByte);
    }
    
    public void zza(zzbxm paramzzbxm)
      throws IOException
    {
      if ((this.zzcvP != null) && (!this.zzcvP.equals(""))) {
        paramzzbxm.zzq(1, this.zzcvP);
      }
      if ((this.zzcvQ != null) && (!this.zzcvQ.equals(""))) {
        paramzzbxm.zzq(2, this.zzcvQ);
      }
      if (this.zzcvR != 0L) {
        paramzzbxm.zzb(3, this.zzcvR);
      }
      if ((this.zzcvS != null) && (!this.zzcvS.equals(""))) {
        paramzzbxm.zzq(4, this.zzcvS);
      }
      if (this.zzcvT != 0) {
        paramzzbxm.zzJ(5, this.zzcvT);
      }
      if (this.zzcvU != 0) {
        paramzzbxm.zzJ(6, this.zzcvU);
      }
      if ((this.zzcvV != null) && (!this.zzcvV.equals(""))) {
        paramzzbxm.zzq(7, this.zzcvV);
      }
      if ((this.zzcvW != null) && (!this.zzcvW.equals(""))) {
        paramzzbxm.zzq(8, this.zzcvW);
      }
      if ((this.zzcvX != null) && (!this.zzcvX.equals(""))) {
        paramzzbxm.zzq(9, this.zzcvX);
      }
      if ((this.zzcvY != null) && (!this.zzcvY.equals(""))) {
        paramzzbxm.zzq(10, this.zzcvY);
      }
      if ((this.zzcvZ != null) && (!this.zzcvZ.equals(""))) {
        paramzzbxm.zzq(11, this.zzcvZ);
      }
      if (this.zzcwa != 0) {
        paramzzbxm.zzJ(12, this.zzcwa);
      }
      if ((this.zzcwb != null) && (this.zzcwb.length > 0))
      {
        int i = 0;
        while (i < this.zzcwb.length)
        {
          zzbxz.zza localzza = this.zzcwb[i];
          if (localzza != null) {
            paramzzbxm.zza(13, localzza);
          }
          i += 1;
        }
      }
      super.zza(paramzzbxm);
    }
    
    public zzb zzaW(zzbxl paramzzbxl)
      throws IOException
    {
      for (;;)
      {
        int i = paramzzbxl.zzaeo();
        switch (i)
        {
        default: 
          if (super.zza(paramzzbxl, i)) {}
          break;
        case 0: 
          return this;
        case 10: 
          this.zzcvP = paramzzbxl.readString();
          break;
        case 18: 
          this.zzcvQ = paramzzbxl.readString();
          break;
        case 24: 
          this.zzcvR = paramzzbxl.zzaer();
          break;
        case 34: 
          this.zzcvS = paramzzbxl.readString();
          break;
        case 40: 
          this.zzcvT = paramzzbxl.zzaes();
          break;
        case 48: 
          this.zzcvU = paramzzbxl.zzaes();
          break;
        case 58: 
          this.zzcvV = paramzzbxl.readString();
          break;
        case 66: 
          this.zzcvW = paramzzbxl.readString();
          break;
        case 74: 
          this.zzcvX = paramzzbxl.readString();
          break;
        case 82: 
          this.zzcvY = paramzzbxl.readString();
          break;
        case 90: 
          this.zzcvZ = paramzzbxl.readString();
          break;
        case 96: 
          i = paramzzbxl.zzaes();
          switch (i)
          {
          default: 
            break;
          case 0: 
          case 1: 
          case 2: 
            this.zzcwa = i;
          }
          break;
        case 106: 
          int j = zzbxw.zzb(paramzzbxl, 106);
          if (this.zzcwb == null) {}
          zzbxz.zza[] arrayOfzza;
          for (i = 0;; i = this.zzcwb.length)
          {
            arrayOfzza = new zzbxz.zza[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzcwb, 0, arrayOfzza, 0, i);
              j = i;
            }
            while (j < arrayOfzza.length - 1)
            {
              arrayOfzza[j] = new zzbxz.zza();
              paramzzbxl.zza(arrayOfzza[j]);
              paramzzbxl.zzaeo();
              j += 1;
            }
          }
          arrayOfzza[j] = new zzbxz.zza();
          paramzzbxl.zza(arrayOfzza[j]);
          this.zzcwb = arrayOfzza;
        }
      }
    }
    
    public zzb zzafk()
    {
      this.zzcvP = "";
      this.zzcvQ = "";
      this.zzcvR = 0L;
      this.zzcvS = "";
      this.zzcvT = 0;
      this.zzcvU = 0;
      this.zzcvV = "";
      this.zzcvW = "";
      this.zzcvX = "";
      this.zzcvY = "";
      this.zzcvZ = "";
      this.zzcwa = 0;
      this.zzcwb = zzbxz.zza.zzafi();
      this.zzcuI = null;
      this.zzcuR = -1;
      return this;
    }
    
    protected int zzu()
    {
      int j = super.zzu();
      int i = j;
      if (this.zzcvP != null)
      {
        i = j;
        if (!this.zzcvP.equals("")) {
          i = j + zzbxm.zzr(1, this.zzcvP);
        }
      }
      j = i;
      if (this.zzcvQ != null)
      {
        j = i;
        if (!this.zzcvQ.equals("")) {
          j = i + zzbxm.zzr(2, this.zzcvQ);
        }
      }
      i = j;
      if (this.zzcvR != 0L) {
        i = j + zzbxm.zzf(3, this.zzcvR);
      }
      j = i;
      if (this.zzcvS != null)
      {
        j = i;
        if (!this.zzcvS.equals("")) {
          j = i + zzbxm.zzr(4, this.zzcvS);
        }
      }
      i = j;
      if (this.zzcvT != 0) {
        i = j + zzbxm.zzL(5, this.zzcvT);
      }
      j = i;
      if (this.zzcvU != 0) {
        j = i + zzbxm.zzL(6, this.zzcvU);
      }
      i = j;
      if (this.zzcvV != null)
      {
        i = j;
        if (!this.zzcvV.equals("")) {
          i = j + zzbxm.zzr(7, this.zzcvV);
        }
      }
      j = i;
      if (this.zzcvW != null)
      {
        j = i;
        if (!this.zzcvW.equals("")) {
          j = i + zzbxm.zzr(8, this.zzcvW);
        }
      }
      i = j;
      if (this.zzcvX != null)
      {
        i = j;
        if (!this.zzcvX.equals("")) {
          i = j + zzbxm.zzr(9, this.zzcvX);
        }
      }
      j = i;
      if (this.zzcvY != null)
      {
        j = i;
        if (!this.zzcvY.equals("")) {
          j = i + zzbxm.zzr(10, this.zzcvY);
        }
      }
      int k = j;
      if (this.zzcvZ != null)
      {
        k = j;
        if (!this.zzcvZ.equals("")) {
          k = j + zzbxm.zzr(11, this.zzcvZ);
        }
      }
      i = k;
      if (this.zzcwa != 0) {
        i = k + zzbxm.zzL(12, this.zzcwa);
      }
      j = i;
      if (this.zzcwb != null)
      {
        j = i;
        if (this.zzcwb.length > 0)
        {
          j = 0;
          while (j < this.zzcwb.length)
          {
            zzbxz.zza localzza = this.zzcwb[j];
            k = i;
            if (localzza != null) {
              k = i + zzbxm.zzc(13, localzza);
            }
            j += 1;
            i = k;
          }
          j = i;
        }
      }
      return j;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbxz.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */