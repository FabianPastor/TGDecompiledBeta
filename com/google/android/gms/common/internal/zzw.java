package com.google.android.gms.common.internal;

import android.app.Activity;
import android.content.Intent;

final class zzw
  extends zzv
{
  zzw(Intent paramIntent, Activity paramActivity, int paramInt) {}
  
  public final void zzale()
  {
    if (this.val$intent != null) {
      this.val$activity.startActivityForResult(this.val$intent, this.val$requestCode);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */