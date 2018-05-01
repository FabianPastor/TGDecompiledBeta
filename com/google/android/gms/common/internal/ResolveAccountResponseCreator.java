package com.google.android.gms.common.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.safeparcel.SafeParcelReader;

public class ResolveAccountResponseCreator
  implements Parcelable.Creator<ResolveAccountResponse>
{
  public ResolveAccountResponse createFromParcel(Parcel paramParcel)
  {
    int i = SafeParcelReader.validateObjectHeader(paramParcel);
    boolean bool1 = false;
    boolean bool2 = false;
    ConnectionResult localConnectionResult = null;
    IBinder localIBinder = null;
    int j = 0;
    while (paramParcel.dataPosition() < i)
    {
      int k = SafeParcelReader.readHeader(paramParcel);
      switch (SafeParcelReader.getFieldId(k))
      {
      default: 
        SafeParcelReader.skipUnknownField(paramParcel, k);
        break;
      case 1: 
        j = SafeParcelReader.readInt(paramParcel, k);
        break;
      case 2: 
        localIBinder = SafeParcelReader.readIBinder(paramParcel, k);
        break;
      case 3: 
        localConnectionResult = (ConnectionResult)SafeParcelReader.createParcelable(paramParcel, k, ConnectionResult.CREATOR);
        break;
      case 4: 
        bool2 = SafeParcelReader.readBoolean(paramParcel, k);
        break;
      case 5: 
        bool1 = SafeParcelReader.readBoolean(paramParcel, k);
      }
    }
    SafeParcelReader.ensureAtEnd(paramParcel, i);
    return new ResolveAccountResponse(j, localIBinder, localConnectionResult, bool2, bool1);
  }
  
  public ResolveAccountResponse[] newArray(int paramInt)
  {
    return new ResolveAccountResponse[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/ResolveAccountResponseCreator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */