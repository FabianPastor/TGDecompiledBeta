package com.google.android.gms.vision.text.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public class WordBoxParcel
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<WordBoxParcel> CREATOR = new zzi();
  public final String aOK;
  public final BoundingBoxParcel aOR;
  public final BoundingBoxParcel aOS;
  public final String aOU;
  public final float aOV;
  public final SymbolBoxParcel[] aPb;
  public final boolean aPc;
  public final int versionCode;
  
  public WordBoxParcel(int paramInt, SymbolBoxParcel[] paramArrayOfSymbolBoxParcel, BoundingBoxParcel paramBoundingBoxParcel1, BoundingBoxParcel paramBoundingBoxParcel2, String paramString1, float paramFloat, String paramString2, boolean paramBoolean)
  {
    this.versionCode = paramInt;
    this.aPb = paramArrayOfSymbolBoxParcel;
    this.aOR = paramBoundingBoxParcel1;
    this.aOS = paramBoundingBoxParcel2;
    this.aOU = paramString1;
    this.aOV = paramFloat;
    this.aOK = paramString2;
    this.aPc = paramBoolean;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzi.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/text/internal/client/WordBoxParcel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */