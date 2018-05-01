package com.google.android.gms.common.internal;

import android.app.Activity;
import android.content.Intent;

final class zzb
  extends DialogRedirect
{
  zzb(Intent paramIntent, Activity paramActivity, int paramInt) {}
  
  public final void redirect()
  {
    if (this.zzsh != null) {
      this.val$activity.startActivityForResult(this.zzsh, this.val$requestCode);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */