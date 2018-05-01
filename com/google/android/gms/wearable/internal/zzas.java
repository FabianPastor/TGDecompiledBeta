package com.google.android.gms.wearable.internal;

import com.google.android.gms.wearable.Channel;
import com.google.android.gms.wearable.ChannelApi.ChannelListener;
import com.google.android.gms.wearable.ChannelClient.ChannelCallback;

public final class zzas
  implements ChannelApi.ChannelListener
{
  private final ChannelClient.ChannelCallback zzch;
  
  public zzas(ChannelClient.ChannelCallback paramChannelCallback)
  {
    this.zzch = paramChannelCallback;
  }
  
  public final boolean equals(Object paramObject)
  {
    boolean bool;
    if (this == paramObject) {
      bool = true;
    }
    for (;;)
    {
      return bool;
      if ((paramObject == null) || (getClass() != paramObject.getClass()))
      {
        bool = false;
      }
      else
      {
        paramObject = (zzas)paramObject;
        bool = this.zzch.equals(((zzas)paramObject).zzch);
      }
    }
  }
  
  public final int hashCode()
  {
    return this.zzch.hashCode();
  }
  
  public final void onChannelClosed(Channel paramChannel, int paramInt1, int paramInt2)
  {
    this.zzch.onChannelClosed(zzao.zzb(paramChannel), paramInt1, paramInt2);
  }
  
  public final void onChannelOpened(Channel paramChannel)
  {
    this.zzch.onChannelOpened(zzao.zzb(paramChannel));
  }
  
  public final void onInputClosed(Channel paramChannel, int paramInt1, int paramInt2)
  {
    this.zzch.onInputClosed(zzao.zzb(paramChannel), paramInt1, paramInt2);
  }
  
  public final void onOutputClosed(Channel paramChannel, int paramInt1, int paramInt2)
  {
    this.zzch.onOutputClosed(zzao.zzb(paramChannel), paramInt1, paramInt2);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzas.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */