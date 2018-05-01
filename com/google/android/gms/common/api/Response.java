package com.google.android.gms.common.api;

import android.support.annotation.NonNull;

public class Response<T extends Result>
{
  private T zzaBj;
  
  public Response() {}
  
  protected Response(@NonNull T paramT)
  {
    this.zzaBj = paramT;
  }
  
  @NonNull
  protected T getResult()
  {
    return this.zzaBj;
  }
  
  public void setResult(@NonNull T paramT)
  {
    this.zzaBj = paramT;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/Response.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */