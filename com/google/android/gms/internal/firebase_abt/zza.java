package com.google.android.gms.internal.firebase_abt;

import java.io.IOException;

public final class zza
{
  private final byte[] buffer;
  private final int zzh;
  private final int zzi;
  private int zzj;
  private int zzk;
  private int zzl;
  private int zzm;
  private int zzn = Integer.MAX_VALUE;
  private int zzo;
  private int zzp = 64;
  private int zzq = 67108864;
  
  private zza(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this.buffer = paramArrayOfByte;
    this.zzh = paramInt1;
    paramInt2 = paramInt1 + paramInt2;
    this.zzj = paramInt2;
    this.zzi = paramInt2;
    this.zzl = paramInt1;
  }
  
  public static zza zza(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return new zza(paramArrayOfByte, 0, paramInt2);
  }
  
  private final void zzc(int paramInt)
    throws IOException
  {
    if (paramInt < 0) {
      throw zzi.zzm();
    }
    if (this.zzl + paramInt > this.zzn)
    {
      zzc(this.zzn - this.zzl);
      throw zzi.zzl();
    }
    if (paramInt <= this.zzj - this.zzl)
    {
      this.zzl += paramInt;
      return;
    }
    throw zzi.zzl();
  }
  
  private final void zzh()
  {
    this.zzj += this.zzk;
    int i = this.zzj;
    if (i > this.zzn)
    {
      this.zzk = (i - this.zzn);
      this.zzj -= this.zzk;
    }
    for (;;)
    {
      return;
      this.zzk = 0;
    }
  }
  
  private final byte zzi()
    throws IOException
  {
    if (this.zzl == this.zzj) {
      throw zzi.zzl();
    }
    byte[] arrayOfByte = this.buffer;
    int i = this.zzl;
    this.zzl = (i + 1);
    return arrayOfByte[i];
  }
  
  public final int getPosition()
  {
    return this.zzl - this.zzh;
  }
  
  public final String readString()
    throws IOException
  {
    int i = zzg();
    if (i < 0) {
      throw zzi.zzm();
    }
    if (i > this.zzj - this.zzl) {
      throw zzi.zzl();
    }
    String str = new String(this.buffer, this.zzl, i, zzh.UTF_8);
    this.zzl = (i + this.zzl);
    return str;
  }
  
  public final void zza(int paramInt)
    throws zzi
  {
    if (this.zzm != paramInt) {
      throw new zzi("Protocol message end-group tag did not match expected tag.");
    }
  }
  
  public final void zza(zzj paramzzj)
    throws IOException
  {
    int i = zzg();
    if (this.zzo >= this.zzp) {
      throw new zzi("Protocol message had too many levels of nesting.  May be malicious.  Use CodedInputStream.setRecursionLimit() to increase the depth limit.");
    }
    if (i < 0) {
      throw zzi.zzm();
    }
    i += this.zzl;
    int j = this.zzn;
    if (i > j) {
      throw zzi.zzl();
    }
    this.zzn = i;
    zzh();
    this.zzo += 1;
    paramzzj.zza(this);
    zza(0);
    this.zzo -= 1;
    this.zzn = j;
    zzh();
  }
  
  public final byte[] zza(int paramInt1, int paramInt2)
  {
    byte[] arrayOfByte;
    if (paramInt2 == 0) {
      arrayOfByte = zzm.zzao;
    }
    for (;;)
    {
      return arrayOfByte;
      arrayOfByte = new byte[paramInt2];
      int i = this.zzh;
      System.arraycopy(this.buffer, i + paramInt1, arrayOfByte, 0, paramInt2);
    }
  }
  
  final void zzb(int paramInt1, int paramInt2)
  {
    if (paramInt1 > this.zzl - this.zzh)
    {
      paramInt2 = this.zzl;
      int i = this.zzh;
      throw new IllegalArgumentException(50 + "Position " + paramInt1 + " is beyond current " + (paramInt2 - i));
    }
    if (paramInt1 < 0) {
      throw new IllegalArgumentException(24 + "Bad position " + paramInt1);
    }
    this.zzl = (this.zzh + paramInt1);
    this.zzm = 106;
  }
  
  public final boolean zzb(int paramInt)
    throws IOException
  {
    boolean bool = true;
    switch (paramInt & 0x7)
    {
    default: 
      throw new zzi("Protocol message tag had invalid wire type.");
    case 0: 
      zzg();
    }
    for (;;)
    {
      return bool;
      zzi();
      zzi();
      zzi();
      zzi();
      zzi();
      zzi();
      zzi();
      zzi();
      continue;
      zzc(zzg());
      continue;
      int i;
      do
      {
        i = zzd();
      } while ((i != 0) && (zzb(i)));
      zza(paramInt >>> 3 << 3 | 0x4);
      continue;
      bool = false;
      continue;
      zzi();
      zzi();
      zzi();
      zzi();
    }
  }
  
  public final int zzd()
    throws IOException
  {
    int i = 0;
    if (this.zzl == this.zzj) {
      this.zzm = 0;
    }
    for (;;)
    {
      return i;
      this.zzm = zzg();
      if (this.zzm == 0) {
        throw new zzi("Protocol message contained an invalid tag (zero).");
      }
      i = this.zzm;
    }
  }
  
  public final long zze()
    throws IOException
  {
    int i = 0;
    long l = 0L;
    while (i < 64)
    {
      int j = zzi();
      l |= (j & 0x7F) << i;
      if ((j & 0x80) == 0) {
        return l;
      }
      i += 7;
    }
    throw zzi.zzn();
  }
  
  public final int zzf()
    throws IOException
  {
    return zzg();
  }
  
  public final int zzg()
    throws IOException
  {
    int i = zzi();
    if (i >= 0) {}
    int k;
    do
    {
      for (;;)
      {
        return i;
        j = i & 0x7F;
        i = zzi();
        if (i >= 0)
        {
          i = j | i << 7;
        }
        else
        {
          i = j | (i & 0x7F) << 7;
          j = zzi();
          if (j >= 0)
          {
            i |= j << 14;
          }
          else
          {
            k = i | (j & 0x7F) << 14;
            i = zzi();
            if (i < 0) {
              break;
            }
            i = k | i << 21;
          }
        }
      }
      j = zzi();
      k = k | (i & 0x7F) << 21 | j << 28;
      i = k;
    } while (j >= 0);
    for (int j = 0;; j++)
    {
      if (j >= 5) {
        break label141;
      }
      i = k;
      if (zzi() >= 0) {
        break;
      }
    }
    label141:
    throw zzi.zzn();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/firebase_abt/zza.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */