package com.google.android.gms.vision.text.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public class SymbolBoxParcel
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<SymbolBoxParcel> CREATOR = new zzf();
  public final int versionCode;
  
  public SymbolBoxParcel(int paramInt)
  {
    this.versionCode = paramInt;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzf.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/text/internal/client/SymbolBoxParcel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */