package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class zzbgq
  extends zza
{
  public static final Parcelable.Creator<zzbgq> CREATOR = new zzbgn();
  final String key;
  private int versionCode;
  final zzbgj<?, ?> zzaIV;
  
  zzbgq(int paramInt, String paramString, zzbgj<?, ?> paramzzbgj)
  {
    this.versionCode = paramInt;
    this.key = paramString;
    this.zzaIV = paramzzbgj;
  }
  
  zzbgq(String paramString, zzbgj<?, ?> paramzzbgj)
  {
    this.versionCode = 1;
    this.key = paramString;
    this.zzaIV = paramzzbgj;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = zzd.zze(paramParcel);
    zzd.zzc(paramParcel, 1, this.versionCode);
    zzd.zza(paramParcel, 2, this.key, false);
    zzd.zza(paramParcel, 3, this.zzaIV, paramInt, false);
    zzd.zzI(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbgq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */