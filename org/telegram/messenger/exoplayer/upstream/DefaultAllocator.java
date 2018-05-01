package org.telegram.messenger.exoplayer.upstream;

import java.util.Arrays;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.Util;

public final class DefaultAllocator
  implements Allocator
{
  private static final int AVAILABLE_EXTRA_CAPACITY = 100;
  private int allocatedCount;
  private Allocation[] availableAllocations;
  private int availableCount;
  private final int individualAllocationSize;
  private final byte[] initialAllocationBlock;
  
  public DefaultAllocator(int paramInt)
  {
    this(paramInt, 0);
  }
  
  public DefaultAllocator(int paramInt1, int paramInt2)
  {
    if (paramInt1 > 0)
    {
      bool1 = true;
      Assertions.checkArgument(bool1);
      if (paramInt2 < 0) {
        break label106;
      }
    }
    label106:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      Assertions.checkArgument(bool1);
      this.individualAllocationSize = paramInt1;
      this.availableCount = paramInt2;
      this.availableAllocations = new Allocation[paramInt2 + 100];
      if (paramInt2 <= 0) {
        break label112;
      }
      this.initialAllocationBlock = new byte[paramInt2 * paramInt1];
      int i = 0;
      while (i < paramInt2)
      {
        this.availableAllocations[i] = new Allocation(this.initialAllocationBlock, i * paramInt1);
        i += 1;
      }
      bool1 = false;
      break;
    }
    label112:
    this.initialAllocationBlock = null;
  }
  
  /* Error */
  public Allocation allocate()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: aload_0
    //   4: getfield 48	org/telegram/messenger/exoplayer/upstream/DefaultAllocator:allocatedCount	I
    //   7: iconst_1
    //   8: iadd
    //   9: putfield 48	org/telegram/messenger/exoplayer/upstream/DefaultAllocator:allocatedCount	I
    //   12: aload_0
    //   13: getfield 35	org/telegram/messenger/exoplayer/upstream/DefaultAllocator:availableCount	I
    //   16: ifle +38 -> 54
    //   19: aload_0
    //   20: getfield 39	org/telegram/messenger/exoplayer/upstream/DefaultAllocator:availableAllocations	[Lorg/telegram/messenger/exoplayer/upstream/Allocation;
    //   23: astore_2
    //   24: aload_0
    //   25: getfield 35	org/telegram/messenger/exoplayer/upstream/DefaultAllocator:availableCount	I
    //   28: iconst_1
    //   29: isub
    //   30: istore_1
    //   31: aload_0
    //   32: iload_1
    //   33: putfield 35	org/telegram/messenger/exoplayer/upstream/DefaultAllocator:availableCount	I
    //   36: aload_2
    //   37: iload_1
    //   38: aaload
    //   39: astore_2
    //   40: aload_0
    //   41: getfield 39	org/telegram/messenger/exoplayer/upstream/DefaultAllocator:availableAllocations	[Lorg/telegram/messenger/exoplayer/upstream/Allocation;
    //   44: aload_0
    //   45: getfield 35	org/telegram/messenger/exoplayer/upstream/DefaultAllocator:availableCount	I
    //   48: aconst_null
    //   49: aastore
    //   50: aload_0
    //   51: monitorexit
    //   52: aload_2
    //   53: areturn
    //   54: new 37	org/telegram/messenger/exoplayer/upstream/Allocation
    //   57: dup
    //   58: aload_0
    //   59: getfield 33	org/telegram/messenger/exoplayer/upstream/DefaultAllocator:individualAllocationSize	I
    //   62: newarray <illegal type>
    //   64: iconst_0
    //   65: invokespecial 44	org/telegram/messenger/exoplayer/upstream/Allocation:<init>	([BI)V
    //   68: astore_2
    //   69: goto -19 -> 50
    //   72: astore_2
    //   73: aload_0
    //   74: monitorexit
    //   75: aload_2
    //   76: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	77	0	this	DefaultAllocator
    //   30	8	1	i	int
    //   23	46	2	localObject1	Object
    //   72	4	2	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   2	36	72	finally
    //   40	50	72	finally
    //   54	69	72	finally
  }
  
  /* Error */
  public void blockWhileTotalBytesAllocatedExceeds(int paramInt)
    throws java.lang.InterruptedException
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: invokevirtual 55	org/telegram/messenger/exoplayer/upstream/DefaultAllocator:getTotalBytesAllocated	()I
    //   6: iload_1
    //   7: if_icmple +15 -> 22
    //   10: aload_0
    //   11: invokevirtual 58	java/lang/Object:wait	()V
    //   14: goto -12 -> 2
    //   17: astore_2
    //   18: aload_0
    //   19: monitorexit
    //   20: aload_2
    //   21: athrow
    //   22: aload_0
    //   23: monitorexit
    //   24: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	25	0	this	DefaultAllocator
    //   0	25	1	paramInt	int
    //   17	4	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	14	17	finally
  }
  
  public int getIndividualAllocationLength()
  {
    return this.individualAllocationSize;
  }
  
  public int getTotalBytesAllocated()
  {
    try
    {
      int i = this.allocatedCount;
      int j = this.individualAllocationSize;
      return i * j;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void release(Allocation paramAllocation)
  {
    label115:
    for (;;)
    {
      try
      {
        if (paramAllocation.data != this.initialAllocationBlock) {
          if (paramAllocation.data.length == this.individualAllocationSize)
          {
            break label115;
            Assertions.checkArgument(bool);
            this.allocatedCount -= 1;
            if (this.availableCount == this.availableAllocations.length) {
              this.availableAllocations = ((Allocation[])Arrays.copyOf(this.availableAllocations, this.availableAllocations.length * 2));
            }
            Allocation[] arrayOfAllocation = this.availableAllocations;
            int i = this.availableCount;
            this.availableCount = (i + 1);
            arrayOfAllocation[i] = paramAllocation;
            notifyAll();
          }
          else
          {
            bool = false;
            continue;
          }
        }
        boolean bool = true;
      }
      finally {}
    }
  }
  
  public void release(Allocation[] paramArrayOfAllocation)
  {
    for (;;)
    {
      try
      {
        if (this.availableCount + paramArrayOfAllocation.length >= this.availableAllocations.length) {
          this.availableAllocations = ((Allocation[])Arrays.copyOf(this.availableAllocations, Math.max(this.availableAllocations.length * 2, this.availableCount + paramArrayOfAllocation.length)));
        }
        int j = paramArrayOfAllocation.length;
        int i = 0;
        if (i < j)
        {
          Allocation localAllocation = paramArrayOfAllocation[i];
          if (localAllocation.data != this.initialAllocationBlock)
          {
            if (localAllocation.data.length != this.individualAllocationSize) {
              break label159;
            }
            break label153;
            Assertions.checkArgument(bool);
            Allocation[] arrayOfAllocation = this.availableAllocations;
            int k = this.availableCount;
            this.availableCount = (k + 1);
            arrayOfAllocation[k] = localAllocation;
            i += 1;
          }
        }
        else
        {
          this.allocatedCount -= paramArrayOfAllocation.length;
          notifyAll();
          return;
        }
      }
      finally {}
      label153:
      boolean bool = true;
      continue;
      label159:
      bool = false;
    }
  }
  
  public void trim(int paramInt)
  {
    for (;;)
    {
      int j;
      int i;
      try
      {
        j = Math.max(0, Util.ceilDivide(paramInt, this.individualAllocationSize) - this.allocatedCount);
        paramInt = this.availableCount;
        if (j >= paramInt) {
          return;
        }
        paramInt = j;
        if (this.initialAllocationBlock == null) {
          break label172;
        }
        paramInt = this.availableCount - 1;
        i = 0;
        if (i <= paramInt)
        {
          Allocation localAllocation = this.availableAllocations[i];
          if (localAllocation.data == this.initialAllocationBlock)
          {
            i += 1;
          }
          else
          {
            Object localObject2 = this.availableAllocations[i];
            if (((Allocation)localObject2).data != this.initialAllocationBlock)
            {
              paramInt -= 1;
            }
            else
            {
              Allocation[] arrayOfAllocation = this.availableAllocations;
              int k = i + 1;
              arrayOfAllocation[i] = localObject2;
              localObject2 = this.availableAllocations;
              i = paramInt - 1;
              localObject2[paramInt] = localAllocation;
              paramInt = i;
              i = k;
            }
          }
        }
      }
      finally {}
      paramInt = Math.max(j, i);
      if (paramInt < this.availableCount)
      {
        label172:
        Arrays.fill(this.availableAllocations, paramInt, this.availableCount, null);
        this.availableCount = paramInt;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/upstream/DefaultAllocator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */