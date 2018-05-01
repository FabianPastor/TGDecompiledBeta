package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

public final class LoyaltyPointsBalance
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<LoyaltyPointsBalance> CREATOR = new zzh();
  String zzbn;
  int zzgt;
  String zzgu;
  double zzgv;
  long zzgw;
  int zzgx;
  
  LoyaltyPointsBalance()
  {
    this.zzgx = -1;
    this.zzgt = -1;
    this.zzgv = -1.0D;
  }
  
  LoyaltyPointsBalance(int paramInt1, String paramString1, double paramDouble, String paramString2, long paramLong, int paramInt2)
  {
    this.zzgt = paramInt1;
    this.zzgu = paramString1;
    this.zzgv = paramDouble;
    this.zzbn = paramString2;
    this.zzgw = paramLong;
    this.zzgx = paramInt2;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 2, this.zzgt);
    SafeParcelWriter.writeString(paramParcel, 3, this.zzgu, false);
    SafeParcelWriter.writeDouble(paramParcel, 4, this.zzgv);
    SafeParcelWriter.writeString(paramParcel, 5, this.zzbn, false);
    SafeParcelWriter.writeLong(paramParcel, 6, this.zzgw);
    SafeParcelWriter.writeInt(paramParcel, 7, this.zzgx);
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/wobs/LoyaltyPointsBalance.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */