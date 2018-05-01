package com.google.android.gms.common.server.converter;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.server.response.FastJsonResponse.zza;

public class ConverterWrapper
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<ConverterWrapper> CREATOR = new zza();
  private final StringToIntConverter Fa;
  final int mVersionCode;
  
  ConverterWrapper(int paramInt, StringToIntConverter paramStringToIntConverter)
  {
    this.mVersionCode = paramInt;
    this.Fa = paramStringToIntConverter;
  }
  
  private ConverterWrapper(StringToIntConverter paramStringToIntConverter)
  {
    this.mVersionCode = 1;
    this.Fa = paramStringToIntConverter;
  }
  
  public static ConverterWrapper zza(FastJsonResponse.zza<?, ?> paramzza)
  {
    if ((paramzza instanceof StringToIntConverter)) {
      return new ConverterWrapper((StringToIntConverter)paramzza);
    }
    throw new IllegalArgumentException("Unsupported safe parcelable field converter class.");
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zza.zza(this, paramParcel, paramInt);
  }
  
  StringToIntConverter zzaww()
  {
    return this.Fa;
  }
  
  public FastJsonResponse.zza<?, ?> zzawx()
  {
    if (this.Fa != null) {
      return this.Fa;
    }
    throw new IllegalStateException("There was no converter wrapped in this ConverterWrapper.");
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/server/converter/ConverterWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */