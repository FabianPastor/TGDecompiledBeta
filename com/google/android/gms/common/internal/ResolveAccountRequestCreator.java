package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.internal.safeparcel.SafeParcelReader;

public class ResolveAccountRequestCreator
  implements Parcelable.Creator<ResolveAccountRequest>
{
  public ResolveAccountRequest createFromParcel(Parcel paramParcel)
  {
    int i = SafeParcelReader.validateObjectHeader(paramParcel);
    GoogleSignInAccount localGoogleSignInAccount = null;
    int j = 0;
    Account localAccount = null;
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
        localAccount = (Account)SafeParcelReader.createParcelable(paramParcel, m, Account.CREATOR);
        break;
      case 3: 
        j = SafeParcelReader.readInt(paramParcel, m);
        break;
      case 4: 
        localGoogleSignInAccount = (GoogleSignInAccount)SafeParcelReader.createParcelable(paramParcel, m, GoogleSignInAccount.CREATOR);
      }
    }
    SafeParcelReader.ensureAtEnd(paramParcel, i);
    return new ResolveAccountRequest(k, localAccount, j, localGoogleSignInAccount);
  }
  
  public ResolveAccountRequest[] newArray(int paramInt)
  {
    return new ResolveAccountRequest[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/ResolveAccountRequestCreator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */