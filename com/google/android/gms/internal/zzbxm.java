package com.google.android.gms.internal;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;

public final class zzbxm
{
  private final ByteBuffer zzcuH;
  
  private zzbxm(ByteBuffer paramByteBuffer)
  {
    this.zzcuH = paramByteBuffer;
    this.zzcuH.order(ByteOrder.LITTLE_ENDIAN);
  }
  
  private zzbxm(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this(ByteBuffer.wrap(paramArrayOfByte, paramInt1, paramInt2));
  }
  
  public static int zzL(int paramInt1, int paramInt2)
  {
    return zzrj(paramInt1) + zzrg(paramInt2);
  }
  
  public static int zzM(int paramInt1, int paramInt2)
  {
    return zzrj(paramInt1) + zzrh(paramInt2);
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
  
  public static zzbxm zzag(byte[] paramArrayOfByte)
  {
    return zzc(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public static int zzai(byte[] paramArrayOfByte)
  {
    return zzrl(paramArrayOfByte.length) + paramArrayOfByte.length;
  }
  
  public static int zzb(int paramInt, double paramDouble)
  {
    return zzrj(paramInt) + 8;
  }
  
  public static int zzb(int paramInt, zzbxt paramzzbxt)
  {
    return zzrj(paramInt) * 2 + zzd(paramzzbxt);
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
  
  public static int zzbe(long paramLong)
  {
    return zzbi(paramLong);
  }
  
  public static int zzbf(long paramLong)
  {
    return zzbi(paramLong);
  }
  
  public static int zzbg(long paramLong)
  {
    return zzbi(zzbk(paramLong));
  }
  
  public static int zzbi(long paramLong)
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
  
  public static long zzbk(long paramLong)
  {
    return paramLong << 1 ^ paramLong >> 63;
  }
  
  public static int zzc(int paramInt, zzbxt paramzzbxt)
  {
    return zzrj(paramInt) + zze(paramzzbxt);
  }
  
  public static int zzc(int paramInt, byte[] paramArrayOfByte)
  {
    return zzrj(paramInt) + zzai(paramArrayOfByte);
  }
  
  public static zzbxm zzc(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return new zzbxm(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public static int zzd(int paramInt, float paramFloat)
  {
    return zzrj(paramInt) + 4;
  }
  
  public static int zzd(zzbxt paramzzbxt)
  {
    return paramzzbxt.zzaeT();
  }
  
  public static int zze(int paramInt, long paramLong)
  {
    return zzrj(paramInt) + zzbe(paramLong);
  }
  
  public static int zze(zzbxt paramzzbxt)
  {
    int i = paramzzbxt.zzaeT();
    return i + zzrl(i);
  }
  
  public static int zzf(int paramInt, long paramLong)
  {
    return zzrj(paramInt) + zzbf(paramLong);
  }
  
  public static int zzg(int paramInt, long paramLong)
  {
    return zzrj(paramInt) + 8;
  }
  
  public static int zzh(int paramInt, long paramLong)
  {
    return zzrj(paramInt) + zzbg(paramLong);
  }
  
  public static int zzh(int paramInt, boolean paramBoolean)
  {
    return zzrj(paramInt) + 1;
  }
  
  public static int zzkb(String paramString)
  {
    int i = zzb(paramString);
    return i + zzrl(i);
  }
  
  public static int zzr(int paramInt, String paramString)
  {
    return zzrj(paramInt) + zzkb(paramString);
  }
  
  public static int zzrg(int paramInt)
  {
    if (paramInt >= 0) {
      return zzrl(paramInt);
    }
    return 10;
  }
  
  public static int zzrh(int paramInt)
  {
    return zzrl(zzrn(paramInt));
  }
  
  public static int zzrj(int paramInt)
  {
    return zzrl(zzbxw.zzO(paramInt, 0));
  }
  
  public static int zzrl(int paramInt)
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
  
  public static int zzrn(int paramInt)
  {
    return paramInt << 1 ^ paramInt >> 31;
  }
  
  public void zzJ(int paramInt1, int paramInt2)
    throws IOException
  {
    zzN(paramInt1, 0);
    zzre(paramInt2);
  }
  
  public void zzK(int paramInt1, int paramInt2)
    throws IOException
  {
    zzN(paramInt1, 0);
    zzrf(paramInt2);
  }
  
  public void zzN(int paramInt1, int paramInt2)
    throws IOException
  {
    zzrk(zzbxw.zzO(paramInt1, paramInt2));
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
    zzba(paramLong);
  }
  
  public void zza(int paramInt, zzbxt paramzzbxt)
    throws IOException
  {
    zzN(paramInt, 2);
    zzc(paramzzbxt);
  }
  
  public int zzaeF()
  {
    return this.zzcuH.remaining();
  }
  
  public void zzaeG()
  {
    if (zzaeF() != 0) {
      throw new IllegalStateException("Did not write as much data as expected.");
    }
  }
  
  public void zzah(byte[] paramArrayOfByte)
    throws IOException
  {
    zzrk(paramArrayOfByte.length);
    zzaj(paramArrayOfByte);
  }
  
  public void zzaj(byte[] paramArrayOfByte)
    throws IOException
  {
    zzd(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public void zzb(int paramInt, long paramLong)
    throws IOException
  {
    zzN(paramInt, 0);
    zzbb(paramLong);
  }
  
  public void zzb(int paramInt, byte[] paramArrayOfByte)
    throws IOException
  {
    zzN(paramInt, 2);
    zzah(paramArrayOfByte);
  }
  
  public void zzb(zzbxt paramzzbxt)
    throws IOException
  {
    paramzzbxt.zza(this);
  }
  
  public void zzba(long paramLong)
    throws IOException
  {
    zzbh(paramLong);
  }
  
  public void zzbb(long paramLong)
    throws IOException
  {
    zzbh(paramLong);
  }
  
  public void zzbc(long paramLong)
    throws IOException
  {
    zzbj(paramLong);
  }
  
  public void zzbd(long paramLong)
    throws IOException
  {
    zzbh(zzbk(paramLong));
  }
  
  public void zzbh(long paramLong)
    throws IOException
  {
    for (;;)
    {
      if ((0xFFFFFFFFFFFFFF80 & paramLong) == 0L)
      {
        zzri((int)paramLong);
        return;
      }
      zzri((int)paramLong & 0x7F | 0x80);
      paramLong >>>= 7;
    }
  }
  
  public void zzbj(long paramLong)
    throws IOException
  {
    if (this.zzcuH.remaining() < 8) {
      throw new zza(this.zzcuH.position(), this.zzcuH.limit());
    }
    this.zzcuH.putLong(paramLong);
  }
  
  public void zzbq(boolean paramBoolean)
    throws IOException
  {
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      zzri(i);
      return;
    }
  }
  
  public void zzc(byte paramByte)
    throws IOException
  {
    if (!this.zzcuH.hasRemaining()) {
      throw new zza(this.zzcuH.position(), this.zzcuH.limit());
    }
    this.zzcuH.put(paramByte);
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
    zzbc(paramLong);
  }
  
  public void zzc(zzbxt paramzzbxt)
    throws IOException
  {
    zzrk(paramzzbxt.zzaeS());
    paramzzbxt.zza(this);
  }
  
  public void zzd(int paramInt, long paramLong)
    throws IOException
  {
    zzN(paramInt, 0);
    zzbd(paramLong);
  }
  
  public void zzd(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (this.zzcuH.remaining() >= paramInt2)
    {
      this.zzcuH.put(paramArrayOfByte, paramInt1, paramInt2);
      return;
    }
    throw new zza(this.zzcuH.position(), this.zzcuH.limit());
  }
  
  public void zzg(int paramInt, boolean paramBoolean)
    throws IOException
  {
    zzN(paramInt, 0);
    zzbq(paramBoolean);
  }
  
  public void zzk(float paramFloat)
    throws IOException
  {
    zzrm(Float.floatToIntBits(paramFloat));
  }
  
  public void zzka(String paramString)
    throws IOException
  {
    int i;
    int j;
    try
    {
      i = zzrl(paramString.length());
      if (i != zzrl(paramString.length() * 3)) {
        break label150;
      }
      j = this.zzcuH.position();
      if (this.zzcuH.remaining() < i) {
        throw new zza(i + j, this.zzcuH.limit());
      }
    }
    catch (BufferOverflowException paramString)
    {
      zza localzza = new zza(this.zzcuH.position(), this.zzcuH.limit());
      localzza.initCause(paramString);
      throw localzza;
    }
    this.zzcuH.position(j + i);
    zza(paramString, this.zzcuH);
    int k = this.zzcuH.position();
    this.zzcuH.position(j);
    zzrk(k - j - i);
    this.zzcuH.position(k);
    return;
    label150:
    zzrk(zzb(paramString));
    zza(paramString, this.zzcuH);
  }
  
  public void zzn(double paramDouble)
    throws IOException
  {
    zzbj(Double.doubleToLongBits(paramDouble));
  }
  
  public void zzq(int paramInt, String paramString)
    throws IOException
  {
    zzN(paramInt, 2);
    zzka(paramString);
  }
  
  public void zzre(int paramInt)
    throws IOException
  {
    if (paramInt >= 0)
    {
      zzrk(paramInt);
      return;
    }
    zzbh(paramInt);
  }
  
  public void zzrf(int paramInt)
    throws IOException
  {
    zzrk(zzrn(paramInt));
  }
  
  public void zzri(int paramInt)
    throws IOException
  {
    zzc((byte)paramInt);
  }
  
  public void zzrk(int paramInt)
    throws IOException
  {
    for (;;)
    {
      if ((paramInt & 0xFFFFFF80) == 0)
      {
        zzri(paramInt);
        return;
      }
      zzri(paramInt & 0x7F | 0x80);
      paramInt >>>= 7;
    }
  }
  
  public void zzrm(int paramInt)
    throws IOException
  {
    if (this.zzcuH.remaining() < 4) {
      throw new zza(this.zzcuH.position(), this.zzcuH.limit());
    }
    this.zzcuH.putInt(paramInt);
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbxm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */