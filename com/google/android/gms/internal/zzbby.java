package com.google.android.gms.internal;

import com.google.android.gms.common.api.Releasable;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataHolder;

public class zzbby
  implements Releasable, Result
{
  private Status mStatus;
  protected final DataHolder zzaCX;
  
  protected zzbby(DataHolder paramDataHolder, Status paramStatus)
  {
    this.mStatus = paramStatus;
    this.zzaCX = paramDataHolder;
  }
  
  public Status getStatus()
  {
    return this.mStatus;
  }
  
  public void release()
  {
    if (this.zzaCX != null) {
      this.zzaCX.close();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbby.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */