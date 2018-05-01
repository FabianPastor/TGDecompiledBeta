package com.google.android.gms.signin.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.ResolveAccountResponse;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

public class SignInResponse
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<SignInResponse> CREATOR = new SignInResponseCreator();
  private final ResolveAccountResponse zzadu;
  private final int zzal;
  private final ConnectionResult zzeu;
  
  public SignInResponse(int paramInt)
  {
    this(new ConnectionResult(paramInt, null), null);
  }
  
  SignInResponse(int paramInt, ConnectionResult paramConnectionResult, ResolveAccountResponse paramResolveAccountResponse)
  {
    this.zzal = paramInt;
    this.zzeu = paramConnectionResult;
    this.zzadu = paramResolveAccountResponse;
  }
  
  public SignInResponse(ConnectionResult paramConnectionResult, ResolveAccountResponse paramResolveAccountResponse)
  {
    this(1, paramConnectionResult, paramResolveAccountResponse);
  }
  
  public ConnectionResult getConnectionResult()
  {
    return this.zzeu;
  }
  
  public ResolveAccountResponse getResolveAccountResponse()
  {
    return this.zzadu;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 1, this.zzal);
    SafeParcelWriter.writeParcelable(paramParcel, 2, getConnectionResult(), paramInt, false);
    SafeParcelWriter.writeParcelable(paramParcel, 3, getResolveAccountResponse(), paramInt, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/signin/internal/SignInResponse.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */