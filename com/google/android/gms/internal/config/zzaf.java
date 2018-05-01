package com.google.android.gms.internal.config;

import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.common.api.Status;
import java.util.Map;

public abstract interface zzaf
  extends IInterface
{
  public abstract void zza(Status paramStatus)
    throws RemoteException;
  
  public abstract void zza(Status paramStatus, zzad paramzzad)
    throws RemoteException;
  
  public abstract void zza(Status paramStatus, Map paramMap)
    throws RemoteException;
  
  public abstract void zza(Status paramStatus, byte[] paramArrayOfByte)
    throws RemoteException;
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/config/zzaf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */