package com.google.android.gms.signin.internal;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public class RecordConsentRequest
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<RecordConsentRequest> CREATOR = new zzf();
  private final Scope[] aAj;
  private final Account ec;
  private final String hk;
  final int mVersionCode;
  
  RecordConsentRequest(int paramInt, Account paramAccount, Scope[] paramArrayOfScope, String paramString)
  {
    this.mVersionCode = paramInt;
    this.ec = paramAccount;
    this.aAj = paramArrayOfScope;
    this.hk = paramString;
  }
  
  public Account getAccount()
  {
    return this.ec;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzf.zza(this, paramParcel, paramInt);
  }
  
  public String zzahn()
  {
    return this.hk;
  }
  
  public Scope[] zzcdi()
  {
    return this.aAj;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/signin/internal/RecordConsentRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */