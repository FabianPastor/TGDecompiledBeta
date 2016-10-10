package org.aspectj.lang.reflect;

import java.lang.reflect.Type;

public abstract interface DeclareParents
{
  public abstract AjType getDeclaringType();
  
  public abstract Type[] getParentTypes()
    throws ClassNotFoundException;
  
  public abstract TypePattern getTargetTypesPattern();
  
  public abstract boolean isExtends();
  
  public abstract boolean isImplements();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/lang/reflect/DeclareParents.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */