package com.google.android.gms.internal;

import android.os.RemoteException;
import android.text.TextUtils;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

final class zzckr
  implements Runnable
{
  zzckr(zzckg paramzzckg, AtomicReference paramAtomicReference, String paramString1, String paramString2, String paramString3, boolean paramBoolean, zzcgi paramzzcgi) {}
  
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
          this.zzjij.zzawy().zzazd().zzd("Failed to get user properties", zzchm.zzjk(this.zzimf), this.zzjgq, this.zzjgr);
          this.zzjik.set(Collections.emptyList());
        }
      }
      catch (RemoteException localRemoteException)
      {
        zzche localzzche;
        this.zzjij.zzawy().zzazd().zzd("Failed to get user properties", zzchm.zzjk(this.zzimf), this.zzjgq, localRemoteException);
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
      this.zzjik.set(localzzche.zza(this.zzjgq, this.zzjgr, this.zzjhf, this.zzjgn));
    }
    for (;;)
    {
      zzckg.zze(this.zzjij);
      this.zzjik.notify();
      return;
      this.zzjik.set(((zzche)localObject1).zza(this.zzimf, this.zzjgq, this.zzjgr, this.zzjhf));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzckr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */