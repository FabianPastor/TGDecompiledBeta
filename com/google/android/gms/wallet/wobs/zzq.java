package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class zzq
  extends zza
{
  public static final Parcelable.Creator<zzq> CREATOR = new zzr();
  private String body;
  private String zzbQQ;
  private zzm zzbQU;
  private zzo zzbQV;
  private zzo zzbQW;
  
  zzq() {}
  
  zzq(String paramString1, String paramString2, zzm paramzzm, zzo paramzzo1, zzo paramzzo2)
  {
    this.zzbQQ = paramString1;
    this.body = paramString2;
    this.zzbQU = paramzzm;
    this.zzbQV = paramzzo1;
    this.zzbQW = paramzzo2;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = zzd.zze(paramParcel);
    zzd.zza(paramParcel, 2, this.zzbQQ, false);
    zzd.zza(paramParcel, 3, this.body, false);
    zzd.zza(paramParcel, 4, this.zzbQU, paramInt, false);
    zzd.zza(paramParcel, 5, this.zzbQV, paramInt, false);
    zzd.zza(paramParcel, 6, this.zzbQW, paramInt, false);
    zzd.zzI(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/wobs/zzq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */