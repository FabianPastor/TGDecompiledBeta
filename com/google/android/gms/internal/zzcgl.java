package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.zzbq;

public final class zzcgl
  extends zzbfm
{
  public static final Parcelable.Creator<zzcgl> CREATOR = new zzcgm();
  public String packageName;
  private int versionCode;
  public String zziyf;
  public zzcln zziyg;
  public long zziyh;
  public boolean zziyi;
  public String zziyj;
  public zzcha zziyk;
  public long zziyl;
  public zzcha zziym;
  public long zziyn;
  public zzcha zziyo;
  
  zzcgl(int paramInt, String paramString1, String paramString2, zzcln paramzzcln, long paramLong1, boolean paramBoolean, String paramString3, zzcha paramzzcha1, long paramLong2, zzcha paramzzcha2, long paramLong3, zzcha paramzzcha3)
  {
    this.versionCode = paramInt;
    this.packageName = paramString1;
    this.zziyf = paramString2;
    this.zziyg = paramzzcln;
    this.zziyh = paramLong1;
    this.zziyi = paramBoolean;
    this.zziyj = paramString3;
    this.zziyk = paramzzcha1;
    this.zziyl = paramLong2;
    this.zziym = paramzzcha2;
    this.zziyn = paramLong3;
    this.zziyo = paramzzcha3;
  }
  
  zzcgl(zzcgl paramzzcgl)
  {
    this.versionCode = 1;
    zzbq.checkNotNull(paramzzcgl);
    this.packageName = paramzzcgl.packageName;
    this.zziyf = paramzzcgl.zziyf;
    this.zziyg = paramzzcgl.zziyg;
    this.zziyh = paramzzcgl.zziyh;
    this.zziyi = paramzzcgl.zziyi;
    this.zziyj = paramzzcgl.zziyj;
    this.zziyk = paramzzcgl.zziyk;
    this.zziyl = paramzzcgl.zziyl;
    this.zziym = paramzzcgl.zziym;
    this.zziyn = paramzzcgl.zziyn;
    this.zziyo = paramzzcgl.zziyo;
  }
  
  zzcgl(String paramString1, String paramString2, zzcln paramzzcln, long paramLong1, boolean paramBoolean, String paramString3, zzcha paramzzcha1, long paramLong2, zzcha paramzzcha2, long paramLong3, zzcha paramzzcha3)
  {
    this.versionCode = 1;
    this.packageName = paramString1;
    this.zziyf = paramString2;
    this.zziyg = paramzzcln;
    this.zziyh = paramLong1;
    this.zziyi = paramBoolean;
    this.zziyj = paramString3;
    this.zziyk = paramzzcha1;
    this.zziyl = paramLong2;
    this.zziym = paramzzcha2;
    this.zziyn = paramLong3;
    this.zziyo = paramzzcha3;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = zzbfp.zze(paramParcel);
    zzbfp.zzc(paramParcel, 1, this.versionCode);
    zzbfp.zza(paramParcel, 2, this.packageName, false);
    zzbfp.zza(paramParcel, 3, this.zziyf, false);
    zzbfp.zza(paramParcel, 4, this.zziyg, paramInt, false);
    zzbfp.zza(paramParcel, 5, this.zziyh);
    zzbfp.zza(paramParcel, 6, this.zziyi);
    zzbfp.zza(paramParcel, 7, this.zziyj, false);
    zzbfp.zza(paramParcel, 8, this.zziyk, paramInt, false);
    zzbfp.zza(paramParcel, 9, this.zziyl);
    zzbfp.zza(paramParcel, 10, this.zziym, paramInt, false);
    zzbfp.zza(paramParcel, 11, this.zziyn);
    zzbfp.zza(paramParcel, 12, this.zziyo, paramInt, false);
    zzbfp.zzai(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcgl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */