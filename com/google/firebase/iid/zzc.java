package com.google.firebase.iid;

import android.support.annotation.Nullable;

public class zzc
{
  private final FirebaseInstanceId bhn;
  
  private zzc(FirebaseInstanceId paramFirebaseInstanceId)
  {
    this.bhn = paramFirebaseInstanceId;
  }
  
  public static zzc A()
  {
    return new zzc(FirebaseInstanceId.getInstance());
  }
  
  public String getId()
  {
    return this.bhn.getId();
  }
  
  @Nullable
  public String getToken()
  {
    return this.bhn.getToken();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/iid/zzc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */