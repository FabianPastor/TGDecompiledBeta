package org.aspectj.lang.reflect;

public abstract interface Pointcut
{
  public abstract AjType getDeclaringType();
  
  public abstract int getModifiers();
  
  public abstract String getName();
  
  public abstract String[] getParameterNames();
  
  public abstract AjType<?>[] getParameterTypes();
  
  public abstract PointcutExpression getPointcutExpression();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/lang/reflect/Pointcut.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */