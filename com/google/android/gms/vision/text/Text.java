package com.google.android.gms.vision.text;

import android.graphics.Point;
import android.graphics.Rect;
import java.util.List;

public abstract interface Text
{
  public abstract Rect getBoundingBox();
  
  public abstract List<? extends Text> getComponents();
  
  public abstract Point[] getCornerPoints();
  
  public abstract String getValue();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/text/Text.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */