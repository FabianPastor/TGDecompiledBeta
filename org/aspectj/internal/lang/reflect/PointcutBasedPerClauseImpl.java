package org.aspectj.internal.lang.reflect;

import org.aspectj.lang.reflect.PerClauseKind;
import org.aspectj.lang.reflect.PointcutBasedPerClause;
import org.aspectj.lang.reflect.PointcutExpression;

public class PointcutBasedPerClauseImpl
  extends PerClauseImpl
  implements PointcutBasedPerClause
{
  private final PointcutExpression pointcutExpression;
  
  public PointcutBasedPerClauseImpl(PerClauseKind paramPerClauseKind, String paramString)
  {
    super(paramPerClauseKind);
    this.pointcutExpression = new PointcutExpressionImpl(paramString);
  }
  
  public PointcutExpression getPointcutExpression()
  {
    return this.pointcutExpression;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    switch (getKind())
    {
    }
    for (;;)
    {
      localStringBuffer.append(this.pointcutExpression.asString());
      localStringBuffer.append(")");
      return localStringBuffer.toString();
      localStringBuffer.append("percflow(");
      continue;
      localStringBuffer.append("percflowbelow(");
      continue;
      localStringBuffer.append("pertarget(");
      continue;
      localStringBuffer.append("perthis(");
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/internal/lang/reflect/PointcutBasedPerClauseImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */