package com.google.firebase.internal;

import android.content.Context;
import com.google.firebase.FirebaseApp;
import java.util.concurrent.atomic.AtomicReference;

public final class zzb
{
  private static final AtomicReference<zzb> zzq = new AtomicReference();
  
  private zzb(Context paramContext) {}
  
  public static void zzb(FirebaseApp paramFirebaseApp) {}
  
  public static zzb zze(Context paramContext)
  {
    zzq.compareAndSet(null, new zzb(paramContext));
    return (zzb)zzq.get();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/internal/zzb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */