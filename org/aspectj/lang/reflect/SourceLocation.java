package org.aspectj.lang.reflect;

public abstract interface SourceLocation
{
  public abstract int getColumn();
  
  public abstract String getFileName();
  
  public abstract int getLine();
  
  public abstract Class getWithinType();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/lang/reflect/SourceLocation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */