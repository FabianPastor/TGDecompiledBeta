package com.google.android.gms.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public final class zzaq
  extends ByteArrayOutputStream
{
  private final zzae zzap;
  
  public zzaq(zzae paramzzae, int paramInt)
  {
    this.zzap = paramzzae;
    this.buf = this.zzap.zzb(Math.max(paramInt, 256));
  }
  
  private final void zzc(int paramInt)
  {
    if (this.count + paramInt <= this.buf.length) {
      return;
    }
    byte[] arrayOfByte = this.zzap.zzb(this.count + paramInt << 1);
    System.arraycopy(this.buf, 0, arrayOfByte, 0, this.count);
    this.zzap.zza(this.buf);
    this.buf = arrayOfByte;
  }
  
  public final void close()
    throws IOException
  {
    this.zzap.zza(this.buf);
    this.buf = null;
    super.close();
  }
  
  public final void finalize()
  {
    this.zzap.zza(this.buf);
  }
  
  public final void write(int paramInt)
  {
    try
    {
      zzc(1);
      super.write(paramInt);
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public final void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    try
    {
      zzc(paramInt2);
      super.write(paramArrayOfByte, paramInt1, paramInt2);
      return;
    }
    finally
    {
      paramArrayOfByte = finally;
      throw paramArrayOfByte;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */