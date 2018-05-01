package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.SafeParcelReader;

public class AuthAccountRequestCreator
  implements Parcelable.Creator<AuthAccountRequest>
{
  public AuthAccountRequest createFromParcel(Parcel paramParcel)
  {
    int i = SafeParcelReader.validateObjectHeader(paramParcel);
    int j = 0;
    Account localAccount = null;
    Integer localInteger1 = null;
    Integer localInteger2 = null;
    Scope[] arrayOfScope = null;
    IBinder localIBinder = null;
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
        arrayOfScope = (Scope[])SafeParcelReader.createTypedArray(paramParcel, k, Scope.CREATOR);
        break;
      case 4: 
        localInteger2 = SafeParcelReader.readIntegerObject(paramParcel, k);
        break;
      case 5: 
        localInteger1 = SafeParcelReader.readIntegerObject(paramParcel, k);
        break;
      case 6: 
        localAccount = (Account)SafeParcelReader.createParcelable(paramParcel, k, Account.CREATOR);
      }
    }
    SafeParcelReader.ensureAtEnd(paramParcel, i);
    return new AuthAccountRequest(j, localIBinder, arrayOfScope, localInteger2, localInteger1, localAccount);
  }
  
  public AuthAccountRequest[] newArray(int paramInt)
  {
    return new AuthAccountRequest[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/AuthAccountRequestCreator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */