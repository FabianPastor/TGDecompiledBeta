package com.google.android.gms.internal;

import java.io.IOException;

public final class acx
{
  private final byte[] buffer;
  private int zzcrP;
  private int zzcrQ;
  private int zzcrR;
  private int zzcrS;
  private int zzcrT;
  private int zzcrU = Integer.MAX_VALUE;
  private int zzcrV;
  private int zzcrW = 64;
  private int zzcrX = 67108864;
  
  private acx(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this.buffer = paramArrayOfByte;
    this.zzcrP = paramInt1;
    this.zzcrQ = (paramInt1 + paramInt2);
    this.zzcrS = paramInt1;
  }
  
  public static acx zzH(byte[] paramArrayOfByte)
  {
    return zzb(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  private final void zzLH()
  {
    this.zzcrQ += this.zzcrR;
    int i = this.zzcrQ;
    if (i > this.zzcrU)
    {
      this.zzcrR = (i - this.zzcrU);
      this.zzcrQ -= this.zzcrR;
      return;
    }
    this.zzcrR = 0;
  }
  
  private final byte zzLJ()
    throws IOException
  {
    if (this.zzcrS == this.zzcrQ) {
      throw adf.zzLO();
    }
    byte[] arrayOfByte = this.buffer;
    int i = this.zzcrS;
    this.zzcrS = (i + 1);
    return arrayOfByte[i];
  }
  
  public static acx zzb(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return new acx(paramArrayOfByte, 0, paramInt2);
  }
  
  private final void zzcq(int paramInt)
    throws IOException
  {
    if (paramInt < 0) {
      throw adf.zzLP();
    }
    if (this.zzcrS + paramInt > this.zzcrU)
    {
      zzcq(this.zzcrU - this.zzcrS);
      throw adf.zzLO();
    }
    if (paramInt <= this.zzcrQ - this.zzcrS)
    {
      this.zzcrS += paramInt;
      return;
    }
    throw adf.zzLO();
  }
  
  public final int getPosition()
  {
    return this.zzcrS - this.zzcrP;
  }
  
  public final byte[] readBytes()
    throws IOException
  {
    int i = zzLD();
    if (i < 0) {
      throw adf.zzLP();
    }
    if (i == 0) {
      return adj.zzcst;
    }
    if (i > this.zzcrQ - this.zzcrS) {
      throw adf.zzLO();
    }
    byte[] arrayOfByte = new byte[i];
    System.arraycopy(this.buffer, this.zzcrS, arrayOfByte, 0, i);
    this.zzcrS = (i + this.zzcrS);
    return arrayOfByte;
  }
  
  public final String readString()
    throws IOException
  {
    int i = zzLD();
    if (i < 0) {
      throw adf.zzLP();
    }
    if (i > this.zzcrQ - this.zzcrS) {
      throw adf.zzLO();
    }
    String str = new String(this.buffer, this.zzcrS, i, ade.UTF_8);
    this.zzcrS = (i + this.zzcrS);
    return str;
  }
  
  public final int zzLA()
    throws IOException
  {
    return zzLD();
  }
  
  public final boolean zzLB()
    throws IOException
  {
    return zzLD() != 0;
  }
  
  public final long zzLC()
    throws IOException
  {
    long l = zzLE();
    return -(l & 1L) ^ l >>> 1;
  }
  
  public final int zzLD()
    throws IOException
  {
    int i = zzLJ();
    if (i >= 0) {}
    int k;
    do
    {
      return i;
      i &= 0x7F;
      j = zzLJ();
      if (j >= 0) {
        return i | j << 7;
      }
      i |= (j & 0x7F) << 7;
      j = zzLJ();
      if (j >= 0) {
        return i | j << 14;
      }
      i |= (j & 0x7F) << 14;
      k = zzLJ();
      if (k >= 0) {
        return i | k << 21;
      }
      j = zzLJ();
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
      if (zzLJ() >= 0) {
        break;
      }
      j += 1;
    }
    label133:
    throw adf.zzLQ();
  }
  
  public final long zzLE()
    throws IOException
  {
    int i = 0;
    long l = 0L;
    while (i < 64)
    {
      int j = zzLJ();
      l |= (j & 0x7F) << i;
      if ((j & 0x80) == 0) {
        return l;
      }
      i += 7;
    }
    throw adf.zzLQ();
  }
  
  public final int zzLF()
    throws IOException
  {
    return zzLJ() & 0xFF | (zzLJ() & 0xFF) << 8 | (zzLJ() & 0xFF) << 16 | (zzLJ() & 0xFF) << 24;
  }
  
  public final long zzLG()
    throws IOException
  {
    int i = zzLJ();
    int j = zzLJ();
    int k = zzLJ();
    int m = zzLJ();
    int n = zzLJ();
    int i1 = zzLJ();
    int i2 = zzLJ();
    int i3 = zzLJ();
    long l = i;
    return (j & 0xFF) << 8 | l & 0xFF | (k & 0xFF) << 16 | (m & 0xFF) << 24 | (n & 0xFF) << 32 | (i1 & 0xFF) << 40 | (i2 & 0xFF) << 48 | (i3 & 0xFF) << 56;
  }
  
  public final int zzLI()
  {
    if (this.zzcrU == Integer.MAX_VALUE) {
      return -1;
    }
    int i = this.zzcrS;
    return this.zzcrU - i;
  }
  
  public final int zzLy()
    throws IOException
  {
    if (this.zzcrS == this.zzcrQ)
    {
      this.zzcrT = 0;
      return 0;
    }
    this.zzcrT = zzLD();
    if (this.zzcrT == 0) {
      throw new adf("Protocol message contained an invalid tag (zero).");
    }
    return this.zzcrT;
  }
  
  public final long zzLz()
    throws IOException
  {
    return zzLE();
  }
  
  public final void zza(adg paramadg)
    throws IOException
  {
    int i = zzLD();
    if (this.zzcrV >= this.zzcrW) {
      throw adf.zzLR();
    }
    i = zzcn(i);
    this.zzcrV += 1;
    paramadg.zza(this);
    zzcl(0);
    this.zzcrV -= 1;
    zzco(i);
  }
  
  public final void zza(adg paramadg, int paramInt)
    throws IOException
  {
    if (this.zzcrV >= this.zzcrW) {
      throw adf.zzLR();
    }
    this.zzcrV += 1;
    paramadg.zza(this);
    zzcl(paramInt << 3 | 0x4);
    this.zzcrV -= 1;
  }
  
  public final void zzcl(int paramInt)
    throws adf
  {
    if (this.zzcrT != paramInt) {
      throw new adf("Protocol message end-group tag did not match expected tag.");
    }
  }
  
  public final boolean zzcm(int paramInt)
    throws IOException
  {
    switch (paramInt & 0x7)
    {
    default: 
      throw new adf("Protocol message tag had invalid wire type.");
    case 0: 
      zzLD();
      return true;
    case 1: 
      zzLG();
      return true;
    case 2: 
      zzcq(zzLD());
      return true;
    case 3: 
      int i;
      do
      {
        i = zzLy();
      } while ((i != 0) && (zzcm(i)));
      zzcl(paramInt >>> 3 << 3 | 0x4);
      return true;
    case 4: 
      return false;
    }
    zzLF();
    return true;
  }
  
  public final int zzcn(int paramInt)
    throws adf
  {
    if (paramInt < 0) {
      throw adf.zzLP();
    }
    paramInt = this.zzcrS + paramInt;
    int i = this.zzcrU;
    if (paramInt > i) {
      throw adf.zzLO();
    }
    this.zzcrU = paramInt;
    zzLH();
    return i;
  }
  
  public final void zzco(int paramInt)
  {
    this.zzcrU = paramInt;
    zzLH();
  }
  
  public final void zzcp(int paramInt)
  {
    zzq(paramInt, this.zzcrT);
  }
  
  public final byte[] zzp(int paramInt1, int paramInt2)
  {
    if (paramInt2 == 0) {
      return adj.zzcst;
    }
    byte[] arrayOfByte = new byte[paramInt2];
    int i = this.zzcrP;
    System.arraycopy(this.buffer, i + paramInt1, arrayOfByte, 0, paramInt2);
    return arrayOfByte;
  }
  
  final void zzq(int paramInt1, int paramInt2)
  {
    if (paramInt1 > this.zzcrS - this.zzcrP)
    {
      paramInt2 = this.zzcrS;
      int i = this.zzcrP;
      throw new IllegalArgumentException(50 + "Position " + paramInt1 + " is beyond current " + (paramInt2 - i));
    }
    if (paramInt1 < 0) {
      throw new IllegalArgumentException(24 + "Bad position " + paramInt1);
    }
    this.zzcrS = (this.zzcrP + paramInt1);
    this.zzcrT = paramInt2;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/acx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */