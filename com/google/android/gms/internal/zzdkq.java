package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;

public final class zzdkq
  extends zzbfm
{
  public static final Parcelable.Creator<zzdkq> CREATOR = new zzdkr();
  private byte[] zzlef;
  
  zzdkq()
  {
    this(new byte[0]);
  }
  
  public zzdkq(byte[] paramArrayOfByte)
  {
    this.zzlef = paramArrayOfByte;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = zzbfp.zze(paramParcel);
    zzbfp.zza(paramParcel, 2, this.zzlef, false);
    zzbfp.zzai(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzdkq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */