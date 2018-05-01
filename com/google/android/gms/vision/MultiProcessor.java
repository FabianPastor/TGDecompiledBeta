package com.google.android.gms.vision;

import android.util.SparseArray;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MultiProcessor<T>
  implements Detector.Processor<T>
{
  private int zzbMR = 3;
  private Factory<T> zzbNd;
  private SparseArray<zza> zzbNe = new SparseArray();
  
  private final void zza(Detector.Detections<T> paramDetections)
  {
    SparseArray localSparseArray = paramDetections.getDetectedItems();
    int i = 0;
    while (i < localSparseArray.size())
    {
      int j = localSparseArray.keyAt(i);
      Object localObject = localSparseArray.valueAt(i);
      zza localzza = (zza)this.zzbNe.get(j);
      zza.zza(localzza, 0);
      zza.zza(localzza).onUpdate(paramDetections, localObject);
      i += 1;
    }
  }
  
  public void receiveDetections(Detector.Detections<T> paramDetections)
  {
    int j = 0;
    Object localObject1 = paramDetections.getDetectedItems();
    int i = 0;
    zza localzza;
    while (i < ((SparseArray)localObject1).size())
    {
      int k = ((SparseArray)localObject1).keyAt(i);
      localObject2 = ((SparseArray)localObject1).valueAt(i);
      if (this.zzbNe.get(k) == null)
      {
        localzza = new zza(null);
        zza.zza(localzza, this.zzbNd.create(localObject2));
        zza.zza(localzza).onNewItem(k, localObject2);
        this.zzbNe.append(k, localzza);
      }
      i += 1;
    }
    localObject1 = paramDetections.getDetectedItems();
    Object localObject2 = new HashSet();
    i = j;
    if (i < this.zzbNe.size())
    {
      j = this.zzbNe.keyAt(i);
      if (((SparseArray)localObject1).get(j) == null)
      {
        localzza = (zza)this.zzbNe.valueAt(i);
        zza.zzb(localzza);
        if (zza.zzc(localzza) < this.zzbMR) {
          break label209;
        }
        zza.zza(localzza).onDone();
        ((Set)localObject2).add(Integer.valueOf(j));
      }
      for (;;)
      {
        i += 1;
        break;
        label209:
        zza.zza(localzza).onMissing(paramDetections);
      }
    }
    localObject1 = ((Set)localObject2).iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Integer)((Iterator)localObject1).next();
      this.zzbNe.delete(((Integer)localObject2).intValue());
    }
    zza(paramDetections);
  }
  
  public void release()
  {
    int i = 0;
    while (i < this.zzbNe.size())
    {
      zza.zza((zza)this.zzbNe.valueAt(i)).onDone();
      i += 1;
    }
    this.zzbNe.clear();
  }
  
  public static class Builder<T>
  {
    private MultiProcessor<T> zzbNf = new MultiProcessor(null);
    
    public Builder(MultiProcessor.Factory<T> paramFactory)
    {
      if (paramFactory == null) {
        throw new IllegalArgumentException("No factory supplied.");
      }
      MultiProcessor.zza(this.zzbNf, paramFactory);
    }
    
    public MultiProcessor<T> build()
    {
      return this.zzbNf;
    }
    
    public Builder<T> setMaxGapFrames(int paramInt)
    {
      if (paramInt < 0) {
        throw new IllegalArgumentException(28 + "Invalid max gap: " + paramInt);
      }
      MultiProcessor.zza(this.zzbNf, paramInt);
      return this;
    }
  }
  
  public static abstract interface Factory<T>
  {
    public abstract Tracker<T> create(T paramT);
  }
  
  final class zza
  {
    private Tracker<T> zzbMQ;
    private int zzbMU = 0;
    
    private zza() {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/MultiProcessor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */