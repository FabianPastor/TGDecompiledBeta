package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.zzbq;

public final class zzcha
  extends zzbfm
{
  public static final Parcelable.Creator<zzcha> CREATOR = new zzchb();
  public final String name;
  public final String zziyf;
  public final zzcgx zzizt;
  public final long zzizu;
  
  zzcha(zzcha paramzzcha, long paramLong)
  {
    zzbq.checkNotNull(paramzzcha);
    this.name = paramzzcha.name;
    this.zzizt = paramzzcha.zzizt;
    this.zziyf = paramzzcha.zziyf;
    this.zzizu = paramLong;
  }
  
  public zzcha(String paramString1, zzcgx paramzzcgx, String paramString2, long paramLong)
  {
    this.name = paramString1;
    this.zzizt = paramzzcgx;
    this.zziyf = paramString2;
    this.zzizu = paramLong;
  }
  
  public final String toString()
  {
    String str1 = this.zziyf;
    String str2 = this.name;
    String str3 = String.valueOf(this.zzizt);
    return String.valueOf(str1).length() + 21 + String.valueOf(str2).length() + String.valueOf(str3).length() + "origin=" + str1 + ",name=" + str2 + ",params=" + str3;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = zzbfp.zze(paramParcel);
    zzbfp.zza(paramParcel, 2, this.name, false);
    zzbfp.zza(paramParcel, 3, this.zzizt, paramInt, false);
    zzbfp.zza(paramParcel, 4, this.zziyf, false);
    zzbfp.zza(paramParcel, 5, this.zzizu);
    zzbfp.zzai(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcha.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */