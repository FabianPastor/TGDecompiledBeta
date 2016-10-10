package org.aspectj.runtime.reflect;

import java.lang.reflect.Field;
import org.aspectj.lang.reflect.FieldSignature;

public class FieldSignatureImpl
  extends MemberSignatureImpl
  implements FieldSignature
{
  private Field field;
  Class fieldType;
  
  FieldSignatureImpl(int paramInt, String paramString, Class paramClass1, Class paramClass2)
  {
    super(paramInt, paramString, paramClass1);
    this.fieldType = paramClass2;
  }
  
  FieldSignatureImpl(String paramString)
  {
    super(paramString);
  }
  
  protected String createToString(StringMaker paramStringMaker)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(paramStringMaker.makeModifiersString(getModifiers()));
    if (paramStringMaker.includeArgs) {
      localStringBuffer.append(paramStringMaker.makeTypeName(getFieldType()));
    }
    if (paramStringMaker.includeArgs) {
      localStringBuffer.append(" ");
    }
    localStringBuffer.append(paramStringMaker.makePrimaryTypeName(getDeclaringType(), getDeclaringTypeName()));
    localStringBuffer.append(".");
    localStringBuffer.append(getName());
    return localStringBuffer.toString();
  }
  
  public Field getField()
  {
    if (this.field == null) {}
    try
    {
      this.field = getDeclaringType().getDeclaredField(getName());
      return this.field;
    }
    catch (Exception localException)
    {
      for (;;) {}
    }
  }
  
  public Class getFieldType()
  {
    if (this.fieldType == null) {
      this.fieldType = extractType(3);
    }
    return this.fieldType;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/runtime/reflect/FieldSignatureImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */