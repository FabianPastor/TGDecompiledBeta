package org.aspectj.lang.reflect;

import java.lang.reflect.Type;

public abstract interface InterTypeConstructorDeclaration
  extends InterTypeDeclaration
{
  public abstract AjType<?>[] getExceptionTypes();
  
  public abstract Type[] getGenericParameterTypes();
  
  public abstract AjType<?>[] getParameterTypes();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/lang/reflect/InterTypeConstructorDeclaration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */