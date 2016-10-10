package org.aspectj.lang.reflect;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import org.aspectj.internal.lang.reflect.AjTypeImpl;

public class AjTypeSystem
{
  private static Map<Class, WeakReference<AjType>> ajTypes = Collections.synchronizedMap(new WeakHashMap());
  
  public static <T> AjType<T> getAjType(Class<T> paramClass)
  {
    Object localObject = (WeakReference)ajTypes.get(paramClass);
    if (localObject != null)
    {
      localObject = (AjType)((WeakReference)localObject).get();
      if (localObject != null) {
        return (AjType<T>)localObject;
      }
      localObject = new AjTypeImpl(paramClass);
      ajTypes.put(paramClass, new WeakReference(localObject));
      return (AjType<T>)localObject;
    }
    localObject = new AjTypeImpl(paramClass);
    ajTypes.put(paramClass, new WeakReference(localObject));
    return (AjType<T>)localObject;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/lang/reflect/AjTypeSystem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */