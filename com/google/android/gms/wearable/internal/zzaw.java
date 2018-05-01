package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.util.Log;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.wearable.ChannelApi.ChannelListener;

public final class zzaw
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<zzaw> CREATOR = new zzax();
  private final int type;
  private final int zzcj;
  private final zzay zzck;
  private final int zzg;
  
  public zzaw(zzay paramzzay, int paramInt1, int paramInt2, int paramInt3)
  {
    this.zzck = paramzzay;
    this.type = paramInt1;
    this.zzg = paramInt2;
    this.zzcj = paramInt3;
  }
  
  public final String toString()
  {
    String str1 = String.valueOf(this.zzck);
    int i = this.type;
    String str2;
    String str3;
    switch (i)
    {
    default: 
      str2 = Integer.toString(i);
      i = this.zzg;
      switch (i)
      {
      default: 
        str3 = Integer.toString(i);
      }
      break;
    }
    for (;;)
    {
      i = this.zzcj;
      return String.valueOf(str1).length() + 81 + String.valueOf(str2).length() + String.valueOf(str3).length() + "ChannelEventParcelable[, channel=" + str1 + ", type=" + str2 + ", closeReason=" + str3 + ", appErrorCode=" + i + "]";
      str2 = "CHANNEL_OPENED";
      break;
      str2 = "CHANNEL_CLOSED";
      break;
      str2 = "OUTPUT_CLOSED";
      break;
      str2 = "INPUT_CLOSED";
      break;
      str3 = "CLOSE_REASON_DISCONNECTED";
      continue;
      str3 = "CLOSE_REASON_REMOTE_CLOSE";
      continue;
      str3 = "CLOSE_REASON_LOCAL_CLOSE";
      continue;
      str3 = "CLOSE_REASON_NORMAL";
    }
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeParcelable(paramParcel, 2, this.zzck, paramInt, false);
    SafeParcelWriter.writeInt(paramParcel, 3, this.type);
    SafeParcelWriter.writeInt(paramParcel, 4, this.zzg);
    SafeParcelWriter.writeInt(paramParcel, 5, this.zzcj);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
  
  public final void zza(ChannelApi.ChannelListener paramChannelListener)
  {
    switch (this.type)
    {
    default: 
      int i = this.type;
      Log.w("ChannelEventParcelable", 25 + "Unknown type: " + i);
    }
    for (;;)
    {
      return;
      paramChannelListener.onChannelOpened(this.zzck);
      continue;
      paramChannelListener.onChannelClosed(this.zzck, this.zzg, this.zzcj);
      continue;
      paramChannelListener.onInputClosed(this.zzck, this.zzg, this.zzcj);
      continue;
      paramChannelListener.onOutputClosed(this.zzck, this.zzg, this.zzcj);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzaw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */