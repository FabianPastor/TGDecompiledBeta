package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class fe
  extends zza
{
  public static final Parcelable.Creator<fe> CREATOR = new ff();
  public final int height;
  public final int left;
  public final int top;
  public final int width;
  public final float zzbNW;
  
  public fe(int paramInt1, int paramInt2, int paramInt3, int paramInt4, float paramFloat)
  {
    this.left = paramInt1;
    this.top = paramInt2;
    this.width = paramInt3;
    this.height = paramInt4;
    this.zzbNW = paramFloat;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = zzd.zze(paramParcel);
    zzd.zzc(paramParcel, 2, this.left);
    zzd.zzc(paramParcel, 3, this.top);
    zzd.zzc(paramParcel, 4, this.width);
    zzd.zzc(paramParcel, 5, this.height);
    zzd.zza(paramParcel, 6, this.zzbNW);
    zzd.zzI(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/fe.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */