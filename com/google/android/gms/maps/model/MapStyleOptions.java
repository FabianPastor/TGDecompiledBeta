package com.google.android.gms.maps.model;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.util.zzn;
import java.io.IOException;

public final class MapStyleOptions
  extends zza
{
  public static final Parcelable.Creator<MapStyleOptions> CREATOR = new zzg();
  private static final String TAG = MapStyleOptions.class.getSimpleName();
  private String zzbnC;
  
  public MapStyleOptions(String paramString)
  {
    this.zzbnC = paramString;
  }
  
  public static MapStyleOptions loadRawResourceStyle(Context paramContext, int paramInt)
    throws Resources.NotFoundException
  {
    paramContext = paramContext.getResources().openRawResource(paramInt);
    try
    {
      paramContext = new MapStyleOptions(new String(zzn.zza(paramContext, true), "UTF-8"));
      return paramContext;
    }
    catch (IOException paramContext)
    {
      paramContext = String.valueOf(paramContext);
      throw new Resources.NotFoundException(String.valueOf(paramContext).length() + 37 + "Failed to read resource " + paramInt + ": " + paramContext);
    }
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = zzd.zze(paramParcel);
    zzd.zza(paramParcel, 2, this.zzbnC, false);
    zzd.zzI(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/MapStyleOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */