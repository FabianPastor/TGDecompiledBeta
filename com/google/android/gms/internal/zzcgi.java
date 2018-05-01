package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzbq;

public final class zzcgi
  extends zzbfm
{
  public static final Parcelable.Creator<zzcgi> CREATOR = new zzcgj();
  public final String packageName;
  public final String zzifm;
  public final String zzixs;
  public final String zzixt;
  public final long zzixu;
  public final long zzixv;
  public final String zzixw;
  public final boolean zzixx;
  public final boolean zzixy;
  public final long zzixz;
  public final String zziya;
  public final long zziyb;
  public final long zziyc;
  public final int zziyd;
  public final boolean zziye;
  
  zzcgi(String paramString1, String paramString2, String paramString3, long paramLong1, String paramString4, long paramLong2, long paramLong3, String paramString5, boolean paramBoolean1, boolean paramBoolean2, String paramString6, long paramLong4, long paramLong5, int paramInt, boolean paramBoolean3)
  {
    zzbq.zzgm(paramString1);
    this.packageName = paramString1;
    paramString1 = paramString2;
    if (TextUtils.isEmpty(paramString2)) {
      paramString1 = null;
    }
    this.zzixs = paramString1;
    this.zzifm = paramString3;
    this.zzixz = paramLong1;
    this.zzixt = paramString4;
    this.zzixu = paramLong2;
    this.zzixv = paramLong3;
    this.zzixw = paramString5;
    this.zzixx = paramBoolean1;
    this.zzixy = paramBoolean2;
    this.zziya = paramString6;
    this.zziyb = paramLong4;
    this.zziyc = paramLong5;
    this.zziyd = paramInt;
    this.zziye = paramBoolean3;
  }
  
  zzcgi(String paramString1, String paramString2, String paramString3, String paramString4, long paramLong1, long paramLong2, String paramString5, boolean paramBoolean1, boolean paramBoolean2, long paramLong3, String paramString6, long paramLong4, long paramLong5, int paramInt, boolean paramBoolean3)
  {
    this.packageName = paramString1;
    this.zzixs = paramString2;
    this.zzifm = paramString3;
    this.zzixz = paramLong3;
    this.zzixt = paramString4;
    this.zzixu = paramLong1;
    this.zzixv = paramLong2;
    this.zzixw = paramString5;
    this.zzixx = paramBoolean1;
    this.zzixy = paramBoolean2;
    this.zziya = paramString6;
    this.zziyb = paramLong4;
    this.zziyc = paramLong5;
    this.zziyd = paramInt;
    this.zziye = paramBoolean3;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = zzbfp.zze(paramParcel);
    zzbfp.zza(paramParcel, 2, this.packageName, false);
    zzbfp.zza(paramParcel, 3, this.zzixs, false);
    zzbfp.zza(paramParcel, 4, this.zzifm, false);
    zzbfp.zza(paramParcel, 5, this.zzixt, false);
    zzbfp.zza(paramParcel, 6, this.zzixu);
    zzbfp.zza(paramParcel, 7, this.zzixv);
    zzbfp.zza(paramParcel, 8, this.zzixw, false);
    zzbfp.zza(paramParcel, 9, this.zzixx);
    zzbfp.zza(paramParcel, 10, this.zzixy);
    zzbfp.zza(paramParcel, 11, this.zzixz);
    zzbfp.zza(paramParcel, 12, this.zziya, false);
    zzbfp.zza(paramParcel, 13, this.zziyb);
    zzbfp.zza(paramParcel, 14, this.zziyc);
    zzbfp.zzc(paramParcel, 15, this.zziyd);
    zzbfp.zza(paramParcel, 16, this.zziye);
    zzbfp.zzai(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcgi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */