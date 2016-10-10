package com.google.android.gms.vision;

import android.util.Log;
import android.util.SparseArray;

public abstract class FocusingProcessor<T>
  implements Detector.Processor<T>
{
  private Detector<T> aKf;
  private Tracker<T> aKv;
  private int aKw = 3;
  private boolean aKx = false;
  private int aKy;
  private int aKz = 0;
  
  public FocusingProcessor(Detector<T> paramDetector, Tracker<T> paramTracker)
  {
    this.aKf = paramDetector;
    this.aKv = paramTracker;
  }
  
  public void receiveDetections(Detector.Detections<T> paramDetections)
  {
    Object localObject1 = paramDetections.getDetectedItems();
    if (((SparseArray)localObject1).size() == 0)
    {
      if (this.aKz == this.aKw)
      {
        this.aKv.onDone();
        this.aKx = false;
      }
      for (;;)
      {
        this.aKz += 1;
        return;
        this.aKv.onMissing(paramDetections);
      }
    }
    this.aKz = 0;
    if (this.aKx)
    {
      Object localObject2 = ((SparseArray)localObject1).get(this.aKy);
      if (localObject2 != null)
      {
        this.aKv.onUpdate(paramDetections, localObject2);
        return;
      }
      this.aKv.onDone();
      this.aKx = false;
    }
    int i = selectFocus(paramDetections);
    localObject1 = ((SparseArray)localObject1).get(i);
    if (localObject1 == null)
    {
      Log.w("FocusingProcessor", 35 + "Invalid focus selected: " + i);
      return;
    }
    this.aKx = true;
    this.aKy = i;
    this.aKf.setFocus(this.aKy);
    this.aKv.onNewItem(this.aKy, localObject1);
    this.aKv.onUpdate(paramDetections, localObject1);
  }
  
  public void release()
  {
    this.aKv.onDone();
  }
  
  public abstract int selectFocus(Detector.Detections<T> paramDetections);
  
  protected void zzaba(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException(28 + "Invalid max gap: " + paramInt);
    }
    this.aKw = paramInt;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/FocusingProcessor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */