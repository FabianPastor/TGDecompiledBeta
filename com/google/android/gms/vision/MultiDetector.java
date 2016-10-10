package com.google.android.gms.vision;

import android.util.SparseArray;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MultiDetector
  extends Detector<Object>
{
  private List<Detector<? extends Object>> aKG = new ArrayList();
  
  public SparseArray<Object> detect(Frame paramFrame)
  {
    SparseArray localSparseArray1 = new SparseArray();
    Iterator localIterator = this.aKG.iterator();
    while (localIterator.hasNext())
    {
      SparseArray localSparseArray2 = ((Detector)localIterator.next()).detect(paramFrame);
      int i = 0;
      while (i < localSparseArray2.size())
      {
        int j = localSparseArray2.keyAt(i);
        if (localSparseArray1.get(j) != null) {
          throw new IllegalStateException(105 + "Detection ID overlap for id = " + j + ".  This means that one of the detectors is not using global IDs.");
        }
        localSparseArray1.append(j, localSparseArray2.valueAt(i));
        i += 1;
      }
    }
    return localSparseArray1;
  }
  
  public boolean isOperational()
  {
    Iterator localIterator = this.aKG.iterator();
    while (localIterator.hasNext()) {
      if (!((Detector)localIterator.next()).isOperational()) {
        return false;
      }
    }
    return true;
  }
  
  public void receiveFrame(Frame paramFrame)
  {
    Iterator localIterator = this.aKG.iterator();
    while (localIterator.hasNext()) {
      ((Detector)localIterator.next()).receiveFrame(paramFrame);
    }
  }
  
  public void release()
  {
    Iterator localIterator = this.aKG.iterator();
    while (localIterator.hasNext()) {
      ((Detector)localIterator.next()).release();
    }
    this.aKG.clear();
  }
  
  public void setProcessor(Detector.Processor<Object> paramProcessor)
  {
    throw new UnsupportedOperationException("MultiDetector.setProcessor is not supported.  You should set a processor instance on each underlying detector instead.");
  }
  
  public static class Builder
  {
    private MultiDetector aKH = new MultiDetector(null);
    
    public Builder add(Detector<? extends Object> paramDetector)
    {
      MultiDetector.zza(this.aKH).add(paramDetector);
      return this;
    }
    
    public MultiDetector build()
    {
      if (MultiDetector.zza(this.aKH).size() == 0) {
        throw new RuntimeException("No underlying detectors added to MultiDetector.");
      }
      return this.aKH;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/MultiDetector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */