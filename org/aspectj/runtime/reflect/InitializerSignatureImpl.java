package org.aspectj.runtime.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import org.aspectj.lang.reflect.InitializerSignature;

class InitializerSignatureImpl
  extends CodeSignatureImpl
  implements InitializerSignature
{
  private Constructor constructor;
  
  InitializerSignatureImpl(int paramInt, Class paramClass) {}
  
  InitializerSignatureImpl(String paramString)
  {
    super(paramString);
  }
  
  protected String createToString(StringMaker paramStringMaker)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(paramStringMaker.makeModifiersString(getModifiers()));
    localStringBuffer.append(paramStringMaker.makePrimaryTypeName(getDeclaringType(), getDeclaringTypeName()));
    localStringBuffer.append(".");
    localStringBuffer.append(getName());
    return localStringBuffer.toString();
  }
  
  public Constructor getInitializer()
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
    if (Modifier.isStatic(getModifiers())) {
      return "<clinit>";
    }
    return "<init>";
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/runtime/reflect/InitializerSignatureImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */