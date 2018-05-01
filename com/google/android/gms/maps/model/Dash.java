package com.google.android.gms.maps.model;

public final class Dash
  extends PatternItem
{
  public final float length;
  
  public Dash(float paramFloat)
  {
    super(0, Float.valueOf(Math.max(paramFloat, 0.0F)));
    this.length = Math.max(paramFloat, 0.0F);
  }
  
  public final String toString()
  {
    float f = this.length;
    return 30 + "[Dash: length=" + f + "]";
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/Dash.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */