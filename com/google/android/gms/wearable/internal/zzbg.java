package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.wearable.Channel.GetInputStreamResult;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nullable;

final class zzbg
  implements Channel.GetInputStreamResult
{
  private final InputStream zzct;
  private final Status zzp;
  
  zzbg(Status paramStatus, @Nullable InputStream paramInputStream)
  {
    this.zzp = ((Status)Preconditions.checkNotNull(paramStatus));
    this.zzct = paramInputStream;
  }
  
  @Nullable
  public final InputStream getInputStream()
  {
    return this.zzct;
  }
  
  public final Status getStatus()
  {
    return this.zzp;
  }
  
  public final void release()
  {
    if (this.zzct != null) {}
    try
    {
      this.zzct.close();
      return;
    }
    catch (IOException localIOException)
    {
      for (;;) {}
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzbg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */