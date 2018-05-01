package com.google.android.gms.vision.text.internal.client;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public class RecognitionOptions
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<RecognitionOptions> CREATOR = new zze();
  public final Rect aPa;
  final int versionCode;
  
  public RecognitionOptions()
  {
    this.versionCode = 1;
    this.aPa = new Rect();
  }
  
  public RecognitionOptions(int paramInt, Rect paramRect)
  {
    this.versionCode = paramInt;
    this.aPa = paramRect;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zze.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/text/internal/client/RecognitionOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */