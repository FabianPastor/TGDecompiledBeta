package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzac;

public class zzauq
  extends zza
{
  public static final Parcelable.Creator<zzauq> CREATOR = new zzaur();
  public final String name;
  public final int versionCode;
  public final String zzaGV;
  public final String zzbqW;
  public final long zzbwf;
  public final Long zzbwg;
  public final Float zzbwh;
  public final Double zzbwi;
  
  zzauq(int paramInt, String paramString1, long paramLong, Long paramLong1, Float paramFloat, String paramString2, String paramString3, Double paramDouble)
  {
    this.versionCode = paramInt;
    this.name = paramString1;
    this.zzbwf = paramLong;
    this.zzbwg = paramLong1;
    this.zzbwh = null;
    if (paramInt == 1)
    {
      paramString1 = (String)localObject;
      if (paramFloat != null) {
        paramString1 = Double.valueOf(paramFloat.doubleValue());
      }
    }
    for (this.zzbwi = paramString1;; this.zzbwi = paramDouble)
    {
      this.zzaGV = paramString2;
      this.zzbqW = paramString3;
      return;
    }
  }
  
  zzauq(zzaus paramzzaus)
  {
    this(paramzzaus.mName, paramzzaus.zzbwj, paramzzaus.mValue, paramzzaus.mOrigin);
  }
  
  zzauq(String paramString1, long paramLong, Object paramObject, String paramString2)
  {
    zzac.zzdr(paramString1);
    this.versionCode = 2;
    this.name = paramString1;
    this.zzbwf = paramLong;
    this.zzbqW = paramString2;
    if (paramObject == null)
    {
      this.zzbwg = null;
      this.zzbwh = null;
      this.zzbwi = null;
      this.zzaGV = null;
      return;
    }
    if ((paramObject instanceof Long))
    {
      this.zzbwg = ((Long)paramObject);
      this.zzbwh = null;
      this.zzbwi = null;
      this.zzaGV = null;
      return;
    }
    if ((paramObject instanceof String))
    {
      this.zzbwg = null;
      this.zzbwh = null;
      this.zzbwi = null;
      this.zzaGV = ((String)paramObject);
      return;
    }
    if ((paramObject instanceof Double))
    {
      this.zzbwg = null;
      this.zzbwh = null;
      this.zzbwi = ((Double)paramObject);
      this.zzaGV = null;
      return;
    }
    throw new IllegalArgumentException("User attribute given of un-supported type");
  }
  
  public Object getValue()
  {
    if (this.zzbwg != null) {
      return this.zzbwg;
    }
    if (this.zzbwi != null) {
      return this.zzbwi;
    }
    if (this.zzaGV != null) {
      return this.zzaGV;
    }
    return null;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzaur.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzauq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */