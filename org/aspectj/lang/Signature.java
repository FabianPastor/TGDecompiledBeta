package org.aspectj.lang;

public abstract interface Signature
{
  public abstract Class getDeclaringType();
  
  public abstract String getDeclaringTypeName();
  
  public abstract int getModifiers();
  
  public abstract String getName();
  
  public abstract String toLongString();
  
  public abstract String toShortString();
  
  public abstract String toString();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/lang/Signature.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */