package com.google.android.gms.signin.internal;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.SafeParcelReader;

public class AuthAccountResultCreator
  implements Parcelable.Creator<AuthAccountResult>
{
  public AuthAccountResult createFromParcel(Parcel paramParcel)
  {
    int i = SafeParcelReader.validateObjectHeader(paramParcel);
    Intent localIntent = null;
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
        localIntent = (Intent)SafeParcelReader.createParcelable(paramParcel, m, Intent.CREATOR);
      }
    }
    SafeParcelReader.ensureAtEnd(paramParcel, i);
    return new AuthAccountResult(k, j, localIntent);
  }
  
  public AuthAccountResult[] newArray(int paramInt)
  {
    return new AuthAccountResult[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/signin/internal/AuthAccountResultCreator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */