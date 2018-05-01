package com.google.android.gms.internal;

import android.accounts.Account;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzg;
import com.google.android.gms.common.internal.zzl;
import com.google.android.gms.identity.intents.UserAddressRequest;

public class zzaqo
  extends zzl<zzaqq>
{
  private Activity mActivity;
  private final int mTheme;
  private final String zzaiu;
  private zza zzbht;
  
  public zzaqo(Activity paramActivity, Looper paramLooper, zzg paramzzg, int paramInt, GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    super(paramActivity, paramLooper, 12, paramzzg, paramConnectionCallbacks, paramOnConnectionFailedListener);
    this.zzaiu = paramzzg.getAccountName();
    this.mActivity = paramActivity;
    this.mTheme = paramInt;
  }
  
  public void disconnect()
  {
    super.disconnect();
    if (this.zzbht != null)
    {
      zza.zza(this.zzbht, null);
      this.zzbht = null;
    }
  }
  
  protected zzaqq zzHd()
    throws DeadObjectException
  {
    return (zzaqq)super.zzxD();
  }
  
  protected void zzHe()
  {
    super.zzxC();
  }
  
  public void zza(UserAddressRequest paramUserAddressRequest, int paramInt)
  {
    zzHe();
    this.zzbht = new zza(paramInt, this.mActivity);
    try
    {
      Bundle localBundle = new Bundle();
      localBundle.putString("com.google.android.gms.identity.intents.EXTRA_CALLING_PACKAGE_NAME", getContext().getPackageName());
      if (!TextUtils.isEmpty(this.zzaiu)) {
        localBundle.putParcelable("com.google.android.gms.identity.intents.EXTRA_ACCOUNT", new Account(this.zzaiu, "com.google"));
      }
      localBundle.putInt("com.google.android.gms.identity.intents.EXTRA_THEME", this.mTheme);
      zzHd().zza(this.zzbht, paramUserAddressRequest, localBundle);
      return;
    }
    catch (RemoteException paramUserAddressRequest)
    {
      Log.e("AddressClientImpl", "Exception requesting user address", paramUserAddressRequest);
      paramUserAddressRequest = new Bundle();
      paramUserAddressRequest.putInt("com.google.android.gms.identity.intents.EXTRA_ERROR_CODE", 555);
      this.zzbht.zzj(1, paramUserAddressRequest);
    }
  }
  
  protected zzaqq zzcW(IBinder paramIBinder)
  {
    return zzaqq.zza.zzcY(paramIBinder);
  }
  
  protected String zzeA()
  {
    return "com.google.android.gms.identity.intents.internal.IAddressService";
  }
  
  protected String zzez()
  {
    return "com.google.android.gms.identity.service.BIND";
  }
  
  public boolean zzxE()
  {
    return true;
  }
  
  public static final class zza
    extends zzaqp.zza
  {
    private Activity mActivity;
    private final int zzazu;
    
    public zza(int paramInt, Activity paramActivity)
    {
      this.zzazu = paramInt;
      this.mActivity = paramActivity;
    }
    
    private void setActivity(Activity paramActivity)
    {
      this.mActivity = paramActivity;
    }
    
    public void zzj(int paramInt, Bundle paramBundle)
    {
      Object localObject;
      if (paramInt == 1)
      {
        localObject = new Intent();
        ((Intent)localObject).putExtras(paramBundle);
        paramBundle = this.mActivity.createPendingResult(this.zzazu, (Intent)localObject, NUM);
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
            paramBundle.startResolutionForResult(this.mActivity, this.zzazu);
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
          paramBundle = this.mActivity.createPendingResult(this.zzazu, new Intent(), NUM);
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
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaqo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */