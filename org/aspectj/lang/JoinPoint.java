package org.aspectj.lang;

public abstract interface JoinPoint
{
  public abstract Object getTarget();
  
  public static abstract interface StaticPart
  {
    public abstract String toString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/lang/JoinPoint.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */