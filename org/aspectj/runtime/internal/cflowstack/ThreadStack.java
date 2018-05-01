package org.aspectj.runtime.internal.cflowstack;

import java.util.Stack;

public abstract interface ThreadStack
{
  public abstract Stack getThreadStack();
  
  public abstract void removeThreadStack();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/runtime/internal/cflowstack/ThreadStack.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */