package com.google.android.gms.wearable.internal;

import android.os.ParcelFileDescriptor.AutoCloseInputStream;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.internal.zzbaz;
import com.google.android.gms.wearable.Channel.GetInputStreamResult;

final class zzfi
  extends zzfc<Channel.GetInputStreamResult>
{
  private final zzbd zzbTf;
  
  public zzfi(zzbaz<Channel.GetInputStreamResult> paramzzbaz, zzbd paramzzbd)
  {
    super(paramzzbaz);
    this.zzbTf = ((zzbd)zzbo.zzu(paramzzbd));
  }
  
  public final void zza(zzck paramzzck)
  {
    zzav localzzav = null;
    if (paramzzck.zzbSI != null)
    {
      localzzav = new zzav(new ParcelFileDescriptor.AutoCloseInputStream(paramzzck.zzbSI));
      this.zzbTf.zza(new zzaw(localzzav));
    }
    zzR(new zzas(new Status(paramzzck.statusCode), localzzav));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzfi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */