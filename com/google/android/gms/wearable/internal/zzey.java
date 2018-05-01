package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.MessageApi.SendMessageResult;

public final class zzey
  implements MessageApi.SendMessageResult
{
  private final int zzeh;
  private final Status zzp;
  
  public zzey(Status paramStatus, int paramInt)
  {
    this.zzp = paramStatus;
    this.zzeh = paramInt;
  }
  
  public final int getRequestId()
  {
    return this.zzeh;
  }
  
  public final Status getStatus()
  {
    return this.zzp;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzey.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */