package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzate
  implements Parcelable.Creator<zzatd>
{
  static void zza(zzatd paramzzatd, Parcel paramParcel, int paramInt)
  {
    paramInt = zzc.zzaZ(paramParcel);
    zzc.zza(paramParcel, 2, paramzzatd.packageName, false);
    zzc.zza(paramParcel, 3, paramzzatd.zzbqK, false);
    zzc.zza(paramParcel, 4, paramzzatd.zzbhN, false);
    zzc.zza(paramParcel, 5, paramzzatd.zzbqL, false);
    zzc.zza(paramParcel, 6, paramzzatd.zzbqM);
    zzc.zza(paramParcel, 7, paramzzatd.zzbqN);
    zzc.zza(paramParcel, 8, paramzzatd.zzbqO, false);
    zzc.zza(paramParcel, 9, paramzzatd.zzbqP);
    zzc.zza(paramParcel, 10, paramzzatd.zzbqQ);
    zzc.zza(paramParcel, 11, paramzzatd.zzbqR);
    zzc.zza(paramParcel, 12, paramzzatd.zzbqS, false);
    zzc.zza(paramParcel, 13, paramzzatd.zzbqT);
    zzc.zza(paramParcel, 14, paramzzatd.zzbqU);
    zzc.zzc(paramParcel, 15, paramzzatd.zzbqV);
    zzc.zzJ(paramParcel, paramInt);
  }
  
  public zzatd zzhQ(Parcel paramParcel)
  {
    int j = zzb.zzaY(paramParcel);
    String str6 = null;
    String str5 = null;
    String str4 = null;
    String str3 = null;
    long l5 = 0L;
    long l4 = 0L;
    String str2 = null;
    boolean bool2 = true;
    boolean bool1 = false;
    long l3 = -2147483648L;
    String str1 = null;
    long l2 = 0L;
    long l1 = 0L;
    int i = 0;
    while (paramParcel.dataPosition() < j)
    {
      int k = zzb.zzaX(paramParcel);
      switch (zzb.zzdc(k))
      {
      default: 
        zzb.zzb(paramParcel, k);
        break;
      case 2: 
        str6 = zzb.zzq(paramParcel, k);
        break;
      case 3: 
        str5 = zzb.zzq(paramParcel, k);
        break;
      case 4: 
        str4 = zzb.zzq(paramParcel, k);
        break;
      case 5: 
        str3 = zzb.zzq(paramParcel, k);
        break;
      case 6: 
        l5 = zzb.zzi(paramParcel, k);
        break;
      case 7: 
        l4 = zzb.zzi(paramParcel, k);
        break;
      case 8: 
        str2 = zzb.zzq(paramParcel, k);
        break;
      case 9: 
        bool2 = zzb.zzc(paramParcel, k);
        break;
      case 10: 
        bool1 = zzb.zzc(paramParcel, k);
        break;
      case 11: 
        l3 = zzb.zzi(paramParcel, k);
        break;
      case 12: 
        str1 = zzb.zzq(paramParcel, k);
        break;
      case 13: 
        l2 = zzb.zzi(paramParcel, k);
        break;
      case 14: 
        l1 = zzb.zzi(paramParcel, k);
        break;
      case 15: 
        i = zzb.zzg(paramParcel, k);
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zzb.zza(37 + "Overread allowed size end=" + j, paramParcel);
    }
    return new zzatd(str6, str5, str4, str3, l5, l4, str2, bool2, bool1, l3, str1, l2, l1, i);
  }
  
  public zzatd[] zzlz(int paramInt)
  {
    return new zzatd[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */