package com.google.android.gms.internal;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class zzctn
  extends zza
  implements Result
{
  public static final Parcelable.Creator<zzctn> CREATOR = new zzcto();
  private int zzaku;
  private int zzbCR;
  private Intent zzbCS;
  
  public zzctn()
  {
    this(0, null);
  }
  
  zzctn(int paramInt1, int paramInt2, Intent paramIntent)
  {
    this.zzaku = paramInt1;
    this.zzbCR = paramInt2;
    this.zzbCS = paramIntent;
  }
  
  private zzctn(int paramInt, Intent paramIntent)
  {
    this(2, 0, null);
  }
  
  public final Status getStatus()
  {
    if (this.zzbCR == 0) {
      return Status.zzaBm;
    }
    return Status.zzaBq;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = zzd.zze(paramParcel);
    zzd.zzc(paramParcel, 1, this.zzaku);
    zzd.zzc(paramParcel, 2, this.zzbCR);
    zzd.zza(paramParcel, 3, this.zzbCS, paramInt, false);
    zzd.zzI(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzctn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */