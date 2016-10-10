package org.aspectj.runtime.internal.cflowstack;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

public class ThreadCounterImpl11
  implements ThreadCounter
{
  private static final int COLLECT_AT = 20000;
  private static final int MIN_COLLECT_AT = 100;
  private Counter cached_counter;
  private Thread cached_thread;
  private int change_count = 0;
  private Hashtable counters = new Hashtable();
  
  private Counter getThreadCounter()
  {
    Object localObject3;
    try
    {
      if (Thread.currentThread() == this.cached_thread) {
        break label214;
      }
      this.cached_thread = Thread.currentThread();
      this.cached_counter = ((Counter)this.counters.get(this.cached_thread));
      if (this.cached_counter == null)
      {
        this.cached_counter = new Counter();
        this.counters.put(this.cached_thread, this.cached_counter);
      }
      this.change_count += 1;
      int i = Math.max(1, this.counters.size());
      if (this.change_count <= Math.max(100, 20000 / i)) {
        break label214;
      }
      ArrayList localArrayList = new ArrayList();
      localObject3 = this.counters.keys();
      while (((Enumeration)localObject3).hasMoreElements())
      {
        Thread localThread = (Thread)((Enumeration)localObject3).nextElement();
        if (!localThread.isAlive()) {
          localArrayList.add(localThread);
        }
      }
      localObject2 = ((List)localObject1).iterator();
    }
    finally {}
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (Thread)((Iterator)localObject2).next();
      this.counters.remove(localObject3);
    }
    this.change_count = 0;
    label214:
    Object localObject2 = this.cached_counter;
    return (Counter)localObject2;
  }
  
  public void dec()
  {
    Counter localCounter = getThreadCounter();
    localCounter.value -= 1;
  }
  
  public void inc()
  {
    Counter localCounter = getThreadCounter();
    localCounter.value += 1;
  }
  
  public boolean isNotZero()
  {
    return getThreadCounter().value != 0;
  }
  
  public void removeThreadCounter() {}
  
  static class Counter
  {
    protected int value = 0;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/runtime/internal/cflowstack/ThreadCounterImpl11.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */