package com.google.android.gms.common.api.internal;

import android.os.Handler;

final class zzbh
  implements BackgroundDetector.BackgroundStateChangeListener
{
  zzbh(GoogleApiManager paramGoogleApiManager) {}
  
  public final void onBackgroundStateChanged(boolean paramBoolean)
  {
    GoogleApiManager.zza(this.zzjy).sendMessage(GoogleApiManager.zza(this.zzjy).obtainMessage(1, Boolean.valueOf(paramBoolean)));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzbh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */