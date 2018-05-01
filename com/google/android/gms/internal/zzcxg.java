package com.google.android.gms.internal;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;

public final class zzcxg
  extends zzbfm
  implements Result
{
  public static final Parcelable.Creator<zzcxg> CREATOR = new zzcxh();
  private int zzeck;
  private int zzkbx;
  private Intent zzkby;
  
  public zzcxg()
  {
    this(0, null);
  }
  
  zzcxg(int paramInt1, int paramInt2, Intent paramIntent)
  {
    this.zzeck = paramInt1;
    this.zzkbx = paramInt2;
    this.zzkby = paramIntent;
  }
  
  private zzcxg(int paramInt, Intent paramIntent)
  {
    this(2, 0, null);
  }
  
  public final Status getStatus()
  {
    if (this.zzkbx == 0) {
      return Status.zzfni;
    }
    return Status.zzfnm;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = zzbfp.zze(paramParcel);
    zzbfp.zzc(paramParcel, 1, this.zzeck);
    zzbfp.zzc(paramParcel, 2, this.zzkbx);
    zzbfp.zza(paramParcel, 3, this.zzkby, paramInt, false);
    zzbfp.zzai(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcxg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */