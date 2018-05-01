package com.google.firebase.components;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class Component<T>
{
  private final Set<Class<? super T>> zzab;
  private final Set<Dependency> zzac;
  private final int zzad;
  private final ComponentFactory<T> zzae;
  
  private Component(Set<Class<? super T>> paramSet, Set<Dependency> paramSet1, int paramInt, ComponentFactory<T> paramComponentFactory)
  {
    this.zzab = Collections.unmodifiableSet(paramSet);
    this.zzac = Collections.unmodifiableSet(paramSet1);
    this.zzad = paramInt;
    this.zzae = paramComponentFactory;
  }
  
  public static <T> Builder<T> builder(Class<T> paramClass)
  {
    return new Builder(paramClass, new Class[0], null);
  }
  
  public static <T> Component<T> of(Class<T> paramClass, T paramT)
  {
    return builder(paramClass).factory(new zza(paramT)).build();
  }
  
  public final String toString()
  {
    return "Component<" + Arrays.toString(this.zzab.toArray()) + ">{" + this.zzad + ", deps=" + Arrays.toString(this.zzac.toArray()) + "}";
  }
  
  public final Set<Class<? super T>> zze()
  {
    return this.zzab;
  }
  
  public final Set<Dependency> zzf()
  {
    return this.zzac;
  }
  
  public final ComponentFactory<T> zzg()
  {
    return this.zzae;
  }
  
  public final boolean zzh()
  {
    boolean bool = true;
    if (this.zzad == 1) {}
    for (;;)
    {
      return bool;
      bool = false;
    }
  }
  
  public final boolean zzi()
  {
    if (this.zzad == 2) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static class Builder<T>
  {
    private final Set<Class<? super T>> zzab = new HashSet();
    private final Set<Dependency> zzac = new HashSet();
    private int zzad = 0;
    private ComponentFactory<T> zzae;
    
    private Builder(Class<T> paramClass, Class<? super T>... paramVarArgs)
    {
      zzk.zza(paramClass, "Null interface");
      this.zzab.add(paramClass);
      int j = paramVarArgs.length;
      while (i < j)
      {
        zzk.zza(paramVarArgs[i], "Null interface");
        i++;
      }
      Collections.addAll(this.zzab, paramVarArgs);
    }
    
    public Component<T> build()
    {
      if (this.zzae != null) {}
      for (boolean bool = true;; bool = false)
      {
        zzk.checkState(bool, "Missing required property: factory.");
        return new Component(new HashSet(this.zzab), new HashSet(this.zzac), this.zzad, this.zzae, null);
      }
    }
    
    public Builder<T> factory(ComponentFactory<T> paramComponentFactory)
    {
      this.zzae = ((ComponentFactory)zzk.zza(paramComponentFactory, "Null factory"));
      return this;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/components/Component.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */