package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzbht
  extends zza
{
  public static final Parcelable.Creator<zzbht> CREATOR = new zzbhu();
  public final int versionCode;
  public final zzbhg zzbNC;
  public final zzbhg zzbND;
  public final String zzbNF;
  public final float zzbNG;
  public final zzbho[] zzbNM;
  public final boolean zzbNN;
  public final String zzbNw;
  
  public zzbht(int paramInt, zzbho[] paramArrayOfzzbho, zzbhg paramzzbhg1, zzbhg paramzzbhg2, String paramString1, float paramFloat, String paramString2, boolean paramBoolean)
  {
    this.versionCode = paramInt;
    this.zzbNM = paramArrayOfzzbho;
    this.zzbNC = paramzzbhg1;
    this.zzbND = paramzzbhg2;
    this.zzbNF = paramString1;
    this.zzbNG = paramFloat;
    this.zzbNw = paramString2;
    this.zzbNN = paramBoolean;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzbhu.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbht.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */