package org.aspectj.internal.lang.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.AjTypeSystem;
import org.aspectj.lang.reflect.InterTypeMethodDeclaration;

public class InterTypeMethodDeclarationImpl
  extends InterTypeDeclarationImpl
  implements InterTypeMethodDeclaration
{
  private Method baseMethod;
  private AjType<?>[] exceptionTypes;
  private Type[] genericParameterTypes;
  private Type genericReturnType;
  private String name;
  private int parameterAdjustmentFactor = 1;
  private AjType<?>[] parameterTypes;
  private AjType<?> returnType;
  
  public InterTypeMethodDeclarationImpl(AjType<?> paramAjType, String paramString1, int paramInt, String paramString2, Method paramMethod)
  {
    super(paramAjType, paramString1, paramInt);
    this.name = paramString2;
    this.baseMethod = paramMethod;
  }
  
  public InterTypeMethodDeclarationImpl(AjType<?> paramAjType1, AjType<?> paramAjType2, Method paramMethod, int paramInt)
  {
    super(paramAjType1, paramAjType2, paramInt);
    this.parameterAdjustmentFactor = 0;
    this.name = paramMethod.getName();
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
    AjType[] arrayOfAjType = new AjType[arrayOfType.length - this.parameterAdjustmentFactor];
    int i = this.parameterAdjustmentFactor;
    if (i < arrayOfType.length)
    {
      if ((arrayOfType[i] instanceof Class)) {
        arrayOfAjType[(i - this.parameterAdjustmentFactor)] = AjTypeSystem.getAjType((Class)arrayOfType[i]);
      }
      for (;;)
      {
        i += 1;
        break;
        arrayOfAjType[(i - this.parameterAdjustmentFactor)] = arrayOfType[i];
      }
    }
    return arrayOfAjType;
  }
  
  public Type getGenericReturnType()
  {
    Type localType = this.baseMethod.getGenericReturnType();
    Object localObject = localType;
    if ((localType instanceof Class)) {
      localObject = AjTypeSystem.getAjType((Class)localType);
    }
    return (Type)localObject;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public AjType<?>[] getParameterTypes()
  {
    Class[] arrayOfClass = this.baseMethod.getParameterTypes();
    AjType[] arrayOfAjType = new AjType[arrayOfClass.length - this.parameterAdjustmentFactor];
    int i = this.parameterAdjustmentFactor;
    while (i < arrayOfClass.length)
    {
      arrayOfAjType[(i - this.parameterAdjustmentFactor)] = AjTypeSystem.getAjType(arrayOfClass[i]);
      i += 1;
    }
    return arrayOfAjType;
  }
  
  public AjType<?> getReturnType()
  {
    return AjTypeSystem.getAjType(this.baseMethod.getReturnType());
  }
  
  public TypeVariable<Method>[] getTypeParameters()
  {
    return this.baseMethod.getTypeParameters();
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(Modifier.toString(getModifiers()));
    localStringBuffer.append(" ");
    localStringBuffer.append(getReturnType().toString());
    localStringBuffer.append(" ");
    localStringBuffer.append(this.targetTypeName);
    localStringBuffer.append(".");
    localStringBuffer.append(getName());
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/internal/lang/reflect/InterTypeMethodDeclarationImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */