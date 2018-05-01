package org.aspectj.lang.reflect;

public class NoSuchAdviceException
  extends Exception
{
  private static final long serialVersionUID = 3256444698657634352L;
  private String name;
  
  public NoSuchAdviceException(String paramString)
  {
    this.name = paramString;
  }
  
  public String getName()
  {
    return this.name;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/lang/reflect/NoSuchAdviceException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */