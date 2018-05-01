package org.aspectj.runtime.reflect;

import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.SourceLocation;

class JoinPointImpl
  implements ProceedingJoinPoint
{
  Object _this;
  Object[] args;
  JoinPoint.StaticPart staticPart;
  Object target;
  
  public JoinPointImpl(JoinPoint.StaticPart paramStaticPart, Object paramObject1, Object paramObject2, Object[] paramArrayOfObject)
  {
    this.staticPart = paramStaticPart;
    this._this = paramObject1;
    this.target = paramObject2;
    this.args = paramArrayOfObject;
  }
  
  public Object getTarget()
  {
    return this.target;
  }
  
  public final String toString()
  {
    return this.staticPart.toString();
  }
  
  static class StaticPartImpl
    implements JoinPoint.StaticPart
  {
    private int id;
    String kind;
    Signature signature;
    SourceLocation sourceLocation;
    
    public StaticPartImpl(int paramInt, String paramString, Signature paramSignature, SourceLocation paramSourceLocation)
    {
      this.kind = paramString;
      this.signature = paramSignature;
      this.sourceLocation = paramSourceLocation;
      this.id = paramInt;
    }
    
    public String getKind()
    {
      return this.kind;
    }
    
    public Signature getSignature()
    {
      return this.signature;
    }
    
    public final String toString()
    {
      return toString(StringMaker.middleStringMaker);
    }
    
    String toString(StringMaker paramStringMaker)
    {
      StringBuffer localStringBuffer = new StringBuffer();
      localStringBuffer.append(paramStringMaker.makeKindName(getKind()));
      localStringBuffer.append("(");
      localStringBuffer.append(((SignatureImpl)getSignature()).toString(paramStringMaker));
      localStringBuffer.append(")");
      return localStringBuffer.toString();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/runtime/reflect/JoinPointImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */