package com.google.android.gms.internal;

import java.io.IOException;

public final class zzbyb
{
  private final byte[] buffer;
  private int zzcwA = 67108864;
  private int zzcws;
  private int zzcwt;
  private int zzcwu;
  private int zzcwv;
  private int zzcww;
  private int zzcwx = Integer.MAX_VALUE;
  private int zzcwy;
  private int zzcwz = 64;
  
  private zzbyb(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this.buffer = paramArrayOfByte;
    this.zzcws = paramInt1;
    this.zzcwt = (paramInt1 + paramInt2);
    this.zzcwv = paramInt1;
  }
  
  private void zzafj()
  {
    this.zzcwt += this.zzcwu;
    int i = this.zzcwt;
    if (i > this.zzcwx)
    {
      this.zzcwu = (i - this.zzcwx);
      this.zzcwt -= this.zzcwu;
      return;
    }
    this.zzcwu = 0;
  }
  
  public static zzbyb zzag(byte[] paramArrayOfByte)
  {
    return zzb(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public static zzbyb zzb(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return new zzbyb(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public static long zzbk(long paramLong)
  {
    return paramLong >>> 1 ^ -(1L & paramLong);
  }
  
  public static int zzre(int paramInt)
  {
    return paramInt >>> 1 ^ -(paramInt & 0x1);
  }
  
  public int getPosition()
  {
    return this.zzcwv - this.zzcws;
  }
  
  public byte[] readBytes()
    throws IOException
  {
    int i = zzaff();
    if (i < 0) {
      throw zzbyi.zzafu();
    }
    if (i == 0) {
      return zzbym.zzcwW;
    }
    if (i > this.zzcwt - this.zzcwv) {
      throw zzbyi.zzaft();
    }
    byte[] arrayOfByte = new byte[i];
    System.arraycopy(this.buffer, this.zzcwv, arrayOfByte, 0, i);
    this.zzcwv = (i + this.zzcwv);
    return arrayOfByte;
  }
  
  public double readDouble()
    throws IOException
  {
    return Double.longBitsToDouble(zzafi());
  }
  
  public float readFloat()
    throws IOException
  {
    return Float.intBitsToFloat(zzafh());
  }
  
  public String readString()
    throws IOException
  {
    int i = zzaff();
    if (i < 0) {
      throw zzbyi.zzafu();
    }
    if (i > this.zzcwt - this.zzcwv) {
      throw zzbyi.zzaft();
    }
    String str = new String(this.buffer, this.zzcwv, i, zzbyh.UTF_8);
    this.zzcwv = (i + this.zzcwv);
    return str;
  }
  
  public byte[] zzI(int paramInt1, int paramInt2)
  {
    if (paramInt2 == 0) {
      return zzbym.zzcwW;
    }
    byte[] arrayOfByte = new byte[paramInt2];
    int i = this.zzcws;
    System.arraycopy(this.buffer, i + paramInt1, arrayOfByte, 0, paramInt2);
    return arrayOfByte;
  }
  
  public void zza(zzbyj paramzzbyj)
    throws IOException
  {
    int i = zzaff();
    if (this.zzcwy >= this.zzcwz) {
      throw zzbyi.zzafz();
    }
    i = zzrf(i);
    this.zzcwy += 1;
    paramzzbyj.zzb(this);
    zzrc(0);
    this.zzcwy -= 1;
    zzrg(i);
  }
  
  public void zza(zzbyj paramzzbyj, int paramInt)
    throws IOException
  {
    if (this.zzcwy >= this.zzcwz) {
      throw zzbyi.zzafz();
    }
    this.zzcwy += 1;
    paramzzbyj.zzb(this);
    zzrc(zzbym.zzO(paramInt, 4));
    this.zzcwy -= 1;
  }
  
  public int zzaeW()
    throws IOException
  {
    if (zzafl())
    {
      this.zzcww = 0;
      return 0;
    }
    this.zzcww = zzaff();
    if (this.zzcww == 0) {
      throw zzbyi.zzafw();
    }
    return this.zzcww;
  }
  
  public void zzaeX()
    throws IOException
  {
    int i;
    do
    {
      i = zzaeW();
    } while ((i != 0) && (zzrd(i)));
  }
  
  public long zzaeY()
    throws IOException
  {
    return zzafg();
  }
  
  public long zzaeZ()
    throws IOException
  {
    return zzafg();
  }
  
  public int zzafa()
    throws IOException
  {
    return zzaff();
  }
  
  public long zzafb()
    throws IOException
  {
    return zzafi();
  }
  
  public boolean zzafc()
    throws IOException
  {
    return zzaff() != 0;
  }
  
  public int zzafd()
    throws IOException
  {
    return zzre(zzaff());
  }
  
  public long zzafe()
    throws IOException
  {
    return zzbk(zzafg());
  }
  
  public int zzaff()
    throws IOException
  {
    int i = zzafm();
    if (i >= 0) {}
    int k;
    do
    {
      return i;
      i &= 0x7F;
      j = zzafm();
      if (j >= 0) {
        return i | j << 7;
      }
      i |= (j & 0x7F) << 7;
      j = zzafm();
      if (j >= 0) {
        return i | j << 14;
      }
      i |= (j & 0x7F) << 14;
      k = zzafm();
      if (k >= 0) {
        return i | k << 21;
      }
      j = zzafm();
      k = i | (k & 0x7F) << 21 | j << 28;
      i = k;
    } while (j >= 0);
    int j = 0;
    for (;;)
    {
      if (j >= 5) {
        break label133;
      }
      i = k;
      if (zzafm() >= 0) {
        break;
      }
      j += 1;
    }
    label133:
    throw zzbyi.zzafv();
  }
  
  public long zzafg()
    throws IOException
  {
    int i = 0;
    long l = 0L;
    while (i < 64)
    {
      int j = zzafm();
      l |= (j & 0x7F) << i;
      if ((j & 0x80) == 0) {
        return l;
      }
      i += 7;
    }
    throw zzbyi.zzafv();
  }
  
  public int zzafh()
    throws IOException
  {
    return zzafm() & 0xFF | (zzafm() & 0xFF) << 8 | (zzafm() & 0xFF) << 16 | (zzafm() & 0xFF) << 24;
  }
  
  public long zzafi()
    throws IOException
  {
    int i = zzafm();
    int j = zzafm();
    int k = zzafm();
    int m = zzafm();
    int n = zzafm();
    int i1 = zzafm();
    int i2 = zzafm();
    int i3 = zzafm();
    long l = i;
    return (j & 0xFF) << 8 | l & 0xFF | (k & 0xFF) << 16 | (m & 0xFF) << 24 | (n & 0xFF) << 32 | (i1 & 0xFF) << 40 | (i2 & 0xFF) << 48 | (i3 & 0xFF) << 56;
  }
  
  public int zzafk()
  {
    if (this.zzcwx == Integer.MAX_VALUE) {
      return -1;
    }
    int i = this.zzcwv;
    return this.zzcwx - i;
  }
  
  public boolean zzafl()
  {
    return this.zzcwv == this.zzcwt;
  }
  
  public byte zzafm()
    throws IOException
  {
    if (this.zzcwv == this.zzcwt) {
      throw zzbyi.zzaft();
    }
    byte[] arrayOfByte = this.buffer;
    int i = this.zzcwv;
    this.zzcwv = (i + 1);
    return arrayOfByte[i];
  }
  
  public void zzrc(int paramInt)
    throws zzbyi
  {
    if (this.zzcww != paramInt) {
      throw zzbyi.zzafx();
    }
  }
  
  public boolean zzrd(int paramInt)
    throws IOException
  {
    switch (zzbym.zzrw(paramInt))
    {
    default: 
      throw zzbyi.zzafy();
    case 0: 
      zzafa();
      return true;
    case 1: 
      zzafi();
      return true;
    case 2: 
      zzri(zzaff());
      return true;
    case 3: 
      zzaeX();
      zzrc(zzbym.zzO(zzbym.zzrx(paramInt), 4));
      return true;
    case 4: 
      return false;
    }
    zzafh();
    return true;
  }
  
  public int zzrf(int paramInt)
    throws zzbyi
  {
    if (paramInt < 0) {
      throw zzbyi.zzafu();
    }
    paramInt = this.zzcwv + paramInt;
    int i = this.zzcwx;
    if (paramInt > i) {
      throw zzbyi.zzaft();
    }
    this.zzcwx = paramInt;
    zzafj();
    return i;
  }
  
  public void zzrg(int paramInt)
  {
    this.zzcwx = paramInt;
    zzafj();
  }
  
  public void zzrh(int paramInt)
  {
    if (paramInt > this.zzcwv - this.zzcws)
    {
      int i = this.zzcwv;
      int j = this.zzcws;
      throw new IllegalArgumentException(50 + "Position " + paramInt + " is beyond current " + (i - j));
    }
    if (paramInt < 0) {
      throw new IllegalArgumentException(24 + "Bad position " + paramInt);
    }
    this.zzcwv = (this.zzcws + paramInt);
  }
  
  public void zzri(int paramInt)
    throws IOException
  {
    if (paramInt < 0) {
      throw zzbyi.zzafu();
    }
    if (this.zzcwv + paramInt > this.zzcwx)
    {
      zzri(this.zzcwx - this.zzcwv);
      throw zzbyi.zzaft();
    }
    if (paramInt <= this.zzcwt - this.zzcwv)
    {
      this.zzcwv += paramInt;
      return;
    }
    throw zzbyi.zzaft();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbyb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */