package com.google.android.gms.internal;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;

public final class zzard
{
  private final ByteBuffer bqu;
  
  private zzard(ByteBuffer paramByteBuffer)
  {
    this.bqu = paramByteBuffer;
    this.bqu.order(ByteOrder.LITTLE_ENDIAN);
  }
  
  private zzard(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this(ByteBuffer.wrap(paramArrayOfByte, paramInt1, paramInt2));
  }
  
  private static int zza(CharSequence paramCharSequence, int paramInt)
  {
    int m = paramCharSequence.length();
    int i = 0;
    if (paramInt < m)
    {
      int n = paramCharSequence.charAt(paramInt);
      int j;
      if (n < 2048)
      {
        i += (127 - n >>> 31);
        j = paramInt;
      }
      for (;;)
      {
        paramInt = j + 1;
        break;
        int k = i + 2;
        j = paramInt;
        i = k;
        if (55296 <= n)
        {
          j = paramInt;
          i = k;
          if (n <= 57343)
          {
            if (Character.codePointAt(paramCharSequence, paramInt) < 65536) {
              throw new IllegalArgumentException(39 + "Unpaired surrogate at index " + paramInt);
            }
            j = paramInt + 1;
            i = k;
          }
        }
      }
    }
    return i;
  }
  
  private static int zza(CharSequence paramCharSequence, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    int k = paramCharSequence.length();
    int j = 0;
    int m = paramInt1 + paramInt2;
    paramInt2 = j;
    while ((paramInt2 < k) && (paramInt2 + paramInt1 < m))
    {
      j = paramCharSequence.charAt(paramInt2);
      if (j >= 128) {
        break;
      }
      paramArrayOfByte[(paramInt1 + paramInt2)] = ((byte)j);
      paramInt2 += 1;
    }
    if (paramInt2 == k) {
      return paramInt1 + k;
    }
    paramInt1 += paramInt2;
    if (paramInt2 < k)
    {
      int i = paramCharSequence.charAt(paramInt2);
      if ((i < 128) && (paramInt1 < m))
      {
        j = paramInt1 + 1;
        paramArrayOfByte[paramInt1] = ((byte)i);
        paramInt1 = j;
      }
      for (;;)
      {
        paramInt2 += 1;
        break;
        if ((i < 2048) && (paramInt1 <= m - 2))
        {
          j = paramInt1 + 1;
          paramArrayOfByte[paramInt1] = ((byte)(i >>> 6 | 0x3C0));
          paramInt1 = j + 1;
          paramArrayOfByte[j] = ((byte)(i & 0x3F | 0x80));
        }
        else
        {
          int n;
          if (((i < 55296) || (57343 < i)) && (paramInt1 <= m - 3))
          {
            j = paramInt1 + 1;
            paramArrayOfByte[paramInt1] = ((byte)(i >>> 12 | 0x1E0));
            n = j + 1;
            paramArrayOfByte[j] = ((byte)(i >>> 6 & 0x3F | 0x80));
            paramInt1 = n + 1;
            paramArrayOfByte[n] = ((byte)(i & 0x3F | 0x80));
          }
          else
          {
            if (paramInt1 > m - 4) {
              break label444;
            }
            j = paramInt2;
            char c;
            if (paramInt2 + 1 != paramCharSequence.length())
            {
              paramInt2 += 1;
              c = paramCharSequence.charAt(paramInt2);
              if (!Character.isSurrogatePair(i, c)) {
                j = paramInt2;
              }
            }
            else
            {
              throw new IllegalArgumentException(39 + "Unpaired surrogate at index " + (j - 1));
            }
            j = Character.toCodePoint(i, c);
            n = paramInt1 + 1;
            paramArrayOfByte[paramInt1] = ((byte)(j >>> 18 | 0xF0));
            paramInt1 = n + 1;
            paramArrayOfByte[n] = ((byte)(j >>> 12 & 0x3F | 0x80));
            n = paramInt1 + 1;
            paramArrayOfByte[paramInt1] = ((byte)(j >>> 6 & 0x3F | 0x80));
            paramInt1 = n + 1;
            paramArrayOfByte[n] = ((byte)(j & 0x3F | 0x80));
          }
        }
      }
      label444:
      throw new ArrayIndexOutOfBoundsException(37 + "Failed writing " + i + " at index " + paramInt1);
    }
    return paramInt1;
  }
  
  private static void zza(CharSequence paramCharSequence, ByteBuffer paramByteBuffer)
  {
    if (paramByteBuffer.isReadOnly()) {
      throw new ReadOnlyBufferException();
    }
    if (paramByteBuffer.hasArray()) {
      try
      {
        paramByteBuffer.position(zza(paramCharSequence, paramByteBuffer.array(), paramByteBuffer.arrayOffset() + paramByteBuffer.position(), paramByteBuffer.remaining()) - paramByteBuffer.arrayOffset());
        return;
      }
      catch (ArrayIndexOutOfBoundsException paramCharSequence)
      {
        paramByteBuffer = new BufferOverflowException();
        paramByteBuffer.initCause(paramCharSequence);
        throw paramByteBuffer;
      }
    }
    zzb(paramCharSequence, paramByteBuffer);
  }
  
  public static int zzag(int paramInt1, int paramInt2)
  {
    return zzahl(paramInt1) + zzahi(paramInt2);
  }
  
  public static int zzah(int paramInt1, int paramInt2)
  {
    return zzahl(paramInt1) + zzahj(paramInt2);
  }
  
  public static int zzahi(int paramInt)
  {
    if (paramInt >= 0) {
      return zzahn(paramInt);
    }
    return 10;
  }
  
  public static int zzahj(int paramInt)
  {
    return zzahn(zzahp(paramInt));
  }
  
  public static int zzahl(int paramInt)
  {
    return zzahn(zzarn.zzaj(paramInt, 0));
  }
  
  public static int zzahn(int paramInt)
  {
    if ((paramInt & 0xFFFFFF80) == 0) {
      return 1;
    }
    if ((paramInt & 0xC000) == 0) {
      return 2;
    }
    if ((0xFFE00000 & paramInt) == 0) {
      return 3;
    }
    if ((0xF0000000 & paramInt) == 0) {
      return 4;
    }
    return 5;
  }
  
  public static int zzahp(int paramInt)
  {
    return paramInt << 1 ^ paramInt >> 31;
  }
  
  public static int zzb(int paramInt, double paramDouble)
  {
    return zzahl(paramInt) + zzp(paramDouble);
  }
  
  public static int zzb(int paramInt, zzark paramzzark)
  {
    return zzahl(paramInt) * 2 + zzd(paramzzark);
  }
  
  public static int zzb(int paramInt, byte[] paramArrayOfByte)
  {
    return zzahl(paramInt) + zzbg(paramArrayOfByte);
  }
  
  private static void zzb(CharSequence paramCharSequence, ByteBuffer paramByteBuffer)
  {
    int m = paramCharSequence.length();
    int j = 0;
    if (j < m)
    {
      int i = paramCharSequence.charAt(j);
      if (i < 128) {
        paramByteBuffer.put((byte)i);
      }
      for (;;)
      {
        j += 1;
        break;
        if (i < 2048)
        {
          paramByteBuffer.put((byte)(i >>> 6 | 0x3C0));
          paramByteBuffer.put((byte)(i & 0x3F | 0x80));
        }
        else if ((i < 55296) || (57343 < i))
        {
          paramByteBuffer.put((byte)(i >>> 12 | 0x1E0));
          paramByteBuffer.put((byte)(i >>> 6 & 0x3F | 0x80));
          paramByteBuffer.put((byte)(i & 0x3F | 0x80));
        }
        else
        {
          int k = j;
          char c;
          if (j + 1 != paramCharSequence.length())
          {
            j += 1;
            c = paramCharSequence.charAt(j);
            if (!Character.isSurrogatePair(i, c)) {
              k = j;
            }
          }
          else
          {
            throw new IllegalArgumentException(39 + "Unpaired surrogate at index " + (k - 1));
          }
          k = Character.toCodePoint(i, c);
          paramByteBuffer.put((byte)(k >>> 18 | 0xF0));
          paramByteBuffer.put((byte)(k >>> 12 & 0x3F | 0x80));
          paramByteBuffer.put((byte)(k >>> 6 & 0x3F | 0x80));
          paramByteBuffer.put((byte)(k & 0x3F | 0x80));
        }
      }
    }
  }
  
  public static zzard zzbe(byte[] paramArrayOfByte)
  {
    return zzc(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public static int zzbg(byte[] paramArrayOfByte)
  {
    return zzahn(paramArrayOfByte.length) + paramArrayOfByte.length;
  }
  
  public static int zzc(int paramInt, zzark paramzzark)
  {
    return zzahl(paramInt) + zze(paramzzark);
  }
  
  public static zzard zzc(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return new zzard(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public static int zzd(int paramInt, float paramFloat)
  {
    return zzahl(paramInt) + zzl(paramFloat);
  }
  
  public static int zzd(zzark paramzzark)
  {
    return paramzzark.db();
  }
  
  private static int zzd(CharSequence paramCharSequence)
  {
    int m = paramCharSequence.length();
    int i = 0;
    while ((i < m) && (paramCharSequence.charAt(i) < '')) {
      i += 1;
    }
    for (;;)
    {
      int k = i;
      int j;
      if (j < m)
      {
        k = paramCharSequence.charAt(j);
        if (k < 2048)
        {
          j += 1;
          i = (127 - k >>> 31) + i;
        }
        else
        {
          k = i + zza(paramCharSequence, j);
        }
      }
      else
      {
        if (k < m)
        {
          long l = k;
          throw new IllegalArgumentException(54 + "UTF-8 length does not fit in int: " + (l + 4294967296L));
        }
        return k;
        j = i;
        i = m;
      }
    }
  }
  
  public static int zzda(long paramLong)
  {
    return zzdf(paramLong);
  }
  
  public static int zzdb(long paramLong)
  {
    return zzdf(paramLong);
  }
  
  public static int zzdc(long paramLong)
  {
    return 8;
  }
  
  public static int zzdd(long paramLong)
  {
    return zzdf(zzdh(paramLong));
  }
  
  public static int zzdf(long paramLong)
  {
    if ((0xFFFFFFFFFFFFFF80 & paramLong) == 0L) {
      return 1;
    }
    if ((0xFFFFFFFFFFFFC000 & paramLong) == 0L) {
      return 2;
    }
    if ((0xFFFFFFFFFFE00000 & paramLong) == 0L) {
      return 3;
    }
    if ((0xFFFFFFFFF0000000 & paramLong) == 0L) {
      return 4;
    }
    if ((0xFFFFFFF800000000 & paramLong) == 0L) {
      return 5;
    }
    if ((0xFFFFFCNUM & paramLong) == 0L) {
      return 6;
    }
    if ((0xFFFE00NUM & paramLong) == 0L) {
      return 7;
    }
    if ((0xFF0000NUM & paramLong) == 0L) {
      return 8;
    }
    if ((0x800000NUM & paramLong) == 0L) {
      return 9;
    }
    return 10;
  }
  
  public static long zzdh(long paramLong)
  {
    return paramLong << 1 ^ paramLong >> 63;
  }
  
  public static int zzdl(boolean paramBoolean)
  {
    return 1;
  }
  
  public static int zze(int paramInt, long paramLong)
  {
    return zzahl(paramInt) + zzda(paramLong);
  }
  
  public static int zze(zzark paramzzark)
  {
    int i = paramzzark.db();
    return i + zzahn(i);
  }
  
  public static int zzf(int paramInt, long paramLong)
  {
    return zzahl(paramInt) + zzdb(paramLong);
  }
  
  public static int zzg(int paramInt, long paramLong)
  {
    return zzahl(paramInt) + zzdc(paramLong);
  }
  
  public static int zzh(int paramInt, long paramLong)
  {
    return zzahl(paramInt) + zzdd(paramLong);
  }
  
  public static int zzk(int paramInt, boolean paramBoolean)
  {
    return zzahl(paramInt) + zzdl(paramBoolean);
  }
  
  public static int zzl(float paramFloat)
  {
    return 4;
  }
  
  public static int zzp(double paramDouble)
  {
    return 8;
  }
  
  public static int zzs(int paramInt, String paramString)
  {
    return zzahl(paramInt) + zzuy(paramString);
  }
  
  public static int zzuy(String paramString)
  {
    int i = zzd(paramString);
    return i + zzahn(i);
  }
  
  public int cN()
  {
    return this.bqu.remaining();
  }
  
  public void cO()
  {
    if (cN() != 0) {
      throw new IllegalStateException("Did not write as much data as expected.");
    }
  }
  
  public void zza(int paramInt, double paramDouble)
    throws IOException
  {
    zzai(paramInt, 1);
    zzo(paramDouble);
  }
  
  public void zza(int paramInt, long paramLong)
    throws IOException
  {
    zzai(paramInt, 0);
    zzcw(paramLong);
  }
  
  public void zza(int paramInt, zzark paramzzark)
    throws IOException
  {
    zzai(paramInt, 2);
    zzc(paramzzark);
  }
  
  public void zza(int paramInt, byte[] paramArrayOfByte)
    throws IOException
  {
    zzai(paramInt, 2);
    zzbf(paramArrayOfByte);
  }
  
  public void zzae(int paramInt1, int paramInt2)
    throws IOException
  {
    zzai(paramInt1, 0);
    zzahg(paramInt2);
  }
  
  public void zzaf(int paramInt1, int paramInt2)
    throws IOException
  {
    zzai(paramInt1, 0);
    zzahh(paramInt2);
  }
  
  public void zzahg(int paramInt)
    throws IOException
  {
    if (paramInt >= 0)
    {
      zzahm(paramInt);
      return;
    }
    zzde(paramInt);
  }
  
  public void zzahh(int paramInt)
    throws IOException
  {
    zzahm(zzahp(paramInt));
  }
  
  public void zzahk(int paramInt)
    throws IOException
  {
    zzc((byte)paramInt);
  }
  
  public void zzahm(int paramInt)
    throws IOException
  {
    for (;;)
    {
      if ((paramInt & 0xFFFFFF80) == 0)
      {
        zzahk(paramInt);
        return;
      }
      zzahk(paramInt & 0x7F | 0x80);
      paramInt >>>= 7;
    }
  }
  
  public void zzaho(int paramInt)
    throws IOException
  {
    if (this.bqu.remaining() < 4) {
      throw new zza(this.bqu.position(), this.bqu.limit());
    }
    this.bqu.putInt(paramInt);
  }
  
  public void zzai(int paramInt1, int paramInt2)
    throws IOException
  {
    zzahm(zzarn.zzaj(paramInt1, paramInt2));
  }
  
  public void zzb(int paramInt, long paramLong)
    throws IOException
  {
    zzai(paramInt, 0);
    zzcx(paramLong);
  }
  
  public void zzb(zzark paramzzark)
    throws IOException
  {
    paramzzark.zza(this);
  }
  
  public void zzbf(byte[] paramArrayOfByte)
    throws IOException
  {
    zzahm(paramArrayOfByte.length);
    zzbh(paramArrayOfByte);
  }
  
  public void zzbh(byte[] paramArrayOfByte)
    throws IOException
  {
    zzd(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public void zzc(byte paramByte)
    throws IOException
  {
    if (!this.bqu.hasRemaining()) {
      throw new zza(this.bqu.position(), this.bqu.limit());
    }
    this.bqu.put(paramByte);
  }
  
  public void zzc(int paramInt, float paramFloat)
    throws IOException
  {
    zzai(paramInt, 5);
    zzk(paramFloat);
  }
  
  public void zzc(int paramInt, long paramLong)
    throws IOException
  {
    zzai(paramInt, 1);
    zzcy(paramLong);
  }
  
  public void zzc(zzark paramzzark)
    throws IOException
  {
    zzahm(paramzzark.da());
    paramzzark.zza(this);
  }
  
  public void zzcw(long paramLong)
    throws IOException
  {
    zzde(paramLong);
  }
  
  public void zzcx(long paramLong)
    throws IOException
  {
    zzde(paramLong);
  }
  
  public void zzcy(long paramLong)
    throws IOException
  {
    zzdg(paramLong);
  }
  
  public void zzcz(long paramLong)
    throws IOException
  {
    zzde(zzdh(paramLong));
  }
  
  public void zzd(int paramInt, long paramLong)
    throws IOException
  {
    zzai(paramInt, 0);
    zzcz(paramLong);
  }
  
  public void zzd(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (this.bqu.remaining() >= paramInt2)
    {
      this.bqu.put(paramArrayOfByte, paramInt1, paramInt2);
      return;
    }
    throw new zza(this.bqu.position(), this.bqu.limit());
  }
  
  public void zzde(long paramLong)
    throws IOException
  {
    for (;;)
    {
      if ((0xFFFFFFFFFFFFFF80 & paramLong) == 0L)
      {
        zzahk((int)paramLong);
        return;
      }
      zzahk((int)paramLong & 0x7F | 0x80);
      paramLong >>>= 7;
    }
  }
  
  public void zzdg(long paramLong)
    throws IOException
  {
    if (this.bqu.remaining() < 8) {
      throw new zza(this.bqu.position(), this.bqu.limit());
    }
    this.bqu.putLong(paramLong);
  }
  
  public void zzdk(boolean paramBoolean)
    throws IOException
  {
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      zzahk(i);
      return;
    }
  }
  
  public void zzj(int paramInt, boolean paramBoolean)
    throws IOException
  {
    zzai(paramInt, 0);
    zzdk(paramBoolean);
  }
  
  public void zzk(float paramFloat)
    throws IOException
  {
    zzaho(Float.floatToIntBits(paramFloat));
  }
  
  public void zzo(double paramDouble)
    throws IOException
  {
    zzdg(Double.doubleToLongBits(paramDouble));
  }
  
  public void zzr(int paramInt, String paramString)
    throws IOException
  {
    zzai(paramInt, 2);
    zzux(paramString);
  }
  
  public void zzux(String paramString)
    throws IOException
  {
    int i;
    int j;
    try
    {
      i = zzahn(paramString.length());
      if (i != zzahn(paramString.length() * 3)) {
        break label150;
      }
      j = this.bqu.position();
      if (this.bqu.remaining() < i) {
        throw new zza(i + j, this.bqu.limit());
      }
    }
    catch (BufferOverflowException paramString)
    {
      zza localzza = new zza(this.bqu.position(), this.bqu.limit());
      localzza.initCause(paramString);
      throw localzza;
    }
    this.bqu.position(j + i);
    zza(paramString, this.bqu);
    int k = this.bqu.position();
    this.bqu.position(j);
    zzahm(k - j - i);
    this.bqu.position(k);
    return;
    label150:
    zzahm(zzd(paramString));
    zza(paramString, this.bqu);
  }
  
  public static class zza
    extends IOException
  {
    zza(int paramInt1, int paramInt2)
    {
      super();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzard.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */