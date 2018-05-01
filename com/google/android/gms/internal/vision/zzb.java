package com.google.android.gms.internal.vision;

import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;

public class zzb
{
  private static final ClassLoader zzc = zzb.class.getClassLoader();
  
  public static void zza(Parcel paramParcel, IInterface paramIInterface)
  {
    if (paramIInterface == null) {
      paramParcel.writeStrongBinder(null);
    }
    for (;;)
    {
      return;
      paramParcel.writeStrongBinder(paramIInterface.asBinder());
    }
  }
  
  public static void zza(Parcel paramParcel, Parcelable paramParcelable)
  {
    if (paramParcelable == null) {
      paramParcel.writeInt(0);
    }
    for (;;)
    {
      return;
      paramParcel.writeInt(1);
      paramParcelable.writeToParcel(paramParcel, 0);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/vision/zzb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */