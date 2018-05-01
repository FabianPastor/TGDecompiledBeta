package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.wallet.wobs.CommonWalletObject;
import com.google.android.gms.wallet.wobs.CommonWalletObject.zza;

public final class OfferWalletObject
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<OfferWalletObject> CREATOR = new zzab();
  private final int versionCode;
  CommonWalletObject zzbj;
  String zzce;
  String zzdq;
  
  OfferWalletObject()
  {
    this.versionCode = 3;
  }
  
  OfferWalletObject(int paramInt, String paramString1, String paramString2, CommonWalletObject paramCommonWalletObject)
  {
    this.versionCode = paramInt;
    this.zzdq = paramString2;
    if (paramInt < 3) {}
    for (this.zzbj = CommonWalletObject.zze().zza(paramString1).zzf();; this.zzbj = paramCommonWalletObject) {
      return;
    }
  }
  
  public final int getVersionCode()
  {
    return this.versionCode;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 1, getVersionCode());
    SafeParcelWriter.writeString(paramParcel, 2, this.zzce, false);
    SafeParcelWriter.writeString(paramParcel, 3, this.zzdq, false);
    SafeParcelWriter.writeParcelable(paramParcel, 4, this.zzbj, paramInt, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/OfferWalletObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */