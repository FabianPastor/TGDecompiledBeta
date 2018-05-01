package com.google.android.gms.maps.model;

public final class Gap
  extends PatternItem
{
  public final float length;
  
  public Gap(float paramFloat)
  {
    super(2, Float.valueOf(Math.max(paramFloat, 0.0F)));
    this.length = Math.max(paramFloat, 0.0F);
  }
  
  public final String toString()
  {
    float f = this.length;
    return 29 + "[Gap: length=" + f + "]";
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/Gap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */