package com.google.android.gms.vision.face;

import android.graphics.PointF;

public final class Landmark
{
  private final PointF zzbd;
  private final int zzbu;
  
  public Landmark(PointF paramPointF, int paramInt)
  {
    this.zzbd = paramPointF;
    this.zzbu = paramInt;
  }
  
  public final PointF getPosition()
  {
    return this.zzbd;
  }
  
  public final int getType()
  {
    return this.zzbu;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/face/Landmark.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */