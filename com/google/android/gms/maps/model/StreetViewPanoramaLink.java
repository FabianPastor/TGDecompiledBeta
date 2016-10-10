package com.google.android.gms.maps.model;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.common.internal.zzab.zza;

public class StreetViewPanoramaLink
  extends AbstractSafeParcelable
{
  public static final zzl CREATOR = new zzl();
  public final float bearing;
  private final int mVersionCode;
  public final String panoId;
  
  StreetViewPanoramaLink(int paramInt, String paramString, float paramFloat)
  {
    this.mVersionCode = paramInt;
    this.panoId = paramString;
    float f = paramFloat;
    if (paramFloat <= 0.0D) {
      f = paramFloat % 360.0F + 360.0F;
    }
    this.bearing = (f % 360.0F);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {}
    do
    {
      return true;
      if (!(paramObject instanceof StreetViewPanoramaLink)) {
        return false;
      }
      paramObject = (StreetViewPanoramaLink)paramObject;
    } while ((this.panoId.equals(((StreetViewPanoramaLink)paramObject).panoId)) && (Float.floatToIntBits(this.bearing) == Float.floatToIntBits(((StreetViewPanoramaLink)paramObject).bearing)));
    return false;
  }
  
  int getVersionCode()
  {
    return this.mVersionCode;
  }
  
  public int hashCode()
  {
    return zzab.hashCode(new Object[] { this.panoId, Float.valueOf(this.bearing) });
  }
  
  public String toString()
  {
    return zzab.zzx(this).zzg("panoId", this.panoId).zzg("bearing", Float.valueOf(this.bearing)).toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzl.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/StreetViewPanoramaLink.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */