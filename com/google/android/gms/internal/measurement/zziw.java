package com.google.android.gms.internal.measurement;

import android.os.RemoteException;
import android.text.TextUtils;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

final class zziw
  implements Runnable
{
  zziw(zzil paramzzil, AtomicReference paramAtomicReference, String paramString1, String paramString2, String paramString3, boolean paramBoolean, zzec paramzzec) {}
  
  public final void run()
  {
    AtomicReference localAtomicReference = this.zzapz;
    for (;;)
    {
      try
      {
        localzzey = zzil.zzd(this.zzapy);
        if (localzzey == null)
        {
          this.zzapy.zzgg().zzil().zzd("Failed to get user properties", zzfg.zzbh(this.zzaoc), this.zzaoa, this.zzaob);
          this.zzapz.set(Collections.emptyList());
        }
      }
      catch (RemoteException localRemoteException)
      {
        zzey localzzey;
        this.zzapy.zzgg().zzil().zzd("Failed to get user properties", zzfg.zzbh(this.zzaoc), this.zzaoa, localRemoteException);
        this.zzapz.set(Collections.emptyList());
        this.zzapz.notify();
        continue;
      }
      finally
      {
        this.zzapz.notify();
      }
      try
      {
        this.zzapz.notify();
        return;
      }
      finally {}
    }
    if (TextUtils.isEmpty(this.zzaoc)) {
      this.zzapz.set(localzzey.zza(this.zzaoa, this.zzaob, this.zzaos, this.zzanq));
    }
    for (;;)
    {
      zzil.zze(this.zzapy);
      this.zzapz.notify();
      break;
      this.zzapz.set(((zzey)localObject1).zza(this.zzaoc, this.zzaoa, this.zzaob, this.zzaos));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zziw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */