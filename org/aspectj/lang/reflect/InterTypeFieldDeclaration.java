package org.aspectj.lang.reflect;

import java.lang.reflect.Type;

public abstract interface InterTypeFieldDeclaration
  extends InterTypeDeclaration
{
  public abstract Type getGenericType();
  
  public abstract String getName();
  
  public abstract AjType<?> getType();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/lang/reflect/InterTypeFieldDeclaration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */