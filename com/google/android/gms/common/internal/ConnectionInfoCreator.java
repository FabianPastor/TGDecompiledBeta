package com.google.android.gms.common.internal;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.Feature;
import com.google.android.gms.common.internal.safeparcel.SafeParcelReader;

public class ConnectionInfoCreator
  implements Parcelable.Creator<ConnectionInfo>
{
  public ConnectionInfo createFromParcel(Parcel paramParcel)
  {
    int i = SafeParcelReader.validateObjectHeader(paramParcel);
    Feature[] arrayOfFeature = null;
    Bundle localBundle = null;
    while (paramParcel.dataPosition() < i)
    {
      int j = SafeParcelReader.readHeader(paramParcel);
      switch (SafeParcelReader.getFieldId(j))
      {
      default: 
        SafeParcelReader.skipUnknownField(paramParcel, j);
        break;
      case 1: 
        localBundle = SafeParcelReader.createBundle(paramParcel, j);
        break;
      case 2: 
        arrayOfFeature = (Feature[])SafeParcelReader.createTypedArray(paramParcel, j, Feature.CREATOR);
      }
    }
    SafeParcelReader.ensureAtEnd(paramParcel, i);
    return new ConnectionInfo(localBundle, arrayOfFeature);
  }
  
  public ConnectionInfo[] newArray(int paramInt)
  {
    return new ConnectionInfo[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/ConnectionInfoCreator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */