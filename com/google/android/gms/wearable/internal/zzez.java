package com.google.android.gms.wearable.internal;

import android.content.Context;
import com.google.android.gms.common.api.GoogleApi.Settings;
import com.google.android.gms.common.internal.PendingResultUtil;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageClient;

public final class zzez
  extends MessageClient
{
  private final MessageApi zzei = new zzeu();
  
  public zzez(Context paramContext, GoogleApi.Settings paramSettings)
  {
    super(paramContext, paramSettings);
  }
  
  public final Task<Integer> sendMessage(String paramString1, String paramString2, byte[] paramArrayOfByte)
  {
    return PendingResultUtil.toTask(this.zzei.sendMessage(asGoogleApiClient(), paramString1, paramString2, paramArrayOfByte), zzfa.zzbx);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzez.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */