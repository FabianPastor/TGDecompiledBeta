package com.google.android.gms.internal;

import java.io.IOException;

public final class adg
{
  private final byte[] buffer;
  private int zzcse;
  private int zzcsf;
  private int zzcsg;
  private int zzcsh;
  private int zzcsi;
  private int zzcsj = Integer.MAX_VALUE;
  private int zzcsk;
  private int zzcsl = 64;
  private int zzcsm = 67108864;
  
  private adg(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this.buffer = paramArrayOfByte;
    this.zzcse = paramInt1;
    this.zzcsf = (paramInt1 + paramInt2);
    this.zzcsh = paramInt1;
  }
  
  public static adg zzH(byte[] paramArrayOfByte)
  {
    return zzb(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  private final void zzLJ()
  {
    this.zzcsf += this.zzcsg;
    int i = this.zzcsf;
    if (i > this.zzcsj)
    {
      this.zzcsg = (i - this.zzcsj);
      this.zzcsf -= this.zzcsg;
      return;
    }
    this.zzcsg = 0;
  }
  
  private final byte zzLL()
    throws IOException
  {
    if (this.zzcsh == this.zzcsf) {
      throw ado.zzLQ();
    }
    byte[] arrayOfByte = this.buffer;
    int i = this.zzcsh;
    this.zzcsh = (i + 1);
    return arrayOfByte[i];
  }
  
  public static adg zzb(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return new adg(paramArrayOfByte, 0, paramInt2);
  }
  
  private final void zzcq(int paramInt)
    throws IOException
  {
    if (paramInt < 0) {
      throw ado.zzLR();
    }
    if (this.zzcsh + paramInt > this.zzcsj)
    {
      zzcq(this.zzcsj - this.zzcsh);
      throw ado.zzLQ();
    }
    if (paramInt <= this.zzcsf - this.zzcsh)
    {
      this.zzcsh += paramInt;
      return;
    }
    throw ado.zzLQ();
  }
  
  public final int getPosition()
  {
    return this.zzcsh - this.zzcse;
  }
  
  public final byte[] readBytes()
    throws IOException
  {
    int i = zzLF();
    if (i < 0) {
      throw ado.zzLR();
    }
    if (i == 0) {
      return ads.zzcsI;
    }
    if (i > this.zzcsf - this.zzcsh) {
      throw ado.zzLQ();
    }
    byte[] arrayOfByte = new byte[i];
    System.arraycopy(this.buffer, this.zzcsh, arrayOfByte, 0, i);
    this.zzcsh = (i + this.zzcsh);
    return arrayOfByte;
  }
  
  public final String readString()
    throws IOException
  {
    int i = zzLF();
    if (i < 0) {
      throw ado.zzLR();
    }
    if (i > this.zzcsf - this.zzcsh) {
      throw ado.zzLQ();
    }
    String str = new String(this.buffer, this.zzcsh, i, adn.UTF_8);
    this.zzcsh = (i + this.zzcsh);
    return str;
  }
  
  public final int zzLA()
    throws IOException
  {
    if (this.zzcsh == this.zzcsf)
    {
      this.zzcsi = 0;
      return 0;
    }
    this.zzcsi = zzLF();
    if (this.zzcsi == 0) {
      throw new ado("Protocol message contained an invalid tag (zero).");
    }
    return this.zzcsi;
  }
  
  public final long zzLB()
    throws IOException
  {
    return zzLG();
  }
  
  public final int zzLC()
    throws IOException
  {
    return zzLF();
  }
  
  public final boolean zzLD()
    throws IOException
  {
    return zzLF() != 0;
  }
  
  public final long zzLE()
    throws IOException
  {
    long l = zzLG();
    return -(l & 1L) ^ l >>> 1;
  }
  
  public final int zzLF()
    throws IOException
  {
    int i = zzLL();
    if (i >= 0) {}
    int k;
    do
    {
      return i;
      i &= 0x7F;
      j = zzLL();
      if (j >= 0) {
        return i | j << 7;
      }
      i |= (j & 0x7F) << 7;
      j = zzLL();
      if (j >= 0) {
        return i | j << 14;
      }
      i |= (j & 0x7F) << 14;
      k = zzLL();
      if (k >= 0) {
        return i | k << 21;
      }
      j = zzLL();
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
      if (zzLL() >= 0) {
        break;
      }
      j += 1;
    }
    label133:
    throw ado.zzLS();
  }
  
  public final long zzLG()
    throws IOException
  {
    int i = 0;
    long l = 0L;
    while (i < 64)
    {
      int j = zzLL();
      l |= (j & 0x7F) << i;
      if ((j & 0x80) == 0) {
        return l;
      }
      i += 7;
    }
    throw ado.zzLS();
  }
  
  public final int zzLH()
    throws IOException
  {
    return zzLL() & 0xFF | (zzLL() & 0xFF) << 8 | (zzLL() & 0xFF) << 16 | (zzLL() & 0xFF) << 24;
  }
  
  public final long zzLI()
    throws IOException
  {
    int i = zzLL();
    int j = zzLL();
    int k = zzLL();
    int m = zzLL();
    int n = zzLL();
    int i1 = zzLL();
    int i2 = zzLL();
    int i3 = zzLL();
    long l = i;
    return (j & 0xFF) << 8 | l & 0xFF | (k & 0xFF) << 16 | (m & 0xFF) << 24 | (n & 0xFF) << 32 | (i1 & 0xFF) << 40 | (i2 & 0xFF) << 48 | (i3 & 0xFF) << 56;
  }
  
  public final int zzLK()
  {
    if (this.zzcsj == Integer.MAX_VALUE) {
      return -1;
    }
    int i = this.zzcsh;
    return this.zzcsj - i;
  }
  
  public final void zza(adp paramadp)
    throws IOException
  {
    int i = zzLF();
    if (this.zzcsk >= this.zzcsl) {
      throw ado.zzLT();
    }
    i = zzcn(i);
    this.zzcsk += 1;
    paramadp.zza(this);
    zzcl(0);
    this.zzcsk -= 1;
    zzco(i);
  }
  
  public final void zza(adp paramadp, int paramInt)
    throws IOException
  {
    if (this.zzcsk >= this.zzcsl) {
      throw ado.zzLT();
    }
    this.zzcsk += 1;
    paramadp.zza(this);
    zzcl(paramInt << 3 | 0x4);
    this.zzcsk -= 1;
  }
  
  public final void zzcl(int paramInt)
    throws ado
  {
    if (this.zzcsi != paramInt) {
      throw new ado("Protocol message end-group tag did not match expected tag.");
    }
  }
  
  public final boolean zzcm(int paramInt)
    throws IOException
  {
    switch (paramInt & 0x7)
    {
    default: 
      throw new ado("Protocol message tag had invalid wire type.");
    case 0: 
      zzLF();
      return true;
    case 1: 
      zzLI();
      return true;
    case 2: 
      zzcq(zzLF());
      return true;
    case 3: 
      int i;
      do
      {
        i = zzLA();
      } while ((i != 0) && (zzcm(i)));
      zzcl(paramInt >>> 3 << 3 | 0x4);
      return true;
    case 4: 
      return false;
    }
    zzLH();
    return true;
  }
  
  public final int zzcn(int paramInt)
    throws ado
  {
    if (paramInt < 0) {
      throw ado.zzLR();
    }
    paramInt = this.zzcsh + paramInt;
    int i = this.zzcsj;
    if (paramInt > i) {
      throw ado.zzLQ();
    }
    this.zzcsj = paramInt;
    zzLJ();
    return i;
  }
  
  public final void zzco(int paramInt)
  {
    this.zzcsj = paramInt;
    zzLJ();
  }
  
  public final void zzcp(int paramInt)
  {
    zzq(paramInt, this.zzcsi);
  }
  
  public final byte[] zzp(int paramInt1, int paramInt2)
  {
    if (paramInt2 == 0) {
      return ads.zzcsI;
    }
    byte[] arrayOfByte = new byte[paramInt2];
    int i = this.zzcse;
    System.arraycopy(this.buffer, i + paramInt1, arrayOfByte, 0, paramInt2);
    return arrayOfByte;
  }
  
  final void zzq(int paramInt1, int paramInt2)
  {
    if (paramInt1 > this.zzcsh - this.zzcse)
    {
      paramInt2 = this.zzcsh;
      int i = this.zzcse;
      throw new IllegalArgumentException(50 + "Position " + paramInt1 + " is beyond current " + (paramInt2 - i));
    }
    if (paramInt1 < 0) {
      throw new IllegalArgumentException(24 + "Bad position " + paramInt1);
    }
    this.zzcsh = (this.zzcse + paramInt1);
    this.zzcsi = paramInt2;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/adg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */