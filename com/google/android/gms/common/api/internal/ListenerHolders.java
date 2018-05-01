package com.google.android.gms.common.api.internal;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;

public class ListenerHolders
{
  private final Set<ListenerHolder<?>> zzlm = Collections.newSetFromMap(new WeakHashMap());
  
  public final void release()
  {
    Iterator localIterator = this.zzlm.iterator();
    while (localIterator.hasNext()) {
      ((ListenerHolder)localIterator.next()).clear();
    }
    this.zzlm.clear();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/ListenerHolders.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */