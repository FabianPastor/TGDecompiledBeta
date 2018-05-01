package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;

public final class zzdks
  extends zzbfm
{
  public static final Parcelable.Creator<zzdks> CREATOR = new zzdkt();
  private byte[] zzleg;
  
  zzdks()
  {
    this(new byte[0]);
  }
  
  public zzdks(byte[] paramArrayOfByte)
  {
    this.zzleg = paramArrayOfByte;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = zzbfp.zze(paramParcel);
    zzbfp.zza(paramParcel, 2, this.zzleg, false);
    zzbfp.zzai(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzdks.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */