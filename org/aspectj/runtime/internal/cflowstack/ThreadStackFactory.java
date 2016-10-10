package org.aspectj.runtime.internal.cflowstack;

public abstract interface ThreadStackFactory
{
  public abstract ThreadCounter getNewThreadCounter();
  
  public abstract ThreadStack getNewThreadStack();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/runtime/internal/cflowstack/ThreadStackFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */