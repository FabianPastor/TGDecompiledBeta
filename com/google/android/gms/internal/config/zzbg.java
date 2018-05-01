package com.google.android.gms.internal.config;

import java.io.IOException;

public final class zzbg
  extends IOException
{
  public zzbg(String paramString)
  {
    super(paramString);
  }
  
  static zzbg zzag()
  {
    return new zzbg("While parsing a protocol message, the input ended unexpectedly in the middle of a field.  This could mean either than the input has been truncated or that an embedded message misreported its own length.");
  }
  
  static zzbg zzah()
  {
    return new zzbg("CodedInputStream encountered an embedded string or message which claimed to have negative size.");
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/config/zzbg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */