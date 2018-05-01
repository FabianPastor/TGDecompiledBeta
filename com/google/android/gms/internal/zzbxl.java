package com.google.android.gms.internal;

import java.io.IOException;

public final class zzbxl
{
  private final byte[] buffer;
  private int zzcuA;
  private int zzcuB;
  private int zzcuC;
  private int zzcuD = Integer.MAX_VALUE;
  private int zzcuE;
  private int zzcuF = 64;
  private int zzcuG = 67108864;
  private int zzcuy;
  private int zzcuz;
  
  private zzbxl(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this.buffer = paramArrayOfByte;
    this.zzcuy = paramInt1;
    this.zzcuz = (paramInt1 + paramInt2);
    this.zzcuB = paramInt1;
  }
  
  public static long zzaZ(long paramLong)
  {
    return paramLong >>> 1 ^ -(1L & paramLong);
  }
  
  private void zzaeB()
  {
    this.zzcuz += this.zzcuA;
    int i = this.zzcuz;
    if (i > this.zzcuD)
    {
      this.zzcuA = (i - this.zzcuD);
      this.zzcuz -= this.zzcuA;
      return;
    }
    this.zzcuA = 0;
  }
  
  public static zzbxl zzaf(byte[] paramArrayOfByte)
  {
    return zzb(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public static zzbxl zzb(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return new zzbxl(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public static int zzqZ(int paramInt)
  {
    return paramInt >>> 1 ^ -(paramInt & 0x1);
  }
  
  public int getPosition()
  {
    return this.zzcuB - this.zzcuy;
  }
  
  public byte[] readBytes()
    throws IOException
  {
    int i = zzaex();
    if (i < 0) {
      throw zzbxs.zzaeM();
    }
    if (i == 0) {
      return zzbxw.zzcvd;
    }
    if (i > this.zzcuz - this.zzcuB) {
      throw zzbxs.zzaeL();
    }
    byte[] arrayOfByte = new byte[i];
    System.arraycopy(this.buffer, this.zzcuB, arrayOfByte, 0, i);
    this.zzcuB = (i + this.zzcuB);
    return arrayOfByte;
  }
  
  public double readDouble()
    throws IOException
  {
    return Double.longBitsToDouble(zzaeA());
  }
  
  public float readFloat()
    throws IOException
  {
    return Float.intBitsToFloat(zzaez());
  }
  
  public String readString()
    throws IOException
  {
    int i = zzaex();
    if (i < 0) {
      throw zzbxs.zzaeM();
    }
    if (i > this.zzcuz - this.zzcuB) {
      throw zzbxs.zzaeL();
    }
    String str = new String(this.buffer, this.zzcuB, i, zzbxr.UTF_8);
    this.zzcuB = (i + this.zzcuB);
    return str;
  }
  
  public byte[] zzI(int paramInt1, int paramInt2)
  {
    if (paramInt2 == 0) {
      return zzbxw.zzcvd;
    }
    byte[] arrayOfByte = new byte[paramInt2];
    int i = this.zzcuy;
    System.arraycopy(this.buffer, i + paramInt1, arrayOfByte, 0, paramInt2);
    return arrayOfByte;
  }
  
  public void zza(zzbxt paramzzbxt)
    throws IOException
  {
    int i = zzaex();
    if (this.zzcuE >= this.zzcuF) {
      throw zzbxs.zzaeR();
    }
    i = zzra(i);
    this.zzcuE += 1;
    paramzzbxt.zzb(this);
    zzqX(0);
    this.zzcuE -= 1;
    zzrb(i);
  }
  
  public void zza(zzbxt paramzzbxt, int paramInt)
    throws IOException
  {
    if (this.zzcuE >= this.zzcuF) {
      throw zzbxs.zzaeR();
    }
    this.zzcuE += 1;
    paramzzbxt.zzb(this);
    zzqX(zzbxw.zzO(paramInt, 4));
    this.zzcuE -= 1;
  }
  
  public long zzaeA()
    throws IOException
  {
    int i = zzaeE();
    int j = zzaeE();
    int k = zzaeE();
    int m = zzaeE();
    int n = zzaeE();
    int i1 = zzaeE();
    int i2 = zzaeE();
    int i3 = zzaeE();
    long l = i;
    return (j & 0xFF) << 8 | l & 0xFF | (k & 0xFF) << 16 | (m & 0xFF) << 24 | (n & 0xFF) << 32 | (i1 & 0xFF) << 40 | (i2 & 0xFF) << 48 | (i3 & 0xFF) << 56;
  }
  
  public int zzaeC()
  {
    if (this.zzcuD == Integer.MAX_VALUE) {
      return -1;
    }
    int i = this.zzcuB;
    return this.zzcuD - i;
  }
  
  public boolean zzaeD()
  {
    return this.zzcuB == this.zzcuz;
  }
  
  public byte zzaeE()
    throws IOException
  {
    if (this.zzcuB == this.zzcuz) {
      throw zzbxs.zzaeL();
    }
    byte[] arrayOfByte = this.buffer;
    int i = this.zzcuB;
    this.zzcuB = (i + 1);
    return arrayOfByte[i];
  }
  
  public int zzaeo()
    throws IOException
  {
    if (zzaeD())
    {
      this.zzcuC = 0;
      return 0;
    }
    this.zzcuC = zzaex();
    if (this.zzcuC == 0) {
      throw zzbxs.zzaeO();
    }
    return this.zzcuC;
  }
  
  public void zzaep()
    throws IOException
  {
    int i;
    do
    {
      i = zzaeo();
    } while ((i != 0) && (zzqY(i)));
  }
  
  public long zzaeq()
    throws IOException
  {
    return zzaey();
  }
  
  public long zzaer()
    throws IOException
  {
    return zzaey();
  }
  
  public int zzaes()
    throws IOException
  {
    return zzaex();
  }
  
  public long zzaet()
    throws IOException
  {
    return zzaeA();
  }
  
  public boolean zzaeu()
    throws IOException
  {
    return zzaex() != 0;
  }
  
  public int zzaev()
    throws IOException
  {
    return zzqZ(zzaex());
  }
  
  public long zzaew()
    throws IOException
  {
    return zzaZ(zzaey());
  }
  
  public int zzaex()
    throws IOException
  {
    int i = zzaeE();
    if (i >= 0) {}
    int k;
    do
    {
      return i;
      i &= 0x7F;
      j = zzaeE();
      if (j >= 0) {
        return i | j << 7;
      }
      i |= (j & 0x7F) << 7;
      j = zzaeE();
      if (j >= 0) {
        return i | j << 14;
      }
      i |= (j & 0x7F) << 14;
      k = zzaeE();
      if (k >= 0) {
        return i | k << 21;
      }
      j = zzaeE();
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
      if (zzaeE() >= 0) {
        break;
      }
      j += 1;
    }
    label133:
    throw zzbxs.zzaeN();
  }
  
  public long zzaey()
    throws IOException
  {
    int i = 0;
    long l = 0L;
    while (i < 64)
    {
      int j = zzaeE();
      l |= (j & 0x7F) << i;
      if ((j & 0x80) == 0) {
        return l;
      }
      i += 7;
    }
    throw zzbxs.zzaeN();
  }
  
  public int zzaez()
    throws IOException
  {
    return zzaeE() & 0xFF | (zzaeE() & 0xFF) << 8 | (zzaeE() & 0xFF) << 16 | (zzaeE() & 0xFF) << 24;
  }
  
  public void zzqX(int paramInt)
    throws zzbxs
  {
    if (this.zzcuC != paramInt) {
      throw zzbxs.zzaeP();
    }
  }
  
  public boolean zzqY(int paramInt)
    throws IOException
  {
    switch (zzbxw.zzrr(paramInt))
    {
    default: 
      throw zzbxs.zzaeQ();
    case 0: 
      zzaes();
      return true;
    case 1: 
      zzaeA();
      return true;
    case 2: 
      zzrd(zzaex());
      return true;
    case 3: 
      zzaep();
      zzqX(zzbxw.zzO(zzbxw.zzrs(paramInt), 4));
      return true;
    case 4: 
      return false;
    }
    zzaez();
    return true;
  }
  
  public int zzra(int paramInt)
    throws zzbxs
  {
    if (paramInt < 0) {
      throw zzbxs.zzaeM();
    }
    paramInt = this.zzcuB + paramInt;
    int i = this.zzcuD;
    if (paramInt > i) {
      throw zzbxs.zzaeL();
    }
    this.zzcuD = paramInt;
    zzaeB();
    return i;
  }
  
  public void zzrb(int paramInt)
  {
    this.zzcuD = paramInt;
    zzaeB();
  }
  
  public void zzrc(int paramInt)
  {
    if (paramInt > this.zzcuB - this.zzcuy)
    {
      int i = this.zzcuB;
      int j = this.zzcuy;
      throw new IllegalArgumentException(50 + "Position " + paramInt + " is beyond current " + (i - j));
    }
    if (paramInt < 0) {
      throw new IllegalArgumentException(24 + "Bad position " + paramInt);
    }
    this.zzcuB = (this.zzcuy + paramInt);
  }
  
  public void zzrd(int paramInt)
    throws IOException
  {
    if (paramInt < 0) {
      throw zzbxs.zzaeM();
    }
    if (this.zzcuB + paramInt > this.zzcuD)
    {
      zzrd(this.zzcuD - this.zzcuB);
      throw zzbxs.zzaeL();
    }
    if (paramInt <= this.zzcuz - this.zzcuB)
    {
      this.zzcuB += paramInt;
      return;
    }
    throw zzbxs.zzaeL();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbxl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */