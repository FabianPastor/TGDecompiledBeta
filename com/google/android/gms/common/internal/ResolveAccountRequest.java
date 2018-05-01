package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

public class ResolveAccountRequest
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<ResolveAccountRequest> CREATOR = new ResolveAccountRequestCreator();
  private final int zzal;
  private final Account zzs;
  private final int zzut;
  private final GoogleSignInAccount zzuu;
  
  ResolveAccountRequest(int paramInt1, Account paramAccount, int paramInt2, GoogleSignInAccount paramGoogleSignInAccount)
  {
    this.zzal = paramInt1;
    this.zzs = paramAccount;
    this.zzut = paramInt2;
    this.zzuu = paramGoogleSignInAccount;
  }
  
  public ResolveAccountRequest(Account paramAccount, int paramInt, GoogleSignInAccount paramGoogleSignInAccount)
  {
    this(2, paramAccount, paramInt, paramGoogleSignInAccount);
  }
  
  public Account getAccount()
  {
    return this.zzs;
  }
  
  public int getSessionId()
  {
    return this.zzut;
  }
  
  public GoogleSignInAccount getSignInAccountHint()
  {
    return this.zzuu;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 1, this.zzal);
    SafeParcelWriter.writeParcelable(paramParcel, 2, getAccount(), paramInt, false);
    SafeParcelWriter.writeInt(paramParcel, 3, getSessionId());
    SafeParcelWriter.writeParcelable(paramParcel, 4, getSignInAccountHint(), paramInt, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/ResolveAccountRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */