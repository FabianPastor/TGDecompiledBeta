package com.google.android.gms.maps.model;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.util.zzo;
import java.io.IOException;

public final class MapStyleOptions
  extends AbstractSafeParcelable
{
  public static final zzf CREATOR = new zzf();
  private static final String TAG = MapStyleOptions.class.getSimpleName();
  private String amU;
  private final int mVersionCode;
  
  MapStyleOptions(int paramInt, String paramString)
  {
    this.mVersionCode = paramInt;
    this.amU = paramString;
  }
  
  public MapStyleOptions(String paramString)
  {
    this.mVersionCode = 1;
    this.amU = paramString;
  }
  
  public static MapStyleOptions loadRawResourceStyle(Context paramContext, int paramInt)
    throws Resources.NotFoundException
  {
    paramContext = paramContext.getResources().openRawResource(paramInt);
    try
    {
      paramContext = new MapStyleOptions(new String(zzo.zzl(paramContext), "UTF-8"));
      return paramContext;
    }
    catch (IOException paramContext)
    {
      paramContext = String.valueOf(paramContext);
      throw new Resources.NotFoundException(String.valueOf(paramContext).length() + 37 + "Failed to read resource " + paramInt + ": " + paramContext);
    }
  }
  
  int getVersionCode()
  {
    return this.mVersionCode;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzf.zza(this, paramParcel, paramInt);
  }
  
  public String zzbsi()
  {
    return this.amU;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/MapStyleOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */