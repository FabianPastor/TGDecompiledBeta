package com.google.android.gms.internal;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiActivity;

public abstract class zzqd
  extends zzra
  implements DialogInterface.OnCancelListener
{
  protected boolean mStarted;
  protected final GoogleApiAvailability vP;
  private int wA = -1;
  private final Handler wB = new Handler(Looper.getMainLooper());
  protected boolean wy;
  private ConnectionResult wz;
  
  protected zzqd(zzrb paramzzrb)
  {
    this(paramzzrb, GoogleApiAvailability.getInstance());
  }
  
  zzqd(zzrb paramzzrb, GoogleApiAvailability paramGoogleApiAvailability)
  {
    super(paramzzrb);
    this.vP = paramGoogleApiAvailability;
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    int i = 1;
    int j = 1;
    switch (paramInt1)
    {
    default: 
      paramInt1 = 0;
      if (paramInt1 != 0)
      {
        zzaqo();
        return;
      }
      break;
    case 2: 
      label30:
      j = this.vP.isGooglePlayServicesAvailable(getActivity());
      if (j != 0) {}
      break;
    }
    for (paramInt2 = i;; paramInt2 = 0)
    {
      paramInt1 = paramInt2;
      if (this.wz.getErrorCode() != 18) {
        break label30;
      }
      paramInt1 = paramInt2;
      if (j != 18) {
        break label30;
      }
      return;
      paramInt1 = j;
      if (paramInt2 == -1) {
        break label30;
      }
      if (paramInt2 != 0) {
        break;
      }
      if (paramIntent != null) {}
      for (paramInt1 = paramIntent.getIntExtra("<<ResolutionFailureErrorDetail>>", 13);; paramInt1 = 13)
      {
        this.wz = new ConnectionResult(paramInt1, null);
        break;
        zza(this.wz, this.wA);
        return;
      }
    }
  }
  
  public void onCancel(DialogInterface paramDialogInterface)
  {
    zza(new ConnectionResult(13, null), this.wA);
    zzaqo();
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (paramBundle != null)
    {
      this.wy = paramBundle.getBoolean("resolving_error", false);
      if (this.wy)
      {
        this.wA = paramBundle.getInt("failed_client_id", -1);
        this.wz = new ConnectionResult(paramBundle.getInt("failed_status"), (PendingIntent)paramBundle.getParcelable("failed_resolution"));
      }
    }
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putBoolean("resolving_error", this.wy);
    if (this.wy)
    {
      paramBundle.putInt("failed_client_id", this.wA);
      paramBundle.putInt("failed_status", this.wz.getErrorCode());
      paramBundle.putParcelable("failed_resolution", this.wz.getResolution());
    }
  }
  
  public void onStart()
  {
    super.onStart();
    this.mStarted = true;
  }
  
  public void onStop()
  {
    super.onStop();
    this.mStarted = false;
  }
  
  protected abstract void zza(ConnectionResult paramConnectionResult, int paramInt);
  
  protected abstract void zzaqk();
  
  protected void zzaqo()
  {
    this.wA = -1;
    this.wy = false;
    this.wz = null;
    zzaqk();
  }
  
  public void zzb(ConnectionResult paramConnectionResult, int paramInt)
  {
    if (!this.wy)
    {
      this.wy = true;
      this.wA = paramInt;
      this.wz = paramConnectionResult;
      this.wB.post(new zza(null));
    }
  }
  
  private class zza
    implements Runnable
  {
    private zza() {}
    
    @MainThread
    public void run()
    {
      if (!zzqd.this.mStarted) {
        return;
      }
      if (zzqd.zza(zzqd.this).hasResolution())
      {
        zzqd.this.yY.startActivityForResult(GoogleApiActivity.zzb(zzqd.this.getActivity(), zzqd.zza(zzqd.this).getResolution(), zzqd.zzb(zzqd.this), false), 1);
        return;
      }
      if (zzqd.this.vP.isUserResolvableError(zzqd.zza(zzqd.this).getErrorCode()))
      {
        zzqd.this.vP.zza(zzqd.this.getActivity(), zzqd.this.yY, zzqd.zza(zzqd.this).getErrorCode(), 2, zzqd.this);
        return;
      }
      if (zzqd.zza(zzqd.this).getErrorCode() == 18)
      {
        final Dialog localDialog = zzqd.this.vP.zza(zzqd.this.getActivity(), zzqd.this);
        zzqd.this.vP.zza(zzqd.this.getActivity().getApplicationContext(), new zzqv.zza()
        {
          public void zzaqp()
          {
            zzqd.this.zzaqo();
            if (localDialog.isShowing()) {
              localDialog.dismiss();
            }
          }
        });
        return;
      }
      zzqd.this.zza(zzqd.zza(zzqd.this), zzqd.zzb(zzqd.this));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzqd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */