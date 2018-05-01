package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import java.util.ArrayList;

public final class MaskedWalletRequest
  extends AbstractSafeParcelable
  implements ReflectedParcelable
{
  public static final Parcelable.Creator<MaskedWalletRequest> CREATOR = new zzz();
  ArrayList<Integer> zzai;
  String zzao;
  String zzax;
  Cart zzbh;
  boolean zzdd;
  boolean zzde;
  boolean zzdf;
  String zzdg;
  String zzdh;
  private boolean zzdi;
  boolean zzdj;
  private CountrySpecification[] zzdk;
  boolean zzdl;
  boolean zzdm;
  ArrayList<com.google.android.gms.identity.intents.model.CountrySpecification> zzdn;
  PaymentMethodTokenizationParameters zzdo;
  String zzh;
  
  MaskedWalletRequest()
  {
    this.zzdl = true;
    this.zzdm = true;
  }
  
  MaskedWalletRequest(String paramString1, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, String paramString2, String paramString3, String paramString4, Cart paramCart, boolean paramBoolean4, boolean paramBoolean5, CountrySpecification[] paramArrayOfCountrySpecification, boolean paramBoolean6, boolean paramBoolean7, ArrayList<com.google.android.gms.identity.intents.model.CountrySpecification> paramArrayList, PaymentMethodTokenizationParameters paramPaymentMethodTokenizationParameters, ArrayList<Integer> paramArrayList1, String paramString5)
  {
    this.zzax = paramString1;
    this.zzdd = paramBoolean1;
    this.zzde = paramBoolean2;
    this.zzdf = paramBoolean3;
    this.zzdg = paramString2;
    this.zzao = paramString3;
    this.zzdh = paramString4;
    this.zzbh = paramCart;
    this.zzdi = paramBoolean4;
    this.zzdj = paramBoolean5;
    this.zzdk = paramArrayOfCountrySpecification;
    this.zzdl = paramBoolean6;
    this.zzdm = paramBoolean7;
    this.zzdn = paramArrayList;
    this.zzdo = paramPaymentMethodTokenizationParameters;
    this.zzai = paramArrayList1;
    this.zzh = paramString5;
  }
  
  public static Builder newBuilder()
  {
    return new Builder(new MaskedWalletRequest(), null);
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeString(paramParcel, 2, this.zzax, false);
    SafeParcelWriter.writeBoolean(paramParcel, 3, this.zzdd);
    SafeParcelWriter.writeBoolean(paramParcel, 4, this.zzde);
    SafeParcelWriter.writeBoolean(paramParcel, 5, this.zzdf);
    SafeParcelWriter.writeString(paramParcel, 6, this.zzdg, false);
    SafeParcelWriter.writeString(paramParcel, 7, this.zzao, false);
    SafeParcelWriter.writeString(paramParcel, 8, this.zzdh, false);
    SafeParcelWriter.writeParcelable(paramParcel, 9, this.zzbh, paramInt, false);
    SafeParcelWriter.writeBoolean(paramParcel, 10, this.zzdi);
    SafeParcelWriter.writeBoolean(paramParcel, 11, this.zzdj);
    SafeParcelWriter.writeTypedArray(paramParcel, 12, this.zzdk, paramInt, false);
    SafeParcelWriter.writeBoolean(paramParcel, 13, this.zzdl);
    SafeParcelWriter.writeBoolean(paramParcel, 14, this.zzdm);
    SafeParcelWriter.writeTypedList(paramParcel, 15, this.zzdn, false);
    SafeParcelWriter.writeParcelable(paramParcel, 16, this.zzdo, paramInt, false);
    SafeParcelWriter.writeIntegerList(paramParcel, 17, this.zzai, false);
    SafeParcelWriter.writeString(paramParcel, 18, this.zzh, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
  
  public final class Builder
  {
    private Builder() {}
    
    public final MaskedWalletRequest build()
    {
      return MaskedWalletRequest.this;
    }
    
    public final Builder setCurrencyCode(String paramString)
    {
      MaskedWalletRequest.this.zzao = paramString;
      return this;
    }
    
    public final Builder setEstimatedTotalPrice(String paramString)
    {
      MaskedWalletRequest.this.zzdg = paramString;
      return this;
    }
    
    public final Builder setPaymentMethodTokenizationParameters(PaymentMethodTokenizationParameters paramPaymentMethodTokenizationParameters)
    {
      MaskedWalletRequest.this.zzdo = paramPaymentMethodTokenizationParameters;
      return this;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/MaskedWalletRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */