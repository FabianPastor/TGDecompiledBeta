package com.google.android.gms.internal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class zzbth
{
  private static final AtomicReference<zzbth> zzbWP = new AtomicReference();
  
  zzbth(Context paramContext) {}
  
  @Nullable
  public static zzbth zzaca()
  {
    return (zzbth)zzbWP.get();
  }
  
  public static zzbth zzcx(Context paramContext)
  {
    zzbWP.compareAndSet(null, new zzbth(paramContext));
    return (zzbth)zzbWP.get();
  }
  
  public Set<String> zzacb()
  {
    return Collections.emptySet();
  }
  
  public void zzg(@NonNull FirebaseApp paramFirebaseApp) {}
  
  public FirebaseOptions zzjC(@NonNull String paramString)
  {
    return null;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbth.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */