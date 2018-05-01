package com.google.android.gms.common.internal;

import android.content.Intent;
import com.google.android.gms.common.api.internal.LifecycleFragment;

final class zzd
  extends DialogRedirect
{
  zzd(Intent paramIntent, LifecycleFragment paramLifecycleFragment, int paramInt) {}
  
  public final void redirect()
  {
    if (this.zzsh != null) {
      this.zzsi.startActivityForResult(this.zzsh, this.val$requestCode);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */