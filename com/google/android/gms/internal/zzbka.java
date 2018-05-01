package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.Frame.Metadata;

public class zzbka
  extends zza
{
  public static final Parcelable.Creator<zzbka> CREATOR = new zzbkb();
  public int height;
  public int id;
  public int rotation;
  final int versionCode;
  public int width;
  public long zzbPo;
  
  public zzbka()
  {
    this.versionCode = 1;
  }
  
  public zzbka(int paramInt1, int paramInt2, int paramInt3, int paramInt4, long paramLong, int paramInt5)
  {
    this.versionCode = paramInt1;
    this.width = paramInt2;
    this.height = paramInt3;
    this.id = paramInt4;
    this.zzbPo = paramLong;
    this.rotation = paramInt5;
  }
  
  public static zzbka zzc(Frame paramFrame)
  {
    zzbka localzzbka = new zzbka();
    localzzbka.width = paramFrame.getMetadata().getWidth();
    localzzbka.height = paramFrame.getMetadata().getHeight();
    localzzbka.rotation = paramFrame.getMetadata().getRotation();
    localzzbka.id = paramFrame.getMetadata().getId();
    localzzbka.zzbPo = paramFrame.getMetadata().getTimestampMillis();
    return localzzbka;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzbkb.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbka.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */