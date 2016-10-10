package org.aspectj.runtime.reflect;

import org.aspectj.lang.reflect.CatchClauseSignature;

class CatchClauseSignatureImpl
  extends SignatureImpl
  implements CatchClauseSignature
{
  String parameterName;
  Class parameterType;
  
  CatchClauseSignatureImpl(Class paramClass1, Class paramClass2, String paramString)
  {
    super(0, "catch", paramClass1);
    this.parameterType = paramClass2;
    this.parameterName = paramString;
  }
  
  CatchClauseSignatureImpl(String paramString)
  {
    super(paramString);
  }
  
  protected String createToString(StringMaker paramStringMaker)
  {
    return "catch(" + paramStringMaker.makeTypeName(getParameterType()) + ")";
  }
  
  public String getParameterName()
  {
    if (this.parameterName == null) {
      this.parameterName = extractString(4);
    }
    return this.parameterName;
  }
  
  public Class getParameterType()
  {
    if (this.parameterType == null) {
      this.parameterType = extractType(3);
    }
    return this.parameterType;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/runtime/reflect/CatchClauseSignatureImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */