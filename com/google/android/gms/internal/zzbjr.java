package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzbjr
  extends zza
{
  public static final Parcelable.Creator<zzbjr> CREATOR = new zzbjs();
  public final float centerX;
  public final float centerY;
  public final float height;
  public final int id;
  public final int versionCode;
  public final float width;
  public final float zzbPb;
  public final float zzbPc;
  public final zzbjx[] zzbPd;
  public final float zzbPe;
  public final float zzbPf;
  public final float zzbPg;
  
  public zzbjr(int paramInt1, int paramInt2, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, zzbjx[] paramArrayOfzzbjx, float paramFloat7, float paramFloat8, float paramFloat9)
  {
    this.versionCode = paramInt1;
    this.id = paramInt2;
    this.centerX = paramFloat1;
    this.centerY = paramFloat2;
    this.width = paramFloat3;
    this.height = paramFloat4;
    this.zzbPb = paramFloat5;
    this.zzbPc = paramFloat6;
    this.zzbPd = paramArrayOfzzbjx;
    this.zzbPe = paramFloat7;
    this.zzbPf = paramFloat8;
    this.zzbPg = paramFloat9;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzbjs.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbjr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */