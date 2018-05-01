package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

public class AuthAccountRequest
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<AuthAccountRequest> CREATOR = new AuthAccountRequestCreator();
  private final int zzal;
  @Deprecated
  private final IBinder zzqv;
  private final Scope[] zzqw;
  private Integer zzqx;
  private Integer zzqy;
  private Account zzs;
  
  AuthAccountRequest(int paramInt, IBinder paramIBinder, Scope[] paramArrayOfScope, Integer paramInteger1, Integer paramInteger2, Account paramAccount)
  {
    this.zzal = paramInt;
    this.zzqv = paramIBinder;
    this.zzqw = paramArrayOfScope;
    this.zzqx = paramInteger1;
    this.zzqy = paramInteger2;
    this.zzs = paramAccount;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 1, this.zzal);
    SafeParcelWriter.writeIBinder(paramParcel, 2, this.zzqv, false);
    SafeParcelWriter.writeTypedArray(paramParcel, 3, this.zzqw, paramInt, false);
    SafeParcelWriter.writeIntegerObject(paramParcel, 4, this.zzqx, false);
    SafeParcelWriter.writeIntegerObject(paramParcel, 5, this.zzqy, false);
    SafeParcelWriter.writeParcelable(paramParcel, 6, this.zzs, paramInt, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/AuthAccountRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */