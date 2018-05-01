package com.google.android.gms.wearable;

import android.os.Parcelable;
import com.google.android.gms.common.api.GoogleApi;

public abstract class ChannelClient
  extends GoogleApi<Wearable.WearableOptions>
{
  public static abstract interface Channel
    extends Parcelable
  {}
  
  public static class ChannelCallback
  {
    public void onChannelClosed(ChannelClient.Channel paramChannel, int paramInt1, int paramInt2) {}
    
    public void onChannelOpened(ChannelClient.Channel paramChannel) {}
    
    public void onInputClosed(ChannelClient.Channel paramChannel, int paramInt1, int paramInt2) {}
    
    public void onOutputClosed(ChannelClient.Channel paramChannel, int paramInt1, int paramInt2) {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/ChannelClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */