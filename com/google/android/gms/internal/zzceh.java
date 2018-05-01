package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbo;

public final class zzceh
  extends zza
{
  public static final Parcelable.Creator<zzceh> CREATOR = new zzcei();
  public final String packageName;
  public final String zzbgW;
  public final String zzboQ;
  public final String zzboR;
  public final long zzboS;
  public final long zzboT;
  public final String zzboU;
  public final boolean zzboV;
  public final boolean zzboW;
  public final long zzboX;
  public final String zzboY;
  public final long zzboZ;
  public final long zzbpa;
  public final int zzbpb;
  
  zzceh(String paramString1, String paramString2, String paramString3, long paramLong1, String paramString4, long paramLong2, long paramLong3, String paramString5, boolean paramBoolean1, boolean paramBoolean2, String paramString6, long paramLong4, long paramLong5, int paramInt)
  {
    zzbo.zzcF(paramString1);
    this.packageName = paramString1;
    paramString1 = paramString2;
    if (TextUtils.isEmpty(paramString2)) {
      paramString1 = null;
    }
    this.zzboQ = paramString1;
    this.zzbgW = paramString3;
    this.zzboX = paramLong1;
    this.zzboR = paramString4;
    this.zzboS = paramLong2;
    this.zzboT = paramLong3;
    this.zzboU = paramString5;
    this.zzboV = paramBoolean1;
    this.zzboW = paramBoolean2;
    this.zzboY = paramString6;
    this.zzboZ = paramLong4;
    this.zzbpa = paramLong5;
    this.zzbpb = paramInt;
  }
  
  zzceh(String paramString1, String paramString2, String paramString3, String paramString4, long paramLong1, long paramLong2, String paramString5, boolean paramBoolean1, boolean paramBoolean2, long paramLong3, String paramString6, long paramLong4, long paramLong5, int paramInt)
  {
    this.packageName = paramString1;
    this.zzboQ = paramString2;
    this.zzbgW = paramString3;
    this.zzboX = paramLong3;
    this.zzboR = paramString4;
    this.zzboS = paramLong1;
    this.zzboT = paramLong2;
    this.zzboU = paramString5;
    this.zzboV = paramBoolean1;
    this.zzboW = paramBoolean2;
    this.zzboY = paramString6;
    this.zzboZ = paramLong4;
    this.zzbpa = paramLong5;
    this.zzbpb = paramInt;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = zzd.zze(paramParcel);
    zzd.zza(paramParcel, 2, this.packageName, false);
    zzd.zza(paramParcel, 3, this.zzboQ, false);
    zzd.zza(paramParcel, 4, this.zzbgW, false);
    zzd.zza(paramParcel, 5, this.zzboR, false);
    zzd.zza(paramParcel, 6, this.zzboS);
    zzd.zza(paramParcel, 7, this.zzboT);
    zzd.zza(paramParcel, 8, this.zzboU, false);
    zzd.zza(paramParcel, 9, this.zzboV);
    zzd.zza(paramParcel, 10, this.zzboW);
    zzd.zza(paramParcel, 11, this.zzboX);
    zzd.zza(paramParcel, 12, this.zzboY, false);
    zzd.zza(paramParcel, 13, this.zzboZ);
    zzd.zza(paramParcel, 14, this.zzbpa);
    zzd.zzc(paramParcel, 15, this.zzbpb);
    zzd.zzI(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzceh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */