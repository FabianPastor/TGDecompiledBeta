package com.google.android.gms.internal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class zzana
{
  private static final AtomicReference<zzana> aST = new AtomicReference();
  
  zzana(Context paramContext) {}
  
  @Nullable
  public static zzana N()
  {
    return (zzana)aST.get();
  }
  
  public static zzana zzew(Context paramContext)
  {
    aST.compareAndSet(null, new zzana(paramContext));
    return (zzana)aST.get();
  }
  
  public Set<String> O()
  {
    return Collections.emptySet();
  }
  
  public void zzg(@NonNull FirebaseApp paramFirebaseApp) {}
  
  public FirebaseOptions zzua(@NonNull String paramString)
  {
    return null;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzana.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */