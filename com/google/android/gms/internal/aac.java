package com.google.android.gms.internal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public final class aac
{
  private static final AtomicReference<aac> zzbVi = new AtomicReference();
  
  private aac(Context paramContext) {}
  
  @Nullable
  public static aac zzJZ()
  {
    return (aac)zzbVi.get();
  }
  
  public static Set<String> zzKa()
  {
    return Collections.emptySet();
  }
  
  public static aac zzbL(Context paramContext)
  {
    zzbVi.compareAndSet(null, new aac(paramContext));
    return (aac)zzbVi.get();
  }
  
  public static void zze(@NonNull FirebaseApp paramFirebaseApp) {}
  
  public static FirebaseOptions zzhq(@NonNull String paramString)
  {
    return null;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/aac.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */