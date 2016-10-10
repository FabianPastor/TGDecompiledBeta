package org.aspectj.runtime.reflect;

import org.aspectj.lang.JoinPoint.EnclosingStaticPart;
import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.SourceLocation;
import org.aspectj.runtime.internal.AroundClosure;

class JoinPointImpl
  implements ProceedingJoinPoint
{
  Object _this;
  private AroundClosure arc;
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
  
  public Object[] getArgs()
  {
    if (this.args == null) {
      this.args = new Object[0];
    }
    Object[] arrayOfObject = new Object[this.args.length];
    System.arraycopy(this.args, 0, arrayOfObject, 0, this.args.length);
    return arrayOfObject;
  }
  
  public String getKind()
  {
    return this.staticPart.getKind();
  }
  
  public Signature getSignature()
  {
    return this.staticPart.getSignature();
  }
  
  public SourceLocation getSourceLocation()
  {
    return this.staticPart.getSourceLocation();
  }
  
  public JoinPoint.StaticPart getStaticPart()
  {
    return this.staticPart;
  }
  
  public Object getTarget()
  {
    return this.target;
  }
  
  public Object getThis()
  {
    return this._this;
  }
  
  public Object proceed()
    throws Throwable
  {
    if (this.arc == null) {
      return null;
    }
    return this.arc.run(this.arc.getState());
  }
  
  public Object proceed(Object[] paramArrayOfObject)
    throws Throwable
  {
    int i5 = 1;
    int i6 = 1;
    if (this.arc == null) {
      return null;
    }
    int i = this.arc.getFlags();
    int i1;
    label40:
    int m;
    label51:
    int i2;
    label62:
    int i3;
    label72:
    int i4;
    label81:
    Object[] arrayOfObject;
    int k;
    label100:
    int n;
    if ((0x100000 & i) != 0)
    {
      if ((0x10000 & i) == 0) {
        break label218;
      }
      i1 = 1;
      if ((i & 0x1000) == 0) {
        break label224;
      }
      m = 1;
      if ((i & 0x100) == 0) {
        break label230;
      }
      i2 = 1;
      if ((i & 0x10) == 0) {
        break label236;
      }
      i3 = 1;
      if ((i & 0x1) == 0) {
        break label242;
      }
      i4 = 1;
      arrayOfObject = this.arc.getState();
      i = 0;
      if (m == 0) {
        break label248;
      }
      k = 1;
      if ((i3 == 0) || (i1 != 0)) {
        break label254;
      }
      n = 1;
      label113:
      j = i;
      if (m != 0)
      {
        j = i;
        if (i2 != 0)
        {
          j = 1;
          arrayOfObject[0] = paramArrayOfObject[0];
        }
      }
      i = j;
      if (i3 != 0)
      {
        i = j;
        if (i4 != 0)
        {
          if (i1 == 0) {
            break label270;
          }
          if (i2 == 0) {
            break label260;
          }
          i = 1;
          label162:
          j = i + 1;
          if (i2 == 0) {
            break label265;
          }
        }
      }
    }
    label218:
    label224:
    label230:
    label236:
    label242:
    label248:
    label254:
    label260:
    label265:
    for (i = i6;; i = 0)
    {
      arrayOfObject[0] = paramArrayOfObject[i];
      i = j;
      j = i;
      while (j < paramArrayOfObject.length)
      {
        arrayOfObject[(j - i + (0 + k + n))] = paramArrayOfObject[j];
        j += 1;
      }
      break;
      i1 = 0;
      break label40;
      m = 0;
      break label51;
      i2 = 0;
      break label62;
      i3 = 0;
      break label72;
      i4 = 0;
      break label81;
      k = 0;
      break label100;
      n = 0;
      break label113;
      i = 0;
      break label162;
    }
    label270:
    if (m != 0)
    {
      i = 1;
      label277:
      i1 = i + 1;
      if (m == 0) {
        break label315;
      }
      i = 1;
      label289:
      if (m == 0) {
        break label320;
      }
    }
    label315:
    label320:
    for (int j = i5;; j = 0)
    {
      arrayOfObject[i] = paramArrayOfObject[j];
      i = i1;
      break;
      i = 0;
      break label277;
      i = 0;
      break label289;
    }
    return this.arc.run(arrayOfObject);
  }
  
  public void set$AroundClosure(AroundClosure paramAroundClosure)
  {
    this.arc = paramAroundClosure;
  }
  
  public final String toLongString()
  {
    return this.staticPart.toLongString();
  }
  
  public final String toShortString()
  {
    return this.staticPart.toShortString();
  }
  
  public final String toString()
  {
    return this.staticPart.toString();
  }
  
  static class EnclosingStaticPartImpl
    extends JoinPointImpl.StaticPartImpl
    implements JoinPoint.EnclosingStaticPart
  {
    public EnclosingStaticPartImpl(int paramInt, String paramString, Signature paramSignature, SourceLocation paramSourceLocation)
    {
      super(paramString, paramSignature, paramSourceLocation);
    }
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
    
    public int getId()
    {
      return this.id;
    }
    
    public String getKind()
    {
      return this.kind;
    }
    
    public Signature getSignature()
    {
      return this.signature;
    }
    
    public SourceLocation getSourceLocation()
    {
      return this.sourceLocation;
    }
    
    public final String toLongString()
    {
      return toString(StringMaker.longStringMaker);
    }
    
    public final String toShortString()
    {
      return toString(StringMaker.shortStringMaker);
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