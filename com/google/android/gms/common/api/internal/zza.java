package com.google.android.gms.common.api.internal;

import android.os.DeadObjectException;
import android.os.RemoteException;
import android.os.TransactionTooLargeException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.util.zzq;

public abstract class zza
{
  private int zzeie;
  
  public zza(int paramInt)
  {
    this.zzeie = paramInt;
  }
  
  private static Status zza(RemoteException paramRemoteException)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if ((zzq.zzamh()) && ((paramRemoteException instanceof TransactionTooLargeException))) {
      localStringBuilder.append("TransactionTooLargeException: ");
    }
    localStringBuilder.append(paramRemoteException.getLocalizedMessage());
    return new Status(8, localStringBuilder.toString());
  }
  
  public abstract void zza(zzae paramzzae, boolean paramBoolean);
  
  public abstract void zza(zzbo<?> paramzzbo)
    throws DeadObjectException;
  
  public abstract void zzs(Status paramStatus);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zza.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */