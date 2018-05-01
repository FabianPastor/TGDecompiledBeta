package com.google.android.gms.internal.measurement;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

public final class zzjs
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<zzjs> CREATOR = new zzjt();
  public final String name;
  private final int versionCode;
  public final String zzaek;
  private final String zzajf;
  public final long zzaqu;
  private final Long zzaqv;
  private final Float zzaqw;
  private final Double zzaqx;
  
  zzjs(int paramInt, String paramString1, long paramLong, Long paramLong1, Float paramFloat, String paramString2, String paramString3, Double paramDouble)
  {
    this.versionCode = paramInt;
    this.name = paramString1;
    this.zzaqu = paramLong;
    this.zzaqv = paramLong1;
    this.zzaqw = null;
    if (paramInt == 1)
    {
      paramString1 = (String)localObject;
      if (paramFloat != null) {
        paramString1 = Double.valueOf(paramFloat.doubleValue());
      }
    }
    for (this.zzaqx = paramString1;; this.zzaqx = paramDouble)
    {
      this.zzajf = paramString2;
      this.zzaek = paramString3;
      return;
    }
  }
  
  zzjs(String paramString1, long paramLong, Object paramObject, String paramString2)
  {
    Preconditions.checkNotEmpty(paramString1);
    this.versionCode = 2;
    this.name = paramString1;
    this.zzaqu = paramLong;
    this.zzaek = paramString2;
    if (paramObject == null)
    {
      this.zzaqv = null;
      this.zzaqw = null;
      this.zzaqx = null;
      this.zzajf = null;
    }
    for (;;)
    {
      return;
      if ((paramObject instanceof Long))
      {
        this.zzaqv = ((Long)paramObject);
        this.zzaqw = null;
        this.zzaqx = null;
        this.zzajf = null;
      }
      else if ((paramObject instanceof String))
      {
        this.zzaqv = null;
        this.zzaqw = null;
        this.zzaqx = null;
        this.zzajf = ((String)paramObject);
      }
      else
      {
        if (!(paramObject instanceof Double)) {
          break;
        }
        this.zzaqv = null;
        this.zzaqw = null;
        this.zzaqx = ((Double)paramObject);
        this.zzajf = null;
      }
    }
    throw new IllegalArgumentException("User attribute given of un-supported type");
  }
  
  public final Object getValue()
  {
    Object localObject;
    if (this.zzaqv != null) {
      localObject = this.zzaqv;
    }
    for (;;)
    {
      return localObject;
      if (this.zzaqx != null) {
        localObject = this.zzaqx;
      } else if (this.zzajf != null) {
        localObject = this.zzajf;
      } else {
        localObject = null;
      }
    }
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 1, this.versionCode);
    SafeParcelWriter.writeString(paramParcel, 2, this.name, false);
    SafeParcelWriter.writeLong(paramParcel, 3, this.zzaqu);
    SafeParcelWriter.writeLongObject(paramParcel, 4, this.zzaqv, false);
    SafeParcelWriter.writeFloatObject(paramParcel, 5, null, false);
    SafeParcelWriter.writeString(paramParcel, 6, this.zzajf, false);
    SafeParcelWriter.writeString(paramParcel, 7, this.zzaek, false);
    SafeParcelWriter.writeDoubleObject(paramParcel, 8, this.zzaqx, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzjs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */