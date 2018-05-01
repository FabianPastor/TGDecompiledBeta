package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.Frame.Metadata;

public final class zzdjw
  extends zzbfm
{
  public static final Parcelable.Creator<zzdjw> CREATOR = new zzdjx();
  public int height;
  private int id;
  public int rotation;
  public int width;
  private long zzikn;
  
  public zzdjw() {}
  
  public zzdjw(int paramInt1, int paramInt2, int paramInt3, long paramLong, int paramInt4)
  {
    this.width = paramInt1;
    this.height = paramInt2;
    this.id = paramInt3;
    this.zzikn = paramLong;
    this.rotation = paramInt4;
  }
  
  public static zzdjw zzc(Frame paramFrame)
  {
    zzdjw localzzdjw = new zzdjw();
    localzzdjw.width = paramFrame.getMetadata().getWidth();
    localzzdjw.height = paramFrame.getMetadata().getHeight();
    localzzdjw.rotation = paramFrame.getMetadata().getRotation();
    localzzdjw.id = paramFrame.getMetadata().getId();
    localzzdjw.zzikn = paramFrame.getMetadata().getTimestampMillis();
    return localzzdjw;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = zzbfp.zze(paramParcel);
    zzbfp.zzc(paramParcel, 2, this.width);
    zzbfp.zzc(paramParcel, 3, this.height);
    zzbfp.zzc(paramParcel, 4, this.id);
    zzbfp.zza(paramParcel, 5, this.zzikn);
    zzbfp.zzc(paramParcel, 6, this.rotation);
    zzbfp.zzai(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzdjw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */