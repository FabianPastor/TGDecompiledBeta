package com.google.android.gms.internal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public final class zt
{
  private static final AtomicReference<zt> zzbVg = new AtomicReference();
  
  private zt(Context paramContext) {}
  
  @Nullable
  public static zt zzJW()
  {
    return (zt)zzbVg.get();
  }
  
  public static Set<String> zzJX()
  {
    return Collections.emptySet();
  }
  
  public static zt zzbL(Context paramContext)
  {
    zzbVg.compareAndSet(null, new zt(paramContext));
    return (zt)zzbVg.get();
  }
  
  public static void zze(@NonNull FirebaseApp paramFirebaseApp) {}
  
  public static FirebaseOptions zzhq(@NonNull String paramString)
  {
    return null;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */