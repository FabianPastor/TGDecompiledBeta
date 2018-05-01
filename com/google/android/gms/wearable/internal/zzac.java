package com.google.android.gms.wearable.internal;

import android.content.IntentFilter;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.wearable.ChannelApi;
import com.google.android.gms.wearable.ChannelApi.ChannelListener;
import com.google.android.gms.wearable.ChannelApi.OpenChannelResult;

public final class zzac
  implements ChannelApi
{
  public final PendingResult<Status> addListener(GoogleApiClient paramGoogleApiClient, ChannelApi.ChannelListener paramChannelListener)
  {
    zzbo.zzb(paramGoogleApiClient, "client is null");
    zzbo.zzb(paramChannelListener, "listener is null");
    return zzb.zza(paramGoogleApiClient, new zzae(new IntentFilter[] { zzez.zzgl("com.google.android.gms.wearable.CHANNEL_EVENT") }), paramChannelListener);
  }
  
  public final PendingResult<ChannelApi.OpenChannelResult> openChannel(GoogleApiClient paramGoogleApiClient, String paramString1, String paramString2)
  {
    zzbo.zzb(paramGoogleApiClient, "client is null");
    zzbo.zzb(paramString1, "nodeId is null");
    zzbo.zzb(paramString2, "path is null");
    return paramGoogleApiClient.zzd(new zzad(this, paramGoogleApiClient, paramString1, paramString2));
  }
  
  public final PendingResult<Status> removeListener(GoogleApiClient paramGoogleApiClient, ChannelApi.ChannelListener paramChannelListener)
  {
    zzbo.zzb(paramGoogleApiClient, "client is null");
    zzbo.zzb(paramChannelListener, "listener is null");
    return paramGoogleApiClient.zzd(new zzag(paramGoogleApiClient, paramChannelListener, null));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzac.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */