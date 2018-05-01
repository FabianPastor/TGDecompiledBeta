package com.google.android.gms.internal;

import android.app.Activity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.util.zza;

public class zzbbw
  extends zzbba
{
  private zzbdb zzaAN;
  private final zza<zzbat<?>> zzaCW = new zza();
  
  private zzbbw(zzbdt paramzzbdt)
  {
    super(paramzzbdt);
    this.zzaEG.zza("ConnectionlessLifecycleHelper", this);
  }
  
  public static void zza(Activity paramActivity, zzbdb paramzzbdb, zzbat<?> paramzzbat)
  {
    zzn(paramActivity);
    zzbdt localzzbdt = zzn(paramActivity);
    zzbbw localzzbbw = (zzbbw)localzzbdt.zza("ConnectionlessLifecycleHelper", zzbbw.class);
    paramActivity = localzzbbw;
    if (localzzbbw == null) {
      paramActivity = new zzbbw(localzzbdt);
    }
    paramActivity.zzaAN = paramzzbdb;
    zzbo.zzb(paramzzbat, "ApiKey cannot be null");
    paramActivity.zzaCW.add(paramzzbat);
    paramzzbdb.zza(paramActivity);
  }
  
  private final void zzpS()
  {
    if (!this.zzaCW.isEmpty()) {
      this.zzaAN.zza(this);
    }
  }
  
  public final void onResume()
  {
    super.onResume();
    zzpS();
  }
  
  public final void onStart()
  {
    super.onStart();
    zzpS();
  }
  
  public final void onStop()
  {
    super.onStop();
    this.zzaAN.zzb(this);
  }
  
  protected final void zza(ConnectionResult paramConnectionResult, int paramInt)
  {
    this.zzaAN.zza(paramConnectionResult, paramInt);
  }
  
  final zza<zzbat<?>> zzpR()
  {
    return this.zzaCW;
  }
  
  protected final void zzps()
  {
    this.zzaAN.zzps();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbbw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */