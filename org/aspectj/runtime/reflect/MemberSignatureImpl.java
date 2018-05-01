package org.aspectj.runtime.reflect;

import org.aspectj.lang.reflect.MemberSignature;

abstract class MemberSignatureImpl
  extends SignatureImpl
  implements MemberSignature
{
  MemberSignatureImpl(int paramInt, String paramString, Class paramClass)
  {
    super(paramInt, paramString, paramClass);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/runtime/reflect/MemberSignatureImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */