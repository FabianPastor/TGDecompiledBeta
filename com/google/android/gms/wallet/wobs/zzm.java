package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class zzm
  extends zza
{
  public static final Parcelable.Creator<zzm> CREATOR = new zzn();
  private long zzbQR;
  private long zzbQS;
  
  zzm() {}
  
  public zzm(long paramLong1, long paramLong2)
  {
    this.zzbQR = paramLong1;
    this.zzbQS = paramLong2;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = zzd.zze(paramParcel);
    zzd.zza(paramParcel, 2, this.zzbQR);
    zzd.zza(paramParcel, 3, this.zzbQS);
    zzd.zzI(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/wobs/zzm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */