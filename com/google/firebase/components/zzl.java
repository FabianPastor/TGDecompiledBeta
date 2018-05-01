package com.google.firebase.components;

import com.google.firebase.inject.Provider;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class zzl
  implements ComponentContainer
{
  private final Set<Class<?>> zzav;
  private final Set<Class<?>> zzaw;
  private final ComponentContainer zzax;
  
  public zzl(Iterable<Dependency> paramIterable, ComponentContainer paramComponentContainer)
  {
    HashSet localHashSet1 = new HashSet();
    HashSet localHashSet2 = new HashSet();
    Iterator localIterator = paramIterable.iterator();
    while (localIterator.hasNext())
    {
      paramIterable = (Dependency)localIterator.next();
      if (paramIterable.zzp()) {
        localHashSet1.add(paramIterable.zzn());
      } else {
        localHashSet2.add(paramIterable.zzn());
      }
    }
    this.zzav = Collections.unmodifiableSet(localHashSet1);
    this.zzaw = Collections.unmodifiableSet(localHashSet2);
    this.zzax = paramComponentContainer;
  }
  
  public final <T> Provider<T> getProvider(Class<T> paramClass)
  {
    if (!this.zzaw.contains(paramClass)) {
      throw new IllegalArgumentException(String.format("Requesting Provider<%s> is not allowed.", new Object[] { paramClass }));
    }
    return this.zzax.getProvider(paramClass);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/components/zzl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */