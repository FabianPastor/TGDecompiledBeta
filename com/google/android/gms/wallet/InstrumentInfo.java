package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

public final class InstrumentInfo
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<InstrumentInfo> CREATOR = new zzp();
  private int zzag;
  private String zzbs;
  private String zzbt;
  
  private InstrumentInfo() {}
  
  public InstrumentInfo(String paramString1, String paramString2, int paramInt)
  {
    this.zzbs = paramString1;
    this.zzbt = paramString2;
    this.zzag = paramInt;
  }
  
  public final int getCardClass()
  {
    switch (this.zzag)
    {
    }
    for (int i = 0;; i = this.zzag) {
      return i;
    }
  }
  
  public final String getInstrumentDetails()
  {
    return this.zzbt;
  }
  
  public final String getInstrumentType()
  {
    return this.zzbs;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeString(paramParcel, 2, getInstrumentType(), false);
    SafeParcelWriter.writeString(paramParcel, 3, getInstrumentDetails(), false);
    SafeParcelWriter.writeInt(paramParcel, 4, getCardClass());
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/InstrumentInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */