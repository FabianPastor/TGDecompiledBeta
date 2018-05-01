package com.google.android.gms.internal;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import java.util.concurrent.atomic.AtomicReference;

public abstract class zzbba
  extends zzbds
  implements DialogInterface.OnCancelListener
{
  protected volatile boolean mStarted;
  protected final AtomicReference<zzbbb> zzaBN = new AtomicReference(null);
  private final Handler zzaBO = new Handler(Looper.getMainLooper());
  protected final GoogleApiAvailability zzaBd;
  
  protected zzbba(zzbdt paramzzbdt)
  {
    this(paramzzbdt, GoogleApiAvailability.getInstance());
  }
  
  private zzbba(zzbdt paramzzbdt, GoogleApiAvailability paramGoogleApiAvailability)
  {
    super(paramzzbdt);
    this.zzaBd = paramGoogleApiAvailability;
  }
  
  private static int zza(@Nullable zzbbb paramzzbbb)
  {
    if (paramzzbbb == null) {
      return -1;
    }
    return paramzzbbb.zzpy();
  }
  
  public final void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    int i = 13;
    zzbbb localzzbbb = (zzbbb)this.zzaBN.get();
    switch (paramInt1)
    {
    default: 
      paramInt1 = 0;
      paramIntent = localzzbbb;
      if (paramInt1 != 0)
      {
        zzpx();
        return;
      }
      break;
    case 2: 
      label45:
      label53:
      i = this.zzaBd.isGooglePlayServicesAvailable(getActivity());
      if (i != 0) {}
      break;
    }
    for (paramInt2 = 1;; paramInt2 = 0)
    {
      if (localzzbbb == null) {
        break label53;
      }
      paramIntent = localzzbbb;
      paramInt1 = paramInt2;
      if (localzzbbb.zzpz().getErrorCode() != 18) {
        break label45;
      }
      paramIntent = localzzbbb;
      paramInt1 = paramInt2;
      if (i != 18) {
        break label45;
      }
      return;
      if (paramInt2 == -1)
      {
        paramInt1 = 1;
        paramIntent = localzzbbb;
        break label45;
      }
      if (paramInt2 != 0) {
        break;
      }
      paramInt1 = i;
      if (paramIntent != null) {
        paramInt1 = paramIntent.getIntExtra("<<ResolutionFailureErrorDetail>>", 13);
      }
      paramIntent = new zzbbb(new ConnectionResult(paramInt1, null), zza(localzzbbb));
      this.zzaBN.set(paramIntent);
      paramInt1 = 0;
      break label45;
      if (paramIntent == null) {
        break label53;
      }
      zza(paramIntent.zzpz(), paramIntent.zzpy());
      return;
    }
  }
  
  public void onCancel(DialogInterface paramDialogInterface)
  {
    zza(new ConnectionResult(13, null), zza((zzbbb)this.zzaBN.get()));
    zzpx();
  }
  
  public final void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    AtomicReference localAtomicReference;
    if (paramBundle != null)
    {
      localAtomicReference = this.zzaBN;
      if (!paramBundle.getBoolean("resolving_error", false)) {
        break label67;
      }
    }
    label67:
    for (paramBundle = new zzbbb(new ConnectionResult(paramBundle.getInt("failed_status"), (PendingIntent)paramBundle.getParcelable("failed_resolution")), paramBundle.getInt("failed_client_id", -1));; paramBundle = null)
    {
      localAtomicReference.set(paramBundle);
      return;
    }
  }
  
  public final void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    zzbbb localzzbbb = (zzbbb)this.zzaBN.get();
    if (localzzbbb != null)
    {
      paramBundle.putBoolean("resolving_error", true);
      paramBundle.putInt("failed_client_id", localzzbbb.zzpy());
      paramBundle.putInt("failed_status", localzzbbb.zzpz().getErrorCode());
      paramBundle.putParcelable("failed_resolution", localzzbbb.zzpz().getResolution());
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
  
  public final void zzb(ConnectionResult paramConnectionResult, int paramInt)
  {
    paramConnectionResult = new zzbbb(paramConnectionResult, paramInt);
    if (this.zzaBN.compareAndSet(null, paramConnectionResult)) {
      this.zzaBO.post(new zzbbc(this, paramConnectionResult));
    }
  }
  
  protected abstract void zzps();
  
  protected final void zzpx()
  {
    this.zzaBN.set(null);
    zzps();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbba.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */