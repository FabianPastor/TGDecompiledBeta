package org.aspectj.lang.reflect;

public abstract interface DeclarePrecedence
{
  public abstract AjType getDeclaringType();
  
  public abstract TypePattern[] getPrecedenceOrder();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/lang/reflect/DeclarePrecedence.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */