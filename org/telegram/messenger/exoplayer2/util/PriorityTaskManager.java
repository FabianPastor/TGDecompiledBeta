package org.telegram.messenger.exoplayer2.util;

import java.io.IOException;
import java.util.Collections;
import java.util.PriorityQueue;

public final class PriorityTaskManager
{
  private int highestPriority = Integer.MIN_VALUE;
  private final Object lock = new Object();
  private final PriorityQueue<Integer> queue = new PriorityQueue(10, Collections.reverseOrder());
  
  public void add(int paramInt)
  {
    synchronized (this.lock)
    {
      this.queue.add(Integer.valueOf(paramInt));
      this.highestPriority = Math.max(this.highestPriority, paramInt);
      return;
    }
  }
  
  public void proceed(int paramInt)
    throws InterruptedException
  {
    synchronized (this.lock)
    {
      if (this.highestPriority != paramInt) {
        this.lock.wait();
      }
    }
  }
  
  public boolean proceedNonBlocking(int paramInt)
  {
    synchronized (this.lock)
    {
      if (this.highestPriority == paramInt)
      {
        bool = true;
        return bool;
      }
      boolean bool = false;
    }
  }
  
  public void proceedOrThrow(int paramInt)
    throws PriorityTaskManager.PriorityTooLowException
  {
    synchronized (this.lock)
    {
      if (this.highestPriority != paramInt)
      {
        PriorityTooLowException localPriorityTooLowException = new org/telegram/messenger/exoplayer2/util/PriorityTaskManager$PriorityTooLowException;
        localPriorityTooLowException.<init>(paramInt, this.highestPriority);
        throw localPriorityTooLowException;
      }
    }
  }
  
  public void remove(int paramInt)
  {
    synchronized (this.lock)
    {
      this.queue.remove(Integer.valueOf(paramInt));
      if (this.queue.isEmpty())
      {
        paramInt = Integer.MIN_VALUE;
        this.highestPriority = paramInt;
        this.lock.notifyAll();
        return;
      }
      paramInt = ((Integer)this.queue.peek()).intValue();
    }
  }
  
  public static class PriorityTooLowException
    extends IOException
  {
    public PriorityTooLowException(int paramInt1, int paramInt2)
    {
      super();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/util/PriorityTaskManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */