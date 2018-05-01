package org.telegram.messenger.exoplayer.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public final class SlidingPercentile
{
  private static final Comparator<Sample> INDEX_COMPARATOR = new Comparator()
  {
    public int compare(SlidingPercentile.Sample paramAnonymousSample1, SlidingPercentile.Sample paramAnonymousSample2)
    {
      return paramAnonymousSample1.index - paramAnonymousSample2.index;
    }
  };
  private static final int MAX_RECYCLED_SAMPLES = 5;
  private static final int SORT_ORDER_BY_INDEX = 1;
  private static final int SORT_ORDER_BY_VALUE = 0;
  private static final int SORT_ORDER_NONE = -1;
  private static final Comparator<Sample> VALUE_COMPARATOR = new Comparator()
  {
    public int compare(SlidingPercentile.Sample paramAnonymousSample1, SlidingPercentile.Sample paramAnonymousSample2)
    {
      if (paramAnonymousSample1.value < paramAnonymousSample2.value) {
        return -1;
      }
      if (paramAnonymousSample2.value < paramAnonymousSample1.value) {
        return 1;
      }
      return 0;
    }
  };
  private int currentSortOrder;
  private final int maxWeight;
  private int nextSampleIndex;
  private int recycledSampleCount;
  private final Sample[] recycledSamples;
  private final ArrayList<Sample> samples;
  private int totalWeight;
  
  public SlidingPercentile(int paramInt)
  {
    this.maxWeight = paramInt;
    this.recycledSamples = new Sample[5];
    this.samples = new ArrayList();
    this.currentSortOrder = -1;
  }
  
  private void ensureSortedByIndex()
  {
    if (this.currentSortOrder != 1)
    {
      Collections.sort(this.samples, INDEX_COMPARATOR);
      this.currentSortOrder = 1;
    }
  }
  
  private void ensureSortedByValue()
  {
    if (this.currentSortOrder != 0)
    {
      Collections.sort(this.samples, VALUE_COMPARATOR);
      this.currentSortOrder = 0;
    }
  }
  
  public void addSample(int paramInt, float paramFloat)
  {
    ensureSortedByIndex();
    Object localObject;
    if (this.recycledSampleCount > 0)
    {
      localObject = this.recycledSamples;
      int i = this.recycledSampleCount - 1;
      this.recycledSampleCount = i;
      localObject = localObject[i];
      i = this.nextSampleIndex;
      this.nextSampleIndex = (i + 1);
      ((Sample)localObject).index = i;
      ((Sample)localObject).weight = paramInt;
      ((Sample)localObject).value = paramFloat;
      this.samples.add(localObject);
      this.totalWeight += paramInt;
    }
    for (;;)
    {
      if (this.totalWeight <= this.maxWeight) {
        return;
      }
      paramInt = this.totalWeight - this.maxWeight;
      localObject = (Sample)this.samples.get(0);
      if (((Sample)localObject).weight <= paramInt)
      {
        this.totalWeight -= ((Sample)localObject).weight;
        this.samples.remove(0);
        if (this.recycledSampleCount >= 5) {
          continue;
        }
        Sample[] arrayOfSample = this.recycledSamples;
        paramInt = this.recycledSampleCount;
        this.recycledSampleCount = (paramInt + 1);
        arrayOfSample[paramInt] = localObject;
        continue;
        localObject = new Sample(null);
        break;
      }
      ((Sample)localObject).weight -= paramInt;
      this.totalWeight -= paramInt;
    }
  }
  
  public float getPercentile(float paramFloat)
  {
    ensureSortedByValue();
    float f = this.totalWeight;
    int j = 0;
    int i = 0;
    while (i < this.samples.size())
    {
      Sample localSample = (Sample)this.samples.get(i);
      j += localSample.weight;
      if (j >= paramFloat * f) {
        return localSample.value;
      }
      i += 1;
    }
    if (this.samples.isEmpty()) {
      return NaN.0F;
    }
    return ((Sample)this.samples.get(this.samples.size() - 1)).value;
  }
  
  private static class Sample
  {
    public int index;
    public float value;
    public int weight;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/util/SlidingPercentile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */