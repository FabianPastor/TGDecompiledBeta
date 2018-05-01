package com.google.android.gms.internal.maps;

import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class zzc
{
  private static final ClassLoader zzd = zzc.class.getClassLoader();
  
  public static <T extends Parcelable> T zza(Parcel paramParcel, Parcelable.Creator<T> paramCreator)
  {
    if (paramParcel.readInt() == 0) {}
    for (paramParcel = null;; paramParcel = (Parcelable)paramCreator.createFromParcel(paramParcel)) {
      return paramParcel;
    }
  }
  
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
  
  public static void zza(Parcel paramParcel, boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      paramParcel.writeInt(i);
      return;
    }
  }
  
  public static boolean zza(Parcel paramParcel)
  {
    if (paramParcel.readInt() != 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/maps/zzc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */