package com.google.android.gms.measurement.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzaa;

public class UserAttributeParcel
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<UserAttributeParcel> CREATOR = new zzaj();
  public final String Fe;
  public final String arK;
  public final long avT;
  public final Long avU;
  public final Float avV;
  public final Double avW;
  public final String name;
  public final int versionCode;
  
  UserAttributeParcel(int paramInt, String paramString1, long paramLong, Long paramLong1, Float paramFloat, String paramString2, String paramString3, Double paramDouble)
  {
    this.versionCode = paramInt;
    this.name = paramString1;
    this.avT = paramLong;
    this.avU = paramLong1;
    this.avV = null;
    if (paramInt == 1)
    {
      paramString1 = (String)localObject;
      if (paramFloat != null) {
        paramString1 = Double.valueOf(paramFloat.doubleValue());
      }
    }
    for (this.avW = paramString1;; this.avW = paramDouble)
    {
      this.Fe = paramString2;
      this.arK = paramString3;
      return;
    }
  }
  
  UserAttributeParcel(zzak paramzzak)
  {
    this(paramzzak.mName, paramzzak.avX, paramzzak.zzcyd, paramzzak.zzctj);
  }
  
  UserAttributeParcel(String paramString1, long paramLong, Object paramObject, String paramString2)
  {
    zzaa.zzib(paramString1);
    this.versionCode = 2;
    this.name = paramString1;
    this.avT = paramLong;
    this.arK = paramString2;
    if (paramObject == null)
    {
      this.avU = null;
      this.avV = null;
      this.avW = null;
      this.Fe = null;
      return;
    }
    if ((paramObject instanceof Long))
    {
      this.avU = ((Long)paramObject);
      this.avV = null;
      this.avW = null;
      this.Fe = null;
      return;
    }
    if ((paramObject instanceof String))
    {
      this.avU = null;
      this.avV = null;
      this.avW = null;
      this.Fe = ((String)paramObject);
      return;
    }
    if ((paramObject instanceof Double))
    {
      this.avU = null;
      this.avV = null;
      this.avW = ((Double)paramObject);
      this.Fe = null;
      return;
    }
    throw new IllegalArgumentException("User attribute given of un-supported type");
  }
  
  public Object getValue()
  {
    if (this.avU != null) {
      return this.avU;
    }
    if (this.avW != null) {
      return this.avW;
    }
    if (this.Fe != null) {
      return this.Fe;
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