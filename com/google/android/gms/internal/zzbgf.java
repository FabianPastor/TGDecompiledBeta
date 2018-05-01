package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class zzbgf
  extends zza
{
  public static final Parcelable.Creator<zzbgf> CREATOR = new zzbgh();
  private int versionCode;
  final String zzaIF;
  final int zzaIG;
  
  zzbgf(int paramInt1, String paramString, int paramInt2)
  {
    this.versionCode = paramInt1;
    this.zzaIF = paramString;
    this.zzaIG = paramInt2;
  }
  
  zzbgf(String paramString, int paramInt)
  {
    this.versionCode = 1;
    this.zzaIF = paramString;
    this.zzaIG = paramInt;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = zzd.zze(paramParcel);
    zzd.zzc(paramParcel, 1, this.versionCode);
    zzd.zza(paramParcel, 2, this.zzaIF, false);
    zzd.zzc(paramParcel, 3, this.zzaIG);
    zzd.zzI(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbgf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */