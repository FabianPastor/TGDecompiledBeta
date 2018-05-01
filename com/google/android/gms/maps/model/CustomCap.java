package com.google.android.gms.maps.model;

import android.support.annotation.NonNull;

public final class CustomCap
  extends Cap
{
  public final BitmapDescriptor bitmapDescriptor;
  public final float refWidth;
  
  public CustomCap(@NonNull BitmapDescriptor paramBitmapDescriptor)
  {
    this(paramBitmapDescriptor, 10.0F);
  }
  
  public CustomCap(@NonNull BitmapDescriptor paramBitmapDescriptor, float paramFloat)
  {
    super(localBitmapDescriptor, paramFloat);
    this.bitmapDescriptor = paramBitmapDescriptor;
    this.refWidth = paramFloat;
  }
  
  public final String toString()
  {
    String str = String.valueOf(this.bitmapDescriptor);
    float f = this.refWidth;
    return String.valueOf(str).length() + 55 + "[CustomCap: bitmapDescriptor=" + str + " refWidth=" + f + "]";
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/CustomCap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */