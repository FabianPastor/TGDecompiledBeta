package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class zzcr
  extends zza
{
  public static final Parcelable.Creator<zzcr> CREATOR = new zzcq();
  private int statusCode;
  private boolean zzbSK;
  private boolean zzbSL;
  
  public zzcr(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.statusCode = paramInt;
    this.zzbSK = paramBoolean1;
    this.zzbSL = paramBoolean2;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = zzd.zze(paramParcel);
    zzd.zzc(paramParcel, 2, this.statusCode);
    zzd.zza(paramParcel, 3, this.zzbSK);
    zzd.zza(paramParcel, 4, this.zzbSL);
    zzd.zzI(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzcr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */