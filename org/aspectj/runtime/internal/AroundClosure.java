package org.aspectj.runtime.internal;

import org.aspectj.lang.ProceedingJoinPoint;

public abstract class AroundClosure
{
  protected int bitflags = 1048576;
  protected Object[] preInitializationState;
  protected Object[] state;
  
  public AroundClosure() {}
  
  public AroundClosure(Object[] paramArrayOfObject)
  {
    this.state = paramArrayOfObject;
  }
  
  public int getFlags()
  {
    return this.bitflags;
  }
  
  public Object[] getPreInitializationState()
  {
    return this.preInitializationState;
  }
  
  public Object[] getState()
  {
    return this.state;
  }
  
  public ProceedingJoinPoint linkClosureAndJoinPoint()
  {
    ProceedingJoinPoint localProceedingJoinPoint = (ProceedingJoinPoint)this.state[(this.state.length - 1)];
    localProceedingJoinPoint.set$AroundClosure(this);
    return localProceedingJoinPoint;
  }
  
  public ProceedingJoinPoint linkClosureAndJoinPoint(int paramInt)
  {
    ProceedingJoinPoint localProceedingJoinPoint = (ProceedingJoinPoint)this.state[(this.state.length - 1)];
    localProceedingJoinPoint.set$AroundClosure(this);
    this.bitflags = paramInt;
    return localProceedingJoinPoint;
  }
  
  public abstract Object run(Object[] paramArrayOfObject)
    throws Throwable;
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/runtime/internal/AroundClosure.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */