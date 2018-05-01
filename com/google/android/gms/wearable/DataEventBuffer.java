package com.google.android.gms.wearable;

import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.data.EntityBuffer;

public class DataEventBuffer
  extends EntityBuffer<Object>
  implements Result
{
  private final Status zzp;
  
  public DataEventBuffer(DataHolder paramDataHolder)
  {
    super(paramDataHolder);
    this.zzp = new Status(paramDataHolder.getStatusCode());
  }
  
  protected String getPrimaryDataMarkerColumn()
  {
    return "path";
  }
  
  public Status getStatus()
  {
    return this.zzp;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/DataEventBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */