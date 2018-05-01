package com.google.android.gms.vision;

import android.util.Log;
import android.util.SparseArray;

public abstract class FocusingProcessor<T>
  implements Detector.Processor<T>
{
  private Detector<T> zzbMB;
  private Tracker<T> zzbMQ;
  private int zzbMR = 3;
  private boolean zzbMS = false;
  private int zzbMT;
  private int zzbMU = 0;
  
  public FocusingProcessor(Detector<T> paramDetector, Tracker<T> paramTracker)
  {
    this.zzbMB = paramDetector;
    this.zzbMQ = paramTracker;
  }
  
  public void receiveDetections(Detector.Detections<T> paramDetections)
  {
    Object localObject1 = paramDetections.getDetectedItems();
    if (((SparseArray)localObject1).size() == 0)
    {
      if (this.zzbMU == this.zzbMR)
      {
        this.zzbMQ.onDone();
        this.zzbMS = false;
      }
      for (;;)
      {
        this.zzbMU += 1;
        return;
        this.zzbMQ.onMissing(paramDetections);
      }
    }
    this.zzbMU = 0;
    if (this.zzbMS)
    {
      Object localObject2 = ((SparseArray)localObject1).get(this.zzbMT);
      if (localObject2 != null)
      {
        this.zzbMQ.onUpdate(paramDetections, localObject2);
        return;
      }
      this.zzbMQ.onDone();
      this.zzbMS = false;
    }
    int i = selectFocus(paramDetections);
    localObject1 = ((SparseArray)localObject1).get(i);
    if (localObject1 == null)
    {
      Log.w("FocusingProcessor", 35 + "Invalid focus selected: " + i);
      return;
    }
    this.zzbMS = true;
    this.zzbMT = i;
    this.zzbMB.setFocus(this.zzbMT);
    this.zzbMQ.onNewItem(this.zzbMT, localObject1);
    this.zzbMQ.onUpdate(paramDetections, localObject1);
  }
  
  public void release()
  {
    this.zzbMQ.onDone();
  }
  
  public abstract int selectFocus(Detector.Detections<T> paramDetections);
  
  protected final void zzbK(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException(28 + "Invalid max gap: " + paramInt);
    }
    this.zzbMR = paramInt;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/FocusingProcessor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */