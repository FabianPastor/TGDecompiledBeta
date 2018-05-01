package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.wearable.ChannelIOException;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nullable;

public final class zzbj
  extends InputStream
{
  private final InputStream zzcv;
  @Nullable
  private volatile zzav zzcw;
  
  public zzbj(InputStream paramInputStream)
  {
    this.zzcv = ((InputStream)Preconditions.checkNotNull(paramInputStream));
  }
  
  private final int zza(int paramInt)
    throws ChannelIOException
  {
    if (paramInt == -1)
    {
      zzav localzzav = this.zzcw;
      if (localzzav != null) {
        throw new ChannelIOException("Channel closed unexpectedly before stream was finished", localzzav.zzg, localzzav.zzcj);
      }
    }
    return paramInt;
  }
  
  public final int available()
    throws IOException
  {
    return this.zzcv.available();
  }
  
  public final void close()
    throws IOException
  {
    this.zzcv.close();
  }
  
  public final void mark(int paramInt)
  {
    this.zzcv.mark(paramInt);
  }
  
  public final boolean markSupported()
  {
    return this.zzcv.markSupported();
  }
  
  public final int read()
    throws IOException
  {
    return zza(this.zzcv.read());
  }
  
  public final int read(byte[] paramArrayOfByte)
    throws IOException
  {
    return zza(this.zzcv.read(paramArrayOfByte));
  }
  
  public final int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    return zza(this.zzcv.read(paramArrayOfByte, paramInt1, paramInt2));
  }
  
  public final void reset()
    throws IOException
  {
    this.zzcv.reset();
  }
  
  public final long skip(long paramLong)
    throws IOException
  {
    return this.zzcv.skip(paramLong);
  }
  
  final void zza(zzav paramzzav)
  {
    this.zzcw = ((zzav)Preconditions.checkNotNull(paramzzav));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzbj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */