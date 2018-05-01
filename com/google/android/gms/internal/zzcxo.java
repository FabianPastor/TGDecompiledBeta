package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.zzbr;

public final class zzcxo
  extends zzbfm
{
  public static final Parcelable.Creator<zzcxo> CREATOR = new zzcxp();
  private int zzeck;
  private zzbr zzkcb;
  
  zzcxo(int paramInt, zzbr paramzzbr)
  {
    this.zzeck = paramInt;
    this.zzkcb = paramzzbr;
  }
  
  public zzcxo(zzbr paramzzbr)
  {
    this(1, paramzzbr);
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = zzbfp.zze(paramParcel);
    zzbfp.zzc(paramParcel, 1, this.zzeck);
    zzbfp.zza(paramParcel, 2, this.zzkcb, paramInt, false);
    zzbfp.zzai(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcxo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */