package org.aspectj.runtime.reflect;

import java.lang.reflect.Modifier;

class StringMaker
{
  static StringMaker longStringMaker;
  static StringMaker middleStringMaker;
  static StringMaker shortStringMaker = new StringMaker();
  int cacheOffset;
  boolean includeArgs = true;
  boolean includeEnclosingPoint = true;
  boolean includeJoinPointTypeName = true;
  boolean includeModifiers = false;
  boolean includeThrows = false;
  boolean shortKindName = true;
  boolean shortPrimaryTypeNames = false;
  boolean shortTypeNames = true;
  
  static
  {
    shortStringMaker.shortTypeNames = true;
    shortStringMaker.includeArgs = false;
    shortStringMaker.includeThrows = false;
    shortStringMaker.includeModifiers = false;
    shortStringMaker.shortPrimaryTypeNames = true;
    shortStringMaker.includeJoinPointTypeName = false;
    shortStringMaker.includeEnclosingPoint = false;
    shortStringMaker.cacheOffset = 0;
    middleStringMaker = new StringMaker();
    middleStringMaker.shortTypeNames = true;
    middleStringMaker.includeArgs = true;
    middleStringMaker.includeThrows = false;
    middleStringMaker.includeModifiers = false;
    middleStringMaker.shortPrimaryTypeNames = false;
    shortStringMaker.cacheOffset = 1;
    longStringMaker = new StringMaker();
    longStringMaker.shortTypeNames = false;
    longStringMaker.includeArgs = true;
    longStringMaker.includeThrows = false;
    longStringMaker.includeModifiers = true;
    longStringMaker.shortPrimaryTypeNames = false;
    longStringMaker.shortKindName = false;
    longStringMaker.cacheOffset = 2;
  }
  
  public void addSignature(StringBuffer paramStringBuffer, Class[] paramArrayOfClass)
  {
    if (paramArrayOfClass == null) {}
    for (;;)
    {
      return;
      if (!this.includeArgs)
      {
        if (paramArrayOfClass.length == 0) {
          paramStringBuffer.append("()");
        } else {
          paramStringBuffer.append("(..)");
        }
      }
      else
      {
        paramStringBuffer.append("(");
        addTypeNames(paramStringBuffer, paramArrayOfClass);
        paramStringBuffer.append(")");
      }
    }
  }
  
  public void addThrows(StringBuffer paramStringBuffer, Class[] paramArrayOfClass)
  {
    if ((!this.includeThrows) || (paramArrayOfClass == null) || (paramArrayOfClass.length == 0)) {}
    for (;;)
    {
      return;
      paramStringBuffer.append(" throws ");
      addTypeNames(paramStringBuffer, paramArrayOfClass);
    }
  }
  
  public void addTypeNames(StringBuffer paramStringBuffer, Class[] paramArrayOfClass)
  {
    for (int i = 0; i < paramArrayOfClass.length; i++)
    {
      if (i > 0) {
        paramStringBuffer.append(", ");
      }
      paramStringBuffer.append(makeTypeName(paramArrayOfClass[i]));
    }
  }
  
  String makeKindName(String paramString)
  {
    int i = paramString.lastIndexOf('-');
    if (i == -1) {}
    for (;;)
    {
      return paramString;
      paramString = paramString.substring(i + 1);
    }
  }
  
  String makeModifiersString(int paramInt)
  {
    String str;
    if (!this.includeModifiers) {
      str = "";
    }
    for (;;)
    {
      return str;
      str = Modifier.toString(paramInt);
      if (str.length() == 0) {
        str = "";
      } else {
        str = str + " ";
      }
    }
  }
  
  public String makePrimaryTypeName(Class paramClass, String paramString)
  {
    return makeTypeName(paramClass, paramString, this.shortPrimaryTypeNames);
  }
  
  public String makeTypeName(Class paramClass)
  {
    return makeTypeName(paramClass, paramClass.getName(), this.shortTypeNames);
  }
  
  String makeTypeName(Class paramClass, String paramString, boolean paramBoolean)
  {
    if (paramClass == null) {
      paramClass = "ANONYMOUS";
    }
    for (;;)
    {
      return paramClass;
      if (paramClass.isArray())
      {
        paramClass = paramClass.getComponentType();
        paramClass = makeTypeName(paramClass, paramClass.getName(), paramBoolean) + "[]";
      }
      else if (paramBoolean)
      {
        paramClass = stripPackageName(paramString).replace('$', '.');
      }
      else
      {
        paramClass = paramString.replace('$', '.');
      }
    }
  }
  
  String stripPackageName(String paramString)
  {
    int i = paramString.lastIndexOf('.');
    if (i == -1) {}
    for (;;)
    {
      return paramString;
      paramString = paramString.substring(i + 1);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/runtime/reflect/StringMaker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */