package com.google.android.gms.wearable.internal;

import android.os.ParcelFileDescriptor.AutoCloseInputStream;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.BaseImplementation.ResultHolder;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.wearable.Channel.GetInputStreamResult;

final class zzgs
  extends zzgm<Channel.GetInputStreamResult>
{
  private final zzbr zzeu;
  
  public zzgs(BaseImplementation.ResultHolder<Channel.GetInputStreamResult> paramResultHolder, zzbr paramzzbr)
  {
    super(paramResultHolder);
    this.zzeu = ((zzbr)Preconditions.checkNotNull(paramzzbr));
  }
  
  public final void zza(zzdm paramzzdm)
  {
    zzbj localzzbj = null;
    if (paramzzdm.zzdr != null)
    {
      localzzbj = new zzbj(new ParcelFileDescriptor.AutoCloseInputStream(paramzzdm.zzdr));
      this.zzeu.zza(new zzbk(localzzbj));
    }
    zza(new zzbg(new Status(paramzzdm.statusCode), localzzbj));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzgs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */