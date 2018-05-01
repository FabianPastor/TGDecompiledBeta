package com.google.android.gms.internal.config;

import com.google.firebase.abt.FirebaseABTesting;
import java.util.List;

public final class zzam
  implements Runnable
{
  private final FirebaseABTesting zzam;
  private final List<byte[]> zzas;
  
  public zzam(FirebaseABTesting paramFirebaseABTesting, List<byte[]> paramList)
  {
    this.zzam = paramFirebaseABTesting;
    this.zzas = paramList;
  }
  
  public final void run()
  {
    if (this.zzam != null) {
      this.zzam.replaceAllExperiments(this.zzas);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/config/zzam.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */