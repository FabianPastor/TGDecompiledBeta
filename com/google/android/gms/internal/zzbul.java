package com.google.android.gms.internal;

import java.io.IOException;

public final class zzbul
{
  private final byte[] buffer;
  private int zzcrN;
  private int zzcrO;
  private int zzcrP;
  private int zzcrQ;
  private int zzcrR;
  private int zzcrS = Integer.MAX_VALUE;
  private int zzcrT;
  private int zzcrU = 64;
  private int zzcrV = 67108864;
  
  private zzbul(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this.buffer = paramArrayOfByte;
    this.zzcrN = paramInt1;
    this.zzcrO = (paramInt1 + paramInt2);
    this.zzcrQ = paramInt1;
  }
  
  public static long zzaV(long paramLong)
  {
    return paramLong >>> 1 ^ -(1L & paramLong);
  }
  
  private void zzacH()
  {
    this.zzcrO += this.zzcrP;
    int i = this.zzcrO;
    if (i > this.zzcrS)
    {
      this.zzcrP = (i - this.zzcrS);
      this.zzcrO -= this.zzcrP;
      return;
    }
    this.zzcrP = 0;
  }
  
  public static zzbul zzad(byte[] paramArrayOfByte)
  {
    return zzb(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public static zzbul zzb(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return new zzbul(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public static int zzqi(int paramInt)
  {
    return paramInt >>> 1 ^ -(paramInt & 0x1);
  }
  
  public int getPosition()
  {
    return this.zzcrQ - this.zzcrN;
  }
  
  public byte[] readBytes()
    throws IOException
  {
    int i = zzacD();
    if (i < 0) {
      throw zzbus.zzacS();
    }
    if (i == 0) {
      return zzbuw.zzcsp;
    }
    if (i > this.zzcrO - this.zzcrQ) {
      throw zzbus.zzacR();
    }
    byte[] arrayOfByte = new byte[i];
    System.arraycopy(this.buffer, this.zzcrQ, arrayOfByte, 0, i);
    this.zzcrQ = (i + this.zzcrQ);
    return arrayOfByte;
  }
  
  public double readDouble()
    throws IOException
  {
    return Double.longBitsToDouble(zzacG());
  }
  
  public float readFloat()
    throws IOException
  {
    return Float.intBitsToFloat(zzacF());
  }
  
  public String readString()
    throws IOException
  {
    int i = zzacD();
    if (i < 0) {
      throw zzbus.zzacS();
    }
    if (i > this.zzcrO - this.zzcrQ) {
      throw zzbus.zzacR();
    }
    String str = new String(this.buffer, this.zzcrQ, i, zzbur.UTF_8);
    this.zzcrQ = (i + this.zzcrQ);
    return str;
  }
  
  public byte[] zzE(int paramInt1, int paramInt2)
  {
    if (paramInt2 == 0) {
      return zzbuw.zzcsp;
    }
    byte[] arrayOfByte = new byte[paramInt2];
    int i = this.zzcrN;
    System.arraycopy(this.buffer, i + paramInt1, arrayOfByte, 0, paramInt2);
    return arrayOfByte;
  }
  
  public void zza(zzbut paramzzbut)
    throws IOException
  {
    int i = zzacD();
    if (this.zzcrT >= this.zzcrU) {
      throw zzbus.zzacX();
    }
    i = zzqj(i);
    this.zzcrT += 1;
    paramzzbut.zzb(this);
    zzqg(0);
    this.zzcrT -= 1;
    zzqk(i);
  }
  
  public void zza(zzbut paramzzbut, int paramInt)
    throws IOException
  {
    if (this.zzcrT >= this.zzcrU) {
      throw zzbus.zzacX();
    }
    this.zzcrT += 1;
    paramzzbut.zzb(this);
    zzqg(zzbuw.zzK(paramInt, 4));
    this.zzcrT -= 1;
  }
  
  public boolean zzacA()
    throws IOException
  {
    return zzacD() != 0;
  }
  
  public int zzacB()
    throws IOException
  {
    return zzqi(zzacD());
  }
  
  public long zzacC()
    throws IOException
  {
    return zzaV(zzacE());
  }
  
  public int zzacD()
    throws IOException
  {
    int i = zzacK();
    if (i >= 0) {}
    int k;
    do
    {
      return i;
      i &= 0x7F;
      j = zzacK();
      if (j >= 0) {
        return i | j << 7;
      }
      i |= (j & 0x7F) << 7;
      j = zzacK();
      if (j >= 0) {
        return i | j << 14;
      }
      i |= (j & 0x7F) << 14;
      k = zzacK();
      if (k >= 0) {
        return i | k << 21;
      }
      j = zzacK();
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
      if (zzacK() >= 0) {
        break;
      }
      j += 1;
    }
    label133:
    throw zzbus.zzacT();
  }
  
  public long zzacE()
    throws IOException
  {
    int i = 0;
    long l = 0L;
    while (i < 64)
    {
      int j = zzacK();
      l |= (j & 0x7F) << i;
      if ((j & 0x80) == 0) {
        return l;
      }
      i += 7;
    }
    throw zzbus.zzacT();
  }
  
  public int zzacF()
    throws IOException
  {
    return zzacK() & 0xFF | (zzacK() & 0xFF) << 8 | (zzacK() & 0xFF) << 16 | (zzacK() & 0xFF) << 24;
  }
  
  public long zzacG()
    throws IOException
  {
    int i = zzacK();
    int j = zzacK();
    int k = zzacK();
    int m = zzacK();
    int n = zzacK();
    int i1 = zzacK();
    int i2 = zzacK();
    int i3 = zzacK();
    long l = i;
    return (j & 0xFF) << 8 | l & 0xFF | (k & 0xFF) << 16 | (m & 0xFF) << 24 | (n & 0xFF) << 32 | (i1 & 0xFF) << 40 | (i2 & 0xFF) << 48 | (i3 & 0xFF) << 56;
  }
  
  public int zzacI()
  {
    if (this.zzcrS == Integer.MAX_VALUE) {
      return -1;
    }
    int i = this.zzcrQ;
    return this.zzcrS - i;
  }
  
  public boolean zzacJ()
  {
    return this.zzcrQ == this.zzcrO;
  }
  
  public byte zzacK()
    throws IOException
  {
    if (this.zzcrQ == this.zzcrO) {
      throw zzbus.zzacR();
    }
    byte[] arrayOfByte = this.buffer;
    int i = this.zzcrQ;
    this.zzcrQ = (i + 1);
    return arrayOfByte[i];
  }
  
  public int zzacu()
    throws IOException
  {
    if (zzacJ())
    {
      this.zzcrR = 0;
      return 0;
    }
    this.zzcrR = zzacD();
    if (this.zzcrR == 0) {
      throw zzbus.zzacU();
    }
    return this.zzcrR;
  }
  
  public void zzacv()
    throws IOException
  {
    int i;
    do
    {
      i = zzacu();
    } while ((i != 0) && (zzqh(i)));
  }
  
  public long zzacw()
    throws IOException
  {
    return zzacE();
  }
  
  public long zzacx()
    throws IOException
  {
    return zzacE();
  }
  
  public int zzacy()
    throws IOException
  {
    return zzacD();
  }
  
  public long zzacz()
    throws IOException
  {
    return zzacG();
  }
  
  public void zzqg(int paramInt)
    throws zzbus
  {
    if (this.zzcrR != paramInt) {
      throw zzbus.zzacV();
    }
  }
  
  public boolean zzqh(int paramInt)
    throws IOException
  {
    switch (zzbuw.zzqA(paramInt))
    {
    default: 
      throw zzbus.zzacW();
    case 0: 
      zzacy();
      return true;
    case 1: 
      zzacG();
      return true;
    case 2: 
      zzqm(zzacD());
      return true;
    case 3: 
      zzacv();
      zzqg(zzbuw.zzK(zzbuw.zzqB(paramInt), 4));
      return true;
    case 4: 
      return false;
    }
    zzacF();
    return true;
  }
  
  public int zzqj(int paramInt)
    throws zzbus
  {
    if (paramInt < 0) {
      throw zzbus.zzacS();
    }
    paramInt = this.zzcrQ + paramInt;
    int i = this.zzcrS;
    if (paramInt > i) {
      throw zzbus.zzacR();
    }
    this.zzcrS = paramInt;
    zzacH();
    return i;
  }
  
  public void zzqk(int paramInt)
  {
    this.zzcrS = paramInt;
    zzacH();
  }
  
  public void zzql(int paramInt)
  {
    if (paramInt > this.zzcrQ - this.zzcrN)
    {
      int i = this.zzcrQ;
      int j = this.zzcrN;
      throw new IllegalArgumentException(50 + "Position " + paramInt + " is beyond current " + (i - j));
    }
    if (paramInt < 0) {
      throw new IllegalArgumentException(24 + "Bad position " + paramInt);
    }
    this.zzcrQ = (this.zzcrN + paramInt);
  }
  
  public void zzqm(int paramInt)
    throws IOException
  {
    if (paramInt < 0) {
      throw zzbus.zzacS();
    }
    if (this.zzcrQ + paramInt > this.zzcrS)
    {
      zzqm(this.zzcrS - this.zzcrQ);
      throw zzbus.zzacR();
    }
    if (paramInt <= this.zzcrO - this.zzcrQ)
    {
      this.zzcrQ += paramInt;
      return;
    }
    throw zzbus.zzacR();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbul.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */