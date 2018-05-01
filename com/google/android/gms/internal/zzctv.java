package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbp;

public final class zzctv
  extends zza
{
  public static final Parcelable.Creator<zzctv> CREATOR = new zzctw();
  private int zzaku;
  private zzbp zzbCU;
  
  zzctv(int paramInt, zzbp paramzzbp)
  {
    this.zzaku = paramInt;
    this.zzbCU = paramzzbp;
  }
  
  public zzctv(zzbp paramzzbp)
  {
    this(1, paramzzbp);
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = zzd.zze(paramParcel);
    zzd.zzc(paramParcel, 1, this.zzaku);
    zzd.zza(paramParcel, 2, this.zzbCU, paramInt, false);
    zzd.zzI(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzctv.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */