package com.google.android.gms.maps;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;

public class zza
  implements Parcelable.Creator<GoogleMapOptions>
{
  static void zza(GoogleMapOptions paramGoogleMapOptions, Parcel paramParcel, int paramInt)
  {
    int i = zzb.zzcr(paramParcel);
    zzb.zzc(paramParcel, 1, paramGoogleMapOptions.getVersionCode());
    zzb.zza(paramParcel, 2, paramGoogleMapOptions.zzbrj());
    zzb.zza(paramParcel, 3, paramGoogleMapOptions.zzbrk());
    zzb.zzc(paramParcel, 4, paramGoogleMapOptions.getMapType());
    zzb.zza(paramParcel, 5, paramGoogleMapOptions.getCamera(), paramInt, false);
    zzb.zza(paramParcel, 6, paramGoogleMapOptions.zzbrl());
    zzb.zza(paramParcel, 7, paramGoogleMapOptions.zzbrm());
    zzb.zza(paramParcel, 8, paramGoogleMapOptions.zzbrn());
    zzb.zza(paramParcel, 9, paramGoogleMapOptions.zzbro());
    zzb.zza(paramParcel, 10, paramGoogleMapOptions.zzbrp());
    zzb.zza(paramParcel, 11, paramGoogleMapOptions.zzbrq());
    zzb.zza(paramParcel, 12, paramGoogleMapOptions.zzbrr());
    zzb.zza(paramParcel, 14, paramGoogleMapOptions.zzbrs());
    zzb.zza(paramParcel, 15, paramGoogleMapOptions.zzbrt());
    zzb.zza(paramParcel, 16, paramGoogleMapOptions.getMinZoomPreference(), false);
    zzb.zza(paramParcel, 17, paramGoogleMapOptions.getMaxZoomPreference(), false);
    zzb.zza(paramParcel, 18, paramGoogleMapOptions.getLatLngBoundsForCameraTarget(), paramInt, false);
    zzb.zzaj(paramParcel, i);
  }
  
  public GoogleMapOptions zzol(Parcel paramParcel)
  {
    int k = com.google.android.gms.common.internal.safeparcel.zza.zzcq(paramParcel);
    int j = 0;
    byte b11 = -1;
    byte b10 = -1;
    int i = 0;
    CameraPosition localCameraPosition = null;
    byte b9 = -1;
    byte b8 = -1;
    byte b7 = -1;
    byte b6 = -1;
    byte b5 = -1;
    byte b4 = -1;
    byte b3 = -1;
    byte b2 = -1;
    byte b1 = -1;
    Float localFloat2 = null;
    Float localFloat1 = null;
    LatLngBounds localLatLngBounds = null;
    while (paramParcel.dataPosition() < k)
    {
      int m = com.google.android.gms.common.internal.safeparcel.zza.zzcp(paramParcel);
      switch (com.google.android.gms.common.internal.safeparcel.zza.zzgv(m))
      {
      case 13: 
      default: 
        com.google.android.gms.common.internal.safeparcel.zza.zzb(paramParcel, m);
        break;
      case 1: 
        j = com.google.android.gms.common.internal.safeparcel.zza.zzg(paramParcel, m);
        break;
      case 2: 
        b11 = com.google.android.gms.common.internal.safeparcel.zza.zze(paramParcel, m);
        break;
      case 3: 
        b10 = com.google.android.gms.common.internal.safeparcel.zza.zze(paramParcel, m);
        break;
      case 4: 
        i = com.google.android.gms.common.internal.safeparcel.zza.zzg(paramParcel, m);
        break;
      case 5: 
        localCameraPosition = (CameraPosition)com.google.android.gms.common.internal.safeparcel.zza.zza(paramParcel, m, CameraPosition.CREATOR);
        break;
      case 6: 
        b9 = com.google.android.gms.common.internal.safeparcel.zza.zze(paramParcel, m);
        break;
      case 7: 
        b8 = com.google.android.gms.common.internal.safeparcel.zza.zze(paramParcel, m);
        break;
      case 8: 
        b7 = com.google.android.gms.common.internal.safeparcel.zza.zze(paramParcel, m);
        break;
      case 9: 
        b6 = com.google.android.gms.common.internal.safeparcel.zza.zze(paramParcel, m);
        break;
      case 10: 
        b5 = com.google.android.gms.common.internal.safeparcel.zza.zze(paramParcel, m);
        break;
      case 11: 
        b4 = com.google.android.gms.common.internal.safeparcel.zza.zze(paramParcel, m);
        break;
      case 12: 
        b3 = com.google.android.gms.common.internal.safeparcel.zza.zze(paramParcel, m);
        break;
      case 14: 
        b2 = com.google.android.gms.common.internal.safeparcel.zza.zze(paramParcel, m);
        break;
      case 15: 
        b1 = com.google.android.gms.common.internal.safeparcel.zza.zze(paramParcel, m);
        break;
      case 16: 
        localFloat2 = com.google.android.gms.common.internal.safeparcel.zza.zzm(paramParcel, m);
        break;
      case 17: 
        localFloat1 = com.google.android.gms.common.internal.safeparcel.zza.zzm(paramParcel, m);
        break;
      case 18: 
        localLatLngBounds = (LatLngBounds)com.google.android.gms.common.internal.safeparcel.zza.zza(paramParcel, m, LatLngBounds.CREATOR);
      }
    }
    if (paramParcel.dataPosition() != k) {
      throw new zza.zza(37 + "Overread allowed size end=" + k, paramParcel);
    }
    return new GoogleMapOptions(j, b11, b10, i, localCameraPosition, b9, b8, b7, b6, b5, b4, b3, b2, b1, localFloat2, localFloat1, localLatLngBounds);
  }
  
  public GoogleMapOptions[] zzvq(int paramInt)
  {
    return new GoogleMapOptions[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/zza.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */