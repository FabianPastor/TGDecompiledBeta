package org.aspectj.internal.lang.reflect;

import java.util.StringTokenizer;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.DeclarePrecedence;
import org.aspectj.lang.reflect.TypePattern;

public class DeclarePrecedenceImpl
  implements DeclarePrecedence
{
  private AjType<?> declaringType;
  private TypePattern[] precedenceList;
  private String precedenceString;
  
  public DeclarePrecedenceImpl(String paramString, AjType paramAjType)
  {
    this.declaringType = paramAjType;
    this.precedenceString = paramString;
    paramAjType = paramString;
    if (paramString.startsWith("(")) {
      paramAjType = paramString.substring(1, paramString.length() - 1);
    }
    paramString = new StringTokenizer(paramAjType, ",");
    this.precedenceList = new TypePattern[paramString.countTokens()];
    int i = 0;
    while (i < this.precedenceList.length)
    {
      this.precedenceList[i] = new TypePatternImpl(paramString.nextToken().trim());
      i += 1;
    }
  }
  
  public AjType getDeclaringType()
  {
    return this.declaringType;
  }
  
  public TypePattern[] getPrecedenceOrder()
  {
    return this.precedenceList;
  }
  
  public String toString()
  {
    return "declare precedence : " + this.precedenceString;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/internal/lang/reflect/DeclarePrecedenceImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */