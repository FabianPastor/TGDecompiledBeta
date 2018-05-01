package com.google.android.gms.internal;

import android.app.Activity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zza;

public class zzaam
  extends zzaae
{
  private final zza<zzzz<?>> zzaBh = new zza();
  private zzaax zzayX;
  
  private zzaam(zzabf paramzzabf)
  {
    super(paramzzabf);
    this.zzaCR.zza("ConnectionlessLifecycleHelper", this);
  }
  
  public static void zza(Activity paramActivity, zzaax paramzzaax, zzzz<?> paramzzzz)
  {
    zzabf localzzabf = zzs(paramActivity);
    zzaam localzzaam = (zzaam)localzzabf.zza("ConnectionlessLifecycleHelper", zzaam.class);
    paramActivity = localzzaam;
    if (localzzaam == null) {
      paramActivity = new zzaam(localzzabf);
    }
    paramActivity.zzayX = paramzzaax;
    paramActivity.zza(paramzzzz);
    paramzzaax.zza(paramActivity);
  }
  
  private void zza(zzzz<?> paramzzzz)
  {
    zzac.zzb(paramzzzz, "ApiKey cannot be null");
    this.zzaBh.add(paramzzzz);
  }
  
  public void onStart()
  {
    super.onStart();
    if (!this.zzaBh.isEmpty()) {
      this.zzayX.zza(this);
    }
  }
  
  public void onStop()
  {
    super.onStop();
    this.zzayX.zzb(this);
  }
  
  protected void zza(ConnectionResult paramConnectionResult, int paramInt)
  {
    this.zzayX.zza(paramConnectionResult, paramInt);
  }
  
  protected void zzvx()
  {
    this.zzayX.zzvx();
  }
  
  zza<zzzz<?>> zzwb()
  {
    return this.zzaBh;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaam.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */