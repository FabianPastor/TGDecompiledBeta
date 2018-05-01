package com.google.android.gms.internal;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.MainThread;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiActivity;

final class zzbbc
  implements Runnable
{
  private final zzbbb zzaBR;
  
  zzbbc(zzbba paramzzbba, zzbbb paramzzbbb)
  {
    this.zzaBR = paramzzbbb;
  }
  
  @MainThread
  public final void run()
  {
    if (!this.zzaBS.mStarted) {
      return;
    }
    Object localObject = this.zzaBR.zzpz();
    if (((ConnectionResult)localObject).hasResolution())
    {
      this.zzaBS.zzaEG.startActivityForResult(GoogleApiActivity.zza(this.zzaBS.getActivity(), ((ConnectionResult)localObject).getResolution(), this.zzaBR.zzpy(), false), 1);
      return;
    }
    if (this.zzaBS.zzaBd.isUserResolvableError(((ConnectionResult)localObject).getErrorCode()))
    {
      this.zzaBS.zzaBd.zza(this.zzaBS.getActivity(), this.zzaBS.zzaEG, ((ConnectionResult)localObject).getErrorCode(), 2, this.zzaBS);
      return;
    }
    if (((ConnectionResult)localObject).getErrorCode() == 18)
    {
      localObject = GoogleApiAvailability.zza(this.zzaBS.getActivity(), this.zzaBS);
      GoogleApiAvailability.zza(this.zzaBS.getActivity().getApplicationContext(), new zzbbd(this, (Dialog)localObject));
      return;
    }
    this.zzaBS.zza((ConnectionResult)localObject, this.zzaBR.zzpy());
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbbc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */