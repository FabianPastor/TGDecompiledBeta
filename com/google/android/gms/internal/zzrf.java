package com.google.android.gms.internal;

import android.app.Activity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.internal.zzb;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.concurrent.CancellationException;

public class zzrf
  extends zzqd
{
  private TaskCompletionSource<Void> wh = new TaskCompletionSource();
  
  private zzrf(zzrb paramzzrb)
  {
    super(paramzzrb);
    this.yY.zza("GmsAvailabilityHelper", this);
  }
  
  public static zzrf zzu(Activity paramActivity)
  {
    paramActivity = zzs(paramActivity);
    zzrf localzzrf = (zzrf)paramActivity.zza("GmsAvailabilityHelper", zzrf.class);
    if (localzzrf != null)
    {
      if (localzzrf.wh.getTask().isComplete()) {
        localzzrf.wh = new TaskCompletionSource();
      }
      return localzzrf;
    }
    return new zzrf(paramActivity);
  }
  
  public Task<Void> getTask()
  {
    return this.wh.getTask();
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    this.wh.setException(new CancellationException("Host activity was destroyed before Google Play services could be made available."));
  }
  
  protected void zza(ConnectionResult paramConnectionResult, int paramInt)
  {
    this.wh.setException(zzb.zzl(paramConnectionResult));
  }
  
  protected void zzaqk()
  {
    int i = this.vP.isGooglePlayServicesAvailable(this.yY.zzasq());
    if (i == 0)
    {
      this.wh.setResult(null);
      return;
    }
    zzk(new ConnectionResult(i, null));
  }
  
  public void zzk(ConnectionResult paramConnectionResult)
  {
    zzb(paramConnectionResult, 0);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzrf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */