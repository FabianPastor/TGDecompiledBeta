package com.google.android.gms.location.places;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbe;
import com.google.android.gms.common.internal.zzbg;
import com.google.android.gms.common.internal.zzbo;
import java.util.Arrays;

public class PlaceReport
  extends zza
  implements ReflectedParcelable
{
  public static final Parcelable.Creator<PlaceReport> CREATOR = new zzl();
  private final String mTag;
  private final String zzaeK;
  private int zzaku;
  private final String zzbjI;
  
  PlaceReport(int paramInt, String paramString1, String paramString2, String paramString3)
  {
    this.zzaku = paramInt;
    this.zzbjI = paramString1;
    this.mTag = paramString2;
    this.zzaeK = paramString3;
  }
  
  public static PlaceReport create(String paramString1, String paramString2)
  {
    boolean bool = false;
    zzbo.zzu(paramString1);
    zzbo.zzcF(paramString2);
    zzbo.zzcF("unknown");
    int i = -1;
    switch ("unknown".hashCode())
    {
    default: 
      switch (i)
      {
      }
      break;
    }
    for (;;)
    {
      zzbo.zzb(bool, "Invalid source");
      return new PlaceReport(1, paramString1, paramString2, "unknown");
      if (!"unknown".equals("unknown")) {
        break;
      }
      i = 0;
      break;
      if (!"unknown".equals("userReported")) {
        break;
      }
      i = 1;
      break;
      if (!"unknown".equals("inferredGeofencing")) {
        break;
      }
      i = 2;
      break;
      if (!"unknown".equals("inferredRadioSignals")) {
        break;
      }
      i = 3;
      break;
      if (!"unknown".equals("inferredReverseGeocoding")) {
        break;
      }
      i = 4;
      break;
      if (!"unknown".equals("inferredSnappedToRoad")) {
        break;
      }
      i = 5;
      break;
      bool = true;
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof PlaceReport)) {}
    do
    {
      return false;
      paramObject = (PlaceReport)paramObject;
    } while ((!zzbe.equal(this.zzbjI, ((PlaceReport)paramObject).zzbjI)) || (!zzbe.equal(this.mTag, ((PlaceReport)paramObject).mTag)) || (!zzbe.equal(this.zzaeK, ((PlaceReport)paramObject).zzaeK)));
    return true;
  }
  
  public String getPlaceId()
  {
    return this.zzbjI;
  }
  
  public String getTag()
  {
    return this.mTag;
  }
  
  public int hashCode()
  {
    return Arrays.hashCode(new Object[] { this.zzbjI, this.mTag, this.zzaeK });
  }
  
  public String toString()
  {
    zzbg localzzbg = zzbe.zzt(this);
    localzzbg.zzg("placeId", this.zzbjI);
    localzzbg.zzg("tag", this.mTag);
    if (!"unknown".equals(this.zzaeK)) {
      localzzbg.zzg("source", this.zzaeK);
    }
    return localzzbg.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = zzd.zze(paramParcel);
    zzd.zzc(paramParcel, 1, this.zzaku);
    zzd.zza(paramParcel, 2, getPlaceId(), false);
    zzd.zza(paramParcel, 3, getTag(), false);
    zzd.zza(paramParcel, 4, this.zzaeK, false);
    zzd.zzI(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/location/places/PlaceReport.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */