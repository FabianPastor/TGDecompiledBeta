package com.google.android.gms.common.api;

import android.support.annotation.NonNull;

public class zza
  extends Exception
{
  protected final Status fp;
  
  public zza(@NonNull Status paramStatus)
  {
    super(paramStatus.getStatusMessage());
    this.fp = paramStatus;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/zza.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */