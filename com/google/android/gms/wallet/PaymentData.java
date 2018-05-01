package com.google.android.gms.wallet;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.identity.intents.model.UserAddress;

public final class PaymentData
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<PaymentData> CREATOR = new zzac();
  private String zzaw;
  private String zzaz;
  private PaymentMethodToken zzbg;
  private String zzby;
  private CardInfo zzds;
  private UserAddress zzdt;
  private Bundle zzdu;
  
  private PaymentData() {}
  
  PaymentData(String paramString1, CardInfo paramCardInfo, UserAddress paramUserAddress, PaymentMethodToken paramPaymentMethodToken, String paramString2, Bundle paramBundle, String paramString3)
  {
    this.zzaz = paramString1;
    this.zzds = paramCardInfo;
    this.zzdt = paramUserAddress;
    this.zzbg = paramPaymentMethodToken;
    this.zzaw = paramString2;
    this.zzdu = paramBundle;
    this.zzby = paramString3;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeString(paramParcel, 1, this.zzaz, false);
    SafeParcelWriter.writeParcelable(paramParcel, 2, this.zzds, paramInt, false);
    SafeParcelWriter.writeParcelable(paramParcel, 3, this.zzdt, paramInt, false);
    SafeParcelWriter.writeParcelable(paramParcel, 4, this.zzbg, paramInt, false);
    SafeParcelWriter.writeString(paramParcel, 5, this.zzaw, false);
    SafeParcelWriter.writeBundle(paramParcel, 6, this.zzdu, false);
    SafeParcelWriter.writeString(paramParcel, 7, this.zzby, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/PaymentData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */