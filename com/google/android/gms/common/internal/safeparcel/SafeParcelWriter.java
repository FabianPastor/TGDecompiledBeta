package com.google.android.gms.common.internal.safeparcel;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

public class SafeParcelWriter
{
  public static int beginObjectHeader(Parcel paramParcel)
  {
    return zza(paramParcel, 20293);
  }
  
  public static void finishObjectHeader(Parcel paramParcel, int paramInt)
  {
    zzb(paramParcel, paramInt);
  }
  
  public static void writeBoolean(Parcel paramParcel, int paramInt, boolean paramBoolean)
  {
    zzb(paramParcel, paramInt, 4);
    if (paramBoolean) {}
    for (paramInt = 1;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      return;
    }
  }
  
  public static void writeBundle(Parcel paramParcel, int paramInt, Bundle paramBundle, boolean paramBoolean)
  {
    if (paramBundle == null) {
      if (paramBoolean) {
        zzb(paramParcel, paramInt, 0);
      }
    }
    for (;;)
    {
      return;
      paramInt = zza(paramParcel, paramInt);
      paramParcel.writeBundle(paramBundle);
      zzb(paramParcel, paramInt);
    }
  }
  
  public static void writeByte(Parcel paramParcel, int paramInt, byte paramByte)
  {
    zzb(paramParcel, paramInt, 4);
    paramParcel.writeInt(paramByte);
  }
  
  public static void writeByteArray(Parcel paramParcel, int paramInt, byte[] paramArrayOfByte, boolean paramBoolean)
  {
    if (paramArrayOfByte == null) {
      if (paramBoolean) {
        zzb(paramParcel, paramInt, 0);
      }
    }
    for (;;)
    {
      return;
      paramInt = zza(paramParcel, paramInt);
      paramParcel.writeByteArray(paramArrayOfByte);
      zzb(paramParcel, paramInt);
    }
  }
  
  public static void writeDouble(Parcel paramParcel, int paramInt, double paramDouble)
  {
    zzb(paramParcel, paramInt, 8);
    paramParcel.writeDouble(paramDouble);
  }
  
  public static void writeDoubleObject(Parcel paramParcel, int paramInt, Double paramDouble, boolean paramBoolean)
  {
    if (paramDouble == null) {
      if (paramBoolean) {
        zzb(paramParcel, paramInt, 0);
      }
    }
    for (;;)
    {
      return;
      zzb(paramParcel, paramInt, 8);
      paramParcel.writeDouble(paramDouble.doubleValue());
    }
  }
  
  public static void writeFloat(Parcel paramParcel, int paramInt, float paramFloat)
  {
    zzb(paramParcel, paramInt, 4);
    paramParcel.writeFloat(paramFloat);
  }
  
  public static void writeFloatObject(Parcel paramParcel, int paramInt, Float paramFloat, boolean paramBoolean)
  {
    if (paramFloat == null) {
      if (paramBoolean) {
        zzb(paramParcel, paramInt, 0);
      }
    }
    for (;;)
    {
      return;
      zzb(paramParcel, paramInt, 4);
      paramParcel.writeFloat(paramFloat.floatValue());
    }
  }
  
  public static void writeIBinder(Parcel paramParcel, int paramInt, IBinder paramIBinder, boolean paramBoolean)
  {
    if (paramIBinder == null) {
      if (paramBoolean) {
        zzb(paramParcel, paramInt, 0);
      }
    }
    for (;;)
    {
      return;
      paramInt = zza(paramParcel, paramInt);
      paramParcel.writeStrongBinder(paramIBinder);
      zzb(paramParcel, paramInt);
    }
  }
  
  public static void writeInt(Parcel paramParcel, int paramInt1, int paramInt2)
  {
    zzb(paramParcel, paramInt1, 4);
    paramParcel.writeInt(paramInt2);
  }
  
  public static void writeIntArray(Parcel paramParcel, int paramInt, int[] paramArrayOfInt, boolean paramBoolean)
  {
    if (paramArrayOfInt == null) {
      if (paramBoolean) {
        zzb(paramParcel, paramInt, 0);
      }
    }
    for (;;)
    {
      return;
      paramInt = zza(paramParcel, paramInt);
      paramParcel.writeIntArray(paramArrayOfInt);
      zzb(paramParcel, paramInt);
    }
  }
  
  public static void writeIntegerList(Parcel paramParcel, int paramInt, List<Integer> paramList, boolean paramBoolean)
  {
    if (paramList == null) {
      if (paramBoolean) {
        zzb(paramParcel, paramInt, 0);
      }
    }
    for (;;)
    {
      return;
      int i = zza(paramParcel, paramInt);
      int j = paramList.size();
      paramParcel.writeInt(j);
      for (paramInt = 0; paramInt < j; paramInt++) {
        paramParcel.writeInt(((Integer)paramList.get(paramInt)).intValue());
      }
      zzb(paramParcel, i);
    }
  }
  
  public static void writeIntegerObject(Parcel paramParcel, int paramInt, Integer paramInteger, boolean paramBoolean)
  {
    if (paramInteger == null) {
      if (paramBoolean) {
        zzb(paramParcel, paramInt, 0);
      }
    }
    for (;;)
    {
      return;
      zzb(paramParcel, paramInt, 4);
      paramParcel.writeInt(paramInteger.intValue());
    }
  }
  
  public static void writeLong(Parcel paramParcel, int paramInt, long paramLong)
  {
    zzb(paramParcel, paramInt, 8);
    paramParcel.writeLong(paramLong);
  }
  
  public static void writeLongObject(Parcel paramParcel, int paramInt, Long paramLong, boolean paramBoolean)
  {
    if (paramLong == null) {
      if (paramBoolean) {
        zzb(paramParcel, paramInt, 0);
      }
    }
    for (;;)
    {
      return;
      zzb(paramParcel, paramInt, 8);
      paramParcel.writeLong(paramLong.longValue());
    }
  }
  
  public static void writeParcelable(Parcel paramParcel, int paramInt1, Parcelable paramParcelable, int paramInt2, boolean paramBoolean)
  {
    if (paramParcelable == null) {
      if (paramBoolean) {
        zzb(paramParcel, paramInt1, 0);
      }
    }
    for (;;)
    {
      return;
      paramInt1 = zza(paramParcel, paramInt1);
      paramParcelable.writeToParcel(paramParcel, paramInt2);
      zzb(paramParcel, paramInt1);
    }
  }
  
  public static void writeString(Parcel paramParcel, int paramInt, String paramString, boolean paramBoolean)
  {
    if (paramString == null) {
      if (paramBoolean) {
        zzb(paramParcel, paramInt, 0);
      }
    }
    for (;;)
    {
      return;
      paramInt = zza(paramParcel, paramInt);
      paramParcel.writeString(paramString);
      zzb(paramParcel, paramInt);
    }
  }
  
  public static void writeStringArray(Parcel paramParcel, int paramInt, String[] paramArrayOfString, boolean paramBoolean)
  {
    if (paramArrayOfString == null) {
      if (paramBoolean) {
        zzb(paramParcel, paramInt, 0);
      }
    }
    for (;;)
    {
      return;
      paramInt = zza(paramParcel, paramInt);
      paramParcel.writeStringArray(paramArrayOfString);
      zzb(paramParcel, paramInt);
    }
  }
  
  public static void writeStringList(Parcel paramParcel, int paramInt, List<String> paramList, boolean paramBoolean)
  {
    if (paramList == null) {
      if (paramBoolean) {
        zzb(paramParcel, paramInt, 0);
      }
    }
    for (;;)
    {
      return;
      paramInt = zza(paramParcel, paramInt);
      paramParcel.writeStringList(paramList);
      zzb(paramParcel, paramInt);
    }
  }
  
  public static <T extends Parcelable> void writeTypedArray(Parcel paramParcel, int paramInt1, T[] paramArrayOfT, int paramInt2, boolean paramBoolean)
  {
    if (paramArrayOfT == null) {
      if (paramBoolean) {
        zzb(paramParcel, paramInt1, 0);
      }
    }
    for (;;)
    {
      return;
      int i = zza(paramParcel, paramInt1);
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
          paramInt1++;
          break;
          zza(paramParcel, ?, paramInt2);
        }
      }
      zzb(paramParcel, i);
    }
  }
  
  public static <T extends Parcelable> void writeTypedList(Parcel paramParcel, int paramInt, List<T> paramList, boolean paramBoolean)
  {
    if (paramList == null) {
      if (paramBoolean) {
        zzb(paramParcel, paramInt, 0);
      }
    }
    for (;;)
    {
      return;
      int i = zza(paramParcel, paramInt);
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
          paramInt++;
          break;
          zza(paramParcel, localParcelable, 0);
        }
      }
      zzb(paramParcel, i);
    }
  }
  
  private static int zza(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(0xFFFF0000 | paramInt);
    paramParcel.writeInt(0);
    return paramParcel.dataPosition();
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
  
  private static void zzb(Parcel paramParcel, int paramInt)
  {
    int i = paramParcel.dataPosition();
    paramParcel.setDataPosition(paramInt - 4);
    paramParcel.writeInt(i - paramInt);
    paramParcel.setDataPosition(i);
  }
  
  private static void zzb(Parcel paramParcel, int paramInt1, int paramInt2)
  {
    if (paramInt2 >= 65535)
    {
      paramParcel.writeInt(0xFFFF0000 | paramInt1);
      paramParcel.writeInt(paramInt2);
    }
    for (;;)
    {
      return;
      paramParcel.writeInt(paramInt2 << 16 | paramInt1);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/safeparcel/SafeParcelWriter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */