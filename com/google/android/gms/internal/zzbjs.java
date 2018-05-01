package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzbjs
  implements Parcelable.Creator<zzbjr>
{
  static void zza(zzbjr paramzzbjr, Parcel paramParcel, int paramInt)
  {
    int i = zzc.zzaZ(paramParcel);
    zzc.zzc(paramParcel, 1, paramzzbjr.versionCode);
    zzc.zzc(paramParcel, 2, paramzzbjr.id);
    zzc.zza(paramParcel, 3, paramzzbjr.centerX);
    zzc.zza(paramParcel, 4, paramzzbjr.centerY);
    zzc.zza(paramParcel, 5, paramzzbjr.width);
    zzc.zza(paramParcel, 6, paramzzbjr.height);
    zzc.zza(paramParcel, 7, paramzzbjr.zzbPb);
    zzc.zza(paramParcel, 8, paramzzbjr.zzbPc);
    zzc.zza(paramParcel, 9, paramzzbjr.zzbPd, paramInt, false);
    zzc.zza(paramParcel, 10, paramzzbjr.zzbPe);
    zzc.zza(paramParcel, 11, paramzzbjr.zzbPf);
    zzc.zza(paramParcel, 12, paramzzbjr.zzbPg);
    zzc.zzJ(paramParcel, i);
  }
  
  public zzbjr zzjN(Parcel paramParcel)
  {
    int k = zzb.zzaY(paramParcel);
    int j = 0;
    int i = 0;
    float f9 = 0.0F;
    float f8 = 0.0F;
    float f7 = 0.0F;
    float f6 = 0.0F;
    float f5 = 0.0F;
    float f4 = 0.0F;
    zzbjx[] arrayOfzzbjx = null;
    float f3 = 0.0F;
    float f2 = 0.0F;
    float f1 = 0.0F;
    while (paramParcel.dataPosition() < k)
    {
      int m = zzb.zzaX(paramParcel);
      switch (zzb.zzdc(m))
      {
      default: 
        zzb.zzb(paramParcel, m);
        break;
      case 1: 
        j = zzb.zzg(paramParcel, m);
        break;
      case 2: 
        i = zzb.zzg(paramParcel, m);
        break;
      case 3: 
        f9 = zzb.zzl(paramParcel, m);
        break;
      case 4: 
        f8 = zzb.zzl(paramParcel, m);
        break;
      case 5: 
        f7 = zzb.zzl(paramParcel, m);
        break;
      case 6: 
        f6 = zzb.zzl(paramParcel, m);
        break;
      case 7: 
        f5 = zzb.zzl(paramParcel, m);
        break;
      case 8: 
        f4 = zzb.zzl(paramParcel, m);
        break;
      case 9: 
        arrayOfzzbjx = (zzbjx[])zzb.zzb(paramParcel, m, zzbjx.CREATOR);
        break;
      case 10: 
        f3 = zzb.zzl(paramParcel, m);
        break;
      case 11: 
        f2 = zzb.zzl(paramParcel, m);
        break;
      case 12: 
        f1 = zzb.zzl(paramParcel, m);
      }
    }
    if (paramParcel.dataPosition() != k) {
      throw new zzb.zza(37 + "Overread allowed size end=" + k, paramParcel);
    }
    return new zzbjr(j, i, f9, f8, f7, f6, f5, f4, arrayOfzzbjx, f3, f2, f1);
  }
  
  public zzbjr[] zzoi(int paramInt)
  {
    return new zzbjr[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbjs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */