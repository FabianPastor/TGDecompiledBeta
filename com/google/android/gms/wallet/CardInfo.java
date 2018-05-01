package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.identity.intents.model.UserAddress;

public final class CardInfo
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<CardInfo> CREATOR = new zzc();
  private String zzad;
  private String zzae;
  private String zzaf;
  private int zzag;
  private UserAddress zzah;
  
  private CardInfo() {}
  
  CardInfo(String paramString1, String paramString2, String paramString3, int paramInt, UserAddress paramUserAddress)
  {
    this.zzad = paramString1;
    this.zzae = paramString2;
    this.zzaf = paramString3;
    this.zzag = paramInt;
    this.zzah = paramUserAddress;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeString(paramParcel, 1, this.zzad, false);
    SafeParcelWriter.writeString(paramParcel, 2, this.zzae, false);
    SafeParcelWriter.writeString(paramParcel, 3, this.zzaf, false);
    SafeParcelWriter.writeInt(paramParcel, 4, this.zzag);
    SafeParcelWriter.writeParcelable(paramParcel, 5, this.zzah, paramInt, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/CardInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */