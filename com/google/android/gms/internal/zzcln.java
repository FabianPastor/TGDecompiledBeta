package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.zzbq;

public final class zzcln
  extends zzbfm
{
  public static final Parcelable.Creator<zzcln> CREATOR = new zzclo();
  public final String name;
  private int versionCode;
  private String zzgcc;
  public final String zziyf;
  public final long zzjji;
  private Long zzjjj;
  private Float zzjjk;
  private Double zzjjl;
  
  zzcln(int paramInt, String paramString1, long paramLong, Long paramLong1, Float paramFloat, String paramString2, String paramString3, Double paramDouble)
  {
    this.versionCode = paramInt;
    this.name = paramString1;
    this.zzjji = paramLong;
    this.zzjjj = paramLong1;
    this.zzjjk = null;
    if (paramInt == 1)
    {
      paramString1 = (String)localObject;
      if (paramFloat != null) {
        paramString1 = Double.valueOf(paramFloat.doubleValue());
      }
    }
    for (this.zzjjl = paramString1;; this.zzjjl = paramDouble)
    {
      this.zzgcc = paramString2;
      this.zziyf = paramString3;
      return;
    }
  }
  
  zzcln(zzclp paramzzclp)
  {
    this(paramzzclp.mName, paramzzclp.zzjjm, paramzzclp.mValue, paramzzclp.mOrigin);
  }
  
  zzcln(String paramString1, long paramLong, Object paramObject, String paramString2)
  {
    zzbq.zzgm(paramString1);
    this.versionCode = 2;
    this.name = paramString1;
    this.zzjji = paramLong;
    this.zziyf = paramString2;
    if (paramObject == null)
    {
      this.zzjjj = null;
      this.zzjjk = null;
      this.zzjjl = null;
      this.zzgcc = null;
      return;
    }
    if ((paramObject instanceof Long))
    {
      this.zzjjj = ((Long)paramObject);
      this.zzjjk = null;
      this.zzjjl = null;
      this.zzgcc = null;
      return;
    }
    if ((paramObject instanceof String))
    {
      this.zzjjj = null;
      this.zzjjk = null;
      this.zzjjl = null;
      this.zzgcc = ((String)paramObject);
      return;
    }
    if ((paramObject instanceof Double))
    {
      this.zzjjj = null;
      this.zzjjk = null;
      this.zzjjl = ((Double)paramObject);
      this.zzgcc = null;
      return;
    }
    throw new IllegalArgumentException("User attribute given of un-supported type");
  }
  
  public final Object getValue()
  {
    if (this.zzjjj != null) {
      return this.zzjjj;
    }
    if (this.zzjjl != null) {
      return this.zzjjl;
    }
    if (this.zzgcc != null) {
      return this.zzgcc;
    }
    return null;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = zzbfp.zze(paramParcel);
    zzbfp.zzc(paramParcel, 1, this.versionCode);
    zzbfp.zza(paramParcel, 2, this.name, false);
    zzbfp.zza(paramParcel, 3, this.zzjji);
    zzbfp.zza(paramParcel, 4, this.zzjjj, false);
    zzbfp.zza(paramParcel, 5, null, false);
    zzbfp.zza(paramParcel, 6, this.zzgcc, false);
    zzbfp.zza(paramParcel, 7, this.zziyf, false);
    zzbfp.zza(paramParcel, 8, this.zzjjl, false);
    zzbfp.zzai(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcln.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */