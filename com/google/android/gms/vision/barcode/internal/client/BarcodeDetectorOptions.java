package com.google.android.gms.vision.barcode.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public class BarcodeDetectorOptions
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<BarcodeDetectorOptions> CREATOR = new zza();
  public int aNZ;
  final int versionCode;
  
  public BarcodeDetectorOptions()
  {
    this.versionCode = 1;
  }
  
  public BarcodeDetectorOptions(int paramInt1, int paramInt2)
  {
    this.versionCode = paramInt1;
    this.aNZ = paramInt2;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zza.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/barcode/internal/client/BarcodeDetectorOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */