package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

public final class zzdt
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<zzdt> CREATOR = new zzds();
  private final int statusCode;
  private final boolean zzdt;
  private final boolean zzdu;
  
  public zzdt(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.statusCode = paramInt;
    this.zzdt = paramBoolean1;
    this.zzdu = paramBoolean2;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 2, this.statusCode);
    SafeParcelWriter.writeBoolean(paramParcel, 3, this.zzdt);
    SafeParcelWriter.writeBoolean(paramParcel, 4, this.zzdu);
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzdt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */