package com.google.android.gms.common.stats;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;
import java.util.ArrayList;

public class zzf
  implements Parcelable.Creator<WakeLockEvent>
{
  static void zza(WakeLockEvent paramWakeLockEvent, Parcel paramParcel, int paramInt)
  {
    paramInt = zzc.zzaV(paramParcel);
    zzc.zzc(paramParcel, 1, paramWakeLockEvent.mVersionCode);
    zzc.zza(paramParcel, 2, paramWakeLockEvent.getTimeMillis());
    zzc.zza(paramParcel, 4, paramWakeLockEvent.zzyg(), false);
    zzc.zzc(paramParcel, 5, paramWakeLockEvent.zzyj());
    zzc.zzb(paramParcel, 6, paramWakeLockEvent.zzyk(), false);
    zzc.zza(paramParcel, 8, paramWakeLockEvent.zzym());
    zzc.zza(paramParcel, 10, paramWakeLockEvent.zzyh(), false);
    zzc.zzc(paramParcel, 11, paramWakeLockEvent.getEventType());
    zzc.zza(paramParcel, 12, paramWakeLockEvent.zzyl(), false);
    zzc.zza(paramParcel, 13, paramWakeLockEvent.zzyo(), false);
    zzc.zzc(paramParcel, 14, paramWakeLockEvent.zzyn());
    zzc.zza(paramParcel, 15, paramWakeLockEvent.zzyp());
    zzc.zza(paramParcel, 16, paramWakeLockEvent.zzyq());
    zzc.zza(paramParcel, 17, paramWakeLockEvent.zzyi(), false);
    zzc.zzJ(paramParcel, paramInt);
  }
  
  public WakeLockEvent zzbf(Parcel paramParcel)
  {
    int n = zzb.zzaU(paramParcel);
    int m = 0;
    long l3 = 0L;
    int k = 0;
    String str5 = null;
    int j = 0;
    ArrayList localArrayList = null;
    String str4 = null;
    long l2 = 0L;
    int i = 0;
    String str3 = null;
    String str2 = null;
    float f = 0.0F;
    long l1 = 0L;
    String str1 = null;
    while (paramParcel.dataPosition() < n)
    {
      int i1 = zzb.zzaT(paramParcel);
      switch (zzb.zzcW(i1))
      {
      case 3: 
      case 7: 
      case 9: 
      default: 
        zzb.zzb(paramParcel, i1);
        break;
      case 1: 
        m = zzb.zzg(paramParcel, i1);
        break;
      case 2: 
        l3 = zzb.zzi(paramParcel, i1);
        break;
      case 4: 
        str5 = zzb.zzq(paramParcel, i1);
        break;
      case 5: 
        j = zzb.zzg(paramParcel, i1);
        break;
      case 6: 
        localArrayList = zzb.zzE(paramParcel, i1);
        break;
      case 8: 
        l2 = zzb.zzi(paramParcel, i1);
        break;
      case 10: 
        str3 = zzb.zzq(paramParcel, i1);
        break;
      case 11: 
        k = zzb.zzg(paramParcel, i1);
        break;
      case 12: 
        str4 = zzb.zzq(paramParcel, i1);
        break;
      case 13: 
        str2 = zzb.zzq(paramParcel, i1);
        break;
      case 14: 
        i = zzb.zzg(paramParcel, i1);
        break;
      case 15: 
        f = zzb.zzl(paramParcel, i1);
        break;
      case 16: 
        l1 = zzb.zzi(paramParcel, i1);
        break;
      case 17: 
        str1 = zzb.zzq(paramParcel, i1);
      }
    }
    if (paramParcel.dataPosition() != n) {
      throw new zzb.zza(37 + "Overread allowed size end=" + n, paramParcel);
    }
    return new WakeLockEvent(m, l3, k, str5, j, localArrayList, str4, l2, i, str3, str2, f, l1, str1);
  }
  
  public WakeLockEvent[] zzdh(int paramInt)
  {
    return new WakeLockEvent[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/stats/zzf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */