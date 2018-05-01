package org.aspectj.lang.reflect;

import java.lang.reflect.Constructor;

public abstract interface InitializerSignature
  extends CodeSignature
{
  public abstract Constructor getInitializer();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/lang/reflect/InitializerSignature.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */