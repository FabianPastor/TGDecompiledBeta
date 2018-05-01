package com.google.android.gms.wallet;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

public final class PaymentMethodTokenizationParameters
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<PaymentMethodTokenizationParameters> CREATOR = new zzah();
  int zzeb;
  Bundle zzed = new Bundle();
  
  private PaymentMethodTokenizationParameters() {}
  
  PaymentMethodTokenizationParameters(int paramInt, Bundle paramBundle)
  {
    this.zzeb = paramInt;
    this.zzed = paramBundle;
  }
  
  public static Builder newBuilder()
  {
    return new Builder(new PaymentMethodTokenizationParameters(), null);
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 2, this.zzeb);
    SafeParcelWriter.writeBundle(paramParcel, 3, this.zzed, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
  
  public final class Builder
  {
    private Builder() {}
    
    public final Builder addParameter(String paramString1, String paramString2)
    {
      Preconditions.checkNotEmpty(paramString1, "Tokenization parameter name must not be empty");
      Preconditions.checkNotEmpty(paramString2, "Tokenization parameter value must not be empty");
      PaymentMethodTokenizationParameters.this.zzed.putString(paramString1, paramString2);
      return this;
    }
    
    public final PaymentMethodTokenizationParameters build()
    {
      return PaymentMethodTokenizationParameters.this;
    }
    
    public final Builder setPaymentMethodTokenizationType(int paramInt)
    {
      PaymentMethodTokenizationParameters.this.zzeb = paramInt;
      return this;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/PaymentMethodTokenizationParameters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */