package com.google.android.gms.internal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class zzbqk
{
  private static final AtomicReference<zzbqk> zzbUK = new AtomicReference();
  
  zzbqk(Context paramContext) {}
  
  @Nullable
  public static zzbqk zzaap()
  {
    return (zzbqk)zzbUK.get();
  }
  
  public static zzbqk zzbZ(Context paramContext)
  {
    zzbUK.compareAndSet(null, new zzbqk(paramContext));
    return (zzbqk)zzbUK.get();
  }
  
  public Set<String> zzaaq()
  {
    return Collections.emptySet();
  }
  
  public void zzg(@NonNull FirebaseApp paramFirebaseApp) {}
  
  public FirebaseOptions zzjD(@NonNull String paramString)
  {
    return null;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbqk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */