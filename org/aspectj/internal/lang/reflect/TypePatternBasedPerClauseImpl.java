package org.aspectj.internal.lang.reflect;

import org.aspectj.lang.reflect.PerClauseKind;
import org.aspectj.lang.reflect.TypePattern;
import org.aspectj.lang.reflect.TypePatternBasedPerClause;

public class TypePatternBasedPerClauseImpl
  extends PerClauseImpl
  implements TypePatternBasedPerClause
{
  private TypePattern typePattern;
  
  public TypePatternBasedPerClauseImpl(PerClauseKind paramPerClauseKind, String paramString)
  {
    super(paramPerClauseKind);
    this.typePattern = new TypePatternImpl(paramString);
  }
  
  public TypePattern getTypePattern()
  {
    return this.typePattern;
  }
  
  public String toString()
  {
    return "pertypewithin(" + this.typePattern.asString() + ")";
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/internal/lang/reflect/TypePatternBasedPerClauseImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */