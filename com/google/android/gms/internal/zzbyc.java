package com.google.android.gms.internal;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;

public final class zzbyc
{
  private final ByteBuffer zzcwB;
  
  private zzbyc(ByteBuffer paramByteBuffer)
  {
    this.zzcwB = paramByteBuffer;
    this.zzcwB.order(ByteOrder.LITTLE_ENDIAN);
  }
  
  private zzbyc(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this(ByteBuffer.wrap(paramArrayOfByte, paramInt1, paramInt2));
  }
  
  public static int zzL(int paramInt1, int paramInt2)
  {
    return zzro(paramInt1) + zzrl(paramInt2);
  }
  
  public static int zzM(int paramInt1, int paramInt2)
  {
    return zzro(paramInt1) + zzrm(paramInt2);
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
  
  public static zzbyc zzah(byte[] paramArrayOfByte)
  {
    return zzc(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public static int zzaj(byte[] paramArrayOfByte)
  {
    return zzrq(paramArrayOfByte.length) + paramArrayOfByte.length;
  }
  
  public static int zzb(int paramInt, double paramDouble)
  {
    return zzro(paramInt) + 8;
  }
  
  public static int zzb(int paramInt, zzbyj paramzzbyj)
  {
    return zzro(paramInt) * 2 + zzd(paramzzbyj);
  }
  
  private static int zzb(CharSequence paramCharSequence)
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
  
  public static int zzbp(long paramLong)
  {
    return zzbt(paramLong);
  }
  
  public static int zzbq(long paramLong)
  {
    return zzbt(paramLong);
  }
  
  public static int zzbr(long paramLong)
  {
    return zzbt(zzbv(paramLong));
  }
  
  public static int zzbt(long paramLong)
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
  
  public static long zzbv(long paramLong)
  {
    return paramLong << 1 ^ paramLong >> 63;
  }
  
  public static int zzc(int paramInt, zzbyj paramzzbyj)
  {
    return zzro(paramInt) + zze(paramzzbyj);
  }
  
  public static int zzc(int paramInt, byte[] paramArrayOfByte)
  {
    return zzro(paramInt) + zzaj(paramArrayOfByte);
  }
  
  public static zzbyc zzc(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return new zzbyc(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public static int zzd(int paramInt, float paramFloat)
  {
    return zzro(paramInt) + 4;
  }
  
  public static int zzd(zzbyj paramzzbyj)
  {
    return paramzzbyj.zzafB();
  }
  
  public static int zze(int paramInt, long paramLong)
  {
    return zzro(paramInt) + zzbp(paramLong);
  }
  
  public static int zze(zzbyj paramzzbyj)
  {
    int i = paramzzbyj.zzafB();
    return i + zzrq(i);
  }
  
  public static int zzf(int paramInt, long paramLong)
  {
    return zzro(paramInt) + zzbq(paramLong);
  }
  
  public static int zzg(int paramInt, long paramLong)
  {
    return zzro(paramInt) + 8;
  }
  
  public static int zzh(int paramInt, long paramLong)
  {
    return zzro(paramInt) + zzbr(paramLong);
  }
  
  public static int zzh(int paramInt, boolean paramBoolean)
  {
    return zzro(paramInt) + 1;
  }
  
  public static int zzku(String paramString)
  {
    int i = zzb(paramString);
    return i + zzrq(i);
  }
  
  public static int zzr(int paramInt, String paramString)
  {
    return zzro(paramInt) + zzku(paramString);
  }
  
  public static int zzrl(int paramInt)
  {
    if (paramInt >= 0) {
      return zzrq(paramInt);
    }
    return 10;
  }
  
  public static int zzrm(int paramInt)
  {
    return zzrq(zzrs(paramInt));
  }
  
  public static int zzro(int paramInt)
  {
    return zzrq(zzbym.zzO(paramInt, 0));
  }
  
  public static int zzrq(int paramInt)
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
  
  public static int zzrs(int paramInt)
  {
    return paramInt << 1 ^ paramInt >> 31;
  }
  
  public void zzJ(int paramInt1, int paramInt2)
    throws IOException
  {
    zzN(paramInt1, 0);
    zzrj(paramInt2);
  }
  
  public void zzK(int paramInt1, int paramInt2)
    throws IOException
  {
    zzN(paramInt1, 0);
    zzrk(paramInt2);
  }
  
  public void zzN(int paramInt1, int paramInt2)
    throws IOException
  {
    zzrp(zzbym.zzO(paramInt1, paramInt2));
  }
  
  public void zza(int paramInt, double paramDouble)
    throws IOException
  {
    zzN(paramInt, 1);
    zzn(paramDouble);
  }
  
  public void zza(int paramInt, long paramLong)
    throws IOException
  {
    zzN(paramInt, 0);
    zzbl(paramLong);
  }
  
  public void zza(int paramInt, zzbyj paramzzbyj)
    throws IOException
  {
    zzN(paramInt, 2);
    zzc(paramzzbyj);
  }
  
  public int zzafn()
  {
    return this.zzcwB.remaining();
  }
  
  public void zzafo()
  {
    if (zzafn() != 0) {
      throw new IllegalStateException("Did not write as much data as expected.");
    }
  }
  
  public void zzai(byte[] paramArrayOfByte)
    throws IOException
  {
    zzrp(paramArrayOfByte.length);
    zzak(paramArrayOfByte);
  }
  
  public void zzak(byte[] paramArrayOfByte)
    throws IOException
  {
    zzd(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public void zzb(int paramInt, long paramLong)
    throws IOException
  {
    zzN(paramInt, 0);
    zzbm(paramLong);
  }
  
  public void zzb(int paramInt, byte[] paramArrayOfByte)
    throws IOException
  {
    zzN(paramInt, 2);
    zzai(paramArrayOfByte);
  }
  
  public void zzb(zzbyj paramzzbyj)
    throws IOException
  {
    paramzzbyj.zza(this);
  }
  
  public void zzbl(long paramLong)
    throws IOException
  {
    zzbs(paramLong);
  }
  
  public void zzbm(long paramLong)
    throws IOException
  {
    zzbs(paramLong);
  }
  
  public void zzbn(long paramLong)
    throws IOException
  {
    zzbu(paramLong);
  }
  
  public void zzbo(long paramLong)
    throws IOException
  {
    zzbs(zzbv(paramLong));
  }
  
  public void zzbr(boolean paramBoolean)
    throws IOException
  {
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      zzrn(i);
      return;
    }
  }
  
  public void zzbs(long paramLong)
    throws IOException
  {
    for (;;)
    {
      if ((0xFFFFFFFFFFFFFF80 & paramLong) == 0L)
      {
        zzrn((int)paramLong);
        return;
      }
      zzrn((int)paramLong & 0x7F | 0x80);
      paramLong >>>= 7;
    }
  }
  
  public void zzbu(long paramLong)
    throws IOException
  {
    if (this.zzcwB.remaining() < 8) {
      throw new zza(this.zzcwB.position(), this.zzcwB.limit());
    }
    this.zzcwB.putLong(paramLong);
  }
  
  public void zzc(byte paramByte)
    throws IOException
  {
    if (!this.zzcwB.hasRemaining()) {
      throw new zza(this.zzcwB.position(), this.zzcwB.limit());
    }
    this.zzcwB.put(paramByte);
  }
  
  public void zzc(int paramInt, float paramFloat)
    throws IOException
  {
    zzN(paramInt, 5);
    zzk(paramFloat);
  }
  
  public void zzc(int paramInt, long paramLong)
    throws IOException
  {
    zzN(paramInt, 1);
    zzbn(paramLong);
  }
  
  public void zzc(zzbyj paramzzbyj)
    throws IOException
  {
    zzrp(paramzzbyj.zzafA());
    paramzzbyj.zza(this);
  }
  
  public void zzd(int paramInt, long paramLong)
    throws IOException
  {
    zzN(paramInt, 0);
    zzbo(paramLong);
  }
  
  public void zzd(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (this.zzcwB.remaining() >= paramInt2)
    {
      this.zzcwB.put(paramArrayOfByte, paramInt1, paramInt2);
      return;
    }
    throw new zza(this.zzcwB.position(), this.zzcwB.limit());
  }
  
  public void zzg(int paramInt, boolean paramBoolean)
    throws IOException
  {
    zzN(paramInt, 0);
    zzbr(paramBoolean);
  }
  
  public void zzk(float paramFloat)
    throws IOException
  {
    zzrr(Float.floatToIntBits(paramFloat));
  }
  
  public void zzkt(String paramString)
    throws IOException
  {
    int i;
    int j;
    try
    {
      i = zzrq(paramString.length());
      if (i != zzrq(paramString.length() * 3)) {
        break label150;
      }
      j = this.zzcwB.position();
      if (this.zzcwB.remaining() < i) {
        throw new zza(i + j, this.zzcwB.limit());
      }
    }
    catch (BufferOverflowException paramString)
    {
      zza localzza = new zza(this.zzcwB.position(), this.zzcwB.limit());
      localzza.initCause(paramString);
      throw localzza;
    }
    this.zzcwB.position(j + i);
    zza(paramString, this.zzcwB);
    int k = this.zzcwB.position();
    this.zzcwB.position(j);
    zzrp(k - j - i);
    this.zzcwB.position(k);
    return;
    label150:
    zzrp(zzb(paramString));
    zza(paramString, this.zzcwB);
  }
  
  public void zzn(double paramDouble)
    throws IOException
  {
    zzbu(Double.doubleToLongBits(paramDouble));
  }
  
  public void zzq(int paramInt, String paramString)
    throws IOException
  {
    zzN(paramInt, 2);
    zzkt(paramString);
  }
  
  public void zzrj(int paramInt)
    throws IOException
  {
    if (paramInt >= 0)
    {
      zzrp(paramInt);
      return;
    }
    zzbs(paramInt);
  }
  
  public void zzrk(int paramInt)
    throws IOException
  {
    zzrp(zzrs(paramInt));
  }
  
  public void zzrn(int paramInt)
    throws IOException
  {
    zzc((byte)paramInt);
  }
  
  public void zzrp(int paramInt)
    throws IOException
  {
    for (;;)
    {
      if ((paramInt & 0xFFFFFF80) == 0)
      {
        zzrn(paramInt);
        return;
      }
      zzrn(paramInt & 0x7F | 0x80);
      paramInt >>>= 7;
    }
  }
  
  public void zzrr(int paramInt)
    throws IOException
  {
    if (this.zzcwB.remaining() < 4) {
      throw new zza(this.zzcwB.position(), this.zzcwB.limit());
    }
    this.zzcwB.putInt(paramInt);
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbyc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */