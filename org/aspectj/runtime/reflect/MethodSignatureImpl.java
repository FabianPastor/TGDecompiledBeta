package org.aspectj.runtime.reflect;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import org.aspectj.lang.reflect.MethodSignature;

class MethodSignatureImpl
  extends CodeSignatureImpl
  implements MethodSignature
{
  private Method method;
  Class returnType;
  
  MethodSignatureImpl(int paramInt, String paramString, Class paramClass1, Class[] paramArrayOfClass1, String[] paramArrayOfString, Class[] paramArrayOfClass2, Class paramClass2)
  {
    super(paramInt, paramString, paramClass1, paramArrayOfClass1, paramArrayOfString, paramArrayOfClass2);
    this.returnType = paramClass2;
  }
  
  MethodSignatureImpl(String paramString)
  {
    super(paramString);
  }
  
  private Method search(Class paramClass, String paramString, Class[] paramArrayOfClass, Set paramSet)
  {
    Method localMethod;
    if (paramClass == null) {
      localMethod = null;
    }
    Object localObject1;
    do
    {
      return localMethod;
      if (!paramSet.contains(paramClass))
      {
        paramSet.add(paramClass);
        try
        {
          localMethod = paramClass.getDeclaredMethod(paramString, paramArrayOfClass);
          return localMethod;
        }
        catch (NoSuchMethodException localNoSuchMethodException) {}
      }
      localObject2 = search(paramClass.getSuperclass(), paramString, paramArrayOfClass, paramSet);
      localObject1 = localObject2;
    } while (localObject2 != null);
    Object localObject2 = paramClass.getInterfaces();
    if (localObject2 != null)
    {
      int i = 0;
      for (;;)
      {
        if (i >= localObject2.length) {
          break label118;
        }
        paramClass = search(localObject2[i], paramString, paramArrayOfClass, paramSet);
        localObject1 = paramClass;
        if (paramClass != null) {
          break;
        }
        i += 1;
      }
    }
    label118:
    return null;
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
  
  public Method getMethod()
  {
    Class localClass;
    if (this.method == null) {
      localClass = getDeclaringType();
    }
    try
    {
      this.method = localClass.getDeclaredMethod(getName(), getParameterTypes());
      return this.method;
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      for (;;)
      {
        HashSet localHashSet = new HashSet();
        localHashSet.add(localClass);
        this.method = search(localClass, getName(), getParameterTypes(), localHashSet);
      }
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/runtime/reflect/MethodSignatureImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */