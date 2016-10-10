package com.google.android.gms.vision.face;

import android.graphics.PointF;

public final class Landmark
{
  public static final int BOTTOM_MOUTH = 0;
  public static final int LEFT_CHEEK = 1;
  public static final int LEFT_EAR = 3;
  public static final int LEFT_EAR_TIP = 2;
  public static final int LEFT_EYE = 4;
  public static final int LEFT_MOUTH = 5;
  public static final int NOSE_BASE = 6;
  public static final int RIGHT_CHEEK = 7;
  public static final int RIGHT_EAR = 9;
  public static final int RIGHT_EAR_TIP = 8;
  public static final int RIGHT_EYE = 10;
  public static final int RIGHT_MOUTH = 11;
  private final PointF aKP;
  private final int lN;
  
  public Landmark(PointF paramPointF, int paramInt)
  {
    this.aKP = paramPointF;
    this.lN = paramInt;
  }
  
  public PointF getPosition()
  {
    return this.aKP;
  }
  
  public int getType()
  {
    return this.lN;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/face/Landmark.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */