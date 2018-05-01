package com.google.android.gms.internal.config;

import java.io.IOException;

public final class zzay
{
  private final byte[] buffer;
  private final int zzbx;
  private final int zzby;
  private int zzbz;
  private int zzca;
  private int zzcb;
  private int zzcc;
  private int zzcd = Integer.MAX_VALUE;
  private int zzce;
  private int zzcf = 64;
  private int zzcg = 67108864;
  
  private zzay(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this.buffer = paramArrayOfByte;
    this.zzbx = 0;
    paramInt1 = paramInt2 + 0;
    this.zzbz = paramInt1;
    this.zzby = paramInt1;
    this.zzcb = 0;
  }
  
  public static zzay zza(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return new zzay(paramArrayOfByte, 0, paramInt2);
  }
  
  private final void zzab()
  {
    this.zzbz += this.zzca;
    int i = this.zzbz;
    if (i > this.zzcd)
    {
      this.zzca = (i - this.zzcd);
      this.zzbz -= this.zzca;
    }
    for (;;)
    {
      return;
      this.zzca = 0;
    }
  }
  
  private final byte zzac()
    throws IOException
  {
    if (this.zzcb == this.zzbz) {
      throw zzbg.zzag();
    }
    byte[] arrayOfByte = this.buffer;
    int i = this.zzcb;
    this.zzcb = (i + 1);
    return arrayOfByte[i];
  }
  
  private final void zzg(int paramInt)
    throws zzbg
  {
    if (this.zzcc != paramInt) {
      throw new zzbg("Protocol message end-group tag did not match expected tag.");
    }
  }
  
  private final void zzi(int paramInt)
    throws IOException
  {
    if (paramInt < 0) {
      throw zzbg.zzah();
    }
    if (this.zzcb + paramInt > this.zzcd)
    {
      zzi(this.zzcd - this.zzcb);
      throw zzbg.zzag();
    }
    if (paramInt <= this.zzbz - this.zzcb)
    {
      this.zzcb += paramInt;
      return;
    }
    throw zzbg.zzag();
  }
  
  public final int getPosition()
  {
    return this.zzcb - this.zzbx;
  }
  
  public final byte[] readBytes()
    throws IOException
  {
    int i = zzz();
    if (i < 0) {
      throw zzbg.zzah();
    }
    byte[] arrayOfByte;
    if (i == 0) {
      arrayOfByte = zzbk.zzde;
    }
    for (;;)
    {
      return arrayOfByte;
      if (i > this.zzbz - this.zzcb) {
        throw zzbg.zzag();
      }
      arrayOfByte = new byte[i];
      System.arraycopy(this.buffer, this.zzcb, arrayOfByte, 0, i);
      this.zzcb = (i + this.zzcb);
    }
  }
  
  public final String readString()
    throws IOException
  {
    int i = zzz();
    if (i < 0) {
      throw zzbg.zzah();
    }
    if (i > this.zzbz - this.zzcb) {
      throw zzbg.zzag();
    }
    String str = new String(this.buffer, this.zzcb, i, zzbf.UTF_8);
    this.zzcb = (i + this.zzcb);
    return str;
  }
  
  public final void zza(zzbh paramzzbh)
    throws IOException
  {
    int i = zzz();
    if (this.zzce >= this.zzcf) {
      throw new zzbg("Protocol message had too many levels of nesting.  May be malicious.  Use CodedInputStream.setRecursionLimit() to increase the depth limit.");
    }
    if (i < 0) {
      throw zzbg.zzah();
    }
    i += this.zzcb;
    int j = this.zzcd;
    if (i > j) {
      throw zzbg.zzag();
    }
    this.zzcd = i;
    zzab();
    this.zzce += 1;
    paramzzbh.zza(this);
    zzg(0);
    this.zzce -= 1;
    this.zzcd = j;
    zzab();
  }
  
  public final byte[] zza(int paramInt1, int paramInt2)
  {
    byte[] arrayOfByte;
    if (paramInt2 == 0) {
      arrayOfByte = zzbk.zzde;
    }
    for (;;)
    {
      return arrayOfByte;
      arrayOfByte = new byte[paramInt2];
      int i = this.zzbx;
      System.arraycopy(this.buffer, i + paramInt1, arrayOfByte, 0, paramInt2);
    }
  }
  
  public final long zzaa()
    throws IOException
  {
    int i = zzac();
    int j = zzac();
    int k = zzac();
    int m = zzac();
    int n = zzac();
    int i1 = zzac();
    int i2 = zzac();
    int i3 = zzac();
    long l = i;
    return (j & 0xFF) << 8 | l & 0xFF | (k & 0xFF) << 16 | (m & 0xFF) << 24 | (n & 0xFF) << 32 | (i1 & 0xFF) << 40 | (i2 & 0xFF) << 48 | (i3 & 0xFF) << 56;
  }
  
  final void zzb(int paramInt1, int paramInt2)
  {
    if (paramInt1 > this.zzcb - this.zzbx)
    {
      paramInt2 = this.zzcb;
      int i = this.zzbx;
      throw new IllegalArgumentException(50 + "Position " + paramInt1 + " is beyond current " + (paramInt2 - i));
    }
    if (paramInt1 < 0) {
      throw new IllegalArgumentException(24 + "Bad position " + paramInt1);
    }
    this.zzcb = (this.zzbx + paramInt1);
    this.zzcc = paramInt2;
  }
  
  public final boolean zzh(int paramInt)
    throws IOException
  {
    boolean bool = true;
    switch (paramInt & 0x7)
    {
    default: 
      throw new zzbg("Protocol message tag had invalid wire type.");
    case 0: 
      zzz();
    }
    for (;;)
    {
      return bool;
      zzaa();
      continue;
      zzi(zzz());
      continue;
      int i;
      do
      {
        i = zzy();
      } while ((i != 0) && (zzh(i)));
      zzg(paramInt >>> 3 << 3 | 0x4);
      continue;
      bool = false;
      continue;
      zzac();
      zzac();
      zzac();
      zzac();
    }
  }
  
  public final int zzy()
    throws IOException
  {
    int i = 0;
    if (this.zzcb == this.zzbz) {
      this.zzcc = 0;
    }
    for (;;)
    {
      return i;
      this.zzcc = zzz();
      if (this.zzcc == 0) {
        throw new zzbg("Protocol message contained an invalid tag (zero).");
      }
      i = this.zzcc;
    }
  }
  
  public final int zzz()
    throws IOException
  {
    int i = zzac();
    if (i >= 0) {}
    int k;
    do
    {
      for (;;)
      {
        return i;
        i &= 0x7F;
        j = zzac();
        if (j >= 0)
        {
          i |= j << 7;
        }
        else
        {
          j = i | (j & 0x7F) << 7;
          i = zzac();
          if (i >= 0)
          {
            i = j | i << 14;
          }
          else
          {
            k = j | (i & 0x7F) << 14;
            i = zzac();
            if (i < 0) {
              break;
            }
            i = k | i << 21;
          }
        }
      }
      j = zzac();
      k = k | (i & 0x7F) << 21 | j << 28;
      i = k;
    } while (j >= 0);
    for (int j = 0;; j++)
    {
      if (j >= 5) {
        break label141;
      }
      i = k;
      if (zzac() >= 0) {
        break;
      }
    }
    label141:
    throw new zzbg("CodedInputStream encountered a malformed varint.");
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/config/zzay.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */