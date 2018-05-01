package org.aspectj.runtime.reflect;

import java.lang.ref.SoftReference;
import java.util.StringTokenizer;
import org.aspectj.lang.Signature;

abstract class SignatureImpl
  implements Signature
{
  static Class[] EMPTY_CLASS_ARRAY = new Class[0];
  static String[] EMPTY_STRING_ARRAY;
  private static boolean useCache = true;
  Class declaringType;
  String declaringTypeName;
  ClassLoader lookupClassLoader = null;
  int modifiers = -1;
  String name;
  Cache stringCache;
  private String stringRep;
  
  static
  {
    EMPTY_STRING_ARRAY = new String[0];
  }
  
  SignatureImpl(int paramInt, String paramString, Class paramClass)
  {
    this.modifiers = paramInt;
    this.name = paramString;
    this.declaringType = paramClass;
  }
  
  private ClassLoader getLookupClassLoader()
  {
    if (this.lookupClassLoader == null) {
      this.lookupClassLoader = getClass().getClassLoader();
    }
    return this.lookupClassLoader;
  }
  
  protected abstract String createToString(StringMaker paramStringMaker);
  
  int extractInt(int paramInt)
  {
    return Integer.parseInt(extractString(paramInt), 16);
  }
  
  String extractString(int paramInt)
  {
    int i = 0;
    int j = this.stringRep.indexOf('-');
    int k = paramInt;
    paramInt = j;
    while (k > 0)
    {
      i = paramInt + 1;
      paramInt = this.stringRep.indexOf('-', i);
      k--;
    }
    k = paramInt;
    if (paramInt == -1) {
      k = this.stringRep.length();
    }
    return this.stringRep.substring(i, k);
  }
  
  Class extractType(int paramInt)
  {
    return Factory.makeClass(extractString(paramInt), getLookupClassLoader());
  }
  
  Class[] extractTypes(int paramInt)
  {
    StringTokenizer localStringTokenizer = new StringTokenizer(extractString(paramInt), ":");
    int i = localStringTokenizer.countTokens();
    Class[] arrayOfClass = new Class[i];
    for (paramInt = 0; paramInt < i; paramInt++) {
      arrayOfClass[paramInt] = Factory.makeClass(localStringTokenizer.nextToken(), getLookupClassLoader());
    }
    return arrayOfClass;
  }
  
  public Class getDeclaringType()
  {
    if (this.declaringType == null) {
      this.declaringType = extractType(2);
    }
    return this.declaringType;
  }
  
  public String getDeclaringTypeName()
  {
    if (this.declaringTypeName == null) {
      this.declaringTypeName = getDeclaringType().getName();
    }
    return this.declaringTypeName;
  }
  
  public int getModifiers()
  {
    if (this.modifiers == -1) {
      this.modifiers = extractInt(0);
    }
    return this.modifiers;
  }
  
  public String getName()
  {
    if (this.name == null) {
      this.name = extractString(1);
    }
    return this.name;
  }
  
  public final String toString()
  {
    return toString(StringMaker.middleStringMaker);
  }
  
  String toString(StringMaker paramStringMaker)
  {
    Object localObject1 = null;
    Object localObject2 = localObject1;
    if ((!useCache) || (this.stringCache == null)) {}
    for (;;)
    {
      try
      {
        localObject2 = new org/aspectj/runtime/reflect/SignatureImpl$CacheImpl;
        ((CacheImpl)localObject2).<init>();
        this.stringCache = ((Cache)localObject2);
        localObject2 = localObject1;
      }
      catch (Throwable localThrowable)
      {
        useCache = false;
        localObject3 = localObject1;
        continue;
      }
      localObject1 = localObject2;
      if (localObject2 == null) {
        localObject1 = createToString(paramStringMaker);
      }
      if (useCache) {
        this.stringCache.set(paramStringMaker.cacheOffset, (String)localObject1);
      }
      return (String)localObject1;
      Object localObject3 = this.stringCache.get(paramStringMaker.cacheOffset);
    }
  }
  
  private static abstract interface Cache
  {
    public abstract String get(int paramInt);
    
    public abstract void set(int paramInt, String paramString);
  }
  
  private static final class CacheImpl
    implements SignatureImpl.Cache
  {
    private SoftReference toStringCacheRef;
    
    public CacheImpl()
    {
      makeCache();
    }
    
    private String[] array()
    {
      return (String[])this.toStringCacheRef.get();
    }
    
    private String[] makeCache()
    {
      String[] arrayOfString = new String[3];
      this.toStringCacheRef = new SoftReference(arrayOfString);
      return arrayOfString;
    }
    
    public String get(int paramInt)
    {
      Object localObject = array();
      if (localObject == null) {}
      for (localObject = null;; localObject = localObject[paramInt]) {
        return (String)localObject;
      }
    }
    
    public void set(int paramInt, String paramString)
    {
      String[] arrayOfString1 = array();
      String[] arrayOfString2 = arrayOfString1;
      if (arrayOfString1 == null) {
        arrayOfString2 = makeCache();
      }
      arrayOfString2[paramInt] = paramString;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/runtime/reflect/SignatureImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */