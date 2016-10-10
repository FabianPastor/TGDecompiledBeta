package org.aspectj.runtime.reflect;

import java.lang.reflect.Method;
import java.util.StringTokenizer;
import org.aspectj.lang.reflect.AdviceSignature;

class AdviceSignatureImpl
  extends CodeSignatureImpl
  implements AdviceSignature
{
  private Method adviceMethod = null;
  Class returnType;
  
  AdviceSignatureImpl(int paramInt, String paramString, Class paramClass1, Class[] paramArrayOfClass1, String[] paramArrayOfString, Class[] paramArrayOfClass2, Class paramClass2)
  {
    super(paramInt, paramString, paramClass1, paramArrayOfClass1, paramArrayOfString, paramArrayOfClass2);
    this.returnType = paramClass2;
  }
  
  AdviceSignatureImpl(String paramString)
  {
    super(paramString);
  }
  
  private String toAdviceName(String paramString)
  {
    if (paramString.indexOf('$') == -1) {}
    String str;
    do
    {
      StringTokenizer localStringTokenizer;
      while (!localStringTokenizer.hasMoreTokens())
      {
        return paramString;
        localStringTokenizer = new StringTokenizer(paramString, "$");
      }
      str = localStringTokenizer.nextToken();
    } while ((!str.startsWith("before")) && (!str.startsWith("after")) && (!str.startsWith("around")));
    return str;
  }
  
  protected String createToString(StringMaker paramStringMaker)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    if (paramStringMaker.includeArgs) {
      localStringBuffer.append(paramStringMaker.makeTypeName(getReturnType()));
    }
    if (paramStringMaker.includeArgs) {
      localStringBuffer.append(" ");
    }
    localStringBuffer.append(paramStringMaker.makePrimaryTypeName(getDeclaringType(), getDeclaringTypeName()));
    localStringBuffer.append(".");
    localStringBuffer.append(toAdviceName(getName()));
    paramStringMaker.addSignature(localStringBuffer, getParameterTypes());
    paramStringMaker.addThrows(localStringBuffer, getExceptionTypes());
    return localStringBuffer.toString();
  }
  
  public Method getAdvice()
  {
    if (this.adviceMethod == null) {}
    try
    {
      this.adviceMethod = getDeclaringType().getDeclaredMethod(getName(), getParameterTypes());
      return this.adviceMethod;
    }
    catch (Exception localException)
    {
      for (;;) {}
    }
  }
  
  public Class getReturnType()
  {
    if (this.returnType == null) {
      this.returnType = extractType(6);
    }
    return this.returnType;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/runtime/reflect/AdviceSignatureImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */