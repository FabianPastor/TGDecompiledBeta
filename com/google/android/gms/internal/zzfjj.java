package com.google.android.gms.internal;

import java.io.IOException;

public final class zzfjj
{
  private final byte[] buffer;
  private int zzpfm;
  private int zzpfn = 64;
  private int zzpfo = 67108864;
  private int zzpfr;
  private int zzpft;
  private int zzpfu = Integer.MAX_VALUE;
  private final int zzpfw;
  private final int zzpmz;
  private int zzpna;
  private int zzpnb;
  
  private zzfjj(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this.buffer = paramArrayOfByte;
    this.zzpmz = paramInt1;
    paramInt2 = paramInt1 + paramInt2;
    this.zzpna = paramInt2;
    this.zzpfw = paramInt2;
    this.zzpnb = paramInt1;
  }
  
  private final void zzcwq()
  {
    this.zzpna += this.zzpfr;
    int i = this.zzpna;
    if (i > this.zzpfu)
    {
      this.zzpfr = (i - this.zzpfu);
      this.zzpna -= this.zzpfr;
      return;
    }
    this.zzpfr = 0;
  }
  
  private final byte zzcwr()
    throws IOException
  {
    if (this.zzpnb == this.zzpna) {
      throw zzfjr.zzdai();
    }
    byte[] arrayOfByte = this.buffer;
    int i = this.zzpnb;
    this.zzpnb = (i + 1);
    return arrayOfByte[i];
  }
  
  private final void zzku(int paramInt)
    throws IOException
  {
    if (paramInt < 0) {
      throw zzfjr.zzdaj();
    }
    if (this.zzpnb + paramInt > this.zzpfu)
    {
      zzku(this.zzpfu - this.zzpnb);
      throw zzfjr.zzdai();
    }
    if (paramInt <= this.zzpna - this.zzpnb)
    {
      this.zzpnb += paramInt;
      return;
    }
    throw zzfjr.zzdai();
  }
  
  public static zzfjj zzn(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return new zzfjj(paramArrayOfByte, 0, paramInt2);
  }
  
  public final int getPosition()
  {
    return this.zzpnb - this.zzpmz;
  }
  
  public final String readString()
    throws IOException
  {
    int i = zzcwi();
    if (i < 0) {
      throw zzfjr.zzdaj();
    }
    if (i > this.zzpna - this.zzpnb) {
      throw zzfjr.zzdai();
    }
    String str = new String(this.buffer, this.zzpnb, i, zzfjq.UTF_8);
    this.zzpnb = (i + this.zzpnb);
    return str;
  }
  
  public final void zza(zzfjs paramzzfjs)
    throws IOException
  {
    int i = zzcwi();
    if (this.zzpfm >= this.zzpfn) {
      throw zzfjr.zzdal();
    }
    i = zzks(i);
    this.zzpfm += 1;
    paramzzfjs.zza(this);
    zzkp(0);
    this.zzpfm -= 1;
    zzkt(i);
  }
  
  public final byte[] zzal(int paramInt1, int paramInt2)
  {
    if (paramInt2 == 0) {
      return zzfjv.zzpnv;
    }
    byte[] arrayOfByte = new byte[paramInt2];
    int i = this.zzpmz;
    System.arraycopy(this.buffer, i + paramInt1, arrayOfByte, 0, paramInt2);
    return arrayOfByte;
  }
  
  final void zzam(int paramInt1, int paramInt2)
  {
    if (paramInt1 > this.zzpnb - this.zzpmz)
    {
      paramInt2 = this.zzpnb;
      int i = this.zzpmz;
      throw new IllegalArgumentException(50 + "Position " + paramInt1 + " is beyond current " + (paramInt2 - i));
    }
    if (paramInt1 < 0) {
      throw new IllegalArgumentException(24 + "Bad position " + paramInt1);
    }
    this.zzpnb = (this.zzpmz + paramInt1);
    this.zzpft = paramInt2;
  }
  
  public final int zzcvt()
    throws IOException
  {
    if (this.zzpnb == this.zzpna)
    {
      this.zzpft = 0;
      return 0;
    }
    this.zzpft = zzcwi();
    if (this.zzpft == 0) {
      throw new zzfjr("Protocol message contained an invalid tag (zero).");
    }
    return this.zzpft;
  }
  
  public final boolean zzcvz()
    throws IOException
  {
    return zzcwi() != 0;
  }
  
  public final int zzcwi()
    throws IOException
  {
    int i = zzcwr();
    if (i >= 0) {}
    int k;
    do
    {
      return i;
      i &= 0x7F;
      j = zzcwr();
      if (j >= 0) {
        return i | j << 7;
      }
      i |= (j & 0x7F) << 7;
      j = zzcwr();
      if (j >= 0) {
        return i | j << 14;
      }
      i |= (j & 0x7F) << 14;
      k = zzcwr();
      if (k >= 0) {
        return i | k << 21;
      }
      j = zzcwr();
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
      if (zzcwr() >= 0) {
        break;
      }
      j += 1;
    }
    label133:
    throw zzfjr.zzdak();
  }
  
  public final int zzcwk()
  {
    if (this.zzpfu == Integer.MAX_VALUE) {
      return -1;
    }
    int i = this.zzpnb;
    return this.zzpfu - i;
  }
  
  public final long zzcwn()
    throws IOException
  {
    int i = 0;
    long l = 0L;
    while (i < 64)
    {
      int j = zzcwr();
      l |= (j & 0x7F) << i;
      if ((j & 0x80) == 0) {
        return l;
      }
      i += 7;
    }
    throw zzfjr.zzdak();
  }
  
  public final int zzcwo()
    throws IOException
  {
    return zzcwr() & 0xFF | (zzcwr() & 0xFF) << 8 | (zzcwr() & 0xFF) << 16 | (zzcwr() & 0xFF) << 24;
  }
  
  public final long zzcwp()
    throws IOException
  {
    int i = zzcwr();
    int j = zzcwr();
    int k = zzcwr();
    int m = zzcwr();
    int n = zzcwr();
    int i1 = zzcwr();
    int i2 = zzcwr();
    int i3 = zzcwr();
    long l = i;
    return (j & 0xFF) << 8 | l & 0xFF | (k & 0xFF) << 16 | (m & 0xFF) << 24 | (n & 0xFF) << 32 | (i1 & 0xFF) << 40 | (i2 & 0xFF) << 48 | (i3 & 0xFF) << 56;
  }
  
  public final void zzkp(int paramInt)
    throws zzfjr
  {
    if (this.zzpft != paramInt) {
      throw new zzfjr("Protocol message end-group tag did not match expected tag.");
    }
  }
  
  public final boolean zzkq(int paramInt)
    throws IOException
  {
    switch (paramInt & 0x7)
    {
    default: 
      throw new zzfjr("Protocol message tag had invalid wire type.");
    case 0: 
      zzcwi();
      return true;
    case 1: 
      zzcwp();
      return true;
    case 2: 
      zzku(zzcwi());
      return true;
    case 3: 
      int i;
      do
      {
        i = zzcvt();
      } while ((i != 0) && (zzkq(i)));
      zzkp(paramInt >>> 3 << 3 | 0x4);
      return true;
    case 4: 
      return false;
    }
    zzcwo();
    return true;
  }
  
  public final int zzks(int paramInt)
    throws zzfjr
  {
    if (paramInt < 0) {
      throw zzfjr.zzdaj();
    }
    paramInt = this.zzpnb + paramInt;
    int i = this.zzpfu;
    if (paramInt > i) {
      throw zzfjr.zzdai();
    }
    this.zzpfu = paramInt;
    zzcwq();
    return i;
  }
  
  public final void zzkt(int paramInt)
  {
    this.zzpfu = paramInt;
    zzcwq();
  }
  
  public final void zzmg(int paramInt)
  {
    zzam(paramInt, this.zzpft);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzfjj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */