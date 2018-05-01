package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzbkh
  extends zza
{
  public static final Parcelable.Creator<zzbkh> CREATOR = new zzbki();
  public final int versionCode;
  public final zzbkd zzbPA;
  public final zzbkd zzbPB;
  public final zzbkd zzbPC;
  public final String zzbPD;
  public final float zzbPE;
  public final int zzbPF;
  public final boolean zzbPG;
  public final int zzbPH;
  public final int zzbPI;
  public final String zzbPu;
  public final zzbkq[] zzbPz;
  
  public zzbkh(int paramInt1, zzbkq[] paramArrayOfzzbkq, zzbkd paramzzbkd1, zzbkd paramzzbkd2, zzbkd paramzzbkd3, String paramString1, float paramFloat, String paramString2, int paramInt2, boolean paramBoolean, int paramInt3, int paramInt4)
  {
    this.versionCode = paramInt1;
    this.zzbPz = paramArrayOfzzbkq;
    this.zzbPA = paramzzbkd1;
    this.zzbPB = paramzzbkd2;
    this.zzbPC = paramzzbkd3;
    this.zzbPD = paramString1;
    this.zzbPE = paramFloat;
    this.zzbPu = paramString2;
    this.zzbPF = paramInt2;
    this.zzbPG = paramBoolean;
    this.zzbPH = paramInt3;
    this.zzbPI = paramInt4;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzbki.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbkh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */