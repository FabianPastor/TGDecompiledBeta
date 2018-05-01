package com.google.android.gms.signin.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.ResolveAccountResponse;
import com.google.android.gms.common.internal.safeparcel.SafeParcelReader;

public class SignInResponseCreator
  implements Parcelable.Creator<SignInResponse>
{
  public SignInResponse createFromParcel(Parcel paramParcel)
  {
    int i = SafeParcelReader.validateObjectHeader(paramParcel);
    int j = 0;
    ResolveAccountResponse localResolveAccountResponse = null;
    ConnectionResult localConnectionResult = null;
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
        localConnectionResult = (ConnectionResult)SafeParcelReader.createParcelable(paramParcel, k, ConnectionResult.CREATOR);
        break;
      case 3: 
        localResolveAccountResponse = (ResolveAccountResponse)SafeParcelReader.createParcelable(paramParcel, k, ResolveAccountResponse.CREATOR);
      }
    }
    SafeParcelReader.ensureAtEnd(paramParcel, i);
    return new SignInResponse(j, localConnectionResult, localResolveAccountResponse);
  }
  
  public SignInResponse[] newArray(int paramInt)
  {
    return new SignInResponse[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/signin/internal/SignInResponseCreator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */