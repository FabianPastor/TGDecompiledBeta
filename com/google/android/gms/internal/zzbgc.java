package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class zzbgc
  extends zza
{
  public static final Parcelable.Creator<zzbgc> CREATOR = new zzbgd();
  private final zzbge zzaIB;
  private int zzaku;
  
  zzbgc(int paramInt, zzbge paramzzbge)
  {
    this.zzaku = paramInt;
    this.zzaIB = paramzzbge;
  }
  
  private zzbgc(zzbge paramzzbge)
  {
    this.zzaku = 1;
    this.zzaIB = paramzzbge;
  }
  
  public static zzbgc zza(zzbgk<?, ?> paramzzbgk)
  {
    if ((paramzzbgk instanceof zzbge)) {
      return new zzbgc((zzbge)paramzzbgk);
    }
    throw new IllegalArgumentException("Unsupported safe parcelable field converter class.");
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = zzd.zze(paramParcel);
    zzd.zzc(paramParcel, 1, this.zzaku);
    zzd.zza(paramParcel, 2, this.zzaIB, paramInt, false);
    zzd.zzI(paramParcel, i);
  }
  
  public final zzbgk<?, ?> zzrK()
  {
    if (this.zzaIB != null) {
      return this.zzaIB;
    }
    throw new IllegalStateException("There was no converter wrapped in this ConverterWrapper.");
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbgc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */