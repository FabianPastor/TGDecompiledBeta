package com.google.android.gms.vision.face.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.apps.common.proguard.UsedByNative;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

@UsedByNative("wrapper.cc")
public final class LandmarkParcel
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<LandmarkParcel> CREATOR = new zzi();
  public final int type;
  private final int versionCode;
  public final float x;
  public final float y;
  
  public LandmarkParcel(int paramInt1, float paramFloat1, float paramFloat2, int paramInt2)
  {
    this.versionCode = paramInt1;
    this.x = paramFloat1;
    this.y = paramFloat2;
    this.type = paramInt2;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 1, this.versionCode);
    SafeParcelWriter.writeFloat(paramParcel, 2, this.x);
    SafeParcelWriter.writeFloat(paramParcel, 3, this.y);
    SafeParcelWriter.writeInt(paramParcel, 4, this.type);
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/face/internal/client/LandmarkParcel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */