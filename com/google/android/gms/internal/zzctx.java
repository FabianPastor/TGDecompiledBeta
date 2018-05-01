package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbr;

public final class zzctx
  extends zza
{
  public static final Parcelable.Creator<zzctx> CREATOR = new zzcty();
  private final ConnectionResult zzaBQ;
  private int zzaku;
  private final zzbr zzbCV;
  
  public zzctx(int paramInt)
  {
    this(new ConnectionResult(8, null), null);
  }
  
  zzctx(int paramInt, ConnectionResult paramConnectionResult, zzbr paramzzbr)
  {
    this.zzaku = paramInt;
    this.zzaBQ = paramConnectionResult;
    this.zzbCV = paramzzbr;
  }
  
  private zzctx(ConnectionResult paramConnectionResult, zzbr paramzzbr)
  {
    this(1, paramConnectionResult, null);
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = zzd.zze(paramParcel);
    zzd.zzc(paramParcel, 1, this.zzaku);
    zzd.zza(paramParcel, 2, this.zzaBQ, paramInt, false);
    zzd.zza(paramParcel, 3, this.zzbCV, paramInt, false);
    zzd.zzI(paramParcel, i);
  }
  
  public final zzbr zzAx()
  {
    return this.zzbCV;
  }
  
  public final ConnectionResult zzpz()
  {
    return this.zzaBQ;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzctx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */