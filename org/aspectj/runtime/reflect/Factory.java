package org.aspectj.runtime.reflect;

import java.util.Hashtable;
import java.util.StringTokenizer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.lang.reflect.SourceLocation;

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
    for (;;)
    {
      return (Class)localObject;
      Class localClass = (Class)prims.get(paramString);
      localObject = localClass;
      if (localClass == null) {
        if (paramClassLoader != null) {}
      }
      try
      {
        localObject = Class.forName(paramString);
      }
      catch (ClassNotFoundException paramString)
      {
        if (class$java$lang$ClassNotFoundException != null) {
          break;
        }
      }
      localObject = Class.forName(paramString, false, paramClassLoader);
    }
    paramString = class$("java.lang.ClassNotFoundException");
    class$java$lang$ClassNotFoundException = paramString;
    for (;;)
    {
      localObject = paramString;
      break;
      paramString = class$java$lang$ClassNotFoundException;
    }
  }
  
  public static JoinPoint makeJP(JoinPoint.StaticPart paramStaticPart, Object paramObject1, Object paramObject2)
  {
    return new JoinPointImpl(paramStaticPart, paramObject1, paramObject2, NO_ARGS);
  }
  
  public static JoinPoint makeJP(JoinPoint.StaticPart paramStaticPart, Object paramObject1, Object paramObject2, Object paramObject3)
  {
    return new JoinPointImpl(paramStaticPart, paramObject1, paramObject2, new Object[] { paramObject3 });
  }
  
  public static JoinPoint makeJP(JoinPoint.StaticPart paramStaticPart, Object paramObject1, Object paramObject2, Object[] paramArrayOfObject)
  {
    return new JoinPointImpl(paramStaticPart, paramObject1, paramObject2, paramArrayOfObject);
  }
  
  public MethodSignature makeMethodSig(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7)
  {
    int i = Integer.parseInt(paramString1, 16);
    paramString1 = makeClass(paramString3, this.lookupClassLoader);
    paramString4 = new StringTokenizer(paramString4, ":");
    int j = paramString4.countTokens();
    paramString3 = new Class[j];
    for (int k = 0; k < j; k++) {
      paramString3[k] = makeClass(paramString4.nextToken(), this.lookupClassLoader);
    }
    paramString5 = new StringTokenizer(paramString5, ":");
    j = paramString5.countTokens();
    paramString4 = new String[j];
    for (k = 0; k < j; k++) {
      paramString4[k] = paramString5.nextToken();
    }
    paramString5 = new StringTokenizer(paramString6, ":");
    j = paramString5.countTokens();
    paramString6 = new Class[j];
    for (k = 0; k < j; k++) {
      paramString6[k] = makeClass(paramString5.nextToken(), this.lookupClassLoader);
    }
    return new MethodSignatureImpl(i, paramString2, paramString1, paramString3, paramString4, paramString6, makeClass(paramString7, this.lookupClassLoader));
  }
  
  public JoinPoint.StaticPart makeSJP(String paramString, Signature paramSignature, int paramInt)
  {
    int i = this.count;
    this.count = (i + 1);
    return new JoinPointImpl.StaticPartImpl(i, paramString, paramSignature, makeSourceLoc(paramInt, -1));
  }
  
  public SourceLocation makeSourceLoc(int paramInt1, int paramInt2)
  {
    return new SourceLocationImpl(this.lexicalClass, this.filename, paramInt1);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/runtime/reflect/Factory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */