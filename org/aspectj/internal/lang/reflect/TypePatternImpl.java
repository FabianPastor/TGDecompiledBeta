package org.aspectj.internal.lang.reflect;

import org.aspectj.lang.reflect.TypePattern;

public class TypePatternImpl
  implements TypePattern
{
  private String typePattern;
  
  public TypePatternImpl(String paramString)
  {
    this.typePattern = paramString;
  }
  
  public String asString()
  {
    return this.typePattern;
  }
  
  public String toString()
  {
    return asString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/internal/lang/reflect/TypePatternImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */