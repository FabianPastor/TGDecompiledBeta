package com.google.android.gms.measurement.internal;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zze;

public class zzai
  extends zzaa
{
  private final zzf ass;
  private boolean cc;
  private final AlarmManager cd = (AlarmManager)getContext().getSystemService("alarm");
  
  protected zzai(zzx paramzzx)
  {
    super(paramzzx);
    this.ass = new zzf(paramzzx)
    {
      public void run()
      {
        zzai.zza(zzai.this);
      }
    };
  }
  
  private PendingIntent zzaee()
  {
    Intent localIntent = new Intent();
    Context localContext = getContext();
    if ((zzbvi().zzact()) && (!this.anq.zzbxg())) {}
    for (Object localObject = "com.google.android.gms.measurement.PackageMeasurementReceiver";; localObject = "com.google.android.gms.measurement.AppMeasurementReceiver")
    {
      localObject = localIntent.setClassName(localContext, (String)localObject);
      ((Intent)localObject).setAction("com.google.android.gms.measurement.UPLOAD");
      return PendingIntent.getBroadcast(getContext(), 0, (Intent)localObject, 0);
    }
  }
  
  private void zzbyl()
  {
    Intent localIntent = new Intent();
    Context localContext = getContext();
    if ((zzbvi().zzact()) && (!this.anq.zzbxg())) {}
    for (Object localObject = "com.google.android.gms.measurement.PackageMeasurementReceiver";; localObject = "com.google.android.gms.measurement.AppMeasurementReceiver")
    {
      localObject = localIntent.setClassName(localContext, (String)localObject);
      ((Intent)localObject).setAction("com.google.android.gms.measurement.UPLOAD");
      getContext().sendBroadcast((Intent)localObject);
      return;
    }
  }
  
  public void cancel()
  {
    zzaax();
    this.cc = false;
    this.cd.cancel(zzaee());
    this.ass.cancel();
  }
  
  public void zzx(long paramLong)
  {
    boolean bool2 = false;
    zzaax();
    if ((zzbvi().zzact()) || (zzu.zzh(getContext(), false))) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      zzac.zza(bool1, "Receiver not registered/enabled");
      if (!zzbvi().zzact())
      {
        bool1 = bool2;
        if (!zzae.zzi(getContext(), false)) {}
      }
      else
      {
        bool1 = true;
      }
      zzac.zza(bool1, "Service not registered/enabled");
      cancel();
      long l = zzaan().elapsedRealtime();
      this.cc = true;
      if ((paramLong < zzbvi().zzbup()) && (!this.ass.zzfl())) {
        this.ass.zzx(paramLong);
      }
      this.cd.setInexactRepeating(2, l + paramLong, Math.max(zzbvi().zzbuq(), paramLong), zzaee());
      return;
    }
  }
  
  protected void zzym()
  {
    this.cd.cancel(zzaee());
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzai.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */