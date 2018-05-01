package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.internal.BaseImplementation.ResultHolder;
import com.google.android.gms.wearable.MessageApi.SendMessageResult;

final class zzhe
  extends zzgm<MessageApi.SendMessageResult>
{
  public zzhe(BaseImplementation.ResultHolder<MessageApi.SendMessageResult> paramResultHolder)
  {
    super(paramResultHolder);
  }
  
  public final void zza(zzga paramzzga)
  {
    zza(new zzey(zzgd.zzb(paramzzga.statusCode), paramzzga.zzeh));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzhe.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */