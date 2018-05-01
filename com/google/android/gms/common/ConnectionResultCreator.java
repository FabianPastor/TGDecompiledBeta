package com.google.android.gms.common;

import android.app.PendingIntent;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.SafeParcelReader;

public class ConnectionResultCreator
  implements Parcelable.Creator<ConnectionResult>
{
  public ConnectionResult createFromParcel(Parcel paramParcel)
  {
    int i = SafeParcelReader.validateObjectHeader(paramParcel);
    String str = null;
    PendingIntent localPendingIntent = null;
    int j = 0;
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
        k = SafeParcelReader.readInt(paramParcel, m);
        break;
      case 2: 
        j = SafeParcelReader.readInt(paramParcel, m);
        break;
      case 3: 
        localPendingIntent = (PendingIntent)SafeParcelReader.createParcelable(paramParcel, m, PendingIntent.CREATOR);
        break;
      case 4: 
        str = SafeParcelReader.createString(paramParcel, m);
      }
    }
    SafeParcelReader.ensureAtEnd(paramParcel, i);
    return new ConnectionResult(k, j, localPendingIntent, str);
  }
  
  public ConnectionResult[] newArray(int paramInt)
  {
    return new ConnectionResult[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/ConnectionResultCreator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */