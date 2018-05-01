package com.google.android.gms.internal;

final class zzcks
  implements Runnable
{
  zzcks(zzckg paramzzckg, boolean paramBoolean, zzcln paramzzcln, zzcgi paramzzcgi) {}
  
  public final void run()
  {
    zzche localzzche = zzckg.zzd(this.zzjij);
    if (localzzche == null)
    {
      this.zzjij.zzawy().zzazd().log("Discarding data. Failed to set user attribute");
      return;
    }
    zzckg localzzckg = this.zzjij;
    if (this.zzjin) {}
    for (Object localObject = null;; localObject = this.zzjgt)
    {
      localzzckg.zza(localzzche, (zzbfm)localObject, this.zzjgn);
      zzckg.zze(this.zzjij);
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcks.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */