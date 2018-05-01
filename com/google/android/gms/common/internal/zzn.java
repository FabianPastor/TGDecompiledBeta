package com.google.android.gms.common.internal;

import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;

public final class zzn
  extends zze
{
  private IBinder zzfze;
  
  public zzn(zzd paramzzd, int paramInt, IBinder paramIBinder, Bundle paramBundle)
  {
    super(paramzzd, paramInt, paramBundle);
    this.zzfze = paramIBinder;
  }
  
  protected final boolean zzakr()
  {
    do
    {
      try
      {
        String str1 = this.zzfze.getInterfaceDescriptor();
        if (!this.zzfza.zzhj().equals(str1))
        {
          String str2 = this.zzfza.zzhj();
          Log.e("GmsClient", String.valueOf(str2).length() + 34 + String.valueOf(str1).length() + "service descriptor mismatch: " + str2 + " vs. " + str1);
          return false;
        }
      }
      catch (RemoteException localRemoteException)
      {
        Log.w("GmsClient", "service probably died");
        return false;
      }
      localObject = this.zzfza.zzd(this.zzfze);
    } while ((localObject == null) || ((!zzd.zza(this.zzfza, 2, 4, (IInterface)localObject)) && (!zzd.zza(this.zzfza, 3, 4, (IInterface)localObject))));
    zzd.zza(this.zzfza, null);
    Object localObject = this.zzfza.zzafi();
    if (zzd.zze(this.zzfza) != null) {
      zzd.zze(this.zzfza).onConnected((Bundle)localObject);
    }
    return true;
  }
  
  protected final void zzj(ConnectionResult paramConnectionResult)
  {
    if (zzd.zzg(this.zzfza) != null) {
      zzd.zzg(this.zzfza).onConnectionFailed(paramConnectionResult);
    }
    this.zzfza.onConnectionFailed(paramConnectionResult);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */