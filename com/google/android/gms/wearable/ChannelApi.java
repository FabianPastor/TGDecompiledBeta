package com.google.android.gms.wearable;

@Deprecated
public abstract interface ChannelApi
{
  @Deprecated
  public static abstract interface ChannelListener
  {
    public abstract void onChannelClosed(Channel paramChannel, int paramInt1, int paramInt2);
    
    public abstract void onChannelOpened(Channel paramChannel);
    
    public abstract void onInputClosed(Channel paramChannel, int paramInt1, int paramInt2);
    
    public abstract void onOutputClosed(Channel paramChannel, int paramInt1, int paramInt2);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/ChannelApi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */