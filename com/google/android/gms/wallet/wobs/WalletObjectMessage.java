package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

public final class WalletObjectMessage
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<WalletObjectMessage> CREATOR = new zzn();
  String zzgz;
  String zzha;
  TimeInterval zzhe;
  UriData zzhf;
  UriData zzhg;
  
  WalletObjectMessage() {}
  
  WalletObjectMessage(String paramString1, String paramString2, TimeInterval paramTimeInterval, UriData paramUriData1, UriData paramUriData2)
  {
    this.zzgz = paramString1;
    this.zzha = paramString2;
    this.zzhe = paramTimeInterval;
    this.zzhf = paramUriData1;
    this.zzhg = paramUriData2;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeString(paramParcel, 2, this.zzgz, false);
    SafeParcelWriter.writeString(paramParcel, 3, this.zzha, false);
    SafeParcelWriter.writeParcelable(paramParcel, 4, this.zzhe, paramInt, false);
    SafeParcelWriter.writeParcelable(paramParcel, 5, this.zzhf, paramInt, false);
    SafeParcelWriter.writeParcelable(paramParcel, 6, this.zzhg, paramInt, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/wobs/WalletObjectMessage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */