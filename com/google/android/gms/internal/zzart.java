package com.google.android.gms.internal;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;

public final class zzart
{
  private final ByteBuffer btF;
  
  private zzart(ByteBuffer paramByteBuffer)
  {
    this.btF = paramByteBuffer;
    this.btF.order(ByteOrder.LITTLE_ENDIAN);
  }
  
  private zzart(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
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
  
  public static int zzagz(int paramInt)
  {
    if (paramInt >= 0) {
      return zzahe(paramInt);
    }
    return 10;
  }
  
  public static int zzah(int paramInt1, int paramInt2)
  {
    return zzahc(paramInt1) + zzagz(paramInt2);
  }
  
  public static int zzaha(int paramInt)
  {
    return zzahe(zzahg(paramInt));
  }
  
  public static int zzahc(int paramInt)
  {
    return zzahe(zzasd.zzak(paramInt, 0));
  }
  
  public static int zzahe(int paramInt)
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
  
  public static int zzahg(int paramInt)
  {
    return paramInt << 1 ^ paramInt >> 31;
  }
  
  public static int zzai(int paramInt1, int paramInt2)
  {
    return zzahc(paramInt1) + zzaha(paramInt2);
  }
  
  public static int zzb(int paramInt, double paramDouble)
  {
    return zzahc(paramInt) + 8;
  }
  
  public static int zzb(int paramInt, zzasa paramzzasa)
  {
    return zzahc(paramInt) * 2 + zzd(paramzzasa);
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
  
  public static zzart zzbe(byte[] paramArrayOfByte)
  {
    return zzc(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public static int zzbg(byte[] paramArrayOfByte)
  {
    return zzahe(paramArrayOfByte.length) + paramArrayOfByte.length;
  }
  
  public static int zzc(int paramInt, zzasa paramzzasa)
  {
    return zzahc(paramInt) + zze(paramzzasa);
  }
  
  public static int zzc(int paramInt, byte[] paramArrayOfByte)
  {
    return zzahc(paramInt) + zzbg(paramArrayOfByte);
  }
  
  public static zzart zzc(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return new zzart(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public static int zzcy(long paramLong)
  {
    return zzdc(paramLong);
  }
  
  public static int zzcz(long paramLong)
  {
    return zzdc(paramLong);
  }
  
  public static int zzd(int paramInt, float paramFloat)
  {
    return zzahc(paramInt) + 4;
  }
  
  public static int zzd(zzasa paramzzasa)
  {
    return paramzzasa.cz();
  }
  
  public static int zzda(long paramLong)
  {
    return zzdc(zzde(paramLong));
  }
  
  public static int zzdc(long paramLong)
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
  
  public static long zzde(long paramLong)
  {
    return paramLong << 1 ^ paramLong >> 63;
  }
  
  public static int zze(int paramInt, long paramLong)
  {
    return zzahc(paramInt) + zzcy(paramLong);
  }
  
  public static int zze(zzasa paramzzasa)
  {
    int i = paramzzasa.cz();
    return i + zzahe(i);
  }
  
  public static int zzf(int paramInt, long paramLong)
  {
    return zzahc(paramInt) + zzcz(paramLong);
  }
  
  public static int zzg(int paramInt, long paramLong)
  {
    return zzahc(paramInt) + 8;
  }
  
  public static int zzh(int paramInt, long paramLong)
  {
    return zzahc(paramInt) + zzda(paramLong);
  }
  
  public static int zzh(int paramInt, boolean paramBoolean)
  {
    return zzahc(paramInt) + 1;
  }
  
  public static int zzr(int paramInt, String paramString)
  {
    return zzahc(paramInt) + zzuy(paramString);
  }
  
  public static int zzuy(String paramString)
  {
    int i = zzb(paramString);
    return i + zzahe(i);
  }
  
  public int cl()
  {
    return this.btF.remaining();
  }
  
  public void cm()
  {
    if (cl() != 0) {
      throw new IllegalStateException("Did not write as much data as expected.");
    }
  }
  
  public void zza(int paramInt, double paramDouble)
    throws IOException
  {
    zzaj(paramInt, 1);
    zzn(paramDouble);
  }
  
  public void zza(int paramInt, long paramLong)
    throws IOException
  {
    zzaj(paramInt, 0);
    zzcu(paramLong);
  }
  
  public void zza(int paramInt, zzasa paramzzasa)
    throws IOException
  {
    zzaj(paramInt, 2);
    zzc(paramzzasa);
  }
  
  public void zzaf(int paramInt1, int paramInt2)
    throws IOException
  {
    zzaj(paramInt1, 0);
    zzagx(paramInt2);
  }
  
  public void zzag(int paramInt1, int paramInt2)
    throws IOException
  {
    zzaj(paramInt1, 0);
    zzagy(paramInt2);
  }
  
  public void zzagx(int paramInt)
    throws IOException
  {
    if (paramInt >= 0)
    {
      zzahd(paramInt);
      return;
    }
    zzdb(paramInt);
  }
  
  public void zzagy(int paramInt)
    throws IOException
  {
    zzahd(zzahg(paramInt));
  }
  
  public void zzahb(int paramInt)
    throws IOException
  {
    zzc((byte)paramInt);
  }
  
  public void zzahd(int paramInt)
    throws IOException
  {
    for (;;)
    {
      if ((paramInt & 0xFFFFFF80) == 0)
      {
        zzahb(paramInt);
        return;
      }
      zzahb(paramInt & 0x7F | 0x80);
      paramInt >>>= 7;
    }
  }
  
  public void zzahf(int paramInt)
    throws IOException
  {
    if (this.btF.remaining() < 4) {
      throw new zza(this.btF.position(), this.btF.limit());
    }
    this.btF.putInt(paramInt);
  }
  
  public void zzaj(int paramInt1, int paramInt2)
    throws IOException
  {
    zzahd(zzasd.zzak(paramInt1, paramInt2));
  }
  
  public void zzb(int paramInt, long paramLong)
    throws IOException
  {
    zzaj(paramInt, 0);
    zzcv(paramLong);
  }
  
  public void zzb(int paramInt, byte[] paramArrayOfByte)
    throws IOException
  {
    zzaj(paramInt, 2);
    zzbf(paramArrayOfByte);
  }
  
  public void zzb(zzasa paramzzasa)
    throws IOException
  {
    paramzzasa.zza(this);
  }
  
  public void zzbf(byte[] paramArrayOfByte)
    throws IOException
  {
    zzahd(paramArrayOfByte.length);
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
    if (!this.btF.hasRemaining()) {
      throw new zza(this.btF.position(), this.btF.limit());
    }
    this.btF.put(paramByte);
  }
  
  public void zzc(int paramInt, float paramFloat)
    throws IOException
  {
    zzaj(paramInt, 5);
    zzk(paramFloat);
  }
  
  public void zzc(int paramInt, long paramLong)
    throws IOException
  {
    zzaj(paramInt, 1);
    zzcw(paramLong);
  }
  
  public void zzc(zzasa paramzzasa)
    throws IOException
  {
    zzahd(paramzzasa.cy());
    paramzzasa.zza(this);
  }
  
  public void zzcu(long paramLong)
    throws IOException
  {
    zzdb(paramLong);
  }
  
  public void zzcv(long paramLong)
    throws IOException
  {
    zzdb(paramLong);
  }
  
  public void zzcw(long paramLong)
    throws IOException
  {
    zzdd(paramLong);
  }
  
  public void zzcx(long paramLong)
    throws IOException
  {
    zzdb(zzde(paramLong));
  }
  
  public void zzd(int paramInt, long paramLong)
    throws IOException
  {
    zzaj(paramInt, 0);
    zzcx(paramLong);
  }
  
  public void zzd(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (this.btF.remaining() >= paramInt2)
    {
      this.btF.put(paramArrayOfByte, paramInt1, paramInt2);
      return;
    }
    throw new zza(this.btF.position(), this.btF.limit());
  }
  
  public void zzdb(long paramLong)
    throws IOException
  {
    for (;;)
    {
      if ((0xFFFFFFFFFFFFFF80 & paramLong) == 0L)
      {
        zzahb((int)paramLong);
        return;
      }
      zzahb((int)paramLong & 0x7F | 0x80);
      paramLong >>>= 7;
    }
  }
  
  public void zzdd(long paramLong)
    throws IOException
  {
    if (this.btF.remaining() < 8) {
      throw new zza(this.btF.position(), this.btF.limit());
    }
    this.btF.putLong(paramLong);
  }
  
  public void zzdm(boolean paramBoolean)
    throws IOException
  {
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      zzahb(i);
      return;
    }
  }
  
  public void zzg(int paramInt, boolean paramBoolean)
    throws IOException
  {
    zzaj(paramInt, 0);
    zzdm(paramBoolean);
  }
  
  public void zzk(float paramFloat)
    throws IOException
  {
    zzahf(Float.floatToIntBits(paramFloat));
  }
  
  public void zzn(double paramDouble)
    throws IOException
  {
    zzdd(Double.doubleToLongBits(paramDouble));
  }
  
  public void zzq(int paramInt, String paramString)
    throws IOException
  {
    zzaj(paramInt, 2);
    zzux(paramString);
  }
  
  public void zzux(String paramString)
    throws IOException
  {
    int i;
    int j;
    try
    {
      i = zzahe(paramString.length());
      if (i != zzahe(paramString.length() * 3)) {
        break label150;
      }
      j = this.btF.position();
      if (this.btF.remaining() < i) {
        throw new zza(i + j, this.btF.limit());
      }
    }
    catch (BufferOverflowException paramString)
    {
      zza localzza = new zza(this.btF.position(), this.btF.limit());
      localzza.initCause(paramString);
      throw localzza;
    }
    this.btF.position(j + i);
    zza(paramString, this.btF);
    int k = this.btF.position();
    this.btF.position(j);
    zzahd(k - j - i);
    this.btF.position(k);
    return;
    label150:
    zzahd(zzb(paramString));
    zza(paramString, this.btF);
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzart.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */