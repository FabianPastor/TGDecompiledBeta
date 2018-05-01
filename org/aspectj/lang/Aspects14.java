package org.aspectj.lang;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Aspects14
{
  private static final String ASPECTOF = "aspectOf";
  private static final Class[] EMPTY_CLASS_ARRAY = new Class[0];
  private static final Object[] EMPTY_OBJECT_ARRAY;
  private static final String HASASPECT = "hasAspect";
  private static final Class[] PEROBJECT_CLASS_ARRAY;
  private static final Class[] PERTYPEWITHIN_CLASS_ARRAY;
  static Class class$java$lang$Class;
  static Class class$java$lang$Object;
  
  static
  {
    Class localClass;
    if (class$java$lang$Object == null)
    {
      localClass = class$("java.lang.Object");
      class$java$lang$Object = localClass;
      PEROBJECT_CLASS_ARRAY = new Class[] { localClass };
      if (class$java$lang$Class != null) {
        break label76;
      }
      localClass = class$("java.lang.Class");
      class$java$lang$Class = localClass;
    }
    for (;;)
    {
      PERTYPEWITHIN_CLASS_ARRAY = new Class[] { localClass };
      EMPTY_OBJECT_ARRAY = new Object[0];
      return;
      localClass = class$java$lang$Object;
      break;
      label76:
      localClass = class$java$lang$Class;
    }
  }
  
  public static Object aspectOf(Class paramClass)
    throws NoAspectBoundException
  {
    try
    {
      Object localObject = getSingletonOrThreadAspectOf(paramClass).invoke(null, EMPTY_OBJECT_ARRAY);
      return localObject;
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      throw new NoAspectBoundException(paramClass.getName(), localInvocationTargetException);
    }
    catch (Exception localException)
    {
      throw new NoAspectBoundException(paramClass.getName(), localException);
    }
  }
  
  public static Object aspectOf(Class paramClass1, Class paramClass2)
    throws NoAspectBoundException
  {
    try
    {
      paramClass2 = getPerTypeWithinAspectOf(paramClass1).invoke(null, new Object[] { paramClass2 });
      return paramClass2;
    }
    catch (InvocationTargetException paramClass2)
    {
      throw new NoAspectBoundException(paramClass1.getName(), paramClass2);
    }
    catch (Exception paramClass2)
    {
      throw new NoAspectBoundException(paramClass1.getName(), paramClass2);
    }
  }
  
  public static Object aspectOf(Class paramClass, Object paramObject)
    throws NoAspectBoundException
  {
    try
    {
      paramObject = getPerObjectAspectOf(paramClass).invoke(null, new Object[] { paramObject });
      return paramObject;
    }
    catch (InvocationTargetException paramObject)
    {
      throw new NoAspectBoundException(paramClass.getName(), (Throwable)paramObject);
    }
    catch (Exception paramObject)
    {
      throw new NoAspectBoundException(paramClass.getName(), (Throwable)paramObject);
    }
  }
  
  private static Method checkAspectOf(Method paramMethod, Class paramClass)
    throws NoSuchMethodException
  {
    paramMethod.setAccessible(true);
    if ((!paramMethod.isAccessible()) || (!Modifier.isPublic(paramMethod.getModifiers())) || (!Modifier.isStatic(paramMethod.getModifiers()))) {
      throw new NoSuchMethodException(paramClass.getName() + ".aspectOf(..) is not accessible public static");
    }
    return paramMethod;
  }
  
  private static Method checkHasAspect(Method paramMethod, Class paramClass)
    throws NoSuchMethodException
  {
    paramMethod.setAccessible(true);
    if ((!paramMethod.isAccessible()) || (!Modifier.isPublic(paramMethod.getModifiers())) || (!Modifier.isStatic(paramMethod.getModifiers()))) {
      throw new NoSuchMethodException(paramClass.getName() + ".hasAspect(..) is not accessible public static");
    }
    return paramMethod;
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
  
  private static Method getPerObjectAspectOf(Class paramClass)
    throws NoSuchMethodException
  {
    return checkAspectOf(paramClass.getDeclaredMethod("aspectOf", PEROBJECT_CLASS_ARRAY), paramClass);
  }
  
  private static Method getPerObjectHasAspect(Class paramClass)
    throws NoSuchMethodException
  {
    return checkHasAspect(paramClass.getDeclaredMethod("hasAspect", PEROBJECT_CLASS_ARRAY), paramClass);
  }
  
  private static Method getPerTypeWithinAspectOf(Class paramClass)
    throws NoSuchMethodException
  {
    return checkAspectOf(paramClass.getDeclaredMethod("aspectOf", PERTYPEWITHIN_CLASS_ARRAY), paramClass);
  }
  
  private static Method getPerTypeWithinHasAspect(Class paramClass)
    throws NoSuchMethodException
  {
    return checkHasAspect(paramClass.getDeclaredMethod("hasAspect", PERTYPEWITHIN_CLASS_ARRAY), paramClass);
  }
  
  private static Method getSingletonOrThreadAspectOf(Class paramClass)
    throws NoSuchMethodException
  {
    return checkAspectOf(paramClass.getDeclaredMethod("aspectOf", EMPTY_CLASS_ARRAY), paramClass);
  }
  
  private static Method getSingletonOrThreadHasAspect(Class paramClass)
    throws NoSuchMethodException
  {
    return checkHasAspect(paramClass.getDeclaredMethod("hasAspect", EMPTY_CLASS_ARRAY), paramClass);
  }
  
  public static boolean hasAspect(Class paramClass)
    throws NoAspectBoundException
  {
    try
    {
      boolean bool = ((Boolean)getSingletonOrThreadHasAspect(paramClass).invoke(null, EMPTY_OBJECT_ARRAY)).booleanValue();
      return bool;
    }
    catch (Exception paramClass) {}
    return false;
  }
  
  public static boolean hasAspect(Class paramClass1, Class paramClass2)
    throws NoAspectBoundException
  {
    try
    {
      boolean bool = ((Boolean)getPerTypeWithinHasAspect(paramClass1).invoke(null, new Object[] { paramClass2 })).booleanValue();
      return bool;
    }
    catch (Exception paramClass1) {}
    return false;
  }
  
  public static boolean hasAspect(Class paramClass, Object paramObject)
    throws NoAspectBoundException
  {
    try
    {
      boolean bool = ((Boolean)getPerObjectHasAspect(paramClass).invoke(null, new Object[] { paramObject })).booleanValue();
      return bool;
    }
    catch (Exception paramClass) {}
    return false;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/lang/Aspects14.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */