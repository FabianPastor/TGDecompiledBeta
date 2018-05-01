package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.Feature;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.SafeParcelReader;

public class GetServiceRequestCreator
  implements Parcelable.Creator<GetServiceRequest>
{
  public GetServiceRequest createFromParcel(Parcel paramParcel)
  {
    int i = SafeParcelReader.validateObjectHeader(paramParcel);
    boolean bool = false;
    Feature[] arrayOfFeature1 = null;
    Feature[] arrayOfFeature2 = null;
    Account localAccount = null;
    Bundle localBundle = null;
    Scope[] arrayOfScope = null;
    IBinder localIBinder = null;
    String str = null;
    int j = 0;
    int k = 0;
    int m = 0;
    while (paramParcel.dataPosition() < i)
    {
      int n = SafeParcelReader.readHeader(paramParcel);
      switch (SafeParcelReader.getFieldId(n))
      {
      case 9: 
      default: 
        SafeParcelReader.skipUnknownField(paramParcel, n);
        break;
      case 1: 
        m = SafeParcelReader.readInt(paramParcel, n);
        break;
      case 2: 
        k = SafeParcelReader.readInt(paramParcel, n);
        break;
      case 3: 
        j = SafeParcelReader.readInt(paramParcel, n);
        break;
      case 4: 
        str = SafeParcelReader.createString(paramParcel, n);
        break;
      case 5: 
        localIBinder = SafeParcelReader.readIBinder(paramParcel, n);
        break;
      case 6: 
        arrayOfScope = (Scope[])SafeParcelReader.createTypedArray(paramParcel, n, Scope.CREATOR);
        break;
      case 7: 
        localBundle = SafeParcelReader.createBundle(paramParcel, n);
        break;
      case 8: 
        localAccount = (Account)SafeParcelReader.createParcelable(paramParcel, n, Account.CREATOR);
        break;
      case 10: 
        arrayOfFeature2 = (Feature[])SafeParcelReader.createTypedArray(paramParcel, n, Feature.CREATOR);
        break;
      case 11: 
        arrayOfFeature1 = (Feature[])SafeParcelReader.createTypedArray(paramParcel, n, Feature.CREATOR);
        break;
      case 12: 
        bool = SafeParcelReader.readBoolean(paramParcel, n);
      }
    }
    SafeParcelReader.ensureAtEnd(paramParcel, i);
    return new GetServiceRequest(m, k, j, str, localIBinder, arrayOfScope, localBundle, localAccount, arrayOfFeature2, arrayOfFeature1, bool);
  }
  
  public GetServiceRequest[] newArray(int paramInt)
  {
    return new GetServiceRequest[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/GetServiceRequestCreator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */