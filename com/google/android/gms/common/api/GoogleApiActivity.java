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
import com.google.android.gms.internal.zzqt;

public class GoogleApiActivity
  extends Activity
  implements DialogInterface.OnCancelListener
{
  protected int vD = 0;
  
  public static PendingIntent zza(Context paramContext, PendingIntent paramPendingIntent, int paramInt)
  {
    return zza(paramContext, paramPendingIntent, paramInt, true);
  }
  
  public static PendingIntent zza(Context paramContext, PendingIntent paramPendingIntent, int paramInt, boolean paramBoolean)
  {
    return PendingIntent.getActivity(paramContext, 0, zzb(paramContext, paramPendingIntent, paramInt, paramBoolean), 134217728);
  }
  
  private void zza(int paramInt, zzqt paramzzqt)
  {
    switch (paramInt)
    {
    default: 
      return;
    case 0: 
      paramzzqt.zza(new ConnectionResult(13, null), getIntent().getIntExtra("failing_client_id", -1));
      return;
    }
    paramzzqt.zzaqk();
  }
  
  private void zzapz()
  {
    Object localObject = getIntent().getExtras();
    if (localObject == null)
    {
      Log.e("GoogleApiActivity", "Activity started without extras");
      finish();
      return;
    }
    PendingIntent localPendingIntent = (PendingIntent)((Bundle)localObject).get("pending_intent");
    localObject = (Integer)((Bundle)localObject).get("error_code");
    if ((localPendingIntent == null) && (localObject == null))
    {
      Log.e("GoogleApiActivity", "Activity started without resolution");
      finish();
      return;
    }
    if (localPendingIntent != null) {
      try
      {
        startIntentSenderForResult(localPendingIntent.getIntentSender(), 1, null, 0, 0, 0);
        this.vD = 1;
        return;
      }
      catch (IntentSender.SendIntentException localSendIntentException)
      {
        Log.e("GoogleApiActivity", "Failed to launch pendingIntent", localSendIntentException);
        finish();
        return;
      }
    }
    GoogleApiAvailability.getInstance().showErrorDialogFragment(this, ((Integer)localObject).intValue(), 2, this);
    this.vD = 1;
  }
  
  public static Intent zzb(Context paramContext, PendingIntent paramPendingIntent, int paramInt, boolean paramBoolean)
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
      this.vD = 0;
      paramIntent = zzqt.zzasa();
      setResultCode(paramInt2);
      if (bool) {
        zza(paramInt2, paramIntent);
      }
    }
    for (;;)
    {
      finish();
      return;
      if (paramInt1 == 2)
      {
        this.vD = 0;
        setResultCode(paramInt2);
      }
    }
  }
  
  public void onCancel(DialogInterface paramDialogInterface)
  {
    this.vD = 0;
    setResult(0);
    finish();
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (paramBundle != null) {
      this.vD = paramBundle.getInt("resolution");
    }
    if (this.vD != 1) {
      zzapz();
    }
  }
  
  protected void onSaveInstanceState(Bundle paramBundle)
  {
    paramBundle.putInt("resolution", this.vD);
    super.onSaveInstanceState(paramBundle);
  }
  
  protected void setResultCode(int paramInt)
  {
    setResult(paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/GoogleApiActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */