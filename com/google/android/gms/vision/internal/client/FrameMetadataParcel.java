package com.google.android.gms.vision.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.Frame.Metadata;

public class FrameMetadataParcel
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<FrameMetadataParcel> CREATOR = new zzb();
  public long aOE;
  public int height;
  public int id;
  public int rotation;
  final int versionCode;
  public int width;
  
  public FrameMetadataParcel()
  {
    this.versionCode = 1;
  }
  
  public FrameMetadataParcel(int paramInt1, int paramInt2, int paramInt3, int paramInt4, long paramLong, int paramInt5)
  {
    this.versionCode = paramInt1;
    this.width = paramInt2;
    this.height = paramInt3;
    this.id = paramInt4;
    this.aOE = paramLong;
    this.rotation = paramInt5;
  }
  
  public static FrameMetadataParcel zzc(Frame paramFrame)
  {
    FrameMetadataParcel localFrameMetadataParcel = new FrameMetadataParcel();
    localFrameMetadataParcel.width = paramFrame.getMetadata().getWidth();
    localFrameMetadataParcel.height = paramFrame.getMetadata().getHeight();
    localFrameMetadataParcel.rotation = paramFrame.getMetadata().getRotation();
    localFrameMetadataParcel.id = paramFrame.getMetadata().getId();
    localFrameMetadataParcel.aOE = paramFrame.getMetadata().getTimestampMillis();
    return localFrameMetadataParcel;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzb.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/internal/client/FrameMetadataParcel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */