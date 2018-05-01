package org.telegram.messenger.exoplayer2.source;

import java.util.Arrays;
import java.util.Random;

public abstract interface ShuffleOrder
{
  public abstract ShuffleOrder cloneAndInsert(int paramInt1, int paramInt2);
  
  public abstract ShuffleOrder cloneAndRemove(int paramInt);
  
  public abstract int getFirstIndex();
  
  public abstract int getLastIndex();
  
  public abstract int getLength();
  
  public abstract int getNextIndex(int paramInt);
  
  public abstract int getPreviousIndex(int paramInt);
  
  public static class DefaultShuffleOrder
    implements ShuffleOrder
  {
    private final int[] indexInShuffled;
    private final Random random;
    private final int[] shuffled;
    
    public DefaultShuffleOrder(int paramInt)
    {
      this(paramInt, new Random());
    }
    
    public DefaultShuffleOrder(int paramInt, long paramLong)
    {
      this(paramInt, new Random(paramLong));
    }
    
    private DefaultShuffleOrder(int paramInt, Random paramRandom)
    {
      this(createShuffledList(paramInt, paramRandom), paramRandom);
    }
    
    private DefaultShuffleOrder(int[] paramArrayOfInt, Random paramRandom)
    {
      this.shuffled = paramArrayOfInt;
      this.random = paramRandom;
      this.indexInShuffled = new int[paramArrayOfInt.length];
      for (int i = 0; i < paramArrayOfInt.length; i++) {
        this.indexInShuffled[paramArrayOfInt[i]] = i;
      }
    }
    
    private static int[] createShuffledList(int paramInt, Random paramRandom)
    {
      int[] arrayOfInt = new int[paramInt];
      for (int i = 0; i < paramInt; i++)
      {
        int j = paramRandom.nextInt(i + 1);
        arrayOfInt[i] = arrayOfInt[j];
        arrayOfInt[j] = i;
      }
      return arrayOfInt;
    }
    
    public ShuffleOrder cloneAndInsert(int paramInt1, int paramInt2)
    {
      int[] arrayOfInt1 = new int[paramInt2];
      int[] arrayOfInt2 = new int[paramInt2];
      for (int i = 0; i < paramInt2; i++)
      {
        arrayOfInt1[i] = this.random.nextInt(this.shuffled.length + 1);
        j = this.random.nextInt(i + 1);
        arrayOfInt2[i] = arrayOfInt2[j];
        arrayOfInt2[j] = (i + paramInt1);
      }
      Arrays.sort(arrayOfInt1);
      int[] arrayOfInt3 = new int[this.shuffled.length + paramInt2];
      int k = 0;
      int j = 0;
      i = 0;
      if (i < this.shuffled.length + paramInt2)
      {
        if ((j < paramInt2) && (k == arrayOfInt1[j]))
        {
          arrayOfInt3[i] = arrayOfInt2[j];
          j++;
        }
        for (;;)
        {
          i++;
          break;
          arrayOfInt3[i] = this.shuffled[k];
          if (arrayOfInt3[i] >= paramInt1) {
            arrayOfInt3[i] += paramInt2;
          }
          k++;
        }
      }
      return new DefaultShuffleOrder(arrayOfInt3, new Random(this.random.nextLong()));
    }
    
    public ShuffleOrder cloneAndRemove(int paramInt)
    {
      int[] arrayOfInt = new int[this.shuffled.length - 1];
      int i = 0;
      int j = 0;
      while (j < this.shuffled.length) {
        if (this.shuffled[j] == paramInt)
        {
          i = 1;
          j++;
        }
        else
        {
          int k;
          if (i != 0)
          {
            k = j - 1;
            label54:
            if (this.shuffled[j] <= paramInt) {
              break label92;
            }
          }
          label92:
          for (int m = this.shuffled[j] - 1;; m = this.shuffled[j])
          {
            arrayOfInt[k] = m;
            break;
            k = j;
            break label54;
          }
        }
      }
      return new DefaultShuffleOrder(arrayOfInt, new Random(this.random.nextLong()));
    }
    
    public int getFirstIndex()
    {
      if (this.shuffled.length > 0) {}
      for (int i = this.shuffled[0];; i = -1) {
        return i;
      }
    }
    
    public int getLastIndex()
    {
      if (this.shuffled.length > 0) {}
      for (int i = this.shuffled[(this.shuffled.length - 1)];; i = -1) {
        return i;
      }
    }
    
    public int getLength()
    {
      return this.shuffled.length;
    }
    
    public int getNextIndex(int paramInt)
    {
      paramInt = this.indexInShuffled[paramInt] + 1;
      if (paramInt < this.shuffled.length) {}
      for (paramInt = this.shuffled[paramInt];; paramInt = -1) {
        return paramInt;
      }
    }
    
    public int getPreviousIndex(int paramInt)
    {
      paramInt = this.indexInShuffled[paramInt] - 1;
      if (paramInt >= 0) {}
      for (paramInt = this.shuffled[paramInt];; paramInt = -1) {
        return paramInt;
      }
    }
  }
  
  public static final class UnshuffledShuffleOrder
    implements ShuffleOrder
  {
    private final int length;
    
    public UnshuffledShuffleOrder(int paramInt)
    {
      this.length = paramInt;
    }
    
    public ShuffleOrder cloneAndInsert(int paramInt1, int paramInt2)
    {
      return new UnshuffledShuffleOrder(this.length + paramInt2);
    }
    
    public ShuffleOrder cloneAndRemove(int paramInt)
    {
      return new UnshuffledShuffleOrder(this.length - 1);
    }
    
    public int getFirstIndex()
    {
      if (this.length > 0) {}
      for (int i = 0;; i = -1) {
        return i;
      }
    }
    
    public int getLastIndex()
    {
      if (this.length > 0) {}
      for (int i = this.length - 1;; i = -1) {
        return i;
      }
    }
    
    public int getLength()
    {
      return this.length;
    }
    
    public int getNextIndex(int paramInt)
    {
      
      if (paramInt < this.length) {}
      for (;;)
      {
        return paramInt;
        paramInt = -1;
      }
    }
    
    public int getPreviousIndex(int paramInt)
    {
      
      if (paramInt >= 0) {}
      for (;;)
      {
        return paramInt;
        paramInt = -1;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/ShuffleOrder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */