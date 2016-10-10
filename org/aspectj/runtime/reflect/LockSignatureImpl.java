package org.aspectj.runtime.reflect;

import org.aspectj.lang.reflect.LockSignature;

class LockSignatureImpl
  extends SignatureImpl
  implements LockSignature
{
  private Class parameterType;
  
  LockSignatureImpl(Class paramClass)
  {
    super(8, "lock", paramClass);
    this.parameterType = paramClass;
  }
  
  LockSignatureImpl(String paramString)
  {
    super(paramString);
  }
  
  protected String createToString(StringMaker paramStringMaker)
  {
    if (this.parameterType == null) {
      this.parameterType = extractType(3);
    }
    return "lock(" + paramStringMaker.makeTypeName(this.parameterType) + ")";
  }
  
  public Class getParameterType()
  {
    if (this.parameterType == null) {
      this.parameterType = extractType(3);
    }
    return this.parameterType;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/runtime/reflect/LockSignatureImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */