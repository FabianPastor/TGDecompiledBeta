package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.wearable.Channel.GetOutputStreamResult;
import java.io.IOException;
import java.io.OutputStream;
import javax.annotation.Nullable;

final class zzbh
  implements Channel.GetOutputStreamResult
{
  private final OutputStream zzcu;
  private final Status zzp;
  
  zzbh(Status paramStatus, @Nullable OutputStream paramOutputStream)
  {
    this.zzp = ((Status)Preconditions.checkNotNull(paramStatus));
    this.zzcu = paramOutputStream;
  }
  
  @Nullable
  public final OutputStream getOutputStream()
  {
    return this.zzcu;
  }
  
  public final Status getStatus()
  {
    return this.zzp;
  }
  
  public final void release()
  {
    if (this.zzcu != null) {}
    try
    {
      this.zzcu.close();
      return;
    }
    catch (IOException localIOException)
    {
      for (;;) {}
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzbh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */