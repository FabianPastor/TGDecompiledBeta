package org.aspectj.lang.reflect;

import java.lang.reflect.Method;

public abstract interface AdviceSignature
  extends CodeSignature
{
  public abstract Method getAdvice();
  
  public abstract Class getReturnType();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/lang/reflect/AdviceSignature.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */