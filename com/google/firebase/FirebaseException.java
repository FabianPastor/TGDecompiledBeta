package com.google.firebase;

import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzac;

public class FirebaseException
  extends Exception
{
  @Deprecated
  protected FirebaseException() {}
  
  public FirebaseException(@NonNull String paramString)
  {
    super(zzac.zzh(paramString, "Detail message must not be empty"));
  }
  
  public FirebaseException(@NonNull String paramString, Throwable paramThrowable)
  {
    super(zzac.zzh(paramString, "Detail message must not be empty"), paramThrowable);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/FirebaseException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */