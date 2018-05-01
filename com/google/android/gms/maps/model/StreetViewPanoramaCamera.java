package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbe;
import com.google.android.gms.common.internal.zzbg;
import com.google.android.gms.common.internal.zzbo;
import java.util.Arrays;

public class StreetViewPanoramaCamera
  extends zza
  implements ReflectedParcelable
{
  public static final Parcelable.Creator<StreetViewPanoramaCamera> CREATOR = new zzm();
  public final float bearing;
  public final float tilt;
  public final float zoom;
  private StreetViewPanoramaOrientation zzbnW;
  
  public StreetViewPanoramaCamera(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    boolean bool;
    if ((-90.0F <= paramFloat2) && (paramFloat2 <= 90.0F))
    {
      bool = true;
      zzbo.zzb(bool, 62 + "Tilt needs to be between -90 and 90 inclusive: " + paramFloat2);
      float f = paramFloat1;
      if (paramFloat1 <= 0.0D) {
        f = 0.0F;
      }
      this.zoom = f;
      this.tilt = (paramFloat2 + 0.0F);
      if (paramFloat3 > 0.0D) {
        break label125;
      }
    }
    label125:
    for (paramFloat1 = paramFloat3 % 360.0F + 360.0F;; paramFloat1 = paramFloat3)
    {
      this.bearing = (paramFloat1 % 360.0F);
      this.zzbnW = new StreetViewPanoramaOrientation.Builder().tilt(paramFloat2).bearing(paramFloat3).build();
      return;
      bool = false;
      break;
    }
  }
  
  public static Builder builder()
  {
    return new Builder();
  }
  
  public static Builder builder(StreetViewPanoramaCamera paramStreetViewPanoramaCamera)
  {
    return new Builder(paramStreetViewPanoramaCamera);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {}
    do
    {
      return true;
      if (!(paramObject instanceof StreetViewPanoramaCamera)) {
        return false;
      }
      paramObject = (StreetViewPanoramaCamera)paramObject;
    } while ((Float.floatToIntBits(this.zoom) == Float.floatToIntBits(((StreetViewPanoramaCamera)paramObject).zoom)) && (Float.floatToIntBits(this.tilt) == Float.floatToIntBits(((StreetViewPanoramaCamera)paramObject).tilt)) && (Float.floatToIntBits(this.bearing) == Float.floatToIntBits(((StreetViewPanoramaCamera)paramObject).bearing)));
    return false;
  }
  
  public StreetViewPanoramaOrientation getOrientation()
  {
    return this.zzbnW;
  }
  
  public int hashCode()
  {
    return Arrays.hashCode(new Object[] { Float.valueOf(this.zoom), Float.valueOf(this.tilt), Float.valueOf(this.bearing) });
  }
  
  public String toString()
  {
    return zzbe.zzt(this).zzg("zoom", Float.valueOf(this.zoom)).zzg("tilt", Float.valueOf(this.tilt)).zzg("bearing", Float.valueOf(this.bearing)).toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = zzd.zze(paramParcel);
    zzd.zza(paramParcel, 2, this.zoom);
    zzd.zza(paramParcel, 3, this.tilt);
    zzd.zza(paramParcel, 4, this.bearing);
    zzd.zzI(paramParcel, paramInt);
  }
  
  public static final class Builder
  {
    public float bearing;
    public float tilt;
    public float zoom;
    
    public Builder() {}
    
    public Builder(StreetViewPanoramaCamera paramStreetViewPanoramaCamera)
    {
      this.zoom = paramStreetViewPanoramaCamera.zoom;
      this.bearing = paramStreetViewPanoramaCamera.bearing;
      this.tilt = paramStreetViewPanoramaCamera.tilt;
    }
    
    public final Builder bearing(float paramFloat)
    {
      this.bearing = paramFloat;
      return this;
    }
    
    public final StreetViewPanoramaCamera build()
    {
      return new StreetViewPanoramaCamera(this.zoom, this.tilt, this.bearing);
    }
    
    public final Builder orientation(StreetViewPanoramaOrientation paramStreetViewPanoramaOrientation)
    {
      this.tilt = paramStreetViewPanoramaOrientation.tilt;
      this.bearing = paramStreetViewPanoramaOrientation.bearing;
      return this;
    }
    
    public final Builder tilt(float paramFloat)
    {
      this.tilt = paramFloat;
      return this;
    }
    
    public final Builder zoom(float paramFloat)
    {
      this.zoom = paramFloat;
      return this;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/StreetViewPanoramaCamera.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */