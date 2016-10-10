package org.aspectj.internal.lang.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.AjTypeSystem;
import org.aspectj.lang.reflect.InterTypeConstructorDeclaration;

public class InterTypeConstructorDeclarationImpl
  extends InterTypeDeclarationImpl
  implements InterTypeConstructorDeclaration
{
  private Method baseMethod;
  
  public InterTypeConstructorDeclarationImpl(AjType<?> paramAjType, String paramString, int paramInt, Method paramMethod)
  {
    super(paramAjType, paramString, paramInt);
    this.baseMethod = paramMethod;
  }
  
  public AjType<?>[] getExceptionTypes()
  {
    Class[] arrayOfClass = this.baseMethod.getExceptionTypes();
    AjType[] arrayOfAjType = new AjType[arrayOfClass.length];
    int i = 0;
    while (i < arrayOfClass.length)
    {
      arrayOfAjType[i] = AjTypeSystem.getAjType(arrayOfClass[i]);
      i += 1;
    }
    return arrayOfAjType;
  }
  
  public Type[] getGenericParameterTypes()
  {
    Type[] arrayOfType = this.baseMethod.getGenericParameterTypes();
    AjType[] arrayOfAjType = new AjType[arrayOfType.length - 1];
    int i = 1;
    if (i < arrayOfType.length)
    {
      if ((arrayOfType[i] instanceof Class)) {
        arrayOfAjType[(i - 1)] = AjTypeSystem.getAjType((Class)arrayOfType[i]);
      }
      for (;;)
      {
        i += 1;
        break;
        arrayOfAjType[(i - 1)] = arrayOfType[i];
      }
    }
    return arrayOfAjType;
  }
  
  public AjType<?>[] getParameterTypes()
  {
    Class[] arrayOfClass = this.baseMethod.getParameterTypes();
    AjType[] arrayOfAjType = new AjType[arrayOfClass.length - 1];
    int i = 1;
    while (i < arrayOfClass.length)
    {
      arrayOfAjType[(i - 1)] = AjTypeSystem.getAjType(arrayOfClass[i]);
      i += 1;
    }
    return arrayOfAjType;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(Modifier.toString(getModifiers()));
    localStringBuffer.append(" ");
    localStringBuffer.append(this.targetTypeName);
    localStringBuffer.append(".new");
    localStringBuffer.append("(");
    AjType[] arrayOfAjType = getParameterTypes();
    int i = 0;
    while (i < arrayOfAjType.length - 1)
    {
      localStringBuffer.append(arrayOfAjType[i].toString());
      localStringBuffer.append(", ");
      i += 1;
    }
    if (arrayOfAjType.length > 0) {
      localStringBuffer.append(arrayOfAjType[(arrayOfAjType.length - 1)].toString());
    }
    localStringBuffer.append(")");
    return localStringBuffer.toString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/internal/lang/reflect/InterTypeConstructorDeclarationImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */