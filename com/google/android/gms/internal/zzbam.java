package com.google.android.gms.internal;

import android.os.Build.VERSION;
import android.os.DeadObjectException;
import android.os.RemoteException;
import android.os.TransactionTooLargeException;
import android.support.annotation.NonNull;
import com.google.android.gms.common.api.Status;

public abstract class zzbam
{
  private int zzamr;
  
  public zzbam(int paramInt)
  {
    this.zzamr = paramInt;
  }
  
  private static Status zza(RemoteException paramRemoteException)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if ((Build.VERSION.SDK_INT >= 15) && ((paramRemoteException instanceof TransactionTooLargeException))) {
      localStringBuilder.append("TransactionTooLargeException: ");
    }
    localStringBuilder.append(paramRemoteException.getLocalizedMessage());
    return new Status(8, localStringBuilder.toString());
  }
  
  public abstract void zza(@NonNull zzbbt paramzzbbt, boolean paramBoolean);
  
  public abstract void zza(zzbdd<?> paramzzbdd)
    throws DeadObjectException;
  
  public abstract void zzp(@NonNull Status paramStatus);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbam.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */