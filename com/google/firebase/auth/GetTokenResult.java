package com.google.firebase.auth;

import android.support.annotation.Nullable;

public class GetTokenResult
{
  private String fG;
  
  public GetTokenResult(String paramString)
  {
    this.fG = paramString;
  }
  
  @Nullable
  public String getToken()
  {
    return this.fG;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/auth/GetTokenResult.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */