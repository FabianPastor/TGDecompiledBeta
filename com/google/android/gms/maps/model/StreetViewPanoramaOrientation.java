package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbe;
import com.google.android.gms.common.internal.zzbg;
import com.google.android.gms.common.internal.zzbo;
import java.util.Arrays;

public class StreetViewPanoramaOrientation
  extends zza
{
  public static final Parcelable.Creator<StreetViewPanoramaOrientation> CREATOR = new zzp();
  public final float bearing;
  public final float tilt;
  
  public StreetViewPanoramaOrientation(float paramFloat1, float paramFloat2)
  {
    if ((-90.0F <= paramFloat1) && (paramFloat1 <= 90.0F)) {}
    for (boolean bool = true;; bool = false)
    {
      zzbo.zzb(bool, 62 + "Tilt needs to be between -90 and 90 inclusive: " + paramFloat1);
      this.tilt = (0.0F + paramFloat1);
      paramFloat1 = paramFloat2;
      if (paramFloat2 <= 0.0D) {
        paramFloat1 = paramFloat2 % 360.0F + 360.0F;
      }
      this.bearing = (paramFloat1 % 360.0F);
      return;
    }
  }
  
  public static Builder builder()
  {
    return new Builder();
  }
  
  public static Builder builder(StreetViewPanoramaOrientation paramStreetViewPanoramaOrientation)
  {
    return new Builder(paramStreetViewPanoramaOrientation);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {}
    do
    {
      return true;
      if (!(paramObject instanceof StreetViewPanoramaOrientation)) {
        return false;
      }
      paramObject = (StreetViewPanoramaOrientation)paramObject;
    } while ((Float.floatToIntBits(this.tilt) == Float.floatToIntBits(((StreetViewPanoramaOrientation)paramObject).tilt)) && (Float.floatToIntBits(this.bearing) == Float.floatToIntBits(((StreetViewPanoramaOrientation)paramObject).bearing)));
    return false;
  }
  
  public int hashCode()
  {
    return Arrays.hashCode(new Object[] { Float.valueOf(this.tilt), Float.valueOf(this.bearing) });
  }
  
  public String toString()
  {
    return zzbe.zzt(this).zzg("tilt", Float.valueOf(this.tilt)).zzg("bearing", Float.valueOf(this.bearing)).toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = zzd.zze(paramParcel);
    zzd.zza(paramParcel, 2, this.tilt);
    zzd.zza(paramParcel, 3, this.bearing);
    zzd.zzI(paramParcel, paramInt);
  }
  
  public static final class Builder
  {
    public float bearing;
    public float tilt;
    
    public Builder() {}
    
    public Builder(StreetViewPanoramaOrientation paramStreetViewPanoramaOrientation)
    {
      this.bearing = paramStreetViewPanoramaOrientation.bearing;
      this.tilt = paramStreetViewPanoramaOrientation.tilt;
    }
    
    public final Builder bearing(float paramFloat)
    {
      this.bearing = paramFloat;
      return this;
    }
    
    public final StreetViewPanoramaOrientation build()
    {
      return new StreetViewPanoramaOrientation(this.tilt, this.bearing);
    }
    
    public final Builder tilt(float paramFloat)
    {
      this.tilt = paramFloat;
      return this;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/StreetViewPanoramaOrientation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */