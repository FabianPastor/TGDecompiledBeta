package org.aspectj.runtime.internal;

import org.aspectj.runtime.CFlow;

public class CFlowPlusState
  extends CFlow
{
  private Object[] state;
  
  public CFlowPlusState(Object[] paramArrayOfObject)
  {
    this.state = paramArrayOfObject;
  }
  
  public CFlowPlusState(Object[] paramArrayOfObject, Object paramObject)
  {
    super(paramObject);
    this.state = paramArrayOfObject;
  }
  
  public Object get(int paramInt)
  {
    return this.state[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/runtime/internal/CFlowPlusState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */