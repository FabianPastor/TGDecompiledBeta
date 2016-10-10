package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.zzc;
import java.util.Collection;

public class GetServiceRequest
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<GetServiceRequest> CREATOR = new zzk();
  final int Ci;
  int Cj;
  String Ck;
  IBinder Cl;
  Scope[] Cm;
  Bundle Cn;
  Account Co;
  long Cp;
  final int version;
  
  public GetServiceRequest(int paramInt)
  {
    this.version = 3;
    this.Cj = zzc.GOOGLE_PLAY_SERVICES_VERSION_CODE;
    this.Ci = paramInt;
  }
  
  GetServiceRequest(int paramInt1, int paramInt2, int paramInt3, String paramString, IBinder paramIBinder, Scope[] paramArrayOfScope, Bundle paramBundle, Account paramAccount, long paramLong)
  {
    this.version = paramInt1;
    this.Ci = paramInt2;
    this.Cj = paramInt3;
    this.Ck = paramString;
    if (paramInt1 < 2) {}
    for (this.Co = zzdq(paramIBinder);; this.Co = paramAccount)
    {
      this.Cm = paramArrayOfScope;
      this.Cn = paramBundle;
      this.Cp = paramLong;
      return;
      this.Cl = paramIBinder;
    }
  }
  
  private Account zzdq(IBinder paramIBinder)
  {
    Account localAccount = null;
    if (paramIBinder != null) {
      localAccount = zza.zza(zzr.zza.zzdr(paramIBinder));
    }
    return localAccount;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzk.zza(this, paramParcel, paramInt);
  }
  
  public GetServiceRequest zzb(zzr paramzzr)
  {
    if (paramzzr != null) {
      this.Cl = paramzzr.asBinder();
    }
    return this;
  }
  
  public GetServiceRequest zzd(Account paramAccount)
  {
    this.Co = paramAccount;
    return this;
  }
  
  public GetServiceRequest zzf(Collection<Scope> paramCollection)
  {
    this.Cm = ((Scope[])paramCollection.toArray(new Scope[paramCollection.size()]));
    return this;
  }
  
  public GetServiceRequest zzht(String paramString)
  {
    this.Ck = paramString;
    return this;
  }
  
  public GetServiceRequest zzo(Bundle paramBundle)
  {
    this.Cn = paramBundle;
    return this;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/GetServiceRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */