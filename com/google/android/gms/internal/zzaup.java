package com.google.android.gms.internal;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.google.android.gms.common.util.zze;

public class zzaup
  extends zzauh
{
  private boolean zzafh;
  private final AlarmManager zzafi = (AlarmManager)getContext().getSystemService("alarm");
  private final zzatk zzbwd;
  
  protected zzaup(zzaue paramzzaue)
  {
    super(paramzzaue);
    this.zzbwd = new zzatk(paramzzaue)
    {
      public void run()
      {
        zzaup.zza(zzaup.this);
      }
    };
  }
  
  private void zzNj()
  {
    Intent localIntent = new Intent();
    Context localContext = getContext();
    zzKn().zzLh();
    localIntent = localIntent.setClassName(localContext, "com.google.android.gms.measurement.AppMeasurementReceiver");
    localIntent.setAction("com.google.android.gms.measurement.UPLOAD");
    getContext().sendBroadcast(localIntent);
  }
  
  private PendingIntent zzpE()
  {
    Intent localIntent = new Intent();
    Context localContext = getContext();
    zzKn().zzLh();
    localIntent = localIntent.setClassName(localContext, "com.google.android.gms.measurement.AppMeasurementReceiver");
    localIntent.setAction("com.google.android.gms.measurement.UPLOAD");
    return PendingIntent.getBroadcast(getContext(), 0, localIntent, 0);
  }
  
  public void cancel()
  {
    zzob();
    this.zzafh = false;
    this.zzafi.cancel(zzpE());
    this.zzbwd.cancel();
  }
  
  protected void zzmS()
  {
    this.zzafi.cancel(zzpE());
  }
  
  public void zzy(long paramLong)
  {
    zzob();
    zzKn().zzLh();
    if (!zzaub.zzi(getContext(), false)) {
      zzKl().zzMe().log("Receiver not registered/enabled");
    }
    zzKn().zzLh();
    if (!zzaum.zzj(getContext(), false)) {
      zzKl().zzMe().log("Service not registered/enabled");
    }
    cancel();
    long l = zznR().elapsedRealtime();
    this.zzafh = true;
    if ((paramLong < zzKn().zzLy()) && (!this.zzbwd.zzcy())) {
      this.zzbwd.zzy(paramLong);
    }
    this.zzafi.setInexactRepeating(2, l + paramLong, Math.max(zzKn().zzLz(), paramLong), zzpE());
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */