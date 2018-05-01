package com.google.android.gms.internal;

import java.io.IOException;

public abstract interface zzbyp
{
  public static final class zza
    extends zzbyd<zza>
  {
    private static volatile zza[] zzcxF;
    public String zzcxG;
    
    public zza()
    {
      zzafR();
    }
    
    public static zza[] zzafQ()
    {
      if (zzcxF == null) {}
      synchronized (zzbyh.zzcwK)
      {
        if (zzcxF == null) {
          zzcxF = new zza[0];
        }
        return zzcxF;
      }
    }
    
    public void zza(zzbyc paramzzbyc)
      throws IOException
    {
      if ((this.zzcxG != null) && (!this.zzcxG.equals(""))) {
        paramzzbyc.zzq(1, this.zzcxG);
      }
      super.zza(paramzzbyc);
    }
    
    public zza zzafR()
    {
      this.zzcxG = "";
      this.zzcwC = null;
      this.zzcwL = -1;
      return this;
    }
    
    public zza zzbc(zzbyb paramzzbyb)
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
          this.zzcxG = paramzzbyb.readString();
        }
      }
    }
    
    protected int zzu()
    {
      int j = super.zzu();
      int i = j;
      if (this.zzcxG != null)
      {
        i = j;
        if (!this.zzcxG.equals("")) {
          i = j + zzbyc.zzr(1, this.zzcxG);
        }
      }
      return i;
    }
  }
  
  public static final class zzb
    extends zzbyd<zzb>
  {
    public String zzcxG;
    public String zzcxH;
    public long zzcxI;
    public String zzcxJ;
    public int zzcxK;
    public int zzcxL;
    public String zzcxM;
    public String zzcxN;
    public String zzcxO;
    public String zzcxP;
    public String zzcxQ;
    public int zzcxR;
    public zzbyp.zza[] zzcxS;
    
    public zzb()
    {
      zzafS();
    }
    
    public static zzb zzal(byte[] paramArrayOfByte)
      throws zzbyi
    {
      return (zzb)zzbyj.zza(new zzb(), paramArrayOfByte);
    }
    
    public void zza(zzbyc paramzzbyc)
      throws IOException
    {
      if ((this.zzcxG != null) && (!this.zzcxG.equals(""))) {
        paramzzbyc.zzq(1, this.zzcxG);
      }
      if ((this.zzcxH != null) && (!this.zzcxH.equals(""))) {
        paramzzbyc.zzq(2, this.zzcxH);
      }
      if (this.zzcxI != 0L) {
        paramzzbyc.zzb(3, this.zzcxI);
      }
      if ((this.zzcxJ != null) && (!this.zzcxJ.equals(""))) {
        paramzzbyc.zzq(4, this.zzcxJ);
      }
      if (this.zzcxK != 0) {
        paramzzbyc.zzJ(5, this.zzcxK);
      }
      if (this.zzcxL != 0) {
        paramzzbyc.zzJ(6, this.zzcxL);
      }
      if ((this.zzcxM != null) && (!this.zzcxM.equals(""))) {
        paramzzbyc.zzq(7, this.zzcxM);
      }
      if ((this.zzcxN != null) && (!this.zzcxN.equals(""))) {
        paramzzbyc.zzq(8, this.zzcxN);
      }
      if ((this.zzcxO != null) && (!this.zzcxO.equals(""))) {
        paramzzbyc.zzq(9, this.zzcxO);
      }
      if ((this.zzcxP != null) && (!this.zzcxP.equals(""))) {
        paramzzbyc.zzq(10, this.zzcxP);
      }
      if ((this.zzcxQ != null) && (!this.zzcxQ.equals(""))) {
        paramzzbyc.zzq(11, this.zzcxQ);
      }
      if (this.zzcxR != 0) {
        paramzzbyc.zzJ(12, this.zzcxR);
      }
      if ((this.zzcxS != null) && (this.zzcxS.length > 0))
      {
        int i = 0;
        while (i < this.zzcxS.length)
        {
          zzbyp.zza localzza = this.zzcxS[i];
          if (localzza != null) {
            paramzzbyc.zza(13, localzza);
          }
          i += 1;
        }
      }
      super.zza(paramzzbyc);
    }
    
    public zzb zzafS()
    {
      this.zzcxG = "";
      this.zzcxH = "";
      this.zzcxI = 0L;
      this.zzcxJ = "";
      this.zzcxK = 0;
      this.zzcxL = 0;
      this.zzcxM = "";
      this.zzcxN = "";
      this.zzcxO = "";
      this.zzcxP = "";
      this.zzcxQ = "";
      this.zzcxR = 0;
      this.zzcxS = zzbyp.zza.zzafQ();
      this.zzcwC = null;
      this.zzcwL = -1;
      return this;
    }
    
    public zzb zzbd(zzbyb paramzzbyb)
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
          this.zzcxG = paramzzbyb.readString();
          break;
        case 18: 
          this.zzcxH = paramzzbyb.readString();
          break;
        case 24: 
          this.zzcxI = paramzzbyb.zzaeZ();
          break;
        case 34: 
          this.zzcxJ = paramzzbyb.readString();
          break;
        case 40: 
          this.zzcxK = paramzzbyb.zzafa();
          break;
        case 48: 
          this.zzcxL = paramzzbyb.zzafa();
          break;
        case 58: 
          this.zzcxM = paramzzbyb.readString();
          break;
        case 66: 
          this.zzcxN = paramzzbyb.readString();
          break;
        case 74: 
          this.zzcxO = paramzzbyb.readString();
          break;
        case 82: 
          this.zzcxP = paramzzbyb.readString();
          break;
        case 90: 
          this.zzcxQ = paramzzbyb.readString();
          break;
        case 96: 
          i = paramzzbyb.zzafa();
          switch (i)
          {
          default: 
            break;
          case 0: 
          case 1: 
          case 2: 
            this.zzcxR = i;
          }
          break;
        case 106: 
          int j = zzbym.zzb(paramzzbyb, 106);
          if (this.zzcxS == null) {}
          zzbyp.zza[] arrayOfzza;
          for (i = 0;; i = this.zzcxS.length)
          {
            arrayOfzza = new zzbyp.zza[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzcxS, 0, arrayOfzza, 0, i);
              j = i;
            }
            while (j < arrayOfzza.length - 1)
            {
              arrayOfzza[j] = new zzbyp.zza();
              paramzzbyb.zza(arrayOfzza[j]);
              paramzzbyb.zzaeW();
              j += 1;
            }
          }
          arrayOfzza[j] = new zzbyp.zza();
          paramzzbyb.zza(arrayOfzza[j]);
          this.zzcxS = arrayOfzza;
        }
      }
    }
    
    protected int zzu()
    {
      int j = super.zzu();
      int i = j;
      if (this.zzcxG != null)
      {
        i = j;
        if (!this.zzcxG.equals("")) {
          i = j + zzbyc.zzr(1, this.zzcxG);
        }
      }
      j = i;
      if (this.zzcxH != null)
      {
        j = i;
        if (!this.zzcxH.equals("")) {
          j = i + zzbyc.zzr(2, this.zzcxH);
        }
      }
      i = j;
      if (this.zzcxI != 0L) {
        i = j + zzbyc.zzf(3, this.zzcxI);
      }
      j = i;
      if (this.zzcxJ != null)
      {
        j = i;
        if (!this.zzcxJ.equals("")) {
          j = i + zzbyc.zzr(4, this.zzcxJ);
        }
      }
      i = j;
      if (this.zzcxK != 0) {
        i = j + zzbyc.zzL(5, this.zzcxK);
      }
      j = i;
      if (this.zzcxL != 0) {
        j = i + zzbyc.zzL(6, this.zzcxL);
      }
      i = j;
      if (this.zzcxM != null)
      {
        i = j;
        if (!this.zzcxM.equals("")) {
          i = j + zzbyc.zzr(7, this.zzcxM);
        }
      }
      j = i;
      if (this.zzcxN != null)
      {
        j = i;
        if (!this.zzcxN.equals("")) {
          j = i + zzbyc.zzr(8, this.zzcxN);
        }
      }
      i = j;
      if (this.zzcxO != null)
      {
        i = j;
        if (!this.zzcxO.equals("")) {
          i = j + zzbyc.zzr(9, this.zzcxO);
        }
      }
      j = i;
      if (this.zzcxP != null)
      {
        j = i;
        if (!this.zzcxP.equals("")) {
          j = i + zzbyc.zzr(10, this.zzcxP);
        }
      }
      int k = j;
      if (this.zzcxQ != null)
      {
        k = j;
        if (!this.zzcxQ.equals("")) {
          k = j + zzbyc.zzr(11, this.zzcxQ);
        }
      }
      i = k;
      if (this.zzcxR != 0) {
        i = k + zzbyc.zzL(12, this.zzcxR);
      }
      j = i;
      if (this.zzcxS != null)
      {
        j = i;
        if (this.zzcxS.length > 0)
        {
          j = 0;
          while (j < this.zzcxS.length)
          {
            zzbyp.zza localzza = this.zzcxS[j];
            k = i;
            if (localzza != null) {
              k = i + zzbyc.zzc(13, localzza);
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbyp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */