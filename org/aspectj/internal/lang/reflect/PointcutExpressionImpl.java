package org.aspectj.internal.lang.reflect;

import org.aspectj.lang.reflect.PointcutExpression;

public class PointcutExpressionImpl
  implements PointcutExpression
{
  private String expression;
  
  public PointcutExpressionImpl(String paramString)
  {
    this.expression = paramString;
  }
  
  public String asString()
  {
    return this.expression;
  }
  
  public String toString()
  {
    return asString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/internal/lang/reflect/PointcutExpressionImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */