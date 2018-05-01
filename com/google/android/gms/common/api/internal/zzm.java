package com.google.android.gms.common.api.internal;

import android.app.Activity;
import android.app.Dialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.google.android.gms.common.api.GoogleApiActivity;

final class zzm
  implements Runnable
{
  private final zzl zzev;
  
  zzm(zzk paramzzk, zzl paramzzl)
  {
    this.zzev = paramzzl;
  }
  
  public final void run()
  {
    if (!this.zzew.mStarted) {}
    for (;;)
    {
      return;
      Object localObject = this.zzev.getConnectionResult();
      if (((ConnectionResult)localObject).hasResolution())
      {
        this.zzew.mLifecycleFragment.startActivityForResult(GoogleApiActivity.zza(this.zzew.getActivity(), ((ConnectionResult)localObject).getResolution(), this.zzev.zzu(), false), 1);
      }
      else if (this.zzew.zzdg.isUserResolvableError(((ConnectionResult)localObject).getErrorCode()))
      {
        this.zzew.zzdg.showErrorDialogFragment(this.zzew.getActivity(), this.zzew.mLifecycleFragment, ((ConnectionResult)localObject).getErrorCode(), 2, this.zzew);
      }
      else if (((ConnectionResult)localObject).getErrorCode() == 18)
      {
        localObject = this.zzew.zzdg.showUpdatingDialog(this.zzew.getActivity(), this.zzew);
        this.zzew.zzdg.registerCallbackOnUpdate(this.zzew.getActivity().getApplicationContext(), new zzn(this, (Dialog)localObject));
      }
      else
      {
        this.zzew.zza((ConnectionResult)localObject, this.zzev.zzu());
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */