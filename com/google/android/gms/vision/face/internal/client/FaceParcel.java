package com.google.android.gms.vision.face.internal.client;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public class FaceParcel
  extends AbstractSafeParcelable
{
  public static final zzb CREATOR = new zzb();
  public final float aLg;
  public final float aLh;
  public final LandmarkParcel[] aLi;
  public final float aLj;
  public final float aLk;
  public final float aLl;
  public final float centerX;
  public final float centerY;
  public final float height;
  public final int id;
  public final int versionCode;
  public final float width;
  
  public FaceParcel(int paramInt1, int paramInt2, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, LandmarkParcel[] paramArrayOfLandmarkParcel, float paramFloat7, float paramFloat8, float paramFloat9)
  {
    this.versionCode = paramInt1;
    this.id = paramInt2;
    this.centerX = paramFloat1;
    this.centerY = paramFloat2;
    this.width = paramFloat3;
    this.height = paramFloat4;
    this.aLg = paramFloat5;
    this.aLh = paramFloat6;
    this.aLi = paramArrayOfLandmarkParcel;
    this.aLj = paramFloat7;
    this.aLk = paramFloat8;
    this.aLl = paramFloat9;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzb.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/face/internal/client/FaceParcel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */