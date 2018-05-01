package com.google.android.gms.internal.measurement;

import java.io.IOException;

public final class zzaba
{
  private final byte[] buffer;
  private int zzbtp = 64;
  private int zzbtq = 67108864;
  private int zzbtu;
  private int zzbtw = Integer.MAX_VALUE;
  private final int zzbza;
  private final int zzbzb;
  private int zzbzc;
  private int zzbzd;
  private int zzbze;
  private int zzbzf;
  
  private zzaba(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this.buffer = paramArrayOfByte;
    this.zzbza = paramInt1;
    paramInt2 = paramInt1 + paramInt2;
    this.zzbzc = paramInt2;
    this.zzbzb = paramInt2;
    this.zzbzd = paramInt1;
  }
  
  public static zzaba zza(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return new zzaba(paramArrayOfByte, 0, paramInt2);
  }
  
  private final void zzap(int paramInt)
    throws IOException
  {
    if (paramInt < 0) {
      throw zzabi.zzwc();
    }
    if (this.zzbzd + paramInt > this.zzbtw)
    {
      zzap(this.zzbtw - this.zzbzd);
      throw zzabi.zzwb();
    }
    if (paramInt <= this.zzbzc - this.zzbzd)
    {
      this.zzbzd += paramInt;
      return;
    }
    throw zzabi.zzwb();
  }
  
  public static zzaba zzj(byte[] paramArrayOfByte)
  {
    return zza(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  private final void zzts()
  {
    this.zzbzc += this.zzbtu;
    int i = this.zzbzc;
    if (i > this.zzbtw)
    {
      this.zzbtu = (i - this.zzbtw);
      this.zzbzc -= this.zzbtu;
    }
    for (;;)
    {
      return;
      this.zzbtu = 0;
    }
  }
  
  private final byte zzvx()
    throws IOException
  {
    if (this.zzbzd == this.zzbzc) {
      throw zzabi.zzwb();
    }
    byte[] arrayOfByte = this.buffer;
    int i = this.zzbzd;
    this.zzbzd = (i + 1);
    return arrayOfByte[i];
  }
  
  public final int getPosition()
  {
    return this.zzbzd - this.zzbza;
  }
  
  public final String readString()
    throws IOException
  {
    int i = zzvs();
    if (i < 0) {
      throw zzabi.zzwc();
    }
    if (i > this.zzbzc - this.zzbzd) {
      throw zzabi.zzwb();
    }
    String str = new String(this.buffer, this.zzbzd, i, zzabh.UTF_8);
    this.zzbzd = (i + this.zzbzd);
    return str;
  }
  
  public final void zza(zzabj paramzzabj)
    throws IOException
  {
    int i = zzvs();
    if (this.zzbzf >= this.zzbtp) {
      throw zzabi.zzwe();
    }
    i = zzah(i);
    this.zzbzf += 1;
    paramzzabj.zzb(this);
    zzal(0);
    this.zzbzf -= 1;
    zzan(i);
  }
  
  public final void zza(zzabj paramzzabj, int paramInt)
    throws IOException
  {
    if (this.zzbzf >= this.zzbtp) {
      throw zzabi.zzwe();
    }
    this.zzbzf += 1;
    paramzzabj.zzb(this);
    zzal(paramInt << 3 | 0x4);
    this.zzbzf -= 1;
  }
  
  public final int zzah(int paramInt)
    throws zzabi
  {
    if (paramInt < 0) {
      throw zzabi.zzwc();
    }
    paramInt = this.zzbzd + paramInt;
    int i = this.zzbtw;
    if (paramInt > i) {
      throw zzabi.zzwb();
    }
    this.zzbtw = paramInt;
    zzts();
    return i;
  }
  
  public final void zzal(int paramInt)
    throws zzabi
  {
    if (this.zzbze != paramInt) {
      throw new zzabi("Protocol message end-group tag did not match expected tag.");
    }
  }
  
  public final boolean zzam(int paramInt)
    throws IOException
  {
    boolean bool = true;
    switch (paramInt & 0x7)
    {
    default: 
      throw new zzabi("Protocol message tag had invalid wire type.");
    case 0: 
      zzvs();
    }
    for (;;)
    {
      return bool;
      zzvv();
      continue;
      zzap(zzvs());
      continue;
      int i;
      do
      {
        i = zzvo();
      } while ((i != 0) && (zzam(i)));
      zzal(paramInt >>> 3 << 3 | 0x4);
      continue;
      bool = false;
      continue;
      zzvu();
    }
  }
  
  public final void zzan(int paramInt)
  {
    this.zzbtw = paramInt;
    zzts();
  }
  
  public final void zzao(int paramInt)
  {
    zzd(paramInt, this.zzbze);
  }
  
  public final byte[] zzc(int paramInt1, int paramInt2)
  {
    byte[] arrayOfByte;
    if (paramInt2 == 0) {
      arrayOfByte = zzabm.zzcae;
    }
    for (;;)
    {
      return arrayOfByte;
      arrayOfByte = new byte[paramInt2];
      int i = this.zzbza;
      System.arraycopy(this.buffer, i + paramInt1, arrayOfByte, 0, paramInt2);
    }
  }
  
  final void zzd(int paramInt1, int paramInt2)
  {
    if (paramInt1 > this.zzbzd - this.zzbza)
    {
      int i = this.zzbzd;
      paramInt2 = this.zzbza;
      throw new IllegalArgumentException(50 + "Position " + paramInt1 + " is beyond current " + (i - paramInt2));
    }
    if (paramInt1 < 0) {
      throw new IllegalArgumentException(24 + "Bad position " + paramInt1);
    }
    this.zzbzd = (this.zzbza + paramInt1);
    this.zzbze = paramInt2;
  }
  
  public final int zzvo()
    throws IOException
  {
    int i = 0;
    if (this.zzbzd == this.zzbzc) {
      this.zzbze = 0;
    }
    for (;;)
    {
      return i;
      this.zzbze = zzvs();
      if (this.zzbze == 0) {
        throw new zzabi("Protocol message contained an invalid tag (zero).");
      }
      i = this.zzbze;
    }
  }
  
  public final long zzvp()
    throws IOException
  {
    return zzvt();
  }
  
  public final int zzvq()
    throws IOException
  {
    return zzvs();
  }
  
  public final boolean zzvr()
    throws IOException
  {
    if (zzvs() != 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public final int zzvs()
    throws IOException
  {
    int i = zzvx();
    if (i >= 0) {}
    int k;
    do
    {
      for (;;)
      {
        return i;
        j = i & 0x7F;
        i = zzvx();
        if (i >= 0)
        {
          i = j | i << 7;
        }
        else
        {
          j |= (i & 0x7F) << 7;
          i = zzvx();
          if (i >= 0)
          {
            i = j | i << 14;
          }
          else
          {
            i = j | (i & 0x7F) << 14;
            k = zzvx();
            if (k < 0) {
              break;
            }
            i |= k << 21;
          }
        }
      }
      j = zzvx();
      k = i | (k & 0x7F) << 21 | j << 28;
      i = k;
    } while (j >= 0);
    for (int j = 0;; j++)
    {
      if (j >= 5) {
        break label141;
      }
      i = k;
      if (zzvx() >= 0) {
        break;
      }
    }
    label141:
    throw zzabi.zzwd();
  }
  
  public final long zzvt()
    throws IOException
  {
    int i = 0;
    long l = 0L;
    while (i < 64)
    {
      int j = zzvx();
      l |= (j & 0x7F) << i;
      if ((j & 0x80) == 0) {
        return l;
      }
      i += 7;
    }
    throw zzabi.zzwd();
  }
  
  public final int zzvu()
    throws IOException
  {
    return zzvx() & 0xFF | (zzvx() & 0xFF) << 8 | (zzvx() & 0xFF) << 16 | (zzvx() & 0xFF) << 24;
  }
  
  public final long zzvv()
    throws IOException
  {
    int i = zzvx();
    int j = zzvx();
    int k = zzvx();
    int m = zzvx();
    int n = zzvx();
    int i1 = zzvx();
    int i2 = zzvx();
    int i3 = zzvx();
    long l = i;
    return (j & 0xFF) << 8 | l & 0xFF | (k & 0xFF) << 16 | (m & 0xFF) << 24 | (n & 0xFF) << 32 | (i1 & 0xFF) << 40 | (i2 & 0xFF) << 48 | (i3 & 0xFF) << 56;
  }
  
  public final int zzvw()
  {
    if (this.zzbtw == Integer.MAX_VALUE) {}
    for (int i = -1;; i = this.zzbtw - i)
    {
      return i;
      i = this.zzbzd;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzaba.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */