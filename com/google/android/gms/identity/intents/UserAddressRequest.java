package com.google.android.gms.identity.intents;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.identity.intents.model.CountrySpecification;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class UserAddressRequest
  extends zza
  implements ReflectedParcelable
{
  public static final Parcelable.Creator<UserAddressRequest> CREATOR = new zzd();
  List<CountrySpecification> zzbgB;
  
  UserAddressRequest() {}
  
  UserAddressRequest(List<CountrySpecification> paramList)
  {
    this.zzbgB = paramList;
  }
  
  public static Builder newBuilder()
  {
    UserAddressRequest localUserAddressRequest = new UserAddressRequest();
    localUserAddressRequest.getClass();
    return new Builder(null);
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = com.google.android.gms.common.internal.safeparcel.zzd.zze(paramParcel);
    com.google.android.gms.common.internal.safeparcel.zzd.zzc(paramParcel, 2, this.zzbgB, false);
    com.google.android.gms.common.internal.safeparcel.zzd.zzI(paramParcel, paramInt);
  }
  
  public final class Builder
  {
    private Builder() {}
    
    public final Builder addAllowedCountrySpecification(CountrySpecification paramCountrySpecification)
    {
      if (UserAddressRequest.this.zzbgB == null) {
        UserAddressRequest.this.zzbgB = new ArrayList();
      }
      UserAddressRequest.this.zzbgB.add(paramCountrySpecification);
      return this;
    }
    
    public final Builder addAllowedCountrySpecifications(Collection<CountrySpecification> paramCollection)
    {
      if (UserAddressRequest.this.zzbgB == null) {
        UserAddressRequest.this.zzbgB = new ArrayList();
      }
      UserAddressRequest.this.zzbgB.addAll(paramCollection);
      return this;
    }
    
    public final UserAddressRequest build()
    {
      if (UserAddressRequest.this.zzbgB != null) {
        UserAddressRequest.this.zzbgB = Collections.unmodifiableList(UserAddressRequest.this.zzbgB);
      }
      return UserAddressRequest.this;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/identity/intents/UserAddressRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */