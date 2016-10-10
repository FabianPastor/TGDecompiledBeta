package com.google.android.gms.common.internal;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import com.google.android.gms.internal.zzrb;

public abstract class zzj
  implements DialogInterface.OnClickListener
{
  public static zzj zza(final Activity paramActivity, Intent paramIntent, final int paramInt)
  {
    new zzj()
    {
      public void zzauo()
      {
        if (zzj.this != null) {
          paramActivity.startActivityForResult(zzj.this, paramInt);
        }
      }
    };
  }
  
  public static zzj zza(@NonNull final Fragment paramFragment, Intent paramIntent, final int paramInt)
  {
    new zzj()
    {
      public void zzauo()
      {
        if (zzj.this != null) {
          paramFragment.startActivityForResult(zzj.this, paramInt);
        }
      }
    };
  }
  
  public static zzj zza(@NonNull final zzrb paramzzrb, Intent paramIntent, final int paramInt)
  {
    new zzj()
    {
      @TargetApi(11)
      public void zzauo()
      {
        if (zzj.this != null) {
          paramzzrb.startActivityForResult(zzj.this, paramInt);
        }
      }
    };
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    try
    {
      zzauo();
      paramDialogInterface.dismiss();
      return;
    }
    catch (ActivityNotFoundException paramDialogInterface)
    {
      Log.e("DialogRedirect", "Can't redirect to app settings for Google Play services", paramDialogInterface);
    }
  }
  
  public abstract void zzauo();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */