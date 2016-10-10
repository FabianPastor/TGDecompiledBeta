package com.google.android.gms.location.places;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.common.internal.zzab.zza;
import com.google.android.gms.common.internal.zzac;

public class PlaceReport
  extends AbstractSafeParcelable
  implements ReflectedParcelable
{
  public static final Parcelable.Creator<PlaceReport> CREATOR = new zzi();
  private final String I;
  private final String aiY;
  private final String mTag;
  final int mVersionCode;
  
  PlaceReport(int paramInt, String paramString1, String paramString2, String paramString3)
  {
    this.mVersionCode = paramInt;
    this.aiY = paramString1;
    this.mTag = paramString2;
    this.I = paramString3;
  }
  
  public static PlaceReport create(String paramString1, String paramString2)
  {
    return zzj(paramString1, paramString2, "unknown");
  }
  
  public static PlaceReport zzj(String paramString1, String paramString2, String paramString3)
  {
    zzac.zzy(paramString1);
    zzac.zzhz(paramString2);
    zzac.zzhz(paramString3);
    zzac.zzb(zzla(paramString3), "Invalid source");
    return new PlaceReport(1, paramString1, paramString2, paramString3);
  }
  
  private static boolean zzla(String paramString)
  {
    boolean bool = true;
    int i = -1;
    switch (paramString.hashCode())
    {
    }
    for (;;)
    {
      switch (i)
      {
      default: 
        bool = false;
      }
      return bool;
      if (paramString.equals("unknown"))
      {
        i = 0;
        continue;
        if (paramString.equals("userReported"))
        {
          i = 1;
          continue;
          if (paramString.equals("inferredGeofencing"))
          {
            i = 2;
            continue;
            if (paramString.equals("inferredRadioSignals"))
            {
              i = 3;
              continue;
              if (paramString.equals("inferredReverseGeocoding"))
              {
                i = 4;
                continue;
                if (paramString.equals("inferredSnappedToRoad")) {
                  i = 5;
                }
              }
            }
          }
        }
      }
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof PlaceReport)) {}
    do
    {
      return false;
      paramObject = (PlaceReport)paramObject;
    } while ((!zzab.equal(this.aiY, ((PlaceReport)paramObject).aiY)) || (!zzab.equal(this.mTag, ((PlaceReport)paramObject).mTag)) || (!zzab.equal(this.I, ((PlaceReport)paramObject).I)));
    return true;
  }
  
  public String getPlaceId()
  {
    return this.aiY;
  }
  
  public String getSource()
  {
    return this.I;
  }
  
  public String getTag()
  {
    return this.mTag;
  }
  
  public int hashCode()
  {
    return zzab.hashCode(new Object[] { this.aiY, this.mTag, this.I });
  }
  
  public String toString()
  {
    zzab.zza localzza = zzab.zzx(this);
    localzza.zzg("placeId", this.aiY);
    localzza.zzg("tag", this.mTag);
    if (!"unknown".equals(this.I)) {
      localzza.zzg("source", this.I);
    }
    return localzza.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzi.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/location/places/PlaceReport.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */