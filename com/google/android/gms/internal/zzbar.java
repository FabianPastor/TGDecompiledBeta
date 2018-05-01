package com.google.android.gms.internal;

import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.Map;

public final class zzbar
  extends zzban
{
  private zzbdy<?> zzaBy;
  
  public zzbar(zzbdy<?> paramzzbdy, TaskCompletionSource<Void> paramTaskCompletionSource)
  {
    super(4, paramTaskCompletionSource);
    this.zzaBy = paramzzbdy;
  }
  
  public final void zzb(zzbdd<?> paramzzbdd)
    throws RemoteException
  {
    zzbef localzzbef = (zzbef)paramzzbdd.zzqs().remove(this.zzaBy);
    if (localzzbef != null)
    {
      localzzbef.zzaBv.zzc(paramzzbdd.zzpJ(), this.zzalE);
      localzzbef.zzaBu.zzqH();
      return;
    }
    Log.wtf("UnregisterListenerTask", "Received call to unregister a listener without a matching registration call.", new Exception());
    this.zzalE.trySetException(new ApiException(Status.zzaBo));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */