package com.google.android.gms.wearable;

import com.google.android.gms.common.data.DataHolder;

final class zzl
  implements Runnable
{
  zzl(WearableListenerService.zzd paramzzd, DataHolder paramDataHolder) {}
  
  public final void run()
  {
    DataEventBuffer localDataEventBuffer = new DataEventBuffer(this.zzan);
    try
    {
      this.zzao.zzak.onDataChanged(localDataEventBuffer);
      return;
    }
    finally
    {
      localDataEventBuffer.release();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/zzl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */