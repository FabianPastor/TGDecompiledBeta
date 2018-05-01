package com.google.android.gms.signin.internal;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.SafeParcelReader;

public class RecordConsentRequestCreator
  implements Parcelable.Creator<RecordConsentRequest>
{
  public RecordConsentRequest createFromParcel(Parcel paramParcel)
  {
    int i = SafeParcelReader.validateObjectHeader(paramParcel);
    int j = 0;
    String str = null;
    Scope[] arrayOfScope = null;
    Account localAccount = null;
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
        localAccount = (Account)SafeParcelReader.createParcelable(paramParcel, k, Account.CREATOR);
        break;
      case 3: 
        arrayOfScope = (Scope[])SafeParcelReader.createTypedArray(paramParcel, k, Scope.CREATOR);
        break;
      case 4: 
        str = SafeParcelReader.createString(paramParcel, k);
      }
    }
    SafeParcelReader.ensureAtEnd(paramParcel, i);
    return new RecordConsentRequest(j, localAccount, arrayOfScope, str);
  }
  
  public RecordConsentRequest[] newArray(int paramInt)
  {
    return new RecordConsentRequest[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/signin/internal/RecordConsentRequestCreator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */