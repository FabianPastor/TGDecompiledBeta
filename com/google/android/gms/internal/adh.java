package com.google.android.gms.internal;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;

public final class adh
{
  private final ByteBuffer zzcsn;
  
  private adh(ByteBuffer paramByteBuffer)
  {
    this.zzcsn = paramByteBuffer;
    this.zzcsn.order(ByteOrder.LITTLE_ENDIAN);
  }
  
  private adh(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this(ByteBuffer.wrap(paramArrayOfByte, paramInt1, paramInt2));
  }
  
  public static adh zzI(byte[] paramArrayOfByte)
  {
    return zzc(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public static int zzJ(byte[] paramArrayOfByte)
  {
    return zzcv(paramArrayOfByte.length) + paramArrayOfByte.length;
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
  
  private final void zzaO(long paramLong)
    throws IOException
  {
    for (;;)
    {
      if ((0xFFFFFFFFFFFFFF80 & paramLong) == 0L)
      {
        zzcs((int)paramLong);
        return;
      }
      zzcs((int)paramLong & 0x7F | 0x80);
      paramLong >>>= 7;
    }
  }
  
  public static int zzaP(long paramLong)
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
  
  private final void zzaQ(long paramLong)
    throws IOException
  {
    if (this.zzcsn.remaining() < 8) {
      throw new adi(this.zzcsn.position(), this.zzcsn.limit());
    }
    this.zzcsn.putLong(paramLong);
  }
  
  private static long zzaR(long paramLong)
  {
    return paramLong << 1 ^ paramLong >> 63;
  }
  
  public static int zzb(int paramInt, adp paramadp)
  {
    paramInt = zzct(paramInt);
    int i = paramadp.zzLV();
    return paramInt + (i + zzcv(i));
  }
  
  private static int zzb(CharSequence paramCharSequence)
  {
    int k = 0;
    int n = paramCharSequence.length();
    int j = 0;
    while ((j < n) && (paramCharSequence.charAt(j) < '')) {
      j += 1;
    }
    for (;;)
    {
      int i;
      if (j < n)
      {
        int m = paramCharSequence.charAt(j);
        if (m < 2048)
        {
          i += (127 - m >>> 31);
          j += 1;
        }
        else
        {
          int i2 = paramCharSequence.length();
          if (j < i2)
          {
            int i3 = paramCharSequence.charAt(j);
            if (i3 < 2048)
            {
              k += (127 - i3 >>> 31);
              m = j;
            }
            for (;;)
            {
              j = m + 1;
              break;
              int i1 = k + 2;
              m = j;
              k = i1;
              if (55296 <= i3)
              {
                m = j;
                k = i1;
                if (i3 <= 57343)
                {
                  if (Character.codePointAt(paramCharSequence, j) < 65536) {
                    throw new IllegalArgumentException(39 + "Unpaired surrogate at index " + j);
                  }
                  m = j + 1;
                  k = i1;
                }
              }
            }
          }
          i += k;
        }
      }
      else
      {
        for (;;)
        {
          if (i < n)
          {
            long l = i;
            throw new IllegalArgumentException(54 + "UTF-8 length does not fit in int: " + (l + 4294967296L));
          }
          return i;
        }
        i = n;
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
  
  public static int zzc(int paramInt, byte[] paramArrayOfByte)
  {
    return zzct(paramInt) + zzJ(paramArrayOfByte);
  }
  
  public static adh zzc(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return new adh(paramArrayOfByte, 0, paramInt2);
  }
  
  public static int zzcr(int paramInt)
  {
    if (paramInt >= 0) {
      return zzcv(paramInt);
    }
    return 10;
  }
  
  private final void zzcs(int paramInt)
    throws IOException
  {
    byte b = (byte)paramInt;
    if (!this.zzcsn.hasRemaining()) {
      throw new adi(this.zzcsn.position(), this.zzcsn.limit());
    }
    this.zzcsn.put(b);
  }
  
  public static int zzct(int paramInt)
  {
    return zzcv(paramInt << 3);
  }
  
  public static int zzcv(int paramInt)
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
  
  public static int zzcw(int paramInt)
  {
    return paramInt << 1 ^ paramInt >> 31;
  }
  
  public static int zze(int paramInt, long paramLong)
  {
    return zzct(paramInt) + zzaP(paramLong);
  }
  
  public static int zzf(int paramInt, long paramLong)
  {
    return zzct(paramInt) + zzaP(zzaR(paramLong));
  }
  
  public static int zzhQ(String paramString)
  {
    int i = zzb(paramString);
    return i + zzcv(i);
  }
  
  public static int zzm(int paramInt, String paramString)
  {
    return zzct(paramInt) + zzhQ(paramString);
  }
  
  public static int zzs(int paramInt1, int paramInt2)
  {
    return zzct(paramInt1) + zzcr(paramInt2);
  }
  
  public final void zzK(byte[] paramArrayOfByte)
    throws IOException
  {
    int i = paramArrayOfByte.length;
    if (this.zzcsn.remaining() >= i)
    {
      this.zzcsn.put(paramArrayOfByte, 0, i);
      return;
    }
    throw new adi(this.zzcsn.position(), this.zzcsn.limit());
  }
  
  public final void zzLM()
  {
    if (this.zzcsn.remaining() != 0) {
      throw new IllegalStateException("Did not write as much data as expected.");
    }
  }
  
  public final void zza(int paramInt, double paramDouble)
    throws IOException
  {
    zzt(paramInt, 1);
    zzaQ(Double.doubleToLongBits(paramDouble));
  }
  
  public final void zza(int paramInt, long paramLong)
    throws IOException
  {
    zzt(paramInt, 0);
    zzaO(paramLong);
  }
  
  public final void zza(int paramInt, adp paramadp)
    throws IOException
  {
    zzt(paramInt, 2);
    zzb(paramadp);
  }
  
  public final void zzb(int paramInt, long paramLong)
    throws IOException
  {
    zzt(paramInt, 0);
    zzaO(paramLong);
  }
  
  public final void zzb(int paramInt, byte[] paramArrayOfByte)
    throws IOException
  {
    zzt(paramInt, 2);
    zzcu(paramArrayOfByte.length);
    zzK(paramArrayOfByte);
  }
  
  public final void zzb(adp paramadp)
    throws IOException
  {
    zzcu(paramadp.zzLU());
    paramadp.zza(this);
  }
  
  public final void zzc(int paramInt, float paramFloat)
    throws IOException
  {
    zzt(paramInt, 5);
    paramInt = Float.floatToIntBits(paramFloat);
    if (this.zzcsn.remaining() < 4) {
      throw new adi(this.zzcsn.position(), this.zzcsn.limit());
    }
    this.zzcsn.putInt(paramInt);
  }
  
  public final void zzc(int paramInt, long paramLong)
    throws IOException
  {
    zzt(paramInt, 1);
    zzaQ(paramLong);
  }
  
  public final void zzcu(int paramInt)
    throws IOException
  {
    for (;;)
    {
      if ((paramInt & 0xFFFFFF80) == 0)
      {
        zzcs(paramInt);
        return;
      }
      zzcs(paramInt & 0x7F | 0x80);
      paramInt >>>= 7;
    }
  }
  
  public final void zzd(int paramInt, long paramLong)
    throws IOException
  {
    zzt(paramInt, 0);
    zzaO(zzaR(paramLong));
  }
  
  public final void zzk(int paramInt, boolean paramBoolean)
    throws IOException
  {
    int i = 0;
    zzt(paramInt, 0);
    paramInt = i;
    if (paramBoolean) {
      paramInt = 1;
    }
    byte b = (byte)paramInt;
    if (!this.zzcsn.hasRemaining()) {
      throw new adi(this.zzcsn.position(), this.zzcsn.limit());
    }
    this.zzcsn.put(b);
  }
  
  public final void zzl(int paramInt, String paramString)
    throws IOException
  {
    zzt(paramInt, 2);
    int i;
    try
    {
      paramInt = zzcv(paramString.length());
      if (paramInt != zzcv(paramString.length() * 3)) {
        break label156;
      }
      i = this.zzcsn.position();
      if (this.zzcsn.remaining() < paramInt) {
        throw new adi(paramInt + i, this.zzcsn.limit());
      }
    }
    catch (BufferOverflowException paramString)
    {
      adi localadi = new adi(this.zzcsn.position(), this.zzcsn.limit());
      localadi.initCause(paramString);
      throw localadi;
    }
    this.zzcsn.position(i + paramInt);
    zza(paramString, this.zzcsn);
    int j = this.zzcsn.position();
    this.zzcsn.position(i);
    zzcu(j - i - paramInt);
    this.zzcsn.position(j);
    return;
    label156:
    zzcu(zzb(paramString));
    zza(paramString, this.zzcsn);
  }
  
  public final void zzr(int paramInt1, int paramInt2)
    throws IOException
  {
    zzt(paramInt1, 0);
    if (paramInt2 >= 0)
    {
      zzcu(paramInt2);
      return;
    }
    zzaO(paramInt2);
  }
  
  public final void zzt(int paramInt1, int paramInt2)
    throws IOException
  {
    zzcu(paramInt1 << 3 | paramInt2);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/adh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */