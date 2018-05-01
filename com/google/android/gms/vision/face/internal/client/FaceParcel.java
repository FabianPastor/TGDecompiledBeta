package com.google.android.gms.vision.face.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.apps.common.proguard.UsedByNative;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

@UsedByNative("wrapper.cc")
public class FaceParcel
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<FaceParcel> CREATOR = new zzb();
  public final float centerX;
  public final float centerY;
  public final float height;
  public final int id;
  private final int versionCode;
  public final float width;
  public final float zzbx;
  public final float zzby;
  public final LandmarkParcel[] zzbz;
  public final float zzca;
  public final float zzcb;
  public final float zzcc;
  
  public FaceParcel(int paramInt1, int paramInt2, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, LandmarkParcel[] paramArrayOfLandmarkParcel, float paramFloat7, float paramFloat8, float paramFloat9)
  {
    this.versionCode = paramInt1;
    this.id = paramInt2;
    this.centerX = paramFloat1;
    this.centerY = paramFloat2;
    this.width = paramFloat3;
    this.height = paramFloat4;
    this.zzbx = paramFloat5;
    this.zzby = paramFloat6;
    this.zzbz = paramArrayOfLandmarkParcel;
    this.zzca = paramFloat7;
    this.zzcb = paramFloat8;
    this.zzcc = paramFloat9;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 1, this.versionCode);
    SafeParcelWriter.writeInt(paramParcel, 2, this.id);
    SafeParcelWriter.writeFloat(paramParcel, 3, this.centerX);
    SafeParcelWriter.writeFloat(paramParcel, 4, this.centerY);
    SafeParcelWriter.writeFloat(paramParcel, 5, this.width);
    SafeParcelWriter.writeFloat(paramParcel, 6, this.height);
    SafeParcelWriter.writeFloat(paramParcel, 7, this.zzbx);
    SafeParcelWriter.writeFloat(paramParcel, 8, this.zzby);
    SafeParcelWriter.writeTypedArray(paramParcel, 9, this.zzbz, paramInt, false);
    SafeParcelWriter.writeFloat(paramParcel, 10, this.zzca);
    SafeParcelWriter.writeFloat(paramParcel, 11, this.zzcb);
    SafeParcelWriter.writeFloat(paramParcel, 12, this.zzcc);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/face/internal/client/FaceParcel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */