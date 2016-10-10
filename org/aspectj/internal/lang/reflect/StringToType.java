package org.aspectj.internal.lang.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.StringTokenizer;
import org.aspectj.lang.reflect.AjTypeSystem;

public class StringToType
{
  public static Type[] commaSeparatedListToTypeArray(String paramString, Class paramClass)
    throws ClassNotFoundException
  {
    paramString = new StringTokenizer(paramString, ",");
    Type[] arrayOfType = new Type[paramString.countTokens()];
    int i = 0;
    while (paramString.hasMoreTokens())
    {
      arrayOfType[i] = stringToType(paramString.nextToken().trim(), paramClass);
      i += 1;
    }
    return arrayOfType;
  }
  
  private static Type makeParameterizedType(String paramString, Class paramClass)
    throws ClassNotFoundException
  {
    int i = paramString.indexOf('<');
    final Class localClass = Class.forName(paramString.substring(0, i), false, paramClass.getClassLoader());
    new ParameterizedType()
    {
      public Type[] getActualTypeArguments()
      {
        return this.val$typeParams;
      }
      
      public Type getOwnerType()
      {
        return localClass.getEnclosingClass();
      }
      
      public Type getRawType()
      {
        return localClass;
      }
    };
  }
  
  public static Type stringToType(String paramString, Class paramClass)
    throws ClassNotFoundException
  {
    try
    {
      if (paramString.indexOf("<") == -1) {
        return AjTypeSystem.getAjType(Class.forName(paramString, false, paramClass.getClassLoader()));
      }
      Type localType = makeParameterizedType(paramString, paramClass);
      return localType;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      paramClass = paramClass.getTypeParameters();
      int i = 0;
      while (i < paramClass.length)
      {
        if (paramClass[i].getName().equals(paramString)) {
          return paramClass[i];
        }
        i += 1;
      }
      throw new ClassNotFoundException(paramString);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/internal/lang/reflect/StringToType.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */