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

public abstract class zzzw
  extends zzaaw
  implements DialogInterface.OnCancelListener
{
  protected boolean mStarted;
  protected final GoogleApiAvailability zzaxX;
  protected boolean zzayG;
  private ConnectionResult zzayH;
  private int zzayI = -1;
  private final Handler zzayJ = new Handler(Looper.getMainLooper());
  
  protected zzzw(zzaax paramzzaax)
  {
    this(paramzzaax, GoogleApiAvailability.getInstance());
  }
  
  zzzw(zzaax paramzzaax, GoogleApiAvailability paramGoogleApiAvailability)
  {
    super(paramzzaax);
    this.zzaxX = paramGoogleApiAvailability;
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
        zzva();
        return;
      }
      break;
    case 2: 
      label30:
      j = this.zzaxX.isGooglePlayServicesAvailable(getActivity());
      if (j != 0) {}
      break;
    }
    for (paramInt2 = i;; paramInt2 = 0)
    {
      paramInt1 = paramInt2;
      if (this.zzayH.getErrorCode() != 18) {
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
        this.zzayH = new ConnectionResult(paramInt1, null);
        break;
        zza(this.zzayH, this.zzayI);
        return;
      }
    }
  }
  
  public void onCancel(DialogInterface paramDialogInterface)
  {
    zza(new ConnectionResult(13, null), this.zzayI);
    zzva();
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (paramBundle != null)
    {
      this.zzayG = paramBundle.getBoolean("resolving_error", false);
      if (this.zzayG)
      {
        this.zzayI = paramBundle.getInt("failed_client_id", -1);
        this.zzayH = new ConnectionResult(paramBundle.getInt("failed_status"), (PendingIntent)paramBundle.getParcelable("failed_resolution"));
      }
    }
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putBoolean("resolving_error", this.zzayG);
    if (this.zzayG)
    {
      paramBundle.putInt("failed_client_id", this.zzayI);
      paramBundle.putInt("failed_status", this.zzayH.getErrorCode());
      paramBundle.putParcelable("failed_resolution", this.zzayH.getResolution());
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
  
  public void zzb(ConnectionResult paramConnectionResult, int paramInt)
  {
    if (!this.zzayG)
    {
      this.zzayG = true;
      this.zzayI = paramInt;
      this.zzayH = paramConnectionResult;
      this.zzayJ.post(new zza(null));
    }
  }
  
  protected abstract void zzuW();
  
  protected void zzva()
  {
    this.zzayI = -1;
    this.zzayG = false;
    this.zzayH = null;
    zzuW();
  }
  
  private class zza
    implements Runnable
  {
    private zza() {}
    
    @MainThread
    public void run()
    {
      if (!zzzw.this.mStarted) {
        return;
      }
      if (zzzw.zza(zzzw.this).hasResolution())
      {
        zzzw.this.zzaBs.startActivityForResult(GoogleApiActivity.zzb(zzzw.this.getActivity(), zzzw.zza(zzzw.this).getResolution(), zzzw.zzb(zzzw.this), false), 1);
        return;
      }
      if (zzzw.this.zzaxX.isUserResolvableError(zzzw.zza(zzzw.this).getErrorCode()))
      {
        zzzw.this.zzaxX.zza(zzzw.this.getActivity(), zzzw.this.zzaBs, zzzw.zza(zzzw.this).getErrorCode(), 2, zzzw.this);
        return;
      }
      if (zzzw.zza(zzzw.this).getErrorCode() == 18)
      {
        final Dialog localDialog = zzzw.this.zzaxX.zza(zzzw.this.getActivity(), zzzw.this);
        zzzw.this.zzaxX.zza(zzzw.this.getActivity().getApplicationContext(), new zzaar.zza()
        {
          public void zzvb()
          {
            zzzw.this.zzva();
            if (localDialog.isShowing()) {
              localDialog.dismiss();
            }
          }
        });
        return;
      }
      zzzw.this.zza(zzzw.zza(zzzw.this), zzzw.zzb(zzzw.this));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzzw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */