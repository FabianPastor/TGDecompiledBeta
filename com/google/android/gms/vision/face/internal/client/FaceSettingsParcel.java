package com.google.android.gms.vision.face.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public class FaceSettingsParcel
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<FaceSettingsParcel> CREATOR = new zzc();
  public boolean aOA;
  public float aOB;
  public int aOx;
  public int aOy;
  public boolean aOz;
  public int mode;
  public final int versionCode;
  
  public FaceSettingsParcel()
  {
    this.versionCode = 2;
  }
  
  public FaceSettingsParcel(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1, boolean paramBoolean2, float paramFloat)
  {
    this.versionCode = paramInt1;
    this.mode = paramInt2;
    this.aOx = paramInt3;
    this.aOy = paramInt4;
    this.aOz = paramBoolean1;
    this.aOA = paramBoolean2;
    this.aOB = paramFloat;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzc.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/face/internal/client/FaceSettingsParcel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */