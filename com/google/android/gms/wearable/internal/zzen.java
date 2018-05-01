package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.internal.wearable.zzb;
import com.google.android.gms.internal.wearable.zzc;

public abstract class zzen
  extends zzb
  implements zzem
{
  public zzen()
  {
    super("com.google.android.gms.wearable.internal.IWearableListener");
  }
  
  protected final boolean dispatchTransaction(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
    throws RemoteException
  {
    boolean bool;
    switch (paramInt1)
    {
    default: 
      bool = false;
      return bool;
    case 1: 
      zza((DataHolder)zzc.zza(paramParcel1, DataHolder.CREATOR));
    }
    for (;;)
    {
      bool = true;
      break;
      zza((zzfe)zzc.zza(paramParcel1, zzfe.CREATOR));
      continue;
      zza((zzfo)zzc.zza(paramParcel1, zzfo.CREATOR));
      continue;
      zzb((zzfo)zzc.zza(paramParcel1, zzfo.CREATOR));
      continue;
      onConnectedNodes(paramParcel1.createTypedArrayList(zzfo.CREATOR));
      continue;
      zza((zzl)zzc.zza(paramParcel1, zzl.CREATOR));
      continue;
      zza((zzaw)zzc.zza(paramParcel1, zzaw.CREATOR));
      continue;
      zza((zzah)zzc.zza(paramParcel1, zzah.CREATOR));
      continue;
      zza((zzi)zzc.zza(paramParcel1, zzi.CREATOR));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzen.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */