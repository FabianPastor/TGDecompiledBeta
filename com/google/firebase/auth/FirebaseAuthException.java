package com.google.firebase.auth;

import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzac;
import com.google.firebase.FirebaseException;

public class FirebaseAuthException
  extends FirebaseException
{
  private final String aTP;
  
  public FirebaseAuthException(@NonNull String paramString1, @NonNull String paramString2)
  {
    super(paramString2);
    this.aTP = zzac.zzhz(paramString1);
  }
  
  @NonNull
  public String getErrorCode()
  {
    return this.aTP;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/auth/FirebaseAuthException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */