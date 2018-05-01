package com.google.android.gms.internal.firebase_abt;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class zzb
{
  private final ByteBuffer zzr;
  
  private zzb(ByteBuffer paramByteBuffer)
  {
    this.zzr = paramByteBuffer;
    this.zzr.order(ByteOrder.LITTLE_ENDIAN);
  }
  
  private zzb(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this(ByteBuffer.wrap(paramArrayOfByte, 0, paramInt2));
  }
  
  public static zzb zzb(byte[] paramArrayOfByte)
  {
    return new zzb(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  private final void zzd(int paramInt)
    throws IOException
  {
    byte b = (byte)paramInt;
    if (!this.zzr.hasRemaining()) {
      throw new zzc(this.zzr.position(), this.zzr.limit());
    }
    this.zzr.put(b);
  }
  
  public static int zzf(int paramInt)
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
  
  public final void zzc(byte[] paramArrayOfByte)
    throws IOException
  {
    int i = paramArrayOfByte.length;
    if (this.zzr.remaining() >= i)
    {
      this.zzr.put(paramArrayOfByte, 0, i);
      return;
    }
    throw new zzc(this.zzr.position(), this.zzr.limit());
  }
  
  public final void zze(int paramInt)
    throws IOException
  {
    for (;;)
    {
      if ((paramInt & 0xFFFFFF80) == 0)
      {
        zzd(paramInt);
        return;
      }
      zzd(paramInt & 0x7F | 0x80);
      paramInt >>>= 7;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/firebase_abt/zzb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */