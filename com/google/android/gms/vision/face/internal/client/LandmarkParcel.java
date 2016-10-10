package com.google.android.gms.vision.face.internal.client;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public final class LandmarkParcel
  extends AbstractSafeParcelable
{
  public static final zzf CREATOR = new zzf();
  public final int type;
  public final int versionCode;
  public final float x;
  public final float y;
  
  public LandmarkParcel(int paramInt1, float paramFloat1, float paramFloat2, int paramInt2)
  {
    this.versionCode = paramInt1;
    this.x = paramFloat1;
    this.y = paramFloat2;
    this.type = paramInt2;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzf.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/face/internal/client/LandmarkParcel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */