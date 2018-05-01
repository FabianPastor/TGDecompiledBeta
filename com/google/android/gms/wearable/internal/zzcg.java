package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import java.util.List;

public final class zzcg
  extends zza
{
  public static final Parcelable.Creator<zzcg> CREATOR = new zzch();
  public final int statusCode;
  public final List<zzaa> zzbSG;
  
  public zzcg(int paramInt, List<zzaa> paramList)
  {
    this.statusCode = paramInt;
    this.zzbSG = paramList;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = zzd.zze(paramParcel);
    zzd.zzc(paramParcel, 2, this.statusCode);
    zzd.zzc(paramParcel, 3, this.zzbSG, false);
    zzd.zzI(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzcg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */