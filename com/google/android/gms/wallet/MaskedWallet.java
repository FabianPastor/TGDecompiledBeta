package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.identity.intents.model.UserAddress;

public final class MaskedWallet
  extends AbstractSafeParcelable
  implements ReflectedParcelable
{
  public static final Parcelable.Creator<MaskedWallet> CREATOR = new zzx();
  String zzaw;
  String zzax;
  String zzaz;
  private zza zzba;
  private zza zzbb;
  String[] zzbc;
  UserAddress zzbd;
  UserAddress zzbe;
  InstrumentInfo[] zzbf;
  private LoyaltyWalletObject[] zzda;
  private OfferWalletObject[] zzdb;
  
  private MaskedWallet() {}
  
  MaskedWallet(String paramString1, String paramString2, String[] paramArrayOfString, String paramString3, zza paramzza1, zza paramzza2, LoyaltyWalletObject[] paramArrayOfLoyaltyWalletObject, OfferWalletObject[] paramArrayOfOfferWalletObject, UserAddress paramUserAddress1, UserAddress paramUserAddress2, InstrumentInfo[] paramArrayOfInstrumentInfo)
  {
    this.zzaw = paramString1;
    this.zzax = paramString2;
    this.zzbc = paramArrayOfString;
    this.zzaz = paramString3;
    this.zzba = paramzza1;
    this.zzbb = paramzza2;
    this.zzda = paramArrayOfLoyaltyWalletObject;
    this.zzdb = paramArrayOfOfferWalletObject;
    this.zzbd = paramUserAddress1;
    this.zzbe = paramUserAddress2;
    this.zzbf = paramArrayOfInstrumentInfo;
  }
  
  public final String getGoogleTransactionId()
  {
    return this.zzaw;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeString(paramParcel, 2, this.zzaw, false);
    SafeParcelWriter.writeString(paramParcel, 3, this.zzax, false);
    SafeParcelWriter.writeStringArray(paramParcel, 4, this.zzbc, false);
    SafeParcelWriter.writeString(paramParcel, 5, this.zzaz, false);
    SafeParcelWriter.writeParcelable(paramParcel, 6, this.zzba, paramInt, false);
    SafeParcelWriter.writeParcelable(paramParcel, 7, this.zzbb, paramInt, false);
    SafeParcelWriter.writeTypedArray(paramParcel, 8, this.zzda, paramInt, false);
    SafeParcelWriter.writeTypedArray(paramParcel, 9, this.zzdb, paramInt, false);
    SafeParcelWriter.writeParcelable(paramParcel, 10, this.zzbd, paramInt, false);
    SafeParcelWriter.writeParcelable(paramParcel, 11, this.zzbe, paramInt, false);
    SafeParcelWriter.writeTypedArray(paramParcel, 12, this.zzbf, paramInt, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/MaskedWallet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */