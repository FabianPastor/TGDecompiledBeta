package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzbkq
  extends zza
{
  public static final Parcelable.Creator<zzbkq> CREATOR = new zzbkr();
  public final int versionCode;
  public final zzbkd zzbPA;
  public final zzbkd zzbPB;
  public final String zzbPD;
  public final float zzbPE;
  public final zzbkl[] zzbPK;
  public final boolean zzbPL;
  public final String zzbPu;
  
  public zzbkq(int paramInt, zzbkl[] paramArrayOfzzbkl, zzbkd paramzzbkd1, zzbkd paramzzbkd2, String paramString1, float paramFloat, String paramString2, boolean paramBoolean)
  {
    this.versionCode = paramInt;
    this.zzbPK = paramArrayOfzzbkl;
    this.zzbPA = paramzzbkd1;
    this.zzbPB = paramzzbkd2;
    this.zzbPD = paramString1;
    this.zzbPE = paramFloat;
    this.zzbPu = paramString2;
    this.zzbPL = paramBoolean;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzbkr.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbkq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */