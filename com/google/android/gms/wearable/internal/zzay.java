package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.wearable.Channel;
import com.google.android.gms.wearable.Channel.GetInputStreamResult;
import com.google.android.gms.wearable.Channel.GetOutputStreamResult;
import com.google.android.gms.wearable.ChannelClient.Channel;

public final class zzay
  extends AbstractSafeParcelable
  implements Channel, ChannelClient.Channel
{
  public static final Parcelable.Creator<zzay> CREATOR = new zzbi();
  private final String zzce;
  private final String zzcl;
  private final String zzo;
  
  public zzay(String paramString1, String paramString2, String paramString3)
  {
    this.zzce = ((String)Preconditions.checkNotNull(paramString1));
    this.zzo = ((String)Preconditions.checkNotNull(paramString2));
    this.zzcl = ((String)Preconditions.checkNotNull(paramString3));
  }
  
  public final PendingResult<Status> close(GoogleApiClient paramGoogleApiClient)
  {
    return paramGoogleApiClient.enqueue(new zzaz(this, paramGoogleApiClient));
  }
  
  public final boolean equals(Object paramObject)
  {
    boolean bool = true;
    if (paramObject == this) {}
    for (;;)
    {
      return bool;
      if (!(paramObject instanceof zzay))
      {
        bool = false;
      }
      else
      {
        paramObject = (zzay)paramObject;
        if ((!this.zzce.equals(((zzay)paramObject).zzce)) || (!Objects.equal(((zzay)paramObject).zzo, this.zzo)) || (!Objects.equal(((zzay)paramObject).zzcl, this.zzcl))) {
          bool = false;
        }
      }
    }
  }
  
  public final PendingResult<Channel.GetInputStreamResult> getInputStream(GoogleApiClient paramGoogleApiClient)
  {
    return paramGoogleApiClient.enqueue(new zzbb(this, paramGoogleApiClient));
  }
  
  public final String getNodeId()
  {
    return this.zzo;
  }
  
  public final PendingResult<Channel.GetOutputStreamResult> getOutputStream(GoogleApiClient paramGoogleApiClient)
  {
    return paramGoogleApiClient.enqueue(new zzbc(this, paramGoogleApiClient));
  }
  
  public final String getPath()
  {
    return this.zzcl;
  }
  
  public final int hashCode()
  {
    return this.zzce.hashCode();
  }
  
  public final String toString()
  {
    Object localObject = this.zzce.toCharArray();
    int i = localObject.length;
    int j = 0;
    int k = 0;
    while (j < i)
    {
      k += localObject[j];
      j++;
    }
    String str1 = this.zzce.trim();
    j = str1.length();
    localObject = str1;
    if (j > 25)
    {
      localObject = str1.substring(0, 10);
      str1 = str1.substring(j - 10, j);
      localObject = String.valueOf(localObject).length() + 16 + String.valueOf(str1).length() + (String)localObject + "..." + str1 + "::" + k;
    }
    str1 = this.zzo;
    String str2 = this.zzcl;
    return String.valueOf(localObject).length() + 31 + String.valueOf(str1).length() + String.valueOf(str2).length() + "Channel{token=" + (String)localObject + ", nodeId=" + str1 + ", path=" + str2 + "}";
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeString(paramParcel, 2, this.zzce, false);
    SafeParcelWriter.writeString(paramParcel, 3, getNodeId(), false);
    SafeParcelWriter.writeString(paramParcel, 4, getPath(), false);
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzay.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */