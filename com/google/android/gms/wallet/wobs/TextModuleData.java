package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

public final class TextModuleData
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<TextModuleData> CREATOR = new zzj();
  private String zzgz;
  private String zzha;
  
  TextModuleData() {}
  
  public TextModuleData(String paramString1, String paramString2)
  {
    this.zzgz = paramString1;
    this.zzha = paramString2;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeString(paramParcel, 2, this.zzgz, false);
    SafeParcelWriter.writeString(paramParcel, 3, this.zzha, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/wobs/TextModuleData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */