package com.google.android.gms.common.internal;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;
import com.google.android.gms.common.api.internal.LifecycleFragment;

public abstract class DialogRedirect
  implements DialogInterface.OnClickListener
{
  public static DialogRedirect getInstance(Activity paramActivity, Intent paramIntent, int paramInt)
  {
    return new zzb(paramIntent, paramActivity, paramInt);
  }
  
  public static DialogRedirect getInstance(Fragment paramFragment, Intent paramIntent, int paramInt)
  {
    return new zzc(paramIntent, paramFragment, paramInt);
  }
  
  public static DialogRedirect getInstance(LifecycleFragment paramLifecycleFragment, Intent paramIntent, int paramInt)
  {
    return new zzd(paramIntent, paramLifecycleFragment, paramInt);
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    try
    {
      redirect();
      return;
    }
    catch (ActivityNotFoundException localActivityNotFoundException)
    {
      for (;;)
      {
        Log.e("DialogRedirect", "Failed to start resolution intent", localActivityNotFoundException);
        paramDialogInterface.dismiss();
      }
    }
    finally
    {
      paramDialogInterface.dismiss();
    }
  }
  
  protected abstract void redirect();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/DialogRedirect.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */