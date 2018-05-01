package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzaza
  implements Parcelable.Creator<zzayz>
{
  static void zza(zzayz paramzzayz, Parcel paramParcel, int paramInt)
  {
    paramInt = zzc.zzaZ(paramParcel);
    zzc.zza(paramParcel, 2, paramzzayz.name, false);
    zzc.zza(paramParcel, 3, paramzzayz.zzbBF);
    zzc.zza(paramParcel, 4, paramzzayz.zzbhn);
    zzc.zza(paramParcel, 5, paramzzayz.zzbhp);
    zzc.zza(paramParcel, 6, paramzzayz.zzaGV, false);
    zzc.zza(paramParcel, 7, paramzzayz.zzbBG, false);
    zzc.zzc(paramParcel, 8, paramzzayz.zzbBH);
    zzc.zzc(paramParcel, 9, paramzzayz.zzbBI);
    zzc.zzJ(paramParcel, paramInt);
  }
  
  public zzayz zzjb(Parcel paramParcel)
  {
    byte[] arrayOfByte = null;
    int i = 0;
    int k = zzb.zzaY(paramParcel);
    long l = 0L;
    double d = 0.0D;
    int j = 0;
    String str1 = null;
    boolean bool = false;
    String str2 = null;
    while (paramParcel.dataPosition() < k)
    {
      int m = zzb.zzaX(paramParcel);
      switch (zzb.zzdc(m))
      {
      default: 
        zzb.zzb(paramParcel, m);
        break;
      case 2: 
        str2 = zzb.zzq(paramParcel, m);
        break;
      case 3: 
        l = zzb.zzi(paramParcel, m);
        break;
      case 4: 
        bool = zzb.zzc(paramParcel, m);
        break;
      case 5: 
        d = zzb.zzn(paramParcel, m);
        break;
      case 6: 
        str1 = zzb.zzq(paramParcel, m);
        break;
      case 7: 
        arrayOfByte = zzb.zzt(paramParcel, m);
        break;
      case 8: 
        j = zzb.zzg(paramParcel, m);
        break;
      case 9: 
        i = zzb.zzg(paramParcel, m);
      }
    }
    if (paramParcel.dataPosition() != k) {
      throw new zzb.zza(37 + "Overread allowed size end=" + k, paramParcel);
    }
    return new zzayz(str2, l, bool, d, str1, arrayOfByte, j, i);
  }
  
  public zzayz[] zzmY(int paramInt)
  {
    return new zzayz[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaza.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */