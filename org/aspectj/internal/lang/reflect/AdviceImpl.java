package org.aspectj.internal.lang.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import org.aspectj.lang.annotation.AdviceName;
import org.aspectj.lang.reflect.Advice;
import org.aspectj.lang.reflect.AdviceKind;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.AjTypeSystem;
import org.aspectj.lang.reflect.PointcutExpression;

public class AdviceImpl
  implements Advice
{
  private static final String AJC_INTERNAL = "org.aspectj.runtime.internal";
  private final Method adviceMethod;
  private AjType[] exceptionTypes;
  private Type[] genericParameterTypes;
  private boolean hasExtraParam = false;
  private final AdviceKind kind;
  private AjType[] parameterTypes;
  private PointcutExpression pointcutExpression;
  
  protected AdviceImpl(Method paramMethod, String paramString, AdviceKind paramAdviceKind)
  {
    this.kind = paramAdviceKind;
    this.adviceMethod = paramMethod;
    this.pointcutExpression = new PointcutExpressionImpl(paramString);
  }
  
  protected AdviceImpl(Method paramMethod, String paramString1, AdviceKind paramAdviceKind, String paramString2)
  {
    this(paramMethod, paramString1, paramAdviceKind);
  }
  
  public AjType getDeclaringType()
  {
    return AjTypeSystem.getAjType(this.adviceMethod.getDeclaringClass());
  }
  
  public AjType<?>[] getExceptionTypes()
  {
    if (this.exceptionTypes == null)
    {
      Class[] arrayOfClass = this.adviceMethod.getExceptionTypes();
      this.exceptionTypes = new AjType[arrayOfClass.length];
      int i = 0;
      while (i < arrayOfClass.length)
      {
        this.exceptionTypes[i] = AjTypeSystem.getAjType(arrayOfClass[i]);
        i += 1;
      }
    }
    return this.exceptionTypes;
  }
  
  public Type[] getGenericParameterTypes()
  {
    if (this.genericParameterTypes == null)
    {
      Type[] arrayOfType = this.adviceMethod.getGenericParameterTypes();
      int j = 0;
      int m = arrayOfType.length;
      int i = 0;
      while (i < m)
      {
        Type localType = arrayOfType[i];
        int k = j;
        if ((localType instanceof Class))
        {
          k = j;
          if (((Class)localType).getPackage().getName().equals("org.aspectj.runtime.internal")) {
            k = j + 1;
          }
        }
        i += 1;
        j = k;
      }
      this.genericParameterTypes = new Type[arrayOfType.length - j];
      i = 0;
      if (i < this.genericParameterTypes.length)
      {
        if ((arrayOfType[i] instanceof Class)) {
          this.genericParameterTypes[i] = AjTypeSystem.getAjType((Class)arrayOfType[i]);
        }
        for (;;)
        {
          i += 1;
          break;
          this.genericParameterTypes[i] = arrayOfType[i];
        }
      }
    }
    return this.genericParameterTypes;
  }
  
  public AdviceKind getKind()
  {
    return this.kind;
  }
  
  public String getName()
  {
    Object localObject2 = this.adviceMethod.getName();
    Object localObject1 = localObject2;
    if (((String)localObject2).startsWith("ajc$"))
    {
      localObject1 = "";
      localObject2 = (AdviceName)this.adviceMethod.getAnnotation(AdviceName.class);
      if (localObject2 != null) {
        localObject1 = ((AdviceName)localObject2).value();
      }
    }
    return (String)localObject1;
  }
  
  public AjType<?>[] getParameterTypes()
  {
    if (this.parameterTypes == null)
    {
      Class[] arrayOfClass = this.adviceMethod.getParameterTypes();
      int j = 0;
      int m = arrayOfClass.length;
      int i = 0;
      while (i < m)
      {
        int k = j;
        if (arrayOfClass[i].getPackage().getName().equals("org.aspectj.runtime.internal")) {
          k = j + 1;
        }
        i += 1;
        j = k;
      }
      this.parameterTypes = new AjType[arrayOfClass.length - j];
      i = 0;
      while (i < this.parameterTypes.length)
      {
        this.parameterTypes[i] = AjTypeSystem.getAjType(arrayOfClass[i]);
        i += 1;
      }
    }
    return this.parameterTypes;
  }
  
  public PointcutExpression getPointcutExpression()
  {
    return this.pointcutExpression;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    if (getName().length() > 0)
    {
      localStringBuffer.append("@AdviceName(\"");
      localStringBuffer.append(getName());
      localStringBuffer.append("\") ");
    }
    if (getKind() == AdviceKind.AROUND)
    {
      localStringBuffer.append(this.adviceMethod.getGenericReturnType().toString());
      localStringBuffer.append(" ");
    }
    switch (getKind())
    {
    }
    AjType[] arrayOfAjType;
    int i;
    for (;;)
    {
      arrayOfAjType = getParameterTypes();
      int j = arrayOfAjType.length;
      i = j;
      if (this.hasExtraParam) {
        i = j - 1;
      }
      j = 0;
      while (j < i)
      {
        localStringBuffer.append(arrayOfAjType[j].getName());
        if (j + 1 < i) {
          localStringBuffer.append(",");
        }
        j += 1;
      }
      localStringBuffer.append("after(");
      continue;
      localStringBuffer.append("after(");
      continue;
      localStringBuffer.append("after(");
      continue;
      localStringBuffer.append("around(");
      continue;
      localStringBuffer.append("before(");
    }
    localStringBuffer.append(") ");
    switch (getKind())
    {
    }
    for (;;)
    {
      arrayOfAjType = getExceptionTypes();
      if (arrayOfAjType.length <= 0) {
        break label439;
      }
      localStringBuffer.append("throws ");
      i = 0;
      while (i < arrayOfAjType.length)
      {
        localStringBuffer.append(arrayOfAjType[i].getName());
        if (i + 1 < arrayOfAjType.length) {
          localStringBuffer.append(",");
        }
        i += 1;
      }
      localStringBuffer.append("returning");
      if (this.hasExtraParam)
      {
        localStringBuffer.append("(");
        localStringBuffer.append(arrayOfAjType[(i - 1)].getName());
        localStringBuffer.append(") ");
      }
      localStringBuffer.append("throwing");
      if (this.hasExtraParam)
      {
        localStringBuffer.append("(");
        localStringBuffer.append(arrayOfAjType[(i - 1)].getName());
        localStringBuffer.append(") ");
      }
    }
    localStringBuffer.append(" ");
    label439:
    localStringBuffer.append(": ");
    localStringBuffer.append(getPointcutExpression().asString());
    return localStringBuffer.toString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/internal/lang/reflect/AdviceImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */