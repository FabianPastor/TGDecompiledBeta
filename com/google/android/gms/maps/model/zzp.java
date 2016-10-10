package com.google.android.gms.maps.model;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zza.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzp
  implements Parcelable.Creator<TileOverlayOptions>
{
  static void zza(TileOverlayOptions paramTileOverlayOptions, Parcel paramParcel, int paramInt)
  {
    paramInt = zzb.zzcr(paramParcel);
    zzb.zzc(paramParcel, 1, paramTileOverlayOptions.getVersionCode());
    zzb.zza(paramParcel, 2, paramTileOverlayOptions.zzbsl(), false);
    zzb.zza(paramParcel, 3, paramTileOverlayOptions.isVisible());
    zzb.zza(paramParcel, 4, paramTileOverlayOptions.getZIndex());
    zzb.zza(paramParcel, 5, paramTileOverlayOptions.getFadeIn());
    zzb.zza(paramParcel, 6, paramTileOverlayOptions.getTransparency());
    zzb.zzaj(paramParcel, paramInt);
  }
  
  public TileOverlayOptions zzpc(Parcel paramParcel)
  {
    boolean bool2 = false;
    float f1 = 0.0F;
    int j = zza.zzcq(paramParcel);
    IBinder localIBinder = null;
    boolean bool1 = true;
    float f2 = 0.0F;
    int i = 0;
    while (paramParcel.dataPosition() < j)
    {
      int k = zza.zzcp(paramParcel);
      switch (zza.zzgv(k))
      {
      default: 
        zza.zzb(paramParcel, k);
        break;
      case 1: 
        i = zza.zzg(paramParcel, k);
        break;
      case 2: 
        localIBinder = zza.zzr(paramParcel, k);
        break;
      case 3: 
        bool2 = zza.zzc(paramParcel, k);
        break;
      case 4: 
        f2 = zza.zzl(paramParcel, k);
        break;
      case 5: 
        bool1 = zza.zzc(paramParcel, k);
        break;
      case 6: 
        f1 = zza.zzl(paramParcel, k);
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zza.zza(37 + "Overread allowed size end=" + j, paramParcel);
    }
    return new TileOverlayOptions(i, localIBinder, bool2, f2, bool1, f1);
  }
  
  public TileOverlayOptions[] zzwh(int paramInt)
  {
    return new TileOverlayOptions[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/zzp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */