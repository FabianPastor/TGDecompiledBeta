package com.google.firebase.components;

import com.google.firebase.inject.Provider;

final class zzi<T>
  implements Provider<T>
{
  private static final Object zzaq = new Object();
  private volatile Object zzar = zzaq;
  private volatile Provider<T> zzas;
  
  zzi(ComponentFactory<T> paramComponentFactory, ComponentContainer paramComponentContainer)
  {
    this.zzas = new zzj(paramComponentFactory, paramComponentContainer);
  }
  
  public final T get()
  {
    Object localObject1 = this.zzar;
    Object localObject2 = localObject1;
    if (localObject1 == zzaq) {}
    try
    {
      localObject1 = this.zzar;
      localObject2 = localObject1;
      if (localObject1 == zzaq)
      {
        localObject2 = this.zzas.get();
        this.zzar = localObject2;
        this.zzas = null;
      }
      return (T)localObject2;
    }
    finally {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/components/zzi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */