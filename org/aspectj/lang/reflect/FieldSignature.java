package org.aspectj.lang.reflect;

import java.lang.reflect.Field;

public abstract interface FieldSignature
  extends MemberSignature
{
  public abstract Field getField();
  
  public abstract Class getFieldType();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/lang/reflect/FieldSignature.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */