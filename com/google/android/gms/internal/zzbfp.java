package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

public final class zzbfp
{
  public static void zza(Parcel paramParcel, int paramInt, byte paramByte)
  {
    zzb(paramParcel, paramInt, 4);
    paramParcel.writeInt(paramByte);
  }
  
  public static void zza(Parcel paramParcel, int paramInt, double paramDouble)
  {
    zzb(paramParcel, paramInt, 8);
    paramParcel.writeDouble(paramDouble);
  }
  
  public static void zza(Parcel paramParcel, int paramInt, float paramFloat)
  {
    zzb(paramParcel, paramInt, 4);
    paramParcel.writeFloat(paramFloat);
  }
  
  public static void zza(Parcel paramParcel, int paramInt, long paramLong)
  {
    zzb(paramParcel, paramInt, 8);
    paramParcel.writeLong(paramLong);
  }
  
  public static void zza(Parcel paramParcel, int paramInt, Bundle paramBundle, boolean paramBoolean)
  {
    if (paramBundle == null) {
      return;
    }
    paramInt = zzag(paramParcel, paramInt);
    paramParcel.writeBundle(paramBundle);
    zzah(paramParcel, paramInt);
  }
  
  public static void zza(Parcel paramParcel, int paramInt, IBinder paramIBinder, boolean paramBoolean)
  {
    if (paramIBinder == null) {
      return;
    }
    paramInt = zzag(paramParcel, paramInt);
    paramParcel.writeStrongBinder(paramIBinder);
    zzah(paramParcel, paramInt);
  }
  
  public static void zza(Parcel paramParcel, int paramInt1, Parcelable paramParcelable, int paramInt2, boolean paramBoolean)
  {
    if (paramParcelable == null)
    {
      if (paramBoolean) {
        zzb(paramParcel, paramInt1, 0);
      }
      return;
    }
    paramInt1 = zzag(paramParcel, paramInt1);
    paramParcelable.writeToParcel(paramParcel, paramInt2);
    zzah(paramParcel, paramInt1);
  }
  
  public static void zza(Parcel paramParcel, int paramInt, Double paramDouble, boolean paramBoolean)
  {
    if (paramDouble == null) {
      return;
    }
    zzb(paramParcel, paramInt, 8);
    paramParcel.writeDouble(paramDouble.doubleValue());
  }
  
  public static void zza(Parcel paramParcel, int paramInt, Float paramFloat, boolean paramBoolean)
  {
    if (paramFloat == null) {
      return;
    }
    zzb(paramParcel, paramInt, 4);
    paramParcel.writeFloat(paramFloat.floatValue());
  }
  
  public static void zza(Parcel paramParcel, int paramInt, Long paramLong, boolean paramBoolean)
  {
    if (paramLong == null) {
      return;
    }
    zzb(paramParcel, paramInt, 8);
    paramParcel.writeLong(paramLong.longValue());
  }
  
  public static void zza(Parcel paramParcel, int paramInt, String paramString, boolean paramBoolean)
  {
    if (paramString == null)
    {
      if (paramBoolean) {
        zzb(paramParcel, paramInt, 0);
      }
      return;
    }
    paramInt = zzag(paramParcel, paramInt);
    paramParcel.writeString(paramString);
    zzah(paramParcel, paramInt);
  }
  
  public static void zza(Parcel paramParcel, int paramInt, List<Integer> paramList, boolean paramBoolean)
  {
    if (paramList == null) {
      return;
    }
    int i = zzag(paramParcel, paramInt);
    int j = paramList.size();
    paramParcel.writeInt(j);
    paramInt = 0;
    while (paramInt < j)
    {
      paramParcel.writeInt(((Integer)paramList.get(paramInt)).intValue());
      paramInt += 1;
    }
    zzah(paramParcel, i);
  }
  
  public static void zza(Parcel paramParcel, int paramInt, boolean paramBoolean)
  {
    zzb(paramParcel, paramInt, 4);
    if (paramBoolean) {}
    for (paramInt = 1;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      return;
    }
  }
  
  public static void zza(Parcel paramParcel, int paramInt, byte[] paramArrayOfByte, boolean paramBoolean)
  {
    if (paramArrayOfByte == null)
    {
      if (paramBoolean) {
        zzb(paramParcel, paramInt, 0);
      }
      return;
    }
    paramInt = zzag(paramParcel, paramInt);
    paramParcel.writeByteArray(paramArrayOfByte);
    zzah(paramParcel, paramInt);
  }
  
  public static void zza(Parcel paramParcel, int paramInt, int[] paramArrayOfInt, boolean paramBoolean)
  {
    if (paramArrayOfInt == null) {
      return;
    }
    paramInt = zzag(paramParcel, paramInt);
    paramParcel.writeIntArray(paramArrayOfInt);
    zzah(paramParcel, paramInt);
  }
  
  public static <T extends Parcelable> void zza(Parcel paramParcel, int paramInt1, T[] paramArrayOfT, int paramInt2, boolean paramBoolean)
  {
    if (paramArrayOfT == null) {
      return;
    }
    int i = zzag(paramParcel, paramInt1);
    int j = paramArrayOfT.length;
    paramParcel.writeInt(j);
    paramInt1 = 0;
    if (paramInt1 < j)
    {
      T ? = paramArrayOfT[paramInt1];
      if (? == null) {
        paramParcel.writeInt(0);
      }
      for (;;)
      {
        paramInt1 += 1;
        break;
        zza(paramParcel, ?, paramInt2);
      }
    }
    zzah(paramParcel, i);
  }
  
  public static void zza(Parcel paramParcel, int paramInt, String[] paramArrayOfString, boolean paramBoolean)
  {
    if (paramArrayOfString == null) {
      return;
    }
    paramInt = zzag(paramParcel, paramInt);
    paramParcel.writeStringArray(paramArrayOfString);
    zzah(paramParcel, paramInt);
  }
  
  private static <T extends Parcelable> void zza(Parcel paramParcel, T paramT, int paramInt)
  {
    int i = paramParcel.dataPosition();
    paramParcel.writeInt(1);
    int j = paramParcel.dataPosition();
    paramT.writeToParcel(paramParcel, paramInt);
    paramInt = paramParcel.dataPosition();
    paramParcel.setDataPosition(i);
    paramParcel.writeInt(paramInt - j);
    paramParcel.setDataPosition(paramInt);
  }
  
  private static int zzag(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(0xFFFF0000 | paramInt);
    paramParcel.writeInt(0);
    return paramParcel.dataPosition();
  }
  
  private static void zzah(Parcel paramParcel, int paramInt)
  {
    int i = paramParcel.dataPosition();
    paramParcel.setDataPosition(paramInt - 4);
    paramParcel.writeInt(i - paramInt);
    paramParcel.setDataPosition(i);
  }
  
  public static void zzai(Parcel paramParcel, int paramInt)
  {
    zzah(paramParcel, paramInt);
  }
  
  private static void zzb(Parcel paramParcel, int paramInt1, int paramInt2)
  {
    if (paramInt2 >= 65535)
    {
      paramParcel.writeInt(0xFFFF0000 | paramInt1);
      paramParcel.writeInt(paramInt2);
      return;
    }
    paramParcel.writeInt(paramInt2 << 16 | paramInt1);
  }
  
  public static void zzc(Parcel paramParcel, int paramInt1, int paramInt2)
  {
    zzb(paramParcel, paramInt1, 4);
    paramParcel.writeInt(paramInt2);
  }
  
  public static <T extends Parcelable> void zzc(Parcel paramParcel, int paramInt, List<T> paramList, boolean paramBoolean)
  {
    if (paramList == null)
    {
      if (paramBoolean) {
        zzb(paramParcel, paramInt, 0);
      }
      return;
    }
    int i = zzag(paramParcel, paramInt);
    int j = paramList.size();
    paramParcel.writeInt(j);
    paramInt = 0;
    if (paramInt < j)
    {
      Parcelable localParcelable = (Parcelable)paramList.get(paramInt);
      if (localParcelable == null) {
        paramParcel.writeInt(0);
      }
      for (;;)
      {
        paramInt += 1;
        break;
        zza(paramParcel, localParcelable, 0);
      }
    }
    zzah(paramParcel, i);
  }
  
  public static int zze(Parcel paramParcel)
  {
    return zzag(paramParcel, 20293);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbfp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */