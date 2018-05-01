package com.google.android.gms.common.api.internal;

import android.os.Handler;
import com.google.android.gms.common.internal.BaseGmsClient.SignOutCallbacks;

final class zzbl
  implements BaseGmsClient.SignOutCallbacks
{
  zzbl(GoogleApiManager.zza paramzza) {}
  
  public final void onSignOutComplete()
  {
    GoogleApiManager.zza(this.zzkk.zzjy).post(new zzbm(this));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzbl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */