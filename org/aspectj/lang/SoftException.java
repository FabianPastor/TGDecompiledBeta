package org.aspectj.lang;

import java.io.PrintStream;
import java.io.PrintWriter;

public class SoftException
  extends RuntimeException
{
  private static final boolean HAVE_JAVA_14;
  Throwable inner;
  
  static
  {
    boolean bool = false;
    try
    {
      Class.forName("java.nio.Buffer");
      bool = true;
    }
    catch (Throwable localThrowable)
    {
      for (;;) {}
    }
    HAVE_JAVA_14 = bool;
  }
  
  public SoftException(Throwable paramThrowable)
  {
    this.inner = paramThrowable;
  }
  
  public Throwable getCause()
  {
    return this.inner;
  }
  
  public Throwable getWrappedThrowable()
  {
    return this.inner;
  }
  
  public void printStackTrace()
  {
    printStackTrace(System.err);
  }
  
  public void printStackTrace(PrintStream paramPrintStream)
  {
    super.printStackTrace(paramPrintStream);
    Throwable localThrowable = this.inner;
    if ((!HAVE_JAVA_14) && (localThrowable != null))
    {
      paramPrintStream.print("Caused by: ");
      localThrowable.printStackTrace(paramPrintStream);
    }
  }
  
  public void printStackTrace(PrintWriter paramPrintWriter)
  {
    super.printStackTrace(paramPrintWriter);
    Throwable localThrowable = this.inner;
    if ((!HAVE_JAVA_14) && (localThrowable != null))
    {
      paramPrintWriter.print("Caused by: ");
      localThrowable.printStackTrace(paramPrintWriter);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/lang/SoftException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */