package com.google.android.gms.internal;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzbhm
  extends zza
{
  public static final Parcelable.Creator<zzbhm> CREATOR = new zzbhn();
  final int versionCode;
  public final Rect zzbNL;
  
  public zzbhm()
  {
    this.versionCode = 1;
    this.zzbNL = new Rect();
  }
  
  public zzbhm(int paramInt, Rect paramRect)
  {
    this.versionCode = paramInt;
    this.zzbNL = paramRect;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzbhn.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbhm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */