package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.Feature;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import java.util.Collection;

public class GetServiceRequest
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<GetServiceRequest> CREATOR = new GetServiceRequestCreator();
  private final int version;
  private final int zzst;
  private int zzsu;
  private String zzsv;
  private IBinder zzsw;
  private Scope[] zzsx;
  private Bundle zzsy;
  private Account zzsz;
  private Feature[] zzta;
  private Feature[] zztb;
  private boolean zztc;
  
  public GetServiceRequest(int paramInt)
  {
    this.version = 4;
    this.zzsu = GoogleApiAvailabilityLight.GOOGLE_PLAY_SERVICES_VERSION_CODE;
    this.zzst = paramInt;
    this.zztc = true;
  }
  
  GetServiceRequest(int paramInt1, int paramInt2, int paramInt3, String paramString, IBinder paramIBinder, Scope[] paramArrayOfScope, Bundle paramBundle, Account paramAccount, Feature[] paramArrayOfFeature1, Feature[] paramArrayOfFeature2, boolean paramBoolean)
  {
    this.version = paramInt1;
    this.zzst = paramInt2;
    this.zzsu = paramInt3;
    if ("com.google.android.gms".equals(paramString))
    {
      this.zzsv = "com.google.android.gms";
      if (paramInt1 >= 2) {
        break label89;
      }
    }
    for (this.zzsz = zzb(paramIBinder);; this.zzsz = paramAccount)
    {
      this.zzsx = paramArrayOfScope;
      this.zzsy = paramBundle;
      this.zzta = paramArrayOfFeature1;
      this.zztb = paramArrayOfFeature2;
      this.zztc = paramBoolean;
      return;
      this.zzsv = paramString;
      break;
      label89:
      this.zzsw = paramIBinder;
    }
  }
  
  private static Account zzb(IBinder paramIBinder)
  {
    Account localAccount = null;
    if (paramIBinder != null) {
      localAccount = AccountAccessor.getAccountBinderSafe(IAccountAccessor.Stub.asInterface(paramIBinder));
    }
    return localAccount;
  }
  
  public GetServiceRequest setAuthenticatedAccount(IAccountAccessor paramIAccountAccessor)
  {
    if (paramIAccountAccessor != null) {
      this.zzsw = paramIAccountAccessor.asBinder();
    }
    return this;
  }
  
  public GetServiceRequest setCallingPackage(String paramString)
  {
    this.zzsv = paramString;
    return this;
  }
  
  public GetServiceRequest setClientApiFeatures(Feature[] paramArrayOfFeature)
  {
    this.zztb = paramArrayOfFeature;
    return this;
  }
  
  public GetServiceRequest setClientRequestedAccount(Account paramAccount)
  {
    this.zzsz = paramAccount;
    return this;
  }
  
  public GetServiceRequest setClientRequiredFeatures(Feature[] paramArrayOfFeature)
  {
    this.zzta = paramArrayOfFeature;
    return this;
  }
  
  public GetServiceRequest setExtraArgs(Bundle paramBundle)
  {
    this.zzsy = paramBundle;
    return this;
  }
  
  public GetServiceRequest setScopes(Collection<Scope> paramCollection)
  {
    this.zzsx = ((Scope[])paramCollection.toArray(new Scope[paramCollection.size()]));
    return this;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 1, this.version);
    SafeParcelWriter.writeInt(paramParcel, 2, this.zzst);
    SafeParcelWriter.writeInt(paramParcel, 3, this.zzsu);
    SafeParcelWriter.writeString(paramParcel, 4, this.zzsv, false);
    SafeParcelWriter.writeIBinder(paramParcel, 5, this.zzsw, false);
    SafeParcelWriter.writeTypedArray(paramParcel, 6, this.zzsx, paramInt, false);
    SafeParcelWriter.writeBundle(paramParcel, 7, this.zzsy, false);
    SafeParcelWriter.writeParcelable(paramParcel, 8, this.zzsz, paramInt, false);
    SafeParcelWriter.writeTypedArray(paramParcel, 10, this.zzta, paramInt, false);
    SafeParcelWriter.writeTypedArray(paramParcel, 11, this.zztb, paramInt, false);
    SafeParcelWriter.writeBoolean(paramParcel, 12, this.zztc);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/GetServiceRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */