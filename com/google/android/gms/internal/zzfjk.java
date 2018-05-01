package com.google.android.gms.internal;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;

public final class zzfjk
{
  private final ByteBuffer buffer;
  
  private zzfjk(ByteBuffer paramByteBuffer)
  {
    this.buffer = paramByteBuffer;
    this.buffer.order(ByteOrder.LITTLE_ENDIAN);
  }
  
  private zzfjk(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this(ByteBuffer.wrap(paramArrayOfByte, paramInt1, paramInt2));
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
  
  public static int zzad(int paramInt1, int paramInt2)
  {
    return zzlg(paramInt1) + zzlh(paramInt2);
  }
  
  public static int zzb(int paramInt, zzfjs paramzzfjs)
  {
    paramInt = zzlg(paramInt);
    int i = paramzzfjs.zzho();
    return paramInt + (i + zzlp(i));
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
  
  public static zzfjk zzbf(byte[] paramArrayOfByte)
  {
    return zzo(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public static int zzc(int paramInt, long paramLong)
  {
    return zzlg(paramInt) + zzdi(paramLong);
  }
  
  private static int zzd(CharSequence paramCharSequence)
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
  
  private final void zzdh(long paramLong)
    throws IOException
  {
    for (;;)
    {
      if ((0xFFFFFFFFFFFFFF80 & paramLong) == 0L)
      {
        zzmh((int)paramLong);
        return;
      }
      zzmh((int)paramLong & 0x7F | 0x80);
      paramLong >>>= 7;
    }
  }
  
  public static int zzdi(long paramLong)
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
  
  private final void zzdj(long paramLong)
    throws IOException
  {
    if (this.buffer.remaining() < 8) {
      throw new zzfjl(this.buffer.position(), this.buffer.limit());
    }
    this.buffer.putLong(paramLong);
  }
  
  public static int zzlg(int paramInt)
  {
    return zzlp(paramInt << 3);
  }
  
  public static int zzlh(int paramInt)
  {
    if (paramInt >= 0) {
      return zzlp(paramInt);
    }
    return 10;
  }
  
  public static int zzlp(int paramInt)
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
  
  private final void zzmh(int paramInt)
    throws IOException
  {
    byte b = (byte)paramInt;
    if (!this.buffer.hasRemaining()) {
      throw new zzfjl(this.buffer.position(), this.buffer.limit());
    }
    this.buffer.put(b);
  }
  
  public static int zzo(int paramInt, String paramString)
  {
    return zzlg(paramInt) + zztt(paramString);
  }
  
  public static zzfjk zzo(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return new zzfjk(paramArrayOfByte, 0, paramInt2);
  }
  
  public static int zztt(String paramString)
  {
    int i = zzd(paramString);
    return i + zzlp(i);
  }
  
  public final void zza(int paramInt, double paramDouble)
    throws IOException
  {
    zzz(paramInt, 1);
    zzdj(Double.doubleToLongBits(paramDouble));
  }
  
  public final void zza(int paramInt, long paramLong)
    throws IOException
  {
    zzz(paramInt, 0);
    zzdh(paramLong);
  }
  
  public final void zza(int paramInt, zzfjs paramzzfjs)
    throws IOException
  {
    zzz(paramInt, 2);
    zzb(paramzzfjs);
  }
  
  public final void zzaa(int paramInt1, int paramInt2)
    throws IOException
  {
    zzz(paramInt1, 0);
    if (paramInt2 >= 0)
    {
      zzmi(paramInt2);
      return;
    }
    zzdh(paramInt2);
  }
  
  public final void zzb(zzfjs paramzzfjs)
    throws IOException
  {
    zzmi(paramzzfjs.zzdam());
    paramzzfjs.zza(this);
  }
  
  public final void zzbh(byte[] paramArrayOfByte)
    throws IOException
  {
    int i = paramArrayOfByte.length;
    if (this.buffer.remaining() >= i)
    {
      this.buffer.put(paramArrayOfByte, 0, i);
      return;
    }
    throw new zzfjl(this.buffer.position(), this.buffer.limit());
  }
  
  public final void zzc(int paramInt, float paramFloat)
    throws IOException
  {
    zzz(paramInt, 5);
    paramInt = Float.floatToIntBits(paramFloat);
    if (this.buffer.remaining() < 4) {
      throw new zzfjl(this.buffer.position(), this.buffer.limit());
    }
    this.buffer.putInt(paramInt);
  }
  
  public final void zzcwt()
  {
    if (this.buffer.remaining() != 0) {
      throw new IllegalStateException(String.format("Did not write as much data as expected, %s bytes remaining.", new Object[] { Integer.valueOf(this.buffer.remaining()) }));
    }
  }
  
  public final void zzf(int paramInt, long paramLong)
    throws IOException
  {
    zzz(paramInt, 0);
    zzdh(paramLong);
  }
  
  public final void zzl(int paramInt, boolean paramBoolean)
    throws IOException
  {
    int i = 0;
    zzz(paramInt, 0);
    paramInt = i;
    if (paramBoolean) {
      paramInt = 1;
    }
    byte b = (byte)paramInt;
    if (!this.buffer.hasRemaining()) {
      throw new zzfjl(this.buffer.position(), this.buffer.limit());
    }
    this.buffer.put(b);
  }
  
  public final void zzmi(int paramInt)
    throws IOException
  {
    for (;;)
    {
      if ((paramInt & 0xFFFFFF80) == 0)
      {
        zzmh(paramInt);
        return;
      }
      zzmh(paramInt & 0x7F | 0x80);
      paramInt >>>= 7;
    }
  }
  
  public final void zzn(int paramInt, String paramString)
    throws IOException
  {
    zzz(paramInt, 2);
    int i;
    try
    {
      paramInt = zzlp(paramString.length());
      if (paramInt != zzlp(paramString.length() * 3)) {
        break label156;
      }
      i = this.buffer.position();
      if (this.buffer.remaining() < paramInt) {
        throw new zzfjl(paramInt + i, this.buffer.limit());
      }
    }
    catch (BufferOverflowException paramString)
    {
      zzfjl localzzfjl = new zzfjl(this.buffer.position(), this.buffer.limit());
      localzzfjl.initCause(paramString);
      throw localzzfjl;
    }
    this.buffer.position(i + paramInt);
    zza(paramString, this.buffer);
    int j = this.buffer.position();
    this.buffer.position(i);
    zzmi(j - i - paramInt);
    this.buffer.position(j);
    return;
    label156:
    zzmi(zzd(paramString));
    zza(paramString, this.buffer);
  }
  
  public final void zzz(int paramInt1, int paramInt2)
    throws IOException
  {
    zzmi(paramInt1 << 3 | paramInt2);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzfjk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */