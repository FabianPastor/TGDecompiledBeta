package org.aspectj.lang;

import org.aspectj.runtime.internal.AroundClosure;

public abstract interface ProceedingJoinPoint
  extends JoinPoint
{
  public abstract Object proceed()
    throws Throwable;
  
  public abstract Object proceed(Object[] paramArrayOfObject)
    throws Throwable;
  
  public abstract void set$AroundClosure(AroundClosure paramAroundClosure);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/lang/ProceedingJoinPoint.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */