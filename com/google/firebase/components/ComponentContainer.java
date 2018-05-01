package com.google.firebase.components;

import com.google.firebase.inject.Provider;

public abstract interface ComponentContainer
{
  public abstract <T> Provider<T> getProvider(Class<T> paramClass);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/components/ComponentContainer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */