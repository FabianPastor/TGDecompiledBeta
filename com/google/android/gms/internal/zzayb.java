package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzaf;

public class zzayb
  extends zza
{
  public static final Parcelable.Creator<zzayb> CREATOR = new zzayc();
  final int mVersionCode;
  private final ConnectionResult zzaFh;
  private final zzaf zzbCs;
  
  public zzayb(int paramInt)
  {
    this(new ConnectionResult(paramInt, null), null);
  }
  
  zzayb(int paramInt, ConnectionResult paramConnectionResult, zzaf paramzzaf)
  {
    this.mVersionCode = paramInt;
    this.zzaFh = paramConnectionResult;
    this.zzbCs = paramzzaf;
  }
  
  public zzayb(ConnectionResult paramConnectionResult, zzaf paramzzaf)
  {
    this(1, paramConnectionResult, paramzzaf);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzayc.zza(this, paramParcel, paramInt);
  }
  
  public zzaf zzOp()
  {
    return this.zzbCs;
  }
  
  public ConnectionResult zzxA()
  {
    return this.zzaFh;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzayb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */