package com.google.android.gms.wearable;

import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.data.zzg;

public class DataItemBuffer
  extends zzg<DataItem>
  implements Result
{
  private final Status mStatus;
  
  public DataItemBuffer(DataHolder paramDataHolder)
  {
    super(paramDataHolder);
    this.mStatus = new Status(paramDataHolder.getStatusCode());
  }
  
  public Status getStatus()
  {
    return this.mStatus;
  }
  
  protected final String zzqS()
  {
    return "path";
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/DataItemBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */