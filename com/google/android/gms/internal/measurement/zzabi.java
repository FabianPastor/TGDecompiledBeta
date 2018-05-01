package com.google.android.gms.internal.measurement;

import java.io.IOException;

public final class zzabi
  extends IOException
{
  public zzabi(String paramString)
  {
    super(paramString);
  }
  
  static zzabi zzwb()
  {
    return new zzabi("While parsing a protocol message, the input ended unexpectedly in the middle of a field.  This could mean either than the input has been truncated or that an embedded message misreported its own length.");
  }
  
  static zzabi zzwc()
  {
    return new zzabi("CodedInputStream encountered an embedded string or message which claimed to have negative size.");
  }
  
  static zzabi zzwd()
  {
    return new zzabi("CodedInputStream encountered a malformed varint.");
  }
  
  static zzabi zzwe()
  {
    return new zzabi("Protocol message had too many levels of nesting.  May be malicious.  Use CodedInputStream.setRecursionLimit() to increase the depth limit.");
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzabi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */