package com.google.android.gms.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.Status;

final class zzbft
  extends zzbfn
{
  private final zzbaz<Status> zzaIz;
  
  public zzbft(zzbaz<Status> paramzzbaz)
  {
    this.zzaIz = paramzzbaz;
  }
  
  public final void zzaC(int paramInt)
    throws RemoteException
  {
    this.zzaIz.setResult(new Status(paramInt));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbft.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */