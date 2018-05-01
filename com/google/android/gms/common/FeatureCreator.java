package com.google.android.gms.common;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.SafeParcelReader;

public class FeatureCreator
  implements Parcelable.Creator<Feature>
{
  public Feature createFromParcel(Parcel paramParcel)
  {
    int i = SafeParcelReader.validateObjectHeader(paramParcel);
    String str = null;
    int j = 0;
    long l = -1L;
    while (paramParcel.dataPosition() < i)
    {
      int k = SafeParcelReader.readHeader(paramParcel);
      switch (SafeParcelReader.getFieldId(k))
      {
      default: 
        SafeParcelReader.skipUnknownField(paramParcel, k);
        break;
      case 1: 
        str = SafeParcelReader.createString(paramParcel, k);
        break;
      case 2: 
        j = SafeParcelReader.readInt(paramParcel, k);
        break;
      case 3: 
        l = SafeParcelReader.readLong(paramParcel, k);
      }
    }
    SafeParcelReader.ensureAtEnd(paramParcel, i);
    return new Feature(str, j, l);
  }
  
  public Feature[] newArray(int paramInt)
  {
    return new Feature[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/FeatureCreator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */