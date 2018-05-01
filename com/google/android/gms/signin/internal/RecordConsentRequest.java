package com.google.android.gms.signin.internal;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

public class RecordConsentRequest
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<RecordConsentRequest> CREATOR = new RecordConsentRequestCreator();
  private final Scope[] zzadr;
  private final int zzal;
  private final Account zzs;
  private final String zzw;
  
  RecordConsentRequest(int paramInt, Account paramAccount, Scope[] paramArrayOfScope, String paramString)
  {
    this.zzal = paramInt;
    this.zzs = paramAccount;
    this.zzadr = paramArrayOfScope;
    this.zzw = paramString;
  }
  
  public Account getAccount()
  {
    return this.zzs;
  }
  
  public Scope[] getScopesToConsent()
  {
    return this.zzadr;
  }
  
  public String getServerClientId()
  {
    return this.zzw;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 1, this.zzal);
    SafeParcelWriter.writeParcelable(paramParcel, 2, getAccount(), paramInt, false);
    SafeParcelWriter.writeTypedArray(paramParcel, 3, getScopesToConsent(), paramInt, false);
    SafeParcelWriter.writeString(paramParcel, 4, getServerClientId(), false);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/signin/internal/RecordConsentRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */