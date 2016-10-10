package org.aspectj.runtime.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.StringTokenizer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.JoinPoint.EnclosingStaticPart;
import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.AdviceSignature;
import org.aspectj.lang.reflect.CatchClauseSignature;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.FieldSignature;
import org.aspectj.lang.reflect.InitializerSignature;
import org.aspectj.lang.reflect.LockSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.lang.reflect.SourceLocation;
import org.aspectj.lang.reflect.UnlockSignature;

public final class Factory
{
  private static Object[] NO_ARGS = new Object[0];
  static Class class$java$lang$ClassNotFoundException;
  static Hashtable prims = new Hashtable();
  int count;
  String filename;
  Class lexicalClass;
  ClassLoader lookupClassLoader;
  
  static
  {
    prims.put("void", Void.TYPE);
    prims.put("boolean", Boolean.TYPE);
    prims.put("byte", Byte.TYPE);
    prims.put("char", Character.TYPE);
    prims.put("short", Short.TYPE);
    prims.put("int", Integer.TYPE);
    prims.put("long", Long.TYPE);
    prims.put("float", Float.TYPE);
    prims.put("double", Double.TYPE);
  }
  
  public Factory(String paramString, Class paramClass)
  {
    this.filename = paramString;
    this.lexicalClass = paramClass;
    this.count = 0;
    this.lookupClassLoader = paramClass.getClassLoader();
  }
  
  static Class class$(String paramString)
  {
    try
    {
      paramString = Class.forName(paramString);
      return paramString;
    }
    catch (ClassNotFoundException paramString)
    {
      throw new NoClassDefFoundError(paramString.getMessage());
    }
  }
  
  static Class makeClass(String paramString, ClassLoader paramClassLoader)
  {
    Object localObject;
    if (paramString.equals("*")) {
      localObject = null;
    }
    Class localClass;
    do
    {
      return (Class)localObject;
      localClass = (Class)prims.get(paramString);
      localObject = localClass;
    } while (localClass != null);
    if (paramClassLoader == null) {}
    try
    {
      return Class.forName(paramString);
    }
    catch (ClassNotFoundException paramString)
    {
      if (class$java$lang$ClassNotFoundException != null) {
        break label67;
      }
    }
    paramString = Class.forName(paramString, false, paramClassLoader);
    return paramString;
    paramString = class$("java.lang.ClassNotFoundException");
    class$java$lang$ClassNotFoundException = paramString;
    for (;;)
    {
      return paramString;
      label67:
      paramString = class$java$lang$ClassNotFoundException;
    }
  }
  
  public static JoinPoint.StaticPart makeEncSJP(Member paramMember)
  {
    if ((paramMember instanceof Method))
    {
      paramMember = (Method)paramMember;
      paramMember = new MethodSignatureImpl(paramMember.getModifiers(), paramMember.getName(), paramMember.getDeclaringClass(), paramMember.getParameterTypes(), new String[paramMember.getParameterTypes().length], paramMember.getExceptionTypes(), paramMember.getReturnType());
    }
    for (String str = "method-execution";; str = "constructor-execution")
    {
      return new JoinPointImpl.EnclosingStaticPartImpl(-1, str, paramMember, null);
      if (!(paramMember instanceof Constructor)) {
        break;
      }
      paramMember = (Constructor)paramMember;
      paramMember = new ConstructorSignatureImpl(paramMember.getModifiers(), paramMember.getDeclaringClass(), paramMember.getParameterTypes(), new String[paramMember.getParameterTypes().length], paramMember.getExceptionTypes());
    }
    throw new IllegalArgumentException("member must be either a method or constructor");
  }
  
  public static JoinPoint makeJP(JoinPoint.StaticPart paramStaticPart, Object paramObject1, Object paramObject2)
  {
    return new JoinPointImpl(paramStaticPart, paramObject1, paramObject2, NO_ARGS);
  }
  
  public static JoinPoint makeJP(JoinPoint.StaticPart paramStaticPart, Object paramObject1, Object paramObject2, Object paramObject3)
  {
    return new JoinPointImpl(paramStaticPart, paramObject1, paramObject2, new Object[] { paramObject3 });
  }
  
  public static JoinPoint makeJP(JoinPoint.StaticPart paramStaticPart, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4)
  {
    return new JoinPointImpl(paramStaticPart, paramObject1, paramObject2, new Object[] { paramObject3, paramObject4 });
  }
  
  public static JoinPoint makeJP(JoinPoint.StaticPart paramStaticPart, Object paramObject1, Object paramObject2, Object[] paramArrayOfObject)
  {
    return new JoinPointImpl(paramStaticPart, paramObject1, paramObject2, paramArrayOfObject);
  }
  
  public AdviceSignature makeAdviceSig(int paramInt, String paramString, Class paramClass1, Class[] paramArrayOfClass1, String[] paramArrayOfString, Class[] paramArrayOfClass2, Class paramClass2)
  {
    paramString = new AdviceSignatureImpl(paramInt, paramString, paramClass1, paramArrayOfClass1, paramArrayOfString, paramArrayOfClass2, paramClass2);
    paramString.setLookupClassLoader(this.lookupClassLoader);
    return paramString;
  }
  
  public AdviceSignature makeAdviceSig(String paramString)
  {
    paramString = new AdviceSignatureImpl(paramString);
    paramString.setLookupClassLoader(this.lookupClassLoader);
    return paramString;
  }
  
  public AdviceSignature makeAdviceSig(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7)
  {
    int j = Integer.parseInt(paramString1, 16);
    paramString1 = makeClass(paramString3, this.lookupClassLoader);
    paramString4 = new StringTokenizer(paramString4, ":");
    int k = paramString4.countTokens();
    paramString3 = new Class[k];
    int i = 0;
    while (i < k)
    {
      paramString3[i] = makeClass(paramString4.nextToken(), this.lookupClassLoader);
      i += 1;
    }
    paramString5 = new StringTokenizer(paramString5, ":");
    k = paramString5.countTokens();
    paramString4 = new String[k];
    i = 0;
    while (i < k)
    {
      paramString4[i] = paramString5.nextToken();
      i += 1;
    }
    paramString5 = new StringTokenizer(paramString6, ":");
    k = paramString5.countTokens();
    paramString6 = new Class[k];
    i = 0;
    while (i < k)
    {
      paramString6[i] = makeClass(paramString5.nextToken(), this.lookupClassLoader);
      i += 1;
    }
    paramString1 = new AdviceSignatureImpl(j, paramString2, paramString1, paramString3, paramString4, paramString6, makeClass(paramString7, this.lookupClassLoader));
    paramString1.setLookupClassLoader(this.lookupClassLoader);
    return paramString1;
  }
  
  public CatchClauseSignature makeCatchClauseSig(Class paramClass1, Class paramClass2, String paramString)
  {
    paramClass1 = new CatchClauseSignatureImpl(paramClass1, paramClass2, paramString);
    paramClass1.setLookupClassLoader(this.lookupClassLoader);
    return paramClass1;
  }
  
  public CatchClauseSignature makeCatchClauseSig(String paramString)
  {
    paramString = new CatchClauseSignatureImpl(paramString);
    paramString.setLookupClassLoader(this.lookupClassLoader);
    return paramString;
  }
  
  public CatchClauseSignature makeCatchClauseSig(String paramString1, String paramString2, String paramString3)
  {
    paramString1 = new CatchClauseSignatureImpl(makeClass(paramString1, this.lookupClassLoader), makeClass(new StringTokenizer(paramString2, ":").nextToken(), this.lookupClassLoader), new StringTokenizer(paramString3, ":").nextToken());
    paramString1.setLookupClassLoader(this.lookupClassLoader);
    return paramString1;
  }
  
  public ConstructorSignature makeConstructorSig(int paramInt, Class paramClass, Class[] paramArrayOfClass1, String[] paramArrayOfString, Class[] paramArrayOfClass2)
  {
    paramClass = new ConstructorSignatureImpl(paramInt, paramClass, paramArrayOfClass1, paramArrayOfString, paramArrayOfClass2);
    paramClass.setLookupClassLoader(this.lookupClassLoader);
    return paramClass;
  }
  
  public ConstructorSignature makeConstructorSig(String paramString)
  {
    paramString = new ConstructorSignatureImpl(paramString);
    paramString.setLookupClassLoader(this.lookupClassLoader);
    return paramString;
  }
  
  public ConstructorSignature makeConstructorSig(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    int j = Integer.parseInt(paramString1, 16);
    paramString1 = makeClass(paramString2, this.lookupClassLoader);
    paramString3 = new StringTokenizer(paramString3, ":");
    int k = paramString3.countTokens();
    paramString2 = new Class[k];
    int i = 0;
    while (i < k)
    {
      paramString2[i] = makeClass(paramString3.nextToken(), this.lookupClassLoader);
      i += 1;
    }
    paramString4 = new StringTokenizer(paramString4, ":");
    k = paramString4.countTokens();
    paramString3 = new String[k];
    i = 0;
    while (i < k)
    {
      paramString3[i] = paramString4.nextToken();
      i += 1;
    }
    paramString4 = new StringTokenizer(paramString5, ":");
    k = paramString4.countTokens();
    paramString5 = new Class[k];
    i = 0;
    while (i < k)
    {
      paramString5[i] = makeClass(paramString4.nextToken(), this.lookupClassLoader);
      i += 1;
    }
    paramString1 = new ConstructorSignatureImpl(j, paramString1, paramString2, paramString3, paramString5);
    paramString1.setLookupClassLoader(this.lookupClassLoader);
    return paramString1;
  }
  
  public JoinPoint.EnclosingStaticPart makeESJP(String paramString, Signature paramSignature, int paramInt)
  {
    int i = this.count;
    this.count = (i + 1);
    return new JoinPointImpl.EnclosingStaticPartImpl(i, paramString, paramSignature, makeSourceLoc(paramInt, -1));
  }
  
  public JoinPoint.EnclosingStaticPart makeESJP(String paramString, Signature paramSignature, int paramInt1, int paramInt2)
  {
    int i = this.count;
    this.count = (i + 1);
    return new JoinPointImpl.EnclosingStaticPartImpl(i, paramString, paramSignature, makeSourceLoc(paramInt1, paramInt2));
  }
  
  public JoinPoint.EnclosingStaticPart makeESJP(String paramString, Signature paramSignature, SourceLocation paramSourceLocation)
  {
    int i = this.count;
    this.count = (i + 1);
    return new JoinPointImpl.EnclosingStaticPartImpl(i, paramString, paramSignature, paramSourceLocation);
  }
  
  public FieldSignature makeFieldSig(int paramInt, String paramString, Class paramClass1, Class paramClass2)
  {
    paramString = new FieldSignatureImpl(paramInt, paramString, paramClass1, paramClass2);
    paramString.setLookupClassLoader(this.lookupClassLoader);
    return paramString;
  }
  
  public FieldSignature makeFieldSig(String paramString)
  {
    paramString = new FieldSignatureImpl(paramString);
    paramString.setLookupClassLoader(this.lookupClassLoader);
    return paramString;
  }
  
  public FieldSignature makeFieldSig(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    paramString1 = new FieldSignatureImpl(Integer.parseInt(paramString1, 16), paramString2, makeClass(paramString3, this.lookupClassLoader), makeClass(paramString4, this.lookupClassLoader));
    paramString1.setLookupClassLoader(this.lookupClassLoader);
    return paramString1;
  }
  
  public InitializerSignature makeInitializerSig(int paramInt, Class paramClass)
  {
    paramClass = new InitializerSignatureImpl(paramInt, paramClass);
    paramClass.setLookupClassLoader(this.lookupClassLoader);
    return paramClass;
  }
  
  public InitializerSignature makeInitializerSig(String paramString)
  {
    paramString = new InitializerSignatureImpl(paramString);
    paramString.setLookupClassLoader(this.lookupClassLoader);
    return paramString;
  }
  
  public InitializerSignature makeInitializerSig(String paramString1, String paramString2)
  {
    paramString1 = new InitializerSignatureImpl(Integer.parseInt(paramString1, 16), makeClass(paramString2, this.lookupClassLoader));
    paramString1.setLookupClassLoader(this.lookupClassLoader);
    return paramString1;
  }
  
  public LockSignature makeLockSig()
  {
    LockSignatureImpl localLockSignatureImpl = new LockSignatureImpl(makeClass("Ljava/lang/Object;", this.lookupClassLoader));
    localLockSignatureImpl.setLookupClassLoader(this.lookupClassLoader);
    return localLockSignatureImpl;
  }
  
  public LockSignature makeLockSig(Class paramClass)
  {
    paramClass = new LockSignatureImpl(paramClass);
    paramClass.setLookupClassLoader(this.lookupClassLoader);
    return paramClass;
  }
  
  public LockSignature makeLockSig(String paramString)
  {
    paramString = new LockSignatureImpl(paramString);
    paramString.setLookupClassLoader(this.lookupClassLoader);
    return paramString;
  }
  
  public MethodSignature makeMethodSig(int paramInt, String paramString, Class paramClass1, Class[] paramArrayOfClass1, String[] paramArrayOfString, Class[] paramArrayOfClass2, Class paramClass2)
  {
    paramString = new MethodSignatureImpl(paramInt, paramString, paramClass1, paramArrayOfClass1, paramArrayOfString, paramArrayOfClass2, paramClass2);
    paramString.setLookupClassLoader(this.lookupClassLoader);
    return paramString;
  }
  
  public MethodSignature makeMethodSig(String paramString)
  {
    paramString = new MethodSignatureImpl(paramString);
    paramString.setLookupClassLoader(this.lookupClassLoader);
    return paramString;
  }
  
  public MethodSignature makeMethodSig(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7)
  {
    int j = Integer.parseInt(paramString1, 16);
    paramString1 = makeClass(paramString3, this.lookupClassLoader);
    paramString4 = new StringTokenizer(paramString4, ":");
    int k = paramString4.countTokens();
    paramString3 = new Class[k];
    int i = 0;
    while (i < k)
    {
      paramString3[i] = makeClass(paramString4.nextToken(), this.lookupClassLoader);
      i += 1;
    }
    paramString5 = new StringTokenizer(paramString5, ":");
    k = paramString5.countTokens();
    paramString4 = new String[k];
    i = 0;
    while (i < k)
    {
      paramString4[i] = paramString5.nextToken();
      i += 1;
    }
    paramString5 = new StringTokenizer(paramString6, ":");
    k = paramString5.countTokens();
    paramString6 = new Class[k];
    i = 0;
    while (i < k)
    {
      paramString6[i] = makeClass(paramString5.nextToken(), this.lookupClassLoader);
      i += 1;
    }
    return new MethodSignatureImpl(j, paramString2, paramString1, paramString3, paramString4, paramString6, makeClass(paramString7, this.lookupClassLoader));
  }
  
  public JoinPoint.StaticPart makeSJP(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, int paramInt)
  {
    paramString2 = makeMethodSig(paramString2, paramString3, paramString4, paramString5, paramString6, "", paramString7);
    int i = this.count;
    this.count = (i + 1);
    return new JoinPointImpl.StaticPartImpl(i, paramString1, paramString2, makeSourceLoc(paramInt, -1));
  }
  
  public JoinPoint.StaticPart makeSJP(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, String paramString8, int paramInt)
  {
    paramString2 = makeMethodSig(paramString2, paramString3, paramString4, paramString5, paramString6, paramString7, paramString8);
    int i = this.count;
    this.count = (i + 1);
    return new JoinPointImpl.StaticPartImpl(i, paramString1, paramString2, makeSourceLoc(paramInt, -1));
  }
  
  public JoinPoint.StaticPart makeSJP(String paramString, Signature paramSignature, int paramInt)
  {
    int i = this.count;
    this.count = (i + 1);
    return new JoinPointImpl.StaticPartImpl(i, paramString, paramSignature, makeSourceLoc(paramInt, -1));
  }
  
  public JoinPoint.StaticPart makeSJP(String paramString, Signature paramSignature, int paramInt1, int paramInt2)
  {
    int i = this.count;
    this.count = (i + 1);
    return new JoinPointImpl.StaticPartImpl(i, paramString, paramSignature, makeSourceLoc(paramInt1, paramInt2));
  }
  
  public JoinPoint.StaticPart makeSJP(String paramString, Signature paramSignature, SourceLocation paramSourceLocation)
  {
    int i = this.count;
    this.count = (i + 1);
    return new JoinPointImpl.StaticPartImpl(i, paramString, paramSignature, paramSourceLocation);
  }
  
  public SourceLocation makeSourceLoc(int paramInt1, int paramInt2)
  {
    return new SourceLocationImpl(this.lexicalClass, this.filename, paramInt1);
  }
  
  public UnlockSignature makeUnlockSig()
  {
    UnlockSignatureImpl localUnlockSignatureImpl = new UnlockSignatureImpl(makeClass("Ljava/lang/Object;", this.lookupClassLoader));
    localUnlockSignatureImpl.setLookupClassLoader(this.lookupClassLoader);
    return localUnlockSignatureImpl;
  }
  
  public UnlockSignature makeUnlockSig(Class paramClass)
  {
    paramClass = new UnlockSignatureImpl(paramClass);
    paramClass.setLookupClassLoader(this.lookupClassLoader);
    return paramClass;
  }
  
  public UnlockSignature makeUnlockSig(String paramString)
  {
    paramString = new UnlockSignatureImpl(paramString);
    paramString.setLookupClassLoader(this.lookupClassLoader);
    return paramString;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/runtime/reflect/Factory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */