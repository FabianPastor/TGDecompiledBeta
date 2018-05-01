package com.google.android.gms.common.api;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.internal.GoogleApiManager;

public class GoogleApiActivity
  extends Activity
  implements DialogInterface.OnCancelListener
{
  private int zzct = 0;
  
  public static PendingIntent zza(Context paramContext, PendingIntent paramPendingIntent, int paramInt)
  {
    return PendingIntent.getActivity(paramContext, 0, zza(paramContext, paramPendingIntent, paramInt, true), 134217728);
  }
  
  public static Intent zza(Context paramContext, PendingIntent paramPendingIntent, int paramInt, boolean paramBoolean)
  {
    paramContext = new Intent(paramContext, GoogleApiActivity.class);
    paramContext.putExtra("pending_intent", paramPendingIntent);
    paramContext.putExtra("failing_client_id", paramInt);
    paramContext.putExtra("notify_manager", paramBoolean);
    return paramContext;
  }
  
  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    if (paramInt1 == 1)
    {
      boolean bool = getIntent().getBooleanExtra("notify_manager", true);
      this.zzct = 0;
      setResult(paramInt2, paramIntent);
      if (bool)
      {
        paramIntent = GoogleApiManager.zzb(this);
        switch (paramInt2)
        {
        }
      }
    }
    for (;;)
    {
      finish();
      return;
      paramIntent.zza(new ConnectionResult(13, null), getIntent().getIntExtra("failing_client_id", -1));
      continue;
      paramIntent.zzr();
      continue;
      if (paramInt1 == 2)
      {
        this.zzct = 0;
        setResult(paramInt2, paramIntent);
      }
    }
  }
  
  public void onCancel(DialogInterface paramDialogInterface)
  {
    this.zzct = 0;
    setResult(0);
    finish();
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (paramBundle != null) {
      this.zzct = paramBundle.getInt("resolution");
    }
    Object localObject;
    if (this.zzct != 1)
    {
      localObject = getIntent().getExtras();
      if (localObject != null) {
        break label52;
      }
      Log.e("GoogleApiActivity", "Activity started without extras");
      finish();
    }
    for (;;)
    {
      return;
      label52:
      paramBundle = (PendingIntent)((Bundle)localObject).get("pending_intent");
      localObject = (Integer)((Bundle)localObject).get("error_code");
      if ((paramBundle == null) && (localObject == null))
      {
        Log.e("GoogleApiActivity", "Activity started without resolution");
        finish();
      }
      else if (paramBundle != null)
      {
        try
        {
          startIntentSenderForResult(paramBundle.getIntentSender(), 1, null, 0, 0, 0);
          this.zzct = 1;
        }
        catch (IntentSender.SendIntentException paramBundle)
        {
          Log.e("GoogleApiActivity", "Failed to launch pendingIntent", paramBundle);
          finish();
        }
      }
      else
      {
        GoogleApiAvailability.getInstance().showErrorDialogFragment(this, ((Integer)localObject).intValue(), 2, this);
        this.zzct = 1;
      }
    }
  }
  
  protected void onSaveInstanceState(Bundle paramBundle)
  {
    paramBundle.putInt("resolution", this.zzct);
    super.onSaveInstanceState(paramBundle);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/GoogleApiActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */