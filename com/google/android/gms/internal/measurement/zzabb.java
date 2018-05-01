package com.google.android.gms.internal.measurement;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;

public final class zzabb
{
  private final ByteBuffer zzbzg;
  
  private zzabb(ByteBuffer paramByteBuffer)
  {
    this.zzbzg = paramByteBuffer;
    this.zzbzg.order(ByteOrder.LITTLE_ENDIAN);
  }
  
  private zzabb(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this(ByteBuffer.wrap(paramArrayOfByte, paramInt1, paramInt2));
  }
  
  private static int zza(CharSequence paramCharSequence)
  {
    int i = 0;
    int j = paramCharSequence.length();
    for (int k = 0; (k < j) && (paramCharSequence.charAt(k) < ''); k++) {}
    for (;;)
    {
      int n;
      if (k < j)
      {
        int m = paramCharSequence.charAt(k);
        if (m < 2048)
        {
          n += (127 - m >>> 31);
          k++;
        }
        else
        {
          int i1 = paramCharSequence.length();
          if (k < i1)
          {
            int i2 = paramCharSequence.charAt(k);
            if (i2 < 2048)
            {
              i += (127 - i2 >>> 31);
              m = k;
            }
            for (;;)
            {
              k = m + 1;
              break;
              int i3 = i + 2;
              m = k;
              i = i3;
              if (55296 <= i2)
              {
                m = k;
                i = i3;
                if (i2 <= 57343)
                {
                  if (Character.codePointAt(paramCharSequence, k) < 65536) {
                    throw new IllegalArgumentException(39 + "Unpaired surrogate at index " + k);
                  }
                  m = k + 1;
                  i = i3;
                }
              }
            }
          }
          n += i;
        }
      }
      else
      {
        for (;;)
        {
          if (n < j)
          {
            long l = n;
            throw new IllegalArgumentException(54 + "UTF-8 length does not fit in int: " + (l + 4294967296L));
          }
          return n;
        }
        n = j;
      }
    }
  }
  
  private static void zza(CharSequence paramCharSequence, ByteBuffer paramByteBuffer)
  {
    int i = 0;
    int j = 0;
    if (paramByteBuffer.isReadOnly()) {
      throw new ReadOnlyBufferException();
    }
    char c;
    if (paramByteBuffer.hasArray()) {
      label586:
      for (;;)
      {
        byte[] arrayOfByte;
        int n;
        int i1;
        try
        {
          arrayOfByte = paramByteBuffer.array();
          i = paramByteBuffer.arrayOffset() + paramByteBuffer.position();
          k = paramByteBuffer.remaining();
          int m = paramCharSequence.length();
          n = i + k;
          if ((j < m) && (j + i < n))
          {
            k = paramCharSequence.charAt(j);
            if (k < 128)
            {
              arrayOfByte[(i + j)] = ((byte)(byte)k);
              j++;
              continue;
            }
          }
          if (j == m)
          {
            i += m;
            paramByteBuffer.position(i - paramByteBuffer.arrayOffset());
            return;
          }
          i += j;
          if (j >= m) {
            break label586;
          }
          i1 = paramCharSequence.charAt(j);
          if ((i1 < 128) && (i < n))
          {
            k = i + 1;
            arrayOfByte[i] = ((byte)(byte)i1);
            i = k;
            j++;
            continue;
          }
          if ((i1 < 2048) && (i <= n - 2))
          {
            k = i + 1;
            arrayOfByte[i] = ((byte)(byte)(i1 >>> 6 | 0x3C0));
            i = k + 1;
            arrayOfByte[k] = ((byte)(byte)(i1 & 0x3F | 0x80));
            continue;
          }
          if (i1 < 55296) {}
        }
        catch (ArrayIndexOutOfBoundsException paramCharSequence)
        {
          paramByteBuffer = new BufferOverflowException();
          paramByteBuffer.initCause(paramCharSequence);
          throw paramByteBuffer;
        }
        int i3;
        if ((57343 < i1) && (i <= n - 3))
        {
          k = i + 1;
          i3 = (byte)(i1 >>> 12 | 0x1E0);
          arrayOfByte[i] = ((byte)i3);
          i3 = k + 1;
          arrayOfByte[k] = ((byte)(byte)(i1 >>> 6 & 0x3F | 0x80));
          i = i3 + 1;
          arrayOfByte[i3] = ((byte)(byte)(i1 & 0x3F | 0x80));
        }
        else if (i <= n - 4)
        {
          k = j;
          if (j + 1 != paramCharSequence.length())
          {
            j++;
            c = paramCharSequence.charAt(j);
            if (!Character.isSurrogatePair(i1, c)) {
              k = j;
            }
          }
          else
          {
            paramByteBuffer = new java/lang/IllegalArgumentException;
            paramCharSequence = new java/lang/StringBuilder;
            paramCharSequence.<init>(39);
            paramByteBuffer.<init>("Unpaired surrogate at index " + (k - 1));
            throw paramByteBuffer;
          }
          k = Character.toCodePoint(i1, c);
          i3 = i + 1;
          arrayOfByte[i] = ((byte)(byte)(k >>> 18 | 0xF0));
          i = i3 + 1;
          arrayOfByte[i3] = ((byte)(byte)(k >>> 12 & 0x3F | 0x80));
          i3 = i + 1;
          arrayOfByte[i] = ((byte)(byte)(k >>> 6 & 0x3F | 0x80));
          i = i3 + 1;
          arrayOfByte[i3] = ((byte)(byte)(k & 0x3F | 0x80));
        }
        else
        {
          paramCharSequence = new java/lang/ArrayIndexOutOfBoundsException;
          paramByteBuffer = new java/lang/StringBuilder;
          paramByteBuffer.<init>(37);
          paramCharSequence.<init>("Failed writing " + i1 + " at index " + i);
          throw paramCharSequence;
        }
      }
    }
    int k = paramCharSequence.length();
    label597:
    int i2;
    if (i < k)
    {
      i2 = paramCharSequence.charAt(i);
      if (i2 >= 128) {
        break label634;
      }
      paramByteBuffer.put((byte)i2);
    }
    for (;;)
    {
      i++;
      break label597;
      break;
      label634:
      if (i2 < 2048)
      {
        paramByteBuffer.put((byte)(i2 >>> 6 | 0x3C0));
        paramByteBuffer.put((byte)(i2 & 0x3F | 0x80));
      }
      else if ((i2 < 55296) || (57343 < i2))
      {
        paramByteBuffer.put((byte)(i2 >>> 12 | 0x1E0));
        paramByteBuffer.put((byte)(i2 >>> 6 & 0x3F | 0x80));
        paramByteBuffer.put((byte)(i2 & 0x3F | 0x80));
      }
      else
      {
        j = i;
        if (i + 1 != paramCharSequence.length())
        {
          j = i + 1;
          c = paramCharSequence.charAt(j);
          if (Character.isSurrogatePair(i2, c)) {}
        }
        else
        {
          throw new IllegalArgumentException(39 + "Unpaired surrogate at index " + (j - 1));
        }
        i = Character.toCodePoint(i2, c);
        paramByteBuffer.put((byte)(i >>> 18 | 0xF0));
        paramByteBuffer.put((byte)(i >>> 12 & 0x3F | 0x80));
        paramByteBuffer.put((byte)(i >>> 6 & 0x3F | 0x80));
        paramByteBuffer.put((byte)(i & 0x3F | 0x80));
        i = j;
      }
    }
  }
  
  private final void zzao(long paramLong)
    throws IOException
  {
    for (;;)
    {
      if ((0xFFFFFFFFFFFFFF80 & paramLong) == 0L)
      {
        zzar((int)paramLong);
        return;
      }
      zzar((int)paramLong & 0x7F | 0x80);
      paramLong >>>= 7;
    }
  }
  
  public static int zzap(long paramLong)
  {
    int i;
    if ((0xFFFFFFFFFFFFFF80 & paramLong) == 0L) {
      i = 1;
    }
    for (;;)
    {
      return i;
      if ((0xFFFFFFFFFFFFC000 & paramLong) == 0L) {
        i = 2;
      } else if ((0xFFFFFFFFFFE00000 & paramLong) == 0L) {
        i = 3;
      } else if ((0xFFFFFFFFF0000000 & paramLong) == 0L) {
        i = 4;
      } else if ((0xFFFFFFF800000000 & paramLong) == 0L) {
        i = 5;
      } else if ((0xFFFFFCNUM & paramLong) == 0L) {
        i = 6;
      } else if ((0xFFFE00NUM & paramLong) == 0L) {
        i = 7;
      } else if ((0xFF0000NUM & paramLong) == 0L) {
        i = 8;
      } else if ((0x800000NUM & paramLong) == 0L) {
        i = 9;
      } else {
        i = 10;
      }
    }
  }
  
  public static int zzaq(int paramInt)
  {
    if (paramInt >= 0) {}
    for (paramInt = zzau(paramInt);; paramInt = 10) {
      return paramInt;
    }
  }
  
  private final void zzar(int paramInt)
    throws IOException
  {
    byte b = (byte)paramInt;
    if (!this.zzbzg.hasRemaining()) {
      throw new zzabc(this.zzbzg.position(), this.zzbzg.limit());
    }
    this.zzbzg.put(b);
  }
  
  public static int zzas(int paramInt)
  {
    return zzau(paramInt << 3);
  }
  
  public static int zzau(int paramInt)
  {
    if ((paramInt & 0xFFFFFF80) == 0) {
      paramInt = 1;
    }
    for (;;)
    {
      return paramInt;
      if ((paramInt & 0xC000) == 0) {
        paramInt = 2;
      } else if ((0xFFE00000 & paramInt) == 0) {
        paramInt = 3;
      } else if ((0xF0000000 & paramInt) == 0) {
        paramInt = 4;
      } else {
        paramInt = 5;
      }
    }
  }
  
  public static int zzb(int paramInt, zzabj paramzzabj)
  {
    int i = zzas(paramInt);
    paramInt = paramzzabj.zzwg();
    return i + (paramInt + zzau(paramInt));
  }
  
  public static zzabb zzb(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return new zzabb(paramArrayOfByte, 0, paramInt2);
  }
  
  public static int zzc(int paramInt, long paramLong)
  {
    return zzas(paramInt) + zzap(paramLong);
  }
  
  public static int zzd(int paramInt, String paramString)
  {
    return zzas(paramInt) + zzfp(paramString);
  }
  
  public static int zzf(int paramInt1, int paramInt2)
  {
    return zzas(paramInt1) + zzaq(paramInt2);
  }
  
  public static int zzfp(String paramString)
  {
    int i = zza(paramString);
    return i + zzau(i);
  }
  
  public static zzabb zzk(byte[] paramArrayOfByte)
  {
    return zzb(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public final void zza(int paramInt, double paramDouble)
    throws IOException
  {
    zzg(paramInt, 1);
    long l = Double.doubleToLongBits(paramDouble);
    if (this.zzbzg.remaining() < 8) {
      throw new zzabc(this.zzbzg.position(), this.zzbzg.limit());
    }
    this.zzbzg.putLong(l);
  }
  
  public final void zza(int paramInt, float paramFloat)
    throws IOException
  {
    zzg(paramInt, 5);
    paramInt = Float.floatToIntBits(paramFloat);
    if (this.zzbzg.remaining() < 4) {
      throw new zzabc(this.zzbzg.position(), this.zzbzg.limit());
    }
    this.zzbzg.putInt(paramInt);
  }
  
  public final void zza(int paramInt, long paramLong)
    throws IOException
  {
    zzg(paramInt, 0);
    zzao(paramLong);
  }
  
  public final void zza(int paramInt, zzabj paramzzabj)
    throws IOException
  {
    zzg(paramInt, 2);
    zzb(paramzzabj);
  }
  
  public final void zza(int paramInt, boolean paramBoolean)
    throws IOException
  {
    int i = 0;
    zzg(paramInt, 0);
    paramInt = i;
    if (paramBoolean) {
      paramInt = 1;
    }
    byte b = (byte)paramInt;
    if (!this.zzbzg.hasRemaining()) {
      throw new zzabc(this.zzbzg.position(), this.zzbzg.limit());
    }
    this.zzbzg.put(b);
  }
  
  public final void zzat(int paramInt)
    throws IOException
  {
    for (;;)
    {
      if ((paramInt & 0xFFFFFF80) == 0)
      {
        zzar(paramInt);
        return;
      }
      zzar(paramInt & 0x7F | 0x80);
      paramInt >>>= 7;
    }
  }
  
  public final void zzb(int paramInt, long paramLong)
    throws IOException
  {
    zzg(paramInt, 0);
    zzao(paramLong);
  }
  
  public final void zzb(zzabj paramzzabj)
    throws IOException
  {
    zzat(paramzzabj.zzwf());
    paramzzabj.zza(this);
  }
  
  public final void zzc(int paramInt, String paramString)
    throws IOException
  {
    zzg(paramInt, 2);
    int i;
    int j;
    try
    {
      i = zzau(paramString.length());
      if (i != zzau(paramString.length() * 3)) {
        break label160;
      }
      j = this.zzbzg.position();
      if (this.zzbzg.remaining() < i)
      {
        paramString = new com/google/android/gms/internal/measurement/zzabc;
        paramString.<init>(i + j, this.zzbzg.limit());
        throw paramString;
      }
    }
    catch (BufferOverflowException paramString)
    {
      zzabc localzzabc = new zzabc(this.zzbzg.position(), this.zzbzg.limit());
      localzzabc.initCause(paramString);
      throw localzzabc;
    }
    this.zzbzg.position(j + i);
    zza(paramString, this.zzbzg);
    paramInt = this.zzbzg.position();
    this.zzbzg.position(j);
    zzat(paramInt - j - i);
    this.zzbzg.position(paramInt);
    for (;;)
    {
      return;
      label160:
      zzat(zza(paramString));
      zza(paramString, this.zzbzg);
    }
  }
  
  public final void zze(int paramInt1, int paramInt2)
    throws IOException
  {
    zzg(paramInt1, 0);
    if (paramInt2 >= 0) {
      zzat(paramInt2);
    }
    for (;;)
    {
      return;
      zzao(paramInt2);
    }
  }
  
  public final void zzg(int paramInt1, int paramInt2)
    throws IOException
  {
    zzat(paramInt1 << 3 | paramInt2);
  }
  
  public final void zzl(byte[] paramArrayOfByte)
    throws IOException
  {
    int i = paramArrayOfByte.length;
    if (this.zzbzg.remaining() >= i)
    {
      this.zzbzg.put(paramArrayOfByte, 0, i);
      return;
    }
    throw new zzabc(this.zzbzg.position(), this.zzbzg.limit());
  }
  
  public final void zzvy()
  {
    if (this.zzbzg.remaining() != 0) {
      throw new IllegalStateException(String.format("Did not write as much data as expected, %s bytes remaining.", new Object[] { Integer.valueOf(this.zzbzg.remaining()) }));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzabb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */