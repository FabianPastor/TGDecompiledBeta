package com.google.android.gms.internal;

import android.app.Activity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.util.zza;

public class zzqw
  extends zzqp
{
  private zzrh xy;
  private final zza<zzql<?>> zx = new zza();
  
  private zzqw(zzrp paramzzrp)
  {
    super(paramzzrp);
    this.Bf.zza("ConnectionlessLifecycleHelper", this);
  }
  
  public static void zza(Activity paramActivity, zzrh paramzzrh, zzql<?> paramzzql)
  {
    zzrp localzzrp = zzs(paramActivity);
    zzqw localzzqw = (zzqw)localzzrp.zza("ConnectionlessLifecycleHelper", zzqw.class);
    paramActivity = localzzqw;
    if (localzzqw == null) {
      paramActivity = new zzqw(localzzrp);
    }
    paramActivity.xy = paramzzrh;
    paramActivity.zza(paramzzql);
    paramzzrh.zza(paramActivity);
  }
  
  private void zza(zzql<?> paramzzql)
  {
    zzaa.zzb(paramzzql, "ApiKey cannot be null");
    this.zx.add(paramzzql);
  }
  
  public void onStart()
  {
    super.onStart();
    if (!this.zx.isEmpty()) {
      this.xy.zza(this);
    }
  }
  
  public void onStop()
  {
    super.onStop();
    this.xy.zzb(this);
  }
  
  protected void zza(ConnectionResult paramConnectionResult, int paramInt)
  {
    this.xy.zza(paramConnectionResult, paramInt);
  }
  
  protected void zzarm()
  {
    this.xy.zzarm();
  }
  
  zza<zzql<?>> zzasl()
  {
    return this.zx;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzqw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */