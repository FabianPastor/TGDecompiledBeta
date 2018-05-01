package com.google.android.gms.internal;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzbkj
  extends zza
{
  public static final Parcelable.Creator<zzbkj> CREATOR = new zzbkk();
  final int versionCode;
  public final Rect zzbPJ;
  
  public zzbkj()
  {
    this.versionCode = 1;
    this.zzbPJ = new Rect();
  }
  
  public zzbkj(int paramInt, Rect paramRect)
  {
    this.versionCode = paramInt;
    this.zzbPJ = paramRect;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzbkk.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbkj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */