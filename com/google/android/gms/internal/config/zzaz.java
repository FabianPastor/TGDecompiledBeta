package com.google.android.gms.internal.config;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;

public final class zzaz
{
  private final ByteBuffer zzch;
  
  private zzaz(ByteBuffer paramByteBuffer)
  {
    this.zzch = paramByteBuffer;
    this.zzch.order(ByteOrder.LITTLE_ENDIAN);
  }
  
  private zzaz(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
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
  
  public static zzaz zza(byte[] paramArrayOfByte)
  {
    return zzb(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  private static void zza(CharSequence paramCharSequence, ByteBuffer paramByteBuffer)
  {
    int i = 0;
    int j = 0;
    if (paramByteBuffer.isReadOnly()) {
      throw new ReadOnlyBufferException();
    }
    int i3;
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
        int i2;
        if ((57343 < i1) && (i <= n - 3))
        {
          k = i + 1;
          i2 = (byte)(i1 >>> 12 | 0x1E0);
          arrayOfByte[i] = ((byte)i2);
          i2 = k + 1;
          arrayOfByte[k] = ((byte)(byte)(i1 >>> 6 & 0x3F | 0x80));
          i = i2 + 1;
          arrayOfByte[i2] = ((byte)(byte)(i1 & 0x3F | 0x80));
        }
        else if (i <= n - 4)
        {
          k = j;
          if (j + 1 != paramCharSequence.length())
          {
            j++;
            i3 = paramCharSequence.charAt(j);
            if (!Character.isSurrogatePair(i1, i3)) {
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
          k = Character.toCodePoint(i1, i3);
          i2 = i + 1;
          arrayOfByte[i] = ((byte)(byte)(k >>> 18 | 0xF0));
          i = i2 + 1;
          arrayOfByte[i2] = ((byte)(byte)(k >>> 12 & 0x3F | 0x80));
          i2 = i + 1;
          arrayOfByte[i] = ((byte)(byte)(k >>> 6 & 0x3F | 0x80));
          i = i2 + 1;
          arrayOfByte[i2] = ((byte)(byte)(k & 0x3F | 0x80));
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
    if (i < k)
    {
      i3 = paramCharSequence.charAt(i);
      if (i3 >= 128) {
        break label634;
      }
      paramByteBuffer.put((byte)i3);
    }
    for (;;)
    {
      i++;
      break label597;
      break;
      label634:
      if (i3 < 2048)
      {
        paramByteBuffer.put((byte)(i3 >>> 6 | 0x3C0));
        paramByteBuffer.put((byte)(i3 & 0x3F | 0x80));
      }
      else if ((i3 < 55296) || (57343 < i3))
      {
        paramByteBuffer.put((byte)(i3 >>> 12 | 0x1E0));
        paramByteBuffer.put((byte)(i3 >>> 6 & 0x3F | 0x80));
        paramByteBuffer.put((byte)(i3 & 0x3F | 0x80));
      }
      else
      {
        j = i;
        char c;
        if (i + 1 != paramCharSequence.length())
        {
          j = i + 1;
          c = paramCharSequence.charAt(j);
          if (Character.isSurrogatePair(i3, c)) {}
        }
        else
        {
          throw new IllegalArgumentException(39 + "Unpaired surrogate at index " + (j - 1));
        }
        i = Character.toCodePoint(i3, c);
        paramByteBuffer.put((byte)(i >>> 18 | 0xF0));
        paramByteBuffer.put((byte)(i >>> 12 & 0x3F | 0x80));
        paramByteBuffer.put((byte)(i >>> 6 & 0x3F | 0x80));
        paramByteBuffer.put((byte)(i & 0x3F | 0x80));
        i = j;
      }
    }
  }
  
  public static int zzb(int paramInt, zzbh paramzzbh)
  {
    int i = zzl(paramInt);
    paramInt = paramzzbh.zzai();
    return i + (paramInt + zzn(paramInt));
  }
  
  public static int zzb(int paramInt, String paramString)
  {
    paramInt = zzl(paramInt);
    int i = zza(paramString);
    return paramInt + (i + zzn(i));
  }
  
  public static int zzb(byte[] paramArrayOfByte)
  {
    return zzn(paramArrayOfByte.length) + paramArrayOfByte.length;
  }
  
  public static zzaz zzb(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return new zzaz(paramArrayOfByte, 0, paramInt2);
  }
  
  public static int zzd(int paramInt1, int paramInt2)
  {
    return zzl(1) + zzj(paramInt2);
  }
  
  public static int zzj(int paramInt)
  {
    if (paramInt >= 0) {}
    for (paramInt = zzn(paramInt);; paramInt = 10) {
      return paramInt;
    }
  }
  
  private final void zzk(int paramInt)
    throws IOException
  {
    byte b = (byte)paramInt;
    if (!this.zzch.hasRemaining()) {
      throw new zzba(this.zzch.position(), this.zzch.limit());
    }
    this.zzch.put(b);
  }
  
  public static int zzl(int paramInt)
  {
    return zzn(paramInt << 3);
  }
  
  public static int zzn(int paramInt)
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
  
  public final void zza(byte paramByte)
    throws IOException
  {
    if (!this.zzch.hasRemaining()) {
      throw new zzba(this.zzch.position(), this.zzch.limit());
    }
    this.zzch.put(paramByte);
  }
  
  public final void zza(int paramInt, long paramLong)
    throws IOException
  {
    zze(paramInt, 1);
    if (this.zzch.remaining() < 8) {
      throw new zzba(this.zzch.position(), this.zzch.limit());
    }
    this.zzch.putLong(paramLong);
  }
  
  public final void zza(int paramInt, zzbh paramzzbh)
    throws IOException
  {
    zze(paramInt, 2);
    if (paramzzbh.zzcr < 0) {
      paramzzbh.zzai();
    }
    zzm(paramzzbh.zzcr);
    paramzzbh.zza(this);
  }
  
  public final void zza(int paramInt, String paramString)
    throws IOException
  {
    zze(paramInt, 2);
    int i;
    try
    {
      i = zzn(paramString.length());
      if (i != zzn(paramString.length() * 3)) {
        break label157;
      }
      paramInt = this.zzch.position();
      if (this.zzch.remaining() < i)
      {
        paramString = new com/google/android/gms/internal/config/zzba;
        paramString.<init>(i + paramInt, this.zzch.limit());
        throw paramString;
      }
    }
    catch (BufferOverflowException localBufferOverflowException)
    {
      paramString = new zzba(this.zzch.position(), this.zzch.limit());
      paramString.initCause(localBufferOverflowException);
      throw paramString;
    }
    this.zzch.position(paramInt + i);
    zza(paramString, this.zzch);
    int j = this.zzch.position();
    this.zzch.position(paramInt);
    zzm(j - paramInt - i);
    this.zzch.position(j);
    for (;;)
    {
      return;
      label157:
      zzm(zza(paramString));
      zza(paramString, this.zzch);
    }
  }
  
  public final void zza(int paramInt, byte[] paramArrayOfByte)
    throws IOException
  {
    zze(paramInt, 2);
    zzm(paramArrayOfByte.length);
    zzc(paramArrayOfByte);
  }
  
  public final void zzad()
  {
    if (this.zzch.remaining() != 0) {
      throw new IllegalStateException(String.format("Did not write as much data as expected, %s bytes remaining.", new Object[] { Integer.valueOf(this.zzch.remaining()) }));
    }
  }
  
  public final void zzc(int paramInt1, int paramInt2)
    throws IOException
  {
    zze(1, 0);
    if (paramInt2 >= 0)
    {
      zzm(paramInt2);
      return;
    }
    for (long l = paramInt2;; l >>>= 7)
    {
      if ((0xFFFFFFFFFFFFFF80 & l) == 0L)
      {
        b = (byte)(int)l;
        if (!this.zzch.hasRemaining()) {
          throw new zzba(this.zzch.position(), this.zzch.limit());
        }
        this.zzch.put(b);
        break;
      }
      byte b = (byte)((int)l & 0x7F | 0x80);
      if (!this.zzch.hasRemaining()) {
        throw new zzba(this.zzch.position(), this.zzch.limit());
      }
      this.zzch.put(b);
    }
  }
  
  public final void zzc(byte[] paramArrayOfByte)
    throws IOException
  {
    int i = paramArrayOfByte.length;
    if (this.zzch.remaining() >= i)
    {
      this.zzch.put(paramArrayOfByte, 0, i);
      return;
    }
    throw new zzba(this.zzch.position(), this.zzch.limit());
  }
  
  public final void zze(int paramInt1, int paramInt2)
    throws IOException
  {
    zzm(paramInt1 << 3 | paramInt2);
  }
  
  public final void zzm(int paramInt)
    throws IOException
  {
    for (;;)
    {
      if ((paramInt & 0xFFFFFF80) == 0)
      {
        zzk(paramInt);
        return;
      }
      zzk(paramInt & 0x7F | 0x80);
      paramInt >>>= 7;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/config/zzaz.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */