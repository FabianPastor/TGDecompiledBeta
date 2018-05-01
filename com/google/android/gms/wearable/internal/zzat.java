package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.wearable.Channel.GetOutputStreamResult;
import java.io.IOException;
import java.io.OutputStream;

final class zzat
  implements Channel.GetOutputStreamResult
{
  private final Status mStatus;
  private final OutputStream zzbSp;
  
  zzat(Status paramStatus, OutputStream paramOutputStream)
  {
    this.mStatus = ((Status)zzbo.zzu(paramStatus));
    this.zzbSp = paramOutputStream;
  }
  
  public final OutputStream getOutputStream()
  {
    return this.zzbSp;
  }
  
  public final Status getStatus()
  {
    return this.mStatus;
  }
  
  public final void release()
  {
    if (this.zzbSp != null) {}
    try
    {
      this.zzbSp.close();
      return;
    }
    catch (IOException localIOException) {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */