package com.google.android.gms.internal;

import java.io.IOException;

public final class zzars
{
  private int btA;
  private int btB = Integer.MAX_VALUE;
  private int btC;
  private int btD = 64;
  private int btE = 67108864;
  private int btw;
  private int btx;
  private int bty;
  private int btz;
  private final byte[] buffer;
  
  private zzars(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this.buffer = paramArrayOfByte;
    this.btw = paramInt1;
    this.btx = (paramInt1 + paramInt2);
    this.btz = paramInt1;
  }
  
  private void ch()
  {
    this.btx += this.bty;
    int i = this.btx;
    if (i > this.btB)
    {
      this.bty = (i - this.btB);
      this.btx -= this.bty;
      return;
    }
    this.bty = 0;
  }
  
  public static int zzags(int paramInt)
  {
    return paramInt >>> 1 ^ -(paramInt & 0x1);
  }
  
  public static zzars zzb(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return new zzars(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public static zzars zzbd(byte[] paramArrayOfByte)
  {
    return zzb(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public static long zzct(long paramLong)
  {
    return paramLong >>> 1 ^ -(1L & paramLong);
  }
  
  public int bU()
    throws IOException
  {
    if (cj())
    {
      this.btA = 0;
      return 0;
    }
    this.btA = cd();
    if (this.btA == 0) {
      throw zzarz.cu();
    }
    return this.btA;
  }
  
  public void bV()
    throws IOException
  {
    int i;
    do
    {
      i = bU();
    } while ((i != 0) && (zzagr(i)));
  }
  
  public long bW()
    throws IOException
  {
    return ce();
  }
  
  public long bX()
    throws IOException
  {
    return ce();
  }
  
  public int bY()
    throws IOException
  {
    return cd();
  }
  
  public long bZ()
    throws IOException
  {
    return cg();
  }
  
  public boolean ca()
    throws IOException
  {
    return cd() != 0;
  }
  
  public int cb()
    throws IOException
  {
    return zzags(cd());
  }
  
  public long cc()
    throws IOException
  {
    return zzct(ce());
  }
  
  public int cd()
    throws IOException
  {
    int i = ck();
    if (i >= 0) {}
    int k;
    do
    {
      return i;
      i &= 0x7F;
      j = ck();
      if (j >= 0) {
        return i | j << 7;
      }
      i |= (j & 0x7F) << 7;
      j = ck();
      if (j >= 0) {
        return i | j << 14;
      }
      i |= (j & 0x7F) << 14;
      k = ck();
      if (k >= 0) {
        return i | k << 21;
      }
      j = ck();
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
      if (ck() >= 0) {
        break;
      }
      j += 1;
    }
    label133:
    throw zzarz.ct();
  }
  
  public long ce()
    throws IOException
  {
    int i = 0;
    long l = 0L;
    while (i < 64)
    {
      int j = ck();
      l |= (j & 0x7F) << i;
      if ((j & 0x80) == 0) {
        return l;
      }
      i += 7;
    }
    throw zzarz.ct();
  }
  
  public int cf()
    throws IOException
  {
    return ck() & 0xFF | (ck() & 0xFF) << 8 | (ck() & 0xFF) << 16 | (ck() & 0xFF) << 24;
  }
  
  public long cg()
    throws IOException
  {
    int i = ck();
    int j = ck();
    int k = ck();
    int m = ck();
    int n = ck();
    int i1 = ck();
    int i2 = ck();
    int i3 = ck();
    long l = i;
    return (j & 0xFF) << 8 | l & 0xFF | (k & 0xFF) << 16 | (m & 0xFF) << 24 | (n & 0xFF) << 32 | (i1 & 0xFF) << 40 | (i2 & 0xFF) << 48 | (i3 & 0xFF) << 56;
  }
  
  public int ci()
  {
    if (this.btB == Integer.MAX_VALUE) {
      return -1;
    }
    int i = this.btz;
    return this.btB - i;
  }
  
  public boolean cj()
  {
    return this.btz == this.btx;
  }
  
  public byte ck()
    throws IOException
  {
    if (this.btz == this.btx) {
      throw zzarz.cr();
    }
    byte[] arrayOfByte = this.buffer;
    int i = this.btz;
    this.btz = (i + 1);
    return arrayOfByte[i];
  }
  
  public int getPosition()
  {
    return this.btz - this.btw;
  }
  
  public byte[] readBytes()
    throws IOException
  {
    int i = cd();
    if (i < 0) {
      throw zzarz.cs();
    }
    if (i == 0) {
      return zzasd.btY;
    }
    if (i > this.btx - this.btz) {
      throw zzarz.cr();
    }
    byte[] arrayOfByte = new byte[i];
    System.arraycopy(this.buffer, this.btz, arrayOfByte, 0, i);
    this.btz = (i + this.btz);
    return arrayOfByte;
  }
  
  public double readDouble()
    throws IOException
  {
    return Double.longBitsToDouble(cg());
  }
  
  public float readFloat()
    throws IOException
  {
    return Float.intBitsToFloat(cf());
  }
  
  public String readString()
    throws IOException
  {
    int i = cd();
    if (i < 0) {
      throw zzarz.cs();
    }
    if (i > this.btx - this.btz) {
      throw zzarz.cr();
    }
    String str = new String(this.buffer, this.btz, i, zzary.UTF_8);
    this.btz = (i + this.btz);
    return str;
  }
  
  public void zza(zzasa paramzzasa)
    throws IOException
  {
    int i = cd();
    if (this.btC >= this.btD) {
      throw zzarz.cx();
    }
    i = zzagt(i);
    this.btC += 1;
    paramzzasa.zzb(this);
    zzagq(0);
    this.btC -= 1;
    zzagu(i);
  }
  
  public void zza(zzasa paramzzasa, int paramInt)
    throws IOException
  {
    if (this.btC >= this.btD) {
      throw zzarz.cx();
    }
    this.btC += 1;
    paramzzasa.zzb(this);
    zzagq(zzasd.zzak(paramInt, 4));
    this.btC -= 1;
  }
  
  public byte[] zzae(int paramInt1, int paramInt2)
  {
    if (paramInt2 == 0) {
      return zzasd.btY;
    }
    byte[] arrayOfByte = new byte[paramInt2];
    int i = this.btw;
    System.arraycopy(this.buffer, i + paramInt1, arrayOfByte, 0, paramInt2);
    return arrayOfByte;
  }
  
  public void zzagq(int paramInt)
    throws zzarz
  {
    if (this.btA != paramInt) {
      throw zzarz.cv();
    }
  }
  
  public boolean zzagr(int paramInt)
    throws IOException
  {
    switch (zzasd.zzahk(paramInt))
    {
    default: 
      throw zzarz.cw();
    case 0: 
      bY();
      return true;
    case 1: 
      cg();
      return true;
    case 2: 
      zzagw(cd());
      return true;
    case 3: 
      bV();
      zzagq(zzasd.zzak(zzasd.zzahl(paramInt), 4));
      return true;
    case 4: 
      return false;
    }
    cf();
    return true;
  }
  
  public int zzagt(int paramInt)
    throws zzarz
  {
    if (paramInt < 0) {
      throw zzarz.cs();
    }
    paramInt = this.btz + paramInt;
    int i = this.btB;
    if (paramInt > i) {
      throw zzarz.cr();
    }
    this.btB = paramInt;
    ch();
    return i;
  }
  
  public void zzagu(int paramInt)
  {
    this.btB = paramInt;
    ch();
  }
  
  public void zzagv(int paramInt)
  {
    if (paramInt > this.btz - this.btw)
    {
      int i = this.btz;
      int j = this.btw;
      throw new IllegalArgumentException(50 + "Position " + paramInt + " is beyond current " + (i - j));
    }
    if (paramInt < 0) {
      throw new IllegalArgumentException(24 + "Bad position " + paramInt);
    }
    this.btz = (this.btw + paramInt);
  }
  
  public void zzagw(int paramInt)
    throws IOException
  {
    if (paramInt < 0) {
      throw zzarz.cs();
    }
    if (this.btz + paramInt > this.btB)
    {
      zzagw(this.btB - this.btz);
      throw zzarz.cr();
    }
    if (paramInt <= this.btx - this.btz)
    {
      this.btz += paramInt;
      return;
    }
    throw zzarz.cr();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzars.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */