package org.aspectj.internal.lang.reflect;

import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.AjTypeSystem;
import org.aspectj.lang.reflect.DeclareSoft;
import org.aspectj.lang.reflect.PointcutExpression;

public class DeclareSoftImpl
  implements DeclareSoft
{
  private AjType<?> declaringType;
  private AjType<?> exceptionType;
  private String missingTypeName;
  private PointcutExpression pointcut;
  
  public DeclareSoftImpl(AjType<?> paramAjType, String paramString1, String paramString2)
  {
    this.declaringType = paramAjType;
    this.pointcut = new PointcutExpressionImpl(paramString1);
    try
    {
      this.exceptionType = AjTypeSystem.getAjType(Class.forName(paramString2, false, paramAjType.getJavaClass().getClassLoader()));
      return;
    }
    catch (ClassNotFoundException paramAjType)
    {
      this.missingTypeName = paramString2;
    }
  }
  
  public AjType getDeclaringType()
  {
    return this.declaringType;
  }
  
  public PointcutExpression getPointcutExpression()
  {
    return this.pointcut;
  }
  
  public AjType getSoftenedExceptionType()
    throws ClassNotFoundException
  {
    if (this.missingTypeName != null) {
      throw new ClassNotFoundException(this.missingTypeName);
    }
    return this.exceptionType;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("declare soft : ");
    if (this.missingTypeName != null) {
      localStringBuffer.append(this.exceptionType.getName());
    }
    for (;;)
    {
      localStringBuffer.append(" : ");
      localStringBuffer.append(getPointcutExpression().asString());
      return localStringBuffer.toString();
      localStringBuffer.append(this.missingTypeName);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/internal/lang/reflect/DeclareSoftImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */