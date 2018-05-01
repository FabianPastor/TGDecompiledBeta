package org.aspectj.lang.reflect;

public abstract interface DeclareErrorOrWarning
{
  public abstract AjType getDeclaringType();
  
  public abstract String getMessage();
  
  public abstract PointcutExpression getPointcutExpression();
  
  public abstract boolean isError();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/lang/reflect/DeclareErrorOrWarning.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */