package com.google.android.gms.signin.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.ResolveAccountResponse;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public class SignInResponse
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<SignInResponse> CREATOR = new zzi();
  private final ResolveAccountResponse aAn;
  final int mVersionCode;
  private final ConnectionResult vm;
  
  public SignInResponse(int paramInt)
  {
    this(new ConnectionResult(paramInt, null), null);
  }
  
  SignInResponse(int paramInt, ConnectionResult paramConnectionResult, ResolveAccountResponse paramResolveAccountResponse)
  {
    this.mVersionCode = paramInt;
    this.vm = paramConnectionResult;
    this.aAn = paramResolveAccountResponse;
  }
  
  public SignInResponse(ConnectionResult paramConnectionResult, ResolveAccountResponse paramResolveAccountResponse)
  {
    this(1, paramConnectionResult, paramResolveAccountResponse);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzi.zza(this, paramParcel, paramInt);
  }
  
  public ConnectionResult zzave()
  {
    return this.vm;
  }
  
  public ResolveAccountResponse zzcdl()
  {
    return this.aAn;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/signin/internal/SignInResponse.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */