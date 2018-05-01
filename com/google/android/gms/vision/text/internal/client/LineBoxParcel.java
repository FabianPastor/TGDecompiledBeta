package com.google.android.gms.vision.text.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public class LineBoxParcel
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<LineBoxParcel> CREATOR = new zzd();
  public final String aOK;
  public final WordBoxParcel[] aOQ;
  public final BoundingBoxParcel aOR;
  public final BoundingBoxParcel aOS;
  public final BoundingBoxParcel aOT;
  public final String aOU;
  public final float aOV;
  public final int aOW;
  public final boolean aOX;
  public final int aOY;
  public final int aOZ;
  public final int versionCode;
  
  public LineBoxParcel(int paramInt1, WordBoxParcel[] paramArrayOfWordBoxParcel, BoundingBoxParcel paramBoundingBoxParcel1, BoundingBoxParcel paramBoundingBoxParcel2, BoundingBoxParcel paramBoundingBoxParcel3, String paramString1, float paramFloat, String paramString2, int paramInt2, boolean paramBoolean, int paramInt3, int paramInt4)
  {
    this.versionCode = paramInt1;
    this.aOQ = paramArrayOfWordBoxParcel;
    this.aOR = paramBoundingBoxParcel1;
    this.aOS = paramBoundingBoxParcel2;
    this.aOT = paramBoundingBoxParcel3;
    this.aOU = paramString1;
    this.aOV = paramFloat;
    this.aOK = paramString2;
    this.aOW = paramInt2;
    this.aOX = paramBoolean;
    this.aOY = paramInt3;
    this.aOZ = paramInt4;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzd.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/text/internal/client/LineBoxParcel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */