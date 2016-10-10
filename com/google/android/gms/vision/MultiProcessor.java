package com.google.android.gms.vision;

import android.util.SparseArray;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MultiProcessor<T>
  implements Detector.Processor<T>
{
  private Factory<T> aKI;
  private SparseArray<zza> aKJ = new SparseArray();
  private int aKw = 3;
  
  private void zza(Detector.Detections<T> paramDetections)
  {
    paramDetections = paramDetections.getDetectedItems();
    int i = 0;
    while (i < paramDetections.size())
    {
      int j = paramDetections.keyAt(i);
      Object localObject = paramDetections.valueAt(i);
      if (this.aKJ.get(j) == null)
      {
        zza localzza = new zza(null);
        zza.zza(localzza, this.aKI.create(localObject));
        zza.zza(localzza).onNewItem(j, localObject);
        this.aKJ.append(j, localzza);
      }
      i += 1;
    }
  }
  
  private void zzb(Detector.Detections<T> paramDetections)
  {
    Object localObject = paramDetections.getDetectedItems();
    HashSet localHashSet = new HashSet();
    int i = 0;
    if (i < this.aKJ.size())
    {
      int j = this.aKJ.keyAt(i);
      zza localzza;
      if (((SparseArray)localObject).get(j) == null)
      {
        localzza = (zza)this.aKJ.valueAt(i);
        zza.zzb(localzza);
        if (zza.zzc(localzza) < this.aKw) {
          break label104;
        }
        zza.zza(localzza).onDone();
        localHashSet.add(Integer.valueOf(j));
      }
      for (;;)
      {
        i += 1;
        break;
        label104:
        zza.zza(localzza).onMissing(paramDetections);
      }
    }
    paramDetections = localHashSet.iterator();
    while (paramDetections.hasNext())
    {
      localObject = (Integer)paramDetections.next();
      this.aKJ.delete(((Integer)localObject).intValue());
    }
  }
  
  private void zzc(Detector.Detections<T> paramDetections)
  {
    SparseArray localSparseArray = paramDetections.getDetectedItems();
    int i = 0;
    while (i < localSparseArray.size())
    {
      int j = localSparseArray.keyAt(i);
      Object localObject = localSparseArray.valueAt(i);
      zza localzza = (zza)this.aKJ.get(j);
      zza.zza(localzza, 0);
      zza.zza(localzza).onUpdate(paramDetections, localObject);
      i += 1;
    }
  }
  
  public void receiveDetections(Detector.Detections<T> paramDetections)
  {
    zza(paramDetections);
    zzb(paramDetections);
    zzc(paramDetections);
  }
  
  public void release()
  {
    int i = 0;
    while (i < this.aKJ.size())
    {
      zza.zza((zza)this.aKJ.valueAt(i)).onDone();
      i += 1;
    }
    this.aKJ.clear();
  }
  
  public static class Builder<T>
  {
    private MultiProcessor<T> aKK = new MultiProcessor(null);
    
    public Builder(MultiProcessor.Factory<T> paramFactory)
    {
      if (paramFactory == null) {
        throw new IllegalArgumentException("No factory supplied.");
      }
      MultiProcessor.zza(this.aKK, paramFactory);
    }
    
    public MultiProcessor<T> build()
    {
      return this.aKK;
    }
    
    public Builder<T> setMaxGapFrames(int paramInt)
    {
      if (paramInt < 0) {
        throw new IllegalArgumentException(28 + "Invalid max gap: " + paramInt);
      }
      MultiProcessor.zza(this.aKK, paramInt);
      return this;
    }
  }
  
  public static abstract interface Factory<T>
  {
    public abstract Tracker<T> create(T paramT);
  }
  
  private class zza
  {
    private Tracker<T> aKv;
    private int aKz = 0;
    
    private zza() {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/MultiProcessor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */