package com.google.android.gms.internal.wallet;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.wallet.FullWallet;
import com.google.android.gms.wallet.MaskedWallet;
import java.lang.ref.WeakReference;

final class zzae
  extends zzaf
{
  private final int zzaa;
  private final WeakReference<Activity> zzgi;
  
  public zzae(Activity paramActivity, int paramInt)
  {
    this.zzgi = new WeakReference(paramActivity);
    this.zzaa = paramInt;
  }
  
  public final void zza(int paramInt, Bundle paramBundle)
  {
    Preconditions.checkNotNull(paramBundle, "Bundle should not be null");
    Activity localActivity = (Activity)this.zzgi.get();
    if (localActivity == null) {
      Log.d("WalletClientImpl", "Ignoring onWalletObjectsCreated, Activity has gone");
    }
    for (;;)
    {
      return;
      paramBundle = new ConnectionResult(paramInt, (PendingIntent)paramBundle.getParcelable("com.google.android.gms.wallet.EXTRA_PENDING_INTENT"));
      if (paramBundle.hasResolution())
      {
        try
        {
          paramBundle.startResolutionForResult(localActivity, this.zzaa);
        }
        catch (IntentSender.SendIntentException paramBundle)
        {
          Log.w("WalletClientImpl", "Exception starting pending intent", paramBundle);
        }
      }
      else
      {
        paramBundle = String.valueOf(paramBundle);
        Log.e("WalletClientImpl", String.valueOf(paramBundle).length() + 75 + "Create Wallet Objects confirmation UI will not be shown connection result: " + paramBundle);
        paramBundle = new Intent();
        paramBundle.putExtra("com.google.android.gms.wallet.EXTRA_ERROR_CODE", 413);
        paramBundle = localActivity.createPendingResult(this.zzaa, paramBundle, NUM);
        if (paramBundle == null) {
          Log.w("WalletClientImpl", "Null pending result returned for onWalletObjectsCreated");
        } else {
          try
          {
            paramBundle.send(1);
          }
          catch (PendingIntent.CanceledException paramBundle)
          {
            Log.w("WalletClientImpl", "Exception setting pending result", paramBundle);
          }
        }
      }
    }
  }
  
  public final void zza(int paramInt, FullWallet paramFullWallet, Bundle paramBundle)
  {
    Activity localActivity = (Activity)this.zzgi.get();
    if (localActivity == null) {
      Log.d("WalletClientImpl", "Ignoring onFullWalletLoaded, Activity has gone");
    }
    for (;;)
    {
      return;
      Object localObject = null;
      if (paramBundle != null) {
        localObject = (PendingIntent)paramBundle.getParcelable("com.google.android.gms.wallet.EXTRA_PENDING_INTENT");
      }
      localObject = new ConnectionResult(paramInt, (PendingIntent)localObject);
      if (((ConnectionResult)localObject).hasResolution())
      {
        try
        {
          ((ConnectionResult)localObject).startResolutionForResult(localActivity, this.zzaa);
        }
        catch (IntentSender.SendIntentException paramFullWallet)
        {
          Log.w("WalletClientImpl", "Exception starting pending intent", paramFullWallet);
        }
      }
      else
      {
        paramBundle = new Intent();
        int i;
        if (((ConnectionResult)localObject).isSuccess())
        {
          i = -1;
          paramBundle.putExtra("com.google.android.gms.wallet.EXTRA_FULL_WALLET", paramFullWallet);
          paramFullWallet = localActivity.createPendingResult(this.zzaa, paramBundle, NUM);
          if (paramFullWallet == null) {
            Log.w("WalletClientImpl", "Null pending result returned for onFullWalletLoaded");
          }
        }
        else
        {
          if (paramInt == 408) {}
          for (i = 0;; i = 1)
          {
            paramBundle.putExtra("com.google.android.gms.wallet.EXTRA_ERROR_CODE", paramInt);
            break;
          }
        }
        try
        {
          paramFullWallet.send(i);
        }
        catch (PendingIntent.CanceledException paramFullWallet)
        {
          Log.w("WalletClientImpl", "Exception setting pending result", paramFullWallet);
        }
      }
    }
  }
  
  public final void zza(int paramInt, MaskedWallet paramMaskedWallet, Bundle paramBundle)
  {
    Activity localActivity = (Activity)this.zzgi.get();
    if (localActivity == null) {
      Log.d("WalletClientImpl", "Ignoring onMaskedWalletLoaded, Activity has gone");
    }
    for (;;)
    {
      return;
      Object localObject = null;
      if (paramBundle != null) {
        localObject = (PendingIntent)paramBundle.getParcelable("com.google.android.gms.wallet.EXTRA_PENDING_INTENT");
      }
      paramBundle = new ConnectionResult(paramInt, (PendingIntent)localObject);
      if (paramBundle.hasResolution())
      {
        try
        {
          paramBundle.startResolutionForResult(localActivity, this.zzaa);
        }
        catch (IntentSender.SendIntentException paramMaskedWallet)
        {
          Log.w("WalletClientImpl", "Exception starting pending intent", paramMaskedWallet);
        }
      }
      else
      {
        localObject = new Intent();
        int i;
        if (paramBundle.isSuccess())
        {
          i = -1;
          ((Intent)localObject).putExtra("com.google.android.gms.wallet.EXTRA_MASKED_WALLET", paramMaskedWallet);
          paramMaskedWallet = localActivity.createPendingResult(this.zzaa, (Intent)localObject, NUM);
          if (paramMaskedWallet == null) {
            Log.w("WalletClientImpl", "Null pending result returned for onMaskedWalletLoaded");
          }
        }
        else
        {
          if (paramInt == 408) {}
          for (i = 0;; i = 1)
          {
            ((Intent)localObject).putExtra("com.google.android.gms.wallet.EXTRA_ERROR_CODE", paramInt);
            break;
          }
        }
        try
        {
          paramMaskedWallet.send(i);
        }
        catch (PendingIntent.CanceledException paramMaskedWallet)
        {
          Log.w("WalletClientImpl", "Exception setting pending result", paramMaskedWallet);
        }
      }
    }
  }
  
  public final void zza(int paramInt, boolean paramBoolean, Bundle paramBundle)
  {
    paramBundle = (Activity)this.zzgi.get();
    if (paramBundle == null) {
      Log.d("WalletClientImpl", "Ignoring onPreAuthorizationDetermined, Activity has gone");
    }
    for (;;)
    {
      return;
      Intent localIntent = new Intent();
      localIntent.putExtra("com.google.android.gm.wallet.EXTRA_IS_USER_PREAUTHORIZED", paramBoolean);
      paramBundle = paramBundle.createPendingResult(this.zzaa, localIntent, NUM);
      if (paramBundle == null) {
        Log.w("WalletClientImpl", "Null pending result returned for onPreAuthorizationDetermined");
      } else {
        try
        {
          paramBundle.send(-1);
        }
        catch (PendingIntent.CanceledException paramBundle)
        {
          Log.w("WalletClientImpl", "Exception setting pending result", paramBundle);
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/wallet/zzae.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */