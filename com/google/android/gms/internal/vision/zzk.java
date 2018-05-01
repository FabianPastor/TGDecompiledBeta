package com.google.android.gms.internal.vision;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.Frame.Metadata;

public final class zzk
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<zzk> CREATOR = new zzl();
  public int height;
  private int id;
  public int rotation;
  public int width;
  private long zzck;
  
  public zzk() {}
  
  public zzk(int paramInt1, int paramInt2, int paramInt3, long paramLong, int paramInt4)
  {
    this.width = paramInt1;
    this.height = paramInt2;
    this.id = paramInt3;
    this.zzck = paramLong;
    this.rotation = paramInt4;
  }
  
  public static zzk zzc(Frame paramFrame)
  {
    zzk localzzk = new zzk();
    localzzk.width = paramFrame.getMetadata().getWidth();
    localzzk.height = paramFrame.getMetadata().getHeight();
    localzzk.rotation = paramFrame.getMetadata().getRotation();
    localzzk.id = paramFrame.getMetadata().getId();
    localzzk.zzck = paramFrame.getMetadata().getTimestampMillis();
    return localzzk;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 2, this.width);
    SafeParcelWriter.writeInt(paramParcel, 3, this.height);
    SafeParcelWriter.writeInt(paramParcel, 4, this.id);
    SafeParcelWriter.writeLong(paramParcel, 5, this.zzck);
    SafeParcelWriter.writeInt(paramParcel, 6, this.rotation);
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/vision/zzk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */