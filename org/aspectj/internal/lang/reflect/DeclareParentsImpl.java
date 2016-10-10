package org.aspectj.internal.lang.reflect;

import java.lang.reflect.Type;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.DeclareParents;
import org.aspectj.lang.reflect.TypePattern;

public class DeclareParentsImpl
  implements DeclareParents
{
  private AjType<?> declaringType;
  private String firstMissingTypeName;
  private boolean isExtends;
  private Type[] parents;
  private boolean parentsError = false;
  private String parentsString;
  private TypePattern targetTypesPattern;
  
  public DeclareParentsImpl(String paramString1, String paramString2, boolean paramBoolean, AjType<?> paramAjType)
  {
    this.targetTypesPattern = new TypePatternImpl(paramString1);
    this.isExtends = paramBoolean;
    this.declaringType = paramAjType;
    this.parentsString = paramString2;
    try
    {
      this.parents = StringToType.commaSeparatedListToTypeArray(paramString2, paramAjType.getJavaClass());
      return;
    }
    catch (ClassNotFoundException paramString1)
    {
      this.parentsError = true;
      this.firstMissingTypeName = paramString1.getMessage();
    }
  }
  
  public AjType getDeclaringType()
  {
    return this.declaringType;
  }
  
  public Type[] getParentTypes()
    throws ClassNotFoundException
  {
    if (this.parentsError) {
      throw new ClassNotFoundException(this.firstMissingTypeName);
    }
    return this.parents;
  }
  
  public TypePattern getTargetTypesPattern()
  {
    return this.targetTypesPattern;
  }
  
  public boolean isExtends()
  {
    return this.isExtends;
  }
  
  public boolean isImplements()
  {
    return !this.isExtends;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("declare parents : ");
    localStringBuffer.append(getTargetTypesPattern().asString());
    if (isExtends()) {}
    for (String str = " extends ";; str = " implements ")
    {
      localStringBuffer.append(str);
      localStringBuffer.append(this.parentsString);
      return localStringBuffer.toString();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/internal/lang/reflect/DeclareParentsImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */