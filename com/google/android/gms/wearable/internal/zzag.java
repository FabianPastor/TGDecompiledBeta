package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.wearable.ChannelApi.ChannelListener;

final class zzag
  extends zzn<Status>
{
  private final String zzakv;
  private ChannelApi.ChannelListener zzbSg;
  
  zzag(GoogleApiClient paramGoogleApiClient, ChannelApi.ChannelListener paramChannelListener, String paramString)
  {
    super(paramGoogleApiClient);
    this.zzbSg = ((ChannelApi.ChannelListener)zzbo.zzu(paramChannelListener));
    this.zzakv = paramString;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzag.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */