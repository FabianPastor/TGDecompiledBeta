package com.google.firebase.components;

import java.util.List;

public class DependencyCycleException
  extends DependencyException
{
  private final List<Component<?>> zzap;
  
  public DependencyCycleException(List<Component<?>> paramList) {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/components/DependencyCycleException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */