package org.aspectj.runtime.internal.cflowstack;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

public class ThreadStackImpl11
  implements ThreadStack
{
  private static final int COLLECT_AT = 20000;
  private static final int MIN_COLLECT_AT = 100;
  private Stack cached_stack;
  private Thread cached_thread;
  private int change_count = 0;
  private Hashtable stacks = new Hashtable();
  
  public Stack getThreadStack()
  {
    Object localObject3;
    try
    {
      if (Thread.currentThread() == this.cached_thread) {
        break label210;
      }
      this.cached_thread = Thread.currentThread();
      this.cached_stack = ((Stack)this.stacks.get(this.cached_thread));
      if (this.cached_stack == null)
      {
        this.cached_stack = new Stack();
        this.stacks.put(this.cached_thread, this.cached_stack);
      }
      this.change_count += 1;
      int i = Math.max(1, this.stacks.size());
      if (this.change_count <= Math.max(100, 20000 / i)) {
        break label210;
      }
      Stack localStack = new Stack();
      localObject3 = this.stacks.keys();
      while (((Enumeration)localObject3).hasMoreElements())
      {
        Thread localThread = (Thread)((Enumeration)localObject3).nextElement();
        if (!localThread.isAlive()) {
          localStack.push(localThread);
        }
      }
      localObject2 = ((Vector)localObject1).elements();
    }
    finally {}
    while (((Enumeration)localObject2).hasMoreElements())
    {
      localObject3 = (Thread)((Enumeration)localObject2).nextElement();
      this.stacks.remove(localObject3);
    }
    this.change_count = 0;
    label210:
    Object localObject2 = this.cached_stack;
    return (Stack)localObject2;
  }
  
  public void removeThreadStack() {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/runtime/internal/cflowstack/ThreadStackImpl11.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */