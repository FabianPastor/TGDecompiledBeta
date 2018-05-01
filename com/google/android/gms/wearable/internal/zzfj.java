package com.google.android.gms.wearable.internal;

import android.os.ParcelFileDescriptor.AutoCloseOutputStream;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.internal.zzbaz;
import com.google.android.gms.wearable.Channel.GetOutputStreamResult;

final class zzfj
  extends zzfc<Channel.GetOutputStreamResult>
{
  private final zzbd zzbTf;
  
  public zzfj(zzbaz<Channel.GetOutputStreamResult> paramzzbaz, zzbd paramzzbd)
  {
    super(paramzzbaz);
    this.zzbTf = ((zzbd)zzbo.zzu(paramzzbd));
  }
  
  public final void zza(zzcm paramzzcm)
  {
    zzax localzzax = null;
    if (paramzzcm.zzbSI != null)
    {
      localzzax = new zzax(new ParcelFileDescriptor.AutoCloseOutputStream(paramzzcm.zzbSI));
      this.zzbTf.zza(new zzay(localzzax));
    }
    zzR(new zzat(new Status(paramzzcm.statusCode), localzzax));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzfj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */