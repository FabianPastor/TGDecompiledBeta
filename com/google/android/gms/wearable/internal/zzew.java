package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import java.util.List;

public final class zzew
  extends zza
{
  public static final Parcelable.Creator<zzew> CREATOR = new zzex();
  private int statusCode;
  private long zzbTc;
  private List<zzek> zzbTe;
  
  public zzew(int paramInt, long paramLong, List<zzek> paramList)
  {
    this.statusCode = paramInt;
    this.zzbTc = paramLong;
    this.zzbTe = paramList;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = zzd.zze(paramParcel);
    zzd.zzc(paramParcel, 2, this.statusCode);
    zzd.zza(paramParcel, 3, this.zzbTc);
    zzd.zzc(paramParcel, 4, this.zzbTe, false);
    zzd.zzI(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzew.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */