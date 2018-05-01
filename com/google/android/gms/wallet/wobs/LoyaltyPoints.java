package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

public final class LoyaltyPoints
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<LoyaltyPoints> CREATOR = new zzi();
  String label;
  TimeInterval zzcp;
  LoyaltyPointsBalance zzgr;
  
  LoyaltyPoints() {}
  
  LoyaltyPoints(String paramString, LoyaltyPointsBalance paramLoyaltyPointsBalance, TimeInterval paramTimeInterval)
  {
    this.label = paramString;
    this.zzgr = paramLoyaltyPointsBalance;
    this.zzcp = paramTimeInterval;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeString(paramParcel, 2, this.label, false);
    SafeParcelWriter.writeParcelable(paramParcel, 3, this.zzgr, paramInt, false);
    SafeParcelWriter.writeParcelable(paramParcel, 5, this.zzcp, paramInt, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/wobs/LoyaltyPoints.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */