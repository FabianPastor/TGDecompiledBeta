package com.google.android.gms.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.Map;

public final class zzbap
  extends zzban
{
  private zzbee<Api.zzb, ?> zzaBu;
  private zzbey<Api.zzb, ?> zzaBv;
  
  public zzbap(zzbef paramzzbef, TaskCompletionSource<Void> paramTaskCompletionSource)
  {
    super(3, paramTaskCompletionSource);
    this.zzaBu = paramzzbef.zzaBu;
    this.zzaBv = paramzzbef.zzaBv;
  }
  
  public final void zzb(zzbdd<?> paramzzbdd)
    throws RemoteException
  {
    this.zzaBu.zzb(paramzzbdd.zzpJ(), this.zzalE);
    if (this.zzaBu.zzqG() != null) {
      paramzzbdd.zzqs().put(this.zzaBu.zzqG(), new zzbef(this.zzaBu, this.zzaBv));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */