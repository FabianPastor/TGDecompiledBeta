package org.aspectj.internal.lang.reflect;

import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.InterTypeDeclaration;

public class InterTypeDeclarationImpl
  implements InterTypeDeclaration
{
  private AjType<?> declaringType;
  private int modifiers;
  private AjType<?> targetType;
  protected String targetTypeName;
  
  public InterTypeDeclarationImpl(AjType<?> paramAjType, String paramString, int paramInt)
  {
    this.declaringType = paramAjType;
    this.targetTypeName = paramString;
    this.modifiers = paramInt;
    try
    {
      this.targetType = ((AjType)StringToType.stringToType(paramString, paramAjType.getJavaClass()));
      return;
    }
    catch (ClassNotFoundException paramAjType) {}
  }
  
  public InterTypeDeclarationImpl(AjType<?> paramAjType1, AjType<?> paramAjType2, int paramInt)
  {
    this.declaringType = paramAjType1;
    this.targetType = paramAjType2;
    this.targetTypeName = paramAjType2.getName();
    this.modifiers = paramInt;
  }
  
  public AjType<?> getDeclaringType()
  {
    return this.declaringType;
  }
  
  public int getModifiers()
  {
    return this.modifiers;
  }
  
  public AjType<?> getTargetType()
    throws ClassNotFoundException
  {
    if (this.targetType == null) {
      throw new ClassNotFoundException(this.targetTypeName);
    }
    return this.targetType;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/internal/lang/reflect/InterTypeDeclarationImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */