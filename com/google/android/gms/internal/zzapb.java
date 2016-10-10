package com.google.android.gms.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public final class zzapb
{
  private final Map<Type, zzaod<?>> bkY;
  
  public zzapb(Map<Type, zzaod<?>> paramMap)
  {
    this.bkY = paramMap;
  }
  
  private <T> zzapg<T> zzc(final Type paramType, Class<? super T> paramClass)
  {
    if (Collection.class.isAssignableFrom(paramClass))
    {
      if (SortedSet.class.isAssignableFrom(paramClass)) {
        new zzapg()
        {
          public T bg()
          {
            return new TreeSet();
          }
        };
      }
      if (EnumSet.class.isAssignableFrom(paramClass)) {
        new zzapg()
        {
          public T bg()
          {
            if ((paramType instanceof ParameterizedType))
            {
              localObject = ((ParameterizedType)paramType).getActualTypeArguments()[0];
              if ((localObject instanceof Class)) {
                return EnumSet.noneOf((Class)localObject);
              }
              localObject = String.valueOf(paramType.toString());
              if (((String)localObject).length() != 0) {}
              for (localObject = "Invalid EnumSet type: ".concat((String)localObject);; localObject = new String("Invalid EnumSet type: ")) {
                throw new zzaoi((String)localObject);
              }
            }
            Object localObject = String.valueOf(paramType.toString());
            if (((String)localObject).length() != 0) {}
            for (localObject = "Invalid EnumSet type: ".concat((String)localObject);; localObject = new String("Invalid EnumSet type: ")) {
              throw new zzaoi((String)localObject);
            }
          }
        };
      }
      if (Set.class.isAssignableFrom(paramClass)) {
        new zzapg()
        {
          public T bg()
          {
            return new LinkedHashSet();
          }
        };
      }
      if (Queue.class.isAssignableFrom(paramClass)) {
        new zzapg()
        {
          public T bg()
          {
            return new LinkedList();
          }
        };
      }
      new zzapg()
      {
        public T bg()
        {
          return new ArrayList();
        }
      };
    }
    if (Map.class.isAssignableFrom(paramClass))
    {
      if (SortedMap.class.isAssignableFrom(paramClass)) {
        new zzapg()
        {
          public T bg()
          {
            return new TreeMap();
          }
        };
      }
      if (((paramType instanceof ParameterizedType)) && (!String.class.isAssignableFrom(zzapx.zzl(((ParameterizedType)paramType).getActualTypeArguments()[0]).by()))) {
        new zzapg()
        {
          public T bg()
          {
            return new LinkedHashMap();
          }
        };
      }
      new zzapg()
      {
        public T bg()
        {
          return new zzapf();
        }
      };
    }
    return null;
  }
  
  private <T> zzapg<T> zzd(final Type paramType, final Class<? super T> paramClass)
  {
    new zzapg()
    {
      private final zzapj blB = zzapj.bl();
      
      public T bg()
      {
        try
        {
          Object localObject = this.blB.zzf(paramClass);
          return (T)localObject;
        }
        catch (Exception localException)
        {
          String str = String.valueOf(paramType);
          throw new RuntimeException(String.valueOf(str).length() + 116 + "Unable to invoke no-args constructor for " + str + ". " + "Register an InstanceCreator with Gson for this type may fix this problem.", localException);
        }
      }
    };
  }
  
  private <T> zzapg<T> zzl(final Class<? super T> paramClass)
  {
    try
    {
      paramClass = paramClass.getDeclaredConstructor(new Class[0]);
      if (!paramClass.isAccessible()) {
        paramClass.setAccessible(true);
      }
      paramClass = new zzapg()
      {
        public T bg()
        {
          try
          {
            Object localObject = paramClass.newInstance(null);
            return (T)localObject;
          }
          catch (InstantiationException localInstantiationException)
          {
            str = String.valueOf(paramClass);
            throw new RuntimeException(String.valueOf(str).length() + 30 + "Failed to invoke " + str + " with no args", localInstantiationException);
          }
          catch (InvocationTargetException localInvocationTargetException)
          {
            String str = String.valueOf(paramClass);
            throw new RuntimeException(String.valueOf(str).length() + 30 + "Failed to invoke " + str + " with no args", localInvocationTargetException.getTargetException());
          }
          catch (IllegalAccessException localIllegalAccessException)
          {
            throw new AssertionError(localIllegalAccessException);
          }
        }
      };
      return paramClass;
    }
    catch (NoSuchMethodException paramClass) {}
    return null;
  }
  
  public String toString()
  {
    return this.bkY.toString();
  }
  
  public <T> zzapg<T> zzb(final zzapx<T> paramzzapx)
  {
    final Type localType = paramzzapx.bz();
    Class localClass = paramzzapx.by();
    paramzzapx = (zzaod)this.bkY.get(localType);
    if (paramzzapx != null) {
      paramzzapx = new zzapg()
      {
        public T bg()
        {
          return (T)paramzzapx.zza(localType);
        }
      };
    }
    zzapg localzzapg;
    do
    {
      do
      {
        return paramzzapx;
        paramzzapx = (zzaod)this.bkY.get(localClass);
        if (paramzzapx != null) {
          new zzapg()
          {
            public T bg()
            {
              return (T)paramzzapx.zza(localType);
            }
          };
        }
        localzzapg = zzl(localClass);
        paramzzapx = localzzapg;
      } while (localzzapg != null);
      localzzapg = zzc(localType, localClass);
      paramzzapx = localzzapg;
    } while (localzzapg != null);
    return zzd(localType, localClass);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzapb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */