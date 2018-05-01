package com.google.android.gms.internal;

import java.io.IOException;

public final class adf
  extends IOException
{
  public adf(String paramString)
  {
    super(paramString);
  }
  
  static adf zzLO()
  {
    return new adf("While parsing a protocol message, the input ended unexpectedly in the middle of a field.  This could mean either than the input has been truncated or that an embedded message misreported its own length.");
  }
  
  static adf zzLP()
  {
    return new adf("CodedInputStream encountered an embedded string or message which claimed to have negative size.");
  }
  
  static adf zzLQ()
  {
    return new adf("CodedInputStream encountered a malformed varint.");
  }
  
  static adf zzLR()
  {
    return new adf("Protocol message had too many levels of nesting.  May be malicious.  Use CodedInputStream.setRecursionLimit() to increase the depth limit.");
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/adf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */