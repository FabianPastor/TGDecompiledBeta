package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzbhg
  extends zza
{
  public static final Parcelable.Creator<zzbhg> CREATOR = new zzbhh();
  public final int height;
  public final int left;
  public final int top;
  public final int versionCode;
  public final int width;
  public final float zzbNA;
  
  public zzbhg(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, float paramFloat)
  {
    this.versionCode = paramInt1;
    this.left = paramInt2;
    this.top = paramInt3;
    this.width = paramInt4;
    this.height = paramInt5;
    this.zzbNA = paramFloat;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzbhh.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbhg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */