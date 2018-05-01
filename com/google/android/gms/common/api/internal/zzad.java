package com.google.android.gms.common.api.internal;

import android.support.v4.util.ArraySet;
import com.google.android.gms.common.ConnectionResult;

public class zzad
  extends zzk
{
  private GoogleApiManager zzcq;
  private final ArraySet<zzh<?>> zzhb;
  
  private final void zzan()
  {
    if (!this.zzhb.isEmpty()) {
      this.zzcq.zza(this);
    }
  }
  
  public final void onResume()
  {
    super.onResume();
    zzan();
  }
  
  public final void onStart()
  {
    super.onStart();
    zzan();
  }
  
  public void onStop()
  {
    super.onStop();
    this.zzcq.zzb(this);
  }
  
  protected final void zza(ConnectionResult paramConnectionResult, int paramInt)
  {
    this.zzcq.zza(paramConnectionResult, paramInt);
  }
  
  final ArraySet<zzh<?>> zzam()
  {
    return this.zzhb;
  }
  
  protected final void zzr()
  {
    this.zzcq.zzr();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzad.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */