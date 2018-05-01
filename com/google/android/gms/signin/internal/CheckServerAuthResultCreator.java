package com.google.android.gms.signin.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.SafeParcelReader;
import java.util.ArrayList;

public class CheckServerAuthResultCreator
  implements Parcelable.Creator<CheckServerAuthResult>
{
  public CheckServerAuthResult createFromParcel(Parcel paramParcel)
  {
    int i = SafeParcelReader.validateObjectHeader(paramParcel);
    ArrayList localArrayList = null;
    boolean bool = false;
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
        bool = SafeParcelReader.readBoolean(paramParcel, k);
        break;
      case 3: 
        localArrayList = SafeParcelReader.createTypedList(paramParcel, k, Scope.CREATOR);
      }
    }
    SafeParcelReader.ensureAtEnd(paramParcel, i);
    return new CheckServerAuthResult(j, bool, localArrayList);
  }
  
  public CheckServerAuthResult[] newArray(int paramInt)
  {
    return new CheckServerAuthResult[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/signin/internal/CheckServerAuthResultCreator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */