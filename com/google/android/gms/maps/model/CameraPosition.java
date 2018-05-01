package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.common.internal.Objects.ToStringHelper;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

public final class CameraPosition
  extends AbstractSafeParcelable
  implements ReflectedParcelable
{
  public static final Parcelable.Creator<CameraPosition> CREATOR = new zza();
  public final float bearing;
  public final LatLng target;
  public final float tilt;
  public final float zoom;
  
  public CameraPosition(LatLng paramLatLng, float paramFloat1, float paramFloat2, float paramFloat3)
  {
    Preconditions.checkNotNull(paramLatLng, "null camera target");
    if ((0.0F <= paramFloat2) && (paramFloat2 <= 90.0F)) {}
    for (boolean bool = true;; bool = false)
    {
      Preconditions.checkArgument(bool, "Tilt needs to be between 0 and 90 inclusive: %s", new Object[] { Float.valueOf(paramFloat2) });
      this.target = paramLatLng;
      this.zoom = paramFloat1;
      this.tilt = (paramFloat2 + 0.0F);
      paramFloat1 = paramFloat3;
      if (paramFloat3 <= 0.0D) {
        paramFloat1 = paramFloat3 % 360.0F + 360.0F;
      }
      this.bearing = (paramFloat1 % 360.0F);
      return;
    }
  }
  
  public static Builder builder()
  {
    return new Builder();
  }
  
  public final boolean equals(Object paramObject)
  {
    boolean bool = true;
    if (this == paramObject) {}
    for (;;)
    {
      return bool;
      if (!(paramObject instanceof CameraPosition))
      {
        bool = false;
      }
      else
      {
        paramObject = (CameraPosition)paramObject;
        if ((!this.target.equals(((CameraPosition)paramObject).target)) || (Float.floatToIntBits(this.zoom) != Float.floatToIntBits(((CameraPosition)paramObject).zoom)) || (Float.floatToIntBits(this.tilt) != Float.floatToIntBits(((CameraPosition)paramObject).tilt)) || (Float.floatToIntBits(this.bearing) != Float.floatToIntBits(((CameraPosition)paramObject).bearing))) {
          bool = false;
        }
      }
    }
  }
  
  public final int hashCode()
  {
    return Objects.hashCode(new Object[] { this.target, Float.valueOf(this.zoom), Float.valueOf(this.tilt), Float.valueOf(this.bearing) });
  }
  
  public final String toString()
  {
    return Objects.toStringHelper(this).add("target", this.target).add("zoom", Float.valueOf(this.zoom)).add("tilt", Float.valueOf(this.tilt)).add("bearing", Float.valueOf(this.bearing)).toString();
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeParcelable(paramParcel, 2, this.target, paramInt, false);
    SafeParcelWriter.writeFloat(paramParcel, 3, this.zoom);
    SafeParcelWriter.writeFloat(paramParcel, 4, this.tilt);
    SafeParcelWriter.writeFloat(paramParcel, 5, this.bearing);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
  
  public static final class Builder
  {
    private float bearing;
    private LatLng target;
    private float tilt;
    private float zoom;
    
    public final Builder bearing(float paramFloat)
    {
      this.bearing = paramFloat;
      return this;
    }
    
    public final CameraPosition build()
    {
      return new CameraPosition(this.target, this.zoom, this.tilt, this.bearing);
    }
    
    public final Builder target(LatLng paramLatLng)
    {
      this.target = paramLatLng;
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/CameraPosition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */