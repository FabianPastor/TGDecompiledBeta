package com.google.android.gms.internal;

import com.google.android.gms.common.api.Releasable;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataHolder;

public abstract class zzqk
  implements Releasable, Result
{
  protected final Status fp;
  protected final DataHolder xi;
  
  protected zzqk(DataHolder paramDataHolder, Status paramStatus)
  {
    this.fp = paramStatus;
    this.xi = paramDataHolder;
  }
  
  public Status getStatus()
  {
    return this.fp;
  }
  
  public void release()
  {
    if (this.xi != null) {
      this.xi.close();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzqk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */