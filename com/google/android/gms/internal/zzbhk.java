package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzbhk
  extends zza
{
  public static final Parcelable.Creator<zzbhk> CREATOR = new zzbhl();
  public final int versionCode;
  public final zzbht[] zzbNB;
  public final zzbhg zzbNC;
  public final zzbhg zzbND;
  public final zzbhg zzbNE;
  public final String zzbNF;
  public final float zzbNG;
  public final int zzbNH;
  public final boolean zzbNI;
  public final int zzbNJ;
  public final int zzbNK;
  public final String zzbNw;
  
  public zzbhk(int paramInt1, zzbht[] paramArrayOfzzbht, zzbhg paramzzbhg1, zzbhg paramzzbhg2, zzbhg paramzzbhg3, String paramString1, float paramFloat, String paramString2, int paramInt2, boolean paramBoolean, int paramInt3, int paramInt4)
  {
    this.versionCode = paramInt1;
    this.zzbNB = paramArrayOfzzbht;
    this.zzbNC = paramzzbhg1;
    this.zzbND = paramzzbhg2;
    this.zzbNE = paramzzbhg3;
    this.zzbNF = paramString1;
    this.zzbNG = paramFloat;
    this.zzbNw = paramString2;
    this.zzbNH = paramInt2;
    this.zzbNI = paramBoolean;
    this.zzbNJ = paramInt3;
    this.zzbNK = paramInt4;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzbhl.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbhk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */