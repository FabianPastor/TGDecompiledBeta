package com.google.android.gms.wearable;

import java.io.IOException;

public class ChannelIOException
  extends IOException
{
  private final int zzg;
  private final int zzh;
  
  public ChannelIOException(String paramString, int paramInt1, int paramInt2)
  {
    super(paramString);
    this.zzg = paramInt1;
    this.zzh = paramInt2;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/ChannelIOException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */