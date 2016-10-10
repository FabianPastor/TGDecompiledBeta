package com.google.android.gms.vision.text.internal.client;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public class LineBoxParcel
  extends AbstractSafeParcelable
{
  public static final zzd CREATOR = new zzd();
  public final WordBoxParcel[] aLF;
  public final BoundingBoxParcel aLG;
  public final BoundingBoxParcel aLH;
  public final BoundingBoxParcel aLI;
  public final String aLJ;
  public final float aLK;
  public final int aLL;
  public final boolean aLM;
  public final int aLN;
  public final int aLO;
  public final String aLz;
  public final int versionCode;
  
  public LineBoxParcel(int paramInt1, WordBoxParcel[] paramArrayOfWordBoxParcel, BoundingBoxParcel paramBoundingBoxParcel1, BoundingBoxParcel paramBoundingBoxParcel2, BoundingBoxParcel paramBoundingBoxParcel3, String paramString1, float paramFloat, String paramString2, int paramInt2, boolean paramBoolean, int paramInt3, int paramInt4)
  {
    this.versionCode = paramInt1;
    this.aLF = paramArrayOfWordBoxParcel;
    this.aLG = paramBoundingBoxParcel1;
    this.aLH = paramBoundingBoxParcel2;
    this.aLI = paramBoundingBoxParcel3;
    this.aLJ = paramString1;
    this.aLK = paramFloat;
    this.aLz = paramString2;
    this.aLL = paramInt2;
    this.aLM = paramBoolean;
    this.aLN = paramInt3;
    this.aLO = paramInt4;
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