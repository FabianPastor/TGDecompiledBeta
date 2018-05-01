package com.google.android.gms.common.data;

import android.database.CursorWindow;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.SafeParcelReader;

public class DataHolderCreator
  implements Parcelable.Creator<DataHolder>
{
  public DataHolder createFromParcel(Parcel paramParcel)
  {
    int i = SafeParcelReader.validateObjectHeader(paramParcel);
    Bundle localBundle = null;
    int j = 0;
    CursorWindow[] arrayOfCursorWindow = null;
    String[] arrayOfString = null;
    int k = 0;
    while (paramParcel.dataPosition() < i)
    {
      int m = SafeParcelReader.readHeader(paramParcel);
      switch (SafeParcelReader.getFieldId(m))
      {
      default: 
        SafeParcelReader.skipUnknownField(paramParcel, m);
        break;
      case 1: 
        arrayOfString = SafeParcelReader.createStringArray(paramParcel, m);
        break;
      case 2: 
        arrayOfCursorWindow = (CursorWindow[])SafeParcelReader.createTypedArray(paramParcel, m, CursorWindow.CREATOR);
        break;
      case 3: 
        j = SafeParcelReader.readInt(paramParcel, m);
        break;
      case 4: 
        localBundle = SafeParcelReader.createBundle(paramParcel, m);
        break;
      case 1000: 
        k = SafeParcelReader.readInt(paramParcel, m);
      }
    }
    SafeParcelReader.ensureAtEnd(paramParcel, i);
    paramParcel = new DataHolder(k, arrayOfString, arrayOfCursorWindow, j, localBundle);
    paramParcel.validateContents();
    return paramParcel;
  }
  
  public DataHolder[] newArray(int paramInt)
  {
    return new DataHolder[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/data/DataHolderCreator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */