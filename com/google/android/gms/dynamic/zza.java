package com.google.android.gms.dynamic;

import java.util.Iterator;
import java.util.LinkedList;

final class zza
  implements OnDelegateCreatedListener<T>
{
  zza(DeferredLifecycleHelper paramDeferredLifecycleHelper) {}
  
  public final void onDelegateCreated(T paramT)
  {
    DeferredLifecycleHelper.zza(this.zzabg, paramT);
    paramT = DeferredLifecycleHelper.zza(this.zzabg).iterator();
    while (paramT.hasNext()) {
      ((DeferredLifecycleHelper.zza)paramT.next()).zza(DeferredLifecycleHelper.zzb(this.zzabg));
    }
    DeferredLifecycleHelper.zza(this.zzabg).clear();
    DeferredLifecycleHelper.zza(this.zzabg, null);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/dynamic/zza.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */