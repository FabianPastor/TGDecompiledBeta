package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;

public abstract class zzctr
  extends zzee
  implements zzctq
{
  public zzctr()
  {
    attachInterface(this, "com.google.android.gms.signin.internal.ISignInCallbacks");
  }
  
  public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
    throws RemoteException
  {
    if (zza(paramInt1, paramParcel1, paramParcel2, paramInt2)) {
      return true;
    }
    switch (paramInt1)
    {
    case 5: 
    default: 
      return false;
    case 3: 
      zzef.zza(paramParcel1, ConnectionResult.CREATOR);
      zzef.zza(paramParcel1, zzctn.CREATOR);
    }
    for (;;)
    {
      paramParcel2.writeNoException();
      return true;
      zzef.zza(paramParcel1, Status.CREATOR);
      continue;
      zzef.zza(paramParcel1, Status.CREATOR);
      continue;
      zzef.zza(paramParcel1, Status.CREATOR);
      zzef.zza(paramParcel1, GoogleSignInAccount.CREATOR);
      continue;
      zzb((zzctx)zzef.zza(paramParcel1, zzctx.CREATOR));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzctr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */