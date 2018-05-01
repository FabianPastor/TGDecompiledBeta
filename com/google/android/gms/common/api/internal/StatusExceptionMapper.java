package com.google.android.gms.common.api.internal;

import com.google.android.gms.common.api.Status;

public abstract interface StatusExceptionMapper
{
  public abstract Exception getException(Status paramStatus);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/StatusExceptionMapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */