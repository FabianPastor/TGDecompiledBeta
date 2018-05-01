package com.google.android.gms.common.internal.safeparcel;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.ArrayList;

public class SafeParcelReader
{
  public static Bundle createBundle(Parcel paramParcel, int paramInt)
  {
    paramInt = readSize(paramParcel, paramInt);
    int i = paramParcel.dataPosition();
    if (paramInt == 0) {}
    Bundle localBundle;
    for (paramParcel = null;; paramParcel = localBundle)
    {
      return paramParcel;
      localBundle = paramParcel.readBundle();
      paramParcel.setDataPosition(paramInt + i);
    }
  }
  
  public static byte[] createByteArray(Parcel paramParcel, int paramInt)
  {
    int i = readSize(paramParcel, paramInt);
    paramInt = paramParcel.dataPosition();
    if (i == 0) {}
    byte[] arrayOfByte;
    for (paramParcel = null;; paramParcel = arrayOfByte)
    {
      return paramParcel;
      arrayOfByte = paramParcel.createByteArray();
      paramParcel.setDataPosition(i + paramInt);
    }
  }
  
  public static int[] createIntArray(Parcel paramParcel, int paramInt)
  {
    int i = readSize(paramParcel, paramInt);
    paramInt = paramParcel.dataPosition();
    if (i == 0) {}
    int[] arrayOfInt;
    for (paramParcel = null;; paramParcel = arrayOfInt)
    {
      return paramParcel;
      arrayOfInt = paramParcel.createIntArray();
      paramParcel.setDataPosition(i + paramInt);
    }
  }
  
  public static ArrayList<Integer> createIntegerList(Parcel paramParcel, int paramInt)
  {
    int i = readSize(paramParcel, paramInt);
    int j = paramParcel.dataPosition();
    if (i == 0) {}
    ArrayList localArrayList;
    for (paramParcel = null;; paramParcel = localArrayList)
    {
      return paramParcel;
      localArrayList = new ArrayList();
      int k = paramParcel.readInt();
      for (paramInt = 0; paramInt < k; paramInt++) {
        localArrayList.add(Integer.valueOf(paramParcel.readInt()));
      }
      paramParcel.setDataPosition(j + i);
    }
  }
  
  public static <T extends Parcelable> T createParcelable(Parcel paramParcel, int paramInt, Parcelable.Creator<T> paramCreator)
  {
    int i = readSize(paramParcel, paramInt);
    paramInt = paramParcel.dataPosition();
    if (i == 0) {}
    for (paramParcel = null;; paramParcel = paramCreator)
    {
      return paramParcel;
      paramCreator = (Parcelable)paramCreator.createFromParcel(paramParcel);
      paramParcel.setDataPosition(i + paramInt);
    }
  }
  
  public static String createString(Parcel paramParcel, int paramInt)
  {
    paramInt = readSize(paramParcel, paramInt);
    int i = paramParcel.dataPosition();
    if (paramInt == 0) {}
    String str;
    for (paramParcel = null;; paramParcel = str)
    {
      return paramParcel;
      str = paramParcel.readString();
      paramParcel.setDataPosition(paramInt + i);
    }
  }
  
  public static String[] createStringArray(Parcel paramParcel, int paramInt)
  {
    paramInt = readSize(paramParcel, paramInt);
    int i = paramParcel.dataPosition();
    if (paramInt == 0) {}
    String[] arrayOfString;
    for (paramParcel = null;; paramParcel = arrayOfString)
    {
      return paramParcel;
      arrayOfString = paramParcel.createStringArray();
      paramParcel.setDataPosition(paramInt + i);
    }
  }
  
  public static ArrayList<String> createStringList(Parcel paramParcel, int paramInt)
  {
    paramInt = readSize(paramParcel, paramInt);
    int i = paramParcel.dataPosition();
    if (paramInt == 0) {}
    ArrayList localArrayList;
    for (paramParcel = null;; paramParcel = localArrayList)
    {
      return paramParcel;
      localArrayList = paramParcel.createStringArrayList();
      paramParcel.setDataPosition(paramInt + i);
    }
  }
  
  public static <T> T[] createTypedArray(Parcel paramParcel, int paramInt, Parcelable.Creator<T> paramCreator)
  {
    int i = readSize(paramParcel, paramInt);
    paramInt = paramParcel.dataPosition();
    if (i == 0) {}
    for (paramParcel = null;; paramParcel = paramCreator)
    {
      return paramParcel;
      paramCreator = paramParcel.createTypedArray(paramCreator);
      paramParcel.setDataPosition(i + paramInt);
    }
  }
  
  public static <T> ArrayList<T> createTypedList(Parcel paramParcel, int paramInt, Parcelable.Creator<T> paramCreator)
  {
    int i = readSize(paramParcel, paramInt);
    paramInt = paramParcel.dataPosition();
    if (i == 0) {}
    for (paramParcel = null;; paramParcel = paramCreator)
    {
      return paramParcel;
      paramCreator = paramParcel.createTypedArrayList(paramCreator);
      paramParcel.setDataPosition(i + paramInt);
    }
  }
  
  public static void ensureAtEnd(Parcel paramParcel, int paramInt)
  {
    if (paramParcel.dataPosition() != paramInt) {
      throw new ParseException(37 + "Overread allowed size end=" + paramInt, paramParcel);
    }
  }
  
  public static int getFieldId(int paramInt)
  {
    return 0xFFFF & paramInt;
  }
  
  public static boolean readBoolean(Parcel paramParcel, int paramInt)
  {
    zza(paramParcel, paramInt, 4);
    if (paramParcel.readInt() != 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static byte readByte(Parcel paramParcel, int paramInt)
  {
    zza(paramParcel, paramInt, 4);
    return (byte)paramParcel.readInt();
  }
  
  public static double readDouble(Parcel paramParcel, int paramInt)
  {
    zza(paramParcel, paramInt, 8);
    return paramParcel.readDouble();
  }
  
  public static Double readDoubleObject(Parcel paramParcel, int paramInt)
  {
    int i = readSize(paramParcel, paramInt);
    if (i == 0) {}
    for (paramParcel = null;; paramParcel = Double.valueOf(paramParcel.readDouble()))
    {
      return paramParcel;
      zza(paramParcel, paramInt, i, 8);
    }
  }
  
  public static float readFloat(Parcel paramParcel, int paramInt)
  {
    zza(paramParcel, paramInt, 4);
    return paramParcel.readFloat();
  }
  
  public static Float readFloatObject(Parcel paramParcel, int paramInt)
  {
    int i = readSize(paramParcel, paramInt);
    if (i == 0) {}
    for (paramParcel = null;; paramParcel = Float.valueOf(paramParcel.readFloat()))
    {
      return paramParcel;
      zza(paramParcel, paramInt, i, 4);
    }
  }
  
  public static int readHeader(Parcel paramParcel)
  {
    return paramParcel.readInt();
  }
  
  public static IBinder readIBinder(Parcel paramParcel, int paramInt)
  {
    paramInt = readSize(paramParcel, paramInt);
    int i = paramParcel.dataPosition();
    if (paramInt == 0) {}
    IBinder localIBinder;
    for (paramParcel = null;; paramParcel = localIBinder)
    {
      return paramParcel;
      localIBinder = paramParcel.readStrongBinder();
      paramParcel.setDataPosition(paramInt + i);
    }
  }
  
  public static int readInt(Parcel paramParcel, int paramInt)
  {
    zza(paramParcel, paramInt, 4);
    return paramParcel.readInt();
  }
  
  public static Integer readIntegerObject(Parcel paramParcel, int paramInt)
  {
    int i = readSize(paramParcel, paramInt);
    if (i == 0) {}
    for (paramParcel = null;; paramParcel = Integer.valueOf(paramParcel.readInt()))
    {
      return paramParcel;
      zza(paramParcel, paramInt, i, 4);
    }
  }
  
  public static long readLong(Parcel paramParcel, int paramInt)
  {
    zza(paramParcel, paramInt, 8);
    return paramParcel.readLong();
  }
  
  public static Long readLongObject(Parcel paramParcel, int paramInt)
  {
    int i = readSize(paramParcel, paramInt);
    if (i == 0) {}
    for (paramParcel = null;; paramParcel = Long.valueOf(paramParcel.readLong()))
    {
      return paramParcel;
      zza(paramParcel, paramInt, i, 8);
    }
  }
  
  public static int readSize(Parcel paramParcel, int paramInt)
  {
    if ((paramInt & 0xFFFF0000) != -65536) {}
    for (paramInt = paramInt >> 16 & 0xFFFF;; paramInt = paramParcel.readInt()) {
      return paramInt;
    }
  }
  
  public static void skipUnknownField(Parcel paramParcel, int paramInt)
  {
    paramParcel.setDataPosition(readSize(paramParcel, paramInt) + paramParcel.dataPosition());
  }
  
  public static int validateObjectHeader(Parcel paramParcel)
  {
    int i = readHeader(paramParcel);
    int j = readSize(paramParcel, i);
    int k = paramParcel.dataPosition();
    if (getFieldId(i) != 20293)
    {
      String str = String.valueOf(Integer.toHexString(i));
      if (str.length() != 0) {}
      for (str = "Expected object header. Got 0x".concat(str);; str = new String("Expected object header. Got 0x")) {
        throw new ParseException(str, paramParcel);
      }
    }
    j = k + j;
    if ((j < k) || (j > paramParcel.dataSize())) {
      throw new ParseException(54 + "Size read is invalid start=" + k + " end=" + j, paramParcel);
    }
    return j;
  }
  
  private static void zza(Parcel paramParcel, int paramInt1, int paramInt2)
  {
    paramInt1 = readSize(paramParcel, paramInt1);
    if (paramInt1 != paramInt2)
    {
      String str = Integer.toHexString(paramInt1);
      throw new ParseException(String.valueOf(str).length() + 46 + "Expected size " + paramInt2 + " got " + paramInt1 + " (0x" + str + ")", paramParcel);
    }
  }
  
  private static void zza(Parcel paramParcel, int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt2 != paramInt3)
    {
      String str = Integer.toHexString(paramInt2);
      throw new ParseException(String.valueOf(str).length() + 46 + "Expected size " + paramInt3 + " got " + paramInt2 + " (0x" + str + ")", paramParcel);
    }
  }
  
  public static class ParseException
    extends RuntimeException
  {
    public ParseException(String paramString, Parcel paramParcel)
    {
      super();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/safeparcel/SafeParcelReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */