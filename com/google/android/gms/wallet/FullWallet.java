package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.identity.intents.model.UserAddress;

public final class FullWallet
  extends AbstractSafeParcelable
  implements ReflectedParcelable
{
  public static final Parcelable.Creator<FullWallet> CREATOR = new zzk();
  private String zzaw;
  private String zzax;
  private ProxyCard zzay;
  private String zzaz;
  private zza zzba;
  private zza zzbb;
  private String[] zzbc;
  private UserAddress zzbd;
  private UserAddress zzbe;
  private InstrumentInfo[] zzbf;
  private PaymentMethodToken zzbg;
  
  private FullWallet() {}
  
  FullWallet(String paramString1, String paramString2, ProxyCard paramProxyCard, String paramString3, zza paramzza1, zza paramzza2, String[] paramArrayOfString, UserAddress paramUserAddress1, UserAddress paramUserAddress2, InstrumentInfo[] paramArrayOfInstrumentInfo, PaymentMethodToken paramPaymentMethodToken)
  {
    this.zzaw = paramString1;
    this.zzax = paramString2;
    this.zzay = paramProxyCard;
    this.zzaz = paramString3;
    this.zzba = paramzza1;
    this.zzbb = paramzza2;
    this.zzbc = paramArrayOfString;
    this.zzbd = paramUserAddress1;
    this.zzbe = paramUserAddress2;
    this.zzbf = paramArrayOfInstrumentInfo;
    this.zzbg = paramPaymentMethodToken;
  }
  
  public final String getGoogleTransactionId()
  {
    return this.zzaw;
  }
  
  public final String[] getPaymentDescriptions()
  {
    return this.zzbc;
  }
  
  public final PaymentMethodToken getPaymentMethodToken()
  {
    return this.zzbg;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeString(paramParcel, 2, this.zzaw, false);
    SafeParcelWriter.writeString(paramParcel, 3, this.zzax, false);
    SafeParcelWriter.writeParcelable(paramParcel, 4, this.zzay, paramInt, false);
    SafeParcelWriter.writeString(paramParcel, 5, this.zzaz, false);
    SafeParcelWriter.writeParcelable(paramParcel, 6, this.zzba, paramInt, false);
    SafeParcelWriter.writeParcelable(paramParcel, 7, this.zzbb, paramInt, false);
    SafeParcelWriter.writeStringArray(paramParcel, 8, this.zzbc, false);
    SafeParcelWriter.writeParcelable(paramParcel, 9, this.zzbd, paramInt, false);
    SafeParcelWriter.writeParcelable(paramParcel, 10, this.zzbe, paramInt, false);
    SafeParcelWriter.writeTypedArray(paramParcel, 11, this.zzbf, paramInt, false);
    SafeParcelWriter.writeParcelable(paramParcel, 12, this.zzbg, paramInt, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/FullWallet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */