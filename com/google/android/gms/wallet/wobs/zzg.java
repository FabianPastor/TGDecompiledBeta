package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class zzg
  extends zza
{
  public static final Parcelable.Creator<zzg> CREATOR = new zzj();
  private String label;
  private String type;
  private zzm zzbPe;
  private zzh zzbQK;
  
  zzg() {}
  
  zzg(String paramString1, zzh paramzzh, String paramString2, zzm paramzzm)
  {
    this.label = paramString1;
    this.zzbQK = paramzzh;
    this.type = paramString2;
    this.zzbPe = paramzzm;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = zzd.zze(paramParcel);
    zzd.zza(paramParcel, 2, this.label, false);
    zzd.zza(paramParcel, 3, this.zzbQK, paramInt, false);
    zzd.zza(paramParcel, 4, this.type, false);
    zzd.zza(paramParcel, 5, this.zzbPe, paramInt, false);
    zzd.zzI(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/wobs/zzg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */