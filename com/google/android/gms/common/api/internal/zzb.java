package com.google.android.gms.common.api.internal;

import android.os.DeadObjectException;
import android.os.RemoteException;
import android.os.TransactionTooLargeException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.util.PlatformVersion;

public abstract class zzb
{
  private final int type;
  
  public zzb(int paramInt)
  {
    this.type = paramInt;
  }
  
  private static Status zza(RemoteException paramRemoteException)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if ((PlatformVersion.isAtLeastIceCreamSandwichMR1()) && ((paramRemoteException instanceof TransactionTooLargeException))) {
      localStringBuilder.append("TransactionTooLargeException: ");
    }
    localStringBuilder.append(paramRemoteException.getLocalizedMessage());
    return new Status(8, localStringBuilder.toString());
  }
  
  public abstract void zza(Status paramStatus);
  
  public abstract void zza(GoogleApiManager.zza<?> paramzza)
    throws DeadObjectException;
  
  public abstract void zza(zzaa paramzzaa, boolean paramBoolean);
  
  public abstract void zza(RuntimeException paramRuntimeException);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */