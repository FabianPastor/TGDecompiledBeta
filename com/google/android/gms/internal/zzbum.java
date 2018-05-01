package com.google.android.gms.internal;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;

public final class zzbum
{
  private final ByteBuffer zzcrW;
  
  private zzbum(ByteBuffer paramByteBuffer)
  {
    this.zzcrW = paramByteBuffer;
    this.zzcrW.order(ByteOrder.LITTLE_ENDIAN);
  }
  
  private zzbum(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this(ByteBuffer.wrap(paramArrayOfByte, paramInt1, paramInt2));
  }
  
  public static int zzH(int paramInt1, int paramInt2)
  {
    return zzqs(paramInt1) + zzqp(paramInt2);
  }
  
  public static int zzI(int paramInt1, int paramInt2)
  {
    return zzqs(paramInt1) + zzqq(paramInt2);
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
  
  public static zzbum zzae(byte[] paramArrayOfByte)
  {
    return zzc(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public static int zzag(byte[] paramArrayOfByte)
  {
    return zzqu(paramArrayOfByte.length) + paramArrayOfByte.length;
  }
  
  public static int zzb(int paramInt, double paramDouble)
  {
    return zzqs(paramInt) + 8;
  }
  
  public static int zzb(int paramInt, zzbut paramzzbut)
  {
    return zzqs(paramInt) * 2 + zzd(paramzzbut);
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
  
  public static int zzba(long paramLong)
  {
    return zzbe(paramLong);
  }
  
  public static int zzbb(long paramLong)
  {
    return zzbe(paramLong);
  }
  
  public static int zzbc(long paramLong)
  {
    return zzbe(zzbg(paramLong));
  }
  
  public static int zzbe(long paramLong)
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
  
  public static long zzbg(long paramLong)
  {
    return paramLong << 1 ^ paramLong >> 63;
  }
  
  public static int zzc(int paramInt, zzbut paramzzbut)
  {
    return zzqs(paramInt) + zze(paramzzbut);
  }
  
  public static int zzc(int paramInt, byte[] paramArrayOfByte)
  {
    return zzqs(paramInt) + zzag(paramArrayOfByte);
  }
  
  public static zzbum zzc(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return new zzbum(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public static int zzd(int paramInt, float paramFloat)
  {
    return zzqs(paramInt) + 4;
  }
  
  public static int zzd(zzbut paramzzbut)
  {
    return paramzzbut.zzacZ();
  }
  
  public static int zze(int paramInt, long paramLong)
  {
    return zzqs(paramInt) + zzba(paramLong);
  }
  
  public static int zze(zzbut paramzzbut)
  {
    int i = paramzzbut.zzacZ();
    return i + zzqu(i);
  }
  
  public static int zzf(int paramInt, long paramLong)
  {
    return zzqs(paramInt) + zzbb(paramLong);
  }
  
  public static int zzg(int paramInt, long paramLong)
  {
    return zzqs(paramInt) + 8;
  }
  
  public static int zzh(int paramInt, long paramLong)
  {
    return zzqs(paramInt) + zzbc(paramLong);
  }
  
  public static int zzh(int paramInt, boolean paramBoolean)
  {
    return zzqs(paramInt) + 1;
  }
  
  public static int zzkc(String paramString)
  {
    int i = zzb(paramString);
    return i + zzqu(i);
  }
  
  public static int zzqp(int paramInt)
  {
    if (paramInt >= 0) {
      return zzqu(paramInt);
    }
    return 10;
  }
  
  public static int zzqq(int paramInt)
  {
    return zzqu(zzqw(paramInt));
  }
  
  public static int zzqs(int paramInt)
  {
    return zzqu(zzbuw.zzK(paramInt, 0));
  }
  
  public static int zzqu(int paramInt)
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
  
  public static int zzqw(int paramInt)
  {
    return paramInt << 1 ^ paramInt >> 31;
  }
  
  public static int zzr(int paramInt, String paramString)
  {
    return zzqs(paramInt) + zzkc(paramString);
  }
  
  public void zzF(int paramInt1, int paramInt2)
    throws IOException
  {
    zzJ(paramInt1, 0);
    zzqn(paramInt2);
  }
  
  public void zzG(int paramInt1, int paramInt2)
    throws IOException
  {
    zzJ(paramInt1, 0);
    zzqo(paramInt2);
  }
  
  public void zzJ(int paramInt1, int paramInt2)
    throws IOException
  {
    zzqt(zzbuw.zzK(paramInt1, paramInt2));
  }
  
  public void zza(int paramInt, double paramDouble)
    throws IOException
  {
    zzJ(paramInt, 1);
    zzn(paramDouble);
  }
  
  public void zza(int paramInt, long paramLong)
    throws IOException
  {
    zzJ(paramInt, 0);
    zzaW(paramLong);
  }
  
  public void zza(int paramInt, zzbut paramzzbut)
    throws IOException
  {
    zzJ(paramInt, 2);
    zzc(paramzzbut);
  }
  
  public void zzaW(long paramLong)
    throws IOException
  {
    zzbd(paramLong);
  }
  
  public void zzaX(long paramLong)
    throws IOException
  {
    zzbd(paramLong);
  }
  
  public void zzaY(long paramLong)
    throws IOException
  {
    zzbf(paramLong);
  }
  
  public void zzaZ(long paramLong)
    throws IOException
  {
    zzbd(zzbg(paramLong));
  }
  
  public int zzacL()
  {
    return this.zzcrW.remaining();
  }
  
  public void zzacM()
  {
    if (zzacL() != 0) {
      throw new IllegalStateException("Did not write as much data as expected.");
    }
  }
  
  public void zzaf(byte[] paramArrayOfByte)
    throws IOException
  {
    zzqt(paramArrayOfByte.length);
    zzah(paramArrayOfByte);
  }
  
  public void zzah(byte[] paramArrayOfByte)
    throws IOException
  {
    zzd(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public void zzb(int paramInt, long paramLong)
    throws IOException
  {
    zzJ(paramInt, 0);
    zzaX(paramLong);
  }
  
  public void zzb(int paramInt, byte[] paramArrayOfByte)
    throws IOException
  {
    zzJ(paramInt, 2);
    zzaf(paramArrayOfByte);
  }
  
  public void zzb(zzbut paramzzbut)
    throws IOException
  {
    paramzzbut.zza(this);
  }
  
  public void zzbd(long paramLong)
    throws IOException
  {
    for (;;)
    {
      if ((0xFFFFFFFFFFFFFF80 & paramLong) == 0L)
      {
        zzqr((int)paramLong);
        return;
      }
      zzqr((int)paramLong & 0x7F | 0x80);
      paramLong >>>= 7;
    }
  }
  
  public void zzbf(long paramLong)
    throws IOException
  {
    if (this.zzcrW.remaining() < 8) {
      throw new zza(this.zzcrW.position(), this.zzcrW.limit());
    }
    this.zzcrW.putLong(paramLong);
  }
  
  public void zzbl(boolean paramBoolean)
    throws IOException
  {
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      zzqr(i);
      return;
    }
  }
  
  public void zzc(byte paramByte)
    throws IOException
  {
    if (!this.zzcrW.hasRemaining()) {
      throw new zza(this.zzcrW.position(), this.zzcrW.limit());
    }
    this.zzcrW.put(paramByte);
  }
  
  public void zzc(int paramInt, float paramFloat)
    throws IOException
  {
    zzJ(paramInt, 5);
    zzk(paramFloat);
  }
  
  public void zzc(int paramInt, long paramLong)
    throws IOException
  {
    zzJ(paramInt, 1);
    zzaY(paramLong);
  }
  
  public void zzc(zzbut paramzzbut)
    throws IOException
  {
    zzqt(paramzzbut.zzacY());
    paramzzbut.zza(this);
  }
  
  public void zzd(int paramInt, long paramLong)
    throws IOException
  {
    zzJ(paramInt, 0);
    zzaZ(paramLong);
  }
  
  public void zzd(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (this.zzcrW.remaining() >= paramInt2)
    {
      this.zzcrW.put(paramArrayOfByte, paramInt1, paramInt2);
      return;
    }
    throw new zza(this.zzcrW.position(), this.zzcrW.limit());
  }
  
  public void zzg(int paramInt, boolean paramBoolean)
    throws IOException
  {
    zzJ(paramInt, 0);
    zzbl(paramBoolean);
  }
  
  public void zzk(float paramFloat)
    throws IOException
  {
    zzqv(Float.floatToIntBits(paramFloat));
  }
  
  public void zzkb(String paramString)
    throws IOException
  {
    int i;
    int j;
    try
    {
      i = zzqu(paramString.length());
      if (i != zzqu(paramString.length() * 3)) {
        break label150;
      }
      j = this.zzcrW.position();
      if (this.zzcrW.remaining() < i) {
        throw new zza(i + j, this.zzcrW.limit());
      }
    }
    catch (BufferOverflowException paramString)
    {
      zza localzza = new zza(this.zzcrW.position(), this.zzcrW.limit());
      localzza.initCause(paramString);
      throw localzza;
    }
    this.zzcrW.position(j + i);
    zza(paramString, this.zzcrW);
    int k = this.zzcrW.position();
    this.zzcrW.position(j);
    zzqt(k - j - i);
    this.zzcrW.position(k);
    return;
    label150:
    zzqt(zzb(paramString));
    zza(paramString, this.zzcrW);
  }
  
  public void zzn(double paramDouble)
    throws IOException
  {
    zzbf(Double.doubleToLongBits(paramDouble));
  }
  
  public void zzq(int paramInt, String paramString)
    throws IOException
  {
    zzJ(paramInt, 2);
    zzkb(paramString);
  }
  
  public void zzqn(int paramInt)
    throws IOException
  {
    if (paramInt >= 0)
    {
      zzqt(paramInt);
      return;
    }
    zzbd(paramInt);
  }
  
  public void zzqo(int paramInt)
    throws IOException
  {
    zzqt(zzqw(paramInt));
  }
  
  public void zzqr(int paramInt)
    throws IOException
  {
    zzc((byte)paramInt);
  }
  
  public void zzqt(int paramInt)
    throws IOException
  {
    for (;;)
    {
      if ((paramInt & 0xFFFFFF80) == 0)
      {
        zzqr(paramInt);
        return;
      }
      zzqr(paramInt & 0x7F | 0x80);
      paramInt >>>= 7;
    }
  }
  
  public void zzqv(int paramInt)
    throws IOException
  {
    if (this.zzcrW.remaining() < 4) {
      throw new zza(this.zzcrW.position(), this.zzcrW.limit());
    }
    this.zzcrW.putInt(paramInt);
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbum.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */