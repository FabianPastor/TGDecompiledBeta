package com.google.android.gms.signin.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ResolveAccountRequest;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

public class SignInRequest
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<SignInRequest> CREATOR = new SignInRequestCreator();
  private final ResolveAccountRequest zzadt;
  private final int zzal;
  
  SignInRequest(int paramInt, ResolveAccountRequest paramResolveAccountRequest)
  {
    this.zzal = paramInt;
    this.zzadt = paramResolveAccountRequest;
  }
  
  public SignInRequest(ResolveAccountRequest paramResolveAccountRequest)
  {
    this(1, paramResolveAccountRequest);
  }
  
  public ResolveAccountRequest getResolveAccountRequest()
  {
    return this.zzadt;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 1, this.zzal);
    SafeParcelWriter.writeParcelable(paramParcel, 2, getResolveAccountRequest(), paramInt, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/signin/internal/SignInRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */