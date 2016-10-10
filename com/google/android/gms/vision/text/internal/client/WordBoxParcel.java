package com.google.android.gms.vision.text.internal.client;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public class WordBoxParcel
  extends AbstractSafeParcelable
{
  public static final zzi CREATOR = new zzi();
  public final BoundingBoxParcel aLG;
  public final BoundingBoxParcel aLH;
  public final String aLJ;
  public final float aLK;
  public final SymbolBoxParcel[] aLQ;
  public final boolean aLR;
  public final String aLz;
  public final int versionCode;
  
  public WordBoxParcel(int paramInt, SymbolBoxParcel[] paramArrayOfSymbolBoxParcel, BoundingBoxParcel paramBoundingBoxParcel1, BoundingBoxParcel paramBoundingBoxParcel2, String paramString1, float paramFloat, String paramString2, boolean paramBoolean)
  {
    this.versionCode = paramInt;
    this.aLQ = paramArrayOfSymbolBoxParcel;
    this.aLG = paramBoundingBoxParcel1;
    this.aLH = paramBoundingBoxParcel2;
    this.aLJ = paramString1;
    this.aLK = paramFloat;
    this.aLz = paramString2;
    this.aLR = paramBoolean;
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