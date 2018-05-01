package com.google.android.gms.wearable.internal;

import android.content.IntentFilter;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbe;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.wearable.Channel;
import com.google.android.gms.wearable.Channel.GetInputStreamResult;
import com.google.android.gms.wearable.Channel.GetOutputStreamResult;
import com.google.android.gms.wearable.ChannelApi.ChannelListener;

public final class zzak
  extends zza
  implements Channel
{
  public static final Parcelable.Creator<zzak> CREATOR = new zzau();
  private final String mPath;
  private final String zzakv;
  private final String zzbRe;
  
  public zzak(String paramString1, String paramString2, String paramString3)
  {
    this.zzakv = ((String)zzbo.zzu(paramString1));
    this.zzbRe = ((String)zzbo.zzu(paramString2));
    this.mPath = ((String)zzbo.zzu(paramString3));
  }
  
  public final PendingResult<Status> addListener(GoogleApiClient paramGoogleApiClient, ChannelApi.ChannelListener paramChannelListener)
  {
    IntentFilter localIntentFilter = zzez.zzgl("com.google.android.gms.wearable.CHANNEL_EVENT");
    return zzb.zza(paramGoogleApiClient, new zzar(this.zzakv, new IntentFilter[] { localIntentFilter }), paramChannelListener);
  }
  
  public final PendingResult<Status> close(GoogleApiClient paramGoogleApiClient)
  {
    return paramGoogleApiClient.zzd(new zzal(this, paramGoogleApiClient));
  }
  
  public final PendingResult<Status> close(GoogleApiClient paramGoogleApiClient, int paramInt)
  {
    return paramGoogleApiClient.zzd(new zzam(this, paramGoogleApiClient, paramInt));
  }
  
  public final boolean equals(Object paramObject)
  {
    if (paramObject == this) {}
    do
    {
      return true;
      if (!(paramObject instanceof zzak)) {
        return false;
      }
      paramObject = (zzak)paramObject;
    } while ((this.zzakv.equals(((zzak)paramObject).zzakv)) && (zzbe.equal(((zzak)paramObject).zzbRe, this.zzbRe)) && (zzbe.equal(((zzak)paramObject).mPath, this.mPath)));
    return false;
  }
  
  public final PendingResult<Channel.GetInputStreamResult> getInputStream(GoogleApiClient paramGoogleApiClient)
  {
    return paramGoogleApiClient.zzd(new zzan(this, paramGoogleApiClient));
  }
  
  public final String getNodeId()
  {
    return this.zzbRe;
  }
  
  public final PendingResult<Channel.GetOutputStreamResult> getOutputStream(GoogleApiClient paramGoogleApiClient)
  {
    return paramGoogleApiClient.zzd(new zzao(this, paramGoogleApiClient));
  }
  
  public final String getPath()
  {
    return this.mPath;
  }
  
  public final int hashCode()
  {
    return this.zzakv.hashCode();
  }
  
  public final PendingResult<Status> receiveFile(GoogleApiClient paramGoogleApiClient, Uri paramUri, boolean paramBoolean)
  {
    zzbo.zzb(paramGoogleApiClient, "client is null");
    zzbo.zzb(paramUri, "uri is null");
    return paramGoogleApiClient.zzd(new zzap(this, paramGoogleApiClient, paramUri, paramBoolean));
  }
  
  public final PendingResult<Status> removeListener(GoogleApiClient paramGoogleApiClient, ChannelApi.ChannelListener paramChannelListener)
  {
    zzbo.zzb(paramGoogleApiClient, "client is null");
    zzbo.zzb(paramChannelListener, "listener is null");
    return paramGoogleApiClient.zzd(new zzag(paramGoogleApiClient, paramChannelListener, this.zzakv));
  }
  
  public final PendingResult<Status> sendFile(GoogleApiClient paramGoogleApiClient, Uri paramUri)
  {
    return sendFile(paramGoogleApiClient, paramUri, 0L, -1L);
  }
  
  public final PendingResult<Status> sendFile(GoogleApiClient paramGoogleApiClient, Uri paramUri, long paramLong1, long paramLong2)
  {
    zzbo.zzb(paramGoogleApiClient, "client is null");
    zzbo.zzb(this.zzakv, "token is null");
    zzbo.zzb(paramUri, "uri is null");
    if (paramLong1 >= 0L)
    {
      bool = true;
      zzbo.zzb(bool, "startOffset is negative: %s", new Object[] { Long.valueOf(paramLong1) });
      if ((paramLong2 < 0L) && (paramLong2 != -1L)) {
        break label113;
      }
    }
    label113:
    for (boolean bool = true;; bool = false)
    {
      zzbo.zzb(bool, "invalid length: %s", new Object[] { Long.valueOf(paramLong2) });
      return paramGoogleApiClient.zzd(new zzaq(this, paramGoogleApiClient, paramUri, paramLong1, paramLong2));
      bool = false;
      break;
    }
  }
  
  public final String toString()
  {
    String str1 = this.zzakv;
    String str2 = this.zzbRe;
    String str3 = this.mPath;
    return String.valueOf(str1).length() + 43 + String.valueOf(str2).length() + String.valueOf(str3).length() + "ChannelImpl{, token='" + str1 + "', nodeId='" + str2 + "', path='" + str3 + "'}";
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = zzd.zze(paramParcel);
    zzd.zza(paramParcel, 2, this.zzakv, false);
    zzd.zza(paramParcel, 3, getNodeId(), false);
    zzd.zza(paramParcel, 4, getPath(), false);
    zzd.zzI(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzak.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */