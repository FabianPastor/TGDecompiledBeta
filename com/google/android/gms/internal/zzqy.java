package com.google.android.gms.internal;

import com.google.android.gms.common.api.Releasable;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataHolder;

public abstract class zzqy
  implements Releasable, Result
{
  protected final Status hv;
  protected final DataHolder zy;
  
  protected zzqy(DataHolder paramDataHolder, Status paramStatus)
  {
    this.hv = paramStatus;
    this.zy = paramDataHolder;
  }
  
  public Status getStatus()
  {
    return this.hv;
  }
  
  public void release()
  {
    if (this.zy != null) {
      this.zy.close();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzqy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */