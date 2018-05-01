package com.google.android.gms.internal;

import java.io.IOException;

public final class zzfjr
  extends IOException
{
  public zzfjr(String paramString)
  {
    super(paramString);
  }
  
  static zzfjr zzdai()
  {
    return new zzfjr("While parsing a protocol message, the input ended unexpectedly in the middle of a field.  This could mean either than the input has been truncated or that an embedded message misreported its own length.");
  }
  
  static zzfjr zzdaj()
  {
    return new zzfjr("CodedInputStream encountered an embedded string or message which claimed to have negative size.");
  }
  
  static zzfjr zzdak()
  {
    return new zzfjr("CodedInputStream encountered a malformed varint.");
  }
  
  static zzfjr zzdal()
  {
    return new zzfjr("Protocol message had too many levels of nesting.  May be malicious.  Use CodedInputStream.setRecursionLimit() to increase the depth limit.");
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzfjr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */