package org.aspectj.lang;

public class NoAspectBoundException
  extends RuntimeException
{
  Throwable cause;
  
  public NoAspectBoundException() {}
  
  public NoAspectBoundException(String paramString, Throwable paramThrowable) {}
  
  public Throwable getCause()
  {
    return this.cause;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/lang/NoAspectBoundException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */