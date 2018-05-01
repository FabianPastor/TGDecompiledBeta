package org.telegram.messenger.exoplayer2.source;

public class CompositeSequenceableLoader
  implements SequenceableLoader
{
  protected final SequenceableLoader[] loaders;
  
  public CompositeSequenceableLoader(SequenceableLoader[] paramArrayOfSequenceableLoader)
  {
    this.loaders = paramArrayOfSequenceableLoader;
  }
  
  public boolean continueLoading(long paramLong)
  {
    boolean bool1 = false;
    boolean bool2 = false;
    long l1 = getNextLoadPositionUs();
    if (l1 == Long.MIN_VALUE) {}
    for (;;)
    {
      return bool1;
      SequenceableLoader[] arrayOfSequenceableLoader = this.loaders;
      int i = arrayOfSequenceableLoader.length;
      int j = 0;
      if (j < i)
      {
        SequenceableLoader localSequenceableLoader = arrayOfSequenceableLoader[j];
        long l2 = localSequenceableLoader.getNextLoadPositionUs();
        if ((l2 != Long.MIN_VALUE) && (l2 <= paramLong)) {}
        for (int k = 1;; k = 0)
        {
          boolean bool3;
          if (l2 != l1)
          {
            bool3 = bool2;
            if (k == 0) {}
          }
          else
          {
            bool3 = bool2 | localSequenceableLoader.continueLoading(paramLong);
          }
          j++;
          bool2 = bool3;
          break;
        }
      }
      boolean bool4 = bool1 | bool2;
      bool1 = bool4;
      if (bool2) {
        break;
      }
      bool1 = bool4;
    }
  }
  
  public final long getBufferedPositionUs()
  {
    long l1 = Long.MAX_VALUE;
    SequenceableLoader[] arrayOfSequenceableLoader = this.loaders;
    int i = arrayOfSequenceableLoader.length;
    int j = 0;
    while (j < i)
    {
      long l2 = arrayOfSequenceableLoader[j].getBufferedPositionUs();
      l3 = l1;
      if (l2 != Long.MIN_VALUE) {
        l3 = Math.min(l1, l2);
      }
      j++;
      l1 = l3;
    }
    long l3 = l1;
    if (l1 == Long.MAX_VALUE) {
      l3 = Long.MIN_VALUE;
    }
    return l3;
  }
  
  public final long getNextLoadPositionUs()
  {
    long l1 = Long.MAX_VALUE;
    SequenceableLoader[] arrayOfSequenceableLoader = this.loaders;
    int i = arrayOfSequenceableLoader.length;
    int j = 0;
    while (j < i)
    {
      long l2 = arrayOfSequenceableLoader[j].getNextLoadPositionUs();
      l3 = l1;
      if (l2 != Long.MIN_VALUE) {
        l3 = Math.min(l1, l2);
      }
      j++;
      l1 = l3;
    }
    long l3 = l1;
    if (l1 == Long.MAX_VALUE) {
      l3 = Long.MIN_VALUE;
    }
    return l3;
  }
  
  public final void reevaluateBuffer(long paramLong)
  {
    SequenceableLoader[] arrayOfSequenceableLoader = this.loaders;
    int i = arrayOfSequenceableLoader.length;
    for (int j = 0; j < i; j++) {
      arrayOfSequenceableLoader[j].reevaluateBuffer(paramLong);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/CompositeSequenceableLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */