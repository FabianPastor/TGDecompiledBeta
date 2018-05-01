package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.wearable.ConnectionConfiguration;

@Deprecated
public final class zzcu
  extends zza
{
  public static final Parcelable.Creator<zzcu> CREATOR = new zzcv();
  private int statusCode;
  private ConnectionConfiguration zzbSM;
  
  public zzcu(int paramInt, ConnectionConfiguration paramConnectionConfiguration)
  {
    this.statusCode = paramInt;
    this.zzbSM = paramConnectionConfiguration;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = zzd.zze(paramParcel);
    zzd.zzc(paramParcel, 2, this.statusCode);
    zzd.zza(paramParcel, 3, this.zzbSM, paramInt, false);
    zzd.zzI(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzcu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */