package com.google.android.gms.common.internal;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;
import com.google.android.gms.common.api.internal.zzcf;

public abstract class zzv
  implements DialogInterface.OnClickListener
{
  public static zzv zza(Activity paramActivity, Intent paramIntent, int paramInt)
  {
    return new zzw(paramIntent, paramActivity, paramInt);
  }
  
  public static zzv zza(Fragment paramFragment, Intent paramIntent, int paramInt)
  {
    return new zzx(paramIntent, paramFragment, paramInt);
  }
  
  public static zzv zza(zzcf paramzzcf, Intent paramIntent, int paramInt)
  {
    return new zzy(paramIntent, paramzzcf, 2);
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    try
    {
      zzale();
      return;
    }
    catch (ActivityNotFoundException localActivityNotFoundException)
    {
      Log.e("DialogRedirect", "Failed to start resolution intent", localActivityNotFoundException);
      return;
    }
    finally
    {
      paramDialogInterface.dismiss();
    }
  }
  
  protected abstract void zzale();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzv.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */