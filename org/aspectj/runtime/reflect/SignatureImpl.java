package org.aspectj.runtime.reflect;

import java.lang.ref.SoftReference;
import java.util.StringTokenizer;
import org.aspectj.lang.Signature;

abstract class SignatureImpl
  implements Signature
{
  static Class[] EMPTY_CLASS_ARRAY = new Class[0];
  static String[] EMPTY_STRING_ARRAY;
  static final String INNER_SEP = ":";
  static final char SEP = '-';
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
  
  public SignatureImpl(String paramString)
  {
    this.stringRep = paramString;
  }
  
  private ClassLoader getLookupClassLoader()
  {
    if (this.lookupClassLoader == null) {
      this.lookupClassLoader = getClass().getClassLoader();
    }
    return this.lookupClassLoader;
  }
  
  static boolean getUseCache()
  {
    return useCache;
  }
  
  static void setUseCache(boolean paramBoolean)
  {
    useCache = paramBoolean;
  }
  
  void addFullTypeNames(StringBuffer paramStringBuffer, Class[] paramArrayOfClass)
  {
    int i = 0;
    while (i < paramArrayOfClass.length)
    {
      if (i > 0) {
        paramStringBuffer.append(", ");
      }
      paramStringBuffer.append(fullTypeName(paramArrayOfClass[i]));
      i += 1;
    }
  }
  
  void addShortTypeNames(StringBuffer paramStringBuffer, Class[] paramArrayOfClass)
  {
    int i = 0;
    while (i < paramArrayOfClass.length)
    {
      if (i > 0) {
        paramStringBuffer.append(", ");
      }
      paramStringBuffer.append(shortTypeName(paramArrayOfClass[i]));
      i += 1;
    }
  }
  
  void addTypeArray(StringBuffer paramStringBuffer, Class[] paramArrayOfClass)
  {
    addFullTypeNames(paramStringBuffer, paramArrayOfClass);
  }
  
  protected abstract String createToString(StringMaker paramStringMaker);
  
  int extractInt(int paramInt)
  {
    return Integer.parseInt(extractString(paramInt), 16);
  }
  
  String extractString(int paramInt)
  {
    int i = 0;
    int k = this.stringRep.indexOf('-');
    int j = paramInt;
    paramInt = k;
    while (j > 0)
    {
      i = paramInt + 1;
      paramInt = this.stringRep.indexOf('-', i);
      j -= 1;
    }
    j = paramInt;
    if (paramInt == -1) {
      j = this.stringRep.length();
    }
    return this.stringRep.substring(i, j);
  }
  
  String[] extractStrings(int paramInt)
  {
    StringTokenizer localStringTokenizer = new StringTokenizer(extractString(paramInt), ":");
    int i = localStringTokenizer.countTokens();
    String[] arrayOfString = new String[i];
    paramInt = 0;
    while (paramInt < i)
    {
      arrayOfString[paramInt] = localStringTokenizer.nextToken();
      paramInt += 1;
    }
    return arrayOfString;
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
    paramInt = 0;
    while (paramInt < i)
    {
      arrayOfClass[paramInt] = Factory.makeClass(localStringTokenizer.nextToken(), getLookupClassLoader());
      paramInt += 1;
    }
    return arrayOfClass;
  }
  
  String fullTypeName(Class paramClass)
  {
    if (paramClass == null) {
      return "ANONYMOUS";
    }
    if (paramClass.isArray()) {
      return fullTypeName(paramClass.getComponentType()) + "[]";
    }
    return paramClass.getName().replace('$', '.');
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
  
  public void setLookupClassLoader(ClassLoader paramClassLoader)
  {
    this.lookupClassLoader = paramClassLoader;
  }
  
  String shortTypeName(Class paramClass)
  {
    if (paramClass == null) {
      return "ANONYMOUS";
    }
    if (paramClass.isArray()) {
      return shortTypeName(paramClass.getComponentType()) + "[]";
    }
    return stripPackageName(paramClass.getName()).replace('$', '.');
  }
  
  String stripPackageName(String paramString)
  {
    int i = paramString.lastIndexOf('.');
    if (i == -1) {
      return paramString;
    }
    return paramString.substring(i + 1);
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
    Object localObject3 = null;
    Object localObject1 = localObject3;
    if ((!useCache) || (this.stringCache == null)) {}
    for (;;)
    {
      try
      {
        this.stringCache = new CacheImpl();
        localObject1 = localObject3;
      }
      catch (Throwable localThrowable)
      {
        useCache = false;
        localObject2 = localObject3;
        continue;
      }
      localObject3 = localObject1;
      if (localObject1 == null) {
        localObject3 = createToString(paramStringMaker);
      }
      if (useCache) {
        this.stringCache.set(paramStringMaker.cacheOffset, (String)localObject3);
      }
      return (String)localObject3;
      Object localObject2 = this.stringCache.get(paramStringMaker.cacheOffset);
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
      String[] arrayOfString = array();
      if (arrayOfString == null) {
        return null;
      }
      return arrayOfString[paramInt];
    }
    
    public void set(int paramInt, String paramString)
    {
      String[] arrayOfString2 = array();
      String[] arrayOfString1 = arrayOfString2;
      if (arrayOfString2 == null) {
        arrayOfString1 = makeCache();
      }
      arrayOfString1[paramInt] = paramString;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/runtime/reflect/SignatureImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */