package org.aspectj.runtime.internal.cflowstack;

import java.util.Stack;

public class ThreadStackFactoryImpl
  implements ThreadStackFactory
{
  public ThreadCounter getNewThreadCounter()
  {
    return new ThreadCounterImpl(null);
  }
  
  public ThreadStack getNewThreadStack()
  {
    return new ThreadStackImpl(null);
  }
  
  private static class ThreadCounterImpl
    extends ThreadLocal
    implements ThreadCounter
  {
    private ThreadCounterImpl() {}
    
    ThreadCounterImpl(ThreadStackFactoryImpl.1 param1)
    {
      this();
    }
    
    public void dec()
    {
      Counter localCounter = getThreadCounter();
      localCounter.value -= 1;
    }
    
    public Counter getThreadCounter()
    {
      return (Counter)get();
    }
    
    public void inc()
    {
      Counter localCounter = getThreadCounter();
      localCounter.value += 1;
    }
    
    public Object initialValue()
    {
      return new Counter();
    }
    
    public boolean isNotZero()
    {
      return getThreadCounter().value != 0;
    }
    
    public void removeThreadCounter()
    {
      remove();
    }
    
    static class Counter
    {
      protected int value = 0;
    }
  }
  
  private static class ThreadStackImpl
    extends ThreadLocal
    implements ThreadStack
  {
    private ThreadStackImpl() {}
    
    ThreadStackImpl(ThreadStackFactoryImpl.1 param1)
    {
      this();
    }
    
    public Stack getThreadStack()
    {
      return (Stack)get();
    }
    
    public Object initialValue()
    {
      return new Stack();
    }
    
    public void removeThreadStack()
    {
      remove();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/runtime/internal/cflowstack/ThreadStackFactoryImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */