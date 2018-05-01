package com.google.android.gms.internal;

import android.app.Activity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzb;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.concurrent.CancellationException;

public class zzbeb
  extends zzbba
{
  private TaskCompletionSource<Void> zzalE = new TaskCompletionSource();
  
  private zzbeb(zzbdt paramzzbdt)
  {
    super(paramzzbdt);
    this.zzaEG.zza("GmsAvailabilityHelper", this);
  }
  
  public static zzbeb zzp(Activity paramActivity)
  {
    paramActivity = zzn(paramActivity);
    zzbeb localzzbeb = (zzbeb)paramActivity.zza("GmsAvailabilityHelper", zzbeb.class);
    if (localzzbeb != null)
    {
      if (localzzbeb.zzalE.getTask().isComplete()) {
        localzzbeb.zzalE = new TaskCompletionSource();
      }
      return localzzbeb;
    }
    return new zzbeb(paramActivity);
  }
  
  public final Task<Void> getTask()
  {
    return this.zzalE.getTask();
  }
  
  public final void onDestroy()
  {
    super.onDestroy();
    this.zzalE.setException(new CancellationException("Host activity was destroyed before Google Play services could be made available."));
  }
  
  protected final void zza(ConnectionResult paramConnectionResult, int paramInt)
  {
    this.zzalE.setException(zzb.zzx(new Status(paramConnectionResult.getErrorCode(), paramConnectionResult.getErrorMessage(), paramConnectionResult.getResolution())));
  }
  
  protected final void zzps()
  {
    int i = this.zzaBd.isGooglePlayServicesAvailable(this.zzaEG.zzqF());
    if (i == 0) {
      this.zzalE.setResult(null);
    }
    while (this.zzalE.getTask().isComplete()) {
      return;
    }
    zzb(new ConnectionResult(i, null), 0);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbeb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */