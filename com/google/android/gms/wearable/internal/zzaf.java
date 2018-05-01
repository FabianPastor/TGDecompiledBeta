package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.wearable.Channel;
import com.google.android.gms.wearable.ChannelApi.OpenChannelResult;

final class zzaf
  implements ChannelApi.OpenChannelResult
{
  private final Status mStatus;
  private final Channel zzbSf;
  
  zzaf(Status paramStatus, Channel paramChannel)
  {
    this.mStatus = ((Status)zzbo.zzu(paramStatus));
    this.zzbSf = paramChannel;
  }
  
  public final Channel getChannel()
  {
    return this.zzbSf;
  }
  
  public final Status getStatus()
  {
    return this.mStatus;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzaf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */