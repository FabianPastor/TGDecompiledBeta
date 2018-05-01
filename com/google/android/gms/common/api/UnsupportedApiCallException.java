package com.google.android.gms.common.api;

import com.google.android.gms.common.Feature;

public final class UnsupportedApiCallException
  extends UnsupportedOperationException
{
  private final Feature zzdr;
  
  public UnsupportedApiCallException(Feature paramFeature)
  {
    this.zzdr = paramFeature;
  }
  
  public final String getMessage()
  {
    String str = String.valueOf(this.zzdr);
    return String.valueOf(str).length() + 8 + "Missing " + str;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/UnsupportedApiCallException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */