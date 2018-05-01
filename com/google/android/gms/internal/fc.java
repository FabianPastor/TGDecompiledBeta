package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.Frame.Metadata;

public final class fc
  extends zza
{
  public static final Parcelable.Creator<fc> CREATOR = new fd();
  public int height;
  private int id;
  public int rotation;
  public int width;
  private long zzbiv;
  
  public fc() {}
  
  public fc(int paramInt1, int paramInt2, int paramInt3, long paramLong, int paramInt4)
  {
    this.width = paramInt1;
    this.height = paramInt2;
    this.id = paramInt3;
    this.zzbiv = paramLong;
    this.rotation = paramInt4;
  }
  
  public static fc zzc(Frame paramFrame)
  {
    fc localfc = new fc();
    localfc.width = paramFrame.getMetadata().getWidth();
    localfc.height = paramFrame.getMetadata().getHeight();
    localfc.rotation = paramFrame.getMetadata().getRotation();
    localfc.id = paramFrame.getMetadata().getId();
    localfc.zzbiv = paramFrame.getMetadata().getTimestampMillis();
    return localfc;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = zzd.zze(paramParcel);
    zzd.zzc(paramParcel, 2, this.width);
    zzd.zzc(paramParcel, 3, this.height);
    zzd.zzc(paramParcel, 4, this.id);
    zzd.zza(paramParcel, 5, this.zzbiv);
    zzd.zzc(paramParcel, 6, this.rotation);
    zzd.zzI(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/fc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */