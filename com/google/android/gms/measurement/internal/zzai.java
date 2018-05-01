package com.google.android.gms.measurement.internal;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.google.android.gms.common.util.zze;

public class zzai
  extends zzaa
{
  private final zzf avR;
  private boolean ej;
  private final AlarmManager ek = (AlarmManager)getContext().getSystemService("alarm");
  
  protected zzai(zzx paramzzx)
  {
    super(paramzzx);
    this.avR = new zzf(paramzzx)
    {
      public void run()
      {
        zzai.zza(zzai.this);
      }
    };
  }
  
  private PendingIntent zzafo()
  {
    Intent localIntent = new Intent();
    Context localContext = getContext();
    zzbwd().zzayi();
    localIntent = localIntent.setClassName(localContext, "com.google.android.gms.measurement.AppMeasurementReceiver");
    localIntent.setAction("com.google.android.gms.measurement.UPLOAD");
    return PendingIntent.getBroadcast(getContext(), 0, localIntent, 0);
  }
  
  private void zzbzg()
  {
    Intent localIntent = new Intent();
    Context localContext = getContext();
    zzbwd().zzayi();
    localIntent = localIntent.setClassName(localContext, "com.google.android.gms.measurement.AppMeasurementReceiver");
    localIntent.setAction("com.google.android.gms.measurement.UPLOAD");
    getContext().sendBroadcast(localIntent);
  }
  
  public void cancel()
  {
    zzacj();
    this.ej = false;
    this.ek.cancel(zzafo());
    this.avR.cancel();
  }
  
  public void zzx(long paramLong)
  {
    zzacj();
    if ((!zzbwd().zzayi()) && (!zzu.zzh(getContext(), false))) {
      zzbwb().zzbxd().log("Receiver not registered/enabled");
    }
    if ((!zzbwd().zzayi()) && (!zzaf.zzi(getContext(), false))) {
      zzbwb().zzbxd().log("Service not registered/enabled");
    }
    cancel();
    long l = zzabz().elapsedRealtime();
    this.ej = true;
    if ((paramLong < zzbwd().zzbvi()) && (!this.avR.zzfy())) {
      this.avR.zzx(paramLong);
    }
    this.ek.setInexactRepeating(2, l + paramLong, Math.max(zzbwd().zzbvj(), paramLong), zzafo());
  }
  
  protected void zzzy()
  {
    this.ek.cancel(zzafo());
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzai.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */