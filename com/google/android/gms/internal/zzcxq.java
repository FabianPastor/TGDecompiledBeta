package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.zzbt;

public final class zzcxq
  extends zzbfm
{
  public static final Parcelable.Creator<zzcxq> CREATOR = new zzcxr();
  private int zzeck;
  private final ConnectionResult zzfoo;
  private final zzbt zzkcc;
  
  public zzcxq(int paramInt)
  {
    this(new ConnectionResult(8, null), null);
  }
  
  zzcxq(int paramInt, ConnectionResult paramConnectionResult, zzbt paramzzbt)
  {
    this.zzeck = paramInt;
    this.zzfoo = paramConnectionResult;
    this.zzkcc = paramzzbt;
  }
  
  private zzcxq(ConnectionResult paramConnectionResult, zzbt paramzzbt)
  {
    this(1, paramConnectionResult, null);
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = zzbfp.zze(paramParcel);
    zzbfp.zzc(paramParcel, 1, this.zzeck);
    zzbfp.zza(paramParcel, 2, this.zzfoo, paramInt, false);
    zzbfp.zza(paramParcel, 3, this.zzkcc, paramInt, false);
    zzbfp.zzai(paramParcel, i);
  }
  
  public final ConnectionResult zzahf()
  {
    return this.zzfoo;
  }
  
  public final zzbt zzbdi()
  {
    return this.zzkcc;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcxq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */