package org.aspectj.runtime.reflect;

import java.lang.reflect.Constructor;
import org.aspectj.lang.reflect.ConstructorSignature;

class ConstructorSignatureImpl
  extends CodeSignatureImpl
  implements ConstructorSignature
{
  private Constructor constructor;
  
  ConstructorSignatureImpl(int paramInt, Class paramClass, Class[] paramArrayOfClass1, String[] paramArrayOfString, Class[] paramArrayOfClass2)
  {
    super(paramInt, "<init>", paramClass, paramArrayOfClass1, paramArrayOfString, paramArrayOfClass2);
  }
  
  ConstructorSignatureImpl(String paramString)
  {
    super(paramString);
  }
  
  protected String createToString(StringMaker paramStringMaker)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(paramStringMaker.makeModifiersString(getModifiers()));
    localStringBuffer.append(paramStringMaker.makePrimaryTypeName(getDeclaringType(), getDeclaringTypeName()));
    paramStringMaker.addSignature(localStringBuffer, getParameterTypes());
    paramStringMaker.addThrows(localStringBuffer, getExceptionTypes());
    return localStringBuffer.toString();
  }
  
  public Constructor getConstructor()
  {
    if (this.constructor == null) {}
    try
    {
      this.constructor = getDeclaringType().getDeclaredConstructor(getParameterTypes());
      return this.constructor;
    }
    catch (Exception localException)
    {
      for (;;) {}
    }
  }
  
  public String getName()
  {
    return "<init>";
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/runtime/reflect/ConstructorSignatureImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */