package org.aspectj.runtime.reflect;

import org.aspectj.lang.reflect.SourceLocation;

class SourceLocationImpl
  implements SourceLocation
{
  String fileName;
  int line;
  Class withinType;
  
  SourceLocationImpl(Class paramClass, String paramString, int paramInt)
  {
    this.withinType = paramClass;
    this.fileName = paramString;
    this.line = paramInt;
  }
  
  public String getFileName()
  {
    return this.fileName;
  }
  
  public int getLine()
  {
    return this.line;
  }
  
  public String toString()
  {
    return getFileName() + ":" + getLine();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/runtime/reflect/SourceLocationImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */