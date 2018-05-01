package com.google.android.gms.vision.text.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public class TextRecognizerOptions
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<TextRecognizerOptions> CREATOR = new zzh();
  final int versionCode;
  
  public TextRecognizerOptions()
  {
    this.versionCode = 1;
  }
  
  public TextRecognizerOptions(int paramInt)
  {
    this.versionCode = paramInt;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzh.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/text/internal/client/TextRecognizerOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */