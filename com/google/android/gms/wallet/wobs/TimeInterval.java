package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

public final class TimeInterval
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<TimeInterval> CREATOR = new zzk();
  private long zzhb;
  private long zzhc;
  
  TimeInterval() {}
  
  public TimeInterval(long paramLong1, long paramLong2)
  {
    this.zzhb = paramLong1;
    this.zzhc = paramLong2;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeLong(paramParcel, 2, this.zzhb);
    SafeParcelWriter.writeLong(paramParcel, 3, this.zzhc);
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/wobs/TimeInterval.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */