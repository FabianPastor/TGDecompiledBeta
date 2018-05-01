package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

public final class LabelValue
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<LabelValue> CREATOR = new zzc();
  private String label;
  private String value;
  
  LabelValue() {}
  
  public LabelValue(String paramString1, String paramString2)
  {
    this.label = paramString1;
    this.value = paramString2;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeString(paramParcel, 2, this.label, false);
    SafeParcelWriter.writeString(paramParcel, 3, this.value, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/wobs/LabelValue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */