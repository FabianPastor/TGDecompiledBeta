package com.google.android.gms.internal;

import android.os.RemoteException;
import android.text.TextUtils;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

final class zzckq
  implements Runnable
{
  zzckq(zzckg paramzzckg, AtomicReference paramAtomicReference, String paramString1, String paramString2, String paramString3, zzcgi paramzzcgi) {}
  
  public final void run()
  {
    AtomicReference localAtomicReference = this.zzjik;
    for (;;)
    {
      try
      {
        localzzche = zzckg.zzd(this.zzjij);
        if (localzzche == null)
        {
          this.zzjij.zzawy().zzazd().zzd("Failed to get conditional properties", zzchm.zzjk(this.zzimf), this.zzjgq, this.zzjgr);
          this.zzjik.set(Collections.emptyList());
        }
      }
      catch (RemoteException localRemoteException)
      {
        zzche localzzche;
        this.zzjij.zzawy().zzazd().zzd("Failed to get conditional properties", zzchm.zzjk(this.zzimf), this.zzjgq, localRemoteException);
        this.zzjik.set(Collections.emptyList());
        this.zzjik.notify();
        continue;
      }
      finally
      {
        this.zzjik.notify();
      }
      try
      {
        this.zzjik.notify();
        return;
      }
      finally {}
    }
    if (TextUtils.isEmpty(this.zzimf)) {
      this.zzjik.set(localzzche.zza(this.zzjgq, this.zzjgr, this.zzjgn));
    }
    for (;;)
    {
      zzckg.zze(this.zzjij);
      this.zzjik.notify();
      return;
      this.zzjik.set(((zzche)localObject1).zzj(this.zzimf, this.zzjgq, this.zzjgr));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzckq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */