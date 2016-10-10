package com.google.android.gms.vision;

public class Tracker<T>
{
  public void onDone() {}
  
  public void onMissing(Detector.Detections<T> paramDetections) {}
  
  public void onNewItem(int paramInt, T paramT) {}
  
  public void onUpdate(Detector.Detections<T> paramDetections, T paramT) {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/Tracker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */