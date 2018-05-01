package com.google.firebase.components;

import com.google.firebase.inject.Provider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class zzg
  implements ComponentContainer
{
  private final List<Component<?>> zzah;
  private final Map<Class<?>, zzi<?>> zzai = new HashMap();
  
  public zzg(Iterable<ComponentRegistrar> paramIterable, Component<?>... paramVarArgs)
  {
    Object localObject1 = new ArrayList();
    paramIterable = paramIterable.iterator();
    while (paramIterable.hasNext()) {
      ((List)localObject1).addAll(((ComponentRegistrar)paramIterable.next()).getComponents());
    }
    Collections.addAll((Collection)localObject1, paramVarArgs);
    paramIterable = new HashMap(((List)localObject1).size());
    Object localObject2 = ((List)localObject1).iterator();
    Object localObject3;
    Object localObject4;
    do
    {
      while (!((Iterator)localObject3).hasNext())
      {
        if (!((Iterator)localObject2).hasNext()) {
          break;
        }
        localObject3 = (Component)((Iterator)localObject2).next();
        paramVarArgs = new zzh((Component)localObject3);
        localObject3 = ((Component)localObject3).zze().iterator();
      }
      localObject4 = (Class)((Iterator)localObject3).next();
    } while (paramIterable.put(localObject4, paramVarArgs) == null);
    throw new IllegalArgumentException(String.format("Multiple components provide %s.", new Object[] { localObject4 }));
    localObject2 = paramIterable.values().iterator();
    while (((Iterator)localObject2).hasNext())
    {
      paramVarArgs = (zzh)((Iterator)localObject2).next();
      localObject3 = paramVarArgs.zzk().zzf().iterator();
      while (((Iterator)localObject3).hasNext())
      {
        localObject4 = (Dependency)((Iterator)localObject3).next();
        if (((Dependency)localObject4).zzp())
        {
          localObject4 = (zzh)paramIterable.get(((Dependency)localObject4).zzn());
          if (localObject4 != null)
          {
            paramVarArgs.zza((zzh)localObject4);
            ((zzh)localObject4).zzb(paramVarArgs);
          }
        }
      }
    }
    paramIterable = new HashSet(paramIterable.values());
    paramVarArgs = new HashSet();
    localObject2 = paramIterable.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (zzh)((Iterator)localObject2).next();
      if (((zzh)localObject3).zzl()) {
        paramVarArgs.add(localObject3);
      }
    }
    ArrayList localArrayList = new ArrayList();
    while (!paramVarArgs.isEmpty())
    {
      localObject3 = (zzh)paramVarArgs.iterator().next();
      paramVarArgs.remove(localObject3);
      localArrayList.add(((zzh)localObject3).zzk());
      localObject2 = ((zzh)localObject3).zzf().iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject4 = (zzh)((Iterator)localObject2).next();
        ((zzh)localObject4).zzc((zzh)localObject3);
        if (((zzh)localObject4).zzl()) {
          paramVarArgs.add(localObject4);
        }
      }
    }
    if (localArrayList.size() == ((List)localObject1).size())
    {
      Collections.reverse(localArrayList);
      this.zzah = Collections.unmodifiableList(localArrayList);
      paramIterable = this.zzah.iterator();
      while (paramIterable.hasNext())
      {
        localObject1 = (Component)paramIterable.next();
        paramVarArgs = new zzi(((Component)localObject1).zzg(), new zzl(((Component)localObject1).zzf(), this));
        localObject2 = ((Component)localObject1).zze().iterator();
        while (((Iterator)localObject2).hasNext())
        {
          localObject1 = (Class)((Iterator)localObject2).next();
          this.zzai.put(localObject1, paramVarArgs);
        }
      }
    }
    paramVarArgs = new ArrayList();
    localObject1 = paramIterable.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      paramIterable = (zzh)((Iterator)localObject1).next();
      if ((!paramIterable.zzl()) && (!paramIterable.zzm())) {
        paramVarArgs.add(paramIterable.zzk());
      }
    }
    throw new DependencyCycleException(paramVarArgs);
    do
    {
      paramIterable = this.zzah.iterator();
      while (!paramVarArgs.hasNext())
      {
        if (!paramIterable.hasNext()) {
          break;
        }
        localObject1 = (Component)paramIterable.next();
        paramVarArgs = ((Component)localObject1).zzf().iterator();
      }
      localObject2 = (Dependency)paramVarArgs.next();
    } while ((!((Dependency)localObject2).zzo()) || (this.zzai.containsKey(((Dependency)localObject2).zzn())));
    throw new MissingDependencyException(String.format("Unsatisfied dependency for component %s: %s", new Object[] { localObject1, ((Dependency)localObject2).zzn() }));
  }
  
  public final Object get(Class paramClass)
  {
    return ComponentContainer..CC.get(this, paramClass);
  }
  
  public final <T> Provider<T> getProvider(Class<T> paramClass)
  {
    zzk.zza(paramClass, "Null interface requested.");
    return (Provider)this.zzai.get(paramClass);
  }
  
  public final void zzb(boolean paramBoolean)
  {
    Iterator localIterator = this.zzah.iterator();
    while (localIterator.hasNext())
    {
      Component localComponent = (Component)localIterator.next();
      if ((localComponent.zzh()) || ((localComponent.zzi()) && (paramBoolean))) {
        get((Class)localComponent.zze().iterator().next());
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/components/zzg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */