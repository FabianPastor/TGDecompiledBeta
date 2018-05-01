package com.google.android.gms.wearable.internal;

import android.util.Log;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.wearable.ChannelIOException;
import java.io.IOException;
import java.io.OutputStream;
import javax.annotation.Nullable;

public final class zzbl
  extends OutputStream
{
  @Nullable
  private volatile zzav zzcw;
  private final OutputStream zzcy;
  
  public zzbl(OutputStream paramOutputStream)
  {
    this.zzcy = ((OutputStream)Preconditions.checkNotNull(paramOutputStream));
  }
  
  private final IOException zza(IOException paramIOException)
  {
    zzav localzzav = this.zzcw;
    Object localObject = paramIOException;
    if (localzzav != null)
    {
      if (Log.isLoggable("ChannelOutputStream", 2)) {
        Log.v("ChannelOutputStream", "Caught IOException, but channel has been closed. Translating to ChannelIOException.", paramIOException);
      }
      localObject = new ChannelIOException("Channel closed unexpectedly before stream was finished", localzzav.zzg, localzzav.zzcj);
    }
    return (IOException)localObject;
  }
  
  public final void close()
    throws IOException
  {
    try
    {
      this.zzcy.close();
      return;
    }
    catch (IOException localIOException)
    {
      throw zza(localIOException);
    }
  }
  
  public final void flush()
    throws IOException
  {
    try
    {
      this.zzcy.flush();
      return;
    }
    catch (IOException localIOException)
    {
      throw zza(localIOException);
    }
  }
  
  public final void write(int paramInt)
    throws IOException
  {
    try
    {
      this.zzcy.write(paramInt);
      return;
    }
    catch (IOException localIOException)
    {
      throw zza(localIOException);
    }
  }
  
  public final void write(byte[] paramArrayOfByte)
    throws IOException
  {
    try
    {
      this.zzcy.write(paramArrayOfByte);
      return;
    }
    catch (IOException paramArrayOfByte)
    {
      throw zza(paramArrayOfByte);
    }
  }
  
  public final void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    try
    {
      this.zzcy.write(paramArrayOfByte, paramInt1, paramInt2);
      return;
    }
    catch (IOException paramArrayOfByte)
    {
      throw zza(paramArrayOfByte);
    }
  }
  
  final void zzc(zzav paramzzav)
  {
    this.zzcw = paramzzav;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzbl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */