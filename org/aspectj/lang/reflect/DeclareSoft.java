package org.aspectj.lang.reflect;

public abstract interface DeclareSoft
{
  public abstract AjType getDeclaringType();
  
  public abstract PointcutExpression getPointcutExpression();
  
  public abstract AjType getSoftenedExceptionType()
    throws ClassNotFoundException;
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/lang/reflect/DeclareSoft.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */