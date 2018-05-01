package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

public final class PaymentMethodToken
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<PaymentMethodToken> CREATOR = new zzaf();
  private int zzeb;
  private String zzec;
  
  private PaymentMethodToken() {}
  
  PaymentMethodToken(int paramInt, String paramString)
  {
    this.zzeb = paramInt;
    this.zzec = paramString;
  }
  
  public final String getToken()
  {
    return this.zzec;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 2, this.zzeb);
    SafeParcelWriter.writeString(paramParcel, 3, this.zzec, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/PaymentMethodToken.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */