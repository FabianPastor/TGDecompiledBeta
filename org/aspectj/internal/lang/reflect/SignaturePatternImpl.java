package org.aspectj.internal.lang.reflect;

import org.aspectj.lang.reflect.SignaturePattern;

public class SignaturePatternImpl
  implements SignaturePattern
{
  private String sigPattern;
  
  public SignaturePatternImpl(String paramString)
  {
    this.sigPattern = paramString;
  }
  
  public String asString()
  {
    return this.sigPattern;
  }
  
  public String toString()
  {
    return asString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/internal/lang/reflect/SignaturePatternImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */