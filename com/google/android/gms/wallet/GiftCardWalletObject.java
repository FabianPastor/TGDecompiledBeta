package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.wallet.wobs.CommonWalletObject;
import com.google.android.gms.wallet.wobs.CommonWalletObject.zza;

public final class GiftCardWalletObject
  extends zza
{
  public static final Parcelable.Creator<GiftCardWalletObject> CREATOR = new zzi();
  private String pin;
  private CommonWalletObject zzbOD = CommonWalletObject.zzDU().zzDV();
  private String zzbOE;
  private String zzbOF;
  private long zzbOG;
  private String zzbOH;
  private long zzbOI;
  private String zzbOJ;
  
  GiftCardWalletObject() {}
  
  GiftCardWalletObject(CommonWalletObject paramCommonWalletObject, String paramString1, String paramString2, String paramString3, long paramLong1, String paramString4, long paramLong2, String paramString5)
  {
    this.zzbOD = paramCommonWalletObject;
    this.zzbOE = paramString1;
    this.pin = paramString2;
    this.zzbOG = paramLong1;
    this.zzbOH = paramString4;
    this.zzbOI = paramLong2;
    this.zzbOJ = paramString5;
    this.zzbOF = paramString3;
  }
  
  public final String getId()
  {
    return this.zzbOD.getId();
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = zzd.zze(paramParcel);
    zzd.zza(paramParcel, 2, this.zzbOD, paramInt, false);
    zzd.zza(paramParcel, 3, this.zzbOE, false);
    zzd.zza(paramParcel, 4, this.pin, false);
    zzd.zza(paramParcel, 5, this.zzbOF, false);
    zzd.zza(paramParcel, 6, this.zzbOG);
    zzd.zza(paramParcel, 7, this.zzbOH, false);
    zzd.zza(paramParcel, 8, this.zzbOI);
    zzd.zza(paramParcel, 9, this.zzbOJ, false);
    zzd.zzI(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/GiftCardWalletObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */