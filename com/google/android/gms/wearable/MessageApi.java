package com.google.android.gms.wearable;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;

@Deprecated
public abstract interface MessageApi
{
  public abstract PendingResult<SendMessageResult> sendMessage(GoogleApiClient paramGoogleApiClient, String paramString1, String paramString2, byte[] paramArrayOfByte);
  
  @Deprecated
  public static abstract interface MessageListener
  {
    public abstract void onMessageReceived(MessageEvent paramMessageEvent);
  }
  
  @Deprecated
  public static abstract interface SendMessageResult
    extends Result
  {
    public abstract int getRequestId();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/MessageApi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */