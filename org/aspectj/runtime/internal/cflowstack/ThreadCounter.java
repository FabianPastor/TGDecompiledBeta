package org.aspectj.runtime.internal.cflowstack;

public abstract interface ThreadCounter
{
  public abstract void dec();
  
  public abstract void inc();
  
  public abstract boolean isNotZero();
  
  public abstract void removeThreadCounter();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/runtime/internal/cflowstack/ThreadCounter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */