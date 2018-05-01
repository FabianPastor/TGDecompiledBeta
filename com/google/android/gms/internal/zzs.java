package com.google.android.gms.internal;

import android.os.Handler;
import android.os.Looper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public final class zzs
{
  private AtomicInteger zzW = new AtomicInteger();
  private final Map<String, Queue<zzp<?>>> zzX = new HashMap();
  private final Set<zzp<?>> zzY = new HashSet();
  private final PriorityBlockingQueue<zzp<?>> zzZ = new PriorityBlockingQueue();
  private final PriorityBlockingQueue<zzp<?>> zzaa = new PriorityBlockingQueue();
  private zzl[] zzab;
  private zzd zzac;
  private List<Object> zzad = new ArrayList();
  private final zzb zzi;
  private final zzw zzj;
  private final zzk zzx;
  
  public zzs(zzb paramzzb, zzk paramzzk)
  {
    this(paramzzb, paramzzk, 4);
  }
  
  private zzs(zzb paramzzb, zzk paramzzk, int paramInt)
  {
    this(paramzzb, paramzzk, 4, new zzh(new Handler(Looper.getMainLooper())));
  }
  
  private zzs(zzb paramzzb, zzk paramzzk, int paramInt, zzw paramzzw)
  {
    this.zzi = paramzzb;
    this.zzx = paramzzk;
    this.zzab = new zzl[4];
    this.zzj = paramzzw;
  }
  
  public final void start()
  {
    int j = 0;
    if (this.zzac != null) {
      this.zzac.quit();
    }
    int i = 0;
    while (i < this.zzab.length)
    {
      if (this.zzab[i] != null) {
        this.zzab[i].quit();
      }
      i += 1;
    }
    this.zzac = new zzd(this.zzZ, this.zzaa, this.zzi, this.zzj);
    this.zzac.start();
    i = j;
    while (i < this.zzab.length)
    {
      zzl localzzl = new zzl(this.zzaa, this.zzx, this.zzi, this.zzj);
      this.zzab[i] = localzzl;
      localzzl.start();
      i += 1;
    }
  }
  
  public final <T> zzp<T> zzc(zzp<T> paramzzp)
  {
    paramzzp.zza(this);
    synchronized (this.zzY)
    {
      this.zzY.add(paramzzp);
      paramzzp.zza(this.zzW.incrementAndGet());
      paramzzp.zzb("add-to-queue");
      if (!paramzzp.zzh())
      {
        this.zzaa.add(paramzzp);
        return paramzzp;
      }
    }
    for (;;)
    {
      String str;
      synchronized (this.zzX)
      {
        str = paramzzp.zzd();
        if (this.zzX.containsKey(str))
        {
          Queue localQueue = (Queue)this.zzX.get(str);
          ??? = localQueue;
          if (localQueue == null) {
            ??? = new LinkedList();
          }
          ((Queue)???).add(paramzzp);
          this.zzX.put(str, ???);
          if (zzab.DEBUG) {
            zzab.zza("Request for cacheKey=%s is in flight, putting on hold.", new Object[] { str });
          }
          return paramzzp;
        }
      }
      this.zzX.put(str, null);
      this.zzZ.add(paramzzp);
    }
  }
  
  final <T> void zzd(zzp<T> paramzzp)
  {
    Object localObject2;
    synchronized (this.zzY)
    {
      this.zzY.remove(paramzzp);
      synchronized (this.zzad)
      {
        localObject2 = this.zzad.iterator();
        if (((Iterator)localObject2).hasNext()) {
          ((Iterator)localObject2).next();
        }
      }
    }
    if (paramzzp.zzh()) {
      synchronized (this.zzX)
      {
        paramzzp = paramzzp.zzd();
        localObject2 = (Queue)this.zzX.remove(paramzzp);
        if (localObject2 != null)
        {
          if (zzab.DEBUG) {
            zzab.zza("Releasing %d waiting requests for cacheKey=%s.", new Object[] { Integer.valueOf(((Queue)localObject2).size()), paramzzp });
          }
          this.zzZ.addAll((Collection)localObject2);
        }
        return;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */