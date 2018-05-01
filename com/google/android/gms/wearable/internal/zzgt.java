package com.google.android.gms.wearable.internal;

import android.os.ParcelFileDescriptor.AutoCloseOutputStream;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.BaseImplementation.ResultHolder;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.wearable.Channel.GetOutputStreamResult;

final class zzgt
  extends zzgm<Channel.GetOutputStreamResult>
{
  private final zzbr zzeu;
  
  public zzgt(BaseImplementation.ResultHolder<Channel.GetOutputStreamResult> paramResultHolder, zzbr paramzzbr)
  {
    super(paramResultHolder);
    this.zzeu = ((zzbr)Preconditions.checkNotNull(paramzzbr));
  }
  
  public final void zza(zzdo paramzzdo)
  {
    zzbl localzzbl = null;
    if (paramzzdo.zzdr != null)
    {
      localzzbl = new zzbl(new ParcelFileDescriptor.AutoCloseOutputStream(paramzzdo.zzdr));
      this.zzeu.zza(new zzbm(localzzbl));
    }
    zza(new zzbh(new Status(paramzzdo.statusCode), localzzbl));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzgt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */