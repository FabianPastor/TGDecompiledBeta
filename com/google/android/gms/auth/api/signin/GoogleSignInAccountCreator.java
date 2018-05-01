package com.google.android.gms.auth.api.signin;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.SafeParcelReader;
import java.util.ArrayList;

public class GoogleSignInAccountCreator
  implements Parcelable.Creator<GoogleSignInAccount>
{
  public GoogleSignInAccount createFromParcel(Parcel paramParcel)
  {
    int i = SafeParcelReader.validateObjectHeader(paramParcel);
    int j = 0;
    String str1 = null;
    String str2 = null;
    String str3 = null;
    String str4 = null;
    Uri localUri = null;
    String str5 = null;
    long l = 0L;
    String str6 = null;
    ArrayList localArrayList = null;
    String str7 = null;
    String str8 = null;
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
        str1 = SafeParcelReader.createString(paramParcel, k);
        break;
      case 3: 
        str2 = SafeParcelReader.createString(paramParcel, k);
        break;
      case 4: 
        str3 = SafeParcelReader.createString(paramParcel, k);
        break;
      case 5: 
        str4 = SafeParcelReader.createString(paramParcel, k);
        break;
      case 6: 
        localUri = (Uri)SafeParcelReader.createParcelable(paramParcel, k, Uri.CREATOR);
        break;
      case 7: 
        str5 = SafeParcelReader.createString(paramParcel, k);
        break;
      case 8: 
        l = SafeParcelReader.readLong(paramParcel, k);
        break;
      case 9: 
        str6 = SafeParcelReader.createString(paramParcel, k);
        break;
      case 10: 
        localArrayList = SafeParcelReader.createTypedList(paramParcel, k, Scope.CREATOR);
        break;
      case 11: 
        str7 = SafeParcelReader.createString(paramParcel, k);
        break;
      case 12: 
        str8 = SafeParcelReader.createString(paramParcel, k);
      }
    }
    SafeParcelReader.ensureAtEnd(paramParcel, i);
    return new GoogleSignInAccount(j, str1, str2, str3, str4, localUri, str5, l, str6, localArrayList, str7, str8);
  }
  
  public GoogleSignInAccount[] newArray(int paramInt)
  {
    return new GoogleSignInAccount[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/auth/api/signin/GoogleSignInAccountCreator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */