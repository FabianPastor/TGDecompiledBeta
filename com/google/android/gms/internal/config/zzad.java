package com.google.android.gms.internal.config;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

public final class zzad
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<zzad> CREATOR = new zzae();
  private final int zzab;
  private final DataHolder zzac;
  private final DataHolder zzad;
  private final long zzr;
  
  public zzad(int paramInt, DataHolder paramDataHolder1, long paramLong, DataHolder paramDataHolder2)
  {
    this.zzab = paramInt;
    this.zzac = paramDataHolder1;
    this.zzr = paramLong;
    this.zzad = paramDataHolder2;
  }
  
  public final int getStatusCode()
  {
    return this.zzab;
  }
  
  public final long getThrottleEndTimeMillis()
  {
    return this.zzr;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 2, this.zzab);
    SafeParcelWriter.writeParcelable(paramParcel, 3, this.zzac, paramInt, false);
    SafeParcelWriter.writeLong(paramParcel, 4, this.zzr);
    SafeParcelWriter.writeParcelable(paramParcel, 5, this.zzad, paramInt, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
  
  public final DataHolder zzi()
  {
    return this.zzac;
  }
  
  public final DataHolder zzj()
  {
    return this.zzad;
  }
  
  public final void zzk()
  {
    if ((this.zzac != null) && (!this.zzac.isClosed())) {
      this.zzac.close();
    }
  }
  
  public final void zzl()
  {
    if ((this.zzad != null) && (!this.zzad.isClosed())) {
      this.zzad.close();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/config/zzad.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */