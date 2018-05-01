package org.aspectj.runtime.reflect;

import org.aspectj.lang.reflect.MethodSignature;

class MethodSignatureImpl
  extends CodeSignatureImpl
  implements MethodSignature
{
  Class returnType;
  
  MethodSignatureImpl(int paramInt, String paramString, Class paramClass1, Class[] paramArrayOfClass1, String[] paramArrayOfString, Class[] paramArrayOfClass2, Class paramClass2)
  {
    super(paramInt, paramString, paramClass1, paramArrayOfClass1, paramArrayOfString, paramArrayOfClass2);
    this.returnType = paramClass2;
  }
  
  protected String createToString(StringMaker paramStringMaker)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(paramStringMaker.makeModifiersString(getModifiers()));
    if (paramStringMaker.includeArgs) {
      localStringBuffer.append(paramStringMaker.makeTypeName(getReturnType()));
    }
    if (paramStringMaker.includeArgs) {
      localStringBuffer.append(" ");
    }
    localStringBuffer.append(paramStringMaker.makePrimaryTypeName(getDeclaringType(), getDeclaringTypeName()));
    localStringBuffer.append(".");
    localStringBuffer.append(getName());
    paramStringMaker.addSignature(localStringBuffer, getParameterTypes());
    paramStringMaker.addThrows(localStringBuffer, getExceptionTypes());
    return localStringBuffer.toString();
  }
  
  public Class getReturnType()
  {
    if (this.returnType == null) {
      this.returnType = extractType(6);
    }
    return this.returnType;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/runtime/reflect/MethodSignatureImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */