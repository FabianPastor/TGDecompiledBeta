package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.wearable.ConnectionConfiguration;

public final class zzcw
  extends zza
{
  public static final Parcelable.Creator<zzcw> CREATOR = new zzcx();
  private int statusCode;
  private ConnectionConfiguration[] zzbSN;
  
  public zzcw(int paramInt, ConnectionConfiguration[] paramArrayOfConnectionConfiguration)
  {
    this.statusCode = paramInt;
    this.zzbSN = paramArrayOfConnectionConfiguration;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = zzd.zze(paramParcel);
    zzd.zzc(paramParcel, 2, this.statusCode);
    zzd.zza(paramParcel, 3, this.zzbSN, paramInt, false);
    zzd.zzI(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzcw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */