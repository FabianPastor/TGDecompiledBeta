package com.google.android.gms.common.api.internal;

import android.os.RemoteException;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.Map;

public final class zzg
  extends zzc<Boolean>
{
  private final ListenerHolder.ListenerKey<?> zzea;
  
  public zzg(ListenerHolder.ListenerKey<?> paramListenerKey, TaskCompletionSource<Boolean> paramTaskCompletionSource)
  {
    super(4, paramTaskCompletionSource);
    this.zzea = paramListenerKey;
  }
  
  public final void zzb(GoogleApiManager.zza<?> paramzza)
    throws RemoteException
  {
    zzbv localzzbv = (zzbv)paramzza.zzbn().remove(this.zzea);
    if (localzzbv != null)
    {
      localzzbv.zzlu.unregisterListener(paramzza.zzae(), this.zzdu);
      localzzbv.zzlt.clearListener();
    }
    for (;;)
    {
      return;
      this.zzdu.trySetResult(Boolean.valueOf(false));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */