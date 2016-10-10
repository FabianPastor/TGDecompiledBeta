package org.aspectj.runtime.internal.cflowstack;

public class ThreadStackFactoryImpl11
  implements ThreadStackFactory
{
  public ThreadCounter getNewThreadCounter()
  {
    return new ThreadCounterImpl11();
  }
  
  public ThreadStack getNewThreadStack()
  {
    return new ThreadStackImpl11();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/runtime/internal/cflowstack/ThreadStackFactoryImpl11.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */