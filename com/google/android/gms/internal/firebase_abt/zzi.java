package com.google.android.gms.internal.firebase_abt;

import java.io.IOException;

public final class zzi
  extends IOException
{
  public zzi(String paramString)
  {
    super(paramString);
  }
  
  static zzi zzl()
  {
    return new zzi("While parsing a protocol message, the input ended unexpectedly in the middle of a field.  This could mean either than the input has been truncated or that an embedded message misreported its own length.");
  }
  
  static zzi zzm()
  {
    return new zzi("CodedInputStream encountered an embedded string or message which claimed to have negative size.");
  }
  
  static zzi zzn()
  {
    return new zzi("CodedInputStream encountered a malformed varint.");
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/firebase_abt/zzi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */