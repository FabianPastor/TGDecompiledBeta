package com.google.android.gms.internal;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;

public final class zzcbf
  extends zzcbh
{
  private Activity mActivity;
  private final int zzaBk;
  
  public zzcbf(int paramInt, Activity paramActivity)
  {
    this.zzaBk = paramInt;
    this.mActivity = paramActivity;
  }
  
  private final void setActivity(Activity paramActivity)
  {
    this.mActivity = null;
  }
  
  public final void zze(int paramInt, Bundle paramBundle)
  {
    Object localObject;
    if (paramInt == 1)
    {
      localObject = new Intent();
      ((Intent)localObject).putExtras(paramBundle);
      paramBundle = this.mActivity.createPendingResult(this.zzaBk, (Intent)localObject, NUM);
      if (paramBundle != null) {}
    }
    for (;;)
    {
      return;
      try
      {
        paramBundle.send(1);
        return;
      }
      catch (PendingIntent.CanceledException paramBundle)
      {
        Log.w("AddressClientImpl", "Exception settng pending result", paramBundle);
        return;
      }
      localObject = null;
      if (paramBundle != null) {
        localObject = (PendingIntent)paramBundle.getParcelable("com.google.android.gms.identity.intents.EXTRA_PENDING_INTENT");
      }
      paramBundle = new ConnectionResult(paramInt, (PendingIntent)localObject);
      if (paramBundle.hasResolution()) {
        try
        {
          paramBundle.startResolutionForResult(this.mActivity, this.zzaBk);
          return;
        }
        catch (IntentSender.SendIntentException paramBundle)
        {
          Log.w("AddressClientImpl", "Exception starting pending intent", paramBundle);
          return;
        }
      }
      try
      {
        paramBundle = this.mActivity.createPendingResult(this.zzaBk, new Intent(), NUM);
        if (paramBundle != null)
        {
          paramBundle.send(1);
          return;
        }
      }
      catch (PendingIntent.CanceledException paramBundle)
      {
        Log.w("AddressClientImpl", "Exception setting pending result", paramBundle);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcbf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */