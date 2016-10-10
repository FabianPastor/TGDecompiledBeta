package com.google.android.gms.measurement.internal;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzac;

public class UserAttributeParcel
  extends AbstractSafeParcelable
{
  public static final zzaj CREATOR = new zzaj();
  public final String Dr;
  public final String aoA;
  public final long asu;
  public final Long asv;
  public final Float asw;
  public final Double asx;
  public final String name;
  public final int versionCode;
  
  UserAttributeParcel(int paramInt, String paramString1, long paramLong, Long paramLong1, Float paramFloat, String paramString2, String paramString3, Double paramDouble)
  {
    this.versionCode = paramInt;
    this.name = paramString1;
    this.asu = paramLong;
    this.asv = paramLong1;
    this.asw = null;
    if (paramInt == 1)
    {
      paramString1 = (String)localObject;
      if (paramFloat != null) {
        paramString1 = Double.valueOf(paramFloat.doubleValue());
      }
    }
    for (this.asx = paramString1;; this.asx = paramDouble)
    {
      this.Dr = paramString2;
      this.aoA = paramString3;
      return;
    }
  }
  
  UserAttributeParcel(zzak paramzzak)
  {
    this(paramzzak.mName, paramzzak.asy, paramzzak.zzctv, paramzzak.zzcpe);
  }
  
  UserAttributeParcel(String paramString1, long paramLong, Object paramObject, String paramString2)
  {
    zzac.zzhz(paramString1);
    this.versionCode = 2;
    this.name = paramString1;
    this.asu = paramLong;
    this.aoA = paramString2;
    if (paramObject == null)
    {
      this.asv = null;
      this.asw = null;
      this.asx = null;
      this.Dr = null;
      return;
    }
    if ((paramObject instanceof Long))
    {
      this.asv = ((Long)paramObject);
      this.asw = null;
      this.asx = null;
      this.Dr = null;
      return;
    }
    if ((paramObject instanceof String))
    {
      this.asv = null;
      this.asw = null;
      this.asx = null;
      this.Dr = ((String)paramObject);
      return;
    }
    if ((paramObject instanceof Double))
    {
      this.asv = null;
      this.asw = null;
      this.asx = ((Double)paramObject);
      this.Dr = null;
      return;
    }
    throw new IllegalArgumentException("User attribute given of un-supported type");
  }
  
  public Object getValue()
  {
    if (this.asv != null) {
      return this.asv;
    }
    if (this.asx != null) {
      return this.asx;
    }
    if (this.Dr != null) {
      return this.Dr;
    }
    return null;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzaj.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/UserAttributeParcel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */